package majava.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import majava.yaku.Yaku;

public class YakuList extends ArrayList<Yaku>{
	private static final long serialVersionUID = 2608398840570577517L;


	public YakuList(Yaku... yakus){super(Arrays.asList(yakus));}
	public YakuList(YakuList other){
		this();
		addAll(other);
	}
	public YakuList clone(){return new YakuList(this);}
	
	
	public int totalHan(){
		int total = 0;
		for (Yaku y: this){
			total += y.getValueClosed();
		}
		return total;
	}
	

	
	
	public boolean containsYakuman(){
		for (Yaku y: this) if (y.isYakuman()) return true;
		return false;
	}
	
	
	
	//remove overlapping yaku of lesser value (ie, iipeiko < ryanpeiko)
	public void removeSmallFry(){
		
		//if contains yakuman, weed out non-yakuman
		if (containsYakuman()){
			retainAll(Yaku.listOfAllYakuman());
			if (contains(Yaku.YKM_DAISHUUSHII)) remove(Yaku.YKM_SHOUSHUUSHII);
			
			return;
		}
		
		
		//if no yakuman, weed out overlapping yaku
		
		if (contains(Yaku.SHOUSANGEN))
			removeAll(Arrays.asList(Yaku.YAKUHAI_DRAGON_HAKU, Yaku.YAKUHAI_DRAGON_HATSU, Yaku.YAKUHAI_DRAGON_CHUN));
		
		if (contains(Yaku.RYANPEIKOU))
			removeAll(Arrays.asList(Yaku.CHIITOITSU, Yaku.IIPEIKOU));
		
		if (contains(Yaku.RIICHI_DOUBLE))
			remove(Yaku.RIICHI);
		
		
		
		if (contains(Yaku.HONROUTO))
			removeAll(Arrays.asList(Yaku.CHIITOITSU, Yaku.TOITOI));
		
		if (contains(Yaku.JUNCHAN))
			remove(Yaku.CHANTA);
		
		
		if (contains(Yaku.CHINITSU))
			remove(Yaku.HONITSU);
		
	}
}
