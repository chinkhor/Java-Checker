import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class CheckerPiece extends JButton
{
	private Color color;
	private int pieceX = CheckerTile.TILE_SIZE/4;
	private int pieceY = CheckerTile.TILE_SIZE/4;
	private int pieceSize = CheckerTile.TILE_SIZE/2;
	private int row, col;
	private String label;
	
	public CheckerPiece(int row, int col, Color color)
	{
		super();
		// do not paint button border, as the button will be re-drawn in oval shape
		setBorderPainted(false);
		this.color = color;
		this.row = row;
		this.col = col;
		this.label = "(" + row + "," + col + ")";
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		super.paintComponent(g2);
		g2.setColor(color);
		g2.drawOval(pieceX, pieceY, pieceSize, pieceSize);
		g2.fillOval(pieceX, pieceY, pieceSize, pieceSize);
	}
	
	public int getRow()
	{
		return this.row;
	}
	
	public int getCol()
	{
		return this.col;
	}
	
	public String getLabel()
	{
		return this.label;
	}
}
