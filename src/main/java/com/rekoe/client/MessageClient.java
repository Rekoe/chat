package com.rekoe.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.swing.JComboBox;
import javax.swing.JTextArea;

import org.nutz.lang.Lang;
import org.nutz.lang.Times;

import com.rekoe.msg.AbstractMessage;
import com.rekoe.msg.ChatMessage;
import com.rekoe.msg.LoginMessage;
import com.rekoe.msg.MessageRecognizer;
import com.rekoe.msg.MessageType;
import com.rekoe.msg.codec.GameClientMessageToMessageCodec;

public class MessageClient {
	private EventLoopGroup group = new NioEventLoopGroup();
	private Channel channel;
	private JTextArea messageShow;
	private JComboBox<String> combobox;
	private String username;

	public void init(String username, JTextArea messageShow, String ip, int port, JComboBox<String> combobox) throws Exception {
		this.messageShow = messageShow;
		this.username = username;
		this.combobox = combobox;
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new GameClientMessageToMessageCodec(new MessageRecognizer()));
				pipeline.addLast(new GameClientHandler());
			}
		});
		ChannelFuture f = b.connect(ip, port).sync();
		this.channel = f.channel();
		LoginMessage msg = new LoginMessage(username);
		channel.writeAndFlush(msg);
		channel.closeFuture().sync();
	}

	@Sharable
	private class GameClientHandler extends SimpleChannelInboundHandler<AbstractMessage> {
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			super.channelInactive(ctx);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();
		}

		@Override
		protected void messageReceived(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {
			short type = msg.getMessageType();
			switch (type) {
			case MessageType.CS_CHAT: {
				ChatMessage _msg = (ChatMessage) msg;
				messageShow.append(_msg.getUsername() + " " + Times.sDT(Times.now()) + "\n");
				messageShow.append("    " + _msg.getMsg() + "\n");
				break;
			}
			case MessageType.CS_LOGIN: {
				LoginMessage _msg = (LoginMessage) msg;
				if (!Lang.equals(username, _msg.getUsername())) {
					combobox.addItem(_msg.getUsername());
				}
				break;
			}
			default:
				break;
			}
		}

		@Override
		public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
			super.channelUnregistered(ctx);
		}
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
