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
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.rekoe.msg.AbstractMessage;
import com.rekoe.msg.ChatMessage;
import com.rekoe.msg.LoginMessage;
import com.rekoe.msg.MessageRecognizer;
import com.rekoe.msg.MessageType;
import com.rekoe.msg.codec.GameMessageToMessageCodec;

public class GameServer extends ChannelInitializer<SocketChannel> {
	private static final Log log = Logs.get();
	private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private final GameServerHandler SHARED = new GameServerHandler();
	protected final BlockingQueue<AbstractMessage> queue = new LinkedBlockingQueue<AbstractMessage>();;
	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
	private UserLinkList userLinkList;
	public GameServer(JComboBox<String> combobox,final JTextField sysMessage) {
		final JComboBox<String> co = combobox;
		Thread t = new Thread(new Runnable() {
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
							if(channelType == 2)
							{
								broadcasts(_msg);
							}else{
								// 向某个用户发送消息
								Node node = userLinkList.findUser(_msg.getToUser());
								try {
									node.channel.writeAndFlush(_msg);
								} catch (Exception e) {
									System.out.println("!!!"+e);
								}
								sysMessage.setText("");// 将发送消息栏的消息清空
							}
							break;
						}
						case MessageType.CS_LOGIN: {
							LoginMessage _msg = (LoginMessage) msg;
							co.addItem(_msg.getUsername());
							broadcasts(_msg);
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
		}, getClass().getName());
		t.start();
	}

	private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler();

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("LOGGING_HANDLER", LOGGING_HANDLER);
		pipeline.addLast(new GameMessageToMessageCodec(new MessageRecognizer()));
		pipeline.addLast("handler", SHARED);
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
		// 断开连接
		EXECUTOR.shutdown();
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

	@Sharable
	private class GameServerHandler extends SimpleChannelInboundHandler<AbstractMessage> {

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			Channel channel = ctx.channel();
			channels.remove(channel);
			super.channelInactive(ctx);
			userLinkList.delUser(channel.attr(STATE).get());
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			log.error(cause.getMessage());
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
		protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {
			Channel channel = ctx.channel();
			if (msg instanceof LoginMessage) {
				Node client = channel.attr(STATE).get();
				client.username = ((LoginMessage) msg).getUsername();
			}
			queue.add(msg);

		}

		@Override
		public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
			log.info("client unRegistered");
			super.channelUnregistered(ctx);
		}
	}

	private long lastping = 0;

	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state().equals(IdleState.READER_IDLE)) {
				log.info("READER_IDLE");
				if (lastping != 0L) {
					long time = (System.currentTimeMillis() - lastping) / 1000;
					log.info("Time : " + time);
					if (time > 3) {
						log.info("No heart beat received in 3 seconds, close channel.");
						channels.remove(ctx.channel());
						ctx.close();
					}
				}
			} else if (event.state().equals(IdleState.WRITER_IDLE)) {
				log.info("WRITER_IDLE");
			} else if (event.state().equals(IdleState.ALL_IDLE)) {
				log.info("ALL_IDLE");
				if (lastping == 0L) {
					lastping = System.currentTimeMillis();
				}
				ctx.channel().writeAndFlush("ping\n");
			}
		}
		super.userEventTriggered(ctx, evt);
	}
}
