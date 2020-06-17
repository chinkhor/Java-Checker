import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CheckerTile extends JPanel 
{
	public static final int TILE_SIZE = 100;
	
	private int row;
	private int col;
	private Color occupied;
	
	public CheckerTile(int row, int col, Color color)
	{
		super();
		this.row = row;
		this.col = col;
		this.occupied = Color.BLACK; // use black color to indicate unoccupied tile
		this.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
		this.setBackground(color);
		this.setLayout(new BorderLayout());
	}
	
	public void setOccupied (Color color)
	{
		this.occupied = color;
	}
	
	public Color getOccupied()
	{
		return occupied;
	}
	
	public int getRow()
	{
		return this.row;
	}
	
	public int getCol()
	{
		return this.col;
	}
}
