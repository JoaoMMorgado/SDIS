package GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.text.DefaultCaret;

import network.Client;

public class Chat extends JFrame {

	private static final long serialVersionUID = 6190183012403148324L;

	private Client client;
	private String username;
	private JTextArea messages;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Chat frame = new Chat(new Client(), "manel");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Chat(Client client, String username) {
		this.client = client;
		this.username = username;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 350, 600);
		getContentPane().setLayout(null);
		setResizable(false);

		JLabel lblChat = new JLabel("CHAT");
		lblChat.setHorizontalAlignment(SwingConstants.CENTER);
		lblChat.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblChat.setBounds(138, 13, 56, 16);
		getContentPane().add(lblChat);

		messages = new JTextArea();
		DefaultCaret caret = (DefaultCaret) messages.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollMessages = new JScrollPane(messages);
		scrollMessages.setBounds(18, 46, 308, 404);
		getContentPane().add(scrollMessages);

		final JTextArea newMessage = new JTextArea();
		JScrollPane scrollNewMessages = new JScrollPane(newMessage);
		scrollNewMessages.setBounds(18, 460, 240, 80);
		getContentPane().add(scrollNewMessages);

		JButton btnSend = new JButton("Send");
		btnSend.setBounds(261, 462, 65, 77);
		getContentPane().add(btnSend);
		btnSend.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
					sendMessage(newMessage.getText());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		getMessages();
	}

	private void sendMessage(String text) throws Exception {
		client.sendMessage(text, username);
	}

	private void getMessages() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						String newMessages = client.getChatMessages();
						// System.out.println("fui ao server");
						// System.out.println(newMessages);
						messages.setText(newMessages);
						Thread.sleep(200);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
}
