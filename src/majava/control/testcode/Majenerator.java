package majava.control.testcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.jws.soap.SOAPBinding;

import majava.hand.AgariHand;
import majava.hand.Hand;
import majava.hand.Meld;
import majava.pond.Pond;
import majava.RoundResult;
import majava.util.GTL;
import majava.util.YakuList;
import majava.yaku.Yaku;
import majava.enums.MeldType;
import majava.enums.Wind;
import majava.player.Player;
import majava.pond.RiverWalker;
import majava.summary.PaymentMap;
import majava.summary.RoundResultSummary;
import majava.tiles.GameTile;
import majava.tiles.Janpai;
import majava.tiles.TileInterface;


public class Majenerator {
	private static final int E=0, S=1, W=2, N=3;
	private static final MeldType[] DEFAULT_ALLOWED_MELDTYPES = {MeldType.CHI_L, MeldType.CHI_M, MeldType.CHI_H, MeldType.PON, MeldType.KAN};
	private static boolean allowChiitoiKokushi = true;
	
	private static final int NUM_PLAYERS = 4;
	private static final int NUM_TILES = 34;
	private static final Random randGen = new Random();
	
	private static final Wind OWNER_WIND = Wind.SOUTH;
	
	
	
	public static void main(String[] args){
		SomeTesters.main(null);
		
//		generatePonds();
		
//		generateAgariHand();
		
//		println(generateRoundResult().toString());
//		for (Yaku y: generateYakuList()) println(y.toString());
	}
	
	
	public static Pond generatePond(){return generatePonds()[0];}
	public static Pond[] generatePonds(){
		
		GTL p1Tiles = new GTL(1,2,3,4);
		GTL p2Tiles = new GTL(10, 11, 12, 13);
		GTL p3Tiles = new GTL(7, 7, 7, 7);
		GTL p4Tiles = new GTL(33, 33, 33);
		
//		Pond p1 = new Pond(), p2 = new Pond(), p3 = new Pond(), p4 = new Pond();
		Pond p1 = new Pond(p1Tiles), p2 = new Pond(p2Tiles), p3 = new Pond(p3Tiles), p4 = new Pond(p4Tiles);
		
		
		return new Pond[]{p1, p2, p3, p4};
	}
	public static Pond[] generatePondsWithCalls(){
		Pond p1 = new Pond(), p2 = new Pond(), p3 = new Pond(), p4 = new Pond();
		
		p1 = p1.addTile(5);
		p1 = p1.removeMostRecentTile(Wind.WEST);
		p3 = p3.addTile(18);
		
		p4 = p4.addTile(12);
		p1 = p1.addTile(5);
		p2 = p2.addTile(32);
		p2 = p2.removeMostRecentTile(Wind.EAST);
		p1 = p1.addTile(1);
		

		p2 = p2.addTile(32);
		p2 = p2.removeMostRecentTile(Wind.EAST);
		p1 = p1.addTile(2);

		p2 = p2.addTile(33);
		
		//final here
		int whoCalledFinal = 4;
		if (whoCalledFinal == 1){
			p2 = p2.removeMostRecentTile(Wind.EAST);
			p1 = p1.addTile(6);
		}
		else {
			p2 = p2.removeMostRecentTile(Wind.NORTH);
			p4 = p4.addTile(6);
		}
		
		
//		println("\n" + p1 + "\n\n\n" + p3);
		
		
		return new Pond[]{p1, p2, p3, p4};
	}
	public static Pond[] generatePondsWithCallsFew(){
		Pond[] pts = generatePondsWithCallsFewAfterCallButBeforeDiscard();
		pts[3] = pts[3].addTile(8);
		return pts;
	}
	public static Pond[] generatePondsWithCallsFewAfterCallButBeforeDiscard(){
		Pond p1 = new Pond(), p2 = new Pond(), p3 = new Pond(), p4 = new Pond();
		
		p1 = p1.addTile(16);
		p2 = p2.addTile(1);
		p2 = p2.removeMostRecentTile(Wind.NORTH);
//		p4 = p4.addTile(8);
		
		return new Pond[]{p1, p2, p3, p4};
	}
	public static Pond[] generatePondsFew(){
		Pond[] fews = generatePondsEmpty();
		
		fews[0] = fews[0].addTile(1);
		fews[1] = fews[1].addTile(2);
		fews[2] = fews[2].addTile(3);
		
		return fews;
	}
	public static Pond[] generatePondsEmpty(){
		Pond p1 = new Pond(), p2 = new Pond(), p3 = new Pond(), p4 = new Pond();
		return new Pond[]{p1, p2, p3, p4};
	}
	public static Pond[] generatePondsOneTile(){
		Pond[] one = generatePondsEmpty();
		
		one[0] = one[0].addTile(17);
		
		return one;
	}
	
	
	
	
	public static AgariHand agariHandFromIDs(Integer... ids){
		
		Hand hand = new Hand(new GTL(ids)).setOwnerSeatWind(OWNER_WIND).sort();
		
		GameTile agarihai = hand.getLastTile();
		hand = hand.removeLastTile();
		
//		agarihai = agarihai.withOwnerWind(OWNER_WIND.kamichaWind());
		
		AgariHand ah = new AgariHand(hand, agarihai);
		return ah;
	}
	
	
	
