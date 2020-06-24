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
public class CheckerPiece extends JButton
{
	public String colorCode; // for tracing and debugging use only
	private Color color = Color.WHITE;
	private int pieceX = CheckerTile.TILE_SIZE/4;
	private int pieceY = CheckerTile.TILE_SIZE/4;
	private int pieceSize = CheckerTile.TILE_SIZE/2;
	private int row, col;
	private String label;
	private boolean select = false;
	private boolean preSelect = false;
	private Image img;	
	private boolean crowned = false;
	
	// computer player attributes or flags
	public static final int INITIAL_RISK = 999;
	private int moveRisk = INITIAL_RISK;
	private int nextAction = 0;
	private int tgtRow = -1, tgtCol = -1;
	
	public static final int A_MOVE = 1;
	public static final int A_FLY = 2;
	public static final int A_JUMP = 3;
	public static final int A_FLYCAPTURE = 4;
	public static final int A_DOUBLE_JUMP = 5;
	public static final int A_DOUBLE_FLYCAPTURE = 6;
	// nextAction = 1: move
	// nextAction = 2: fly
	// nextAction = 3: jump
	// nextAction = 4: fly capture
	// nextAction = 5: double/continuous jump
	// nextAction = 6: double/continuous fly capture
	
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

	
	public void setNextAction(int action)
	{
		this.nextAction = action;
	}
	
	public int getNextAction()
	{
		return this.nextAction;
	}
	
	public void setMoveRisk(int risk)
	{
		this.moveRisk = risk;
	}
	
	public int getMoveRisk()
	{
		return moveRisk;
	}
	
	public int getRow()
	{
		return this.row;
	}
	
	public int getCol()
	{
		return this.col;
	}
	
	public void setRow(int row)
	{
		this.row = row;
	}
	
	public void setCol(int col)
	{
		this.col = col;
	}
	
	public int getTgtRow()
	{
		return this.tgtRow;
	}
	
	public int getTgtCol()
	{
		return this.tgtCol;
	}
	
	public void setTgtRow(int row)
	{
		this.tgtRow = row;
	}
	
	public void setTgtCol(int col)
	{
		this.tgtCol = col;
	}
	
	public Color getColor()
	{
		return this.color;
	}
	
	public void setLabel(String str)
	{
		this.label = str;
	}
	
	public String getLabel()
	{
		return this.label;
	}
	
	public boolean isSelect()
	{
		return select;
	}
	
	public boolean getPreSelect()
	{
		return preSelect;
	}
	
	public void setPreSelect(boolean s)
	{
		this.preSelect = s;
	}

	public void select(boolean s)
	{
		this.select = s;
		repaint();
	}
	
	public void setCrown()
	{
		this.crowned = true;
		repaint();
	}
	
	public boolean getCrown()
	{
		return this.crowned;
	}
	
	
	// this method will not find the best or optimal jump yet, it will make the first possible jump
	public boolean canJump()
	{
		boolean status1, status2, status3, status4;

		// random generate row and col direction 
		int randRow = ((int) Math.random())%2;
		if (randRow == 0)
			randRow = -1;
		int randCol = ((int) Math.random())%2 ;
		if (randCol == 0)
			randCol = -1;
				
		status1 = canJump(this.row+2*randRow,this.col+2*randCol);
		if (status1)
		{	
			setTgtRow(this.row+2*randRow);
			setTgtCol(this.col+2*randCol);
			return true;
		}
		
		status2 = canJump(this.row+2*randRow,this.col-2*randCol);
		if (status2)
		{	
			setTgtRow(this.row+2*randRow);
			setTgtCol(this.col-2*randCol);
			return true;
		}
		
		status3 = canJump(this.row-2*randRow,this.col+2*randCol);
		if (status3)
		{	
			setTgtRow(this.row-2*randRow);
			setTgtCol(this.col+2*randCol);
			return true;
		}	
		
		status4 = canJump(this.row-2*randRow,this.col-2*randCol);
		if (status4)
		{	
			setTgtRow(this.row-2*randRow);
			setTgtCol(this.col-2*randCol);
			return true;
		}	
		
		return false;

		
	}
	
	public boolean canJump(int row, int col)
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
			System.out.printf(colorCode + "canJump(): piece %s can jump to (%d,%d) and capture (%d,%d)\n", label, row,col,midRow,midCol);
			return true;
		}
		else
			return false;

	}
	
	public boolean canFlyCapture()
	{
		int risk = INITIAL_RISK; // initialize risk to high number 
		boolean status1, status2, status3, status4;
		
		if (!crowned)
			return false;
		
		setMoveRisk(risk);
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
		int risk = INITIAL_RISK;

		while (board.isTileFree(row,  col))
		{
			count++;
			
			risk = board.computeFlyRiskToBeCaptured(piece, row, col, rowDir, colDir);
			if (piece.getMoveRisk() == INITIAL_RISK)
			{
				piece.setMoveRisk(risk);
				piece.setTgtRow(row);
				piece.setTgtCol(col);
			}
			else if (risk < piece.getMoveRisk()) 
			{
				piece.setMoveRisk(risk);
				piece.setTgtRow(row);
				piece.setTgtCol(col);
			}
			row+=rowDir;
			col+=colDir;
		}
		
		if (count > 0)
		{
			System.out.printf(colorCode + "canFlyCapture: piece (%d,%d) best attempt to (%d,%d), moveRisk %d\n", srcRow, srcCol, piece.getTgtRow(), piece.getTgtCol(), piece.getMoveRisk());
			return true;
		}
		else
			return false;
	}
	
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
	
	public boolean canFly()
	{
		boolean status1, status2, status3, status4;
		
		if (!crowned)
			return false;
		
		setMoveRisk(INITIAL_RISK);// initialize risk to high number 
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
				risk = board.computeFlyRiskToBeCaptured(piece, row, col, rowDir, colDir);
				if (piece.getMoveRisk() == INITIAL_RISK)
				{
					piece.setMoveRisk(risk);
					piece.setTgtRow(row);
					piece.setTgtCol(col);
				}
				else if (risk < piece.getMoveRisk()) 
				{
					piece.setMoveRisk(risk);
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
			System.out.printf(colorCode + "canFly: piece (%d,%d) best attempt to (%d,%d), moveRisk %d\n", srcRow, srcCol, piece.getTgtRow(), piece.getTgtCol(), piece.getMoveRisk());
			return true;	
		}
		else
			return false; 
		
	}
	
	public boolean canMove()
	{
		boolean status1, status2, status3, status4;
		
		setMoveRisk(INITIAL_RISK); // initialize risk to high number 
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
		
		System.out.printf(colorCode + "canMove: piece %s, move status %s, best move (%d,%d), risk %s\n", label, status1 || status2 || status3 || status4, tgtRow, tgtCol, moveRisk);
		return (status1 || status2 || status3 || status4);
		
	}
	
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
			int risk = board.computeMoveRisk(this, row, col);
			if (risk < getMoveRisk())
			{
				setMoveRisk(risk);
				setTgtRow(row);
				setTgtCol(col);
			}
		}
		System.out.printf(colorCode + "canMove: piece %s can move to (%d,%d)\n", label, row, col);
		return true;
	}
}
