package com.jhl.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
        try {
      //      log.info("Receiver:{},{}",ctx.alloc().getClass() , ctx.alloc().buffer().getClass());
            inboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                //writeAndFlush 已经回收内存的了，确保内存再次回收把
                release((ByteBuf) msg);
                if (future.isSuccess()) {
                    ctx.channel().read();
                } else {
                    future.channel().close();
                }

            });
        } catch (Exception e) {
            release((ByteBuf) msg);
            Dispatcher.closeOnFlush(ctx.channel());
        }


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

    private void release(ByteBuf msg) {
        if (msg == null) return;
        ByteBuf byteBuf = msg;
        if (byteBuf.refCnt() > 0) {
            byteBuf.release(byteBuf.refCnt());
        }
    }
}
