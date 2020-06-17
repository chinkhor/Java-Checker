import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CheckerPlayer implements ActionListener {

	private Color playerColor;
	private CheckerBoard board;
	// player array list, store all its pieces
	private ArrayList<CheckerPiece> pieces;
	
	// number of rows for each player = half of number of tiles per row minus one
	private final int PLAY_ROWS = CheckerBoard.TILES/2 - 1;
	
	// use Color.Black as indicator as the tile is free (unoccupied)
	private final Color TILE_FREE = Color.black;
	
	private int state = 0;
	private final static int STATE_FREE = 0;
	private final static int STATE_SELECTED = 1;
	private final static int STATE_JUMPED = 2;
	// state = 0: free, possible next action: select
	// state = 1: selected, possible next action: re-select, move and jump
	// state = 2: jumped, possible next action: jump again (double jump or more)
	
	public CheckerPlayer(Color color, CheckerBoard board)
	{
		int startRow, endRow;
		
		this.playerColor = color;
		this.board = board;
		this.pieces = new ArrayList<CheckerPiece>();
		this.state = 0;
	
		// player ORANGE is at bottom of board
		if (this.playerColor == Color.ORANGE)
		{
			startRow = CheckerBoard.TILES - PLAY_ROWS;
			endRow = CheckerBoard.TILES;
		}
		// player WHITE is at top of board
		else
		{
			startRow = 0;
			endRow = PLAY_ROWS;
		}
		for (int row = startRow; row < endRow; row++)
		{
			// starting col is at 0 or 1 based on row number
			for (int col = row %2; col < CheckerBoard.TILES; col+=2)
			{
				// create piece and add to the board
				CheckerPiece piece = new CheckerPiece(row, col, playerColor);
				board.addPiece(piece, row, col);
				
				// add piece to player array list
				pieces.add(piece);
				
				// action action listener for button click
				piece.addActionListener(this);
			}
		}
		showPlayerPieceList();
	}
	
	public void showPlayerPieceList()
	{
		if (playerColor == Color.ORANGE)
			System.out.println("Player ORANGE: ");
		else
			System.out.println("Player WHITE: ");
		
		for (CheckerPiece piece : pieces)
		{
			System.out.print(piece.getLabel() + " ");
		}
		System.out.println("");
	}
	
	public void actionPerformed(ActionEvent e)
	{
		CheckerPiece piece = (CheckerPiece) e.getSource();
		
		System.out.println(piece.getLabel() + " is clicked");
		
		// check if current player's piece is in action
		if (Checker.getCurrentPlayer() == piece.getColor())
		{
			CheckerPlayer player = Checker.getPlayer(Checker.getCurrentPlayer());
			player.srcActionNotify(piece);
		}
	}
	
	public void setState (int state)
	{
		this.state = state;
	}
	
	public int getState()
	{
		return this.state;
	}
	
	public void srcActionNotify(CheckerPiece piece)
	{
		// state = 0: free state, possible next action: piece selection
		// state = 1: a piece is selected, possible next action: re-select, move and jump
		// state = 2: a piece is jumped, possible next action: jump again (double jump or more)
		
		int state = getState();
					
		if ((state == STATE_FREE) || (state == STATE_SELECTED))
		{
			setState(STATE_SELECTED); // select or re-select sets this state
						
			// de-select any piece formerly selected
			// if there is one, it is always located at head of the player piece array list (index 0)
			CheckerPiece p = pieces.get(0);
			if (p.isSelect())
				p.select(false);
						
			// select the newly selected piece
			piece.select(true);
						
			// and move the selected piece to the head of the player piece array list  
			pieces.remove(piece);
			pieces.add(0, piece); 
		}			
	}
	
	public void dstActionNotify(CheckerTile tile)
	{
		int state = getState();
		
		if (state == STATE_FREE)
			return;
		
		
		// de-select any piece formerly selected
		// if there is one, it is always located at head of the player piece array list (index 0)
		CheckerPiece piece = pieces.get(0);
					
		int srcRow = piece.getRow();
		int srcCol = piece.getCol();
		int dstRow = tile.getRow();
		int dstCol = tile.getCol();
		int rowDiff = Math.abs(srcRow-dstRow);
		int colDiff = Math.abs(srcCol-dstCol);
		
		if (state == STATE_SELECTED)
		{
			if ((rowDiff == 1) && (colDiff == 1))
				move(piece, dstRow, dstCol);
			else if ((rowDiff == 2) && (colDiff == 2))
				jump(piece, dstRow, dstCol);
			else
				System.out.println("Invalid action for " + piece.getLabel());
		}	
		// check for double/continuous jump possibility 
		else if (state == STATE_JUMPED)
		{
			if ((rowDiff == 2) && (colDiff == 2))
				jump(piece, dstRow, dstCol);	
		}
	}
	
	public void actionComplete()
	{
		setState(STATE_FREE); // reset to free state
		Checker.turnOver();
	}
	
	public void move(CheckerPiece piece, int row, int col)
	{
		if (((piece.getColor() == Color.ORANGE) && (row < piece.getRow())) ||
			((piece.getColor() == Color.WHITE) && (row > piece.getRow())))
		{	
			// move piece in the board
			CheckerBoard board = Checker.getBoard();
			board.removePiece(piece, piece.getRow(), piece.getCol());
			board.addPiece(piece, row, col);	
			
			// set piece attribute for new location
			piece.select(false);
			piece.setRow(row);
			piece.setCol(col);
			
			actionComplete();
		}
		else
		{
			System.out.println("Piece " + piece.getLabel() + " cannot move backward");
		}
	}

	public void capture(int row, int col)
	{
		CheckerBoard board = Checker.getBoard();
		CheckerPlayer player = Checker.getPlayer(Checker.getOpponentPlayer());
		CheckerPiece capturePiece = null;
		
		for (CheckerPiece piece : player.pieces)
		{
			if ((piece.getRow()==row) && (piece.getCol()==col))
			{
				capturePiece = piece;
				break;
			}
		}
		
		if (capturePiece != null)
		{	
			// remove piece from the board
			board.removePiece(capturePiece, row, col);
			
			// remove piece from Opponent Player piece array list
			player.pieces.remove(capturePiece);
		}
		else
			System.out.println("Piece (" + row + "," + col + ") does not exist, cannot capture");
	}
	
	public boolean jumpValid(int pieceRow, int pieceCol, int rowDir, int colDir)
	{
		int row = pieceRow + rowDir;
		int col = pieceCol + colDir;
		int row2 = row + rowDir;
		int col2 = col + colDir;
		
		CheckerBoard board = Checker.getBoard();
		CheckerTile[][] tile = board.getTileArray();
		
		// check out of boundary
		if ((row < 0 || row >= CheckerBoard.TILES || col < 0 || col >= CheckerBoard.TILES) ||
			(row2 < 0 || row2 >= CheckerBoard.TILES || col2 < 0 || col2 >= CheckerBoard.TILES))
			return false;
		
		if ((tile[row][col].getOccupied() == Checker.getOpponentPlayer()) && 
			(tile[row2][col2].getOccupied() == TILE_FREE))
			return true;
		else
			return false;
		
	}
	
	public boolean canDoubleJump(CheckerPiece piece, int row, int col)
	{
		if (((piece.getColor() == Color.ORANGE) &&
			(jumpValid(row, col, -1, 1) || jumpValid(row, col, -1, -1))) ||
			((piece.getColor() == Color.WHITE) &&
			(jumpValid(row, col, 1, 1) || jumpValid(row, col, 1, -1)))) 	
		{
			return true;
		}
		else
			return false;
	}
	
	public void jump(CheckerPiece piece, int row, int col)
	{
		if (((piece.getColor() == Color.ORANGE) && (row < piece.getRow())) ||
			((piece.getColor() == Color.WHITE) && (row > piece.getRow())))
		{	
			int srcRow = piece.getRow();
			int srcCol = piece.getCol();
			int midRow = srcRow+(row-srcRow)/2;
			int midCol = srcCol+(col-srcCol)/2;
			
			CheckerBoard board = Checker.getBoard();
			CheckerTile[][] tile = board.getTileArray();
			
			if (tile[midRow][midCol].getOccupied() == Checker.getOpponentPlayer())
			{
				// move piece in the board
				board.removePiece(piece, piece.getRow(), piece.getCol());
				board.addPiece(piece, row, col);	
				
				// set piece attribute for new location
				piece.select(false);
				piece.setRow(row);
				piece.setCol(col);
				
				// remove opponent piece
				capture(midRow, midCol);
			
				if (canDoubleJump(piece, row, col))
				{
					piece.select(true);
					setState(STATE_JUMPED);
				}
				else 
					actionComplete();
			}
		}
		else
		{
			System.out.println("Piece " + piece.getLabel() + " cannot jump backward");
		}
	}
}

