import java.awt.Color;
import java.awt.event.ActionListener;

/**
 * @author      Chin Kooi Khor (chin.kooi.khor@gmail.com)
 * @version     1.0   
 * @since       24 Jun 2020  
 */
public class CheckerComputerPlayer extends CheckerPlayer implements Runnable 
{
	/**
	 * Constructor of CheckerComputerPlayer class
	 * <p>                           
	 * This constructor will call CheckerPlayer constructor to initialize computer player. It disables action listener for all pieces.
	 * 
	 * @param  color	The color of the pieces	
	 * @param  board    The CheckerBoard instance where the pieces are placed on  
	 * @see CheckerPlayer
	 */
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
	/**
	 * Delay for given time                
	 * 
	 * @param  s Mili-seconds for delay        
	 */
	private void delay(int s)
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
	/**
	 * Perform jump action and capture opponent piece                
	 * 
	 * @param  piece Selected piece for jump        
	 */
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
	
	/**
	 * Perform move action                
	 * 
	 * @param  piece Selected piece for move        
	 */
	public void actionMove(CheckerPiece piece)
	{
		srcActionNotify(piece);
		delay(1000);
		System.out.printf("%s actionMove: piece (%d,%d) moved to (%d,%d), state = %d\n", piece.colorCode, piece.getRow(), piece.getCol(), piece.getTgtRow(), piece.getTgtCol(), getState());
		move(piece);
	}
	
	/**
	 * Perform fly action                
	 * 
	 * @param  king Selected king for fly        
	 */
	public void actionFly(CheckerPiece king)
	{
		srcActionNotify(king);
		delay(1000);
		System.out.printf("%s actionFly: piece (%d,%d) flied to (%d,%d), state = %d\n", king.colorCode, king.getRow(), king.getCol(), king.getTgtRow(), king.getTgtCol(), getState());
		fly(king);
	}
	
	/**
	 * Perform fly and capture opponent piece               
	 * 
	 * @param  king Selected king for fly capture        
	 */
	public void actionFlyCapture(CheckerPiece king)
	{
		srcActionNotify(king);		
		do
		{
			delay(1000);
			System.out.printf("%s actionFlyCapture: piece (%d,%d) flied to (%d,%d), state = %d\n",king.colorCode, king.getRow(), king.getCol(), king.getTgtRow(), king.getTgtCol(), getState());
			flyCapture(king);			
		} while (getState() == STATE_FLIED);
	}
	
	/**
	 * Check all the pieces that have valid jump and flyCapture in the play and saves them to preSelectList ArrayList
	 * <p>
	 * One of these pieces must be selected for next play.                     
	 * 
	 */
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
	
	/**
	 * Check all the pieces that have valid move and fly in the play and saves them to preSelectList ArrayList
	 * <p>
	 * If no piece has valid move or fly, surrender flag will be set.                       
	 * 
	 */
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
						if (piece.getRisk() <= p.getRisk())
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
					System.out.printf("%s checkPlayerPossibleMove: piece (%d,%d) can move to tgt (%d,%d). moveRisk %d\n", colorCode, row, col, piece.getTgtRow(), piece.getTgtCol(),piece.getRisk());
				}
				else
				{
					piece.setNextAction(CheckerPiece.A_FLY);
					System.out.printf("%s checkPlayerPossibleMove: king (%d,%d) can move to tgt (%d,%d). moveRisk %d\n", colorCode, row, col, piece.getTgtRow(), piece.getTgtCol(),piece.getRisk());
				}
			}
		}
		
		if (preSelectList.isEmpty())
		{
			System.out.println(colorCode + "checkPlayerPossibleMove: no piece can move, surrender!!!");
			surrender = true;
		}
			
	}
	
	/**
	 * This method will run to check all possible actions (jump, flycapture, move and fly) and decide the optimal action for next play                      
	 * <p>
	 * Implement Runnable interface to run in a thread. 
	 * 
	 */
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
