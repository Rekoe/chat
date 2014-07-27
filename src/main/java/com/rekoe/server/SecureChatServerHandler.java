package com.rekoe.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SecureChatServerHandler extends SimpleChannelInboundHandler<String> {

	static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	JComboBox<String> combobox;
	JTextArea textarea;
	JTextField textfield;
	UserLinkList userLinkList;// 用户链表
	Node client;
	public boolean isStop;
	
	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
		ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(new GenericFutureListener<Future<Channel>>() {
			@Override
			public void operationComplete(Future<Channel> future) throws Exception {
				ctx.writeAndFlush("Welcome to " + InetAddress.getLocalHost().getHostName() + " secure chat service!\n");
				ctx.writeAndFlush("Your session is protected by " + ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() + " cipher suite.\n");
				try {
					client = new Node();
					client.output = new ObjectOutputStream(client.socket.getOutputStream());
					client.output.flush();
					client.input = new ObjectInputStream(client.socket.getInputStream());
					client.username = (String) client.input.readObject();
					// 显示提示信息
					combobox.addItem(client.username);
					userLinkList.addUser(client);
					textarea.append("用户 " + client.username + " 上线" + "\n");
					textfield.setText("在线用户" + userLinkList.getCount() + "人\n");
				} catch (Exception e) {
				}
				channels.add(ctx.channel());
			}
		});
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
		for (Channel c : channels) {
			if (c != ctx.channel()) {
				c.writeAndFlush("[" + ctx.channel().remoteAddress() + "] " + msg + '\n');
			} else {
				c.writeAndFlush("[you] " + msg + '\n');
			}
		}
		if ("bye".equals(msg.toLowerCase())) {
			ctx.close();
		}
		
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}