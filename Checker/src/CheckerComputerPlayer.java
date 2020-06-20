import java.awt.Color;
import java.awt.event.ActionListener;

public class CheckerComputerPlayer extends CheckerPlayer implements Runnable 
{
	public CheckerComputerPlayer(Color color, CheckerBoard board)
	{
		super(color, board);
		
		// remove Actionlistener (user triggered button click for piece action)
		// computer player will move without user action
		for (CheckerPiece piece : pieces)
		{
			if(piece.getActionListeners().length > 0) 
			{
		        for(ActionListener g : piece.getActionListeners()) {
		            piece.removeActionListener(g);
		        }
		    }
		}
	}
	
	// override CheckerPlayer's checkPlayerPossibleCapture()
	public void checkPlayerPossibleCapture()
	{
		CheckerBoard board = Checker.getBoard();
		
		// check any piece have capture possibility in next action
		for (CheckerPiece piece : pieces)
		{
			int row = piece.getRow();
			int col = piece.getCol();
			
			if (!piece.getCrown() && board.canJumpCapture(piece.getColor(), row, col))
			{
				// save the piece with capture possibility to preSelectList
				preSelectList.add(piece);
				piece.setNextAction(CheckerPiece.A_JUMP);
				System.out.println("checkPlayerPossibleCapture: piece (" + row + "," + col + ") can capture. ");
			}
			else if (piece.getCrown() && board.canFlyCapture(piece.getColor(), row, col))
			{
				// save the crowned king piece with capture possibility to preSelectList
				preSelectList.add(0, piece);
				piece.setNextAction(CheckerPiece.A_FLYCAPTURE);
				System.out.println("checkPlayerPossibleCapture: king (" + row + "," + col + ") can capture. ");
			}
		}
		
	}
	
