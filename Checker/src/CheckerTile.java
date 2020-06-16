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
	
	public CheckerTile(int row, int col, Color color)
	{
		super();
		this.row = row;
		this.col = col;
		this.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
		this.setBackground(color);
		this.setLayout(new BorderLayout());
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
