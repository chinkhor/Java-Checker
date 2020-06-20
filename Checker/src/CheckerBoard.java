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
	
	public void computeRiskToBeCaptured(CheckerPiece piece, int row, int col, int rowDir, int colDir)
	{
		// compute risk of the move
		int row2 = row + rowDir; 
		int col2 = col + colDir;
		// check out of boundary and if the move is toward opponent piece in the same direction
		if (!(row2 < 0 || row2 >= TILES || col2 < 0 || col2 >= TILES) &&
			(tile[row2][col2].getOccupied() == Checker.getOpponentPlayer()))
		{
			CheckerPiece opponent = tile[row2][col2].getPiece();
			if (opponent.getCrown() == false)
			{
				piece.setMoveRisk(2);
				System.out.printf("computeRiskToBeCaptured: if piece (%d,%d) moves to (%d,%d), can be captured by piece (%d,%d), moveRisk %d\n", piece.getRow(), piece.getCol(), row,col,row2,col2,piece.getMoveRisk());
			}
			else
			{
				piece.setMoveRisk(3); // set 3 because the move will trigger capture by opponent king
				System.out.printf("computeRiskToBeCaptured: if piece (%d,%d) moves to (%d,%d), can be captured by king (%d,%d), moveRisk %d\n", piece.getRow(), piece.getCol(), row,col,row2,col2,piece.getMoveRisk());
			}
		}
		else
			System.out.printf("computeRiskToBeCaptured: if piece (%d,%d) moves to (%d,%d), no risk in this direction %d, %d\n", piece.getRow(), piece.getCol(), row,col, rowDir, colDir);
					
		int row3 = row2;
		int col3 = col + colDir*(-1);
		int row4 = row + rowDir*(-1);
		int col4 = col2;
		// check out of boundary and if the move is toward opponent piece in the opposite direction
		if (!(row3 < 0 || row3 >= TILES || col3 < 0 || col3 >= TILES) &&
			!(row4 < 0 || row4 >= TILES || col4 < 0 || col4 >= TILES))
		{
			if ((tile[row3][col3].getOccupied() == Checker.getOpponentPlayer()) &&
				(tile[row4][col4].getOccupied() == TILE_FREE))
			{
				CheckerPiece opponent = tile[row3][col3].getPiece();
				if (opponent.getCrown() == false)
				{
					if (piece.getMoveRisk() < 2)
						piece.setMoveRisk(2);
					System.out.printf("computeRiskToBeCaptured: if piece (%d,%d) moves to (%d,%d), can be captured by piece (%d,%d), moveRisk %d\n", piece.getRow(), piece.getCol(), row,col,row3,col3,piece.getMoveRisk());
				}
				else
				{
					piece.setMoveRisk(3); // set 3 because the move will trigger capture by opponent king
					System.out.printf("computeRiskToBeCaptured: if piece (%d,%d) moves to (%d,%d), can be captured by king (%d,%d), moveRisk %d\n", piece.getRow(), piece.getCol(), row,col,row3,col3,piece.getMoveRisk());

				}
			}
			else
				System.out.printf("computeRiskToBeCaptured: if piece (%d,%d) moves to (%d,%d), no risk in this direction %d, %d\n", piece.getRow(), piece.getCol(), row,col, rowDir, colDir*(-1));

		}		
		else
			System.out.printf("computeRiskToBeCaptured: if piece (%d,%d) moves to (%d,%d), no risk in this direction %d, %d\n", piece.getRow(), piece.getCol(), row,col, rowDir, colDir*(-1));

		return;
	}
	
	
	public boolean kingFound(CheckerPiece piece, int row, int col, int rowDir, int colDir)
	{
		int row2 = row + rowDir; 
		int col2 = col + colDir;
		
		while (true)
		{
			if ((row2 < 0 || row2 >= TILES || col2 < 0 || col2 >= TILES) ||
				(tile[row2][col2].getOccupied() == piece.getColor()))
				return false;
	
			else if (tile[row2][col2].getOccupied() != TILE_FREE)
			{
				break;
			}
			row2 = row2 + rowDir;
			col2 = col2 + colDir;			
		}
		
		CheckerPiece opponent = tile[row2][col2].getPiece();
		if (opponent.getCrown())
			return true;
		else
			return false;
		
	}
	
	public void computeRiskToBeFlyCaptured(CheckerPiece piece, int row, int col, int rowDir, int colDir)
	{
		
		if (kingFound(piece, row, col, rowDir, colDir))
		{
			piece.setMoveRisk(3); // set 3 because the move will trigger capture by opponent king
			System.out.printf("computeRiskToBeFlyCaptured: if piece moves to (%d,%d), can be captured by a king in (%d,%d) direction, moveRisk %d\n", row,col,rowDir, colDir, piece.getMoveRisk());

		}
		
		if (kingFound(piece, row, col, rowDir, colDir*(-1)) && (tile[row+rowDir*(-1)][col + colDir].getOccupied() == TILE_FREE))
		{
			piece.setMoveRisk(3); // set 3 because the move will trigger capture by opponent king
			System.out.printf("computeRiskToBeFlyCaptured: if piece moves to (%d,%d), can be captured by a king in (%d,%d) direction, moveRisk %d\n", row,col,rowDir, colDir*(-1), piece.getMoveRisk());
		}
		
		if (kingFound(piece, row, col, rowDir*(-1), colDir) && (tile[row+rowDir][col + colDir*(-1)].getOccupied() == TILE_FREE))
		{
			piece.setMoveRisk(3); // set 3 because the move will trigger capture by opponent king
			System.out.printf("computeRiskToBeFlyCaptured: if piece moves to (%d,%d), can be captured by a king in (%d,%d) direction, moveRisk %d\n", row,col,rowDir*(-1), colDir, piece.getMoveRisk());
		}	
		
		if (kingFound(piece, row, col, rowDir*(-1), colDir*(-1)) && (tile[row+rowDir][col + colDir].getOccupied() == TILE_FREE))
		{
			piece.setMoveRisk(3); // set 3 because the move will trigger capture by opponent king
			System.out.printf("computeRiskToBeFlyCaptured: if piece moves to (%d,%d), can be captured by a king in (%d,%d) direction, moveRisk %d\n", row,col,rowDir*(-1), colDir*(-1), piece.getMoveRisk());
		}	
		return;
	}
	
	public boolean moveValid(CheckerPiece piece, int pieceRow, int pieceCol, int rowDir, int colDir)
	{
		int row = pieceRow + rowDir;
		int col = pieceCol + colDir;
		
		System.out.printf("moveValid: piece (%d,%d) to move to (%d,%d) in direction %d,%d \n", piece.getRow(), piece.getCol(), row, col, rowDir, colDir);
		// check out of boundary
		if (row < 0 || row >= TILES || col < 0 || col >= TILES)
			return false;
		
		// if next diagonal tile is unoccupied
		if (tile[row][col].getOccupied() == TILE_FREE)
		{
			// if the move promotes piece to king, make it the highest priority  (moveRisk = -1);
			CheckerPlayer player = Checker.getPlayer(Checker.getCurrentPlayer());
			if (row == player.getKingRow())
			{
				System.out.printf("Piece (%d,%d) can move to (%d,%d) and become king (king row %d)\n", piece.getRow(), piece.getCol(), row, col, player.getKingRow());
				piece.setMoveRisk(0);
			}
			else
			{
				// if the move will be captured by opponent piece, set moveRisk = 1
				computeRiskToBeCaptured(piece, row, col, rowDir, colDir);
			
				// if the move will be captured by opponent king, set moveRisk = 2
				computeRiskToBeFlyCaptured(piece, row, col, rowDir, colDir);
			
				if (piece.getMoveRisk() == -1) 
					piece.setMoveRisk(1); // safe move
			}
			
			return true;
		}
		else
			return false;
		
	}
	public boolean canMove(Color pieceColor, int row, int col)
	{
		boolean status = false;
		
		// check for move possibility
		if (pieceColor == Color.ORANGE)
		{
			status = moveValid(row, col, -1, 1);
			status = status || moveValid(row, col, -1, -1);
		}
		else
		{
			status = moveValid(row, col, 1, 1);
			status = status || moveValid(row, col, 1, -1);
		}
		
		return status;
	}
	
	public boolean canMove(CheckerPiece piece, Color pieceColor, int row, int col)
	{
		boolean status1 = false; 
		boolean status2 = false;
		int risk = 5;
		
		// force to check all possible moves (to compute the move risk)
		if (pieceColor == Color.ORANGE)
		{
			status1 = moveValid(piece, row, col, -1, 1);
			status2 = moveValid(piece, row, col, -1, -1);
		}
		else
		{
			status1 = moveValid(piece, row, col, 1, 1);
			if (status1)
			{
				risk = piece.getMoveRisk();
				piece.setTgtRow(row+1);
				piece.setTgtCol(col+1);
				piece.setMoveRisk(-1);
			}

			status2 = moveValid(piece, row, col, 1, -1);
			if (status2)
			{
				if (piece.getMoveRisk() < risk)
				{
					piece.setTgtRow(row+1);
					piece.setTgtCol(col-1);
				}
				else
					piece.setMoveRisk(risk);
			}
			else
			{
				if (status1)
					piece.setMoveRisk(risk);
			}
		}
		
		return status1 || status2;
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
	
	public boolean canFly(CheckerPiece piece, Color pieceColor, int row, int col)
	{
		boolean status = false;
		
	    // force to check all possible moves (to compute the move value)
		status = moveValid(piece, row, col, -1, 1);
		status = status || moveValid(piece, row, col, -1, -1);
		status = status || moveValid(piece, row, col, 1, 1);
		status = status || moveValid(piece, row, col, 1, -1);
		
		return status;
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
