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
			// state = 0: free state, possible next action: piece selection
			// state = 1: a piece is selected, possible next action: re-select, move and jump
			// state = 2: a piece is jumped, possible next action: jump again (double jump or more)
			CheckerPlayer player = Checker.getPlayer(Checker.getCurrentPlayer());
			int state = player.getState();
			
			if ((state == STATE_FREE) || (state == STATE_SELECTED))
			{
				player.setState(STATE_SELECTED); // select or re-select sets this state
				
				// de-select any piece formerly selected
				// if there is one, it is always located at head of the player piece array list (index 0)
				CheckerPiece p = player.pieces.get(0);
				if (p.isSelect())
					p.select(false);
				
				// select the newly selected piece
				piece.select(true);
				
				// and move the selected piece to the head of the player piece array list  
				player.pieces.remove(piece);
				player.pieces.add(0, piece); 
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
}

