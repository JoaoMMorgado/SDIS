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
	boolean timeout = false;
	public Thread peerT;
	public Thread confirmConnectionT;
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

	public void processInput(String[] tokens) throws InterruptedException {
		switch (tokens[0]) {
		case "NOWANT":
			JOptionPane.showMessageDialog(null, "User dont want to play!");
			mainWindow.sidePanel.showPlayerList();
			mainWindow.sidePanel.timer.stop();
			break;
		case "CONF":
			startCheckConnectionThread();
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

			// TWITTER
			mainWindow.twitter.postToTwitter(engine.score);

			mainWindow.engine.isStarted = false;
			engine.curPiece.setShape("NoShape");
			mainWindow.sidePanel.showPlayerList();
			break;
		case "START":
			if (!mainWindow.engine.isStarted) {
				hostName = tokens[1];
				timer = new Timer(6000, new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						timeout = true;
					}
				});
				timer.setRepeats(false);
				timer.start();

				int reply = JOptionPane.showConfirmDialog(null,
						"Start Game with " + tokens[2] + "?", "Start Game",
						JOptionPane.YES_NO_OPTION);

				if (!timeout)
					if (reply == JOptionPane.YES_OPTION) {
						timer.stop();
						startCheckConnectionThread();

						mainWindow.sidePanel.nextPieceLabel.setVisible(true);
						mainWindow.sidePanel.nextPieceG.setVisible(true);
						mainWindow.boardGraph.requestFocus();
						mainWindow.sidePanel.chatButton.setVisible(false);
						if (sendConfirmStart(0)) {
							mainWindow.sidePanel.removePlayerList();
							engine.start();
							engine.multiPlayer = true;

						}
					} else {
						sendNoWant(0);

					}
				else {
					JOptionPane.showMessageDialog(null, "Invitation Expired");
					timeout = false;
				}
			} else {
				sendAlreadyPlaying(tokens[1], 0);
			}
			break;
		}
	}

	private void startCheckConnectionThread() {
		confirmConnectionT = new Thread(new Runnable() {
			public void run() {
				checkConnectionTask();
			}
		});
		confirmConnectionT.start();

	}

	public void checkConnectionTask() {

		while (mainWindow.engine.isStarted) {
			sendCheckConnection(0);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendAlreadyPlaying(String ip, int failedSends) {
		String send = "PLAYING ";

		try {
			openConnection(send, ip);
		} catch (IOException e) {
			if (failedSends < 3) {
				failedSends++;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendAlreadyPlaying(ip, failedSends);
			} else {
				engine.timer.stop();
				mainWindow.engine.isStarted = false;
				JOptionPane.showMessageDialog(null, "Don't know about host "
						+ hostName);
				engine.clearBoard();
				engine.curPiece.setShape("NoShape");
				mainWindow.sidePanel.showPlayerList();
			}
		}
	}

	private void sendCheckConnection(int failedSends) {
		String send = "IMALIVE ";

		try {
			openConnection(send, hostName);
		} catch (IOException e) {
			if (failedSends < 5) {
				failedSends++;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendCheckConnection(failedSends);
			} else {
				engine.timer.stop();
				mainWindow.engine.isStarted = false;
				JOptionPane.showMessageDialog(null, "Don't know about host "
						+ hostName);

				engine.clearBoard();
				engine.curPiece.setShape("NoShape");
				mainWindow.sidePanel.showPlayerList();

			}
		}

	}

	public void sendLine(int numLines, int failedSends) {
		String send = "ADDLINE " + numLines + " ";

		try {
			openConnection(send, hostName);
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);

			if (failedSends < 5) {
				failedSends++;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendLine(numLines, failedSends);
			} else {
				engine.timer.stop();
				mainWindow.engine.isStarted = false;
				JOptionPane.showMessageDialog(null, "Don't know about host "
						+ hostName);
				engine.clearBoard();
				engine.curPiece.setShape("NoShape");
				mainWindow.sidePanel.showPlayerList();
			}
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			if (failedSends < 5) {
				failedSends++;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendLine(numLines, failedSends);
			} else {
				engine.timer.stop();
				mainWindow.engine.isStarted = false;
				JOptionPane.showMessageDialog(null,
						"Couldn't get I/O for the connection to " + hostName);
				engine.clearBoard();
				mainWindow.engine.isStarted = false;
				engine.curPiece.setShape("NoShape");
				mainWindow.sidePanel.showPlayerList();
			}
		}
	}

	public boolean sendStart(String Ip, int failedSends) {
		hostName = Ip;
		String myIP = mainWindow.client
				.getMyIp(mainWindow.sidePanel.txtUsername.getText());
		System.out.println(myIP);
		String send = "START " + myIP + " "
				+ mainWindow.sidePanel.txtUsername.getText() + " ";

		try {
			openConnection(send, hostName);
		} catch (IOException e) {
			if (failedSends < 2) {
				failedSends++;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendStart(hostName, failedSends);
			} else {
				engine.timer.stop();
				System.err.println("Couldn't get I/O for the connection to "
						+ hostName);
				JOptionPane.showMessageDialog(null,
						"Couldn't get I/O for the connection to " + hostName);

				mainWindow.sidePanel.showPlayerList();
				return false;
			}
		}
		return true;

	}

	private boolean sendConfirmStart(int failedSends) {
		String send = "CONF ";

		try {
			openConnection(send, hostName);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			if (failedSends < 5) {
				failedSends++;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendConfirmStart(failedSends);
			} else {

				engine.timer.stop();
				mainWindow.engine.isStarted = false;
				JOptionPane.showMessageDialog(null,
						"Couldn't get I/O for the connection to " + hostName);
				engine.clearBoard();
				mainWindow.sidePanel.showPlayerList();
				return false;
			}
		}
		return true;
	}

	private void sendNoWant(int failedSends) {
		String send = "NOWANT ";

		try {
			openConnection(send, hostName);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			if (failedSends < 5) {
				failedSends++;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendNoWant(failedSends);
			} else {

				engine.timer.stop();
				mainWindow.engine.isStarted = false;
				JOptionPane.showMessageDialog(null,
						"Couldn't get I/O for the connection to " + hostName);
				engine.clearBoard();
				mainWindow.engine.isStarted = false;
				mainWindow.sidePanel.showPlayerList();
			}
		}

	}

	public void sendGameOver(int failedSends) {
		String send = "GAMEOVER ";

		try {
			openConnection(send, hostName);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);

			if (failedSends < 5) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				failedSends++;
				sendGameOver(failedSends);
			} else {
				JOptionPane.showMessageDialog(null,
						"Couldn't get I/O for the connection to " + hostName);
				engine.clearBoard();
				mainWindow.engine.isStarted = false;
				mainWindow.sidePanel.showPlayerList();
			}
		}
	}

	private void openConnection(String send, String ip) throws IOException {
		Socket echoSocket = new Socket();
		echoSocket.connect(new InetSocketAddress(ip, portNumber), 500);

		PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				echoSocket.getInputStream()));

		out.println(send);

		echoSocket.close();
		in.close();
	}
}