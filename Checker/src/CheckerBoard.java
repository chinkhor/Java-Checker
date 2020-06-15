import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CheckerBoard extends JPanel implements MouseListener
{
	private final int TILES = 8;
	private final int TILE_SIZE = 100;
	private CheckerTile tile[][] = new CheckerTile[TILES][TILES];
	
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
				tile[row][col] = new CheckerTile(row, col);
				tile[row][col].setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
				//alternating the color of the tile
				if (count % 2 == 0) 
				{
					tile[row][col].setBackground(Color.BLACK);
					tile[row][col].addMouseListener(this);
					
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
	
	public void mouseClicked(MouseEvent e) 
	{ 
		CheckerTile tile = (CheckerTile) e.getSource();
		
		System.out.printf ("Tile[%d][%d] is clicked\n", tile.getRow(), tile.getCol());
		
    }

	// the following events are dummy, not use.
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	} 	
}
