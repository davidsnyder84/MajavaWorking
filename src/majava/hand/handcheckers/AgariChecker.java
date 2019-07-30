package majava.hand.handcheckers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import majava.enums.MeldType;
import majava.hand.Hand;
import majava.hand.Meld;
import majava.tiles.GameTile;
import majava.tiles.HandCheckerTile;
import majava.tiles.Janpai;
import majava.util.GameTileList;
import majava.util.TileKnowledge;


//checks if a hand is complete (agari) or nearly complete (tenpai)
public class AgariChecker {
	private static final int MAX_HAND_SIZE = 14;
	
	private final ChiitoiChecker chiitoiChecker;
	private final KokushiChecker kokushiChecker;
	private final NormalAgariChecker normalAgariChecker;
	
	public AgariChecker(Hand handToCheck, GameTileList receivedHandTiles){
		chiitoiChecker = new ChiitoiChecker(handToCheck, receivedHandTiles);
		kokushiChecker = new KokushiChecker(handToCheck, receivedHandTiles);
		normalAgariChecker = new NormalAgariChecker(handToCheck, receivedHandTiles);
	}
	
	
	
	//returns true if a hand is complete (it is a winning hand)
	public boolean isComplete(){
		return (isCompleteKokushi() || isCompleteChiitoitsu() || isCompleteNormal());
	}
	
	
	//returns the list of tenpai waits (will be empty if the hand is not in tenpai)
	public GameTileList getTenpaiWaits(){
		GameTileList waits = new GameTileList();
		waits.addAll(getNormalTenpaiWaits());
		if (waits.isEmpty()) waits.addAll(getKokushiWaits());
		if (waits.isEmpty()) waits.addAll(getChiitoiWait());
		
		return waits;
	}
	public boolean isInTenpai(){return !getTenpaiWaits().isEmpty();}
	
	
	
	public boolean isTenpaiKokushi(){return kokushiChecker.isTenpaiKokushi();}
	public boolean isCompleteKokushi(){return kokushiChecker.isCompleteKokushi();}
	public GameTileList getKokushiWaits(){return kokushiChecker.getKokushiWaits();}
	
	public boolean isTenpaiChiitoitsu(){return chiitoiChecker.isTenpaiChiitoitsu();}
	public boolean isCompleteChiitoitsu(){return chiitoiChecker.isCompleteChiitoitsu();}
	public GameTileList getChiitoiWait(){return chiitoiChecker.getChiitoiWait();}
	
	public boolean isCompleteNormal(){return normalAgariChecker.isCompleteNormal();}
	public GameTileList getNormalTenpaiWaits(){return normalAgariChecker.getNormalTenpaiWaits();}
	public List<Meld> getFinishingMelds(){return normalAgariChecker.getFinishingMelds();}
	
	
	
	
	
	
	
	
	
	private static class KokushiChecker{
		private static final Integer[] YAOCHUU_TILE_IDS = Janpai.retrieveYaochuuTileIDs();
		private static final int NUMBER_OF_YAOCHUU_TILES = YAOCHUU_TILE_IDS.length;
		
		private final Hand myHand;
		private final GameTileList handTiles;
		public KokushiChecker(Hand handToCheck, GameTileList receivedHandTiles){
			myHand = handToCheck;
			handTiles = receivedHandTiles;
		}
		
		
		public boolean isTenpaiKokushi(){
			//conditions for kokushi tenpai: handsize >= 13, and hand has at least 12 different TYC tiles
			
			//if any melds have been made, kokushi musou is impossible, return false
			if (handTiles.size() < MAX_HAND_SIZE-1) return false;
			//if the hand contains even one non-honor tile, return false
			for (GameTile t: handTiles) if (!t.isYaochuu()) return false;
			
			
			//check if the hand contains at least 12 different TYC tiles
			int countTYC = 0;
			for (int id: YAOCHUU_TILE_IDS)
				if (handTiles.contains(id))
					countTYC++;
			
			//return false if the hand doesn't contain at least 12 different TYC tiles
			if (countTYC < NUMBER_OF_YAOCHUU_TILES - 1) return false;
			
			return true;
		}
		
		//returns true if a 14-tile hand is a complete kokushi musou
		public boolean isCompleteKokushi(){
			if ((handTiles.size() == MAX_HAND_SIZE) &&
				(isTenpaiKokushi()) &&
				(getKokushiWaits().size() == NUMBER_OF_YAOCHUU_TILES))
				return true;
			
			return false;
		}

