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
import javax.swing.JCheckBox;
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

import com.rekoe.client.test.GSLoginMessage;
import com.rekoe.msg.codec.ChatMessage;
import com.rekoe.msg.codec.GameMessageToMessageCodec;
import com.rekoe.msg.codec.LoginMessage;
import com.rekoe.msg.codec.MessageRecognizer;

/*
 * ����ͻ��˵��������
 */
public class ChatClient extends JFrame implements ActionListener {

	private static final long serialVersionUID = -4066450547015554233L;
	String ip = "127.0.0.1";// ���ӵ�����˵�ip��ַ
	int port = 9010;// ���ӵ�����˵Ķ˿ں�
	String userName = "�Ҵҹ���";// �û���
	int type = 0;// 0��ʾδ���ӣ�1��ʾ������
	Image icon;// ����ͼ��
	JComboBox<String> combobox;// ѡ������Ϣ�Ľ�����
	JTextArea messageShow;// �ͻ��˵���Ϣ��ʾ
	JScrollPane messageScrollPane;// ��Ϣ��ʾ�Ĺ�����
	JLabel express, sendToLabel, messageLabel;
	JTextField clientMessage;// �ͻ�����Ϣ�ķ���
	JCheckBox checkbox;// ���Ļ�
	JComboBox<String> actionlist;// ����ѡ��
	JButton clientMessageButton;// ������Ϣ
	JTextField showStatus;// ��ʾ�û�����״̬
	ClientReceive recvThread;
	// �����˵���
	JMenuBar jMenuBar = new JMenuBar();
	// �����˵���
	JMenu operateMenu = new JMenu("����(O)");
	// �����˵���
	JMenuItem loginItem = new JMenuItem("�û���¼(I)");
	JMenuItem logoffItem = new JMenuItem("�û�ע��(L)");
	JMenuItem exitItem = new JMenuItem("�˳�(X)");
	JMenu conMenu = new JMenu("����(C)");
	JMenuItem userItem = new JMenuItem("�û�����(U)");
	JMenuItem connectItem = new JMenuItem("��������(C)");
	JMenu helpMenu = new JMenu("����(H)");
	JMenuItem helpItem = new JMenuItem("����(H)");
	// ����������
	JToolBar toolBar = new JToolBar();
	// �����������еİ�ť���
	JButton loginButton;// �û���¼
	JButton logoffButton;// �û�ע��
	JButton userButton;// �û���Ϣ������
	JButton connectButton;// ��������
	JButton exitButton;// �˳���ť
	// ��ܵĴ�С
	Dimension faceSize = new Dimension(400, 600);
	JPanel downPanel;
	GridBagLayout girdBag;
	GridBagConstraints girdBagCon;

