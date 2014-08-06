package com.rekoe.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.rekoe.msg.AbstractMessage;
import com.rekoe.msg.ChatMessage;
import com.rekoe.msg.LoginMessage;
import com.rekoe.msg.LoginOutMessage;
import com.rekoe.msg.LoginUsersMessage;
import com.rekoe.msg.MessageRecognizer;
import com.rekoe.msg.MessageType;
import com.rekoe.msg.codec.ByteToGameMessageDecoder;
import com.rekoe.msg.codec.GameMessageToByteEncoder;

public class GameServer extends ChannelInitializer<SocketChannel> {
	private static final Log log = Logs.get();
	private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	protected final BlockingQueue<AbstractMessage> queue = new LinkedBlockingQueue<AbstractMessage>();
	private ExecutorService EXECUTOR = Executors.newCachedThreadPool();
	private UserLinkList userLinkList;
	private JComboBox<String> combobox;

	public GameServer(JComboBox<String> combobox, final JTextField sysMessage) {
		this.combobox = combobox;
		final JComboBox<String> co = combobox;
		EXECUTOR.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						AbstractMessage msg = queue.take();
						short type = msg.getMessageType();
						switch (type) {
						case MessageType.CS_CHAT: {
							ChatMessage _msg = (ChatMessage) msg;
							short channelType = _msg.getType();
							if (channelType == 2) {
								broadcasts(_msg);
								sysMessage.setText("");// 将发送消息栏的消息清空
							} else {
								// 向某个用户发送消息
								Node node = userLinkList.findUser(_msg.getToUser());
								try {
									node.channel.writeAndFlush(_msg);
								} catch (Exception e) {
									System.out.println("!!!" + e);
								}
								sysMessage.setText("");// 将发送消息栏的消息清空
							}
							break;
						}
						case MessageType.CS_LOGIN: {
							LoginMessage _msg = (LoginMessage) msg;
							co.addItem(_msg.getUsername());
							broadcasts(new LoginUsersMessage(userLinkList.users()));
							break;
						}
						case MessageType.CS_LOGIN_OUT: {
							LoginOutMessage _msg = (LoginOutMessage) msg;
							broadcasts(_msg);
							co.removeItem(_msg.getUsername());
							break;
						}
						default:
							break;
						}
					} catch (InterruptedException ex) {
						log.errorf(ex.getMessage());
					}
				}
			}
		});
	}

	private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler();
	private final GameServerHandler serverHandler = new GameServerHandler();

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new ByteToGameMessageDecoder(new MessageRecognizer())).addLast(new GameMessageToByteEncoder());
		pipeline.addLast("LOGGING_HANDLER", LOGGING_HANDLER);
		pipeline.addLast("handler", serverHandler);
		pipeline.addLast("idleStateHandler", new IdleStateHandler(5, 5, 8,TimeUnit.SECONDS));
		pipeline.addLast("heartHandler", new HeartHandler());
	}

	EventLoopGroup bossGroup = new NioEventLoopGroup();
	EventLoopGroup workerGroup = new NioEventLoopGroup();

	public void connect(int port, UserLinkList userLinkList) throws Exception {
		this.userLinkList = userLinkList;
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(this);
		ChannelFuture f = b.bind(port).sync();
		f.channel().closeFuture().sync();
	}

	/**
	 * 消息群发
	 * 
	 * @param msg
	 */
	public void broadcasts(AbstractMessage msg) {
		channels.writeAndFlush(msg);
	}

	public void addChannel(Channel channel) {
		channels.add(channel);
	}

	public void stopServer() throws Exception {
		EXECUTOR.shutdown();
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

	@Sharable
	private class GameServerHandler extends SimpleChannelInboundHandler<AbstractMessage> {

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();
		}

		private AttributeKey<Node> STATE = AttributeKey.valueOf("client");

		@Override
		public void channelRegistered(final ChannelHandlerContext ctx) {
			Channel channel = ctx.channel();
			Node client = new Node();
			channel.attr(STATE).setIfAbsent(client);
			client.channel = channel;
			channels.add(channel);
			userLinkList.addUser(client);
			ctx.fireChannelRegistered();
		}

		@Override
		protected void messageReceived(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {
			Channel channel = ctx.channel();
			if (msg instanceof LoginMessage) {
				Node client = channel.attr(STATE).get();
				client.username = ((LoginMessage) msg).getUsername();
			}
			queue.add(msg);
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			Channel channel = ctx.channel();
			channels.remove(channel);
			Node client = channel.attr(STATE).get();
			userLinkList.delUser(client);
			combobox.removeItem(client.username);
			log.infof("client exit user[%s]", client.username);
			super.channelUnregistered(ctx);
			broadcasts(new LoginOutMessage(client.username));
		}
	}
}
