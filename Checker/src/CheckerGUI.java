import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class CheckerGUI 
{
	public CheckerGUI()
	{
		// default frame layout is BorderLayout
		JFrame frame = new JFrame("Checker");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout()); // good practice to set layout explicitly
		
		JLabel label = new JLabel(" Current Player: ORANGE  ", SwingConstants.RIGHT);
		frame.add (label, BorderLayout.NORTH);

		CheckerBoard board = new CheckerBoard();
		frame.add(board, BorderLayout.CENTER);
		
		CheckerPlayer playerOrange = new CheckerPlayer(Color.ORANGE, board);
		CheckerPlayer playerWhite = new CheckerPlayer(Color.WHITE, board);
		
		//frame.setSize(800, 800);
		frame.pack(); // use the size specified by the components, and pack them.	
		frame.setVisible(true);
	}

	public static void main(String[] args) 
	{
		new CheckerGUI();
	}

}
