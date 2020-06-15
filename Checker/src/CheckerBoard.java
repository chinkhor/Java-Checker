import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CheckerBoard extends JPanel
{
	private final int TILES = 8;
	
	public CheckerBoard()
	{
		super();
		setLayout(new GridLayout(TILES, TILES)); 
		setBackground(Color.BLACK);
	}
}
