package com.rapidbackend.webserver.netty.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.handler.timeout.TimeoutException;
import org.jboss.netty.util.Timer;
/**
 * 
 * @author chiqiu
 *
 */
public class CloseIdleChannelHandler extends IdleStateHandler{
    static final TimeoutException TIMEOUT_EXCEPTION = new TimeoutException();
    public CloseIdleChannelHandler(
            Timer timer,
            int readerIdleTimeSeconds,
            int writerIdleTimeSeconds,
            int allIdleTimeSeconds) {

        super(timer, readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
    }
    
    @Override
    protected void channelIdle(
            ChannelHandlerContext ctx, IdleState state, long lastActivityTimeMillis) throws Exception {
        Channels.fireExceptionCaught(ctx, TIMEOUT_EXCEPTION);
    }
    
}