	public static YakuList generateYakuList(){
		
		final int MAX_HOW_MANY = 10;
		
		YakuList yakuList = new YakuList();
		int howMany = 1+randGen.nextInt(MAX_HOW_MANY);
		
		Yaku[] allYaku = Yaku.values();
		for (int i = 0; i < howMany; i++){
			yakuList.add(allYaku[randGen.nextInt(allYaku.length)]);
		}
		
		return yakuList;
	}
	
	
	
	
	
	public static RoundResultSummary generateRoundResultSummary(){
		return generateRoundResult().getSummary();
	}
	public static RoundResult generateRoundResult(){
		
		RoundResult res = new RoundResult();
		
		Player[] players = {generatePlayer(0), generatePlayer(1), generatePlayer(2), generatePlayer(3)};
		int windex = randGen.nextInt(NUM_PLAYERS), losedex; do{losedex = randGen.nextInt(NUM_PLAYERS);}while(losedex == windex);
		
		Player winner = players[windex];
		Player furi = players[losedex];
		
		
		WinningHandAndMelds wham = generateWinningHandAndMelds();
//		WinningHandAndMelds wham = generateWinningHandAndMelds(0);
		List<Meld> winMelds = wham.getMelds();
		GTL winHandTiles = wham.getHand().getTiles();
		
		
		int removeIndex = randGen.nextInt(winHandTiles.size());
		GameTile winningTile = winHandTiles.get(removeIndex);
		winHandTiles = winHandTiles.remove(removeIndex);
		
		
		if (randGen.nextBoolean()) res.setVictoryRon(winner, furi);
		else res.setVictoryTsumo(winner);
		
		res.setWinningHand(winHandTiles, winMelds, winningTile);
		
		
		PaymentMap payments = generatePaymentsMap(players, res);
		res.recordPayments(payments);
		
		return res;
	}
	public static PaymentMap generatePaymentsMap(Player[] players, RoundResult res){
		PaymentMap playerPaymentMap = new PaymentMap();
		
		final double DEALER_WIN_MULTIPLIER = 1.5, DEALER_TSUMO_MULTIPLIER = 2;
		
		int paymentDue = 8000;
		int tsumoPointsNonDealer = paymentDue / 4;
		int tsumoPointsDealer = (int) (DEALER_TSUMO_MULTIPLIER * tsumoPointsNonDealer);
		
		//find who the winner is
		Player winner = res.getWinningPlayer(); int windex = Arrays.asList(players).indexOf(winner);
		Player[] losers = {players[(windex+1)%4], players[(windex+2)%4], players[(windex+3)%4]};
		Player furikonda = null;
		
		if (winner.isDealer()) paymentDue *= DEALER_WIN_MULTIPLIER;
		
		///////add in honba here
		
		playerPaymentMap.put(winner, paymentDue);
		
		
		//find who has to pay
		if (res.isVictoryRon()){
			furikonda = res.getFurikondaPlayer();
			for (Player p: losers)
				if (p == furikonda) playerPaymentMap.put(p, -paymentDue);
				else playerPaymentMap.put(p, 0);
		}
		else{//tsumo
			for (Player p: losers){
				if (p.isDealer() || winner.isDealer()) playerPaymentMap.put(p, -tsumoPointsDealer);
				else  playerPaymentMap.put(p, -tsumoPointsNonDealer);
			}
		}
		///////add in riichi sticks here
		return playerPaymentMap;
	}
	
	
	
	
	
	
	public static GTL generateWinningHandTiles(){
		WinningHandAndMelds wham = generateWinningHandAndMelds();
		
		return wham.getHand().getTiles();
	}
	
	
	public static AgariHand generateAgariHandOnlyPonkan(){return generateAgariHand(MeldType.listOfMultiTypes());}
	public static AgariHand generateAgariHandOnlyChi(){return generateAgariHand(MeldType.listOfChiTypes());}
	
	
	public static AgariHand generateAgariHand(int howManyMelds, MeldType[] onlyAllowTheseMeldTypes){
		final Wind ownerWind = OWNER_WIND;
		
		WinningHandAndMelds wham = generateWinningHandAndMelds(howManyMelds, onlyAllowTheseMeldTypes);		
		GTL winHand = wham.getHand().getTiles();
		List<Meld> winMelds = wham.getMelds();
		
		
		
//		winHand = winHand.withWind(ownerWind);
//		for (Meld m: winMelds)
//			for (GameTile t: m)
//				t.setOwner(ownerWind);
		
		//set owner wind for hand, tiles, and melds
		//add tiles to hand, and melds to hand
		Hand hand = new Hand(ownerWind);
		for (GameTile t: winHand) hand.addTile(t.withOwnerWind(ownerWind));
		for (Meld m: winMelds) hand.DEMOaddMeld(m.DEMO_setWind(ownerWind));
		
		//set agarihai
		int agariIndex = randGen.nextInt(hand.size());
		GameTile agarihai = hand.getTile(agariIndex);
		hand = hand.removeTile(agariIndex);
		
		if (randGen.nextBoolean()) agarihai = agarihai.withOwnerWind(ownerWind.kamichaWind());	//decide tsumo/ron
		
		AgariHand ah = new AgariHand(hand, agarihai);
		
//		println(winHand.toString());println(winMelds.toString());println(hand.toString());println("\n\n----uh here's agarihand" + ah.toString());
		
		return ah;
	}
	public static AgariHand generateAgariHand(MeldType[] onlyAllowTheseMeldTypes){return generateAgariHand(randGen.nextInt(5), onlyAllowTheseMeldTypes);}
	public static AgariHand generateAgariHand(int howManyMelds){return generateAgariHand(howManyMelds, DEFAULT_ALLOWED_MELDTYPES);}
	public static AgariHand generateAgariHand(){return generateAgariHand(randGen.nextInt(5));}
	
	
	
	
	
