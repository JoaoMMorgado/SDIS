package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

import network.Peer;
import engine.Engine;


public class Board extends JPanel implements ActionListener{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
public Engine engine;
Timer timer;
Peer peer;
	Board(Engine engine,Peer peer){
		this.engine = engine;
		this.peer = peer;
		addKeyListener(new Keyboard());
		this.requestFocus();
		timer = new Timer(20, this);
		timer.start();
	}
	
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		Dimension size = getSize();
		int boardTop = (int) size.getHeight() - engine.BoardHeight * squareHeight();

		for (int i = 0; i < engine.BoardHeight; ++i) {
			for (int j = 0; j < engine.BoardWidth; ++j) {
				String shape = engine.pieceAt(j, engine.BoardHeight - i - 1);
				drawSquare(g, 0 + j * squareWidth(), boardTop + i
						* squareHeight(), shape);
			}
		}

		if (engine.curPiece.getShape() != "NoShape") {
			for (int i = 0; i < 4; i++) {
				int x = engine.curX + engine.curPiece.x(i);
				int y = engine.curY - engine.curPiece.y(i);
				drawSquare(g, 0 + x * squareWidth(), boardTop
						+ (engine.BoardHeight - y - 1) * squareHeight(),
						engine.curPiece.getShape());
			}
		}
	}
	private void drawSquare(Graphics g, int x, int y, String shape) {

		Color color = null;
		if (shape.equals("ZShape"))
			color = new Color(200, 100, 100);
		else if (shape.equals("SShape"))
			color = new Color(100, 200, 100);
		else if (shape.equals("LineShape"))
			color = new Color(100, 100, 200);
		else if (shape.equals("TShape"))
			color = new Color(200, 200, 100);
		else if (shape.equals("SquareShape"))
			color = new Color(200, 100, 200);
		else if (shape.equals("LShape"))
			color = new Color(100, 200, 200);
		else if (shape.equals("MirroredLShape"))
			color = new Color(200, 170, 0);
		else if (shape.equals("NoShape"))
			color = new Color(220, 220, 220);
		else if (shape.equals("Trash"))
			color = new Color(0,0,0);

		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

	}

	int squareWidth() {
		return (int) getSize().getWidth() / engine.BoardWidth;
	}

	int squareHeight() {
		return (int) getSize().getHeight() / engine.BoardHeight;
	}
	class Keyboard extends KeyAdapter {
		public void keyPressed(KeyEvent e) {

			if (engine.isStarted && engine.curPiece.getShape() != "NoShape") {

				int keycode = e.getKeyCode();

				switch (keycode) {
				case KeyEvent.VK_ENTER:

					engine.pause();
					break;
				case KeyEvent.VK_LEFT:
					engine.checkMove(engine.curPiece, engine.curX - 1, engine.curY);
					break;
				case KeyEvent.VK_RIGHT:
					engine.checkMove(engine.curPiece, engine.curX + 1, engine.curY);
					break;
				case KeyEvent.VK_D:
					engine.checkMove(engine.curPiece.rotateRight(), engine.curX, engine.curY);
					break;
				case KeyEvent.VK_UP:
					engine.checkMove(engine.curPiece.rotateRight(), engine.curX, engine.curY);
					break;
				case KeyEvent.VK_A:
					engine.checkMove(engine.curPiece.rotateLeft(), engine.curX, engine.curY);
					break;
				case KeyEvent.VK_SPACE:
					engine.dropDown();
					break;
				case KeyEvent.VK_DOWN:
					engine.dropOneLine();
					break;
				

				}
			}

		}
	}
}


