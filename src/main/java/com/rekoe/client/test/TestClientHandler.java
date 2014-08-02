package com.rekoe.client.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.rekoe.msg.codec.AbstractMessage;

/**
 * @author 科技㊣²º¹³ Feb 16, 2013 2:35:33 PM http://www.rekoe.com QQ:5382211
 */
public class TestClientHandler extends SimpleChannelInboundHandler<AbstractMessage> {

	private final static Log log = Logs.get();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {
		log.infof("Server inactive: %s", ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		log.error(cause.getMessage());
		ctx.close();
	}
}
