package com.jhl.proxy;

import com.jhl.cache.ConnectionLimitCache;
import com.jhl.cache.ProxyAccountCache;
import com.jhl.constant.ProxyConstant;
import com.jhl.exception.ReleaseDirectMemoryException;
import com.jhl.pojo.ConnectionLimit;
import com.jhl.pojo.ProxyAccountWrapper;
import com.jhl.pojo.Report;
import com.jhl.service.ConnectionStatsService;
import com.jhl.service.ReportService;
import com.jhl.service.TrafficControllerService;
import com.jhl.utils.SynchronizedInternerUtils;
import com.ljh.common.model.FlowStat;
import com.ljh.common.model.ProxyAccount;
import com.ljh.common.utils.V2RayPathEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

@Slf4j
public class Dispatcher extends ChannelInboundHandlerAdapter {


    private static final String HOST = "HOST";
    private static final Long MAX_INTERVAL_REPORT_TIME_MS = 1000 * 60 * 5L;

    /**
     * proxy端配置数据
     */
    final ProxyConstant proxyConstant;

    final TrafficControllerService trafficControllerService;

    private Channel outboundChannel;

    private String accountNo;

    private ProxyAccountCache proxyAccountCache;

    private String host;

    private boolean isHandshaking = true;

    private Long version = null;

    private ConnectionStatsService connectionStatsService;

    public Dispatcher(ProxyConstant proxyConstant, TrafficControllerService trafficControllerService, ProxyAccountCache proxyAccountCache,
                      ConnectionStatsService connectionStatsService) {
        this.proxyConstant = proxyConstant;
        this.trafficControllerService = trafficControllerService;
        this.proxyAccountCache = proxyAccountCache;
        this.connectionStatsService = connectionStatsService;

    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
       // log.info("Dispatcher len:"+((ByteBuf)msg).readableBytes()+"B");
        if (isHandshaking) {

            /**
             * PooledUnsafeDirectByteBuf(ridx: 0, widx: 188, cap: 1024)
             *
             * GET /ws/50001:token/ HTTP/1.1
             * Host: 127.0.0.1:8081
             * User-Agent: Go-http-client/1.1
             * Connection: Upgrade
             * Sec-WebSocket-Key: 90rYhIPctMP+ykUzA6QLrA==
             * Sec-WebSocket-Version: 13
             * Upgrade: websocket
             */

            ByteBuf handshakeByteBuf = null;
            try {

                handshakeByteBuf = parse(ctx, msg);

            } catch (Exception e) {
                if (!(e instanceof ReleaseDirectMemoryException))
                    log.warn("解析阶段发生错误:{},e:{}", ((ByteBuf) msg).toString(Charset.defaultCharset()), e.getLocalizedMessage());
                if (handshakeByteBuf != null)
                    ReferenceCountUtil.release(handshakeByteBuf);
                closeOnFlush(ctx.channel());
                return;
            } finally {
                //释放握手数据，防止内存溢出
                ReferenceCountUtil.release(msg);
            }

            //step2

            try {
                // 获取proxyAccount
                ProxyAccountWrapper proxyAccount = getProxyAccount();

                if (proxyAccount == null || isFull(proxyAccount)) {
                    ReferenceCountUtil.release(handshakeByteBuf);
                    closeOnFlush(ctx.channel());
                    return;
                }

                attachTrafficController(ctx, proxyAccount);

                sendNewPackageToClient(ctx, handshakeByteBuf, ctx.channel(), proxyAccount);

            } catch (Exception e) {
                log.error("建立与v2ray连接阶段发送错误e:{}", e);
                release(handshakeByteBuf);
                closeOnFlush(ctx.channel());
            } finally {
                isHandshaking = false;
            }


        } else {
            try {
                writeToOutBoundChannel(msg, ctx);
            } catch (Exception e) {
                if (!(e instanceof ReleaseDirectMemoryException)) {
                    log.error("数据交互发生异常：{}", e);
                }
                release((ByteBuf) msg);
                closeOnFlush(ctx.channel());
            }

        }


    }

    private void release(ByteBuf msg) {
        if (msg == null) return;
        ByteBuf byteBuf = msg;
        if (byteBuf.refCnt() > 0) {
            byteBuf.release(byteBuf.refCnt());
        }
    }

    /**
     * 解析握手数据，并且生成新的握手数据
     *
     * @param ctx ChannelHandlerContext
     * @param msg ByteBuf
     * @return newHeadPackage
     * @throws Exception
     */
    private ByteBuf parse(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        ByteBuf byteBuf = ((ByteBuf) msg);
        String heads = byteBuf.toString(Charset.defaultCharset());

        String[] headRows = heads.split("\r\n");
        getHost(headRows);
        //GET /ws/50001:token/ HTTP/1.1
        String[] requestRow = headRows[0].split(" ");

        //50001:token/
        String[] accountNoAndToken = requestRow[1].split("/")[2].split(":");

        if (accountNoAndToken.length < 2) throw new NullPointerException("旧版接入不在支持");

        accountNo = accountNoAndToken[0];

        String token = accountNoAndToken[1];

        checkToken(token);
        // /ws/50001:token/ ,定位目录
        String directory = requestRow[1];
        int directoryLen = directory.length();
        //+1 因为 :占1
        int tokenLen = token.length() + accountNo.length() + 1;
        // /ws/
        String newHeadPackage = heads.replaceAll(directory, directory.substring(0, directoryLen - (tokenLen + 1)));
        //整形后的新握手数据

        return ctx.alloc().buffer().writeBytes(newHeadPackage.getBytes());
    }

