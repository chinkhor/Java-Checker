
import java.util.ArrayList;
import java.util.TimerTask;

public class CheckerTimerTask extends TimerTask
{
	// this timer task will run to blink the pieces in the preSelectList
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
