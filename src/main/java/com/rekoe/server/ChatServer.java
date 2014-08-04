package com.rekoe.server;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.rekoe.msg.ChatMessage;

/*
 * 聊天服务端的主框架类
 */
public class ChatServer extends JFrame implements ActionListener {
	private static final long serialVersionUID = -4093288044755684830L;
	public static int port = 8888;// 服务端的侦听端口
	private Image icon;// 程序图标
	private JComboBox<String> combobox;// 选择发送消息的接受者
	private JTextArea messageShow;// 服务端的信息显示
	private JScrollPane messageScrollPane;// 信息显示的滚动条
	private JTextField showStatus;// 显示用户连接状态
	private JLabel sendToLabel, messageLabel;
	private JTextField sysMessage;// 服务端消息的发送
	private JButton sysMessageButton;// 服务端消息的发送按钮
	private UserLinkList userLinkList;// 用户链表
	// 建立菜单栏
	private JMenuBar jMenuBar = new JMenuBar();
	// 建立菜单组
	private JMenu serviceMenu = new JMenu("服务(V)");
	// 建立菜单项
	private JMenuItem portItem = new JMenuItem("端口设置(P)");
	private JMenuItem startItem = new JMenuItem("启动服务(S)");
	private JMenuItem stopItem = new JMenuItem("停止服务(T)");
	private JMenuItem exitItem = new JMenuItem("退出(X)");
	private JMenu helpMenu = new JMenu("帮助(H)");
	private JMenuItem helpItem = new JMenuItem("帮助(H)");

	// 建立工具栏
	private JToolBar toolBar = new JToolBar();

	// 建立工具栏中的按钮组件
	private JButton portSet;// 启动服务端侦听
	private JButton startServer;// 启动服务端侦听
	private JButton stopServer;// 关闭服务端侦听
	private JButton exitButton;// 退出按钮

	// 框架的大小
	private Dimension faceSize = new Dimension(400, 600);

	private JPanel downPanel;
	private GridBagLayout girdBag;
	private GridBagConstraints girdBagCon;
	private GameServer gameServer;

