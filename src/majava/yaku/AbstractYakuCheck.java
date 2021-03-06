package majava.yaku;

import java.util.List;

import majava.hand.AgariHand;
import majava.hand.Hand;
import majava.hand.Meld;
import majava.util.GTL;
import majava.util.YakuList;

public abstract class AbstractYakuCheck {
	
	protected final AgariHand hand;
	
	public AbstractYakuCheck(AgariHand ah){
		hand = ah;
	}
	
	
	
	final public YakuList getElligibleYaku(){
		final YakuList elligibleYakus = new YakuList();
		
		findElligibleYaku(elligibleYakus);
		
		return elligibleYakus;
	}
	
	//the subclass should find elligible yaku, and store them in the list putElligibleYakuHere
	public abstract void findElligibleYaku(final YakuList putElligibleYakuHere);
	
	
	
	
	protected AgariHand getHand(){return hand;}
	protected int handSize(){return hand.size();}
	
	protected boolean handIsKokushiOrChiitoi(){return hand.isCompleteKokushi() || hand.isCompleteChiitoitsu();}
	
	protected GTL handInTilesForm(){return hand.getTilesAsListIncludingMeldTiles();}
	protected List<Meld> handInMeldForm(){return hand.getMeldForm();}
}
