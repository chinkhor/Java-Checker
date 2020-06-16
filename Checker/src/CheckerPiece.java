import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class CheckerPiece extends JButton
{
	private Color color;
	private int pieceX, pieceY;
	private int pieceSize;
	
	public CheckerPiece(Color color)
	{
		super();
		// do not paint button border, as the button will be re-drawn in oval shape
		setBorderPainted(false);
		this.color = color;
		this.pieceX = CheckerTile.TILE_SIZE/4;
		this.pieceY = CheckerTile.TILE_SIZE/4;
		this.pieceSize = CheckerTile.TILE_SIZE/2;
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		super.paintComponent(g2);
		g2.setColor(color);
		g2.drawOval(pieceX, pieceY, pieceSize, pieceSize);
		g2.fillOval(pieceX, pieceY, pieceSize, pieceSize);
	}
}
