package com.rekoe.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author 科技㊣²º¹³ Feb 16, 2013 2:35:33 PM http://www.rekoe.com QQ:5382211
 */
public class TestClientHandler extends SimpleChannelInboundHandler<Object> {

	private final static Log log = Logs.get();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		log.infof("Server inactive: %s", ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		log.error(cause.getMessage());
		ctx.close();
	}
}