	public static WinningHandAndMelds generateWinningHandAndMelds(int howManyMelds, MeldType[] onlyAllowTheseMeldTypes){
		GTL winHand = new GTL();
		List<Meld> winMelds = new ArrayList<Meld>();
		
		
		
		//sometimes do chiitoi and kokushi
//		if (true){
//			winHand = generateHandTilesKokushi();
//			winHand = generateHandTilesChiitoi();
//			return new WinningHandAndMelds(winHand, winMelds);
//		}
		if (allowChiitoiKokushi && (randGen.nextInt(15) == 14)){
			if (randGen.nextBoolean())  winHand = generateHandTilesKokushi();
			else winHand = generateHandTilesChiitoi();
				
			return new WinningHandAndMelds(winHand, winMelds);
		}
		
		
		
		List<Meld> handMelds = new ArrayList<Meld>();
		List<Meld> recipient = null;
		Meld candidateMeld = null;
		
		//form melds
		int meldsFormed = 0;
		while (meldsFormed < 4){
			if (meldsFormed < howManyMelds){
				recipient = winMelds;
				candidateMeld = generateMeld(onlyAllowTheseMeldTypes);
			}
			else{
				recipient = handMelds;
				candidateMeld = generateMeld(randomMeldTypeNoKan(onlyAllowTheseMeldTypes));
			}
			
			
			if (!__meldWouldViolateTileLimit(candidateMeld, handMelds, winMelds)){
				recipient.add(candidateMeld);
				meldsFormed++;
			}
		}
		
		//form pair
		while (__meldWouldViolateTileLimit((candidateMeld = generateMeld(MeldType.PAIR)), handMelds, winMelds));
		handMelds.add(candidateMeld);
		
		//add hand meld tiles to hand
		for (Meld m: handMelds) winHand = winHand.addAll(m);
		winHand = winHand.sort();
		
		return new WinningHandAndMelds(winHand, winMelds);
	}
	public static WinningHandAndMelds generateWinningHandAndMelds(MeldType[] onlyAllowTheseMeldTypes){return generateWinningHandAndMelds(randGen.nextInt(5), onlyAllowTheseMeldTypes);}
	public static WinningHandAndMelds generateWinningHandAndMelds(int howManyMelds){return generateWinningHandAndMelds(howManyMelds, DEFAULT_ALLOWED_MELDTYPES);}
	public static WinningHandAndMelds generateWinningHandAndMelds(){return generateWinningHandAndMelds(randGen.nextInt(5));}

//	public static AgariHand generateAgariHand(MeldType[] onlyAllowTheseMeldTypes){return generateAgariHand(randGen.nextInt(5), onlyAllowTheseMeldTypes);}
//	public static AgariHand generateAgariHand(int howManyMelds){return generateAgariHand(howManyMelds, DEFAULT_ALLOWED_MELDTYPES);}
//	public static AgariHand generateAgariHand(){return generateAgariHand(randGen.nextInt(5));}
	
	
	
	
	private static boolean __meldWouldViolateTileLimit(Meld candidateMeld, GTL existingTiles){
		
		//chis
		if (candidateMeld.isChi()){
			for (GameTile t: candidateMeld) if (existingTiles.findHowManyOf(t) >= 4) return true;
			return false;
		}
		
		//pon, kan, pair
		if ((existingTiles.findHowManyOf(candidateMeld.getFirstTile()) + candidateMeld.size()) > 4) return true;
		return false;
	}
	private static boolean __meldWouldViolateTileLimit(Meld candidateMeld, List<Meld> handMelds, List<Meld> melds){	
		
		GTL existingTiles = new GTL();
		for (Meld m: handMelds) existingTiles = existingTiles.addAll(m);
		for (Meld m: melds) existingTiles = existingTiles.addAll(m);
		
		
		return __meldWouldViolateTileLimit(candidateMeld, existingTiles);
	}
	
	
	
	
	public static GTL generateHandTilesChiitoi(){
		int id;
		GTL tlist = new GTL();
		while (tlist.size() != 14){
			id = 1+randGen.nextInt(NUM_TILES);
			if (!tlist.contains(new GameTile(id)))
				tlist = tlist.addAll(new GTL(id, id));
		}
		
		return tlist.sort();
	}
	public static GTL generateHandTilesKokushi(){
		Integer[] yaochuuIDs = Janpai.retrieveYaochuuTileIDs();
		GTL tlist = new GTL(yaochuuIDs).add(yaochuuIDs[randGen.nextInt(13)]).sort();
		return tlist;
	}
	
