import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CheckerBoard extends JPanel
{
	private final int TILES = 8;
	private final int TILE_SIZE = 100;
	JPanel tile[][] = new JPanel[TILES][TILES];
	
	public CheckerBoard()
	{
		super();
		this.setLayout(new GridLayout(TILES, TILES)); 
		this.setBackground(Color.RED);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		int count = 0;
		for (int row = 0; row < TILES; row++)
		{
			for (int col=0; col < TILES; col++)
			{
				tile[row][col] = new JPanel();
				tile[row][col].setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
				//alternating the color of the tile
				if (count % 2 == 0) 
				{
					tile[row][col].setBackground(Color.BLACK);
				}
				else
				{
					tile[row][col].setBackground(Color.RED);
				}
				this.add(tile[row][col]);
				
				count++;
			}
			count++;
		}
	}
}
