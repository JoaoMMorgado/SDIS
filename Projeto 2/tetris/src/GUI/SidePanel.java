package GUI;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class SidePanel extends JPanel {

	private static final long serialVersionUID = -3413635382022654561L;
	public Timer timer;
	MainWindow mainWindow;
	JLabel lblScore;
	public JLabel scoreBar;
	public JTextField txtUsername;
	private JTextField txtPassword;
	private JTextField txtUsernameRegister;
	private JTextField txtPasswordRegister;
	private JTextField txtRegisterConfirm;
	JButton btnLogin;
	JButton startButton;
	JLabel lblLogin;
	JFrame players;
	JLabel lblUsername;
	JLabel lblPassword;
	JLabel lblUsername_1;
	JLabel lblConfirm;
	JLabel lblPassword_1;
	JLabel lblRegister;
	JButton btnRegister;
	public JLabel nextPieceLabel;
	public JButton chatButton ;
	public NextPieceGraph nextPieceG;
	boolean loggedIn = false;
	Vector<String[]> onlineUsers;
	public JList<String> lista = new JList<String>();
	Chat chat;
	SidePanel(MainWindow tetris) {
		setBounds(312, 30, 130, 576);
		
		this.mainWindow = tetris;
		setLayout(null);

		lblScore = new JLabel("Score: ");
		lblScore.setBounds(10, 11, 95, 14);
		add(lblScore);

		scoreBar = new JLabel(" 0");
		scoreBar.setBounds(10, 39, 95, 14);
		add(scoreBar);

		nextPieceLabel = new JLabel("NextPiece");
		nextPieceLabel.setBounds(10, 64, 95, 14);
		nextPieceLabel.setVisible(false);
		add(nextPieceLabel);

		lblLogin = new JLabel("Login");
		lblLogin.setBounds(10, 207, 46, 14);
		add(lblLogin);

		txtUsername = new JTextField();
		txtUsername.setText("");
		txtUsername.setBounds(10, 246, 86, 20);
		add(txtUsername);
		txtUsername.setColumns(10);

		txtPassword = new JPasswordField();
		txtPassword.setText("");
		txtPassword.setBounds(10, 283, 86, 20);
		add(txtPassword);
		txtPassword.setColumns(10);

		btnLogin = new JButton("Login");
		btnLogin.setBounds(10, 314, 89, 23);
		add(btnLogin);
		btnLogin.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				// check login on database
				try {
					login();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		lblRegister = new JLabel("Register");
		lblRegister.setBounds(10, 348, 60, 14);
		add(lblRegister);

		txtUsernameRegister = new JTextField();
		txtUsernameRegister.setText("");
		txtUsernameRegister.setBounds(10, 390, 86, 20);
		add(txtUsernameRegister);
		txtUsernameRegister.setColumns(10);

		txtPasswordRegister = new JPasswordField();
		txtPasswordRegister.setBounds(10, 432, 86, 20);
		add(txtPasswordRegister);
		txtPasswordRegister.setColumns(10);

		txtRegisterConfirm = new JPasswordField();
		txtRegisterConfirm.setText("");

		txtRegisterConfirm.setBounds(10, 471, 86, 20);
		add(txtRegisterConfirm);
		txtRegisterConfirm.setColumns(10);

		btnRegister = new JButton("Register");
		btnRegister.setBounds(10, 502, 89, 23);
		add(btnRegister);
		btnRegister.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
					if (mainWindow.client.register(
							txtUsernameRegister.getText(),
							txtPasswordRegister.getText(),
							txtRegisterConfirm.getText()))
						removeRegister();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});

		lblUsername = new JLabel("Username:");
		lblUsername.setBounds(10, 232, 71, 14);
		add(lblUsername);

		lblPassword = new JLabel("Password:");
		lblPassword.setBounds(10, 269, 71, 14);
		add(lblPassword);

		lblUsername_1 = new JLabel("Username:");
		lblUsername_1.setBounds(10, 373, 86, 14);
		add(lblUsername_1);

		lblPassword_1 = new JLabel("Password:");
		lblPassword_1.setBounds(10, 415, 71, 14);
		add(lblPassword_1);

		lblConfirm = new JLabel("Confirm:");
		lblConfirm.setBounds(10, 457, 86, 14);
		add(lblConfirm);

		nextPieceG = new NextPieceGraph(mainWindow.engine.nextPiece);
		nextPieceG.setLocation(10, 89);
		nextPieceG.setSize(100, 100);
		nextPieceG.setBorder(new LineBorder(new Color(0, 0, 0)));
		nextPieceG.setVisible(false);
		add(nextPieceG);
		
		scoreBar.setVisible(false);
		nextPieceLabel.setVisible(false);
		lblScore.setVisible(false);
		initializePlayerList();
	}

	private void removeRegister() {
		txtUsernameRegister.setVisible(false);
		txtPasswordRegister.setVisible(false);
		txtRegisterConfirm.setVisible(false);
		lblUsername_1.setVisible(false);
		lblConfirm.setVisible(false);
		lblPassword_1.setVisible(false);
		lblRegister.setVisible(false);
		btnRegister.setVisible(false);

	}

	protected void login() throws HeadlessException, Exception {

		if (mainWindow.client.login(txtUsername.getText(),
				txtPassword.getText())) {
			startClient();
			chatButton.setVisible(true);
			mainWindow.loggedIn = true;
			removeLogin();
			removeRegister();
			showPlayerList();
			mainWindow.peer.start();

			
			
		} else {
			String st = "Wrong Username/Password";
			JOptionPane.showMessageDialog(null, st);
		}
	}

	private void startClient() {
		ScheduledExecutorService exec = Executors
				.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					RefreshUserList();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}, 0, 4, TimeUnit.SECONDS);
		
	}

	public void removePlayerList() {
		lista.setVisible(false);
		startButton.setVisible(false);
	}
	public void showScoreMenu(){
		nextPieceG.setVisible(true);
		scoreBar.setVisible(true);
		nextPieceLabel.setVisible(true);
		lblScore.setVisible(true);
	}
	public void showPlayerList() {
		chatButton.setVisible(true);
		nextPieceG.setVisible(false);
		scoreBar.setVisible(false);
		nextPieceLabel.setVisible(false);
		lblScore.setVisible(false);
		lista.setVisible(true);
		startButton.setVisible(true);

	}

	private void removeLogin() {
		txtUsername.setVisible(false);
		txtPassword.setVisible(false);
		btnLogin.setVisible(false);
		lblLogin.setVisible(false);
		lblUsername.setVisible(false);
		lblPassword.setVisible(false);

	}

	void initializePlayerList() {

		lista = new JList<String>();
		lista.setBounds(7, 218, 115, 290);
		add(lista);
		lista.setVisible(false);
		lista.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		chatButton = new JButton("CHAT");
		chatButton.setBounds(7, 547, 115, 22);
		add(chatButton);
		chatButton.setVisible(false);
		chatButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						chat = new Chat(mainWindow.client,
								txtUsername.getText());
						chat.startChating();
						chat.setVisible(true);
					}
				});
				thread.start();
				
			}
			
		});
		
		
		startButton = new JButton("Start");
		startButton.setBounds(7, 513, 115, 23);
		add(startButton);
		startButton.setVisible(false);
		startButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (lista.getSelectedIndex() >= 0
						&& lista.getSelectedIndex() < onlineUsers.size()) {
					nextPieceLabel.setVisible(true);
					nextPieceG.setVisible(true);
					startButton.setVisible(false);
					lista.setVisible(false);
					
					timer = new Timer(5000, new ActionListener() {

						public void actionPerformed(ActionEvent arg0) {
							JOptionPane.showMessageDialog(null, "User is not responding!");
			    			showPlayerList();
						}
					});
					timer.setRepeats(false); // Only execute once
					timer.start();
					try {
						mainWindow.peer.sendStart(onlineUsers.get(lista
								.getSelectedIndex())[1]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					mainWindow.boardGraph.requestFocus();
				}
			}

		});

	}

	private void RefreshUserList() throws Exception {
		Vector<String> strings = new Vector<String>();
		onlineUsers = mainWindow.client.getCurrentUsers();
		for (int i = 0; i < onlineUsers.size(); i++) {

			if (!txtUsername.getText().equals(onlineUsers.get(i)[0]))
				strings.add(onlineUsers.get(i)[0]);
			else {
				onlineUsers.remove(i);
				i--;
			}
		}
		lista.setListData(strings);
	}
}