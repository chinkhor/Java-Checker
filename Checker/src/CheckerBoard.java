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
	// use Color.Black as indicator as the tile is free (unoccupied)
	private final static Color TILE_FREE = Color.black;
		
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
	
	public CheckerTile[][] getTileArray()
	{
		return tile;
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
		tile[row][col].setOccupied(TILE_FREE);
		tile[row][col].revalidate();
		tile[row][col].repaint();
	}
	
	public boolean moveValid(int pieceRow, int pieceCol, int rowDir, int colDir)
	{
		int row = pieceRow + rowDir;
		int col = pieceCol + colDir;
		
		// check out of boundary
		if (row < 0 || row >= TILES || col < 0 || col >= TILES)
			return false;
		
		// if next diagonal tile is unoccupied
		if (tile[row][col].getOccupied() == TILE_FREE)
		{
			return true;
		}
		else
			return false;
		
	}
	
	public boolean canMove(Color pieceColor, int row, int col)
	{
		// check for move possibility
		if (((pieceColor == Color.ORANGE) &&
			(moveValid(row, col, -1, 1) || moveValid(row, col, -1, -1))) ||
			((pieceColor == Color.WHITE) &&
			(moveValid(row, col, 1, 1) || moveValid(row, col, 1, -1)))) 	
		{
			return true;
		}
		else
			return false;
	}
	
	public boolean jumpValid(int pieceRow, int pieceCol, int rowDir, int colDir)
	{
		int row = pieceRow + rowDir;
		int col = pieceCol + colDir;
		int row2 = row + rowDir;
		int col2 = col + colDir;
		
		// check out of boundary
		if ((row < 0 || row >= TILES || col < 0 || col >= TILES) ||
			(row2 < 0 || row2 >= TILES || col2 < 0 || col2 >= TILES))
			return false;
		
		// if next diagonal tile is occupied by opponent piece and there is occupied tile after the opponent piece, can jump again
		if ((tile[row][col].getOccupied() == Checker.getOpponentPlayer()) && 
			(tile[row2][col2].getOccupied() == TILE_FREE))
			return true;
		else
			return false;
		
	}
	
	public boolean jumpValid(CheckerPiece piece, int pieceRow, int pieceCol, int rowDir, int colDir)
	{
		int row = pieceRow + rowDir;
		int col = pieceCol + colDir;
		int row2 = row + rowDir;
		int col2 = col + colDir;
		
		// check out of boundary
		if ((row < 0 || row >= TILES || col < 0 || col >= TILES) ||
			(row2 < 0 || row2 >= TILES || col2 < 0 || col2 >= TILES))
			return false;
		
		// if next diagonal tile is occupied by opponent piece and there is occupied tile after the opponent piece, can jump again
		if ((tile[row][col].getOccupied() == Checker.getOpponentPlayer()) && 
			(tile[row2][col2].getOccupied() == TILE_FREE))
		{
			piece.setTgtRow(row2);
			piece.setTgtCol(col2);
			return true;
		}
		else
			return false;
		
	}
	
	public boolean canJumpCapture(Color pieceColor, int row, int col)
	{
		// check for jump possibility
		if (((pieceColor == Color.ORANGE) &&
			(jumpValid(row, col, -1, 1) || jumpValid(row, col, -1, -1))) ||
			((pieceColor == Color.WHITE) &&
			(jumpValid(row, col, 1, 1) || jumpValid(row, col, 1, -1)))) 	
		{
			return true;
		}
		else
			return false;
	}
	
	public boolean flyCaptureValid(int srcRow, int srcCol, int rowDir, int colDir)
	{
		int row = srcRow + rowDir;
		int col = srcCol + colDir;
		int opponentCount = 0;
		
		while (true)
		{
			// check out of boundary
			if (row < 0 || row >= TILES || col < 0 || col >= TILES) 
				return false;
		
			if (tile[row][col].getOccupied() == Checker.getOpponentPlayer())
			{
				opponentCount++;
				// two opponent pieces found, cannot fly
				if (opponentCount > 1) // two opponent pieces along the fly
					return false;
			}
			// block by own piece, cannot fly
			else if (tile[row][col].getOccupied() == Checker.getCurrentPlayer()) 
			{
				return false;
			}
			// detect opponent piece followed by TILE FREE, can fly
			else if ((tile[row][col].getOccupied() == TILE_FREE) && (opponentCount == 1))
			{
				return true;
			}
			
			row += rowDir;
			col += colDir;
		}
	}
	
	public boolean flyCaptureValid(CheckerPiece piece, int srcRow, int srcCol, int rowDir, int colDir)
	{
		int row = srcRow + rowDir;
		int col = srcCol + colDir;
		int opponentCount = 0;
				
		while (true)
		{
			// check out of boundary
			if (row < 0 || row >= TILES || col < 0 || col >= TILES) 
				return false;
		
			if (tile[row][col].getOccupied() == Checker.getOpponentPlayer())
			{
				opponentCount++;
				// two opponent pieces found, cannot fly
				if (opponentCount > 1) // two opponent pieces along the fly
					return false;
			}
			// block by own piece, cannot fly
			else if (tile[row][col].getOccupied() == Checker.getCurrentPlayer()) 
			{
				return false;
			}
			// detect opponent piece followed by TILE FREE, can fly
			else if ((tile[row][col].getOccupied() == TILE_FREE) && (opponentCount == 1))
			{
				piece.setTgtRow(row);
				piece.setTgtCol(col);
				return true;
			}
			
			row += rowDir;
			col += colDir;
		}
	}
	
	public boolean canFlyCapture(Color pieceColor, int row, int col)
	{
		// check for fly capture possibility
		if (flyCaptureValid(row, col, -1, 1) || flyCaptureValid(row, col, -1, -1) ||
			flyCaptureValid(row, col, 1, 1)  || flyCaptureValid(row, col, 1, -1)) 	
		{
			return true;
		}
		else
			return false;
	}
	
	public boolean canFly(Color pieceColor, int row, int col)
	{
		// check for move possibility
		if (moveValid(row, col, -1, 1) || moveValid(row, col, -1, -1) ||
			moveValid(row, col, 1, 1)  || moveValid(row, col, 1, -1)) 	
		{
			return true;
		}
		else
			return false;
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
