package com.jhl.proxy;
import com.jhl.service.TrafficControllerService;
import com.jhl.cache.ProxyAccountCache;
import com.jhl.constant.ProxyConstant;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProxyInitializer extends ChannelInitializer<SocketChannel> {
    final ProxyConstant proxyConstant;
    final TrafficControllerService trafficControllerService;
    final ProxyAccountCache proxyAccountCache;

    @Override
    public void initChannel(SocketChannel ch) {

        ch.pipeline().addLast(
                //   new ChannelTrafficShapingHandler(1024*1024,0),
                new Dispatcher(proxyConstant, trafficControllerService, proxyAccountCache));
    }
}
