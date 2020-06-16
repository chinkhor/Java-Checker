import java.awt.Color;
import java.util.ArrayList;

public class CheckerPlayer {

	private Color playerColor;
	private CheckerBoard board;
	// player array list, store all its pieces
	private ArrayList<CheckerPiece> pieces;
	
	// number of rows for each player = half of number of tiles per row minus one
	private final int PLAY_ROWS = CheckerBoard.TILES/2 - 1;
	
	public CheckerPlayer(Color color, CheckerBoard board)
	{
		int startRow, endRow;
		
		this.playerColor = color;
		this.board = board;
		this.pieces = new ArrayList<CheckerPiece>();
	
		// player ORANGE is at bottom of board
		if (color == Color.ORANGE)
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
				CheckerPiece piece = new CheckerPiece(row, col, color);
				board.addPiece(piece, row, col);
				
				// add piece to player array list
				pieces.add(piece);
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
}

