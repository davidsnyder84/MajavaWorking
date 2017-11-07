package majava;

import java.util.ArrayList;
import java.util.List;

import majava.userinterface.GameUI;
import majava.player.Player;
import majava.summary.PaymentMap;
import majava.summary.RoundResultSummary;
import majava.tiles.GameTile;
import majava.enums.GameplayEvent;
import majava.enums.Wind;
import majava.enums.Exclamation;



/*
Class: Round
methods:
	public:
		mutators:
	 	play - play the round
	 	setOptionFastGameplay - set option to do fast gameplay or not
	 	
	 	accessors:
	 	getRoundWind, getRoundNum, getRoundBonusNum - returns round information
	 	
	 	roundIsOver - returns true if the round has ended
	 	endedWithVictory - returns true if the round ended with a win (not a draw)
	 	getWinningHandString - return a string repesentation of the round's winning hand
	 	displayRoundResult
*/
public class Round{

	private static final int NUM_PLAYERS = 4;
	private static final Wind DEFAULT_ROUND_WIND = Wind.EAST;
	private static final int DEFAULT_ROUND_NUM = 1;
	private static final int DEFAULT_ROUND_BONUS_NUM = 0;
	
	//for debug use
	private static final boolean DEBUG_LOAD_DEBUG_WALL = false;
	private static final boolean DEFAULT_DO_FAST_GAMEPLAY = false;
	
	
	
	
	private final Player p1, p2, p3, p4;
	private final Player[] mPlayerArray;
	
	private final Wall mWall;
	
	private final RoundTracker mRoundTracker;
	private final RoundResult mRoundResult;
	private final GameUI mUI;
	
	
	private final Wind mRoundWind;
	private final int mRoundNum, mRoundBonusNum;
	
	
	//options
	private boolean mDoFastGameplay;
	private int sleepTime, sleepTimeExclamation, sleepTimeRoundEnd;
	
	
	
	//constructor
	public Round(GameUI ui, Player[] playerArray, Wind roundWind, int roundNum, int roundBonusNum){
		
		mPlayerArray = playerArray;
		p1 = mPlayerArray[0]; p2 = mPlayerArray[1]; p3 = mPlayerArray[2]; p4 = mPlayerArray[3];
		
		//prepare players for new round
		for (Player p: mPlayerArray)
			p.prepareForNewRound();
		
		//creates the wall
		mWall = new Wall();
		
		//initializes round info
		mRoundWind = roundWind;
		mRoundNum = roundNum;
		mRoundBonusNum = roundBonusNum;
		
		mUI = ui;
		
		//initialize Round Tracker
		mRoundResult = new RoundResult();
		mRoundTracker = new RoundTracker(mUI, mRoundResult, mRoundWind,mRoundNum,mRoundBonusNum,  mWall,  p1,p2,p3,p4);
		
		setOptionFastGameplay(DEFAULT_DO_FAST_GAMEPLAY);
	}
	//no bonus round info
	public Round(GameUI ui, Player[] playerArray, Wind roundWind, int roundNum){this(ui, playerArray, roundWind, roundNum, DEFAULT_ROUND_BONUS_NUM);}
	//no round info
	public Round(GameUI ui, Player[] playerArray){this(ui, playerArray, DEFAULT_ROUND_WIND, DEFAULT_ROUND_NUM);}
	
	
	
	
	
	//plays a single round of mahjong with the round's players
	public void play(){
		if (roundIsOver()){mUI.printErrorRoundAlreadyOver();return;}
		
		dealHands();
		while (!roundIsOver()){
			doPlayerTurn(mRoundTracker.currentPlayer());
			
			if (!roundIsOver())
				if (mRoundTracker.callWasMadeOnDiscard())
					handleReaction();
		}
		
		handleRoundEnd();
	}
	
	
	private void handleRoundEnd(){
		doPointPayments();
		
		if (mUI != null) mUI.setRoundResult(mRoundResult.getSummary());
		
		//display end of round result
		displayRoundResult();
	}
	
	
	