	public static void optionForbidChiitoiAndKokushi(){allowChiitoiKokushi = false;}
	public static void optionAllowChiitoiAndKokushi(){allowChiitoiKokushi = true;}
	
	
	
	public static Meld generateMeld(final MeldType type, final boolean closed){
		GTL meldTiles = null;
		
		int id = 1;
		while(!tileCanMeldMeldType((id = 1+randGen.nextInt(NUM_TILES)), type));
		
		switch (type){
		case CHI_L: meldTiles = new GTL(id, id + 1, id + 2); break;
		case CHI_M: meldTiles = new GTL(id - 1, id, id + 1); break;
		case CHI_H: meldTiles = new GTL(id - 2, id - 1, id); break;
		case KAN: meldTiles = new GTL(id, id, id, id); break;
		case PON: meldTiles = new GTL(id, id, id); break;
		case PAIR: meldTiles = new GTL(id, id); break;
		default: break;
		}
		
		return new Meld(meldTiles, type);
	}
	public static Meld generateMeld(MeldType mt){return generateMeld(mt, true);}
	public static Meld generateMeld(MeldType[] onlyAllowTheseMeldTypes){return generateMeld(randomMeldType(onlyAllowTheseMeldTypes));}
	public static Meld generateMeld(){return generateMeld(DEFAULT_ALLOWED_MELDTYPES);}
	
