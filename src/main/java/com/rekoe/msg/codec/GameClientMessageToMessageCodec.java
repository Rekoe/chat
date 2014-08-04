package com.rekoe.msg.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

import com.rekoe.msg.AbstractMessage;
import com.rekoe.msg.IMessageRecognizer;

public class GameClientMessageToMessageCodec extends MessageToMessageCodec<ByteBuf, AbstractMessage> {

	private IMessageRecognizer messageRecognizer;

	public GameClientMessageToMessageCodec(IMessageRecognizer messageRecognizer) {
		this.messageRecognizer = messageRecognizer;
	}

	@Override
	protected void decode(ChannelHandlerContext chx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 6) {
			return;
		}
		//in.markReaderIndex();
		int expectLen = in.getInt(in.readerIndex());
		int buffCurLen = in.readableBytes();
		if (buffCurLen < expectLen) {
			in.resetWriterIndex();
			return;
		}
		int msgLen = in.readInt();
		short msgType = in.readShort();
		AbstractMessage msg = messageRecognizer.createMessage(msgType);
		if (msg == null) {
			int msgContentLen = msgLen - 6;
			if (0 < msgContentLen) {
				in.readBytes(msgContentLen);
			}
			return;
		}
		msg.setByteBuf(in);
		msg.read();
		out.add(msg);
	}

	@Override
	protected void encode(ChannelHandlerContext chx, AbstractMessage msg, List<Object> out) throws Exception {
		ByteBuf byteBuf = chx.alloc().buffer();
		msg.setByteBuf(byteBuf);
		msg.write();
		out.add(byteBuf);
	}

}