package com.jhl.proxy;


import com.jhl.service.TrafficControllerService;
import com.jhl.cache.ProxyAccountCache;
import com.jhl.config.ProxyConfig;
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
    ProxyConfig proxyConfig;
    @Autowired
    ProxyAccountCache proxyAccountCache;
    @Autowired
    TrafficControllerService trafficControllerService;
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
                    .childHandler(new ProxyInitializer(proxyConfig, trafficControllerService, proxyAccountCache))
                    .childOption(ChannelOption.AUTO_READ, false)
                    .bind(proxyConfig.getLocalPort()).sync().channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("netty start exception:{}", e);
        }
    }

    @PostConstruct
    public void initNettyServer() {
        new Thread(this,"starter thread").start();
    }

    @PreDestroy
    public void preDestroy() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        log.warn("netty 关闭");
    }
}
