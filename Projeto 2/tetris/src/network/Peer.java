package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import GUI.Board;
import GUI.MainWindow;
import engine.Engine;

public class Peer {
	Engine engine;
	public Board board;
	String hostName ;
	MainWindow mainWindow;
	int portNumber = 8000;

	public Peer(Engine engine, MainWindow mainWindow) {
		this.engine = engine;
		this.mainWindow = mainWindow;
		
	}
	public void start(){
		Thread peerT = new Thread(new Runnable() {

			public void run() {
				try {
					startServer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		peerT.start();
	}
	public void startServer() throws IOException {

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

	public void processInput(String[] tokens) throws IOException {
		if (tokens[0].equals("CONF")) {
			engine.start();
		} else if (tokens[0].equals("ADDLINE")) {
			System.out.println("lixo: " + Integer.parseInt(tokens[1]));
			for (int i = 0; i < Integer.parseInt(tokens[1]); i++) {
				engine.addOneLine();
			}
		} else if (tokens[0].equals("GAMEOVER")) {
			engine.timer.stop();
			JOptionPane.showMessageDialog(null, "You Won");
			engine.clearBoard();
			engine.curPiece.setShape("NoShape");
		} else if (tokens[0].equals("START")) {
			if(!mainWindow.engine.isStarted){
			hostName = tokens[1];
			int reply = JOptionPane.showConfirmDialog(null, "Start Game with "
					+ tokens[2] + "?", "Start Game", JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
				mainWindow.sidePanel.nextPieceLabel.setVisible(true);
				mainWindow.sidePanel.nextPieceG.setVisible(true);

				sendConfirmStart();

				engine.start();
				board.requestFocus();
			} else {
				JOptionPane.showMessageDialog(null, "GOODBYE");
			}
			}
		}
	}

	public void sendLine(int numLines) throws IOException {
		String send = "ADDLINE " + numLines + " ";

		try (Socket echoSocket = new Socket(hostName, portNumber);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
						true);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						echoSocket.getInputStream()));
				BufferedReader stdIn = new BufferedReader(
						new InputStreamReader(System.in))) {
			out.println(send);
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			// System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			// System.exit(1);
		}
	}

	public void sendStart(String Ip) throws Exception {
		hostName = Ip;
		String myIP = mainWindow.client.getMyIp(mainWindow.sidePanel.txtUsername.getText());
		System.out.println(myIP);
		String send = "START " + myIP + " " + mainWindow.sidePanel.txtUsername.getText() + " ";

		try (Socket echoSocket = new Socket(hostName, portNumber);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
						true);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						echoSocket.getInputStream()));
				BufferedReader stdIn = new BufferedReader(
						new InputStreamReader(System.in))) {
			out.println(send);
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			// System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			// System.exit(1);
		}

	}

	private void sendConfirmStart() {
		String send = "CONF ";

		try (Socket echoSocket = new Socket(hostName, portNumber);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
						true);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						echoSocket.getInputStream()));
				BufferedReader stdIn = new BufferedReader(
						new InputStreamReader(System.in))) {
			out.println(send);
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			// System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			// System.exit(1);
		}
	}

	public void sendGameOver() {
		String send = "GAMEOVER";

		try (Socket echoSocket = new Socket(hostName, portNumber);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
						true);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						echoSocket.getInputStream()));
				BufferedReader stdIn = new BufferedReader(
						new InputStreamReader(System.in))) {
			out.println(send);
			System.out.println(in.readLine());
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			// System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			// System.exit(1);
		}
	}
}