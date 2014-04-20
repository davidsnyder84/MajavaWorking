package majava;

import java.util.ArrayList;
import java.util.Scanner;

import majava.graphics.TableViewer;

import utility.GenSort;




/*
Class: Table

data:
	p1, p2, p3, p4 - four players. p1 is always east, p2 is always south, etc. 
	mWall - wall of tiles, includes the dead wall
	
	mGameType - length of game being played (single, tonpuusen, or hanchan)
	
	mRoundWind - the prevailing wind of the current round ('E' or 'S')
	mWhoseTurn - whose turn it is (1,2,3,4 corresponds to E,S,W,N)
	mReaction - will be NO_REACTION if no calls were made during a turn, will be something else otherwise
	mGameIsOver - will be true if the game is over, false if not
	mGameResult - the specific result of the game (reason for a draw game, or who won), is UNDECIDED if game is not over
	
methods:
	mutators:
 	
 	accessors:
	
	other:
*/
public class Table {
	
	public static final int NUM_PLAYERS = 4;
	public static final char DEFAULT_ROUND_WIND = 'E';
	
	public static final int NO_REACTION = 0;
	
	public static final int GAME_TYPE_SINGLE = 0;
	public static final int GAME_TYPE_TONPUUSEN = 1;
	public static final int GAME_TYPE_HANCHAN = 2;
	public static final int GAME_TYPE_DEFAULT = GAME_TYPE_SINGLE;
	
	
	//for debug use
	public static final boolean DEBUG_DO_SINGLE_PLAYER_GAME = true;
	public static final boolean DEBUG_SHUFFLE_SEATS = false;
	public static final boolean DEBUG_WAIT_AFTER_COMPUTER = true;
	public static final boolean DEBUG_LOAD_DEBUG_WALL = false;
	
	
	
	
	private Player p1, p2, p3, p4;
	private Player[] mPlayerArray;
	
	private Wall mWall;
	
	private RoundTracker mRoundTracker;
//	public static TableViewer mTviewer;	//will be private
	private TableViewer mTviewer;
	
	
	private char mRoundWind;
	private int mRoundNum;
	private int mRoundBonusNum;
	
//	private int mWhoseTurn;
	private int mReaction;
	
	private int mGameType;
//	private boolean mGameIsOver;
//	private int mGameResult;
	
	
	
