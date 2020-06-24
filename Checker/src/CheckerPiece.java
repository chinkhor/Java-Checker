import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

@SuppressWarnings("serial")
/**
 * @author      Chin Kooi Khor (chin.kooi.khor@gmail.com)
 * @version     1.0   
 * @since       24 Jun 2020  
 */
public class CheckerPiece extends JButton
{
	/**
	 * x-axis location of the piece to be drawn on the tile
	 */
	private int pieceX = CheckerTile.TILE_SIZE/4;
	/**
	 * y-axis location of the piece to be drawn on the tile
	 */
	private int pieceY = CheckerTile.TILE_SIZE/4;
	/**
	 * size of the piece to be drawn on the tile
	 */
	private int pieceSize = CheckerTile.TILE_SIZE/2;
	/**
	 * Color of the piece (WHITE or ORANGE)
	 */
	private Color color = Color.WHITE;
	/**
	 * Row number of tile on the board where the piece occupies
	 */
	private int row;
	/**
	 * Column number of tile on the board where the piece occupies
	 */
	private int col;
	/**
	 * select flag. This flag will be set when the piece is selected for play action
	 */
	private boolean select = false;
	/**
	 * preSelect flag. This flag will be set when the piece is pre-selected for next play action (e.g. jump for capture) before player select
	 */
	private boolean preSelect = false;
	/**
	 * image of the crown, indicating the piece is a king
	 */
	private Image img;	
	/**
	 * crowned flag. This flag will be set when a piece is promoted to king
	 */
	private boolean crowned = false;
	
	/**
	 * Constant value for initial risk, very high number
	 */
	public static final int INITIAL_RISK = 999;
	
	/**
	 * Constant risk value for being king, if a piece reaches king row, there is no risk for being captured, should be prioritized action play
	 */ 
	public final static int BE_KING = 0;
	/**
	 * Constant risk value for no capture is detected
	 */
	public final static int NO_CAPTURE = 1;
	/**
	 * Constant risk value for potential capture by opponent piece
	 */
	public final static int CAPTURE_BY_PIECE = 2;
	/**
	 * Constant risk value for potential capture by opponent king, highest risk for the play action, should be avoid if possible
	 */
	public final static int CAPTURE_BY_KING = 3;
	/**
	 * risk value for next play action
	 */	
	private int risk = INITIAL_RISK;
	/**
	 * Row number of the destined or target tile for piece to get to in next play
	 */
	private int tgtRow = -1;
	/**
	 * Column number of the destined or target tile for piece to get to in next play
	 */
	private int tgtCol = -1;
	
	/**
	 * next play action
	 */
	private int nextAction = 0;
	/**
	 * Constant value for move as next play action
	 */
	public static final int A_MOVE = 1;
	/**
	 * Constant value for fly as next play action
	 */
	public static final int A_FLY = 2;
	/**
	 * Constant value for jump as next play action
	 */
	public static final int A_JUMP = 3;
	/**
	 * Constant value for fly and capture as next play action
	 */
	public static final int A_FLYCAPTURE = 4;

	// nextAction = 1: move
	// nextAction = 2: fly
	// nextAction = 3: jump
	// nextAction = 4: fly capture
	/**
	 * String label for the piece, it is (row#, col#) in text 
	 */
	private String label;
	/**
	 * Color for the piece in String format, mainly use for tracing and debugging only 
	 */
	public String colorCode; 
	
	/**
	 * Constructor of CheckerPiece class
	 * <p>                           
	 * This constructor will construct a JButton piece. It will also load the crown image.
	 * 
	 * @param  row Row number of the tile where the piece will be located
	 * @param  col Column number of the tile where the piece will be located
	 * @param  color Color of the piece
	 */
	public CheckerPiece(int row, int col, Color color)
	{
		super();
		// do not paint button border, as the button will be re-drawn in oval shape
		setBorderPainted(false);
		this.color = color;
		this.row = row;
		this.col = col;
		this.label = "(" + row + "," + col + ")";
		try 
		   {
			   this.img = ImageIO.read(new File("src/crown.png"));
		   }   catch (IOException e)
		   {
			   System.out.println("Couldn't load/find crown.png");
			   System.exit(0);
		   }
		if (color == Color.ORANGE)
			colorCode = "ORANGE: ";
		else
			colorCode = "WHITE: ";
	}
	