	private void doPointPayments(){
		final int PAYMENT_DUE = 8000;
		int paymentDue = PAYMENT_DUE;
		
		PaymentMap payments = null;

		//find who has to pay what
		if (mRoundResult.isDraw())
			payments = mapPaymentsDraw();
		else
			payments = mapPaymentsWin(paymentDue);
		
		//carry out payments
		for (Player p: mPlayerArray)
			p.pointsIncrease(payments.get(p));
		
		mRoundResult.recordPayments(payments);
	}

	
	
	//maps payments to players, for the case of a win
	private PaymentMap mapPaymentsWin(int handValue){
		PaymentMap payments = new PaymentMap();
		
		final double DEALER_WIN_MULTIPLIER = 1.5, DEALER_TSUMO_MULTIPLIER = 2;
		
		int paymentDue = handValue;
		int tsumoPointsNonDealer = paymentDue / 4;
		int tsumoPointsDealer = (int) (DEALER_TSUMO_MULTIPLIER * tsumoPointsNonDealer);
		
		
		//find who the winner is
		Player winner = mRoundResult.getWinningPlayer();
		Player[] losers = {mRoundTracker.neighborShimochaOf(winner), mRoundTracker.neighborToimenOf(winner), mRoundTracker.neighborKamichaOf(winner)};
		Player furikonda = null;
		
		if (winner.isDealer()) paymentDue *= DEALER_WIN_MULTIPLIER;
		
		///////add in honba here
		
		payments.put(winner, paymentDue);
		
		
		//find who has to pay
		if (mRoundResult.isVictoryRon()){
			furikonda = mRoundResult.getFurikondaPlayer();
			for (Player p: losers)
				if (p == furikonda) payments.put(p, -paymentDue);
				else payments.put(p, 0);
		}
		else{//tsumo
			for (Player p: losers){
				if (p.isDealer() || winner.isDealer()) payments.put(p, -tsumoPointsDealer);
				else  payments.put(p, -tsumoPointsNonDealer);
			}
		}
		///////add in riichi sticks here
		return payments;
	}
	
