package com.rekoe.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.nutz.log.Log;
import org.nutz.log.Logs;

public class HeartHandler extends ChannelHandlerAdapter{
	private final static Log log = Logs.get();
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    	if (evt instanceof IdleStateEvent) {
    		IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
            	log.info("READER_IDLE Close");
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush("new PingMessage()");
                log.info("new PingMessage()");
            }
        }
    }
}
