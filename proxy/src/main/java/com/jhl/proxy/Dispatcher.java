package com.jhl.proxy;

import com.jhl.service.TrafficControllerService;
import com.jhl.cache.ConnectionLimitCache;
import com.jhl.cache.ProxyAccountCache;
import com.jhl.constant.ProxyConstant;
import com.jhl.pojo.Report;
import com.jhl.pojo.ConnectionLimit;
import com.jhl.service.ReportService;
import com.jhl.utils.SynchronizedInternerUtils;
import com.ljh.common.model.FlowStat;
import com.ljh.common.model.ProxyAccount;
import com.ljh.common.utils.V2RayPathEncoder;
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

@Slf4j
public class Dispatcher extends ChannelInboundHandlerAdapter {


    final ProxyConstant proxyConstant;
    final TrafficControllerService trafficControllerService;
    //800多k
    //private static final long CHANNEL_TRAFFIC = 934 * 1000;
    // As we use inboundChannel.eventLoop() when building the Bootstrap this does not need to be volatile as
    // the outboundChannel will use the same EventLoop (and therefore Thread) as the inboundChannel.
    private Channel outboundChannel;
    private String accountNo;
    private ProxyAccountCache proxyAccountCache;
    private static final String HOST = "HOST";
    private static final Long MAX_INTERVAL_REPORT_TIME_MS = 1000 * 60 * 5L;
    private String host;
    public Dispatcher(ProxyConstant proxyConstant, TrafficControllerService trafficControllerService, ProxyAccountCache proxyAccountCache) {
        this.proxyConstant = proxyConstant;
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
                String[] path = split[0].split(" ");

                String[] accountNoAndToken = path[1].split("/")[2].split(":");;

                 accountNo= accountNoAndToken[0];
                String requestToken=accountNoAndToken[1];
                String  token= V2RayPathEncoder.encoder(accountNo, host, proxyConstant.getAuthPassword());
                if (!requestToken.equals(token))throw new IllegalAccessException("非法访问,token检测不通过");
                String directory = path[1];
                int directoryLen = directory.length();
                //+1 因为 :占1
                int length = requestToken.length()+accountNo.length() +1;
                String rep = heads.replaceAll(directory, directory.substring(0, directoryLen - (length + 1)));
                //整形后的握手数据

                handshakeByteBuf.writeBytes(rep.getBytes());
            } catch (Exception e) {

                log.warn("解析阶段发生错误:{},e:{}", heads, e.getLocalizedMessage());
                if (handshakeByteBuf != null) ReferenceCountUtil.release(handshakeByteBuf);
                closeOnFlush(ctx.channel());
                return;
            } finally {
                ReferenceCountUtil.release(msg);
            }

            try {
                //  bug :如果 accountNo正常获取到应该->增加
                int connections = trafficControllerService.incrementChannelCount(getAccountId());
                log.info("当前连接数account:{},{}", getAccountId(), connections);


                ProxyAccount proxyAccount = proxyAccountCache.getOrRemoteAccess(accountNo,host);
                if (proxyAccount == null) {
                    log.warn("获取不到账号");
                    ReferenceCountUtil.release(handshakeByteBuf);
                    closeOnFlush(ctx.channel());
                    return;
                }



                //如果来源不是proxyAccount 中设置的 断开连接
             /*   if (!proxyAccount.getHost().toUpperCase().equals(host.toUpperCase())) {
                    log.warn("来源host错误 from :{} ,to:{}", host, proxyAccount.getHost());
                    ReferenceCountUtil.release(handshakeByteBuf);
                    closeOnFlush(ctx.channel());
                    return;
                }*/

                Long readLimit = proxyAccount.getUpTrafficLimit() * 1000;
                Long writeLimit = proxyAccount.getDownTrafficLimit() * 1000;
                //触发最大连接数，惩罚性减低连接数1小时
                int maxConnection =ConnectionLimitCache.containKey(getAccountId())?  Integer.valueOf(proxyAccount.getMaxConnection()/2):proxyAccount.getMaxConnection();
                String v2rayIp = proxyAccount.getV2rayHost();
                int v2rayPort = proxyAccount.getV2rayPort();

                //加入流量控制
                //保持对全局的控制，不修改key
                GlobalTrafficShapingHandler orSetGroupGlobalTrafficShapingHandler = trafficControllerService.putIfAbsent(getAccountId(), ctx.executor(), readLimit, writeLimit);
                //因为没有fireChannel
                ctx.pipeline().addFirst(orSetGroupGlobalTrafficShapingHandler);


                if (connections > maxConnection) {
                    reportConnectionLimit();
                    log.warn("{}:连接数过多当前：{},最大值：{}",accountNo, connections, maxConnection);
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
                outboundChannel = f.channel();
                final ByteBuf handshakeByteBuf2 = handshakeByteBuf;
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
                log.error("建立与v2ray连接阶段发送错误:{},e:{}", e);
                if (handshakeByteBuf.refCnt() > 0) {
                    handshakeByteBuf.release(handshakeByteBuf.refCnt());
                }
                closeOnFlush(ctx.channel());
                return;
            }finally {
                isHandshaking = false;
            }


        } else {
            if (outboundChannel.isActive()) {
                writeToOutBoundChannel(msg, ctx);
            }
        }

