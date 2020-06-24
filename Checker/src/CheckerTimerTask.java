
import java.util.ArrayList;
import java.util.TimerTask;

/**
 * @author      Chin Kooi Khor (chin.kooi.khor@gmail.com)
 * @version     1.0   
 * @since       24 Jun 2020  
 */
public class CheckerTimerTask extends TimerTask
{
	
	/**
	 * This method will blink all the pieces stored in preSelectList for player's attention 
	 */
	public void run()
	{
		CheckerPlayer player = Checker.getPlayer(Checker.getCurrentPlayer());
		ArrayList<CheckerPiece> preSelectList = player.getPreSelectArrayList();
				
		if (!preSelectList.isEmpty())
		{
			for (CheckerPiece piece : preSelectList)
			{
				piece.setVisible(false);;	
			}
			try
			{
				Thread.sleep(100);
			}
			catch(InterruptedException ex)
			{
				Thread.currentThread().interrupt();
			}
				
			for (CheckerPiece piece : preSelectList)
			{
				piece.setVisible(true);
			}
		}
			
	}
}
