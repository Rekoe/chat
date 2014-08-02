package com.rekoe.client.test;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;

@ChannelHandler.Sharable
public class TestChannelInitializer extends ChannelInitializer<SocketChannel> {
	private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler();

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("LOGGING_HANDLER", LOGGING_HANDLER);
		pipeline.addLast("handler", new TestClientHandler());
		pipeline.addLast("encoder", new TestMessageEncoder());

	}
}