        //  ctx.fireChannelRead(msg);

    }

    private String getAccountId() {
        return accountNo + ":" +host;
    }

    private void reportConnectionLimit() {
        if (!ConnectionLimitCache.containKey(accountNo)) {
            //连接限制警告
            ReportService.addQueue(Report.builder()
                    .t(ConnectionLimit.builder().accountNo(accountNo).build())
                    .nextTime(0)
                    .failureTimes(0)
                    .build()
            );
            ConnectionLimitCache.put(accountNo);
        }
    }

    private void writeToOutBoundChannel(Object msg, final ChannelHandlerContext ctx) {
        outboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                //todo 测试对正常连接的影响
                if(proxyAccountCache.getOrRemoteAccess(accountNo, host)==null){
                       closeOnFlush(ctx.channel());
                }else {
                    ctx.channel().read();
                }
                // was able to flush out data, start to read the next chunk


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
        int channelCount = trafficControllerService.decrementChannelCount(getAccountId());
        log.info("关闭当前连接数后->account:{},：{}", getAccountId(), channelCount);

        GlobalTrafficShapingHandler globalTrafficShapingHandler = trafficControllerService.getGlobalTrafficShapingHandler(getAccountId());
        if (globalTrafficShapingHandler == null) return;
        TrafficCounter trafficCounter = globalTrafficShapingHandler.trafficCounter();
        if (channelCount < 1) {
            long writtenBytes = trafficCounter.cumulativeWrittenBytes();
            long readBytes = trafficCounter.cumulativeReadBytes();
            //统计流量
            reportStat(writtenBytes, readBytes);

            log.info("账号:{},完全断开连接,累计字节:{}B", getAccountId(),writtenBytes + readBytes);
            //   log.info("当前{},累计读字节:{}", accountNo, readBytes);
            trafficControllerService.releaseGroupGlobalTrafficShapingHandler(getAccountId());
        } else {
            if (System.currentTimeMillis() - trafficCounter.lastCumulativeTime() >= MAX_INTERVAL_REPORT_TIME_MS) {
                synchronized (SynchronizedInternerUtils.getInterner().intern(accountNo+accountNo+ ":reportStat")) {
                    if (System.currentTimeMillis() - trafficCounter.lastCumulativeTime() >= MAX_INTERVAL_REPORT_TIME_MS) {

                        long writtenBytes = trafficCounter.cumulativeWrittenBytes();
                        long readBytes = trafficCounter.cumulativeReadBytes();

                        reportStat(writtenBytes, readBytes);
                        //重置
                        trafficCounter.resetCumulativeTime();
                        log.info("账号:{},连接超过5分钟.上传分段流量统计数据:{}B", getAccountId(), writtenBytes + readBytes);
                    }

                }

            }
        }


    }

    private void reportStat(long writtenBytes, long readBytes) {
        FlowStat flowStat = new FlowStat();
        flowStat.setDomain(host);
        flowStat.setAccountNo(accountNo);
        flowStat.setUsed(writtenBytes + readBytes);
        flowStat.setUniqueId(UUID.randomUUID().toString());
        ReportService.addQueue(Report.builder().t(flowStat).failureTimes(0).nextTime(0).build());
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
