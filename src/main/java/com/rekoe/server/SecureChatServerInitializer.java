package com.rekoe.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import com.rekoe.msg.AbstractMessage;
import com.rekoe.msg.MessageRecognizer;
import com.rekoe.msg.codec.GameMessageToMessageCodec;

public class SecureChatServerInitializer extends ChannelInitializer<SocketChannel> {
	private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler();
	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new GameMessageToMessageCodec(new MessageRecognizer()));
		pipeline.addLast("handler", LOGGING_HANDLER);
		pipeline.addLast(new GameServerHandler());
	}
	static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	@Sharable
	private class GameServerHandler extends SimpleChannelInboundHandler<AbstractMessage> {
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			Channel Channel = ctx.channel();
			Channel.attr(STATE).get();
			channels.remove(Channel);
			super.channelInactive(ctx);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			ctx.close();
		}

		private AttributeKey<Node> STATE = new AttributeKey<Node>("client");

		@Override
		public void channelRegistered(final ChannelHandlerContext ctx) {
			Channel channel = ctx.channel();
			channels.add(channel);
			Node client = new Node();
			channel.attr(STATE).setIfAbsent(client);
			client.channel = channel;
			ctx.fireChannelRegistered();
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {
			Channel channel = ctx.channel();
			Node client = channel.attr(STATE).get();
			/*short type = msg.getMessageType();
			switch (type) {
			case MessageType.CS_LOGIN:
				client.username = ((LoginMessage)msg).getUsername();
				// 显示提示信息
				combobox.addItem(client.username);
				userLinkList.addUser(client);
				textarea.append("用户 " + client.username + " 上线" + "\n");
				textfield.setText("在线用户" + userLinkList.getCount() + "人\n");
				break;
			case MessageType.CS_CHAT:{
				ChatMessage _msg = (ChatMessage)msg;
				String text = _msg.getMsg();
				short _type = _msg.getType();
				textarea.append(client.username +">>说:"+text);
				break;
			}
			default:
				break;
			}*/
		}

		@Override
		public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
			super.channelUnregistered(ctx);
		}
	}
}