	public void delay(int s)
	{
		try
		{
			Thread.sleep(s);
		}
		catch(InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}
	
	public void actionJump(CheckerPiece piece, int srcRow, int srcCol)
	{
		CheckerBoard board = Checker.getBoard();
		srcActionNotify(piece);
		
		int row = srcRow;
		int col = srcCol;		
		do
		{
			delay(1000);
			if (board.jumpValid(piece, row, col, 1, 1) || board.jumpValid(piece, row, col, 1, -1))
			{
				row = piece.getTgtRow();
				col = piece.getTgtCol();
				jump(piece, row, col);
				
				System.out.printf("actionJump: piece (%d,%d) jumped to (%d,%d), state = %d\n",srcRow, srcCol, row, col, getState());
			}
			else
				System.out.println("actionJump: piece (" + row + "," + col + ") has no valid jump or further jump");
			
		} while (getState() == STATE_JUMPED);
	
	}
	
	public void actionMove(CheckerPiece piece, int srcRow, int srcCol)
	{
		srcActionNotify(piece);
		
		delay(1000);
		
		int row = piece.getTgtRow();
		int col = piece.getTgtCol();
		move(piece, row, col);
		
		System.out.printf("actionMove: piece (%d,%d) moved to (%d,%d), state = %d\n",srcRow, srcCol, row, col, getState());
		
	}
	
	public void actionFly(CheckerPiece piece, int srcRow, int srcCol)
	{
		CheckerBoard board = Checker.getBoard();
		srcActionNotify(piece);
		
		delay(1000);
		if (board.moveValid(piece, srcRow, srcCol, 1, 1) || board.moveValid(piece, srcRow, srcCol, 1, -1) || 
			board.moveValid(piece, srcRow, srcCol, -1, 1) || board.moveValid(piece, srcRow, srcCol, -1, -1))
		{	
			int row = piece.getTgtRow();
			int col = piece.getTgtCol();
			move(piece, row, col);
			
			System.out.printf("actionFly: piece (%d,%d) flied to (%d,%d), state = %d\n",srcRow, srcCol, row, col, getState());
		}		
		else
		{
			System.out.println("actionFly: piece (" + srcRow + "," + srcCol + ") has no valid fly");
		}
		
	}
	
	public void actionFlyCapture(CheckerPiece piece, int srcRow, int srcCol)
	{
		CheckerBoard board = Checker.getBoard();
		srcActionNotify(piece);
		
		int row = srcRow;
		int col = srcCol;		
		do
		{
			delay(1000);
			if (board.flyCaptureValid(piece, row, col, 1, 1) || board.flyCaptureValid(piece, row, col, 1, -1) || 
				board.flyCaptureValid(piece, row, col, -1, 1) || board.flyCaptureValid(piece, row, col, -1, -1))
			{
				row = piece.getTgtRow();
				col = piece.getTgtCol();
				fly(piece, row, col);
				
				System.out.printf("actionFlyCapture: piece (%d,%d) flied to (%d,%d), state = %d\n",srcRow, srcCol, row, col, getState());
			}
			else
				System.out.println("actionFlyCapture: piece (" + row + "," + col + ") has no valid fly capture or further fly capture");
			
		} while (getState() == STATE_FLIED);
		
	}
	
	public void checkPlayerPossibleMove()
	{
		CheckerBoard board = Checker.getBoard();
			
		// check any piece have move possibility in next action
		for (CheckerPiece piece : pieces)
		{
			int row = piece.getRow();
			int col = piece.getCol();
				
			if ((!piece.getCrown() && board.canMove(piece, piece.getColor(), row, col)) || 
				(piece.getCrown() && board.canFly(piece, piece.getColor(), row, col)))
			{
				// save the piece with move possibility to preSelectList, sorted with moveRisk value. Lowest value (-1, followed by 0, 1, 2) is at the head
				if (preSelectList.isEmpty())
					preSelectList.add(piece);
				else
				{
					int index = 0;
					CheckerPiece p;
					boolean addToList = false;
					while (index < preSelectList.size())
					{
						p = preSelectList.get(index);
						if (piece.getMoveRisk() <= p.getMoveRisk())
						{
							preSelectList.add(index, piece);
							addToList = true;
							break;
						}
						index++;
					}
					if (addToList == false)
						preSelectList.add(piece);
				}
				
				if (!piece.getCrown())
				{
					piece.setNextAction(CheckerPiece.A_MOVE);
					System.out.printf("checkPlayerPossibleMove: piece (%d,%d) can move to tgt (%d,%d). moveRisk %d\n", row, col, piece.getTgtRow(), piece.getTgtCol(),piece.getMoveRisk());
				}
				else
				{
					piece.setNextAction(CheckerPiece.A_FLY);
					System.out.println("checkPlayerPossibleMove: king (" + row + "," + col + ") can fly. moveRisk " + piece.getMoveRisk());
				}
			}
		}
		
		if (preSelectList.isEmpty())
		{
			surrender = true;
		}
			
	}
	
	public void run()
	{
		checkPlayerPossibleCapture();
		
		if (preSelectList.isEmpty())
		{
			checkPlayerPossibleMove();
		}
		
		System.out.print("Player White run: ");
		for (CheckerPiece p: preSelectList)
		{
			System.out.print(p.getLabel() + " ");
		}
		System.out.println("");
		
		if (!preSelectList.isEmpty())
		{
			CheckerPiece piece = preSelectList.get(0);
			int row = piece.getRow();
			int col = piece.getCol();
			
			switch (piece.getNextAction())
			{
				case CheckerPiece.A_MOVE:
				{
					actionMove(piece, row, col);
					break;
				}
				case CheckerPiece.A_FLY:
				{
					actionFly(piece, row, col);
					break;
				}
				case CheckerPiece.A_JUMP:
				{
					actionJump(piece, row, col);
					break;
				}
					
				case CheckerPiece.A_FLYCAPTURE:
				{
					actionFlyCapture(piece, row, col);
					break;
				}					
				
				default:
					System.out.println("Computer run: piece (" + row + "," + col + ") has no valid next action");
			}
		}
		System.out.println("Ended Computer's turn");
	}
}
