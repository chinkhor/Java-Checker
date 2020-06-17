import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CheckerBoard extends JPanel implements MouseListener
{
	public final static int TILES = 8;

	public CheckerTile tile[][] = new CheckerTile[TILES][TILES];
	
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
				//alternating the color of the tile
				if (count % 2 == 0) 
				{
					tile[row][col] = new CheckerTile(row, col, Color.BLACK);
					
					// only add MouseListener to black colored playing tiles
					tile[row][col].addMouseListener(this);			
				}
				else
				{
					// red colored tiles are not playing tiles
					tile[row][col] = new CheckerTile(row, col, Color.RED);
				}
				this.add(tile[row][col]);
				
				count++;
			}
			count++;
		}
	}
	
	public void addPiece(CheckerPiece piece, int row, int col)
	{		
		tile[row][col].add(piece, BorderLayout.CENTER);
		tile[row][col].setOccupied(piece.getColor());
		tile[row][col].revalidate();
		tile[row][col].repaint();
		piece.repaint();
	}
	
	public void removePiece(CheckerPiece piece, int row, int col)
	{		
		tile[row][col].remove(piece);
		tile[row][col].setOccupied(Color.BLACK);
		tile[row][col].revalidate();
		tile[row][col].repaint();
	}
	
	public void mouseClicked(MouseEvent e) 
	{ 
		CheckerTile tile = (CheckerTile) e.getSource();
		
		System.out.printf ("Tile[%d][%d] is clicked\n", tile.getRow(), tile.getCol());
		
		CheckerPlayer player = Checker.getPlayer(Checker.getCurrentPlayer());
		player.dstActionNotify(tile);
		
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