	/*
	1-arg Constructor
	initializes a table to make it ready for playing
	
	input: gameType is the length of game that will be played (single, tonpuusen, or hanchan)
	
	creates a player for each seat (4)
	creates the wall
	
	initializes round and game info
	*/
	public Table(int gameType){
		
		//creates a new player to sit at each seat
		p1 = new Player(Player.SEAT_EAST);
		p2 = new Player(Player.SEAT_SOUTH);
		p3 = new Player(Player.SEAT_WEST);
		p4 = new Player(Player.SEAT_NORTH);
//		mPlayerArray[0] = p1;mPlayerArray[1] = p2;mPlayerArray[2] = p3;mPlayerArray[3] = p4;
		mPlayerArray = new Player[]{p1, p2, p3, p4};
		
		//creates the wall
		mWall = new Wall();
		
		//initializes round info
		mRoundWind = DEFAULT_ROUND_WIND;
		mRoundNum = 1;
		mRoundBonusNum = 0;
		
		mReaction = NO_REACTION;
//		mGameIsOver = false;
		
		if (gameType == GAME_TYPE_SINGLE || gameType == GAME_TYPE_TONPUUSEN || gameType == GAME_TYPE_HANCHAN)
			mGameType = gameType;
		else
			mGameType = GAME_TYPE_DEFAULT;
		
		
		//initialize Round Tracker
		mRoundTracker = new RoundTracker(mRoundWind,mRoundNum,mRoundBonusNum,  mWall,  p1,p2,p3,p4);
		
		//initialize Table Viewer
		mTviewer = new TableViewer(mRoundTracker);
		mTviewer.setVisible(true);
		mTviewer.updateEverything();
	}
	//no-arg constuctor, defaults to single round game
	public Table(){
		this(GAME_TYPE_DEFAULT);
	}
	
	
	
	
	/*
	method: play
	plays a new game of mahjong with four new players
	 
	 
	(before play is called)
		note: this is done in the Constructor
		4 players are created (empty seats, not assigned controllers)
		wall/deadwall is set up
		round wind = East
	 (play is called)
	 
	 
	decide seat order
	deal hands
	 
	whoseTurn = 1;
	while (game is not over)
		mReaction = no reaction;
		
		//handle player turns here
	 	if (it is p1's turn && game is not over): do p1's turn
	 	if (it is p2's turn && no calls were made && game is not over): do p2's turn
	 	if (it is p3's turn && no calls were made && game is not over): do p3's turn
	 	if (it is p4's turn && no calls were made && game is not over): do p4's turn


	 	//handle reactions here
	 	if (reaction): handleReaction
	end while
	
	display endgame result
	*/
	public void play()
	{
		Tile discardedTile = null;
		TileList indicators = null;
		
		
		

		//------------------------------------------------DEBUG INFO
		if (DEBUG_LOAD_DEBUG_WALL) mWall.loadDebugWall();
		System.out.println(mWall.toString() + "\n\n\n");mWall.printDoraIndicators();
		//------------------------------------------------DEBUG INFO
		
		
		
		//decide seats
		decideSeats();
		
		
		
		//deal and sort hands
		mWall.dealHands(p1, p2, p3, p4);
		p1.sortHand(); p2.sortHand(); p3.sortHand(); p4.sortHand();
		mTviewer.updateEverything();
		

		//------------------------------------------------DEBUG INFO
		System.out.println(mWall.toString());
		indicators = mWall.getDoraIndicators();

		System.out.println("\nDora indicators:");
		for (Tile t: indicators)
			System.out.println(t.toString());
		
		p1.showHand();p2.showHand();p3.showHand();p4.showHand();
		System.out.println("\n\n\n");
		//------------------------------------------------DEBUG INFO
		
		
		
		mReaction = NO_REACTION;
//		mGameIsOver = false;
		
		//do one round
		while (mRoundTracker.roundIsOver() == false){
			
			//handle player turns
			if (mRoundTracker.whoseTurn() == 1 && !mRoundTracker.roundIsOver())
				discardedTile = doPlayerTurn(p1);
			
			if (mReaction == NO_REACTION && mRoundTracker.whoseTurn() == 2 && !mRoundTracker.roundIsOver())
				discardedTile = doPlayerTurn(p2);
			
			if (mReaction == NO_REACTION && mRoundTracker.whoseTurn() == 3 && !mRoundTracker.roundIsOver())
				discardedTile = doPlayerTurn(p3);
			
			if (mReaction == NO_REACTION && mRoundTracker.whoseTurn() == 4 && !mRoundTracker.roundIsOver())
				discardedTile = doPlayerTurn(p4);
			
			
			//handle reactions here
			if (mReaction != NO_REACTION){
				handleReaction(discardedTile);
			}
			
		}
		
		//display end of round result
		displayRoundResult();
		
	}
	
	
	
	

	
	/*
	method: doPlayerTurn
	handles a player's turn, and the other players' reactions to the player's turn
	
	input: p is the player whose turn it is
	
	returns the tile that the player discarded
	
	
	if (player needs to draw)
		take tile from wall or dead wall depending on what player needs
		if (there were no tiles left in the wall to take)
			gameIsOver = true, result = washout
			return null
		else
			add the tile to the player's hand
		end if
	end if
	
 	discardedTile = player's chosen discard
 	display what the player discarded
 	get the other players' reactions to the discarded tile
 	(the players will "make a call", the call won't actually be handled yet)
 	
	whoseTurn++
	return discardedTile
	*/
	private Tile doPlayerTurn(Player p){

		Tile discardedTile = null;
		Tile drawnTile = null;
		
		//~~~~~~handle drawing a tile
		//if the player needs to draw a tile, draw a tile
		if (p.needsDraw()){
			
			//draw from wall or dead wall, depending on what player needs
			if (p.needsDrawNormal())
				drawnTile = mWall.takeTile();
			else if (p.needsDrawRinshan()){
				drawnTile = mWall.takeTileFromDeadWall();
				mWall.printDoraIndicators();	//debug
			}
			
			if (drawnTile == null){
				System.out.println("-----End of wall reached. Cannot draw tile.");
				mRoundTracker.setResultWashout();
				return null;
			}
			else{
				//add the tile to the player's hand
				p.addTileToHand(drawnTile);
				if (p.controllerIsHuman()) mTviewer.updateEverything();
			}
		}
		
		
		//~~~~~~get player's discard (ankans, riichi, and such are handled inside here)
		discardedTile = p.takeTurn(mTviewer);
		
		//show the human player their hand
		showHandsOfHumanPlayers();
		//show the discarded tile and the discarder's pond
		System.out.println("\n\n\tTiles left: " + mWall.getNumTilesLeftInWall());
		System.out.println("\t" + p.getSeatWind() + " Player's discard: ^^^^^" + discardedTile.toString() + "^^^^^");
		p.showPond();
		mTviewer.updateEverything();
		
		
		
		//~~~~~~get reactions from the other players
		mReaction += p.getShimocha().reactToDiscard(discardedTile, mTviewer);
		mReaction += p.getToimen().reactToDiscard(discardedTile, mTviewer);
		mReaction += p.getKamicha().reactToDiscard(discardedTile, mTviewer);
		
		//pause for dramatic effect
		pauseWait();
		if (mReaction == NO_REACTION){
			pauseWait();
			
			//update turn indicator
			mRoundTracker.nextTurn();
		}
		
		//return the tile that was discarded
		return discardedTile;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	method: handleReaction
	handles a call made on a discarded tile
	
	input: t is the discarded tile
	
	
	priorityCaller = figure out who made the call (and decide priority if >1 calls)
	if (caller called ron)
		handle Ron
	else
		let the caller make the meld with the tile
		show who called the tile, and what they called 
	end if
	
	if (multiple players tried to call)
		display other callers who got bumped by priority
	end if
	
	whoseTurn = priorityCaller's turn
	reaction = NO_REACTION
	*/
	private void handleReaction(Tile discardedTile){
		
		//figure out who called the tile, and if multiple players called, who gets priority
		Player priorityCaller = whoCalled();
		
		//give the caller the discarded tile so they can make their meld
		//if the caller called Ron, handle that instead
		if (priorityCaller.calledRon()){
			System.out.println("\n*****RON! RON RON! RON! RON! ROOOOOOOOOOOOOOOOOOOOOON!");
			//handle here
		}
		else{
			//make the meld
			priorityCaller.makeMeld(discardedTile);
			mTviewer.updateEverything();
			//meld has been made

			//show who called the tile 
			System.out.println("\n*********************************************************");
			System.out.println("**********" + priorityCaller.getSeatWind() + " Player called the tile (" + discardedTile.toString() + ")! " + priorityCaller.getCallStatusString() + "!!!**********");
			System.out.println("*********************************************************");
		}
		
		
		//if multiple players called, show if someone got bumped by priority 
		if (p1.called() && p1 != priorityCaller){
			System.out.println("~~~~~~~~~~" + p1.getSeatWind() + " Player tried to call " + p1.getCallStatusString() + ", but got bumped by " + priorityCaller.getSeatWind() + "!");
		}
		if (p2.called() && p2 != priorityCaller){
			System.out.println("~~~~~~~~~~" + p2.getSeatWind() + " Player tried to call " + p2.getCallStatusString() + ", but got bumped by " + priorityCaller.getSeatWind() + "!");
		}
		if (p3.called() && p3 != priorityCaller){
			System.out.println("~~~~~~~~~~" + p3.getSeatWind() + " Player tried to call " + p3.getCallStatusString() + ", but got bumped by " + priorityCaller.getSeatWind() + "!");
		}
		if (p4.called() && p4 != priorityCaller){
			System.out.println("~~~~~~~~~~" + p4.getSeatWind() + " Player tried to call " + p4.getCallStatusString() + ", but got bumped by " + priorityCaller.getSeatWind() + "!");
		}
		System.out.println();
		
		//it is now the calling player's turn
		mRoundTracker.setTurn(priorityCaller.getPlayerNumber());
		
		//pause for dramatic effect
		pauseWait();
		
		//reset reaction to none (since reaction has been handled)
		mReaction = NO_REACTION;
	}
	
	
	
	
	/*
	method: whoCalled
	decides who gets to call the tile
	
	
	check if only one player tried to call
	if (only one player made a call)
		return that player
	else (>1 player made a call)
		return the player with greater priority
		(ron > pon/kan > chi, break ron tie by seat order)
	end if
	
	callingPlayer = figure out who made the call (decide priority if >1 calls)
	handle the call
	show the result of the call
	
	whoseTurn = the player who made the call's turn
	reaction = NO_REACTION
	*/
	public Player whoCalled(){
		
		Player callingPlayer = null;
		
		//this is set to true by default, because we know at LEAST one player called
		boolean onlyOnePlayerCalled = true;
		//this is false until a call is found
		boolean alreadyFoundACall = false;
		
		//if this player called, foundCall = true
		if (p1.called()){
			alreadyFoundACall = true;
			callingPlayer = p1;
		}
		
		//if this player called, and we have already found another call, onlyOnePlayerCalled = false
		if (p2.called())
			if (alreadyFoundACall)
				onlyOnePlayerCalled = false;
			else{
				alreadyFoundACall = true;
				callingPlayer = p2;
			}
		
		if (p3.called())
			if (alreadyFoundACall)
				onlyOnePlayerCalled = false;
			else{
				alreadyFoundACall = true;
				callingPlayer = p3;
			}
		
		if (p4.called())
			if (alreadyFoundACall)
				onlyOnePlayerCalled = false;
			else{
				alreadyFoundACall = true;
				callingPlayer = p4;
			}
		
		

		//if only one player called, return that player
		if (onlyOnePlayerCalled){
			return callingPlayer;
		}
		else{	
			//else, if more than one player called, figure out who has more priority
			/*
			can 2 players call pon?: no, not enough tiles
			can 2 players call kan?: no, not enough tiles
			can 2 players call chi?: no, only shimocha can chi
			1 chi, 1 pon?: yes
			1 chi, 1 kan?: yes
			
			can 2 players call ron?: yes
			can 3 players call ron?: yes
			1 chi, 1 ron?: yes
			1 chi, 2 ron?: yes
			1 chi, 1 pon, 1 ron?: yes
			
			ron > pon/kan > chi
			pon/kan > chi
			
			2 rons: decide by closest seat order
			
			if >1 players called, and one of them called chi, the chi caller will NEVER have higher priority
			*/
			
			//if 1 chi and 1 pon/kan, or 1 chi and 1 ron, the pon/kan/ron always gets higher priority
			//so the chi is not even considered
			Player callerPon = null, callerRon = null;
			
			//if p1 called something other than a chi...
			if (p1.called() && !p1.calledChi())
				if (p1.calledPon() || p1.calledKan())
					//if he called pon/kan, he is the pon caller (there can't be 2 pon callers, not enough tiles in the game)
					callerPon = p1;
				else if (callerRon == null)
					//if he called ron, he is the ron caller (if there is already a ron caller, do nothing, because that caller has seat order priority)
					callerRon = p1;
			
			//check p2
			if (p2.called() && !p2.calledChi())
				if (p2.calledPon() || p2.calledKan())
					callerPon = p2;
				else if (callerRon == null)
					callerRon = p2;

			//check p3
			if (p3.called() && !p3.calledChi())
				if (p3.calledPon() || p3.calledKan())
					callerPon = p3;
				else if (callerRon == null)
					callerRon = p3;

			//check p4
			if (p4.called() && !p4.calledChi())
				if (p4.calledPon() || p4.calledKan())
					callerPon = p4;
				else if (callerRon == null)
					callerRon = p4;
			
			
			//return the first ron caller, or return the pon caller if there was no ron caller
			if (callerRon != null)
				return callerRon;
			else
				return callerPon;
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	method: decideSeats
	decides how many humans are playing, and randomly assigns all players to a seat
	
	
	numHumans = ask how many humans will be playing
	make list of controllers, with the desired number of humans
	
	shuffle the list randomly
	assign each of the controllers in the list to a seat
	
	return
	*/
	private void decideSeats(){

		//figure out how many humans are playing
		int numHumans = 0;
		if (DEBUG_DO_SINGLE_PLAYER_GAME)
			numHumans = 1;
		else{
//			@SuppressWarnings("resource")
			Scanner keyboard = new Scanner(System.in);
			System.out.println("How many humans will be playing? (Enter 1-4): ");
			numHumans = keyboard.nextInt();
		}
		
		
		//add the requested number of humans to the list of controllers
		ArrayList<Character> controllers = new ArrayList<Character>(NUM_PLAYERS);
		int i;
		for (i = 0; i < NUM_PLAYERS; i++)
			if (i < numHumans)
				controllers.add(Player.CONTROLLER_HUMAN);
			else
				controllers.add(Player.CONTROLLER_COM);
		
		if (DEBUG_SHUFFLE_SEATS){
			//shuffle the list controllers
			GenSort<Character> sorter = new GenSort<Character>(controllers);
			sorter.shuffle();
		}

		//assign the controllers to seats
		p1.setController(controllers.get(0));
		p2.setController(controllers.get(1));
		p3.setController(controllers.get(2));
		p4.setController(controllers.get(3));
		
		//set my human player name
		if (p1.controllerIsHuman()) p1.setPlayerName("Suwado");
		if (p2.controllerIsHuman()) p2.setPlayerName("Albert");
		if (p3.controllerIsHuman()) p3.setPlayerName("Brenda");
		if (p4.controllerIsHuman()) p4.setPlayerName("Carl");
		
		//assign neighbor links
		p1.setNeighbors(p2, p3, p4);
		p2.setNeighbors(p3, p4, p1);
		p3.setNeighbors(p4, p1, p2);
		p4.setNeighbors(p1, p2, p3);
	}
	
	
	
	/*
	method: showHandsOfHumanPlayers
	shows the hands of all human players in the game
	*/
	public void showHandsOfHumanPlayers(){for (Player p: mPlayerArray) p.showHand();}
	
	
	
	
	//accessors
	public int getGameType(){return mGameType;}
	public char getRoundWind(){return mRoundWind;}
	public int getGameResult(){return mRoundTracker.getRoundResult();}
	public boolean roundIsOver(){return mRoundTracker.roundIsOver();}
//	public boolean gameIsOver(){return mGameIsOver;}
	
	
	//pauses for dramatic effect (like after a computer's turn)
	public static void pauseWait(){
		int time = 0;
		if (DEBUG_WAIT_AFTER_COMPUTER) time = Player.TIME_TO_SLEEP / 2;
		
		try {Thread.sleep(time);} catch (InterruptedException e){}
	}
	
	
	
	/*
	method: displayRoundResult
	displays the result of the current round
	*/
	public void displayRoundResult(){
		
		mRoundTracker.printRoundResult();
		
		for (Player p: mPlayerArray) p.showHand();
	}
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
		System.out.println("Welcome to Majava (Table)!");
		boolean keepgoing = true;
		
		if (keepgoing){
			System.out.println("\n\n\n\n");
			
			Table table = new Table();
			table.play();
		}
	}
	

}
