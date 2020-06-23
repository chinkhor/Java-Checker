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
	private int nextAction = 0;
	private int tgtRow = -1, tgtCol = -1;
	private int moveRisk = -1;
	
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
	
/*	public boolean canFlyCapture()
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
	
	
	
	public boolean canFly()
	{
		CheckerBoard board = Checker.getBoard();
		
		if (!computerMode)
		{
			// force to check all possible moves (to compute the move risk)
			if (this.color == Color.ORANGE)
			{
				return (board.moveValid(this.row, this.col, -1, 1) ||
						board.moveValid(this.row, this.col, -1, -1) || 
						board.moveValid(this.row, this.col, 1, 1) ||
						board.moveValid(this.row, this.col, 1, -1));
			}
		}
		else
		{
			int risk = 5;
			boolean status1 = false; 
			boolean status2 = false;
			
			setMoveRisk(-1); // initialize moveRisk
			status1 = board.moveValid(this, 1, 1);
			if (status1) // move to +1,+1 direction is valid
			{
				risk = getMoveRisk();
				setTgtRow(this.row+1);
				setTgtCol(this.col+1);

			}

			setMoveRisk(-1); // initialize moveRisk
			status2 = board.moveValid(this, 1, -1);
			
			if (status2) // move to +1,-1 direction is valid
			{
				// change tgtRow and tgtCol to this direction if the risk is lower 
				if (getMoveRisk() < risk)
				{
					setTgtRow(row+1);
					setTgtCol(col-1);
				}
				else
					setMoveRisk(risk);
			}
			else 
			{
				if (status1)
					setMoveRisk(risk);
			}
			
			return status1 || status2;
		}
	}
*/
	

	// this method will not find the best or optimal jump yet, it will make the first possible jump
	public boolean canJump()
	{
		boolean status1, status2, status3, status4;
		
		// 1,1 direction
		status1 = canJump(this.row+2,this.col+2);
		if (status1)
		{	
			setTgtRow(this.row+2);
			setTgtCol(this.col+2);
			return true;
		}
		
		// 1,-1 direction
		status2 = canJump(this.row+2,this.col-2);
		if (status2)
		{	
			setTgtRow(this.row+2);
			setTgtCol(this.col-2);
			return true;
		}
		
		// -1,1 direction
		status3 = canJump(this.row-2,this.col+2);
		if (status3)
		{	
			setTgtRow(this.row-2);
			setTgtCol(this.col+2);
			return true;
		}	
		
		// -1,-1 direction
		status4 = canJump(this.row-2,this.col-2);
		if (status4)
		{	
			setTgtRow(this.row-2);
			setTgtCol(this.col-2);
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
			System.out.printf("piece (%d,%d) is free, piece(%d,%d)'s color is %s\n", row,col,midRow,midCol,Checker.getOpponentPlayer());
			return true;
		}
		else
			return false;

	}
	
	public boolean canFly()
	{
		int risk = 999; // initialize risk to high number 
		boolean status1, status2, status3, status4;
		
		if (!crowned)
			return false;
		
		setMoveRisk(risk);
		setTgtRow(-1);
		setTgtCol(-1);
		
		// random generate row and col direction 
		int randRow = ((int) Math.random())%2 - 1 ;
		if (randRow == 0)
			randRow = 1;
		int randCol = ((int) Math.random())%2 - 1 ;
		if (randCol == 0)
			randCol = 1;
		
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
		
		// check out of boundary
		if (!board.isTileInBound(dstRow, dstCol))
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
		System.out.printf("canFly: piece (%d,%d) can fly to (%d,%d) !!! \n", srcRow, srcCol, row,col);
		return true;
	}
		
	public boolean canFly(CheckerPiece piece, int rowDir, int colDir)
	{
		CheckerBoard board = Checker.getBoard();
		int srcRow = piece.getRow();
		int srcCol = piece.getCol();
		int row = srcRow+rowDir;
		int col = srcCol+colDir;
		int risk = 999;
		boolean first = true;
		
		while (board.isTileInBound(row,  col))
		{
			if (board.isTileFree(row,  col))
			{
				risk = board.computeFlyRiskToBeCaptured(piece, row, col, rowDir, colDir);
				if (first)
				{
					piece.setMoveRisk(risk);
					piece.setTgtRow(row);
					piece.setTgtCol(col);
					first = false;
				}
				else if (risk < piece.getMoveRisk()) 
				{
					risk = board.computeFlyRiskToBeCaptured(piece, row, col, rowDir, colDir);
					piece.setMoveRisk(risk);
					piece.setTgtRow(row);
					piece.setTgtCol(col);
				}
				row+=rowDir;
				col+=colDir;
			}
			else
				break;
		}
	
		
		if (first)
			return false;
		else 
		{
			System.out.printf("canFly: piece (%d,%d) best attempt to (%d,%d), moveRisk %d\n", srcRow, srcCol, piece.getTgtRow(), piece.getTgtCol(), piece.getMoveRisk());
			return true;	
		}
	}
	
	public int computeMoveRisk(int rowDir, int colDir)
	{
		// if the move to king row, no risk to be captured, and it should be priority move
		if (this.row+rowDir == Checker.getPlayer(Checker.getCurrentPlayer()).getKingRow() && !this.crowned)
		{
			return 0;
		}
		else
		{
			CheckerBoard board = Checker.getBoard();
			int risk = board.computeRiskToBeFlyCaptured(this, this.row+rowDir, this.col+colDir);
			if (risk < 3) // no capture by a king
			{
				risk = board.computeRiskToBeCaptured(this, this.row+rowDir, this.col+colDir);
			}
			return risk;
		}	

	}
	
	public boolean canMove()
	{
		int risk = 999; // initialize risk to high number 
		boolean status1, status2, status3, status4;
		
		setMoveRisk(risk);
		// 1,1 direction
		status1 = canMove(this.row+1,this.col+1);
		if (status1)
		{	
			risk = computeMoveRisk(1,1);
			setMoveRisk(risk);
			setTgtRow(this.row+1);
			setTgtCol(this.col+1);
		}
		
		// 1,-1 direction
		status2 = canMove(this.row+1,this.col-1);
		if (status2)
		{	
			risk = computeMoveRisk(1,-1);
			if (risk < getMoveRisk())
			{
				setMoveRisk(risk);
				setTgtRow(this.row+1);
				setTgtCol(this.col-1);
			}
		}
		
		// -1,1 direction
		status3 = canMove(this.row-1,this.col+1);
		if (status3)
		{	
			risk = computeMoveRisk(-1,1);
			if (risk < getMoveRisk())
			{
				setMoveRisk(risk);
				setTgtRow(this.row-1);
				setTgtCol(this.col+1);
			}
		}	
		
		// -1,-1 direction
		status4 = canMove(this.row-1,this.col-1);
		if (status4)
		{	
			risk = computeMoveRisk(-1,-1);
			if (risk < getMoveRisk())
			{
				setMoveRisk(risk);
				setTgtRow(this.row-1);
				setTgtCol(this.col-1);
			}
		}	
		
		return (status1 || status2 || status3 || status4);
		
	}
	
	public boolean canMove(int row, int col)
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
		
		return true;
	}
}
