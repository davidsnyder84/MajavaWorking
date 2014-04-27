package majava.graphics;



import javax.swing.JFrame;

import java.awt.Color;
import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Toolkit;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.FlowLayout;
import javax.swing.JTextArea;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBox;



public class MajavaWelcome extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 3226158067427833672L;
	
	

	private static final int WINDOW_WIDTH = 500;
	private static final int WINDOW_HEIGHT = 300;
	
	private static final int WINDOW_TOP_BORDER_SIZE = 30;
	private static final int WINDOW_SIDE_BORDER_SIZE = 8;
	private static final int WINDOW_BOTTOM_BORDER_SIZE = 8;
	private static final int WINDOW_MENU_SIZE = 23;
	
	
	private static final Color COLOR_CONTENT_PANE = new Color(210,0, 210, 30);
//	private static final Color COLOR_CONTENT_PANE = new Color(250, 0, 210);
	

	
	private static final String COMMAND_SINGLE_PLAYER = "Single Player";
	private static final String COMMAND_COMPUTERS_PLAYERS_ONLY = "Computer Players Only";
	private static final String COMMAND_QUIT = "Quit";
	private static final String COMMAND_FAST_GAMEPLAY = "Fast";
	
	
	private enum Option {UNDECIDED, SINGLE_PLAYER, COMPUTER_ONLY}
	
	private Option mChosenOption;
	private boolean mFastGameplay;
	
	
	
	
	
	
	
	
	
	public void waitForChoice(){
		while (mChosenOption == Option.UNDECIDED)
			;//intentionally blank
		this.dispose();
	}
	
	public boolean choseSinglePlayer(){return (mChosenOption == Option.SINGLE_PLAYER);}
	
	public boolean choseFastGameplay(){return mFastGameplay;}
	
	
	
	
	
	
	public static void main(String[] args) {
		MajavaWelcome welcomer = new MajavaWelcome();
		welcomer.setVisible(true);
		
		welcomer.waitForChoice();
		
		System.out.println(welcomer.choseSinglePlayer());
	}
	
	
	
	
	
	public MajavaWelcome(){
		
		
		
		final int WINDOW_BOUND_WIDTH = WINDOW_WIDTH + 2*WINDOW_SIDE_BORDER_SIZE;
		final int WINDOW_BOUND_HEIGHT = WINDOW_HEIGHT + WINDOW_TOP_BORDER_SIZE + WINDOW_BOTTOM_BORDER_SIZE + WINDOW_MENU_SIZE;
		
		
		ImageIcon windowIconImg = new ImageIcon(getClass().getResource("/res/img/winds/transE.png"));
		setIconImage(windowIconImg.getImage());
		
		setTitle("Majava Main Menu");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, WINDOW_BOUND_WIDTH, WINDOW_BOUND_HEIGHT);
		
		
		
		
		
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.setBackground(COLOR_CONTENT_PANE);
		
		JLabel lblMajava = new JLabel("MAJAVA");
		lblMajava.setFont(new Font("High Tower Text", Font.PLAIN, 52));
		lblMajava.setHorizontalAlignment(SwingConstants.CENTER);
		lblMajava.setBounds(0, 11, 500, 97);
		contentPane.add(lblMajava);
		
		
		JButton btnSinglePlayerGame = new JButton("Single Player Game");
		btnSinglePlayerGame.setBounds(156, 119, 174, 40);
		btnSinglePlayerGame.setActionCommand(COMMAND_SINGLE_PLAYER);
		btnSinglePlayerGame.addActionListener(this);
		contentPane.add(btnSinglePlayerGame);
		
		JButton btnComputerPlayersOnly = new JButton("Computer Players Only");
		btnComputerPlayersOnly.setBounds(156, 166, 174, 40);
		btnComputerPlayersOnly.setActionCommand(COMMAND_COMPUTERS_PLAYERS_ONLY);
		btnComputerPlayersOnly.addActionListener(this);
		contentPane.add(btnComputerPlayersOnly);
		
		JButton btnQuit = new JButton("Quit");
		btnQuit.setBounds(156, 258, 174, 40);
		btnQuit.setActionCommand(COMMAND_QUIT);
		btnQuit.addActionListener(this);
		contentPane.add(btnQuit);
		
		
		
		
		JLabel lblTori = new JLabel("");
		lblTori.setBounds(351, 102, 137, 190);
		lblTori.setIcon(new ImageIcon(getClass().getResource("/res/img/other/sounotori.png")));
		contentPane.add(lblTori);
		
		
		
		JCheckBox checkboxFastGameplay = new JCheckBox("Fast gameplay");
		checkboxFastGameplay.setBounds(156, 213, 146, 23);
//		checkboxFastGameplay.setBackground(new Color (250, 0, 0));
		checkboxFastGameplay.setOpaque(true);
		contentPane.add(checkboxFastGameplay);
		checkboxFastGameplay.setActionCommand(COMMAND_FAST_GAMEPLAY);
		checkboxFastGameplay.setSelected(false);
		checkboxFastGameplay.addActionListener(this);
		
		

		
		mChosenOption = Option.UNDECIDED;
		mFastGameplay = checkboxFastGameplay.isSelected();
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if (arg0.getActionCommand().equals(COMMAND_SINGLE_PLAYER)) mChosenOption = Option.SINGLE_PLAYER;
		if (arg0.getActionCommand().equals(COMMAND_COMPUTERS_PLAYERS_ONLY)) mChosenOption = Option.COMPUTER_ONLY;
		
		if (arg0.getActionCommand().equals(COMMAND_FAST_GAMEPLAY)) mFastGameplay = !mFastGameplay;
		
		if (arg0.getActionCommand().equals(COMMAND_QUIT))
			System.exit(0);
	}
}