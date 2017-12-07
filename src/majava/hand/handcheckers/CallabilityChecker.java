package majava.hand.handcheckers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import majava.enums.MeldType;
import majava.enums.Wind;
import majava.hand.Hand;
import majava.tiles.GameTile;
import majava.util.GameTileList;
import majava.util.TileKnowledge;


//checks what calls a hand can make on a given tile (chi, pon, ron, etc)
public class CallabilityChecker {
	private static final int NUM_PARTNERS_NEEDED_TO_PON = 2;
	private static final int NUM_PARTNERS_NEEDED_TO_KAN = 3;
	private static final int OFFSET_CHI_L1 = 1, OFFSET_CHI_L2 = 2;
	private static final int OFFSET_CHI_M1 = -1,  OFFSET_CHI_M2 = 1;
	private static final int OFFSET_CHI_H1 = -2, OFFSET_CHI_H2 = -1;
	
	
	private final Hand hand;
	private final GameTileList handTiles;
	
	public CallabilityChecker(Hand handToCheck, GameTileList reveivedHandTiles){
		hand = handToCheck;
		handTiles = reveivedHandTiles;
	}
	
	private Wind ownerSeatWind(){return hand.getOwnerSeatWind();}
	
	
	
	

	public boolean tileIsCallable(GameTile candidate){
		boolean handIsInTenpai = hand.isInTenpai();	//use temporary variable to avoid having to calculate twice
		boolean flagCanChiL = false, flagCanChiM = false, flagCanChiH = false, flagCanPon = false, flagCanRon = false;
		
		//if candidate is not a hot tile, return false
		if (!tileIsHot(candidate) && !handIsInTenpai) return false;
		
		if (tileCameFromChiablePlayer(candidate)){
			flagCanChiL = ableToChiL(candidate);
			flagCanChiM = ableToChiM(candidate);
			flagCanChiH = ableToChiH(candidate);
		}
		//check pon (don't bother checking kan. you know why.)
		flagCanPon = ableToPon(candidate);
		
		//if in tenpai, check ron
		flagCanRon = handIsInTenpai && ableToRon(candidate);
		
		//~~~~return true if a call (any call) can be made
		return (flagCanChiL || flagCanChiM || flagCanChiH || flagCanPon || flagCanRon);
	}
	
	private boolean tileIsHot(GameTile candidate){return TileKnowledge.findAllHotTiles(handTiles).contains(candidate.getId());}
	
	public boolean tileCameFromChiablePlayer(GameTile candidate){
		return (candidate.getOrignalOwner() == ownerSeatWind()) || 
				(candidate.getOrignalOwner() == ownerSeatWind().kamichaWind());
	}
	
	//returns the partner indices list for a given meld type
	public List<Integer> getPartnerIndices(GameTile candidate, MeldType meldType){
		switch (meldType){
		case CHI_L: return getPartnerIndicesChiL(candidate);
		case CHI_M: return getPartnerIndicesChiM(candidate);
		case CHI_H: return getPartnerIndicesChiH(candidate);
		case PON: return getPartnerIndicesPon(candidate);
		case KAN: return getPartnerIndicesKan(candidate);
		default: return null;
		}
	}
	//シュンツ
	private List<Integer> getPartnerIndicesChiType(GameTile candidate, int offset1, int offset2){
		if (!tileCameFromChiablePlayer(candidate)) return emptyIndicesList();
		if (handTiles.contains(candidate.getId() + offset1) && handTiles.contains(candidate.getId() + offset2))
			return Arrays.asList(handTiles.indexOf(candidate.getId() + offset1), handTiles.indexOf(candidate.getId() + offset2));
		return emptyIndicesList();
	}
	private List<Integer> getPartnerIndicesChiL(GameTile candidate){return getPartnerIndicesChiType(candidate, OFFSET_CHI_L1, OFFSET_CHI_L2);}
	private List<Integer> getPartnerIndicesChiM(GameTile candidate){return getPartnerIndicesChiType(candidate, OFFSET_CHI_M1, OFFSET_CHI_M2);}
	private List<Integer> getPartnerIndicesChiH(GameTile candidate){return getPartnerIndicesChiType(candidate, OFFSET_CHI_H1, OFFSET_CHI_H2);}
	//コーツ
	private List<Integer> getPartnerIndicesMulti(GameTile candidate, int numPartnersNeeded){		
		//pon/kan is possible if there are enough partners in the hannd to form the meld
		List<Integer> partnerIndices = handTiles.findAllIndicesOf(candidate);
		if (partnerIndices.size() >= numPartnersNeeded)
			return partnerIndices.subList(0, numPartnersNeeded);
		return emptyIndicesList();
	}
	public List<Integer> getPartnerIndicesPon(GameTile candidate){return getPartnerIndicesMulti(candidate, NUM_PARTNERS_NEEDED_TO_PON);}
	public List<Integer> getPartnerIndicesKan(GameTile candidate){return getPartnerIndicesMulti(candidate, NUM_PARTNERS_NEEDED_TO_KAN);}
	private static List<Integer> emptyIndicesList(){return new ArrayList<Integer>();}
	//ableToCall methods
	public boolean ableToChiL(GameTile candidate){return candidate.canCompleteChiL() && !getPartnerIndicesChiL(candidate).isEmpty();}
	public boolean ableToChiM(GameTile candidate){return candidate.canCompleteChiM() && !getPartnerIndicesChiM(candidate).isEmpty();}
	public boolean ableToChiH(GameTile candidate){return candidate.canCompleteChiH() && !getPartnerIndicesChiH(candidate).isEmpty();}
	public boolean ableToPon(GameTile candidate){return !getPartnerIndicesPon(candidate).isEmpty();}
	public boolean ableToKan(GameTile candidate){return !getPartnerIndicesKan(candidate).isEmpty();}
	public boolean ableToRon(GameTile candidate){return hand.getTenpaiWaits().contains(candidate);}
	
	
}
