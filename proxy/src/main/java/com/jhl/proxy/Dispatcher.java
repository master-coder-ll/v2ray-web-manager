package com.jhl.proxy;

import com.jhl.service.TrafficControllerService;
import com.jhl.cache.ProxyAccountCache;
import com.jhl.config.ProxyConfig;
import com.jhl.pojo.Report;
import com.jhl.service.ReportService;
import com.jhl.utils.Utils;
import com.ljh.common.model.ProxyAccount;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 *解析分发连接-pool.DirectByteBuf ,无阻塞（除了握手阶段）
 * 如果 client ws协议在握手阶段支持支持的http响应头，那就可以通过http code 302 调整线路
 * 如果你是client端作者，希望你能实现并且响应302、301 头，但是这个不是ws的标准
 * 应该是在error里面实现的吧
 */
@Slf4j
public class Dispatcher extends ChannelInboundHandlerAdapter {


    final ProxyConfig proxyConfig;
    final TrafficControllerService trafficControllerService;
    //800多k
    //  private static final long CHANNEL_TRAFFIC = 934 * 1000;
    // As we use inboundChannel.eventLoop() when building the Bootstrap this does not need to be volatile as
    // the outboundChannel will use the same EventLoop (and therefore Thread) as the inboundChannel.
    private Channel outboundChannel;
    private String accountNo;
    private ProxyAccountCache proxyAccountCache;
    private static final String HOST = "HOST";
    private static final Long MAX_INTERVAL_REPORT_TIME_MS = 1000 * 60 * 5L;

    public Dispatcher(ProxyConfig proxyConfig, TrafficControllerService trafficControllerService, ProxyAccountCache proxyAccountCache) {
        this.proxyConfig = proxyConfig;
        this.trafficControllerService = trafficControllerService;
        this.proxyAccountCache = proxyAccountCache;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("active");
        ctx.read();
    }

