package com.rekoe.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 生成连接信息输入的对话框 让用户输入连接服务器的IP和端口
 */
public class ConnectConf extends JDialog {
	private static final long serialVersionUID = 6846926546259580685L;
	JPanel panelUserConf = new JPanel();
	JButton save = new JButton();
	JButton cancel = new JButton();
	JLabel DLGINFO = new JLabel("                  默认连接设置为  127.0.0.1:8888");

	JPanel panelSave = new JPanel();
	JLabel message = new JLabel();

	String userInputIp;
	int userInputPort;

	JTextField inputIp;
	JTextField inputPort;

	public ConnectConf(JFrame frame, String ip, int port) {
		super(frame, true);
		this.userInputIp = ip;
		this.userInputPort = port;
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 设置运行位置，使对话框居中
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int) (screenSize.width - 400) / 2 + 50, (int) (screenSize.height - 600) / 2 + 150);
		this.setResizable(false);
	}

	private void jbInit() throws Exception {
		this.setSize(new Dimension(300, 130));
		this.setTitle("连接设置");
		message.setText(" 请输入服务器的IP地址:");
		inputIp = new JTextField(10);
		inputIp.setText(userInputIp);
		inputPort = new JTextField(4);
		inputPort.setText("" + userInputPort);
		save.setText("保存");
		cancel.setText("取消");

		panelUserConf.setLayout(new GridLayout(2, 2, 1, 1));
		panelUserConf.add(message);
		panelUserConf.add(inputIp);
		panelUserConf.add(new JLabel(" 请输入服务器的端口号:"));
		panelUserConf.add(inputPort);

		panelSave.add(new Label("              "));
		panelSave.add(save);
		panelSave.add(cancel);
		panelSave.add(new Label("              "));

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(panelUserConf, BorderLayout.NORTH);
		contentPane.add(DLGINFO, BorderLayout.CENTER);
		contentPane.add(panelSave, BorderLayout.SOUTH);

		// 保存按钮的事件处理
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				int savePort;
				// 判断端口号是否合法
				try {
					userInputIp = "" + InetAddress.getByName(inputIp.getText());
					userInputIp = userInputIp.substring(1);
				} catch (UnknownHostException e) {
					DLGINFO.setText("错误的IP地址！");
					return;
				}
				// userInputIp = inputIP;

				// 判断端口号是否合法
				try {
					savePort = Integer.parseInt(inputPort.getText());

					if (savePort < 1 || savePort > 65535) {
						DLGINFO.setText("               侦听端口必须是0-65535之间的整数!");
						inputPort.setText("");
						return;
					}
					userInputPort = savePort;
					dispose();
				} catch (NumberFormatException e) {
					DLGINFO.setText("                错误的端口号,端口号请填写整数!");
					inputPort.setText("");
					return;
				}
			}
		});

		// 关闭对话框时的操作
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				DLGINFO.setText("                  默认连接设置为  127.0.0.1:8888");
			}
		});

		// 取消按钮的事件处理
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DLGINFO.setText("                  默认连接设置为  127.0.0.1:8888");
				dispose();
			}
		});
	}
}
