package com.rekoe.client.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TestMessageEncoder extends MessageToByteEncoder<AbstractTestMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, AbstractTestMessage msg, ByteBuf out) throws Exception {
		msg.ByteBuf(out);
		msg.write();
		ctx.writeAndFlush(msg);
	}

}