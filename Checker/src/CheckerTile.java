import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CheckerTile extends JPanel 
{
	private int row;
	private int col;
	
	public CheckerTile(int row, int col)
	{
		super();
		this.row = row;
		this.col = col;
	}
	
	public int getRow()
	{
		return this.row;
	}
	
	public int getCol()
	{
		return this.col;
	}
}
