package com.jhl.proxy;


import com.jhl.constant.ProxyConstant;
import com.jhl.service.TrafficControllerService;
import com.jhl.cache.ProxyAccountCache;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
/**
 * a proxyServer starter
 */
public final class ProxyServer implements Runnable {

    @Autowired
    ProxyConstant proxyConstant;
    @Autowired
    ProxyAccountCache proxyAccountCache;
    @Autowired
    TrafficControllerService trafficControllerService;
    private static EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Override
    public void run() {
        log.info("Proxying *:" + proxyConstant.getLocalPort() + " to " + proxyConstant.getRemoteHost() + ':' + proxyConstant.getRemotePort() + " ...");

        // Configure the bootstrap.
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            //ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //    .handler(new LoggingHandler(LogLevel.ERROR))
                    .childHandler(new ProxyInitializer(proxyConstant, trafficControllerService, proxyAccountCache))
                    .childOption(ChannelOption.AUTO_READ, false)
                    .bind(proxyConstant.getLocalPort()).sync().channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("netty start exception:{}", e);
        }
    }

    @PostConstruct
    public void initNettyServer() {
        new Thread(this).start();
    }

    @PreDestroy
    public void preDestroy() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        log.warn("spring 即将关闭，netty 关闭");
    }
}
