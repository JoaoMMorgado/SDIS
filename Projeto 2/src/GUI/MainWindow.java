package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

import engine.Engine;

import java.awt.Color;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Engine engine;
	Board boardGraph;
	public SidePanel sidePanel;
	JButton StartButton;
	boolean loggedIn = false;

	public MainWindow() {
		setResizable(false);
		//setUndecorated(true);
		getContentPane().setLayout(null);
		engine = new Engine(this);
		boardGraph = new Board(engine);
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
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	

	private void setStartButton() {
		StartButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sidePanel.nextPieceLabel.setVisible(true);
				sidePanel.nextPieceG.setVisible(true);
				engine.start();
				boardGraph.requestFocus();

			}

		});

		
	}

	public static void main(String[] args) {

		MainWindow game = new MainWindow();
		game.setLocationRelativeTo(null);
		game.setVisible(true);

	}
}