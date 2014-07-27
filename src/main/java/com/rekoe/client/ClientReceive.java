package com.rekoe.client;

import io.netty.channel.Channel;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * 聊天客户端消息收发类
 */
public class ClientReceive extends Thread {
	private JComboBox<String> combobox;
	private JTextArea textarea;

	Channel ch;
	ObjectOutputStream output;
	ObjectInputStream input;
	JTextField showStatus;

	public ClientReceive(Channel ch, ObjectOutputStream output, ObjectInputStream input, JComboBox<String> combobox, JTextArea textarea, JTextField showStatus) {
		this.ch = ch;
		this.output = output;
		this.input = input;
		this.combobox = combobox;
		this.textarea = textarea;
		this.showStatus = showStatus;
	}

	public void run() {
		while (!ch.isWritable()) {
			try {
				String type = (String) input.readObject();
				if (type.equalsIgnoreCase("系统信息")) {
					String sysmsg = (String) input.readObject();
					textarea.append("系统信息: " + sysmsg);
				} else if (type.equalsIgnoreCase("服务关闭")) {
					output.close();
					input.close();
					ch.close();
					textarea.append("服务器已关闭！\n");
					break;
				} else if (type.equalsIgnoreCase("聊天信息")) {
					String message = (String) input.readObject();
					textarea.append(message);
				} else if (type.equalsIgnoreCase("用户列表")) {
					String userlist = (String) input.readObject();
					String usernames[] = userlist.split("\n");
					combobox.removeAllItems();

					int i = 0;
					combobox.addItem("所有人");
					while (i < usernames.length) {
						combobox.addItem(usernames[i]);
						i++;
					}
					combobox.setSelectedIndex(0);
					showStatus.setText("在线用户 " + usernames.length + " 人");
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
}
