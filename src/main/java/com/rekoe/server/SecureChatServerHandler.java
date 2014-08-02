package com.rekoe.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.rekoe.msg.codec.AbstractMessage;
import com.rekoe.msg.codec.ChatMessage;
import com.rekoe.msg.codec.LoginMessage;
import com.rekoe.msg.codec.MessageType;

@Sharable
public class SecureChatServerHandler extends SimpleChannelInboundHandler<AbstractMessage> {

	static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	JComboBox<String> combobox;
	JTextArea textarea;
	JTextField textfield;
	UserLinkList userLinkList;// 用户链表
	Node client;
	public boolean isStop;
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
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel Channel = ctx.channel();
		Node client = Channel.attr(STATE).get();
		channels.remove(Channel);
		super.channelInactive(ctx);
	}
	@Override
	public void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {
		Channel channel = ctx.channel();
		Node client = channel.attr(STATE).get();
		short type = msg.getMessageType();
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
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}