package network;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import GUI.MainWindow;
import engine.Engine;

public class Peer {
	Engine engine;
	String hostName;
	MainWindow mainWindow;
	int portNumber = 8000;
	int failedSend = 0;
	boolean timeout = false;
	public Thread peerT;
	Timer timer;

	public Peer(Engine engine, MainWindow mainWindow) {
		this.engine = engine;
		this.mainWindow = mainWindow;

	}

	public void start() {
		peerT = new Thread(new Runnable() {

			public void run() {
				try {
					startServer();
				} catch (IOException e) {

					e.printStackTrace();
				} catch (Exception e) {

					e.printStackTrace();
				}

			}
		});
		peerT.start();
	}

	public void startServer() throws Exception {

		System.out.println("Peer started");

		boolean stop = false;

		while (!stop) {

			try (ServerSocket serverSocket = new ServerSocket(portNumber);
					Socket clientSocket = serverSocket.accept();

					PrintWriter out = new PrintWriter(
							clientSocket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(
							new InputStreamReader(clientSocket.getInputStream()));) {

				String inputLine = "";

				inputLine = in.readLine();
				System.out.println("Received: " + inputLine);

				String[] tokens = inputLine.split(" ");

				processInput(tokens);
				in.close();
				out.close();
				serverSocket.close();
			}

		}

	}

	public void processInput(String[] tokens) throws Exception {
		switch (tokens[0]) {
		case "NOWANT":
			JOptionPane.showMessageDialog(null, "User dont want to play!");
			mainWindow.sidePanel.showPlayerList();
			mainWindow.sidePanel.timer.stop();
			break;
		case "CONF":
			mainWindow.sidePanel.showScoreMenu();
			mainWindow.sidePanel.timer.stop();
			engine.start();
			engine.multiPlayer = true;
			break;
		case "PLAYING":
			JOptionPane.showMessageDialog(null, "User is Already Playing!");
			mainWindow.sidePanel.showPlayerList();
			break;
		case "ADDLINE":
			System.out.println("lixo: " + Integer.parseInt(tokens[1]));
			for (int i = 0; i < Integer.parseInt(tokens[1]); i++) {
				engine.addOneLine();
			}
			break;
		case "GAMEOVER":
			mainWindow.client.sendScore(
					mainWindow.sidePanel.txtUsername.getText(),
					mainWindow.engine.score, true);
			mainWindow.engine.isStarted = false;
			engine.timer.stop();
			JOptionPane.showMessageDialog(null, "You won with "
					+ mainWindow.engine.score + " points!\n\n"
					+ mainWindow.client.getScores(engine.multiPlayer));
			engine.clearBoard();
			mainWindow.engine.isStarted = false;
			engine.curPiece.setShape("NoShape");
			mainWindow.sidePanel.showPlayerList();
			break;
		case "START":
			if (!mainWindow.engine.isStarted) {
				hostName = tokens[1];
				timer = new Timer(5000, new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						timeout = true;
					}
				});
				timer.setRepeats(false); // Only execute once
				timer.start();

				int reply = JOptionPane.showConfirmDialog(null,
						"Start Game with " + tokens[2] + "?", "Start Game",
						JOptionPane.YES_NO_OPTION);

				if (!timeout)
					if (reply == JOptionPane.YES_OPTION) {
						timer.stop();

						mainWindow.sidePanel.nextPieceLabel.setVisible(true);
						mainWindow.sidePanel.nextPieceG.setVisible(true);
						mainWindow.boardGraph.requestFocus();
						mainWindow.sidePanel.chatButton.setVisible(false);
						if (sendConfirmStart()) {
							mainWindow.sidePanel.removePlayerList();
							engine.start();
							engine.multiPlayer = true;

						}
					} else {
						sendNoWant();

					}
				else {
					JOptionPane.showMessageDialog(null, "Invitation Expired");
					timeout = false;
				}
			} else {
				sendAlreadyPlaying(tokens[1]);
			}
			break;
		}
	}

	private void sendAlreadyPlaying(String ip) {
		String send = "PLAYING ";

		try {
			Socket echoSocket = new Socket();
			echoSocket
					.connect(new InetSocketAddress(hostName, portNumber), 500);

			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
					true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					echoSocket.getInputStream()));

			out.println(send);

			echoSocket.close();
			in.close();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			// System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			// System.exit(1);
		}

	}

	public void sendLine(int numLines) throws IOException {
		String send = "ADDLINE " + numLines + " ";

		try {
			Socket echoSocket = new Socket();
			echoSocket
					.connect(new InetSocketAddress(hostName, portNumber), 500);

			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
					true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					echoSocket.getInputStream()));

			out.println(send);

			echoSocket.close();
			in.close();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);

			if (failedSend < 5) {
				failedSend++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendLine(numLines);
			} else {
				failedSend = 0;
				JOptionPane.showMessageDialog(null, "Don't know about host "
						+ hostName);
				engine.clearBoard();
				engine.curPiece.setShape("NoShape");
				mainWindow.sidePanel.showPlayerList();
				mainWindow.engine.isStarted = false;
				// System.exit(1);
			}
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			if (failedSend < 5) {
				failedSend++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendLine(numLines);
			} else {
				failedSend = 0;
				engine.timer.stop();
				JOptionPane.showMessageDialog(null,
						"Couldn't get I/O for the connection to " + hostName);
				engine.clearBoard();
				mainWindow.engine.isStarted = false;
				engine.curPiece.setShape("NoShape");
				mainWindow.sidePanel.showPlayerList();

				// System.exit(1);
			}
		}
	}

	public boolean sendStart(String Ip) throws Exception {
		hostName = Ip;
		String myIP = mainWindow.client
				.getMyIp(mainWindow.sidePanel.txtUsername.getText());
		System.out.println(myIP);
		String send = "START " + myIP + " "
				+ mainWindow.sidePanel.txtUsername.getText() + " ";

		try {
			Socket echoSocket = new Socket();
			echoSocket.connect(new InetSocketAddress(hostName, portNumber),
					3000);

			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
					true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					echoSocket.getInputStream()));

			out.println(send);

			echoSocket.close();
			in.close();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			mainWindow.sidePanel.timer.stop();
			JOptionPane.showMessageDialog(null, "Don't know about host "
					+ hostName);

			mainWindow.sidePanel.showPlayerList();
			return false;
		} catch (IOException e) {
			mainWindow.sidePanel.timer.stop();
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			JOptionPane.showMessageDialog(null,
					"Couldn't get I/O for the connection to " + hostName);

			mainWindow.sidePanel.showPlayerList();
			return false;
		}
		return true;

	}

	private boolean sendConfirmStart() throws InterruptedException {
		String send = "CONF ";

		try {
			Socket echoSocket = new Socket();
			echoSocket
					.connect(new InetSocketAddress(hostName, portNumber), 500);

			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
					true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					echoSocket.getInputStream()));

			out.println(send);

			echoSocket.close();
			in.close();

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);

			if (failedSend < 5) {
				failedSend++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendConfirmStart();
			} else {
				failedSend = 0;
				JOptionPane.showMessageDialog(null, "Don't know about host "
						+ hostName);
				engine.clearBoard();
				mainWindow.engine.isStarted = false;
				engine.curPiece.setShape("NoShape");
				mainWindow.sidePanel.showPlayerList();
				return false;
				// System.exit(1);
			}
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			if (failedSend < 5) {
				failedSend++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendConfirmStart();
			} else {
				failedSend = 0;
				engine.timer.stop();
				JOptionPane.showMessageDialog(null,
						"Couldn't get I/O for the connection to " + hostName);
				engine.clearBoard();
				mainWindow.engine.isStarted = false;
				engine.curPiece.setShape("NoShape");
				mainWindow.sidePanel.showPlayerList();
				return false;
				// System.exit(1);
			}
		}
		return true;
	}

	private void sendNoWant() throws InterruptedException {
		String send = "NOWANT ";

		try {
			Socket echoSocket = new Socket();
			echoSocket
					.connect(new InetSocketAddress(hostName, portNumber), 500);

			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
					true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					echoSocket.getInputStream()));

			out.println(send);

			echoSocket.close();
			in.close();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);

			if (failedSend < 5) {
				failedSend++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendNoWant();
			} else {
				failedSend = 0;
				JOptionPane.showMessageDialog(null, "Don't know about host "
						+ hostName);
				engine.clearBoard();
				mainWindow.engine.isStarted = false;
				engine.curPiece.setShape("NoShape");
				mainWindow.sidePanel.showPlayerList();
				// System.exit(1);
			}
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			if (failedSend < 5) {
				failedSend++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendNoWant();
			} else {
				failedSend = 0;
				engine.timer.stop();
				JOptionPane.showMessageDialog(null,
						"Couldn't get I/O for the connection to " + hostName);
				engine.clearBoard();
				mainWindow.engine.isStarted = false;
				engine.curPiece.setShape("NoShape");
				mainWindow.sidePanel.showPlayerList();

				// System.exit(1);
			}
		}

	}

	public void sendGameOver() throws InterruptedException {
		String send = "GAMEOVER ";

		try {
			Socket echoSocket = new Socket();
			echoSocket
					.connect(new InetSocketAddress(hostName, portNumber), 500);

			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
					true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					echoSocket.getInputStream()));

			out.println(send);

			echoSocket.close();
			in.close();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);

			if (failedSend < 5) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				failedSend++;
				sendGameOver();
			} else {
				failedSend = 0;
				JOptionPane.showMessageDialog(null, "Don't know about host "
						+ hostName);
				engine.clearBoard();
				mainWindow.engine.isStarted = false;
				engine.curPiece.setShape("NoShape");
				mainWindow.sidePanel.showPlayerList();
				// System.exit(1);
			}
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			if (failedSend < 5) {
				failedSend++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendGameOver();
			} else {
				failedSend = 0;
				engine.timer.stop();
				JOptionPane.showMessageDialog(null,
						"Couldn't get I/O for the connection to " + hostName);
				engine.clearBoard();
				mainWindow.engine.isStarted = false;
				engine.curPiece.setShape("NoShape");
				mainWindow.sidePanel.showPlayerList();

			}
		}
	}
}