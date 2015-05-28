package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
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
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class SidePanel extends JPanel {
	
	private static final long serialVersionUID = -3413635382022654561L;
	
	MainWindow mainWindow;
	public JLabel ScoreBar;
	public JTextField txtUsername;
	private JTextField txtPassword;
	private JTextField txtUsernameRegister;
	private JTextField txtPasswordRegister;
	private JTextField txtRegisterConfirm;
	JButton btnLogin;
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
	public NextPieceGraph nextPieceG;
	JButton StartButton;
	boolean loggedIn = false;
	Vector<String[]> onlineUsers;
	JList<String> lista = new JList<String>();

	SidePanel(MainWindow tetris) {

		ScheduledExecutorService exec = Executors
				.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {

					RefreshUserList();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}, 0, 5, TimeUnit.SECONDS);
		this.mainWindow = tetris;
		// setBounds(312, 30, 130, 547);
		setLayout(null);

		JLabel lblScore = new JLabel("Score: ");
		lblScore.setBounds(10, 11, 95, 14);
		add(lblScore);

		ScoreBar = new JLabel(" 0");
		ScoreBar.setBounds(10, 39, 95, 14);
		add(ScoreBar);

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

		txtPassword = new JTextField();
		txtPassword.setText("");
		txtPassword.setBounds(10, 283, 86, 20);
		add(txtPassword);
		txtPassword.setColumns(10);

		btnLogin = new JButton("Login");
		btnLogin.setBounds(10, 314, 89, 23);
		add(btnLogin);
		btnLogin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// check login on database
				try {
					login();
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		lblRegister = new JLabel("Register");
		lblRegister.setBounds(10, 348, 46, 14);
		add(lblRegister);

		txtUsernameRegister = new JTextField();
		txtUsernameRegister.setText("");
		txtUsernameRegister.setBounds(10, 390, 86, 20);
		add(txtUsernameRegister);
		txtUsernameRegister.setColumns(10);

		txtPasswordRegister = new JTextField();
		txtPasswordRegister.setBounds(10, 432, 86, 20);
		add(txtPasswordRegister);
		txtPasswordRegister.setColumns(10);

		txtRegisterConfirm = new JTextField();
		txtRegisterConfirm.setText("");

		txtRegisterConfirm.setBounds(10, 471, 86, 20);
		add(txtRegisterConfirm);
		txtRegisterConfirm.setColumns(10);

		btnRegister = new JButton("Register");
		btnRegister.setBounds(10, 502, 89, 23);
		add(btnRegister);
		btnRegister.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (mainWindow.client.register(
							txtUsernameRegister.getText(),
							txtRegisterConfirm.getText(),
							txtPasswordRegister.getText()))
						removeRegister();
				} catch (Exception e) {
					// TODO Auto-generated catch block
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
		initializeUserList();
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
			mainWindow.loggedIn = true;
			mainWindow.StartButton.setVisible(true);
			txtUsername.setVisible(false);
			txtPassword.setVisible(false);

			btnLogin.setVisible(false);
			lblLogin.setVisible(false);

			lblUsername.setVisible(false);
			lblPassword.setVisible(false);
			removeRegister();
			players.setVisible(true);
			mainWindow.peer.start();
		} else {
			String st = "Wrong Username/Password";
			JOptionPane.showMessageDialog(null, st);
		}
	}

	void initializeUserList() {
		players = new JFrame();
		players.setLayout(null);
		// players.setUndecorated(true);
		// players.setLocationRelativeTo(null);
		players.setSize(200, 320);
		players.setResizable(false);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		players.setLocation(dim.width / 2 - players.getSize().width / 2,
				dim.height / 2 - players.getSize().height / 2);
		lista = new JList<String>();
		lista.setBounds(5, 5, 190, 290);
		players.add(lista);
		JButton startPlaying = new JButton();
		startPlaying.setBounds(5, 290, 190, 15);
		players.add(startPlaying);

	}

	private void RefreshUserList() throws Exception {
		Vector<String> strings = new Vector<String>();
		onlineUsers = mainWindow.client.getCurrentUsers();
		for (int i = 0; i < onlineUsers.size(); i++) {
			if (!txtUsername.getText().equals(onlineUsers.get(i)[0]))
				strings.add(onlineUsers.get(i)[0]);
		}
		lista.setListData(strings);
	}
}