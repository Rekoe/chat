package com.rekoe.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * 服务端的侦听类
 */
public class ServerListen extends Thread {
	
	ServerSocket server;
	JComboBox<String> combobox;
	JTextArea textarea;
	JTextField textfield;
	UserLinkList userLinkList;// 用户链表

	Node client;
	ServerReceive recvThread;

	public boolean isStop;

	/*
	 * 聊天服务端的用户上线于下线侦听类
	 */
	public ServerListen(ServerSocket server, JComboBox<String> combobox, JTextArea textarea, JTextField textfield, UserLinkList userLinkList) {

		this.server = server;
		this.combobox = combobox;
		this.textarea = textarea;
		this.textfield = textfield;
		this.userLinkList = userLinkList;

		isStop = false;
	}

	public void run() {
		while (!isStop && !server.isClosed()) {
			try {
				client = new Node();
				client.socket = server.accept();
				client.output = new ObjectOutputStream(client.socket.getOutputStream());
				client.output.flush();
				client.input = new ObjectInputStream(client.socket.getInputStream());
				client.username = (String) client.input.readObject();
				// 显示提示信息
				combobox.addItem(client.username);
				userLinkList.addUser(client);
				textarea.append("用户 " + client.username + " 上线" + "\n");
				textfield.setText("在线用户" + userLinkList.getCount() + "人\n");
				recvThread = new ServerReceive(textarea, textfield, combobox, client, userLinkList);
				recvThread.start();
			} catch (Exception e) {
			}
		}
	}
}
