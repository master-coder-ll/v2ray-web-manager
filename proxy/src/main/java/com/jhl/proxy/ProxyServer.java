package com.jhl.proxy;


import com.jhl.constant.ProxyConstant;
import com.jhl.service.ProxyAccountService;
import com.jhl.task.service.TaskService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.StandardSocketOptions;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
/**
 * a proxyServer starter
 */
public final class ProxyServer {

    @Autowired
    ProxyConstant proxyConstant;
    @Autowired
    ProxyAccountService proxyAccountService;
    private static EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();

    @PostConstruct
    public void initNettyServer() {


        // Configure the bootstrap.
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
                   // 发送buf :32k
                    b.childOption(ChannelOption.SO_SNDBUF,32 * 1024)
                    //接收BUF: 32k
                    .childOption(ChannelOption.SO_RCVBUF,32 * 1024)
                    .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT);
                            //
                            //打开TCP keepalive 检测是否有必要？？？
                            //正常应该v2ray负责。
                            //ws本身就有PING、PONG 检测 --应用层
                            //TCP的keepalive默认2多小时后执行检测，需要配合应用层的心跳检测服务状态。
                            //所以作为中间件不需要心跳检测相关逻辑，委派给ws协议
                   // .childOption(ChannelOption.SO_KEEPALIVE,true)
                    //.childOption(NioChannelOption.of(StandardSocketOptions.SO_KEEPALIVE),true);

           // ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //    .handler(new LoggingHandler(LogLevel.ERROR))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    //new SafeByteToMessageDecoder(),
                                    new Dispatcher(proxyConstant, proxyAccountService));
                        }
                    })
                    .childOption(ChannelOption.AUTO_READ, false)
                    .bind(proxyConstant.getLocalPort()).sync()
                    .addListener((ChannelFutureListener) future -> log.info("Proxying on:" + proxyConstant.getLocalPort() + " ..."));


        } catch (Exception e) {
            log.error("netty start exception:{}", e);
        }
    }


    @PreDestroy
    public void preDestroy() throws InterruptedException {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully().addListener(future -> {
            log.warn("ReportService 已经关闭....");
            TaskService.destroy();
        });
        workerGroup.awaitTermination(3, TimeUnit.SECONDS);
        log.warn("netty 已经关闭....");



    }
}