	//maps payments to players, for the case of a draw
	private PaymentMap mapPaymentsDraw(){
		PaymentMap payments = new PaymentMap();
		/////implement no-ten bappu here 
		
		for (Player p: mPlayerArray)
			payments.put(p, 0);
		return payments;
	}
	
	
	
	
	
	
	//deals players their starting hands
	private void dealHands(){
		
		if (DEBUG_LOAD_DEBUG_WALL) mWall.DEMOloadDebugWall();	//DEBUG
		
		//get starting hands from the wall
		List<GameTile> tilesE = new ArrayList<GameTile>(), tilesS = new ArrayList<GameTile>(), tilesW = new ArrayList<GameTile>(), tilesN = new ArrayList<GameTile>();
		mWall.getStartingHands(tilesE, tilesS, tilesW, tilesN);
		
		
		Player eastPlayer = mRoundTracker.currentPlayer();
		//give dealer their tiles
		eastPlayer.giveStartingHand(tilesE);
		mRoundTracker.neighborShimochaOf(eastPlayer).giveStartingHand(tilesS);
		mRoundTracker.neighborToimenOf(eastPlayer).giveStartingHand(tilesW);
		mRoundTracker.neighborKamichaOf(eastPlayer).giveStartingHand(tilesN);
		
		__updateUI(GameplayEvent.START_OF_ROUND);
	}
	
	
	
	

	
	/*
	//handles player p's turn, and gets the other players' reactions to the p's turn
	
 	display what the player discarded
 	get the other players' reactions to the discarded tile (the players will "make a call", the call won't actually be handled yet)
 	
	if the wall is not empty and no one made a call, move to the next player's turn
	*/
	private void doPlayerTurn(Player p){
		
		if (p.needsDraw()){
			givePlayerTile(p);
		}
		else __updateUI(GameplayEvent.PLACEHOLDER);
		
		//return early if the round is over (4kan or washout)
		if (roundIsOver()) return;
		
		//~~~~~~get player's discard (kans and riichi are handled inside here)
		//loop until the player has chosen a discard
		//loop until the player stops making kans
		GameTile discardedTile = null;
		do{
			discardedTile = p.takeTurn();
			mRoundTracker.setMostRecentDiscard(discardedTile);
			
			//if the player made an ankan or minkan, they need a rinshan draw
			if (p.needsDrawRinshan()){
				
				GameplayEvent kanEvent = GameplayEvent.DECLARED_OWN_KAN;
				kanEvent.setExclamation(Exclamation.OWN_KAN, p.getPlayerNumber());
				__updateUI(kanEvent);
				__updateUI(GameplayEvent.MADE_OWN_KAN);
				
				//give player a rinshan draw
				givePlayerTile(p);
				
			}
			
			if (p.turnActionCalledTsumo()){
				GameplayEvent tsumoEvent = GameplayEvent.DECLARED_TSUMO;
				tsumoEvent.setExclamation(Exclamation.TSUMO, p.getPlayerNumber());
				__updateUI(tsumoEvent);
				mRoundTracker.setResultVictory(p);
			}
			
		}
		while (!p.turnActionChoseDiscard() && !roundIsOver());
		
		//return early if the round is over (tsumo or 4kan or 4riichi or kyuushu)
		if (roundIsOver()) return;
		
		//show the human player their hand, show the discarded tile and the discarder's pond
		__updateUI(GameplayEvent.DISCARDED_TILE);
		
		//~~~~~~get reactions from the other players
		mPlayerArray[(p.getPlayerNumber() + 1) % NUM_PLAYERS].reactToDiscard(discardedTile);
		mPlayerArray[(p.getPlayerNumber() + 2) % NUM_PLAYERS].reactToDiscard(discardedTile);
		mPlayerArray[(p.getPlayerNumber() + 3) % NUM_PLAYERS].reactToDiscard(discardedTile);
//		mRoundTracker.neighborShimochaOf(p).reactToDiscard(discardedTile);
//		mRoundTracker.neighborToimenOf(p).reactToDiscard(discardedTile);
//		mRoundTracker.neighborKamichaOf(p).reactToDiscard(discardedTile);
		
		if (!mRoundTracker.callWasMadeOnDiscard()){
			//update turn indicator
			if (!mRoundTracker.checkIfWallIsEmpty())
				mRoundTracker.nextTurn();
		}
	}
	
	
	
	
	
	
	