	public ChatClient() {
		init();// ��ʼ������

		// ��ӿ�ܵĹر��¼�����
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		// ���ÿ�ܵĴ�С
		this.setSize(faceSize);
		// ��������ʱ���ڵ�λ��
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int) (screenSize.width - faceSize.getWidth()) / 2, (int) (screenSize.height - faceSize.getHeight()) / 2);
		this.setResizable(false);
		this.setTitle("�����ҿͻ���"); // ���ñ���
		// ����ͼ��
		icon = getImage("image/icon.gif");
		this.setIconImage(icon); // ���ó���ͼ��
		show();
		// Ϊ�����˵��������ȼ�'V'
		operateMenu.setMnemonic('O');
		// Ϊ�û���¼���ÿ�ݼ�Ϊctrl+i
		loginItem.setMnemonic('I');
		loginItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
		// Ϊ�û�ע����ݼ�Ϊctrl+l
		logoffItem.setMnemonic('L');
		logoffItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
		// Ϊ�˳���ݼ�Ϊctrl+x
		exitItem.setMnemonic('X');
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		// Ϊ���ò˵��������ȼ�'C'
		conMenu.setMnemonic('C');
		// Ϊ�û��������ÿ�ݼ�Ϊctrl+u
		userItem.setMnemonic('U');
		userItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
		// Ϊ�����������ÿ�ݼ�Ϊctrl+c
		connectItem.setMnemonic('C');
		connectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		// Ϊ�����˵��������ȼ�'H'
		helpMenu.setMnemonic('H');
		// Ϊ�������ÿ�ݼ�Ϊctrl+p
		helpItem.setMnemonic('H');
		helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
	}

	/**
	 * �����ʼ������
	 */
	public void init() {
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		// ��Ӳ˵���
		operateMenu.add(loginItem);
		operateMenu.add(logoffItem);
		operateMenu.add(exitItem);
		jMenuBar.add(operateMenu);
		conMenu.add(userItem);
		conMenu.add(connectItem);
		jMenuBar.add(conMenu);
		helpMenu.add(helpItem);
		jMenuBar.add(helpMenu);
		setJMenuBar(jMenuBar);
		// ��ʼ����ť
		loginButton = new JButton("��¼");
		logoffButton = new JButton("ע��");
		userButton = new JButton("�û�����");
		connectButton = new JButton("��������");
		exitButton = new JButton("�˳�");
		// ����������ʾ��Ϣ
		loginButton.setToolTipText("���ӵ�ָ���ķ�����");
		logoffButton.setToolTipText("��������Ͽ�����");
		userButton.setToolTipText("�����û���Ϣ");
		connectButton.setToolTipText("������Ҫ���ӵ��ķ�������Ϣ");
		// ����ť��ӵ�������
		toolBar.add(userButton);
		toolBar.add(connectButton);
		toolBar.addSeparator();// ��ӷָ���
		toolBar.add(loginButton);
		toolBar.add(logoffButton);
		toolBar.addSeparator();// ��ӷָ���
		toolBar.add(exitButton);
		contentPane.add(toolBar, BorderLayout.NORTH);
		checkbox = new JCheckBox("���Ļ�");
		checkbox.setSelected(false);
		actionlist = new JComboBox<String>();
		actionlist.addItem("΢Ц��");
		actionlist.addItem("���˵�");
		actionlist.addItem("�����");
		actionlist.addItem("������");
		actionlist.addItem("С�ĵ�");
		actionlist.addItem("������");
		actionlist.setSelectedIndex(0);
		// ��ʼʱ
		loginButton.setEnabled(true);
		logoffButton.setEnabled(false);
		// Ϊ�˵�������¼�����
		loginItem.addActionListener(this);
		logoffItem.addActionListener(this);
		exitItem.addActionListener(this);
		userItem.addActionListener(this);
		connectItem.addActionListener(this);
		helpItem.addActionListener(this);
		// ��Ӱ�ť���¼�����
		loginButton.addActionListener(this);
		logoffButton.addActionListener(this);
		userButton.addActionListener(this);
		connectButton.addActionListener(this);
		exitButton.addActionListener(this);
		combobox = new JComboBox<String>();
		combobox.insertItemAt("������", 0);
		combobox.setSelectedIndex(0);
		messageShow = new JTextArea();
		messageShow.setEditable(false);
		// ��ӹ�����
		messageScrollPane = new JScrollPane(messageShow, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setPreferredSize(new Dimension(400, 400));
		messageScrollPane.revalidate();
		clientMessage = new JTextField(23);
		clientMessage.setEnabled(false);
		clientMessageButton = new JButton();
		clientMessageButton.setText("����");
		// ���ϵͳ��Ϣ���¼�����
		clientMessage.addActionListener(this);
		clientMessageButton.addActionListener(this);
		sendToLabel = new JLabel("������:");
		express = new JLabel("         ����:   ");
		messageLabel = new JLabel("������Ϣ:");
		downPanel = new JPanel();
		girdBag = new GridBagLayout();
		downPanel.setLayout(girdBag);
		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 0;
		girdBagCon.gridwidth = 5;
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
		// girdBagCon.ipadx = 5;
		// girdBagCon.ipady = 5;
		girdBag.setConstraints(sendToLabel, girdBagCon);
		downPanel.add(sendToLabel);
		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 1;
		girdBagCon.gridy = 2;
		girdBagCon.anchor = GridBagConstraints.LINE_START;
		girdBag.setConstraints(combobox, girdBagCon);
		downPanel.add(combobox);
		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 2;
		girdBagCon.gridy = 2;
		girdBagCon.anchor = GridBagConstraints.LINE_END;
		girdBag.setConstraints(express, girdBagCon);
		downPanel.add(express);
		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 3;
		girdBagCon.gridy = 2;
		girdBagCon.anchor = GridBagConstraints.LINE_START;
		// girdBagCon.insets = new Insets(1,0,0,0);
		// girdBagCon.ipadx = 5;
		// girdBagCon.ipady = 5;
		girdBag.setConstraints(actionlist, girdBagCon);
		downPanel.add(actionlist);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 4;
		girdBagCon.gridy = 2;
		girdBagCon.insets = new Insets(1, 0, 0, 0);
		// girdBagCon.ipadx = 5;
		// girdBagCon.ipady = 5;
		girdBag.setConstraints(checkbox, girdBagCon);
		downPanel.add(checkbox);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 3;
		girdBag.setConstraints(messageLabel, girdBagCon);
		downPanel.add(messageLabel);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 1;
		girdBagCon.gridy = 3;
		girdBagCon.gridwidth = 3;
		girdBagCon.gridheight = 1;
		girdBag.setConstraints(clientMessage, girdBagCon);
		downPanel.add(clientMessage);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 4;
		girdBagCon.gridy = 3;
		girdBag.setConstraints(clientMessageButton, girdBagCon);
		downPanel.add(clientMessageButton);

		showStatus = new JTextField(35);
		showStatus.setEditable(false);
		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 5;
		girdBagCon.gridwidth = 5;
		girdBag.setConstraints(showStatus, girdBagCon);
		downPanel.add(showStatus);

		contentPane.add(messageScrollPane, BorderLayout.CENTER);
		contentPane.add(downPanel, BorderLayout.SOUTH);
		// �رճ���ʱ�Ĳ���
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (type == 1) {
					DisConnect();
				}
				System.exit(0);
			}
		});
	}

	/**
	 * �¼�����
	 */
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == userItem || obj == userButton) { // �û���Ϣ����
			// �����û���Ϣ���öԻ���
			UserConf userConf = new UserConf(this, userName);
			userConf.show();
			userName = userConf.userInputName;
		} else if (obj == connectItem || obj == connectButton) { // ���ӷ��������
			// �����������öԻ���
			ConnectConf conConf = new ConnectConf(this, ip, port);
			conConf.show();
			ip = conConf.userInputIp;
			port = conConf.userInputPort;
		} else if (obj == loginItem || obj == loginButton) { // ��¼
			Connect();
		} else if (obj == logoffItem || obj == logoffButton) { // ע��
			DisConnect();
			showStatus.setText("");
		} else if (obj == clientMessage || obj == clientMessageButton) { // ������Ϣ
			SendMessage();
			clientMessage.setText("");
		} else if (obj == exitButton || obj == exitItem) { // �˳�
			int j = JOptionPane.showConfirmDialog(this, "���Ҫ�˳���?", "�˳�", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (j == JOptionPane.YES_OPTION) {
				if (type == 1) {
					DisConnect();
				}
				System.exit(0);
			}
		} else if (obj == helpItem) { // �˵����еİ���
			// ���������Ի���
			Help helpDialog = new Help(this);
			helpDialog.show();
		}
	}
	MessageClient client = new MessageClient(userName);
	public void Connect() {
		try {
			loginButton.setEnabled(false);
			loginItem.setEnabled(false);
			userButton.setEnabled(false);
			userItem.setEnabled(false);
			connectButton.setEnabled(false);
			connectItem.setEnabled(false);
			logoffButton.setEnabled(true);
			logoffItem.setEnabled(true);
			clientMessage.setEnabled(true);
			messageShow.append("���ӷ����� "+ " �ɹ�...\n");
			type = 1;// ��־λ��Ϊ������
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						client.init();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			System.out.println(e);
			return;
		}
	}

	public void DisConnect() {
		loginButton.setEnabled(true);
		loginItem.setEnabled(true);
		userButton.setEnabled(true);
		userItem.setEnabled(true);
		connectButton.setEnabled(true);
		connectItem.setEnabled(true);
		logoffButton.setEnabled(false);
		logoffItem.setEnabled(false);
		clientMessage.setEnabled(false);
		try {
			client.destory();
			messageShow.append("�Ѿ���������Ͽ�����...\n");
			type = 0;// ��־λ��Ϊδ����
		} catch (Exception e) {
			//
		}
	}

	public void SendMessage() {
		String toSomebody = combobox.getSelectedItem().toString();
		String status = "";
		if (checkbox.isSelected()) {
			status = "���Ļ�";
		}

		String action = actionlist.getSelectedItem().toString();
		String message = clientMessage.getText();

		try {
			ChatMessage chat = new ChatMessage((short) 1, message);
			client.write(chat);
		} catch (Exception e) {
			//
		}
	}

	/**
	 * ͨ���������ļ������ͼ��
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
		new ChatClient();
	}
}
