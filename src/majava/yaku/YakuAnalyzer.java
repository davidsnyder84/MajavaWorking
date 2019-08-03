package majava.yaku;

import java.util.ArrayList;
import java.util.List;

import majava.RoundResult;
import majava.hand.AgariHand;
import majava.hand.Hand;
import majava.util.YakuList;

public class YakuAnalyzer {
	
	
	private final AgariHand hand;
	private final RoundResult roundResult;
	
	private final List<AbstractYakuCheck> checks;
	
	
	public YakuAnalyzer(Hand receivedHand, RoundResult result){
		roundResult = result;
		hand = new AgariHand(receivedHand, roundResult.getWinningTile());		
		
		checks = new ArrayList<AbstractYakuCheck>();
		makeCheckers();
	}
	
	private void makeCheckers(){
		
		checks.add(new TerminalHonorYakuCheck(hand));
		checks.add(new KotsuYakuCheck(hand));
		
		checks.add(new ChankanCheck(hand));
		checks.add(new HaiteiCheck(hand));
		checks.add(new MenzenTsumoCheck(hand));
		checks.add(new RiichiCheck(hand));
		checks.add(new RinshanCheck(hand));
		checks.add(new LimitTenhouCheck(hand));
		checks.add(new DoraCheck(hand));
		checks.add(new FanpaiYakuhaiCheck(hand));
		
		checks.add(new ChiitoitsuCheck(hand));
		checks.add(new LimitKokushiCheck(hand));
		
		checks.add(new ChinitsuHonitsuCheck(hand));
		checks.add(new IttsuuCheck(hand));
		checks.add(new LimitChuurenpoutoCheck(hand));
		
		checks.add(new LimitRyuuiisouCheck(hand));
		checks.add(new LimitDaisangenCheck(hand));
		checks.add(new LimitDaisuushiCheck(hand));
		
		checks.add(new SanshokuDoukouCheck(hand));
		checks.add(new PeikouCheck(hand));
		checks.add(new PinfuCheck(hand));
		checks.add(new SanshokuDoujunCheck(hand));
	}
	
	
	public YakuList getAllElligibleYaku(){
		YakuList elligibleYakus = new YakuList();
		for (AbstractYakuCheck checker: checks)
			elligibleYakus.addAll(checker.getElligibleYaku());
		
		return elligibleYakus;
	}
	
}
