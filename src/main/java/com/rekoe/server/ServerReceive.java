package com.rekoe.server;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * 服务器收发消息的类
 */
public class ServerReceive extends Thread {
	JTextArea textarea;
	JTextField textfield;
	JComboBox<String> combobox;
	Node client;
	UserLinkList userLinkList;// 用户链表

	public boolean isStop;

	public ServerReceive(JTextArea textarea, JTextField textfield, JComboBox<String> combobox, Node client, UserLinkList userLinkList) {

		this.textarea = textarea;
		this.textfield = textfield;
		this.client = client;
		this.userLinkList = userLinkList;
		this.combobox = combobox;

		isStop = false;
	}

	public void run() {
		// 向所有人发送用户的列表
		sendUserList();

		while (!isStop && !client.socket.isClosed()) {
			try {
				String type = (String) client.input.readObject();

				if (type.equalsIgnoreCase("聊天信息")) {
					String toSomebody = (String) client.input.readObject();
					String status = (String) client.input.readObject();
					String action = (String) client.input.readObject();
					String message = (String) client.input.readObject();

					String msg = client.username + " " + action + "对 " + toSomebody + " 说 : " + message + "\n";
					if (status.equalsIgnoreCase("悄悄话")) {
						msg = " [悄悄话] " + msg;
					}
					textarea.append(msg);

					if (toSomebody.equalsIgnoreCase("所有人")) {
						sendToAll(msg);// 向所有人发送消息
					} else {
						try {
							client.output.writeObject("聊天信息");
							client.output.flush();
							client.output.writeObject(msg);
							client.output.flush();
						} catch (Exception e) {
							// System.out.println("###"+e);
						}

						Node node = userLinkList.findUser(toSomebody);

						if (node != null) {
							node.output.writeObject("聊天信息");
							node.output.flush();
							node.output.writeObject(msg);
							node.output.flush();
						}
					}
				} else if (type.equalsIgnoreCase("用户下线")) {
					Node node = userLinkList.findUser(client.username);
					userLinkList.delUser(node);

					String msg = "用户 " + client.username + " 下线\n";
					int count = userLinkList.getCount();

					combobox.removeAllItems();
					combobox.addItem("所有人");
					int i = 0;
					while (i < count) {
						node = userLinkList.findUser(i);
						if (node == null) {
							i++;
							continue;
						}

						combobox.addItem(node.username);
						i++;
					}
					combobox.setSelectedIndex(0);

					textarea.append(msg);
					textfield.setText("在线用户" + userLinkList.getCount() + "人\n");

					sendToAll(msg);// 向所有人发送消息
					sendUserList();// 重新发送用户列表,刷新

					break;
				}
			} catch (Exception e) {
				// System.out.println(e);
			}
		}
	}

	/*
	 * 向所有人发送消息
	 */
	public void sendToAll(String msg) {
		int count = userLinkList.getCount();

		int i = 0;
		while (i < count) {
			Node node = userLinkList.findUser(i);
			if (node == null) {
				i++;
				continue;
			}

			try {
				node.output.writeObject("聊天信息");
				node.output.flush();
				node.output.writeObject(msg);
				node.output.flush();
			} catch (Exception e) {
				// System.out.println(e);
			}

			i++;
		}
	}

	/*
	 * 向所有人发送用户的列表
	 */
	public void sendUserList() {
		String userlist = "";
		int count = userLinkList.getCount();

		int i = 0;
		while (i < count) {
			Node node = userLinkList.findUser(i);
			if (node == null) {
				i++;
				continue;
			}

			userlist += node.username;
			userlist += '\n';
			i++;
		}

		i = 0;
		while (i < count) {
			Node node = userLinkList.findUser(i);
			if (node == null) {
				i++;
				continue;
			}

			try {
				node.output.writeObject("用户列表");
				node.output.flush();
				node.output.writeObject(userlist);
				node.output.flush();
			} catch (Exception e) {
				// System.out.println(e);
			}
			i++;
		}
	}
}
