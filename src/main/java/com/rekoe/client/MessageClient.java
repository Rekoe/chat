package com.rekoe.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import com.rekoe.msg.codec.AbstractMessage;
import com.rekoe.msg.codec.GameMessageToMessageCodec;
import com.rekoe.msg.codec.LoginMessage;
import com.rekoe.msg.codec.MessageRecognizer;

public class MessageClient {
	private static final int PORT = 8888;
	private static final String HOST = "127.0.0.1";
	private EventLoopGroup group = new NioEventLoopGroup();
	private Channel channel;
	private String username;

	public MessageClient(String username) {
		this.username = username;
	}

	public void init() throws Exception {
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {
			private final LoggingHandler LOGGING_HANDLER = new LoggingHandler();

			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new GameMessageToMessageCodec(new MessageRecognizer()));
				pipeline.addLast("LOGGING_HANDLER", LOGGING_HANDLER);
			}
		});
		ChannelFuture f = b.connect(HOST, PORT).sync();
		this.channel = f.channel();
		LoginMessage msg = new LoginMessage(username);
		channel.write(msg);
		channel.flush();
		channel.closeFuture().sync();
	}

	public static void main(String[] args) throws Exception {
		MessageClient client = new MessageClient("a");
		client.init();
	}

	public Channel getChannel() {
		return this.channel;
	}

	public void write(AbstractMessage msg) {
		channel.writeAndFlush(msg);
	}

	public void destory() {
		group.shutdownGracefully();
	}
}