    private boolean isHandshaking = true;

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        if (isHandshaking) {
            /**
             * PooledUnsafeDirectByteBuf(ridx: 0, widx: 188, cap: 1024)
             *
             * GET /ws/50001/ HTTP/1.1
             * Host: 127.0.0.1:8081
             * User-Agent: Go-http-client/1.1
             * Connection: Upgrade
             * Sec-WebSocket-Key: 90rYhIPctMP+ykUzA6QLrA==
             * Sec-WebSocket-Version: 13
             * Upgrade: websocket
             */

            String heads = null;
            ByteBuf handshakeByteBuf = null;
            final Channel inboundChannel = ctx.channel();
            String host = null;
            try {
                handshakeByteBuf = ctx.alloc().buffer();
                ByteBuf byteBuf = ((ByteBuf) msg);
                heads = byteBuf.toString(Charset.defaultCharset());
                //使用了处理后的握手数据，释放原理的
                String[] split = heads.split("\r\n");

                //获取host头信息
                for (String head : split) {
                    // :空格
                    String[] headKV = head.split(": ");
                    if (headKV.length != 2) continue;
                    if (headKV[0].trim().toUpperCase().equals(HOST)) {
                        host = headKV[1].trim();
                        String[] ipAndPort = host.split(":");
                         host=ipAndPort[0];
                        break;
                    }
                }

                if (host == null) throw new NullPointerException("获取不到host信息");
                String[] s = split[0].split(" ");
                accountNo = s[1].split("/")[2];
                String path = s[1];
                int pathLen = path.length();
                int accountIdLen = accountNo.length();
                String rep = heads.replaceAll(path, path.substring(0, pathLen - (accountIdLen + 1)));
                //整形后的握手数据

                handshakeByteBuf.writeBytes(rep.getBytes());
            } catch (Exception e) {

                log.error("解析协议阶段发送错误,可能原因：1. 爬虫,2.黑客扫描攻击 :{},e:{}", heads, e.getLocalizedMessage());
                ReferenceCountUtil.release(handshakeByteBuf);
                closeOnFlush(ctx.channel());
                return;
            } finally {
                ReferenceCountUtil.release(msg);
            }

            try {


                //  bug :如果 accountNo正常获取到应该->增加
                int connections = trafficControllerService.incrementChannelCount(accountNo);
                log.info("当前连接数account:{},{}", accountNo, connections);

                isHandshaking = false;
                ProxyAccount proxyAccount = proxyAccountCache.get(accountNo);
                if (proxyAccount == null) {
                    log.error("获取不到账号");
                    ReferenceCountUtil.release(handshakeByteBuf);
                    closeOnFlush(ctx.channel());
                    return;
                }


                //如果来源不是proxyAccount 中设置的 断开连接
                if (!proxyAccount.getHost().toUpperCase().equals(host.toUpperCase())) {
                    log.error("来源host错误 from :{} ,to:{}", host, proxyAccount.getHost());
                    ReferenceCountUtil.release(handshakeByteBuf);
                    closeOnFlush(ctx.channel());
                    return;
                }

                Long readLimit = proxyAccount.getUpTrafficLimit() * 1000;
                Long writeLimit = proxyAccount.getDownTrafficLimit() * 1000;
                int maxConnection = proxyAccount.getMaxConnection();
                String v2rayIp = proxyAccount.getV2rayHost();
                int v2rayPort = proxyAccount.getV2rayPort();

                //加入流量控制
                GlobalTrafficShapingHandler orSetGroupGlobalTrafficShapingHandler = trafficControllerService.putIfAbsent(accountNo, ctx.executor(), readLimit, writeLimit);
                //因为没有fireChannel
                ctx.pipeline().addFirst(orSetGroupGlobalTrafficShapingHandler);


                if (connections > maxConnection) {
                    log.error("连接数过多当前：{},最大值：{}", connections, maxConnection);
                    ReferenceCountUtil.release(handshakeByteBuf);
                    closeOnFlush(ctx.channel());
                    return;
                }
                Bootstrap b = new Bootstrap();
                b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
                b.group(inboundChannel.eventLoop())
                        .channel(ctx.channel().getClass())
                        .handler(new Receiver(inboundChannel))
                        .option(ChannelOption.AUTO_READ, false);

                ChannelFuture f = b.connect(v2rayIp, v2rayPort);
                final ByteBuf handshakeByteBuf2 = handshakeByteBuf;
                outboundChannel = f.channel();
                f.addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        // connection complete start to read first data
                        writeToOutBoundChannel(handshakeByteBuf2, ctx);

                    } else {
                        // Close the connection if the connection attempt has failed.
                        inboundChannel.close();
                    }
                });
            } catch (Exception e) {
                int refCnt = handshakeByteBuf.refCnt();
                if (refCnt > 0) handshakeByteBuf.release(refCnt);
                log.error("v2ray建立连接阶段发送错误，可能原因：v2ray未启动;配置错误", e);
                closeOnFlush(ctx.channel());
            }
        } else {
            if (outboundChannel.isActive()) {
                writeToOutBoundChannel(msg, ctx);
            }
        }

        //  ctx.fireChannelRead(msg);

    }

    private void writeToOutBoundChannel(Object msg, final ChannelHandlerContext ctx) {
        outboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                // was able to flush out data, start to read the next chunk
                ctx.channel().read();

            } else {
                future.channel().close();
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }

        if (accountNo == null) return;
        //减少channel 引用计数
        int channelCount = trafficControllerService.decrementChannelCount(accountNo);
        log.info("关闭当前连接数后->account:{},：{}", accountNo, channelCount);

        GlobalTrafficShapingHandler globalTrafficShapingHandler = trafficControllerService.getGlobalTrafficShapingHandler(accountNo);
        if (globalTrafficShapingHandler == null) return;
        TrafficCounter trafficCounter = globalTrafficShapingHandler.trafficCounter();
        if (channelCount < 1) {
            long writtenBytes = trafficCounter.cumulativeWrittenBytes();
            long readBytes = trafficCounter.cumulativeReadBytes();
            //统计流量
            reportStat(writtenBytes, readBytes);

            log.info("账号:{},完全断开连接。。", accountNo);
            log.info("当前:{},累计字节:{}B", accountNo, writtenBytes + readBytes);
            //   log.info("当前{},累计读字节:{}", accountNo, readBytes);
            trafficControllerService.releaseGroupGlobalTrafficShapingHandler(accountNo);
        } else {
            if (System.currentTimeMillis() - trafficCounter.lastCumulativeTime() >= MAX_INTERVAL_REPORT_TIME_MS) {
                synchronized (Utils.getStringWeakReference(accountNo)) {
                    if (System.currentTimeMillis() - trafficCounter.lastCumulativeTime() >= MAX_INTERVAL_REPORT_TIME_MS) {

                        long writtenBytes = trafficCounter.cumulativeWrittenBytes();
                        long readBytes = trafficCounter.cumulativeReadBytes();

                        reportStat(writtenBytes, readBytes);
                        //重置
                        trafficCounter.resetCumulativeTime();
                        log.info("账号:{},连接超过5分钟.上传分段流量统计数据:{}B", accountNo, writtenBytes + readBytes);
                    }

                }

            }
        }


    }

    private void reportStat(long writtenBytes, long readBytes) {
        Report report = new Report();
        report.setAccountNo(accountNo);
        report.setFailureTimes(0);
        report.setUsed(writtenBytes + readBytes);
        report.setNextTime(0);
        report.setUniqueId(UUID.randomUUID().toString());
        ReportService.addQueue(report);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!(cause instanceof IOException)) log.error("exceptionCaught:", cause);

        closeOnFlush(ctx.channel());
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

   /* public String convertByteBufToString(ByteBuf buf) {

        String str;
        if (buf.hasArray()) { // 处理堆缓冲区
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else { // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        return str;
    }*/
}