		//returns a list of the hand's waits, if it is in tenpai for kokushi musou
		//returns an empty list if not in kokushi musou tenpai
		private GameTileList getKokushiWaits(){
			GameTileList waits = new GameTileList();
			GameTile missingTYC = null;
			
			if (isTenpaiKokushi()){
				//look for a Yaochuu tile that the hand doesn't contain
				for (Integer id: YAOCHUU_TILE_IDS)
					if (!handTiles.contains(id))
						missingTYC = new GameTile(id);
				
				//if the hand contains exactly one of every Yaochuu tile, then it is a 13-sided wait for all Yaochuu tiles
				if (missingTYC == null)
					waits = new GameTileList(YAOCHUU_TILE_IDS);
				else
					//else, if the hand is missing a Yaochuu tile, that missing tile is the hand's wait
					waits.add(missingTYC);
			}
			return waits;
		}
	}
	
	
	
	
	
	
	
	
	private static class ChiitoiChecker{
		
		private final Hand myHand;
		private final GameTileList handTiles;
		public ChiitoiChecker(Hand handToCheck, GameTileList receivedHandTiles){
			myHand = handToCheck;
			handTiles = receivedHandTiles;
		}
		
		public GameTileList getChiitoiWait(){
			//conditions for chiitoi tenpai:
				//hand must be 13 tiles (no melds made)
				//hand must have exactly 7 different types of tiles
				//hand must have no more than 2 of each tile
			
			//if any melds have been made, chiitoitsu is impossible, return false
			if (handTiles.size() != MAX_HAND_SIZE-1 || myHand.numberOfMeldsMade() > 0) return emptyWaitsList();
			
			//the hand should have exactly 7 different types of tiles (4 of a kind != 2 pairs)
			if (handTiles.makeCopyNoDuplicates().size() != 7) return emptyWaitsList();

			//the hand must have no more than 2 of each tile
			GameTile missingTile = null;
			for(GameTile t: handTiles){
				switch(handTiles.findHowManyOf(t)){
				case 1: missingTile = t;
				case 2: break;//intentionally blank
				default: return emptyWaitsList();
				}
			}
			
			//at this point, we know that the hand is in chiitoitsu tenpai
			
			//add the missing tile to the wait list (it will be the only wait)
			//if NPO happens here, look at old code
			return new GameTileList(missingTile);
		}
		public boolean isTenpaiChiitoitsu(){return !getChiitoiWait().isEmpty();}
		
		
		//returns true if a 14-tile hand is a complete chiitoitsu
		public boolean isCompleteChiitoitsu(){
			GameTileList checkTilesCopy = handTiles.clone();
			checkTilesCopy.sort();
			
			//chiitoitsu is impossible if a meld has been made
			if (checkTilesCopy.size() < MAX_HAND_SIZE) return false;
			
			//even tiles should equal odd tiles, if chiitoitsu
			GameTileList evenTiles = checkTilesCopy.getMultiple(0,2,4,6,8,10,12);
			GameTileList oddTiles = checkTilesCopy.getMultiple(1,3,5,7,9,11,13);
			return evenTiles.equals(oddTiles);
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	private static class NormalAgariChecker{
		private static final int NUM_PARTNERS_NEEDED_TO_PON = 2;
		private static final int NUM_PARTNERS_NEEDED_TO_PAIR = 1;
		private static final int OFFSET_CHI_L1 = 1, OFFSET_CHI_L2 = 2;
		private static final int OFFSET_CHI_M1 = -1,  OFFSET_CHI_M2 = 1;
		private static final int OFFSET_CHI_H1 = -2, OFFSET_CHI_H2 = -1;
		
		
		private final Hand myHand;
		private final GameTileList handTiles;
		public NormalAgariChecker(Hand handToCheck, GameTileList receivedHandTiles){
			myHand = handToCheck;
			handTiles = receivedHandTiles;
		}
		
		//シュンツ
		private static boolean canClosedChiType(GameTileList checkTiles, GameTile candidate, int offset1, int offset2){
			return (checkTiles.contains(candidate.getId() + offset1) && checkTiles.contains(candidate.getId() + offset2));
		}
		private static boolean canClosedChiL(GameTileList checkTiles, GameTile candidate){return candidate.canCompleteChiL() && canClosedChiType(checkTiles, candidate, OFFSET_CHI_L1, OFFSET_CHI_L2);}
		private static boolean canClosedChiM(GameTileList checkTiles, GameTile candidate){return candidate.canCompleteChiM() && canClosedChiType(checkTiles, candidate, OFFSET_CHI_M1, OFFSET_CHI_M2);}
		private static boolean canClosedChiH(GameTileList checkTiles, GameTile candidate){return candidate.canCompleteChiH() && canClosedChiType(checkTiles, candidate, OFFSET_CHI_H1, OFFSET_CHI_H2);}
		//コーツ
		private static boolean canClosedMultiType(GameTileList checkTiles, GameTile candidate, int numPartnersNeeded){
			return checkTiles.findHowManyOf(candidate) >= numPartnersNeeded;
		}
		private static boolean canClosedPair(GameTileList checkTiles, GameTile candidate){return canClosedMultiType(checkTiles, candidate, NUM_PARTNERS_NEEDED_TO_PAIR + 1);}
		private static boolean canClosedPon(GameTileList checkTiles, GameTile candidate){return canClosedMultiType(checkTiles, candidate, NUM_PARTNERS_NEEDED_TO_PON + 1);}
		
		
		//checks if a tile is meldable, populates the meldStack for candidate. returns true if a meld (any meld) can be made
		private static boolean checkMeldableTile(HandCheckerTile candidate, GameTileList checkTiles){
			//order of stack should be top->L,M,H,Pon,Pair
			if (canClosedPair(checkTiles, candidate)) candidate.mstackPush(MeldType.PAIR);
			if (canClosedPon(checkTiles, candidate)) candidate.mstackPush(MeldType.PON);
			if (canClosedChiH(checkTiles, candidate)) candidate.mstackPush(MeldType.CHI_H);
			if (canClosedChiM(checkTiles, candidate)) candidate.mstackPush(MeldType.CHI_M);
			if (canClosedChiL(checkTiles, candidate)) candidate.mstackPush(MeldType.CHI_L);
			
			return (!candidate.mstackIsEmpty());
		}

		
		private GameTileList getNormalTenpaiWaits(){
			final GameTileList waits = new GameTileList();
			final GameTileList checkTilesCopy = handTiles.clone();
			
			final List<Integer> hotTileIDs = TileKnowledge.findAllHotTiles(handTiles);
			for (Integer id: hotTileIDs){
				//get a hot tile (and mark it with the hand's seat wind, so chi is valid)
				GameTile currentHotTile = new GameTile(id);
				currentHotTile.setOwner(myHand.getOwnerSeatWind());
				
				//make a copy of the hand, add the current hot tile to that copy
				checkTilesCopy.add(currentHotTile);
				
				//check if the copy, with the added tile, is complete
				if (isCompleteNormal(checkTilesCopy)) waits.add(currentHotTile);
				
				checkTilesCopy.remove(currentHotTile);
			}
			
			return waits;
		}
		
		
		
		//returns true if list of checkTiles is complete (is a winning hand)
		public static boolean isCompleteNormal(GameTileList checkTiles, List<Meld> finishingMelds){
			if ((checkTiles.size() % 3) != 2) return false;
			
			GameTileList copyOfCheckTiles = HandCheckerTile.makeCopyOfListWithCheckers(checkTiles);
			copyOfCheckTiles.sort();
			
			//populate stacks
			if (!populateMeldStacks(copyOfCheckTiles)) return false;
			return isCompleteNormalHand(copyOfCheckTiles, finishingMelds);
		}
		//overloaded, checks mHandTiles by default
		public static boolean isCompleteNormal(GameTileList checkTiles){return isCompleteNormal(checkTiles, null);}
		public boolean isCompleteNormal(List<Meld> finishingMelds){return isCompleteNormal(handTiles, finishingMelds);}
		public boolean isCompleteNormal(){return isCompleteNormal(handTiles);}
		
		
		//populates the meld type stacks for all of the tile in checkTiles
		//returns true if all tiles can make a meld, returns false if a tile cannot make a meld
		private static boolean populateMeldStacks(GameTileList checkTiles){
			for (GameTile t: checkTiles)
				if (!checkMeldableTile((HandCheckerTile)t, checkTiles)) return false;
			
			return true;
		}
		
		public List<Meld> getFinishingMelds(){
			List<Meld> finishingMelds = new ArrayList<Meld>(5);
			isCompleteNormal(finishingMelds);
			return finishingMelds;
		}
		
		
		
		//checks if a hand is complete (one pair + n number of シュンツ/コーツ)
		//recursive method
		private static boolean isCompleteNormalHand(GameTileList checkTiles, List<Meld> finishingMelds, AtomicBoolean pairHasBeenChosen){
			if (checkTiles.isEmpty()) return true;	//if the hand is empty, it is complete (base case)
			
			HandCheckerTile currentTile = (HandCheckerTile)checkTiles.getFirst();
			
			//loop until every possible meld type has been tried for the current tile
			while(currentTile.mstackIsEmpty() == false){
				
				//~~~~Verify that currentTile's partners are still in the hand
				int[] currentTileParterIDs = currentTile.mstackTopParterIDs();
				MeldType currentTileMeldType = currentTile.mstackPop();	//(remove it)
				boolean currentTilePartersAreStillHere = currentTilePartersAreStillHere(currentTileMeldType, checkTiles, currentTile, currentTileParterIDs);
				
				//if (currentTile's partners for the meld are no longer here) OR (currentTileMeldType is pair and pair has already been chosen)
				if (currentTilePartersAreStillHere == false || (currentTileMeldType == MeldType.PAIR && pairHasBeenChosen.get()))
					continue;	//exit loop early and continue to the next meldType
					
				//take the pair privelige if the current meldType is a pair
				if (currentTileMeldType == MeldType.PAIR) pairHasBeenChosen.set(true);
				
				//~~~~Find the inidces of currentTile's partners for the current meldType					
				List<Integer> partnerIndices = findIndicesOfCurrentTilePartersForMeldType(currentTileMeldType, checkTiles, currentTile, currentTileParterIDs);
				
				//make a copy of the hand, then remove the meld tiles from the copy and add them to the meld
				GameTileList checkTilesMinusThisMeld = HandCheckerTile.makeCopyOfListWithCheckers(checkTiles);
				GameTileList toMeldTiles = removeMeldTilesFrom(checkTilesMinusThisMeld, partnerIndices);
				
				//~~~~Recursive call, check if the hand is still complete without the removed meld tiles
				if (isCompleteNormalHand(checkTilesMinusThisMeld, finishingMelds, pairHasBeenChosen)){
					if (finishingMelds != null) finishingMelds.add(new Meld(toMeldTiles.clone(), currentTileMeldType));	//add the meld tiles to the finishing melds stack
					return true;	//hand is complete
				}
				else{
					if (currentTileMeldType == MeldType.PAIR)
						pairHasBeenChosen.set(false);	//relinquish the pair privelege, if it was taken
				}
			}
			return false;	//currentTile could not make any meld, so the hand must not be complete
		}
		private static boolean isCompleteNormalHand(GameTileList checkTiles, List<Meld> finishingMelds){return isCompleteNormalHand(HandCheckerTile.makeCopyOfListWithCheckers(checkTiles), finishingMelds, new AtomicBoolean(false));}
		
		
		//extracted methods to make isCompleteNormalHand more readable
		private static boolean currentTilePartersAreStillHere(MeldType currentTileMeldType, GameTileList checkTiles, HandCheckerTile currentTile, int[] currentTileParterIDs){
			if (currentTileMeldType.isChi())
				if (!checkTiles.contains(currentTileParterIDs[0]) || !checkTiles.contains(currentTileParterIDs[1]))
					return false;
			if (currentTileMeldType == MeldType.PAIR && checkTiles.findHowManyOf(currentTile) < NUM_PARTNERS_NEEDED_TO_PAIR + 1)
				return false;
			if (currentTileMeldType == MeldType.PON && checkTiles.findHowManyOf(currentTile) < NUM_PARTNERS_NEEDED_TO_PON + 1)
				return false;
			
			return true;
		}
		
		private static List<Integer> findIndicesOfCurrentTilePartersForMeldType(MeldType currentTileMeldType, GameTileList checkTiles, HandCheckerTile currentTile, int[] currentTileParterIDs){
			List<Integer> partnerIndices = new ArrayList<Integer>();
			//if chi, just find the partners
			if (currentTileMeldType.isChi()){
				partnerIndices.add(checkTiles.indexOf(currentTileParterIDs[0]));
				partnerIndices.add(checkTiles.indexOf(currentTileParterIDs[1]));
				return partnerIndices;
			}
			//else if pon/pair, make sure you don't count the tile itsef
			partnerIndices = checkTiles.findAllIndicesOf(currentTile);
			
			//trim the lists down to size to fit the meld type
			if (currentTileMeldType == MeldType.PAIR) while(partnerIndices.size() > NUM_PARTNERS_NEEDED_TO_PAIR) partnerIndices.remove(partnerIndices.size() - 1);
			if (currentTileMeldType == MeldType.PON) while(partnerIndices.size() > NUM_PARTNERS_NEEDED_TO_PON) partnerIndices.remove(partnerIndices.size() - 1);
			return partnerIndices;
		}
		
		private static GameTileList removeMeldTilesFrom(GameTileList checkTilesMinusThisMeld, List<Integer> partnerIndices){
			GameTileList toMeldTiles = new GameTileList();
			
			while (!partnerIndices.isEmpty())
				toMeldTiles.add(checkTilesMinusThisMeld.remove( partnerIndices.remove(partnerIndices.size() - 1).intValue()) );
			toMeldTiles.add(checkTilesMinusThisMeld.removeFirst());
			return toMeldTiles;
		}
		
	}
	
	private static final GameTileList emptyWaitsList(){return new GameTileList();}
}
