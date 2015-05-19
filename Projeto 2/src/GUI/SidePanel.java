package GUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;





@SuppressWarnings("serial")
public class SidePanel extends JPanel{
	MainWindow mainWindow;
	public JLabel ScoreBar;
	private JTextField txtUsername;
	private JTextField txtPassword;
	private JTextField txtUsername_1;
	private JTextField txtPassword_1;
	private JTextField txtConfirm;
	JButton btnLogin;
	JLabel lblLogin;
	JLabel lblRegister;
	JButton btnRegister;
	JLabel lblUsername;
	JLabel lblPassword;
	JLabel lblUsername_1;
	JLabel lblConfirm;
	JLabel lblPassword_1;
	JLabel nextPieceLabel;
	public NextPieceGraph nextPieceG;
	JButton StartButton;
	boolean loggedIn = false;
SidePanel(MainWindow tetris){
	this.mainWindow = tetris;
	//setBounds(312, 30, 130, 547);
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
			login(txtUsername.getText(),txtPassword.getText());
		}
	});

	lblRegister = new JLabel("Register");
	lblRegister.setBounds(10, 348, 46, 14);
	add(lblRegister);

	txtUsername_1 = new JTextField();
	txtUsername_1.setText("");
	txtUsername_1.setBounds(10, 390, 86, 20);
	add(txtUsername_1);
	txtUsername_1.setColumns(10);

	txtPassword_1 = new JTextField();
	txtPassword_1.setBounds(10, 432, 86, 20);
	add(txtPassword_1);
	txtPassword_1.setColumns(10);

	txtConfirm = new JTextField();
	txtConfirm.setText("");

	txtConfirm.setBounds(10, 471, 86, 20);
	add(txtConfirm);
	txtConfirm.setColumns(10);

	btnRegister = new JButton("Register");
	btnRegister.setBounds(10, 502, 89, 23);
	add(btnRegister);
	btnRegister.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// check register on database

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
}

protected void login(String username, String password) {
	mainWindow.loggedIn = true;
	if(mainWindow.loggedIn){
	mainWindow.StartButton.setVisible(true);
	txtUsername.setVisible(false);
	txtPassword.setVisible(false);
	txtUsername_1.setVisible(false);
	txtPassword_1.setVisible(false);
	txtConfirm.setVisible(false);
	btnLogin.setVisible(false);
	lblLogin.setVisible(false);
	lblRegister.setVisible(false);
	btnRegister.setVisible(false);
	lblUsername.setVisible(false);
	lblPassword.setVisible(false);
	lblUsername_1.setVisible(false);
	lblConfirm.setVisible(false);
	lblPassword_1.setVisible(false);}
	else {
		String st="Wrong Username/Password";
	JOptionPane.showMessageDialog(null,st);}
}
}
