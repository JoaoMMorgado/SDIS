package GUI;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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

	private boolean windowClosed;

	public Chat(Client client, String username) {
		this.client = client;
		this.username = username;

		windowClosed = false;
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				dispose();
				windowClosed = true;
			}
		});
	}

	public void startChating() {
		setBounds(100, 100, 350, 600);
		getContentPane().setLayout(null);
		setResizable(false);

		JLabel lblChat = new JLabel("CHAT");
		lblChat.setHorizontalAlignment(SwingConstants.CENTER);
		lblChat.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblChat.setBounds(138, 13, 56, 16);
		getContentPane().add(lblChat);

		messages = new JTextArea();
		messages.setEditable(false);
		DefaultCaret caret = (DefaultCaret) messages.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollMessages = new JScrollPane(messages);
		scrollMessages.setBounds(18, 46, 308, 404);
		getContentPane().add(scrollMessages);

		final JTextArea newMessage = new JTextArea();
		JScrollPane scrollNewMessages = new JScrollPane(newMessage);
		scrollNewMessages.setBounds(18, 460, 240, 80);
		getContentPane().add(scrollNewMessages);
		newMessage.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					try {
						sendMessage(newMessage.getText());
						newMessage.setText("");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
			}
		});

		JButton btnSend = new JButton("Send");
		btnSend.setBounds(261, 462, 65, 77);
		getContentPane().add(btnSend);
		btnSend.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
					sendMessage(newMessage.getText());
					newMessage.setText("");
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
						messages.setText(newMessages);
						Thread.sleep(500);
						if (windowClosed)
							break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
}
