import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Checker
{
	private static Color currentPlayer, opponentPlayer;
	private static CheckerBoard board;
	private static JLabel label;
	private static CheckerPlayer playerOrange;
	private static CheckerComputerPlayer playerWhite;
	private static JFrame frame;
	private static boolean computerPlayer = false;
	
	public Checker()
	{
		// default frame layout is BorderLayout
		frame = new JFrame("Checker");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout()); // good practice to set layout explicitly
		
		board = new CheckerBoard();
		frame.add(board, BorderLayout.CENTER);
				
		label = new JLabel("Current Player: ORANGE ", SwingConstants.RIGHT);
		Font font = new Font("Courier", Font.BOLD, 16);
		label.setFont(font);
		label.setPreferredSize(new Dimension(CheckerTile.TILE_SIZE*3,30));
		
		frame.add (label, BorderLayout.NORTH);

		currentPlayer = Color.ORANGE;
		playerOrange = new CheckerPlayer(currentPlayer, board);
		opponentPlayer = Color.WHITE;
		playerWhite = new CheckerComputerPlayer(opponentPlayer, board);
		computerPlayer = true;
		
		//frame.setSize(800, 800);
		frame.pack(); // use the size specified by the components, and pack them.	
		frame.setVisible(true);
		
	}

	public static void restartChecker()
	{	
		/* alternative method
		frame.remove(board);
		board = new CheckerBoard();
		frame.add(board, BorderLayout.CENTER);
		
		label.setText("Current Player: ORANGE ");
		currentPlayer = Color.ORANGE;
		playerOrange = new CheckerPlayer(currentPlayer, board);
		opponentPlayer = Color.WHITE;
		playerWhite = new CheckerPlayer(opponentPlayer, board);
		frame.pack();
		frame.repaint();
		*/
		frame.setVisible(false);
		frame.dispose();
		new Checker();
	}
	
	public static void startNewGame()
	{
		JPanel panel = new JPanel(new BorderLayout());
		frame.add(panel, BorderLayout.SOUTH);
		
		JLabel label = new JLabel("Start New Game?", SwingConstants.RIGHT);
		Font font = new Font("Courier", Font.BOLD, 16);
		label.setFont(font);
		label.setForeground(Color.RED);
		label.setPreferredSize(new Dimension(CheckerTile.TILE_SIZE*(CheckerBoard.TILES-1),30));
		panel.add(label, BorderLayout.WEST);
		
		JButton yes = new JButton("Yes");
		yes.setPreferredSize(new Dimension(50,30));
		panel.add(yes, BorderLayout.CENTER);
		yes.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					restartChecker();
					frame.remove(panel);
					frame.repaint();
				}
			});
		
		JButton no = new JButton("No");
		no.setPreferredSize(new Dimension(50,30));
		panel.add(no, BorderLayout.EAST);
		no.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					System.out.println("Quit Checker. Thank for Playing");
					System.exit(0);
				}
			});
		
		frame.pack();
		frame.repaint();
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
		{	
			if (!playerOrange.getPieceArrayList().isEmpty())
			{
				label.setText("Current Player: ORANGE ");
				playerOrange.checkPlayerPossibleCapture();
				if (playerOrange.getPreSelectArrayList().isEmpty())
				{
					// check if player is running out of move
					playerOrange.checkPlayerPossibleMove();
					playerOrange.clrPreSelection(); // clear preSelectList here to allow player to move
				}
				if (playerOrange.getSurrender())
				{
					label.setText("Player WHITE won !!!");
					startNewGame();
				}
			}
			else
			{
				label.setText("Player WHITE won !!!");
				startNewGame();
			}
		}
		else
		{
			if (computerPlayer)
			{	
				label.setText("Current Player: WHITE  ");
				Thread t1 =new Thread(playerWhite);  
				t1.start();  
			}
			else 
			{
				if (!playerWhite.getPieceArrayList().isEmpty())
				{
					label.setText("Current Player: WHITE  ");
					playerWhite.checkPlayerPossibleCapture();
					
					if (playerWhite.getPreSelectArrayList().isEmpty())
					{
						// check if player is running out of move
						playerWhite.checkPlayerPossibleMove();
						playerWhite.clrPreSelection(); // clear preSelectList here to allow player to move
					}
					if (playerWhite.getSurrender())
					{
						label.setText("Player ORANGE won !!!");
						startNewGame();
					}
					
				}
			}
			
			if (playerWhite.getPieceArrayList().isEmpty() || playerWhite.getSurrender())
			{
				label.setText("Player ORANGE won !!!");
				startNewGame();
			}
		}
	}
	
	public static CheckerPlayer getPlayer(Color color)
	{
		if (color == Color.ORANGE)
			return playerOrange;
		else
			return playerWhite;
	}
	
	public static CheckerBoard getBoard()
	{
		return board;
	}
	
	public static void main(String[] args) 
	{
		new Checker();
	}

}
