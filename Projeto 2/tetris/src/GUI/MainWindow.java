package GUI;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

import twitter4j.TwitterException;
import network.Client;
import network.Peer;
import network.TwitterPost;
import engine.Engine;

import java.awt.Color;
import java.io.IOException;
import java.net.URISyntaxException;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = -4271366833680464301L;

	public Engine engine;
	public Board boardGraph;
	public Peer peer;
	public Client client;
	public SidePanel sidePanel;
	JButton StartButton;
	boolean loggedIn = false;
	public SoundPlayer menuMusic;
	public TwitterPost twitter;

	public MainWindow(String ip) {
		// twitter :D
		try {
			twitter = new TwitterPost();
		} catch (ClassNotFoundException | IOException | TwitterException
				| URISyntaxException e) {

			e.printStackTrace();
		}

		menuMusic = new SoundPlayer(
				MainWindow.class.getResource("items/menuMusic.wav"));
		setResizable(false);
		getContentPane().setLayout(null);
		engine = new Engine(this);
		peer = new Peer(engine, this);
		client = new Client(ip);
		boardGraph = new Board(engine, peer);
		boardGraph.setBorder(new LineBorder(new Color(0, 0, 0)));
		boardGraph.setBounds(10, 30, 282, 530);
		getContentPane().add(boardGraph);

		sidePanel = new SidePanel(this);
		sidePanel.setBounds(312, 30, 130, 576);
		getContentPane().add(sidePanel);

		setSize(458, 646);
		setTitle("Tetris");
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {

				if (loggedIn) {
					try {
						client.logout(sidePanel.txtUsername.getText());
					} catch (Exception e) {

						e.printStackTrace();
					}
				}

				System.exit(0);
			}

		});
	}
}