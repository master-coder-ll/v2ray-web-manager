package com.jhl.framework.proxy.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReceiverHandler extends ChannelInboundHandlerAdapter {

    private final Channel inboundChannel;

    public ReceiverHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        try {
      //      log.info("Receiver:{},{}",ctx.alloc().getClass() , ctx.alloc().buffer().getClass());
            inboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    ctx.channel().read();
                } else {
                    future.channel().close();
                }

            });
        } catch (Exception e) {
            release((ByteBuf) msg);
            DispatcherHandler.closeOnFlush(ctx.channel());
        }


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        DispatcherHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info(" Receiver exceptionCaught:{}", cause);
        DispatcherHandler.closeOnFlush(ctx.channel());
    }

    private void release(ByteBuf msg) {
        if (msg == null) return;
        ByteBuf byteBuf = msg;
        if (byteBuf.refCnt() > 0) {
            byteBuf.release(byteBuf.refCnt());
        }
    }
}
