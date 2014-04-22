package majava;

import majava.graphics.TableViewer;




/*
Class: Round

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
public class Round {
	
	public static final int DEFAULT_NUM_PLAYERS = 4;
	public static final char DEFAULT_ROUND_WIND = 'E';
	public static final int DEFAULT_ROUND_NUM = 1;
	public static final int DEFAULT_ROUND_BONUS_NUM = 0;
	
	public static final int NO_REACTION = 0;
	
	
	//for debug use
	public static final boolean DEBUG_DO_SINGLE_PLAYER_GAME = true;
	public static final boolean DEBUG_SHUFFLE_SEATS = false;
	public static final boolean DEBUG_WAIT_AFTER_COMPUTER = true;
	public static final boolean DEBUG_LOAD_DEBUG_WALL = true;
	
	
	
	
	private Player p1, p2, p3, p4;
	private Player[] mPlayerArray;
	
	private Wall mWall;
	
	private RoundTracker mRoundTracker;
	private TableViewer mTviewer;
	
	
	private char mRoundWind;
	private int mRoundNum;
	private int mRoundBonusNum;
	
	private int mReaction;
	
	
	
	/*
	1-arg Constructor
	initializes a new round to make it ready for playing
	
	creates the wall
	initializes round and game info
	*/
	public Round(TableViewer tviewer, Player[] playerArray, char roundWind, int roundNum, int roundBonusNum){
		
		
		mPlayerArray = playerArray;
		p1 = mPlayerArray[0]; p2 = mPlayerArray[1]; p3 = mPlayerArray[2]; p4 = mPlayerArray[3];
		
		
		//creates the wall
		mWall = new Wall();
		
		//initializes round info
		mRoundWind = roundWind;
		mRoundNum = roundNum;
		mRoundBonusNum = roundBonusNum;
		
		mReaction = NO_REACTION;
		
		
		mTviewer = tviewer;
		
		//initialize Round Tracker
		mRoundTracker = new RoundTracker(tviewer, mRoundWind,mRoundNum,mRoundBonusNum,  mWall,  p1,p2,p3,p4);
		
	}
	public Round(TableViewer tviewer, Player[] playerArray, char roundWind, int roundNum){this(tviewer, playerArray, roundWind, roundNum, DEFAULT_ROUND_BONUS_NUM);}
	public Round(TableViewer tviewer, Player[] playerArray){this(tviewer, playerArray, DEFAULT_ROUND_WIND, DEFAULT_ROUND_NUM);}
	
	
	
	
	/*
	method: play
	plays a new game of mahjong with four new players
	 
	 
	before play is called:
		wall/deadwall is set up
		round wind = East
	 
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
	public void play(){
		
		Tile discardedTile = null;
		

		//------------------------------------------------DEBUG INFO
		if (DEBUG_LOAD_DEBUG_WALL) mWall.loadDebugWall();
		System.out.println(mWall.toString() + "\n\n\n");mWall.printDoraIndicators();
		//------------------------------------------------DEBUG INFO
		
		
		
		
		//deal and sort hands
		mWall.dealHands(p1, p2, p3, p4);
		p1.sortHand(); p2.sortHand(); p3.sortHand(); p4.sortHand();
		mTviewer.updateEverything();
		

		//------------------------------------------------DEBUG INFO
		mWall.printWall();
		mWall.printDoraIndicators();
		
		p1.showHand();p2.showHand();p3.showHand();p4.showHand();
		System.out.println("\n\n\n");
		//------------------------------------------------DEBUG INFO
		
		
		
		mReaction = NO_REACTION;
		
		
		//loop until the round is over
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
		
		//~~~~~~handle drawing a tile
		//if the player needs to draw a tile, draw a tile
		if (p.needsDraw()){
			givePlayerTile(p);
		}
		
		
		
		
		//~~~~~~get player's discard (ankans, riichi, and such are handled inside here)
		//loop until the player has chosen a discard
		//loop until the player stops making kans
		do{
			discardedTile = p.takeTurn(mTviewer);
			
			//if the player made an ankan or minkan, they need a rinshan draw
			if (p.needsDrawRinshan()){
				
				//give player a rinshan draw
				givePlayerTile(p);
				
			}
			
			if (p.turnActionCalledTsumo()) mRoundTracker.setResultVictory(p.getSeatWind());
		}
		while (p.turnActionChoseDiscard() == false && mRoundTracker.roundIsOver() == false);
//		while (p.turnActionMadeKan());
		
		
		
		
		//return early if the round is over (tsumo or 4kan or 4riichi or kyuushu)
		if (mRoundTracker.roundIsOver())
			return null;
		
		
		
		
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
	
	
	
	
	//gives a player a tile from the wall or dead wall
	public void givePlayerTile(Player p){
		
		Tile drawnTile = null;
		if (p.needsDraw() == false) return;
		
		//draw from wall or dead wall, depending on what player needs
		if (p.needsDrawNormal()){
			
			if (mRoundTracker.checkIfWallIsEmpty()){
				//no tiles left in wall, round over
				return;
			}
			else{
				drawnTile = mWall.takeTile();
			}
		}
		else if (p.needsDrawRinshan()){
			
			//check if too many kans have been made before making a rinshan draw
			if (mRoundTracker.checkIfTooManyKans()){
				//too many kans, round over
				return;
			}
			else{
				drawnTile = mWall.takeTileFromDeadWall();
				mWall.printDeadWall();
				mWall.printDoraIndicators();	//debug
			}			
		}
		
		
		//add the tile to the player's hand
		p.addTileToHand(drawnTile);
		if (p.controllerIsHuman()) mTviewer.updateEverything();
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
	method: showHandsOfHumanPlayers
	shows the hands of all human players in the game
	*/
	public void showHandsOfHumanPlayers(){for (Player p: mPlayerArray) p.showHand();}
	
	
	
	
	//accessors
	public char getRoundWind(){return mRoundWind;}
	public boolean roundIsOver(){return mRoundTracker.roundIsOver();}
	
	
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
		
		mTviewer.updateEverything();
		
		for (Player p: mPlayerArray) p.showHand();
	}
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
		System.out.println("Welcome to Majava (Round)!");
		
		
		System.out.println("Launching Table...");
		Table.main(null);
	}
	

}
