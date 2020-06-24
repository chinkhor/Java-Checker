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
	
	public void actionJump(CheckerPiece piece)
	{
		srcActionNotify(piece);		
		do
		{
			delay(1000);
			System.out.printf("%s actionJump: piece (%d,%d) jumped to (%d,%d), state = %d\n",piece.colorCode, piece.getRow(), piece.getCol(), piece.getTgtRow(), piece.getTgtCol(), getState());
			jump(piece);			
		} while (getState() == STATE_JUMPED);
	
	}
	
	public void actionMove(CheckerPiece piece)
	{
		srcActionNotify(piece);
		delay(1000);
		System.out.printf("%s actionMove: piece (%d,%d) moved to (%d,%d), state = %d\n", piece.colorCode, piece.getRow(), piece.getCol(), piece.getTgtRow(), piece.getTgtCol(), getState());
		move(piece);
	}
	
	public void actionFly(CheckerPiece piece)
	{
		srcActionNotify(piece);
		delay(1000);
		System.out.printf("%s actionFly: piece (%d,%d) flied to (%d,%d), state = %d\n", piece.colorCode, piece.getRow(), piece.getCol(), piece.getTgtRow(), piece.getTgtCol(), getState());
		fly(piece);
	}
	
	public void actionFlyCapture(CheckerPiece piece)
	{
		srcActionNotify(piece);		
		do
		{
			delay(1000);
			System.out.printf("%s actionFlyCapture: piece (%d,%d) flied to (%d,%d), state = %d\n",piece.colorCode, piece.getRow(), piece.getCol(), piece.getTgtRow(), piece.getTgtCol(), getState());
			flyCapture(piece);			
		} while (getState() == STATE_FLIED);
	}
	
	// override CheckerPlayer's checkPlayerPossibleCapture()
	public void checkPlayerPossibleCapture()
	{		
		// check any piece have capture possibility in next action
		for (CheckerPiece piece : pieces)
		{
			int row = piece.getRow();
			int col = piece.getCol();
			
			if (piece.canFlyCapture())
			{
				// save the crowned king piece with capture possibility to preSelectList
				preSelectList.add(0, piece);
				piece.setNextAction(CheckerPiece.A_FLYCAPTURE);
				System.out.println(colorCode + "checkPlayerPossibleCapture: king (" + row + "," + col + ") can capture. ");
			}
			else if (piece.canJump())
			{
				// save the piece with capture possibility to preSelectList
				preSelectList.add(piece);
				piece.setNextAction(CheckerPiece.A_JUMP);
				System.out.println(colorCode + "checkPlayerPossibleCapture (computer): piece (" + row + "," + col + ") can capture. ");
			}
		}
		
	}
	
	// override CheckerPlayer's checkPlayerPossibleMove()
	public void checkPlayerPossibleMove()
	{			
		// check any piece have move possibility in next action
		for (CheckerPiece piece : pieces)
		{
			int row = piece.getRow();
			int col = piece.getCol();
				
			if (piece.canFly() || piece.canMove())
			{
				// save the piece with move possibility to preSelectList, sorted with moveRisk value. Lowest value (0, followed by 1, 2, 3) is at the head
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
					System.out.printf("%s checkPlayerPossibleMove: piece (%d,%d) can move to tgt (%d,%d). moveRisk %d\n", colorCode, row, col, piece.getTgtRow(), piece.getTgtCol(),piece.getMoveRisk());
				}
				else
				{
					piece.setNextAction(CheckerPiece.A_FLY);
					System.out.printf("%s checkPlayerPossibleMove: king (%d,%d) can move to tgt (%d,%d). moveRisk %d\n", colorCode, row, col, piece.getTgtRow(), piece.getTgtCol(),piece.getMoveRisk());
				}
			}
		}
		
		if (preSelectList.isEmpty())
		{
			System.out.println(colorCode + "checkPlayerPossibleMove: no piece can move, surrender!!!");
			surrender = true;
		}
			
	}
	
	public void run()
	{
		System.out.println(colorCode + "run(): ");
		checkPlayerPossibleCapture();
		
		if (preSelectList.isEmpty())
		{
			checkPlayerPossibleMove();
		}
		
		System.out.print(colorCode + "preSelectList: ");
		for (CheckerPiece p: preSelectList)
		{
			System.out.print(p.getLabel() + " ");
		}
		System.out.println("");
		
		if (!preSelectList.isEmpty())
		{
			CheckerPiece piece = preSelectList.get(0);
			
			switch (piece.getNextAction())
			{
				case CheckerPiece.A_MOVE:
				{
					actionMove(piece);
					break;
				}
				case CheckerPiece.A_FLY:
				{
					actionFly(piece);
					break;
				}
				case CheckerPiece.A_JUMP:
				{
					actionJump(piece);
					break;
				}
					
				case CheckerPiece.A_FLYCAPTURE:
				{
					actionFlyCapture(piece);
					break;
				}					
				
				default:
					System.out.println(colorCode + "Computer run: piece (" + piece.getRow() + "," + piece.getCol() + ") has no valid next action");
			}
		}
		else
			actionComplete();
		
	}
}
