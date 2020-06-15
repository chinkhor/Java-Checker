import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class CheckerGUI 
{

	public static void main(String[] args) 
	{
		// default frame layout is BorderLayout
		JFrame frame = new JFrame("Checker");
		
		JLabel label = new JLabel(" Current Player: ORANGE  ", SwingConstants.RIGHT);
		frame.add (label, BorderLayout.NORTH);

		CheckerBoard board = new CheckerBoard();
		frame.add(board, BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(new Dimension(480, 480));
		frame.setVisible(true);
	}

}