	//gives a player a tile from the wall or dead wall
	private void givePlayerTile(Player p){
		if (!p.needsDraw()) return;
		GameTile drawnTile = null;
		
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
				__updateUI(GameplayEvent.NEW_DORA_INDICATOR);
			}			
		}
		
		p.addTileToHand(drawnTile);
		__updateUI(GameplayEvent.DREW_TILE);
	}
	
	
	
	
	
	//handles a call made on a discarded tile
	private void handleReaction(){
		//get the discarded tile
		GameTile discardedTile = mRoundTracker.currentPlayer().getLastDiscard();
		
		//figure out who called the tile, and if multiple players called, who gets priority
		Player priorityCaller = whoCalled();
		
		//remove the tile from the discarder's pond (because it is being called), unless the call was Ron
		if (!priorityCaller.calledRon())
			mRoundTracker.currentPlayer().removeTileFromPond();
		
		//show the call
		GameplayEvent callEvent = GameplayEvent.CALLED_TILE;
		callEvent.setExclamation(priorityCaller.getCallStatusExclamation(), priorityCaller.getPlayerNumber());
		__updateUI(callEvent);
		
		//give the caller the discarded tile so they can make their meld
		//if the caller called Ron, handle that instead
		if (priorityCaller.calledRon()){
			mRoundTracker.setResultVictory(priorityCaller);
		}
		else{
			//make the meld
			priorityCaller.makeMeld(discardedTile);
			__updateUI(GameplayEvent.MADE_OPEN_MELD);
		}
		
		//it is now the calling player's turn (if the round isn't over)
		if (!roundIsOver())
			mRoundTracker.setTurn(priorityCaller);
	}
	
	//decides who gets to call the tile
	private Player whoCalled(){
		
		Player callingPlayer = null;
		Player callerPon = null, callerRon = null;
		
		int numCallers = 0;
		for (Player p: mPlayerArray)
			if (p.called()){
				callingPlayer = p;
				numCallers++;
			}
		
		//if only one player called, return that player
		if (numCallers == 1){
			return callingPlayer;
		}
		else {
			//else, if more than one player called, figure out who has more priority
			//if p called something other than a chi...
				//if he called pon/kan, he is the pon caller (there can't be 2 pon callers, not enough tiles in the game)
				//if he called ron, he is the ron caller (if there is already a ron caller, do nothing, because that caller has seat order priority)
			for (int i = mPlayerArray.length - 1; i >= 0 ; i--){
				if (mPlayerArray[i].called() && !mPlayerArray[i].calledChi())
					if (mPlayerArray[i].calledPon() || mPlayerArray[i].calledKan())
						callerPon = mPlayerArray[i];
					else
						callerRon = mPlayerArray[i];
			}
			
			//return the first ron caller, or return the pon caller if there was no ron caller
			if (callerRon != null) return callerRon;
			return callerPon;
		}
	}
	
	
	
	
	
	
	//accessors
	public Wind getRoundWind(){return mRoundWind;}
	public int getRoundNum(){return mRoundNum;}
	public int getRoundBonusNum(){return mRoundBonusNum;}
	
	public boolean roundIsOver(){return mRoundResult.isOver();}
	public boolean endedWithVictory(){return mRoundResult.isVictory();}
	public String getWinningHandString(){return mRoundResult.getAsStringWinningHand();}
	public RoundResultSummary getResultSummary(){return mRoundResult.getSummary();}
	
	public boolean qualifiesForRenchan(){return mRoundTracker.qualifiesForRenchan();}
	
	
	
	public void displayRoundResult(){
		__updateUI(GameplayEvent.END_OF_ROUND);
	}
	
	
	private void __updateUI(GameplayEvent event){
		if (mUI == null) return;
		
		mUI.displayEvent(event);
	}
	
	
	public void setOptionFastGameplay(boolean doFastGameplay){
		
		mDoFastGameplay = doFastGameplay;

		final int DEAFULT_SLEEPTIME = 400;
		final int DEAFULT_SLEEPTIME_EXCLAMATION = 1500;
//		final int DEAFULT_SLEEPTIME_ROUND_END = 2000;
//		final int DEAFULT_SLEEPTIME_ROUND_END = 7000;
		final int DEAFULT_SLEEPTIME_ROUND_END = 18000;
		
		final int FAST_SLEEPTIME = 0;
		final int FAST_SLEEPTIME_EXCLAMATION = 0;
//		final int FAST_SLEEPTIME_EXCLAMATION = DEAFULT_SLEEPTIME_EXCLAMATION;
		final int FAST_SLEEPTIME_ROUND_END = 0;
//		final int FAST_SLEEPTIME_ROUND_END = DEAFULT_SLEEPTIME_ROUND_END;
		
		
		if (mDoFastGameplay){
			sleepTime = FAST_SLEEPTIME;
			sleepTimeExclamation = FAST_SLEEPTIME_EXCLAMATION;
			sleepTimeRoundEnd = FAST_SLEEPTIME_ROUND_END;
		}
		else{
			sleepTime = DEAFULT_SLEEPTIME;
			sleepTimeExclamation = DEAFULT_SLEEPTIME_EXCLAMATION;
			sleepTimeRoundEnd = DEAFULT_SLEEPTIME_ROUND_END;
		}
		
		if (mUI != null) mUI.setSleepTimes(sleepTime, sleepTimeExclamation, sleepTimeRoundEnd);
	}
	
	
	
	public static void main(String[] args) {
		
		System.out.println("Welcome to Majava (Round)!");
		
		System.out.println("Launching Table...");
		Table.main(null);
	}

}
