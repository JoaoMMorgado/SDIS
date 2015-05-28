package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

import network.Client;
import network.Peer;
import engine.Engine;

import java.awt.Color;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Engine engine;
	Board boardGraph;
	public Peer peer;
	public Client client;
	public SidePanel sidePanel;
	JButton StartButton;
	boolean loggedIn = false;
	static SoundPlayer menuMusic;

	public MainWindow() {

		menuMusic = new SoundPlayer(
				MainWindow.class.getResource("items/menuMusic.wav"));
		setResizable(false);
		// setUndecorated(true);
		getContentPane().setLayout(null);
		engine = new Engine(this);
		peer = new Peer(engine, this);
		client = new Client();
		boardGraph = new Board(engine, peer);
		boardGraph.setBorder(new LineBorder(new Color(0, 0, 0)));
		boardGraph.setBounds(10, 30, 282, 530);
		getContentPane().add(boardGraph);

		StartButton = new JButton("Start");
		StartButton.setBounds(10, 583, 98, 23);
		setStartButton();
		getContentPane().add(StartButton);
		StartButton.setVisible(false);

		sidePanel = new SidePanel(this);
		sidePanel.setBounds(312, 30, 130, 547);
		getContentPane().add(sidePanel);

		setSize(458, 646);
		setTitle("Tetris");
		// setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {

			
				if (loggedIn) {
					try {
						System.out.println(sidePanel.txtUsername.getText());
						client.logout(sidePanel.txtUsername.getText());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				System.exit(0);
			}
		
		});
	}

	private void setStartButton() {
		StartButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sidePanel.nextPieceLabel.setVisible(true);
				sidePanel.nextPieceG.setVisible(true);
				try {
					peer.sendStart();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				boardGraph.requestFocus();

			}

		});

	}

	public static void main(String[] args) {

		MainWindow game = new MainWindow();
		game.setLocationRelativeTo(null);
		game.setVisible(true);
		// menuMusic.play();

	}
}