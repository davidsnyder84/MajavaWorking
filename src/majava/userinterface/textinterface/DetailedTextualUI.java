package majava.userinterface.textinterface;


import java.util.Arrays;
import java.util.List;

import utility.Pauser;
import majava.hand.Meld;
import majava.util.GTL;
import majava.util.YakuList;
import majava.yaku.Yaku;
import majava.enums.Exclamation;
import majava.player.Player;
import majava.summary.PaymentMap;
import majava.summary.PlayerSummary;
import majava.summary.RoundResultSummary;
import majava.tiles.GameTile;

public class DetailedTextualUI extends TextualUI{
	
	
	public DetailedTextualUI(){
		super();
	}
	
	
	

	protected void displayEventDiscardedTile(){
		
		//show the human player their hand
		__showHandsOfAllPlayers();
		
		//show the discarded tile and the discarder's pond
		println("\n\n\tTiles left: " + gameState.getNumTilesLeftInWall());
		println("\t" + gameState.seatWindOfPlayer(gameState.whoseTurn()) + " Player's discard: ^^^^^" + gameState.getMostRecentDiscard().toString() + "^^^^^");
		println("\t" + gameState.getPondAsString(gameState.whoseTurn()));
		
		
	}
	
	
	
	protected void displayEventMadeOpenMeld(){}
	protected void displayEventDrewTile(){}
	protected void displayEventMadeOwnKan(){}
	protected void displayEventHumanReactionStart() {}
	
	
	protected void displayEventNewDoraIndicator(){
		__showDeadWall();
	}

	protected void displayEventHumanTurnStart(){
		__showPlayerHand(gameState.whoseTurn());
	}
	
	protected void displayEventStartOfRound(){
		__showWall();__showDoraIndicators();
	}
	
	protected void displayEventEndOfRound(){
		__showRoundResult();
		__showHandsOfAllPlayers();
		println("\n\n");
		
		if (sleepTimeRoundEnd > 0) Pauser.pauseFor(sleepTimeRoundEnd);
	}
	
	protected void displayEventPlayerTurnStart(){/*blank*/}
	
	
	
	
	
	@Override
	protected void __showPlayerHand(int seatNum) {println(gameState.getHandAsString(seatNum));}
	
	protected void __showWall(){println(gameState.wallToString());}
	
	protected void __showDoraIndicators(){
		GTL doraIndicators = gameState.getDoraIndicators();
		println("Dora Indicators: " + doraIndicators + "\n\n");
	}
	
	protected void __showDeadWall(){
		__showDoraIndicators();
		println(gameState.deadWallToString());
	}
	
	protected void __showRoundResultOLD(){
		if (!gameState.roundIsOver()) return;
		
		System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + 
		 					"\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~Round over!~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + 
				 			"\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		RoundResultSummary result = gameState.getResultSummary();
		
		String resultStr = "Result: " + gameState.getRoundResultString();
		System.out.println(resultStr);
		
		if (result.isVictory());
//			System.out.println(mRoundResult.getAsStringWinningHand());
	}
	
	protected void __showRoundResult(){
		if (!gameState.roundIsOver()) return;
		
		println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + 
		 		"\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~Round over!~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + 
				"\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
		
		RoundResultSummary roundResultSummary = gameState.getResultSummary();
		
		//for all
		String resultLabel = null;
		PaymentMap payments = null;
		//for win
		PlayerSummary winner = null, furikon = null;
		GTL winnerHandTiles = null; List<Meld> winnerMelds = null; GameTile winningTile = null;
		YakuList yakuList = null; int yakuWorth = -1; int handScore = -1; 
		
		
		//***result label (Player 1 wins!, Draw!, etc)
		resultLabel = roundResultSummary.getAsStringResultType();
		
		
		//***payments per player panel
		payments = roundResultSummary.getPayments();
		
		if (roundResultSummary.isVictory()){
			winner = roundResultSummary.getWinningPlayer();
			furikon = roundResultSummary.getFurikondaPlayer();
			
			//***winning hand/melds panel
			winnerHandTiles = roundResultSummary.getWinnerHandTiles();	
			winnerMelds = roundResultSummary.getWinnerMelds();
			winningTile = roundResultSummary.getWinningTile();
			
			//***panel/list of yaku
			yakuList = roundResultSummary.getYakuOfWinner();
			yakuWorth = yakuList.totalHan();
			
			//***hand score label
			handScore = payments.get(winner);
		}
		
		
		System.out.println("Result: " + resultLabel);
		if (roundResultSummary.isVictory()){
			print(roundResultSummary.getAsStringWinType() + "!");
			if (roundResultSummary.isVictoryRon()) print(" (from Player " + (furikon.getPlayerNumber()+1) + ")");
			println();
		}
		
		
		System.out.println("\nPayments:");
		for (PlayerSummary ps: payments){
			print("\tPlayer " + (ps.getPlayerNumber()+1) + " (" + ps.getPlayerName() + ", " + ps.getSeatWind().toChar() + ")... Points:" + ps.getPoints() + " (");
			if (payments.get(ps) > 0) print("+");
			println(payments.get(ps) + ")");
		}
		
		if (roundResultSummary.isVictory()){
			//***winning hand/melds panel
			println("\nWinner's hand: " + winnerHandTiles);
			println("Winner's melds:");
			for (Meld m: winnerMelds) System.out.println("\t" + m);
			println("Winning tile: " + winningTile);
			
			//***panel/list of yaku
			System.out.println("\nList of Yaku:");
			for (Yaku y: yakuList) println("\t" + y + " (" + yakuWorth + ")");
			
			//***hand score label
			println("Hand score: " + handScore);
		}
		
		
		println("\n\n\n");
	}
	
	
	
	
	
	
	//info needed: seat wind of caller, most recent discard
	@Override
	protected void showExclamation(Exclamation exclamation, int seat){
		
		if (exclamation.isCall())
			println("\n*********************************************************" + 
					"\n**********" + gameState.seatWindOfPlayer(seat) + " Player called the tile (" + gameState.getMostRecentDiscard() + ")! " + exclamation + "!!!**********" +  
					"\n*********************************************************");
		else
			println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + 
					"\n~~~~~~~~~~~" + gameState.seatWindOfPlayer(seat) + " Player declared " + exclamation + "!!!~~~~~~~~~~~" + 
					"\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		

//		//if multiple players called, show if someone got bumped by priority 
//		for (Player p: mPlayerArray)
//			if (p.called() && p != priorityCaller)
//				System.out.println("~~~~~~~~~~" + p.getSeatWind() + " Player tried to call " + p.getCallStatusString() + ", but got bumped by " + priorityCaller.getSeatWind() + "!");
//		System.out.println();
		
		//pause
		if (sleepTimeExclamation > 0) Pauser.pauseFor(sleepTimeExclamation);
	}

}
