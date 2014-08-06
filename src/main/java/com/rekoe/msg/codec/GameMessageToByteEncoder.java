package com.rekoe.msg.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.rekoe.msg.AbstractMessage;

public class GameMessageToByteEncoder extends MessageToByteEncoder<AbstractMessage>{

	@Override
	protected void encode(ChannelHandlerContext ctx, AbstractMessage msg, ByteBuf out) throws Exception {
		msg.setByteBuf(out);
		msg.write();
	}

}