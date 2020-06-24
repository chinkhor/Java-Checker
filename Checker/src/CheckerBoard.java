import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
/**
 * @author      Chin Kooi Khor (chin.kooi.khor@gmail.com)
 * @version     1.0   
 * @since       24 Jun 2020  
 */
public class CheckerBoard extends JPanel implements MouseListener
{
	/**
	 * Constant value for number of tiles at each side of the checker board
	 */
	public final static int TILES = 8;
	
	/**
	 * Constant value of color to indicate un-occupied tile
	 */
	public final static Color TILE_FREE = Color.black;
		
	/**
	 * Two dimensional arrays for checker tiles
	 */
	private CheckerTile tile[][] = new CheckerTile[TILES][TILES];
	
	/**
	 * Constructor of CheckerBoard class
	 * <p>                           
	 * This constructor will construct a nxn checker board (n is specified by TILES). The tiles are red and black in alternating order. 
	 * Black tiles are playing tiles where the pieces can occupy. Each playing tile is added a mouse click listener. When mouse click occurs
	 * on the playing tiles, an event will be invoked. 
	 * 
	 */
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
	
	/**
	 * Getter to get two dimensional arrays for tiles 
	 * 
	 * @return CheckerTile[][]	Return the two dimensional arrays for tiles  
	 */
	public CheckerTile[][] getTileArray()
	{
		return tile;
	}
	
	/**
	 * Add a piece to the tile of the board 
	 * 
	 * @param piece Piece to add to the tile
	 * @param row Row number of tile on the board for addition
	 * @param col Column number of tile on the board for addition
	 */
	public void addPiece(CheckerPiece piece, int row, int col)
	{		
		tile[row][col].add(piece, BorderLayout.CENTER); // add piece (JButton) to tile (JPanel)
		tile[row][col].setOccupied(piece.getColor()); // set the tile occupied
		
		// repaint after the addition
		tile[row][col].revalidate();
		tile[row][col].repaint();
		piece.repaint();
	}
	
	/**
	 * Remove a piece from the tile of the board 
	 * 
	 * @param piece Piece to remove from the tile
	 * @param row Row number of tile on the board for removal
	 * @param col Column number of tile on the board for removal
	 */
	public void removePiece(CheckerPiece piece, int row, int col)
	{		
		tile[row][col].remove(piece); // remove piece (JButton) from tile (JPanel)
		tile[row][col].setOccupied(TILE_FREE); // set the tile un-occupied
		
		// repaint after the removal
		tile[row][col].revalidate();
		tile[row][col].repaint();
	}
	
	/**
	 * Check if the given tile specified by row and column is within the boundary of the checker board 
	 * 
	 * @param row Row number of tile
	 * @param col Column number of tile
	 * @return boolean Return true if within boundary, else return false
	 */
	public boolean isTileInBound(int row, int col)
	{
		return ((row >= 0 && row < TILES && col >= 0 && col < TILES));
	}

	/**
	 * Check if the given tile specified by row and column is un-occupied 
	 * 
	 * @param row Row number of tile
	 * @param col Column number of tile
	 * @return boolean Return true if un-occupied, else return false
	 */
	public boolean isTileFree(int row, int col)
	{
		if (isTileInBound(row, col))
			return (tile[row][col].getOccupied() == TILE_FREE);
		else
			return false;
	}
	
	/**
	 * Check if the given tile specified by row and column is occupied by piece with color
	 * 
	 * @param row Row number of tile
	 * @param col Column number of tile
	 * @param color Color of the piece
	 * @return boolean Return true if occupied by piece matches the color, else return false
	 */	
	public boolean isTileOccupiedByPlayer(int row, int col, Color color)
	{
		if (isTileInBound(row, col))
			return (tile[row][col].getOccupied() == color);
		else
			return false;
		
	}
	