	/**
	 * Paint the piece in round shape in the middle of the tile 
	 * <p>
	 * If the piece is a king, a crown image will be painted on the piece. If the piece is selected, a blue round bold boarder will be drawn.
	 * 
	 * @param  g Instance of Graphics for painting
	 */
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		super.paintComponent(g2);
		
		g2.setColor(color);
		g2.fillOval(pieceX, pieceY, pieceSize, pieceSize);
		if (crowned)
	    {
	    	g2.drawImage(img, pieceX, pieceY, pieceSize, pieceSize, null);
	    }  
	
		if (select)
		{
			g2.setColor(Color.BLUE);
			// increase the line thickness
			g2.setStroke(new BasicStroke(3));
		}
		g2.drawOval(pieceX, pieceY, pieceSize, pieceSize);  
	}

	/**
	 * Setter to set next action for the piece
	 * 
	 * @param  action Next action to be set  
	 */
	public void setNextAction(int action)
	{
		this.nextAction = action;
	}
	
	/**
	 * Getter to get next action for the piece
	 * 
	 * @return int Return next action for the piece   
	 */
	public int getNextAction()
	{
		return this.nextAction;
	}
	
	/**
	 * Setter to set risk for the piece on next action
	 * 
	 * @param  risk Risk value for next action 
	 */
	public void setRisk(int risk)
	{
		this.risk = risk;
	}
	
	/**
	 * Getter to get the risk for piece on next action
	 * 
	 * @return int Return risk value for next action   
	 */
	public int getRisk()
	{
		return risk;
	}
	
	/**
	 * Getter to get row number of the tile where the piece is located
	 * 
	 * @return int Return row number of the tile where the piece is located   
	 */
	public int getRow()
	{
		return this.row;
	}
	
	/**
	 * Getter to get column number of the tile where the piece is located
	 * 
	 * @return int Return column number of the tile where the piece is located   
	 */
	public int getCol()
	{
		return this.col;
	}
	
	/**
	 * Setter to set row number of the tile where the piece is located
	 * 
	 * @param  row Row number of the tile where the piece is located 
	 */
	public void setRow(int row)
	{
		this.row = row;
	}
	
	/**
	 * Setter to set column number of the tile where the piece is located
	 * 
	 * @param  col Column number of the tile where the piece is located
	 */
	public void setCol(int col)
	{
		this.col = col;
	}
	
	/**
	 * Getter to get row number of the destined tile for the piece
	 * 
	 * @return int Return row number of the destined tile for the piece   
	 */
	public int getTgtRow()
	{
		return this.tgtRow;
	}
	
	/**
	 * Getter to get column number of the destined tile for the piece
	 * 
	 * @return int Return column number of the destined tile for the piece   
	 */
	public int getTgtCol()
	{
		return this.tgtCol;
	}
	
	/**
	 * Setter to set row number of the tile where the piece is destined in next action
	 * 
	 * @param  row Row number of the tile where the piece is destined in next action  
	 */
	public void setTgtRow(int row)
	{
		this.tgtRow = row;
	}
	
	/**
	 * Setter to set column number of the tile where the piece is destined in next action
	 * 
	 * @param  col Column number of the tile where the piece is destined in next action 
	 */
	public void setTgtCol(int col)
	{
		this.tgtCol = col;
	}
	
	/**
	 * Getter to get color for the piece
	 * 
	 * @return Color Return color of the piece   
	 */
	public Color getColor()
	{
		return this.color;
	}
	
	/**
	 * Setter to set label of the piece
	 * 
	 * @param  str String label for the piece 
	 */
	public void setLabel(String str)
	{
		this.label = str;
	}
	
	/**
	 * Getter to get label for the piece
	 * 
	 * @return String Return string label    
	 */
	public String getLabel()
	{
		return this.label;
	}
	
	/**
	 * Check if select flag for the piece is set
	 * 
	 * @return boolean Return select flag value   
	 */
	public boolean isSelect()
	{
		return select;
	}
	
	/**
	 * Getter to get preSelect flag for the piece
	 * 
	 * @return boolean Return the preSelect flag   
	 */
	public boolean getPreSelect()
	{
		return preSelect;
	}
	
	/**
	 * Setter to set preSelect flag
	 * 
	 * @param  s preSelect flag value 
	 */
	public void setPreSelect(boolean s)
	{
		this.preSelect = s;
	}

	/**
	 * Setter to set select flag
	 * 
	 * @param  s select flag value  
	 */
	public void select(boolean s)
	{
		this.select = s;
		repaint();
	}
	
	/**
	 * Setter to set crown for piece
	 *   
	 */
	public void setCrown()
	{
		this.crowned = true;
		repaint();
	}
	
	/**
	 * Getter to get crown status for piece
	 * 
	 * @return boolean Return the crown status of the piece  
	 */
	public boolean getCrown()
	{
		return this.crowned;
	}
	
	/**
	 * Check if the piece can jump in any direction
	 * 
	 * @return boolean Return true if there is valid jump for the piece  
	 */
	public boolean canJump()
	{
		boolean status1, status2, status3, status4;
		
		setRisk(INITIAL_RISK); // initialize risk to high number 
		setTgtRow(-1);
		setTgtCol(-1);
		
		// random generate row and col direction 
		int randRow = ((int) Math.random())%2;
		if (randRow == 0)
			randRow = -1;
		int randCol = ((int) Math.random())%2 ;
		if (randCol == 0)
			randCol = -1;
			
		int row = this.row+2*randRow;
		int col = this.col+2*randCol;
		status1 = canJump(row, col, true);
		
		row = this.row+randRow*(-2);
		status2 = canJump(row, col, true);
		
		col = this.col+randCol*(-2);
		status3 = canJump(row, col, true);
		
		row = this.row+2*randRow;
		status4 = canJump(row, col, true);
		
		System.out.printf(colorCode + "canJump: piece %s, jump status %s, best jump (%d,%d), risk %s\n", label, status1 || status2 || status3 || status4, tgtRow, tgtCol, risk);
		
		return (status1 || status2 || status3 || status4);	
	}
	
	/**
	 * Check if the piece can jump to a destination and capture an opponent. Risk of the jump can be computed if needed.
	 * 
	 * @param row The row number of the destined tile where the piece is jumped to
	 * @param col The column number of the destined tile where the piece is jumped to
	 * @param computeRisk Compute the risk of the jump if this flag is set 
	 * @return boolean Return true if there is valid jump for the piece
	 */
	public boolean canJump(int row, int col, boolean computeRisk)
	{
		CheckerBoard board = Checker.getBoard();
		int srcRow = this.row;
		int srcCol = this.col;
		
		// check if the jump is diagonally up or down two tiles
		if (Math.abs(row-srcRow)!=2 || Math.abs(col-srcCol)!=2)
			return false;
		
		int midRow = srcRow + (row-srcRow)/Math.abs(row-srcRow);
		int midCol = srcCol + (col-srcCol)/Math.abs(col-srcCol);
		
		// check out of boundary
		if ((row < 0 || row >= CheckerBoard.TILES || col < 0 || col >= CheckerBoard.TILES) ||
		    (midRow < 0 || midRow >= CheckerBoard.TILES || midCol < 0 || midCol >= CheckerBoard.TILES))
			return false;
			
		// check if the piece (not king, i.e. not yet crowned) is jumped backward 
		if ((!this.crowned && (this.color == Color.orange) && (row > srcRow)) ||
			(!this.crowned && (this.color == Color.white) && (row < srcRow)))
			return false;

		// check if the jump-to tile is free
		if (board.isTileFree(row,  col) && board.isTileOccupiedByPlayer(midRow, midCol, Checker.getOpponentPlayer())) 
		{
			if (computeRisk)
			{
				int risk = board.computeActionRisk(this, row, col);
				if (risk < getRisk())
				{
					setRisk(risk);
					setTgtRow(row);
					setTgtCol(col);
				}
			}
			System.out.printf(colorCode + "canJump(): piece %s can jump to (%d,%d) and capture (%d,%d)\n", label, row,col,midRow,midCol);
			return true;
		}
		else
			return false;

	}
	
	/**
	 * Check if the king can fly and capture an opponent in any direction
	 * 
	 * @return boolean Return true if there is valid fly and capture for the king
	 */
	public boolean canFlyCapture()
	{
		boolean status1, status2, status3, status4;
		
		if (!crowned)
			return false;
		
		setRisk(INITIAL_RISK); // initialize risk to high number 
		setTgtRow(-1);
		setTgtCol(-1);
			
		// random generate row and col direction 
		int randRow = ((int) Math.random())%2;
		if (randRow == 0)
			randRow = -1;
		int randCol = ((int) Math.random())%2 ;
		if (randCol == 0)
			randCol = -1;
		
		status1 = canFlyCapture(this, randRow, randCol);
		status2 = canFlyCapture(this, randRow*(-1), randCol);
		status3 = canFlyCapture(this, randRow, randCol*(-1));
		status4 = canFlyCapture(this, randRow*(-1), randCol*(-1));
		
		return (status1 || status2 || status3 || status4);		
	}
		
	/**
	 * Check if the king can fly to a destination and capture any opponent. The risk of the fly capture will be computed.
	 * 
	 * @param piece Piece instance
	 * @param rowDir The row direction of the flycapture
	 * @param colDir The Column direction of the flycapture
	 * @return boolean Return true if there is valid flycapture for the king
	 */
	public boolean canFlyCapture(CheckerPiece piece, int rowDir, int colDir)
	{
		CheckerBoard board = Checker.getBoard();
		int srcRow = piece.getRow();
		int srcCol = piece.getCol();
		int row = srcRow+rowDir;
		int col = srcCol+colDir;
		boolean detectOpponent = false;
		
		while (board.isTileInBound(row,  col))
		{
			// detect own piece along the fly, cannot capture
			if (board.isTileOccupiedByPlayer(row, col, piece.getColor()))
				return false;
						
			if (board.isTileOccupiedByPlayer(row, col, Checker.getOpponentPlayer()))
			{
				detectOpponent = true;
				break;
			}
			row+=rowDir;
			col+=colDir;
		}
		
		// no opponent piece is detected
		if (!detectOpponent)
			return false;
		
		// detect opponent piece, check if valid capture can happen and the low risk location to land after capture
		row = row+rowDir;
		col = col+colDir;
		int count = 0;
		int risk;
		
		while (board.isTileFree(row,  col))
		{
			count++;
			
			risk = board.computeActionRisk(piece, row, col);
			if (risk < piece.getRisk()) 
			{
				piece.setRisk(risk);
				piece.setTgtRow(row);
				piece.setTgtCol(col);
			}
			row+=rowDir;
			col+=colDir;
		}
		
		if (count > 0)
		{
			System.out.printf(colorCode + "canFlyCapture: piece (%d,%d) best attempt to (%d,%d), moveRisk %d\n", srcRow, srcCol, piece.getTgtRow(), piece.getTgtCol(), piece.getRisk());
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Check if the king can fly to a destination and capture an opponent.
	 * 
	 * @param dstRow The row number of the destined tile where the king is flied to
	 * @param dstCol The column number of the destined tile where the piece is flied to
	 * @return boolean Return true if there is valid flycapture for the king
	 */
	public boolean canFlyCapture(int dstRow, int dstCol)
	{
		CheckerBoard board = Checker.getBoard();
		int srcRow = this.row;
		int srcCol = this.col;
		
		if (!this.crowned)
			return false;
		
		// check if the fly is diagonally up or down
		if (Math.abs(dstRow-srcRow) != Math.abs(dstCol-srcCol))
			return false;
		
		int rowDir = (dstRow - srcRow)/Math.abs(dstRow-srcRow);
		int colDir = (dstCol - srcCol)/Math.abs(dstCol-srcCol);
		int row = srcRow+rowDir;
		int col = srcCol+colDir;
		boolean opponentDetected = false;
		
		while (row != dstRow)
		{
			// detect own piece along the fly, cannot capture
			if (board.isTileOccupiedByPlayer(row, col, this.color))
				return false;
			
			if (board.isTileOccupiedByPlayer(row, col, Checker.getOpponentPlayer()))
			{
				opponentDetected = true;
				break;
			}
			
			row+=rowDir;
			col+=colDir;
		}
		
		if (opponentDetected && board.isTileFree(row+rowDir, col+colDir))
		{
			System.out.printf(colorCode + "canFlyCapture: piece (%d,%d) can flycapture opponent at (%d,%d)!!! \n", srcRow, srcCol, row,col);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Check if the king can fly in any direction
	 * 
	 * @return boolean Return true if there is valid fly for the king
	 */
	public boolean canFly()
	{
		boolean status1, status2, status3, status4;
		
		if (!crowned)
			return false;
		
		setRisk(INITIAL_RISK);// initialize risk to high number 
		setTgtRow(-1);
		setTgtCol(-1);
		
		// random generate row and col direction 
		int randRow = ((int) Math.random())%2;
		if (randRow == 0)
			randRow = -1;
		int randCol = ((int) Math.random())%2;
		if (randCol == 0)
			randCol = -1;
		
		status1 = canFly(this, randRow, randCol);
		status2 = canFly(this, randRow*(-1), randCol);
		status3 = canFly(this, randRow, randCol*(-1));
		status4 = canFly(this, randRow*(-1), randCol*(-1));
		
		return (status1 || status2 || status3 || status4);
	}
	
	/**
	 * Check if the king can fly to a destination 
	 * 
	 * @param dstRow The row number of the destined tile where the king is flied to
	 * @param dstCol The column number of the destined tile where the piece is flied to
	 * @return boolean Return true if there is valid fly for the king
	 */
	public boolean canFly(int dstRow, int dstCol)
	{
		CheckerBoard board = Checker.getBoard();
		int srcRow = this.row;
		int srcCol = this.col;
		
		if (!this.crowned)
			return false;
		
		// check if the fly is diagonally up or down
		if (Math.abs(dstRow-srcRow) != Math.abs(dstCol-srcCol))
			return false;
		
		int rowDir = (dstRow - srcRow)/Math.abs(dstRow-srcRow);
		int colDir = (dstCol - srcCol)/Math.abs(dstCol-srcCol);
		int row = srcRow+rowDir;
		int col = srcCol+colDir;
		
		while (row != dstRow)
		{
			if (!board.isTileFree(row,col))
				return false;
			row+=rowDir;
			col+=colDir;
		}
		System.out.printf(colorCode + "canFly: piece (%d,%d) can fly to (%d,%d) !!! \n", srcRow, srcCol, row,col);
		return true;
	}
	
	/**
	 * Check if the king can fly to a destination and compute the risk of the fly
	 * 
	 * @param piece Piece instance
	 * @param rowDir The row direction of the fly
	 * @param colDir The Column direction of the fly 
	 * @return boolean Return true if there is valid fly for the king
	 */
	public boolean canFly(CheckerPiece piece, int rowDir, int colDir)
	{
		CheckerBoard board = Checker.getBoard();
		int srcRow = piece.getRow();
		int srcCol = piece.getCol();
		int row = srcRow+rowDir;
		int col = srcCol+colDir;
		int risk;
		boolean fly = false;
		
		while (board.isTileInBound(row,  col))
		{
			if (board.isTileFree(row,  col))
			{
				risk = board.computeActionRisk(piece, row, col);
				if (risk < piece.getRisk()) 
				{
					piece.setRisk(risk);
					piece.setTgtRow(row);
					piece.setTgtCol(col);
				}
				row+=rowDir;
				col+=colDir;
				fly = true;
			}
			else
				break;
		}
	
		
		if (fly)
		{
			System.out.printf(colorCode + "canFly: piece (%d,%d) best attempt to (%d,%d), moveRisk %d\n", srcRow, srcCol, piece.getTgtRow(), piece.getTgtCol(), piece.getRisk());
			return true;	
		}
		else
			return false; 
		
	}
	
	/**
	 * Check if the piece can move in any direction
	 * 
	 * @return boolean Return true if there is valid move for the piece
	 */
	public boolean canMove()
	{
		boolean status1, status2, status3, status4;
		
		setRisk(INITIAL_RISK); // initialize risk to high number 
		setTgtRow(-1);
		setTgtCol(-1);
		
		// random generate row and col direction 
		int randRow = ((int) Math.random())%2;
		if (randRow == 0)
			randRow = -1;
		int randCol = ((int) Math.random())%2;
		if (randCol == 0)
			randCol = -1;
			
		int row = this.row+randRow;
		int col = this.col+randCol;
		status1 = canMove(row, col, true);
		
		row = this.row+randRow*(-1);
		status2 = canMove(row, col, true);
		
		col = this.col+randCol*(-1);
		status3 = canMove(row, col, true);
		
		row = this.row+randRow;
		status4 = canMove(row, col, true);
		
		System.out.printf(colorCode + "canMove: piece %s, move status %s, best move (%d,%d), risk %s\n", label, status1 || status2 || status3 || status4, tgtRow, tgtCol, risk);
		return (status1 || status2 || status3 || status4);
		
	}
	
	/**
	 * Check if the piece can move to a destination and compute the risk of the move
	 * 
	 * @param row The row number of the destined tile where the piece is moved to
	 * @param col The column number of the destined tile where the piece is moved to
	 * @param computeRisk Compute the risk of the move if this flag is set 
	 * @return boolean Return true if there is valid move for the piece
	 */
	public boolean canMove(int row, int col, boolean computeRisk)
	{
		CheckerBoard board = Checker.getBoard();
		
		// check out of boundary
		if (row < 0 || row >= CheckerBoard.TILES || col < 0 || col >= CheckerBoard.TILES)
			return false;
		
		int srcRow = this.row;
		int srcCol = this.col;
		
		// if the piece is pre-Selected, must jump, prohibit move
		if (preSelect)
			return false;
		
		// check if the move is diagonally up or down a tile 
		if (Math.abs(row-srcRow)!=1 || Math.abs(col-srcCol)!=1)
			return false;
				
		// check if the move-to tile is free
		if (!board.isTileFree(row,  col))
			return false;
		
		// check if the piece (not king, i.e. not yet crowned) is moved backward 
		if ((!this.crowned && (this.color == Color.orange) && (row > srcRow)) ||
			(!this.crowned && (this.color == Color.white) && (row < srcRow)))
			return false;
	
		if (computeRisk)
		{
			int risk = board.computeActionRisk(this, row, col);
			if (risk < getRisk())
			{
				setRisk(risk);
				setTgtRow(row);
				setTgtCol(col);
			}
		}
		System.out.printf(colorCode + "canMove: piece %s can move to (%d,%d)\n", label, row, col);
		return true;
	}
}
