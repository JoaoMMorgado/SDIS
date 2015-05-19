package engine;

import java.util.HashMap;
import java.util.Random;
import java.awt.Color;
import java.lang.Math;


public class Shape {
               
    String[] types = new String[] { "ZShape", "SShape", "LineShape", 
                   "TShape", "SquareShape", "LShape", "MirroredLShape" };
    Color colors[] = {new Color(204, 102, 102), 
            new Color(102, 204, 102), new Color(102, 102, 204), 
            new Color(204, 204, 102), new Color(204, 102, 204), 
            new Color(102, 204, 204), new Color(218, 170, 0)
        };
    public static HashMap<String,int[][]>pieces;
    public Color color;
    String pieceShape;
    private int coords[][];


    public Shape() {
    	setPiecesMap();
        coords = new int[4][2];
        setShape("NoShape");

    }

    private void setPiecesMap() {
    	pieces = new HashMap<String,int[][]>();
    	pieces.put("NoShape", new int[][]{{ 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 }});
    	pieces.put("ZShape", new int[][]{{ 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } });
    	pieces.put("SShape", new int[][]{{ 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } });
    	pieces.put("LineShape", new int[][]{{ 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 }});
    	pieces.put("TShape", new int[][]{{ -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } });
    	pieces.put("SquareShape", new int[][]{{ 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } });
    	pieces.put("LShape", new int[][]{{ -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } });
    	pieces.put("MirroredLShape", new int[][]{{ 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } });
		
	}

	public void setShape(String shape) {


        for (int i = 0; i < 4 ; i++) {
            for (int j = 0; j < 2; ++j) {
            	coords[i][j] = pieces.get(shape)[i][j] ;
            }
        }
        pieceShape = shape;

    }

    private void setX(int index, int x) { coords[index][0] = x; }
    private void setY(int index, int y) { coords[index][1] = y; }
    public int x(int index) { return coords[index][0]; }
    public int y(int index) { return coords[index][1]; }
    public String getShape()  { return pieceShape; }

    public void setRandomShape()
    {
        Random r = new Random();
        int type = r.nextInt(7);
        setShape(types[type]);
        setColor(colors[type]);
    }

    private void setColor(Color color) {
		this.color = color;
		
	}

	public int minX()
    {
      int m = coords[0][0];
      for (int i=0; i < 4; i++) {
          m = Math.min(m, coords[i][0]);
      }
      return m;
    }


    public int minY() 
    {
      int m = coords[0][1];
      for (int i=0; i < 4; i++) {
          m = Math.min(m, coords[i][1]);
      }
      return m;
    }

    public Shape rotateLeft() 
    {
        if (pieceShape == "SquareShape")
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        return result;
    }

    public Shape rotateRight()
    {
        if (pieceShape == "SquareShape")
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        return result;
    }
}