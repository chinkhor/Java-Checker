import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;

/**
 * @author      Chin Kooi Khor (chin.kooi.khor@gmail.com)
 * @version     1.0   
 * @since       24 Jun 2020  
 */
public class CheckerPlayer implements ActionListener {
	/**
	 * the row number where the piece can become king when reaching this row
	 */
	protected int kingRow;
	
	/**
	 * surrender flag which is set when out of move
	 */
	protected boolean surrender = false;
	/**
	 * the color of the player (orange or white)
	 */
	protected Color playerColor;
	
	/**
	 * Arrraylist which stores all pieces of the player
	 */
	protected ArrayList<CheckerPiece> pieces; 
	
	/**
	 * Arrraylist which stores all pre-selected pieces for next play
	 */
	protected ArrayList<CheckerPiece> preSelectList;
	
	/**
	 * constant that set the number of rows for each player to place their pieces before the game starts
	 */
	protected final int PLAY_ROWS = CheckerBoard.TILES/2 - 1;
	
	/**
	 * state - the state of the player's current play, possible states are
	 * <br> STATE_FREE = 0
	 * <br> STATE_SELECTED =1
	 * <br> STATE_JUMPED = 2
	 * <br> STATE_FLIED = 3
	 */
	protected int state = 0;
	protected final static int STATE_FREE = 0;
	protected final static int STATE_SELECTED = 1;
	protected final static int STATE_JUMPED = 2;
	protected final static int STATE_FLIED = 3;
	// state = 0: free, possible next action: select
	// state = 1: selected, possible next action: re-select, move, jump, fly, flyCapture
	// state = 2: jumped, possible next action: jump again (double jump or more)
	// state = 3: flied, possible next action: flyCapture again (double fly or more)
	
	/**
	 * Timer instance
	 */
	protected Timer timer;
	
	/**
	 * CheckerTimerTask instance inheriting TimerTask
	 */
	protected CheckerTimerTask task;
	
	/**
	 * string that shows Color (WHITE or ORANGE) for tracing and debugging use only
	 */
	public String colorCode; 
	
	/**
	 * Constructor of CheckerPlayer class
	 * <p>                           
	 * This constructor will create the pieces and place them on the board. All the pieces will be added to ArrayList. A timer is also created.
	 * 
	 * @param  color	The color of the pieces	
	 * @param  board    The CheckerBoard instance where the pieces are placed on  
	 */
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
	
	/**
	 * Getter to get the row number where pieces becoming king 
	 * 
	 * @return int	Return kingRow: the row number where the pieces of this player will become king  
	 */
	public int getKingRow()
	{
		return this.kingRow;
	}
	
	/**
	 * Getter to get surrender flag 
	 * 
	 * @return boolean	Return surrender flag  
	 */
	public boolean getSurrender()
	{
		return this.surrender;
	}
	
	/**
	 * Setter to set state flag 
	 * 
	 * @param  state Set the state of the player to this   
	 */
	public void setState (int state)
	{
		this.state = state;
	}
	
	/**
	 * Getter to get state flag 
	 * 
	 * @return int Return the state of the player  
	 */
	public int getState()
	{
		return this.state;
	}
	
	/**
	 * Getter to get ArrayList which stores all pieces of this player 
	 *  
	 * @return CheckerPiece Return the ArrayList of the pieces 
	 */
	public ArrayList<CheckerPiece> getPieceArrayList()
	{
		return pieces;
	}
	
	/**
	 * Getter to get ArrayList which stores all pieces pre-selected for next play 
	 *  
	 * @return CheckerPiece Return the preSelectList ArrayList 
	 */
	public ArrayList<CheckerPiece> getPreSelectArrayList()
	{
		return preSelectList;
	}
	
	/**
	 * Displays all the pieces in (row, col) format stored in the ArrayList                            
	 *
	 */
	private void showPlayerPieceList()
	{
		System.out.print(colorCode + "Player piece list: ");
		for (CheckerPiece piece : pieces)
		{
			System.out.print(piece.getLabel() + " ");
		}
		System.out.println("");
	}

