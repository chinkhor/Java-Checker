import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class CheckerGUI 
{
	public CheckerGUI()
	{
		// default frame layout is BorderLayout
		JFrame frame = new JFrame("Checker");
				
		JLabel label = new JLabel(" Current Player: ORANGE  ", SwingConstants.RIGHT);
		frame.add (label, BorderLayout.NORTH);

		CheckerBoard board = new CheckerBoard();
		frame.add(board, BorderLayout.CENTER);
				
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//frame.setSize(480, 480);
		frame.pack(); // use the size specified by the components, and pack them.
		frame.setVisible(true);
	}
	
	public static void main(String[] args) 
	{
		new CheckerGUI();
	}

}
