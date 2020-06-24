import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;

public class CheckerPlayer implements ActionListener {
	public String colorCode; // for tracing and debugging use only
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
	protected boolean surrender = false;
	
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
			colorCode = "ORANGE: ";
		}
		// player WHITE is at top of board
		else
		{
			startRow = 0;
			endRow = PLAY_ROWS;
			kingRow = CheckerBoard.TILES - 1;
			colorCode = "WHITE: ";
		}
		for (int row = startRow; row < endRow; row++)
		{
			// starting col is at 0 or 1 based on row number
			for (int col = row %2; col < CheckerBoard.TILES; col+=2)
			{
				// create piece and add to the board
				CheckerPiece piece = new CheckerPiece(row, col, playerColor);
				board.addPiece(piece, row, col);
				
				// randomly add piece to player array list (head or tail)
				int rand = ((int)Math.random()) % 2;
				if (rand == 0)
					pieces.add(0, piece);
				else
					pieces.add(piece);
				
				// action action listener for button click
				piece.addActionListener(this);
			}
		}
		showPlayerPieceList();
		
		// start timer
		timer = new Timer();
	}
	
	public int getKingRow()
	{
		return this.kingRow;
	}
	
	public boolean getSurrender()
	{
		return this.surrender;
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
	
	public void showPlayerPieceList()
	{
		System.out.print(colorCode + "Player piece list: ");
		for (CheckerPiece piece : pieces)
		{
			System.out.print(piece.getLabel() + " ");
		}
		System.out.println("");
	}
	
	public void actionPerformed(ActionEvent e)
	{
		CheckerPiece piece = (CheckerPiece) e.getSource();
		
		System.out.println(piece.colorCode + "actionPerformed(): piece " + piece.getLabel() + " is clicked");
		
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
	
	public void srcActionNotify(CheckerPiece piece)
	{
		// state = 0: free state, possible next action: piece selection
		// state = 1: a piece is selected, possible next action: re-select, move and jump
		//			: a king is selected, possible next action: fly
		// state = 2: a piece is jumped, possible next action: jump again (double jump or more)
		// state = 3: a king is flied, possible next action: fly again (double fly or more)
		
		int state = getState();
		System.out.printf(colorCode + "srcxActionNotify(): piece %s state %d\n", piece.getLabel(), state);			
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
					
		int dstRow = tile.getRow();
		int dstCol = tile.getCol();
		
		System.out.printf(colorCode + "dstActionNotify(): piece %s target (%d,%d) state %d\n", piece.getLabel(), dstRow, dstCol, state);
		// if a piece is selected, possible next action is move or jump
		// if a king is selected, possible next action is fly
		if (state == STATE_SELECTED)
		{
			if (piece.canFlyCapture(dstRow,dstCol))
			{
				piece.setTgtRow(dstRow);
				piece.setTgtCol(dstCol);
				flyCapture(piece);
			}
			else if (piece.canJump(dstRow,dstCol))
			{
				piece.setTgtRow(dstRow);
				piece.setTgtCol(dstCol);
				jump(piece);
			}
			else if (piece.canFly(dstRow, dstCol))
			{
				piece.setTgtRow(dstRow);
				piece.setTgtCol(dstCol);
				fly(piece);
			}
			else if (piece.canMove(dstRow, dstCol, false))
			{
				piece.setTgtRow(dstRow);
				piece.setTgtCol(dstCol);
				move(piece);
			}
			else
				System.out.println(colorCode + "dstActionNotify(): Invalid action for " + piece.getLabel());
		}	
		// check for double/continuous jump possibility 
		else if (state == STATE_JUMPED)
		{
			if (piece.canJump(dstRow,dstCol))
			{
				piece.setTgtRow(dstRow);
				piece.setTgtCol(dstCol);
				jump(piece);
			}
		}
		// check for double/continuous fly possibility 
		else if (state == STATE_FLIED)
		{
			if (piece.canFlyCapture(dstRow,dstCol))
			{
				piece.setTgtRow(dstRow);
				piece.setTgtCol(dstCol);
				flyCapture(piece);
			}	
		}
	}
	
	public void clrPreSelection()
	{	
		
		System.out.print(colorCode + "clrPreSelection() : preSelectList: ");
		// clear preSelectList and its pieces
		for (CheckerPiece piece: preSelectList)
		{
			// a task is blinking the piece. Make sure it is set back to visible.
			piece.setVisible(true);
			piece.setPreSelect(false);
			
			// clear next action
			piece.setNextAction(0);
			
			piece.setMoveRisk(-1);
			piece.setTgtRow(-1);
			piece.setTgtCol(-1);
			System.out.print(piece.getLabel() + " ");
		}
		System.out.println("");
		
		// clear preSelectList
		preSelectList.clear();
				
		// kill the task
		if (task != null)
			task.cancel();
	}
	
	
	public void actionComplete()
	{
		System.out.println(colorCode + "actionComplete for player ");
		clrPreSelection();
		setState(STATE_FREE); // reset to free state
		Checker.turnOver();
	}
	
	public void move(CheckerPiece piece)
	{
		int row = piece.getTgtRow();
		int col = piece.getTgtCol();
		
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

		System.out.printf("%s move: piece (%d,%d) is moved to (%d,%d), state %d, crown %s\n", colorCode, piece.getRow(), piece.getCol(), row, col, getState(), piece.getCrown());	
					
		actionComplete();
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
			System.out.println(colorCode + "capture: Piece (" + row + "," + col + ") was captured");
		}
		else
			System.out.println(colorCode + "capture: Piece (" + row + "," + col + ") does not exist, cannot capture");
	}
	

	public void jump(CheckerPiece piece)
	{
		boolean crowning = false;
		int row = piece.getTgtRow();
		int col = piece.getTgtCol();
		int srcRow = piece.getRow();
		int srcCol = piece.getCol();
		int midRow = srcRow+(row-srcRow)/2;
		int midCol = srcCol+(col-srcCol)/2;
			
		
		CheckerBoard board = Checker.getBoard();			
		// move piece in the board
		board.removePiece(piece, srcRow, srcCol);
		board.addPiece(piece, row, col);	
				
		// set piece attribute for new location
		piece.select(false);
		piece.setRow(row);
		piece.setCol(col);
		piece.setLabel("(" + row + "," + col + ")");
		if ((row == kingRow) && !piece.getCrown())
		{
			crowning = true;
			piece.setCrown();
		}		
		// remove opponent piece
		capture(midRow, midCol);
			
		// clear preSelectList after making one successful jump
		clrPreSelection();
		
		System.out.printf("%s jump: piece (%d,%d) is jumped to (%d,%d), captured (%d,%d), state %d, crowning %s\n", colorCode, srcRow, srcCol, row, col, midRow, midCol, getState(), crowning);	
		
		// check for double jump
		if (!crowning && piece.canJump())
		{
			piece.select(true);
			setState(STATE_JUMPED);
		}
		else 
			actionComplete();
	}
	
	public void fly(CheckerPiece piece)
	{
		int row = piece.getTgtRow();
		int col = piece.getTgtCol();
		
		// move piece in the board
		CheckerBoard board = Checker.getBoard();
		board.removePiece(piece, piece.getRow(), piece.getCol());
		board.addPiece(piece, row, col);	
			
		// set piece attribute for new location
		piece.select(false);
		piece.setRow(row);
		piece.setCol(col);
		piece.setLabel("(" + row + "," + col + ")");
		
		System.out.printf("%s fly: piece (%d,%d) is flied to (%d,%d), state %d, crown %s\n", colorCode, piece.getRow(), piece.getCol(), row, col, getState(), piece.getCrown());	
			
		actionComplete();
	}
	
	public void flyCapture(CheckerPiece piece)
	{
		int srcRow = piece.getRow();
		int srcCol = piece.getCol();
		int row = piece.getTgtRow();
		int col = piece.getTgtCol();
			

		CheckerBoard board = Checker.getBoard();			
		// move piece in the board
		board.removePiece(piece, srcRow, srcCol);
		board.addPiece(piece, row, col);	
				
		// set piece attribute for new location
		piece.select(false);
		piece.setRow(row);
		piece.setCol(col);
		piece.setLabel("(" + row + "," + col + ")");
			
		// remove opponent piece
		int rowDir = (row-srcRow)/Math.abs(row-srcRow);
		int colDir = (col-srcCol)/Math.abs(col-srcCol);
		int opponentRow = srcRow + rowDir;
		int opponentCol = srcCol + colDir;
		while (!board.isTileOccupiedByPlayer(opponentRow, opponentCol, Checker.getOpponentPlayer()))
		{
			opponentRow+=rowDir;
			opponentCol+=colDir;
		}
		capture(opponentRow, opponentCol);
		System.out.printf(colorCode + "flyCapture: piece (%d,%d) is flied to (%d,%d), captured (%d,%d), state %d\n", srcRow, srcCol, row, col, opponentRow, opponentCol, getState());
			
		// clear preSelectList after making one successful jump
		clrPreSelection();
				
		// check for double fly capture
		if (piece.canFlyCapture())
		{
			piece.select(true);
			setState(STATE_FLIED);
		}
		else 
			actionComplete();
	}

	
	public void checkPlayerPossibleMove()
	{			
		// check any piece have move possibility in next action
		for (CheckerPiece piece : pieces)
		{
			int row = piece.getRow();
			int col = piece.getCol();
	
			if (piece.canFly())
			{
				// save the crowned king piece with fly possibility to preSelectList
				preSelectList.add(0, piece);
				//piece.setNextAction(CheckerPiece.A_FLY);
				System.out.println(colorCode + "checkPlayerPossibleMove: king (" + row + "," + col + ") can fly. ");
			}
			else if (piece.canMove())
			{
				// save the piece with move possibility to preSelectList
				preSelectList.add(piece);
				//piece.setNextAction(CheckerPiece.A_MOVE);
				System.out.println(colorCode + "checkPlayerPossibleMove: piece (" + row + "," + col + ") can move. ");
			}
		}
		
		if (preSelectList.isEmpty())
		{
			surrender = true;
		}
			
	}
	
	public void checkPlayerPossibleCapture()
	{
		CheckerPlayer player = Checker.getPlayer(Checker.getCurrentPlayer());
		ArrayList<CheckerPiece> preSelectList = player.getPreSelectArrayList();
		
		// check any piece have capture possibility in next action
		for (CheckerPiece piece : player.pieces)
		{
			int row = piece.getRow();
			int col = piece.getCol();
			
			if (piece.canFlyCapture())
			{
				// save the crowned king piece with capture possibility to preSelectList
				preSelectList.add(0, piece);
				//piece.setNextAction(CheckerPiece.A_FLYCAPTURE);
				System.out.println(colorCode + "checkPlayerPossibleCapture (player): king (" + row + "," + col + ") can capture. ");
			}
			else if (piece.canJump())
			{
				// save the piece with capture possibility to preSelectList
				preSelectList.add(piece);
				//piece.setNextAction(CheckerPiece.A_JUMP);
				System.out.println(colorCode + "checkPlayerPossibleCapture (player): piece (" + row + "," + col + ") can capture. ");
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

