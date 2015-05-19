package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import engine.Shape;

public class NextPieceGraph extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Shape nextPiece;
	NextPieceGraph(Shape next){
		nextPiece = next;
	}
	int squareWidth() { return (int) getSize().getWidth() / 4; }
    int squareHeight() { return (int) getSize().getHeight() / 4; }
    
    public void paint(Graphics g)
    { 	int incX = 0 , incY = 0;
        super.paint(g);
        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - 4 * squareHeight();
        for (int i = 0 ; i < 4 ; i++){
        	int x = nextPiece.x(i);
            if(x < 0 )
            	incX=1;
            int y = nextPiece.y(i);
            if(y<0)
            	incY=1;
        }
        for (int i = 0; i < 4; i++) {
            int x = nextPiece.x(i);
            int y = nextPiece.y(i);
            drawSquare(g,(y + incY) * squareWidth(),
                       boardTop + ( 4 - (x+incX) - 1) * squareHeight());
        }
        
        
        
    }
    
    private void drawSquare(Graphics g, int x, int y)
    {
      Color color = nextPiece.color;
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

    }

}