	/**
	 * This method is an action event called when a piece is mouse clicked by the player                            
	 * <p>
	 * If the clicked piece is owned by the player, srcActionNotify will be called for further action
	 * 
	 * @param  e the piece instance which is the source generating the event can be retrieved via e.getSource ()	      
	 */
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
	/**
	 * This method is called when a piece is selected to set the "state" of the player to STATE_SELECTED                           
	 * <p>
	 * To highlight the piece is selected, the "select" flag of the piece is set. And, the selected piece is moved to the head of the ArrayList for pieces
	 * 
	 * @param  piece  CheckerPiece instance for next action        
	 */
	public void srcActionNotify(CheckerPiece piece)
	{
		// state = 0: free state, possible next action: piece selection
		// state = 1: a piece is selected, possible next action: re-select
		// state = 2: invalid
		// state = 3: invalid
		
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
	
	/**
	 * This method is called to determine the next action of the play                           
	 * <p>
	 * This method will check the current state of the player and decide next possible action:
	 * <br>STATE &emsp; &emsp; &emsp; &emsp; &emsp; &nbsp;POSSIBLE ACTIONS
	 * <br>STATE_FREE &emsp; &emsp; &emsp; no
	 * <br>STATE_SELECTED &emsp; move, fly, jump, flycapture
	 * <br>STATE_JUMPED &emsp; &emsp;jump
	 * <br>STATE_FLIED &emsp; &emsp; &emsp;flycapture
	 * 
	 * @param  tile  CheckerTile instance which specifies the destination where the piece will get to based on the possible action        
	 */
	public void dstActionNotify(CheckerTile tile)
	{
		// state = 0: free state, possible next action: none
		// state = 1: a piece is selected, possible next action: move and jump
		//			: a king is selected, possible next action: fly and flyCapture
		// state = 2: a piece is jumped, possible next action: jump again (double jump or more)
		// state = 3: a king is flied, possible next action: fly again (double fly or more)
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
			else if (piece.canJump(dstRow,dstCol, false))
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
			if (piece.canJump(dstRow,dstCol, false))
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
	
	/**
	 * Clear preSelectList and reset its pieces to initial value                          
	 * <p>
	 * This method will also kill the invoked TimerTask 
	 * 
	 */
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
			
			piece.setRisk(CheckerPiece.INITIAL_RISK);
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
	
	/**
	 * Complete the current play. 
	 * <p>
	 * This method will clear state and turns over the play to opponent player                        
	 * 
	 */
	public void actionComplete()
	{
		System.out.println(colorCode + "actionComplete for player ");
		clrPreSelection();
		setState(STATE_FREE); // reset to free state
		Checker.turnOver();
	}
	
	/**
	 * Move the piece to destined tile on the board                       
	 * <p>
	 * If the move hits the king row, the piece will be crowned to become king
	 * @param  piece The piece for the move        
	 */
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

	/**
	 * Capture the piece at (row,col) by removing it from the board and the opponent player ArrayList for pieces                      
	 * 
	 * @param  row The row number on the board where the piece is located 
	 * @param  col The column number on the board where the piece is located         
	 */
	private void capture(int row, int col)
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
	
	/**
	 * Jump the piece to destined tile on the board and captures the opponent piece in between                      
	 * <p>
	 * If the jump is destined to the king row, the piece will be crowned to become king
	 * @param  piece The piece for the jump        
	 */
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
	
	/**
	 * Fly the piece to destined tile on the board                      
	 * 
	 * @param  piece The piece for the fly        
	 */
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
	
	/**
	 * Fly the piece to destined tile on the board and capture opponent piece along the fly                      
	 * 
	 * @param  piece The piece for the fly and capture       
	 */
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

	/**
	 * Check all the pieces that have valid move and fly in the play and saves them to preSelectList ArrayList
	 * <p>
	 * If no piece has valid move or fly, surrender flag will be set                      
	 * 
	 */
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
	
	/**
	 * Check all the pieces that have valid jump and flyCapture in the play and saves them to preSelectList ArrayList
	 * <p>
	 * One of these pieces must be selected for next play. A timer task will be invoked to blinking all these pieces for attention to the player for next selection.                    
	 * 
	 */
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

