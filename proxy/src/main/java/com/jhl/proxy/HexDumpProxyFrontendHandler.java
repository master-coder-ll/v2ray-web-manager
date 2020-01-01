package com.jhl.proxy;

import com.jhl.TrafficController.TrafficController;
import com.jhl.cache.FlowStatQueue;
import com.jhl.cache.ProxyAccountCache;
import com.jhl.config.ProxyConfig;
import com.ljh.common.model.FlowStat;
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
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.util.UUID;

@Slf4j
public class HexDumpProxyFrontendHandler extends ChannelInboundHandlerAdapter {


    final ProxyConfig proxyConfig;
    final TrafficController trafficController;
    //800多k
    private static final long CHANNEL_TRAFFIC = 934 * 1000;
    // As we use inboundChannel.eventLoop() when building the Bootstrap this does not need to be volatile as
    // the outboundChannel will use the same EventLoop (and therefore Thread) as the inboundChannel.
    private Channel outboundChannel;
    private String accountNo;
    private ProxyAccountCache proxyAccountCache;
    private final String HOST = "HOST";

    public HexDumpProxyFrontendHandler(ProxyConfig proxyConfig, TrafficController trafficController, ProxyAccountCache proxyAccountCache) {
        this.proxyConfig = proxyConfig;
        this.trafficController = trafficController;
        this.proxyAccountCache = proxyAccountCache;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("active");
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
            final ByteBuf HandshakeByteBuf = ctx.alloc().buffer();
            final Channel inboundChannel = ctx.channel();
            String host = null;
            try {
                ByteBuf byteBuf = ((ByteBuf) msg);
                heads = byteBuf.toString(Charset.defaultCharset());
                //使用了处理后的握手数据，释放原理的
                String[] split = heads.split("\r\n");

                //获取host头信息
                for (String head : split) {
                    // :空格
                    String[] headKV = head.split(": ");
                    if (headKV == null || headKV.length != 2) continue;
                    if (headKV[0].trim().toUpperCase().equals(HOST)) {
                        host = headKV[1].trim();
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

                HandshakeByteBuf.writeBytes(rep.getBytes());
            } catch (Exception e) {

                log.error("获取认证路径失败:{},e:{}", heads, e.getLocalizedMessage());
                ReferenceCountUtil.release(HandshakeByteBuf);
                closeOnFlush(ctx.channel());
                return;
            } finally {
                ReferenceCountUtil.release(msg);
            }

            // bug fixed:如果 accountNo正常获取到应该->增加
            int connections = trafficController.incrementChannelCount(accountNo);
            log.info("当前连接数account:{},{}", accountNo, connections);

            isHandshaking = false;
            ProxyAccount proxyAccount = proxyAccountCache.get(accountNo);
            if (proxyAccount == null) {
                log.error("获取不到账号");
                ReferenceCountUtil.release(HandshakeByteBuf);
                closeOnFlush(ctx.channel());
                return;
            }


                //如果来源不是proxyAccount 中设置的 断开连接
            if (!proxyAccount.getHost().toUpperCase().equals(host.toUpperCase())) {
                log.error("来源host错误 from :{} ,to:{}",host,proxyAccount.getHost());
                ReferenceCountUtil.release(HandshakeByteBuf);
                closeOnFlush(ctx.channel());
                return;
            }

            Long readLimit = proxyAccount != null ? proxyAccount.getUpTrafficLimit() * 1000 : CHANNEL_TRAFFIC;
            Long writeLimit = proxyAccount != null ? proxyAccount.getDownTrafficLimit() * 1000 : CHANNEL_TRAFFIC;
            int maxConnection = proxyAccount != null ? proxyAccount.getMaxConnection() : proxyConfig.getMaxConnections();
            String v2rayIp = proxyAccount != null ? proxyAccount.getV2rayHost() : proxyConfig.getRemoteHost();
            Integer v2rayPort = proxyAccount != null ? proxyAccount.getV2rayPort() : proxyConfig.getRemotePort();

            //加入流量控制
            GlobalTrafficShapingHandler orSetGroupGlobalTrafficShapingHandler = trafficController.getOrSetGroupGlobalTrafficShapingHandler(accountNo, ctx.executor(), readLimit, writeLimit);
            //因为没有fireChannel
            ctx.pipeline().addFirst(orSetGroupGlobalTrafficShapingHandler);


            if (connections > maxConnection) {
                log.error("连接数过多当前：{},最大值：{}", connections, maxConnection);
                ReferenceCountUtil.release(HandshakeByteBuf);
                closeOnFlush(ctx.channel());
                return;
            }
            Bootstrap b = new Bootstrap();
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.group(inboundChannel.eventLoop())
                    .channel(ctx.channel().getClass())
                    .handler(new HexDumpProxyBackendHandler(inboundChannel))
                    .option(ChannelOption.AUTO_READ, false);

            ChannelFuture f = b.connect(v2rayIp, v2rayPort);
            outboundChannel = f.channel();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        // connection complete start to read first data
                        writeToOutBoundChannel(HandshakeByteBuf, ctx);

                    } else {
                        // Close the connection if the connection attempt has failed.
                        inboundChannel.close();
                    }
                }

            });

        } else {
            if (outboundChannel.isActive()) {
                writeToOutBoundChannel(msg, ctx);
            }
        }

        //  ctx.fireChannelRead(msg);

    }

    private void writeToOutBoundChannel(Object msg, final ChannelHandlerContext ctx) {
        outboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    // was able to flush out data, start to read the next chunk
                    ctx.channel().read();

                } else {
                    future.channel().close();
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }

        if (accountNo != null) {
            //减少channel 引用计数
            int channelCount = trafficController.decrementChannelCount(accountNo);
            log.info("关闭当前连接数后->account:{},：{}", accountNo, channelCount);

         /*   if (globalTrafficShapingHandler != null) {
                TrafficCounter trafficCounter = globalTrafficShapingHandler.trafficCounter();

            }*/
            if (channelCount < 1) {
                GlobalTrafficShapingHandler globalTrafficShapingHandler = trafficController.getGlobalTrafficShapingHandler(accountNo);
                //释放 流量控制类
                if (globalTrafficShapingHandler != null) {
                    TrafficCounter trafficCounter = globalTrafficShapingHandler.trafficCounter();
                    ProxyAccount proxyAccount = proxyAccountCache.get(accountNo);
                    if (proxyAccount == null) {
                        log.warn("断开连接时候获取不到PA,【原因猜测】account已经不可用");
                        trafficController.releaseGroupGlobalTrafficShapingHandler(accountNo);
                        return;
                    }
                    Integer accountId = proxyAccount.getAccountId();
                    long writtenBytes = trafficCounter.cumulativeWrittenBytes();
                    long readBytes = trafficCounter.cumulativeReadBytes();
                    //统计流量
                    if (!StringUtils.isEmpty(accountNo)) {
                        FlowStatQueue.addQueue(new FlowStat(accountNo, writtenBytes + readBytes, 0, UUID.randomUUID().toString()));
                    }

                    log.info("账号:{},id:{},完全断开连接。。", accountNo, accountId);
                    log.info("当前{},累计写字节:{}", accountNo, writtenBytes);
                    log.info("当前{},累计读字节:{}", accountNo, readBytes);
                }
                trafficController.releaseGroupGlobalTrafficShapingHandler(accountNo);
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exceptionCaught:{}", cause);
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
