import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;

public class CheckerPlayer implements ActionListener {

	protected Color playerColor;
	// player array list, store all its pieces
	protected ArrayList<CheckerPiece> pieces; 
	
	// player array list, store the pieces that have capture in next action
	protected ArrayList<CheckerPiece> preSelectList;
	
	// number of rows for each player = half of number of tiles per row minus one
	protected final int PLAY_ROWS = CheckerBoard.TILES/2 - 1;
	
	protected int state = 0;
	protected final static int STATE_FREE = 0;
	protected final static int STATE_SELECTED = 1;
	protected final static int STATE_JUMPED = 2;
	protected final static int STATE_FLIED = 3;
	// state = 0: free, possible next action: select
	// state = 1: selected, possible next action: re-select, move and jump
	// state = 2: jumped, possible next action: jump again (double jump or more)
	// state = 3: flied, possible next action: fly again (double fly or more)
	
	protected Timer timer;
	protected CheckerTimerTask task;
	
	protected int kingRow;
	
	public CheckerPlayer(Color color, CheckerBoard board)
	{
		int startRow, endRow;
		
		this.playerColor = color;
		this.pieces = new ArrayList<CheckerPiece>();
		this.preSelectList = new ArrayList<CheckerPiece>();
		this.state = 0;
	
		// player ORANGE is at bottom of board
		if (this.playerColor == Color.ORANGE)
		{
			startRow = CheckerBoard.TILES - PLAY_ROWS;
			endRow = CheckerBoard.TILES;
			kingRow = 0;
		}
		// player WHITE is at top of board
		else
		{
			startRow = 0;
			endRow = PLAY_ROWS;
			kingRow = CheckerBoard.TILES - 1;
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
		
		// start timer
		timer = new Timer();
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
			if (preSelectList.isEmpty())
			{
				srcActionNotify(piece);
			}
			else
			{
				// if preSelectList is not empty, next selected piece must be one of preSelectList
				for (CheckerPiece p : preSelectList)
				{
					if (p.getLabel().equals(piece.getLabel()))
					{
						p.setPreSelect(true);
						srcActionNotify(piece);
						//break; do not break here to allow the loop to walk thru preSelectList to clear preSelect flag for non-selected piece 
					}
					else
					{
						p.setPreSelect(false);
					}
				}
			}
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
	
	public ArrayList<CheckerPiece> getPieceArrayList()
	{
		return pieces;
	}
	
	public ArrayList<CheckerPiece> getPreSelectArrayList()
	{
		return preSelectList;
	}
	
	public void srcActionNotify(CheckerPiece piece)
	{
		// state = 0: free state, possible next action: piece selection
		// state = 1: a piece is selected, possible next action: re-select, move and jump
		//			: a king is selected, possible next action: fly
		// state = 2: a piece is jumped, possible next action: jump again (double jump or more)
		// state = 3: a king is flied, possible next action: fly again (double fly or more)
		
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
		
		// no piece is selected, no further action can be performed
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
		
		// if a piece is selected, possible next action is move or jump
		// if a king is selected, possible next action is fly
		if (state == STATE_SELECTED)
		{
			if ((rowDiff == 1) && (colDiff == 1) && !piece.getPreSelect()) // prohibit move if the piece is preSelect (i.e. must do jump/capture)
				move(piece, dstRow, dstCol);
			else if ((rowDiff == 2) && (colDiff == 2) && !piece.getCrown())
				jump(piece, dstRow, dstCol);
			else if (piece.getCrown())
				fly(piece, dstRow, dstCol);
			else
				System.out.println("Invalid action for " + piece.getLabel());
		}	
		// check for double/continuous jump possibility 
		else if (state == STATE_JUMPED)
		{
			if ((rowDiff == 2) && (colDiff == 2))
				jump(piece, dstRow, dstCol);	
		}
		// check for double/continuous fly possibility 
		else if (state == STATE_FLIED)
		{
			fly(piece, dstRow, dstCol);	
		}
	}
	
	public void clrPreSelection()
	{	
		// clear preSelectList and its pieces
		for (CheckerPiece piece: preSelectList)
		{
			// a task is blinking the piece. Make sure it is set back to visible.
			piece.setVisible(true);
			piece.setPreSelect(false);
			
			// clear next action
			piece.setNextAction(0);
		}
				
		// clear preSelectList
		preSelectList.clear();
				
		// kill the task
		if (task != null)
			task.cancel();
	}
	
	
	public void actionComplete()
	{
		clrPreSelection();
		setState(STATE_FREE); // reset to free state
		Checker.turnOver();
	}
	
	public void move(CheckerPiece piece, int row, int col)
	{
		if (((piece.getColor() == Color.ORANGE) && (row < piece.getRow())) ||
			((piece.getColor() == Color.WHITE) && (row > piece.getRow())) ||
			  piece.getCrown())
		{	
			// move piece in the board
			CheckerBoard board = Checker.getBoard();
			board.removePiece(piece, piece.getRow(), piece.getCol());
			board.addPiece(piece, row, col);	
			
			// set piece attribute for new location
			piece.select(false);
			piece.setRow(row);
			piece.setCol(col);
			piece.setLabel("(" + row + "," + col + ")");
			if ((row == kingRow) && !piece.getCrown())
				piece.setCrown();
			
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
		
		// get the piece to capture
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
			
			// if the middle tile is occupied by opponent piece, capture is valid
			if (tile[midRow][midCol].getOccupied() == Checker.getOpponentPlayer())
			{
				// move piece in the board
				board.removePiece(piece, piece.getRow(), piece.getCol());
				board.addPiece(piece, row, col);	
				
				// set piece attribute for new location
				piece.select(false);
				piece.setRow(row);
				piece.setCol(col);
				piece.setLabel("(" + row + "," + col + ")");
				if ((row == kingRow) && !piece.getCrown())
					piece.setCrown();
				
				// remove opponent piece
				capture(midRow, midCol);
			
				// clear preSelectList after making one successful jump
				clrPreSelection();
				
				// check for double jump
				if (board.canJumpCapture(piece.getColor(), row, col))
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
	
	public void fly(CheckerPiece piece, int dstRow, int dstCol)
	{
		int srcRow = piece.getRow();
		int srcCol = piece.getCol();
		
		// fly diagonally
		if (Math.abs(dstRow - srcRow) == Math.abs(dstCol - srcCol))
		{
			// check any obstacle piece along the fly
			int incrementRow = (dstRow - srcRow)/Math.abs(dstRow - srcRow);
			int incrementCol = (dstCol - srcCol)/Math.abs(dstCol - srcCol);
			int row = srcRow;
			int col = srcCol;
			int opponentCount = 0;
			int opponentRow = -1;
			int opponentCol = -1;
			
			CheckerBoard board = Checker.getBoard();
			CheckerTile[][] tile = board.getTileArray();
			
			// since this is diagonal fly, just check for row (col will increment accordingly too)
			while (row != dstRow)
			{
				row += incrementRow;
				col += incrementCol;
				
				if (tile[row][col].getOccupied() == Checker.getOpponentPlayer())
				{
					opponentCount++;
					opponentRow = row;
					opponentCol = col;
					
					// two opponent pieces found, cannot fly
					if (opponentCount > 1) // two opponent pieces along the fly
						return;
				}
				// block by own piece, cannot fly
				else if (tile[row][col].getOccupied() == Checker.getCurrentPlayer()) 
				{
					return;
				}
				// else occupied = Color.BLACK, i.e. tile is unoccupied. In this case, continue to next diagonal tile
			}
			
			// move piece in the board
			board.removePiece(piece, piece.getRow(), piece.getCol());
			board.addPiece(piece, dstRow, dstCol);	
				
			// set piece attribute for new location
			piece.select(false);
			piece.setRow(dstRow);
			piece.setCol(dstCol);
			piece.setLabel("(" + dstRow + "," + dstCol + ")");
			
			// fly only scenario, no opponent to capture
			if ((state != STATE_FLIED) && (opponentCount == 0))
				actionComplete();
			
			// fly and capture scenario
			else if (opponentCount == 1)
			{
				// remove opponent piece
				capture(opponentRow, opponentCol);
			
				// clear preSelectList after making one successful fly
				clrPreSelection();
				
				// check for double fly
				if (board.canFlyCapture(piece.getColor(), row, col))
				{
					piece.select(true);
					setState(STATE_FLIED);
				}
				else 
					actionComplete();
			}
		}
		else
		{
			System.out.println("Piece " + piece.getLabel() + " cannot fly non-diagnolly");
		}
	}
	
	public void checkPlayerPossibleCapture()
	{
		CheckerPlayer player = Checker.getPlayer(Checker.getCurrentPlayer());
		CheckerBoard board = Checker.getBoard();
		ArrayList<CheckerPiece> preSelectList = player.getPreSelectArrayList();
		
		// check any piece have capture possibility in next action
		for (CheckerPiece piece : player.pieces)
		{
			int row = piece.getRow();
			int col = piece.getCol();
			
			if (!piece.getCrown() && board.canJumpCapture(piece.getColor(), row, col))
			{
				// save the piece with capture possibility to preSelectList
				preSelectList.add(piece);
				System.out.println("checkPlayerPossibleCapture: piece (" + row + "," + col + ") can capture. ");
			}
			else if (piece.getCrown() && board.canFlyCapture(piece.getColor(), row, col))
			{
				// save the crowned king piece with capture possibility to preSelectList
				preSelectList.add(piece);
				System.out.println("checkPlayerPossibleCapture: king (" + row + "," + col + ") can capture. ");
			}
		}
		
		if (!preSelectList.isEmpty())
		{
			// start a timer task to blink the piece for attention
			task = new CheckerTimerTask();
			timer.schedule(task, 500, 500);
		}
	}
}

