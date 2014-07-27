package com.rekoe.client;

import io.netty.channel.Channel;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * ����ͻ�����Ϣ�շ���
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
				if (type.equalsIgnoreCase("ϵͳ��Ϣ")) {
					String sysmsg = (String) input.readObject();
					textarea.append("ϵͳ��Ϣ: " + sysmsg);
				} else if (type.equalsIgnoreCase("����ر�")) {
					output.close();
					input.close();
					ch.close();
					textarea.append("�������ѹرգ�\n");
					break;
				} else if (type.equalsIgnoreCase("������Ϣ")) {
					String message = (String) input.readObject();
					textarea.append(message);
				} else if (type.equalsIgnoreCase("�û��б�")) {
					String userlist = (String) input.readObject();
					String usernames[] = userlist.split("\n");
					combobox.removeAllItems();

					int i = 0;
					combobox.addItem("������");
					while (i < usernames.length) {
						combobox.addItem(usernames[i]);
						i++;
					}
					combobox.setSelectedIndex(0);
					showStatus.setText("�����û� " + usernames.length + " ��");
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
}
