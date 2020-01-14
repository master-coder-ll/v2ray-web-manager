package com.jhl.proxy;


import com.jhl.TrafficController.TrafficController;
import com.jhl.cache.ProxyAccountCache;
import com.jhl.config.ProxyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component

public final class HexDumpProxyServer implements Runnable {

    @Autowired
    ProxyConfig proxyConfig;
    @Autowired
    ProxyAccountCache proxyAccountCache;
    @Autowired
    TrafficController trafficController;
    private static EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Override
    public void run() {
        log.info("Proxying *:" + proxyConfig.getLocalPort() + " to " + proxyConfig.getRemoteHost() + ':' + proxyConfig.getRemotePort() + " ...");

        // Configure the bootstrap.
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

             b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HexDumpProxyInitializer(proxyConfig, trafficController, proxyAccountCache))
                    .childOption(ChannelOption.AUTO_READ, false)
                    .bind(proxyConfig.getLocalPort()).sync().channel().closeFuture().sync();

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
