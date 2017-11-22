package majava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import majava.userinterface.GameUI;
import majava.util.GameTileList;
import majava.player.Player;
import majava.summary.StateOfGame;
import majava.summary.PlayerSummary;
import majava.summary.RoundResultSummary;
import majava.tiles.GameTile;
import majava.enums.Wind;


//other objects can ask a RoundTracker for universally available information about the round
//(analogous to what you can see with your own eyes)
public class RoundTracker {
	private static final int NUM_PLAYERS = 4;
	
	
	private final Round round;
	private final Wall wall;	//duplicate
	
	private final Player[] players;
	
	
	public RoundTracker(Round roundToTrack, Wall receivedWall, Player[] playerArray, GameUI ui){
		round = roundToTrack;	
		
		wall = receivedWall;
		
		players = playerArray.clone();
		for (Player p: players)
			p.syncWithRoundTracker(this);
		
		__syncWithUI(ui);
	}
	//overloaded without UI
	public RoundTracker(Round roundToTrack, Wall receivedWall, Player[] playerArray){this(roundToTrack, receivedWall, playerArray, null);}
	
	private void __syncWithUI(GameUI ui){
		if (ui == null) return;
		StateOfGame stateOfGame = new StateOfGame(this, players, wall);
		ui.syncWithRoundTracker(this, stateOfGame);
	}
	
	
	
	
	
	/////I kind of want to get rid of these eventually, but they're used by the UIs
	public int whoseTurn(){return round.whoseTurnNumber();}
	public GameTile getMostRecentDiscard(){return round.mostRecentDiscard();}
	
	
	public RoundResultSummary getResultSummary(){return round.getResultSummary();}	
	public String getRoundResultString(){return round.getRoundResultString();}
	public boolean roundIsOver(){return round.roundIsOver();}
	
	
	
	//returns true if multiple players have made kans, returns false if only one player or no players have made kans
	private boolean multiplePlayersHaveMadeKans(){
		//count the number of players who have made kans
		int count = 0;
		for (Player p: players){
			if (p.hasMadeAKan())
				count++;
		}
		return (count > 1);
	}
	//returns true if a round-ending number of kans have been made
	//returns true if 5 kans have been made, or if 4 kans have been made by multiple players
	public boolean tooManyKans(){
		final int KAN_LIMIT = 4;
		if (getNumKansMade() < KAN_LIMIT) return false;
		if (getNumKansMade() == KAN_LIMIT && !multiplePlayersHaveMadeKans()) return false;		
		return true;
	}
	
	public int getNumKansMade(){
		int count = 0;
		for (Player p: players) count += p.getNumKansMade();
		return count;
	}
	
	
	
	public int getNumTilesLeftInWall(){return wall.numTilesLeftInWall();}
	public GameTileList getDoraIndicators(){return wall.getDoraIndicators();}
	public GameTileList getDoraIndicatorsWithUra(){return wall.getDoraIndicatorsWithUra();}
	
	
	public Wind getRoundWind(){return round.getRoundWind();}
	public int getRoundNum(){return round.getRoundNum();}
	public int getRoundBonusNum(){return round.getRoundBonusNum();}
}

