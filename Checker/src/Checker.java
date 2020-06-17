import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Checker 
{
	private static Color currentPlayer, opponentPlayer;
	private static CheckerBoard board;
	private static JLabel label;
	private static CheckerPlayer playerOrange, playerWhite;
	
	public Checker()
	{
		// default frame layout is BorderLayout
		JFrame frame = new JFrame("Checker");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout()); // good practice to set layout explicitly
		
		board = new CheckerBoard();
		frame.add(board, BorderLayout.CENTER);
				
		label = new JLabel("Current Player: ORANGE ", SwingConstants.RIGHT);
		frame.add (label, BorderLayout.NORTH);

		currentPlayer = Color.ORANGE;
		playerOrange = new CheckerPlayer(currentPlayer, board);
		opponentPlayer = Color.WHITE;
		playerWhite = new CheckerPlayer(opponentPlayer, board);
		
		//frame.setSize(800, 800);
		frame.pack(); // use the size specified by the components, and pack them.	
		frame.setVisible(true);
	}

	public static Color getCurrentPlayer()
	{
		return currentPlayer;
	}
	
	public static Color getOpponentPlayer()
	{
		return opponentPlayer;
	}
	
	public static void turnOver()
	{
		Color tmp = currentPlayer;
		
		currentPlayer = opponentPlayer;
		opponentPlayer = tmp;
		
		if (currentPlayer == Color.ORANGE)
			label.setText("Current Player: ORANGE ");
		else
			label.setText("Current Player: WHITE  ");
	}
	
	public static CheckerPlayer getPlayer(Color color)
	{
		if (color == Color.ORANGE)
			return playerOrange;
		else
			return playerWhite;
	}
	
	public static void main(String[] args) 
	{
		new Checker();
	}

}