	public static boolean tileCanMeldMeldType(Janpai tile, MeldType mt){
		if (tile.getId() == 0) return false;
		
		//pon/kan/pair
		if (mt.isMulti()) return true;
		
		//chis
		if (tile.isHonor()) return false;
		
		switch (mt){
		case CHI_L: return (tile.getFace() != '8' && tile.getFace() != '9');
		case CHI_M: return (tile.getFace() != '1' && tile.getFace() != '9');
		case CHI_H: return (tile.getFace() != '1' && tile.getFace() != '2');
		default: return false;
		}
	}
	public static boolean tileCanMeldMeldType(int tId, MeldType mt){return tileCanMeldMeldType(Janpai.retrieveTile(tId), mt);}
	
	
	
	
	public static MeldType randomMeldType(MeldType[] onlyAllowTheseMeldTypes){
		return onlyAllowTheseMeldTypes[randGen.nextInt(onlyAllowTheseMeldTypes.length)];
	}
	public static MeldType randomMeldType(){return randomMeldType(DEFAULT_ALLOWED_MELDTYPES);}
	
	public static MeldType randomMeldTypeNoKan(MeldType[] onlyAllowTheseMeldTypes){
		MeldType mt = randomMeldType(onlyAllowTheseMeldTypes);
		while ((mt = randomMeldType(onlyAllowTheseMeldTypes)).isKan());	//loop until something other than kan is chosen
		return mt;
	}
	public static MeldType randomMeldTypeNoKan(){return randomMeldTypeNoKan(DEFAULT_ALLOWED_MELDTYPES);}
	
	
	
	
	
	
	
	public static Player generatePlayer(int playernum){
		String[] names = {"Suwado", "Albert", "Brenda", "Carl", "Dick", "Eddie", "Geromy", "Halbert", "Little King John"};
		String randName = names[randGen.nextInt(names.length)];
		
		if (playernum < 0 || playernum >= 4)
			playernum = 0;
		
		Player player = new Player(randName);
		player = player.setControllerComputer();
		
		switch(playernum){
		case 0: player = player.setSeatWindEast(); break;
		case 1: player = player.setSeatWindSouth(); break;
		case 2: player = player.setSeatWindWest(); break;
		case 3: player = player.setSeatWindNorth(); break;
		default: break;
		}
		
//		set player number?
		
		player.DEMOfillHandNoTsumo();////////////////
		
		return player;
	}
	public static Player generatePlayer(){return generatePlayer(randGen.nextInt(NUM_PLAYERS));}
	
	
	

	public static void println(String prints){System.out.println(prints);}public static void println(){System.out.println("");}public static void println(int prints){System.out.println(prints+"");}
}





