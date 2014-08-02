package com.rekoe.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;

import com.rekoe.msg.codec.GameMessageToMessageCodec;
import com.rekoe.msg.codec.MessageRecognizer;

public class SecureChatClientInitializer extends ChannelInitializer<SocketChannel> {
	private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler();
	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new GameMessageToMessageCodec(new MessageRecognizer()));
		pipeline.addLast("handler", LOGGING_HANDLER);
		pipeline.addLast(new SecureChatClientHandler());
	}
}