    /**
     * 判断是否超过最大连接数
     *
     * @param proxyAccount ProxyAccount
     * @return true is full
     */
    private boolean isFull(ProxyAccount proxyAccount) {
        int connections = connectionStatsService.incrementAndGet(getAccountId());
        log.info("当前连接数account:{},{}", getAccountId(), connections);
        int maxConnection = ConnectionLimitCache.containKey(getAccountId()) ? Integer.valueOf(proxyAccount.getMaxConnection() / 2) : proxyAccount.getMaxConnection();
        if (connections > maxConnection) {
            reportConnectionLimit();
            log.warn("{}:连接数过多当前：{},最大值：{}", accountNo, connections, maxConnection);
            //ReferenceCountUtil.release(handshakeByteBuf);
            //closeOnFlush(ctx.channel());
            return true;
        }
        return false;
    }

    private ProxyAccountWrapper getProxyAccount() {
        ProxyAccountWrapper proxyAccount = proxyAccountCache.getProxyAccount(accountNo, host);
        if (proxyAccount == null) {
            log.warn("获取不到账号。。。");
            //ReferenceCountUtil.release(handshakeByteBuf);
            //  closeOnFlush(ctx.channel());
            return null;
        }
        version = proxyAccount.getVersion();
        return proxyAccount;
    }

    /**
     * 为channel 增加对应的TrafficController
     *
     * @param ctx          ChannelHandlerContext
     * @param proxyAccount ProxyAccount
     */
    private void attachTrafficController(ChannelHandlerContext ctx, ProxyAccountWrapper proxyAccount) {
        Long readLimit = proxyAccount.getUpTrafficLimit() * 1000;
        Long writeLimit = proxyAccount.getDownTrafficLimit() * 1000;
        //触发最大连接数，惩罚性减低连接数1小时
        //加入流量控制
        //保持对全局的控制，不修改key
        GlobalTrafficShapingHandler orSetGroupGlobalTrafficShapingHandler = trafficControllerService.putIfAbsent(getAccountId(), ctx.executor(), readLimit, writeLimit);
        //因为没有fireChannel
        ctx.pipeline().addFirst(orSetGroupGlobalTrafficShapingHandler);
    }

    /**
     * 发送握手数据，并且提升为ws协议
     *
     * @param ctx
     * @param handshakeByteBuf
     * @param inboundChannel
     * @param proxyAccount
     */
    private void sendNewPackageToClient(ChannelHandlerContext ctx, final ByteBuf handshakeByteBuf, Channel inboundChannel, ProxyAccount proxyAccount) {
        Bootstrap b = getMuxClient(inboundChannel);

        ChannelFuture f = b.connect(proxyAccount.getV2rayHost(), proxyAccount.getV2rayPort());
        outboundChannel = f.channel();
        f.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                writeToOutBoundChannel(handshakeByteBuf, ctx);
            } else {
                release(handshakeByteBuf);
                inboundChannel.close();

            }
        });
    }

    private Bootstrap getMuxClient(Channel inboundChannel) {
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .handler(new Receiver(inboundChannel))
                .option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true))
                .option(ChannelOption.AUTO_READ, false)
                .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                //32k/64k
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT);
        return b;


    }


    /**
     * 获取host头信息
     *
     * @param headRows
     */
    private void getHost(String[] headRows) {

        for (String head : headRows) {
            // :空格
            String[] headKV = head.split(": ");
            if (headKV.length != 2) continue;
            if (headKV[0].trim().toUpperCase().equals(HOST)) {
                host = headKV[1].trim();
                String[] ipAndPort = host.split(":");
                host = ipAndPort[0];
                break;
            }
        }

        if (host == null) throw new NullPointerException("获取不到host信息");
    }

    private void checkToken(String requestToken) throws IllegalAccessException {
        String token = V2RayPathEncoder.encoder(accountNo, host, proxyConstant.getAuthPassword());
        if (!requestToken.equals(token)) throw new IllegalAccessException("非法访问,token检测不通过");
    }

    private String getAccountId() {
        return accountNo + ":" + host + ":" + version;
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

    private void writeToOutBoundChannel(Object msg, final ChannelHandlerContext ctx) throws ReleaseDirectMemoryException {
        if (proxyAccountCache.interrupted(accountNo, host, version)) {
            throw new ReleaseDirectMemoryException("【当前版本已经更新】抛出异常。统一内存释放");
        }
        outboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            release((ByteBuf) msg);
            if (future.isSuccess()) {
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
        int accountConnections = connectionStatsService.decrementAndGet(getAccountId());
        log.info("关闭当前连接数后->account:{},：{}", getAccountId(), accountConnections);

        GlobalTrafficShapingHandler globalTrafficShapingHandler = trafficControllerService.getGlobalTrafficShapingHandler(getAccountId());
        if (globalTrafficShapingHandler == null) return;
        TrafficCounter trafficCounter = globalTrafficShapingHandler.trafficCounter();
        if (accountConnections < 1) {
            long writtenBytes = trafficCounter.cumulativeWrittenBytes();
            long readBytes = trafficCounter.cumulativeReadBytes();
            //统计流量
            reportStat(writtenBytes, readBytes);

            log.info("账号:{},完全断开连接,累计字节:{}B", getAccountId(), writtenBytes + readBytes);
            //   log.info("当前{},累计读字节:{}", accountNo, readBytes);
            trafficControllerService.releaseGroupGlobalTrafficShapingHandler(getAccountId());
            connectionStatsService.delete(getAccountId());

        } else {
            if (System.currentTimeMillis() - trafficCounter.lastCumulativeTime() >= MAX_INTERVAL_REPORT_TIME_MS) {
                synchronized (SynchronizedInternerUtils.getInterner().intern( accountNo + ":reportStat")) {
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

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("active");
        ctx.read();
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