	/**
	 * 服务端构造函数
	 */
	@SuppressWarnings("deprecation")
	public ChatServer() {
		init();// 初始化程序
		// 添加框架的关闭事件处理
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		// 设置框架的大小
		this.setSize(faceSize);

		// 设置运行时窗口的位置
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int) (screenSize.width - faceSize.getWidth()) / 2, (int) (screenSize.height - faceSize.getHeight()) / 2);
		this.setResizable(false);

		this.setTitle("聊天室服务端"); // 设置标题

		// 程序图标
		icon = getImage("image/icon.gif");
		this.setIconImage(icon); // 设置程序图标
		show();

		// 为服务菜单栏设置热键'V'
		serviceMenu.setMnemonic('V');

		// 为端口设置快捷键为ctrl+p
		portItem.setMnemonic('P');
		portItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));

		// 为启动服务快捷键为ctrl+s
		startItem.setMnemonic('S');
		startItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

		// 为端口设置快捷键为ctrl+T
		stopItem.setMnemonic('T');
		stopItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));

		// 为退出设置快捷键为ctrl+x
		exitItem.setMnemonic('X');
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));

		// 为帮助菜单栏设置热键'H'
		helpMenu.setMnemonic('H');

		// 为帮助设置快捷键为ctrl+p
		helpItem.setMnemonic('H');
		helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));

	}

	/**
	 * 程序初始化函数
	 */
	public void init() {

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		// 添加菜单栏
		serviceMenu.add(portItem);
		serviceMenu.add(startItem);
		serviceMenu.add(stopItem);
		serviceMenu.add(exitItem);
		jMenuBar.add(serviceMenu);
		helpMenu.add(helpItem);
		jMenuBar.add(helpMenu);
		setJMenuBar(jMenuBar);

		// 初始化按钮
		portSet = new JButton("端口设置");
		startServer = new JButton("启动服务");
		stopServer = new JButton("停止服务");
		exitButton = new JButton("退出");
		// 将按钮添加到工具栏
		toolBar.add(portSet);
		toolBar.addSeparator();// 添加分隔栏
		toolBar.add(startServer);
		toolBar.add(stopServer);
		toolBar.addSeparator();// 添加分隔栏
		toolBar.add(exitButton);
		contentPane.add(toolBar, BorderLayout.NORTH);

		// 初始时，令停止服务按钮不可用
		stopServer.setEnabled(false);
		stopItem.setEnabled(false);

		// 为菜单栏添加事件监听
		portItem.addActionListener(this);
		startItem.addActionListener(this);
		stopItem.addActionListener(this);
		exitItem.addActionListener(this);
		helpItem.addActionListener(this);

		// 添加按钮的事件侦听
		portSet.addActionListener(this);
		startServer.addActionListener(this);
		stopServer.addActionListener(this);
		exitButton.addActionListener(this);

		combobox = new JComboBox<String>();
		combobox.insertItemAt("所有人", 0);
		combobox.setSelectedIndex(0);

		messageShow = new JTextArea();
		messageShow.setEditable(false);
		// 添加滚动条
		messageScrollPane = new JScrollPane(messageShow, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setPreferredSize(new Dimension(400, 400));
		messageScrollPane.revalidate();

		showStatus = new JTextField(35);
		showStatus.setEditable(false);

		sysMessage = new JTextField(24);
		sysMessage.setEnabled(false);
		sysMessageButton = new JButton();
		sysMessageButton.setText("发送");

		// 添加系统消息的事件侦听
		sysMessage.addActionListener(this);
		sysMessageButton.addActionListener(this);

		sendToLabel = new JLabel("发送至:");
		messageLabel = new JLabel("发送消息:");
		downPanel = new JPanel();
		girdBag = new GridBagLayout();
		downPanel.setLayout(girdBag);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 0;
		girdBagCon.gridwidth = 3;
		girdBagCon.gridheight = 2;
		girdBagCon.ipadx = 5;
		girdBagCon.ipady = 5;
		JLabel none = new JLabel("    ");
		girdBag.setConstraints(none, girdBagCon);
		downPanel.add(none);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 2;
		girdBagCon.insets = new Insets(1, 0, 0, 0);
		girdBagCon.ipadx = 5;
		girdBagCon.ipady = 5;
		girdBag.setConstraints(sendToLabel, girdBagCon);
		downPanel.add(sendToLabel);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 1;
		girdBagCon.gridy = 2;
		girdBagCon.anchor = GridBagConstraints.LINE_START;
		girdBag.setConstraints(combobox, girdBagCon);
		downPanel.add(combobox);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 3;
		girdBag.setConstraints(messageLabel, girdBagCon);
		downPanel.add(messageLabel);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 1;
		girdBagCon.gridy = 3;
		girdBag.setConstraints(sysMessage, girdBagCon);
		downPanel.add(sysMessage);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 2;
		girdBagCon.gridy = 3;
		girdBag.setConstraints(sysMessageButton, girdBagCon);
		downPanel.add(sysMessageButton);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 4;
		girdBagCon.gridwidth = 3;
		girdBag.setConstraints(showStatus, girdBagCon);
		downPanel.add(showStatus);

		contentPane.add(messageScrollPane, BorderLayout.CENTER);
		contentPane.add(downPanel, BorderLayout.SOUTH);
		gameServer = new GameServer(combobox, sysMessage);
		// 关闭程序时的操作
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stopService();
				System.exit(0);
			}
		});
	}

	/**
	 * 事件处理
	 */
	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == startServer || obj == startItem) { // 启动服务端
			startService();
		} else if (obj == stopServer || obj == stopItem) { // 停止服务端
			int j = JOptionPane.showConfirmDialog(this, "真的停止服务吗?", "停止服务", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (j == JOptionPane.YES_OPTION) {
				stopService();
			}
		} else if (obj == portSet || obj == portItem) { // 端口设置
			// 调出端口设置的对话框
			PortConf portConf = new PortConf(this);
			portConf.show();
		} else if (obj == exitButton || obj == exitItem) { // 退出程序
			int j = JOptionPane.showConfirmDialog(this, "真的要退出吗?", "退出", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (j == JOptionPane.YES_OPTION) {
				stopService();
				System.exit(0);
			}
		} else if (obj == helpItem) { // 菜单栏中的帮助
			// 调出帮助对话框
			Help helpDialog = new Help(this);
			helpDialog.show();
		} else if (obj == sysMessage || obj == sysMessageButton) { // 发送系统消息
			sendSystemMessage();
		}
	}

	static int PORT = port;

	/**
	 * 启动服务端
	 */
	public void startService() {
		userLinkList = new UserLinkList();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					gameServer.connect(port, userLinkList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
		messageShow.append("服务端已经启动，在" + port + "端口侦听...\n");
		startServer.setEnabled(false);
		startItem.setEnabled(false);
		portSet.setEnabled(false);
		portItem.setEnabled(false);
		stopServer.setEnabled(true);
		stopItem.setEnabled(true);
		sysMessage.setEnabled(true);
	}

	/**
	 * 关闭服务端
	 */
	public void stopService() {
		try {
			// 向所有人发送服务器关闭的消息
			int count = userLinkList.getCount();
			int i = 0;
			while (i < count) {
				Node node = userLinkList.findUser(i);
				node.channel.close();
				i++;
			}
			stopServer.setEnabled(false);
			stopItem.setEnabled(false);
			startServer.setEnabled(true);
			startItem.setEnabled(true);
			portSet.setEnabled(true);
			portItem.setEnabled(true);
			sysMessage.setEnabled(false);
			messageShow.append("服务端已经关闭\n");
			combobox.removeAllItems();
			combobox.addItem("所有人");
			gameServer.stopServer();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * 向客户端用户发送消息
	 */
	public void sendSystemMessage() {
		String toSomebody = combobox.getSelectedItem().toString();
		String message = sysMessage.getText() + "\n";
		messageShow.append(message);
		ChatMessage chat = new ChatMessage((short) 2, message, "系统消息", toSomebody);
		// 向所有人发送消息
		if (toSomebody.equalsIgnoreCase("所有人")) {
			gameServer.broadcasts(chat);
		} else {
			// 向某个用户发送消息
			Node node = userLinkList.findUser(toSomebody);
			if (Lang.isEmpty(node)) {
				log.errorf("user[%s] not found", toSomebody);
			} else {
				node.channel.writeAndFlush(chat);
			}
			sysMessage.setText("");// 将发送消息栏的消息清空
		}
	}

	private final static Log log = Logs.get();

	/**
	 * 通过给定的文件名获得图像
	 */
	Image getImage(String filename) {
		URLClassLoader urlLoader = (URLClassLoader) this.getClass().getClassLoader();
		URL url = null;
		Image image = null;
		url = urlLoader.findResource(filename);
		image = Toolkit.getDefaultToolkit().getImage(url);
		MediaTracker mediatracker = new MediaTracker(this);
		try {
			mediatracker.addImage(image, 0);
			mediatracker.waitForID(0);
		} catch (InterruptedException _ex) {
			image = null;
		}
		if (mediatracker.isErrorID(0)) {
			image = null;
		}
		return image;
	}

	public static void main(String[] args) {
		new ChatServer();
	}
}
