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
 * ����������Ϣ����ĶԻ��� ���û��������ӷ�������IP�Ͷ˿�
 */
public class ConnectConf extends JDialog {
	private static final long serialVersionUID = 6846926546259580685L;
	JPanel panelUserConf = new JPanel();
	JButton save = new JButton();
	JButton cancel = new JButton();
	JLabel DLGINFO = new JLabel("                  Ĭ����������Ϊ  127.0.0.1:8888");

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
		// ��������λ�ã�ʹ�Ի������
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int) (screenSize.width - 400) / 2 + 50, (int) (screenSize.height - 600) / 2 + 150);
		this.setResizable(false);
	}

	private void jbInit() throws Exception {
		this.setSize(new Dimension(300, 130));
		this.setTitle("��������");
		message.setText(" �������������IP��ַ:");
		inputIp = new JTextField(10);
		inputIp.setText(userInputIp);
		inputPort = new JTextField(4);
		inputPort.setText("" + userInputPort);
		save.setText("����");
		cancel.setText("ȡ��");

		panelUserConf.setLayout(new GridLayout(2, 2, 1, 1));
		panelUserConf.add(message);
		panelUserConf.add(inputIp);
		panelUserConf.add(new JLabel(" ������������Ķ˿ں�:"));
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

		// ���水ť���¼�����
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				int savePort;
				String inputIP;
				// �ж϶˿ں��Ƿ�Ϸ�
				try {
					userInputIp = "" + InetAddress.getByName(inputIp.getText());
					userInputIp = userInputIp.substring(1);
				} catch (UnknownHostException e) {
					DLGINFO.setText("�����IP��ַ��");
					return;
				}
				// userInputIp = inputIP;

				// �ж϶˿ں��Ƿ�Ϸ�
				try {
					savePort = Integer.parseInt(inputPort.getText());

					if (savePort < 1 || savePort > 65535) {
						DLGINFO.setText("               �����˿ڱ�����0-65535֮�������!");
						inputPort.setText("");
						return;
					}
					userInputPort = savePort;
					dispose();
				} catch (NumberFormatException e) {
					DLGINFO.setText("                ����Ķ˿ں�,�˿ں�����д����!");
					inputPort.setText("");
					return;
				}
			}
		});

		// �رնԻ���ʱ�Ĳ���
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				DLGINFO.setText("                  Ĭ����������Ϊ  127.0.0.1:8888");
			}
		});

		// ȡ����ť���¼�����
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DLGINFO.setText("                  Ĭ����������Ϊ  127.0.0.1:8888");
				dispose();
			}
		});
	}
}
