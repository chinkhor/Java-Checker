import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class CheckerPiece extends JButton
{
	private Color color;
	private int pieceX = CheckerTile.TILE_SIZE/4;
	private int pieceY = CheckerTile.TILE_SIZE/4;
	private int pieceSize = CheckerTile.TILE_SIZE/2;
	private int row, col;
	private int tgtRow, tgtCol;
	private String label;
	private boolean select;
	private boolean preSelect;
	private Image img;	
	private boolean crowned;
	private int nextAction = 0;
	private int moveRisk;
	public static final int A_MOVE = 1;
	public static final int A_FLY = 2;
	public static final int A_JUMP = 3;
	public static final int A_FLYCAPTURE = 4;
	public static final int A_DOUBLE_JUMP = 5;
	public static final int A_DOUBLE_FLYCAPTURE = 6;
	// nextAction = 1: move
	// nextAction = 2: fly
	// nextAction = 3: jump
	// nextAction = 4: fly capture
	// nextAction = 5: double/continuous jump
	// nextAction = 6: double/continuous fly capture
	
	public CheckerPiece(int row, int col, Color color)
	{
		super();
		// do not paint button border, as the button will be re-drawn in oval shape
		setBorderPainted(false);
		this.color = color;
		this.row = row;
		this.col = col;
		this.select = false;
		this.preSelect = false;
		this.label = "(" + row + "," + col + ")";
		this.crowned = false;
		this.moveRisk = -1;
		this.tgtRow = -1;
		this.tgtCol = -1;
		try 
		   {
			   this.img = ImageIO.read(new File("src/crown.png"));
		   }   catch (IOException e)
		   {
			   System.out.println("Couldn't load/find crown.png");
			   System.exit(0);
		   }
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		super.paintComponent(g2);
		
		g2.setColor(color);
		g2.fillOval(pieceX, pieceY, pieceSize, pieceSize);
		if (crowned)
	    {
	    	g2.drawImage(img, pieceX, pieceY, pieceSize, pieceSize, null);
	    }  
	
		if (select)
		{
			g2.setColor(Color.BLUE);
			// increase the line thickness
			g2.setStroke(new BasicStroke(3));
		}
		g2.drawOval(pieceX, pieceY, pieceSize, pieceSize);  
	}
	
	public void setNextAction(int action)
	{
		this.nextAction = action;
	}
	
	public int getNextAction()
	{
		return this.nextAction;
	}
	
	public void setMoveRisk(int risk)
	{
		this.moveRisk = risk;
	}
	
	public int getMoveRisk()
	{
		return moveRisk;
	}
	
	public int getRow()
	{
		return this.row;
	}
	
	public int getCol()
	{
		return this.col;
	}
	
	public void setRow(int row)
	{
		this.row = row;
	}
	
	public void setCol(int col)
	{
		this.col = col;
	}
	
	public int getTgtRow()
	{
		return this.tgtRow;
	}
	
	public int getTgtCol()
	{
		return this.tgtCol;
	}
	
	public void setTgtRow(int row)
	{
		this.tgtRow = row;
	}
	
	public void setTgtCol(int col)
	{
		this.tgtCol = col;
	}
	
	public Color getColor()
	{
		return this.color;
	}
	
	public void setLabel(String str)
	{
		this.label = str;
	}
	
	public String getLabel()
	{
		return this.label;
	}
	
	public boolean isSelect()
	{
		return select;
	}
	
	public boolean getPreSelect()
	{
		return preSelect;
	}
	
	public void setPreSelect(boolean s)
	{
		this.preSelect = s;
	}

	public void select(boolean s)
	{
		this.select = s;
		repaint();
	}
	
	public void setCrown()
	{
		this.crowned = true;
		repaint();
	}
	
	public boolean getCrown()
	{
		return this.crowned;
	}
}