	/**
	 * Compute the risk of being captured by opponent piece if making a given action
	 * 
	 * @param piece Instance of the piece for action
	 * @param dstRow Destined tile row number for the piece after the action
	 * @param dstCol Destined tile column number for the piece after the action
	 * @return int Return computed risk (CAPTURE_BY_PIECE or NO_CAPTURE)
	 */	
	public int computeRiskToBeCaptured(CheckerPiece piece, int dstRow, int dstCol)
	{		
		int capture = CheckerPiece.CAPTURE_BY_PIECE;
		int noCapture = CheckerPiece.NO_CAPTURE;
		
		int srcRow = piece.getRow();
		int srcCol = piece.getCol();
		int rowDir = (dstRow - srcRow)/Math.abs(dstRow - srcRow);
		int colDir = (dstCol - srcCol)/Math.abs(dstCol - srcCol);
		
		// next tile in direction of rowDir,colDir
		int row = dstRow + rowDir;
		int col = dstCol + colDir;
	
		if (isTileOccupiedByPlayer(row, col, Checker.getOpponentPlayer()))
		{
			CheckerPiece opponent = tile[row][col].getPiece();
			if (opponent.getCrown() ||  // if king, should not get here, but check anyway
			   (!opponent.getCrown() && (((opponent.getColor() == Color.ORANGE) && (dstRow < row)) || ((opponent.getColor() == Color.WHITE)  && (dstRow > row)))))
			{
				System.out.printf(piece.colorCode + "computeRiskToBeCaptured: piece (%d,%d) gets to (%d,%d) will be captured by opponent(%d,%d)\n", piece.getRow(), piece.getCol(), dstRow,dstCol, row, col);
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
			if (opponent.getCrown() || // if king, should not get here, but check anyway
			   (!opponent.getCrown() && (((opponent.getColor() == Color.ORANGE) && (row3 < row2)) || ((opponent.getColor() == Color.WHITE)  && (row3 > row2)))))
			{
				System.out.printf(piece.colorCode + "computeRiskToBeCaptured: piece (%d,%d) gets to (%d,%d) will be captured by opponent(%d,%d)\n", piece.getRow(), piece.getCol(), dstRow,dstCol, row2, col2);
				return capture;
			}
		}

		if (isTileFree(row2, col2) && isTileOccupiedByPlayer(row3, col3, Checker.getOpponentPlayer()))
		{
			CheckerPiece opponent = tile[row3][col3].getPiece();
			if (opponent.getCrown() || // if king, should not get here, but check anyway
			   (!opponent.getCrown() && (((opponent.getColor() == Color.ORANGE) && (row2 < row3)) || ((opponent.getColor() == Color.WHITE)  && (row2 > row3)))))
			{
				System.out.printf(piece.colorCode + "computeRiskToBeCaptured: piece (%d,%d) gets to (%d,%d) will be captured by opponent(%d,%d)\n", piece.getRow(), piece.getCol(), dstRow,dstCol, row3, col3);
				return capture;
			}
		}
		
		System.out.printf(piece.colorCode + "computeRiskToBeCaptured: piece (%d,%d) gets to (%d,%d), direction (%d,%d): no capture\n", piece.getRow(), piece.getCol(), dstRow,dstCol, rowDir, colDir);	
		// get here mean no being captured risk
		return noCapture;
	}

	/**
	 * Check if there is opponent king in the given direction that can fly and capture after making the play action
	 * 
	 * @param dstRow Destined tile row number for the piece after the action
	 * @param dstCol Destined tile column number for the piece after the action
	 * @param rowDir Row direction (+1 is down, -1 is up) 
	 * @param colDir Column direction (+1 is right, -1 is left)
	 * @param color Color of the piece
	 * @return boolean Return true if there is opponent king, else return false
	 */	
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
	
	/**
	 * Compute the risk of being captured by opponent king if making a given action
	 * 
	 * @param piece Instance of the piece for action
	 * @param dstRow Destined tile row number for the piece after the action
	 * @param dstCol Destined tile column number for the piece after the action
	 * @return int Return computed risk (CAPTURE_BY_KING or NO_CAPTURE)
	 */	
	public int computeRiskToBeFlyCaptured(CheckerPiece piece, int dstRow, int dstCol)
	{
		int capture = CheckerPiece.CAPTURE_BY_KING;
		int noCapture = CheckerPiece.NO_CAPTURE;
		
		int srcRow = piece.getRow();
		int srcCol = piece.getCol();
		int rowDir = (dstRow - srcRow)/Math.abs(dstRow - srcRow);
		int colDir = (dstCol - srcCol)/Math.abs(dstCol - srcCol);
		
		// note: after the move, the piece location will be free, no need to check for tile free at piece location
		if (isKingFoundInDirection(dstRow, dstCol, rowDir, colDir, piece.getColor()))
		{
			System.out.printf(piece.colorCode + "computeRiskToBeFlyCaptured: if piece (%d,%d) moves to (%d,%d), can be captured by a king in (%d,%d) direction\n", piece.getRow(), piece.getCol(), dstRow,dstCol,rowDir, colDir);
			return capture;
		}
	
		// to check if there is opponent's king is the opposite move direction, start at piece location (instead of dstRow, dstCol) before the move. 
		// note: before the move, the piece location is occupied
		if (isTileFree(dstRow+rowDir, dstCol+colDir) && isKingFoundInDirection(piece.getRow(), piece.getCol(), rowDir*(-1), colDir*(-1), piece.getColor()))
		{
			System.out.printf(piece.colorCode + "computeRiskToBeFlyCaptured: if piece (%d,%d) moves to (%d,%d), can be captured by a king in (%d,%d) direction\n", piece.getRow(), piece.getCol(), dstRow,dstCol,rowDir*(-1), colDir*(-1));
			return capture;
		}
		
		// check if there is opponent's king in the perpendicular direction
		if (isTileFree(dstRow+rowDir, dstCol+colDir*(-1)) && isKingFoundInDirection(dstRow, dstCol, rowDir*(-1), colDir, piece.getColor()))
		{
			System.out.printf(piece.colorCode + "computeRiskToBeFlyCaptured: if piece (%d,%d) moves to (%d,%d), can be captured by a king in (%d,%d) direction\n", piece.getRow(), piece.getCol(), dstRow,dstCol,rowDir*(-1), colDir);
			return capture;
		}

		// check if there is opponent's king in the perpendicular direction
		if (isTileFree(dstRow+rowDir*(-1), dstCol+colDir) && isKingFoundInDirection(dstRow, dstCol, rowDir, colDir*(-1), piece.getColor()))
		{
			System.out.printf(piece.colorCode + "computeRiskToBeFlyCaptured: if piece (%d,%d) moves to (%d,%d), can be captured by a king in (%d,%d) direction\n", piece.getRow(), piece.getCol(), dstRow,dstCol,rowDir, colDir*(-1));
			return capture;
		}

		return noCapture;
	}

	/**
	 * Compute the risk of play action. 
	 * 
	 * @param piece Instance of the piece for action
	 * @param dstRow Destined tile row number for the piece after the action
	 * @param dstCol Destined tile column number for the piece after the action
	 * @return int Return computed risk (CAPTURE_BY_KING, CAPTURE_BY_PIECE, NO_CAPTURE, BE_KING)
	 */	
	public int computeActionRisk(CheckerPiece piece, int dstRow, int dstCol)
	{
		// if the move to king row, no risk to be captured, and it should be priority move to become king
		if (!piece.getCrown() && (dstRow == Checker.getPlayer(Checker.getCurrentPlayer()).getKingRow()))
		{
			System.out.printf (piece.colorCode + "computeActionRisk: piece %s become king if gets to (%d,%d), set risk to 0\n", piece.getLabel(), dstRow, dstCol);
			return CheckerPiece.BE_KING;
		}
		else
		{
			int risk = computeRiskToBeFlyCaptured(piece, dstRow, dstCol);
			if (risk < CheckerPiece.CAPTURE_BY_KING) // no capture by a king
			{
				risk = computeRiskToBeCaptured(piece, dstRow, dstCol);
			}
			System.out.printf (piece.colorCode + "computeActionRisk: piece %s if gets to (%d,%d), risk is %d\n", piece.getLabel(), dstRow, dstCol, risk);
			return risk;
		}	

	}
	
	/**
	 * Mouse click event invoked when a tile is being clicked on. 
	 * 
	 * @param e MouseEvent which can get the selected tile via getSource method
	 */	
	public void mouseClicked(MouseEvent e) 
	{ 
		CheckerTile tile = (CheckerTile) e.getSource();
		CheckerPlayer player = Checker.getPlayer(Checker.getCurrentPlayer());
		System.out.printf (player.colorCode + "Tile[%d][%d] is clicked\n", tile.getRow(), tile.getCol());
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
