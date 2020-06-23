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
	
	public boolean isTileInBound(int row, int col)
	{
		return ((row >= 0 && row < TILES && col >= 0 && col < TILES));
	}

	public boolean isTileFree(int row, int col)
	{
		if (isTileInBound(row, col))
			return (tile[row][col].getOccupied() == TILE_FREE);
		else
			return false;
	}
	
		
	public boolean isTileOccupiedByPlayer(int row, int col, Color color)
	{
		if (isTileInBound(row, col))
			return (tile[row][col].getOccupied() == color);
		else
			return false;
		
	}
	
	public int computeFlyRiskToBeCaptured(CheckerPiece piece, int dstRow, int dstCol, int rowDir, int colDir)
	{		
		int capture = 3;
		int noCapture = 1;
		
		int row = dstRow+rowDir;
		int col = dstCol+colDir;
		
		if (isTileOccupiedByPlayer(row, col, Checker.getOpponentPlayer()))
		{
			CheckerPiece opponent = tile[row][col].getPiece();
			if (opponent.getCrown() || 
			   (!opponent.getCrown() && (((opponent.getColor() == Color.ORANGE) && (dstRow < row)) ||
										 ((opponent.getColor() == Color.WHITE)  && (dstRow > row)))))
			{
				System.out.printf("computeFlyRiskToBeCaptured: piece (%d,%d) fly to (%d,%d) will be captured by opponent(%d,%d)\n", piece.getRow(), piece.getCol(), dstRow,dstCol, row, col);
				return capture;
			}
		}
	
		// next tile in opposite direction of rowDir,colDir
		int row2 = row;
		int col2 = col + colDir*(-2);
		int row3 = row + rowDir*(-2);
		int col3 = col;
		
		if (isTileFree(row3, col3) && isTileOccupiedByPlayer(row2, col2, Checker.getOpponentPlayer()))
		{
			CheckerPiece opponent = tile[row2][col2].getPiece();
			if (opponent.getCrown() || 
			   (!opponent.getCrown() && (((opponent.getColor() == Color.ORANGE) && (row3 < row2)) ||
										 ((opponent.getColor() == Color.WHITE)  && (row3 > row2)))))
			{
				System.out.printf("computeFlyRiskToBeCaptured: piece (%d,%d) fly to (%d,%d) will be captured by opponent(%d,%d)\n", piece.getRow(), piece.getCol(), dstRow,dstCol, row2, col2);
				return capture;
			}
		}
		
		if (isTileFree(row2, col2) && isTileOccupiedByPlayer(row3, col3, Checker.getOpponentPlayer()))
		{
			CheckerPiece opponent = tile[row3][col3].getPiece();
			if (opponent.getCrown() || 
			   (!opponent.getCrown() && (((opponent.getColor() == Color.ORANGE) && (row2 < row3)) ||
										 ((opponent.getColor() == Color.WHITE)  && (row2 > row3)))))
			{
				System.out.printf("computeFlyRiskToBeCaptured: piece (%d,%d) fly to (%d,%d) will be captured by opponent(%d,%d)\n", piece.getRow(), piece.getCol(), dstRow,dstCol, row3, col3);
				return capture;
			}
		}
		
		System.out.printf("computeFlyRiskToBeCaptured: piece (%d,%d) fly to (%d,%d), direction (%d,%d): no capture\n", piece.getRow(), piece.getCol(), dstRow,dstCol, rowDir, colDir);
		
		// get here mean no being captured risk
		return noCapture;
	}
	
	public int computeRiskToBeCaptured(CheckerPiece piece, int dstRow, int dstCol)
	{
// reminder: need to implement king moveback and can capture by orange piece		
		int capture = 2;
		int noCapture = 1;
		
		int rowDir = dstRow - piece.getRow();
		int colDir = dstCol - piece.getCol();
		
		// next tile in direction of rowDir,colDir
		int row = dstRow + rowDir;
		int col = dstCol + colDir;
	
		if (isTileOccupiedByPlayer(row, col, Checker.getOpponentPlayer()))
		{
			return capture;
		}
	
		// next tile in opposite direction of rowDir,colDir
		int row2 = row;
		int col2 = col + colDir*(-2);
		int row3 = row + rowDir*(-2);
		int col3 = col;
		
		if (isTileFree(row3, col3) && isTileOccupiedByPlayer(row2, col2, Checker.getOpponentPlayer()))
		{
			return capture;
		}
		
		// get here mean no being captured risk
		return noCapture;
	}

	public boolean isKingFoundInDirection(int dstRow, int dstCol, int rowDir, int colDir, Color color)
	{		
		int row = dstRow + rowDir;
		int col = dstCol + colDir;
		
		while (isTileInBound(row, col))
		{
			if (isTileOccupiedByPlayer(row, col, color))
			{
				return false;
			}
			
			// next tile is occupied by opponent
			if (!isTileFree(row,col))
			{
				CheckerPiece opponent = tile[row][col].getPiece();
				
				if (opponent.getCrown())
					return true;
				else
					return false;
			}
			// next tile is free
			else
			{
				// next tile in direction of rowDir,colDir
				row += rowDir;
				col += colDir;
			}
		}
		return false;
	}
	
	public int computeRiskToBeFlyCaptured(CheckerPiece piece, int dstRow, int dstCol)
	{
		int capture = 3;
		int noCapture = 1;
		
		int rowDir = dstRow - piece.getRow();
		int colDir = dstCol - piece.getCol();
		
		// note: after the move, the piece location will be free, no need to check for tile free at piece location
		if (isKingFoundInDirection(dstRow, dstCol, rowDir, colDir, piece.getColor()))
		{
			System.out.printf("computeRiskToBeFlyCaptured: if piece (%d,%d) moves to (%d,%d), can be captured by a king in (%d,%d) direction\n", piece.getRow(), piece.getCol(), dstRow,dstCol,rowDir, colDir);
			return capture;
		}
	
		// to check if there is opponent's king is the opposite move direction, start at piece location (instead of dstRow, dstCol) before the move. 
		// note: before the move, the piece location is occupied
		if (isTileFree(dstRow+rowDir, dstCol+colDir) && isKingFoundInDirection(piece.getRow(), piece.getCol(), rowDir*(-1), colDir*(-1), piece.getColor()))
		{
			System.out.printf("computeRiskToBeFlyCaptured: if piece (%d,%d) moves to (%d,%d), can be captured by a king in (%d,%d) direction\n", piece.getRow(), piece.getCol(), dstRow,dstCol,rowDir*(-1), colDir*(-1));
			return capture;
		}
		
		// check if there is opponent's king in the perpendicular direction
		if (isTileFree(dstRow+rowDir, dstCol+colDir*(-1)) && isKingFoundInDirection(dstRow, dstCol, rowDir*(-1), colDir, piece.getColor()))
		{
			System.out.printf("computeRiskToBeFlyCaptured: if piece (%d,%d) moves to (%d,%d), can be captured by a king in (%d,%d) direction\n", piece.getRow(), piece.getCol(), dstRow,dstCol,rowDir*(-1), colDir);
			return capture;
		}

		// check if there is opponent's king in the perpendicular direction
		if (isTileFree(dstRow+rowDir*(-1), dstCol+colDir) && isKingFoundInDirection(dstRow, dstCol, rowDir, colDir*(-1), piece.getColor()))
		{
			System.out.printf("computeRiskToBeFlyCaptured: if piece (%d,%d) moves to (%d,%d), can be captured by a king in (%d,%d) direction\n", piece.getRow(), piece.getCol(), dstRow,dstCol,rowDir, colDir*(-1));
			return capture;
		}

		return noCapture;
	}
	

/*	public boolean flyCaptureValid(int srcRow, int srcCol, int rowDir, int colDir)
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
	*/
	


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
