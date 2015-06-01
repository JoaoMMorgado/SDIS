package engine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import GUI.MainWindow;

public class Engine implements ActionListener {

	public final int BoardWidth = 10;
	public final int BoardHeight = 22;
	MainWindow mainWindow;
	public Timer timer;
	public boolean isFallingFinished = false;
	public boolean isStarted = false;
	boolean isPaused = false;
	int numLinesRemoved = 0;
	public int curX = 0;
	public int curY = 0;
	JLabel statusbar;
	public Shape curPiece;
	public Shape nextPiece;
	String[] board;

	public Engine(MainWindow mainwindow) {
		mainWindow = mainwindow;
		curPiece = new Shape();
		nextPiece = new Shape();
		nextPiece.setRandomShape();
		timer = new Timer(250, this);
		board = new String[BoardWidth * BoardHeight];
		clearBoard();
	}

	public void actionPerformed(ActionEvent e) {
		if (isFallingFinished) {
			isFallingFinished = false;
			newPiece();
		} else {
			dropOneLine();
		}
	}

	public String pieceAt(int x, int y) {
		return board[(y * BoardWidth) + x];
	}

	public void start() {
		isStarted = true;
		isFallingFinished = false;
		numLinesRemoved = 0;
		clearBoard();
		newPiece();
		timer.start();
	}

	public void pause() {
		if (isStarted)
			if (!isPaused) {
				isPaused = true;
				timer.stop();
				mainWindow.sidePanel.scoreBar.setText("paused");
			} else {
				isPaused = false;
				timer.start();
				mainWindow.sidePanel.scoreBar.setText(String
						.valueOf(numLinesRemoved));
			}
	}

	public void dropDown() {
		int newY = curY;
		while (newY > 0) {
			if (!checkMove(curPiece, curX, newY - 1))
				break;
			newY--;
		}
		pieceDropped();
	}

	public void addOneLine() {
		String[] clone = board;
		for (int y = BoardHeight - 1; y > 0; y--) {
			for (int x = 0; x < BoardWidth; x++) {

				board[(y * BoardWidth) + x] = clone[((y - 1) * BoardWidth) + x];

			}
		}

		for (int x = 0; x < BoardWidth; x++) {
			board[x] = "Trash";
		}

	}

	public void dropOneLine() {
		if (!checkMove(curPiece, curX, curY - 1))
			pieceDropped();
	}

	public void clearBoard() {
		for (int i = 0; i < BoardHeight * BoardWidth; ++i)
			board[i] = "NoShape";
	}

	private void pieceDropped() {
		for (int i = 0; i < 4; i++) {
			int x = curX + curPiece.x(i);
			int y = curY - curPiece.y(i);
			board[(y * BoardWidth) + x] = curPiece.pieceShape;
		}

		removeFullLines();

		if (!isFallingFinished) {
			newPiece();

		}
	}

	public void newPiece() {

		curPiece = nextPiece;
		nextPiece = new Shape();
		nextPiece.setRandomShape();
		mainWindow.sidePanel.nextPieceG.nextPiece = nextPiece;

		mainWindow.sidePanel.nextPieceG.repaint();

		curX = BoardWidth / 2 ;
		curY = BoardHeight - 1 + curPiece.minY();

		if (!checkMove(curPiece, curX, curY)) {
			curPiece.setShape("NoShape");
			timer.stop();
			isStarted = false;
			JOptionPane.showMessageDialog(null, "You Lost");
			mainWindow.sidePanel.scoreBar.setText("game over");
			mainWindow.peer.sendGameOver();
			clearBoard();
			curPiece.setShape("NoShape");
			mainWindow.sidePanel.showPlayerList();
		}
	}

	public boolean checkMove(Shape newPiece, int newX, int newY) {
		for (int i = 0; i < 4; i++) {
			int x = newX + newPiece.x(i);
			int y = newY - newPiece.y(i);
			if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
				return false;
			if (!pieceAt(x, y).equals("NoShape"))
				return false;
		}

		curPiece = newPiece;
		curX = newX;
		curY = newY;
		return true;
	}

	private void removeFullLines() {
		Vector<Integer> lines = new Vector<Integer>();
		for (int y = BoardHeight - 1; y >= 0; y--) {
			lines.add(y);
			for (int x = 0; x < BoardWidth; x++) {
				if (pieceAt(x, y).equals("NoShape")
						|| pieceAt(x, y).equals("Trash")) {
					lines.removeElement(y);
					break;
				}
			}
		}
		String[] clone = board;
		for (int k = 0; k < lines.size(); k++) {
			clone = board;
			System.out.println(lines.elementAt(k));
			for (int y = lines.elementAt(k); y < BoardHeight - 1; y++) {
				for (int x = 0; x < BoardWidth; x++) {

					board[(y * BoardWidth) + x] = clone[((y + 1) * BoardWidth)
							+ x];

				}
			}
		}

		if (lines.size() > 0) {
			final int numLines = lines.size();
			Thread sendLineT = new Thread(new Runnable() {

	
				public void run() {
					try {
						mainWindow.peer.sendLine(numLines);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
			sendLineT.start();

			numLinesRemoved += lines.size() * lines.size() * 100;
			mainWindow.sidePanel.scoreBar.setText(String
					.valueOf(numLinesRemoved));
			isFallingFinished = true;
			curPiece.setShape("NoShape");
		}
	}

	class Keyboard extends KeyAdapter {
		public void keyPressed(KeyEvent e) {

			if (isStarted && curPiece.getShape() != "NoShape") {

				int keycode = e.getKeyCode();

				switch (keycode) {
				case KeyEvent.VK_ENTER:

					pause();
					break;
				case KeyEvent.VK_LEFT:
					checkMove(curPiece, curX - 1, curY);
					break;
				case KeyEvent.VK_RIGHT:
					checkMove(curPiece, curX + 1, curY);
					break;
				case KeyEvent.VK_D:
					checkMove(curPiece.rotateRight(), curX, curY);
					break;
				case KeyEvent.VK_UP:
					checkMove(curPiece.rotateRight(), curX, curY);
					break;
				case KeyEvent.VK_A:
					checkMove(curPiece.rotateLeft(), curX, curY);
					break;
				case KeyEvent.VK_SPACE:
					dropDown();
					break;
				case KeyEvent.VK_DOWN:
					dropOneLine();
					break;

				}
			}

		}
	}
}