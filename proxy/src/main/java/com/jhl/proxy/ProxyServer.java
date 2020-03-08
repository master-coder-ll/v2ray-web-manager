package com.jhl.proxy;


import com.jhl.cache.ProxyAccountCache;
import com.jhl.constant.ProxyConstant;
import com.jhl.service.ConnectionStatsService;
import com.jhl.service.ReportService;
import com.jhl.service.TrafficControllerService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
    ProxyAccountCache proxyAccountCache;
    @Autowired
    TrafficControllerService trafficControllerService;
    @Autowired
    ConnectionStatsService connectionStatsService;
    private static EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();

    @PostConstruct
    public void initNettyServer() {
        log.info("Proxying on:" + proxyConstant.getLocalPort() +  " ...");

        // Configure the bootstrap.
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true));
            b.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true));
            //ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //    .handler(new LoggingHandler(LogLevel.ERROR))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new Dispatcher(proxyConstant,trafficControllerService,proxyAccountCache, connectionStatsService));
                        }
                    })
                    .childOption(ChannelOption.AUTO_READ, false)
                    .bind(proxyConstant.getLocalPort()).sync().channel()
                    .closeFuture().
                    addListener(future -> {
                       ReportService.destroy();
                        log.warn("ReportService 已经关闭....");
                    });

        } catch (Exception e) {
            log.error("netty start exception:{}", e);
        }
    }


    @PreDestroy
    public void preDestroy()   {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        log.warn("netty 已经关闭....");


    }
}
