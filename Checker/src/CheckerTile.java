import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

@SuppressWarnings("serial")
/**
 * @author      Chin Kooi Khor (chin.kooi.khor@gmail.com)
 * @version     1.0   
 * @since       24 Jun 2020  
 */
public class CheckerTile extends JPanel 
{
	/**
	 * the constant value for the tile size
	 */
	public static final int TILE_SIZE = 100;
	/**
	 * the row number of the tile on the checker board
	 */
	private int row;
	/**
	 * the column number of the tile on the checker board
	 */
	private int col;
	/**
	 * flag to indicate who occupies the tile (Orange or White piece). Black color indicates the tile is not occupied 
	 */
	private Color occupied;
	
	/**
	 * Constructor of CheckerTile class
	 * <p>                           
	 * The constructor will create customized JPanel called tile with specified color and dimension
	 * 
	 * @param  row	The row number of the board	
	 * @param  col	The column number of the board
	 * @param  color The color of the tile 
	 */
	public CheckerTile(int row, int col, Color color)
	{
		super();
		this.row = row;
		this.col = col;
		this.occupied = Color.BLACK; // use black color to indicate unoccupied tile
		this.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
		this.setBackground(color);
		this.setLayout(new BorderLayout());
	}
	
	/**
	 * Setter to set the occupied flag  
	 * 
	 * @param  color Set the occupied flag to the given color
	 */
	public void setOccupied (Color color)
	{
		this.occupied = color;
	}
	
	/**
	 * Getter to get the occupied flag 
	 * 
	 * @return Color Return occupied flag 
	 */
	public Color getOccupied()
	{
		return occupied;
	}
	
	/**
	 * Getter to get the row number where the tile is located on the board 
	 * 
	 * @return int	Return row number of the tile  
	 */
	public int getRow()
	{
		return this.row;
	}
	
	/**
	 * Getter to get the column number where the tile is located on the board 
	 * 
	 * @return int	Return column number of the tile  
	 */
	public int getCol()
	{
		return this.col;
	}
	
	/**
	 * Getter to get instance of the piece occupied the tile 
	 * 
	 * @return CheckerPiece	Return piece instance  
	 */
	public CheckerPiece getPiece()
	{
		CheckerPlayer player = Checker.getPlayer(occupied);
		
		for (CheckerPiece piece: player.getPieceArrayList())
		{
			if (piece.getRow() == row && piece.getCol()==col)
				return piece;
		}
		System.out.println("CheckerTile getPiece couldn't find matching piece");
		return null;
	}
}
