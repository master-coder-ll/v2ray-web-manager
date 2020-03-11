package com.jhl.proxy;

import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Receiver extends ChannelInboundHandlerAdapter {

    private final Channel inboundChannel;

    public Receiver(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        inboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                ctx.channel().read();
            } else {
                future.channel().close();
            }

        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        Dispatcher.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info(" Receiver exceptionCaught:{}", cause);
        Dispatcher.closeOnFlush(ctx.channel());
    }
}
