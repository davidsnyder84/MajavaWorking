package majava;


import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import utility.ConviniList;
import majava.tiles.GameTile;
import majava.tiles.TileInterface;
import majava.util.GameTileList;
import majava.enums.MeldType;
import majava.enums.Wind;


/*
Class: Meld
represents a meld of tiles (chi, pon, kan, pair)

data:
	mTiles - list of tiles in the meld
	mMeldType - the type of meld (chi, pon, kan, pair)
	mClosed = if the meld is a closed meld, this will be true, false otherwise
	mFu - the fu value of the meld
	
	mOwnerSeatWind - the seat wind of the owner of the meld (needed for checking fu of wind pairs)
	
	mCompletedTile - the tile that completed the meld
	mPlayerResponsible - the player who contributed the completing tile to the meld
	
methods:
	constructors:
	3-arg: requires list of tiles from hand, the completing tile, and meld type
	2-arg: requires list of tiles from hand and meld type
	copy constructor
	
	public:
		mutators:
		upgradeToMinkan - uses the given tile to upgrade an open pon to a minkan
		
		
	 	accessors:
	 	calculateFu - returns the amount of fu points the meld is worth
	 	isClosed - returns true if the meld is closed
	 	
	 	getOwnerSeatWind - returns the seat wind of the meld's owner
	 	getResponsible - returns the seat wind of the player responsible for completing the meld
	 	
	 	getTile - returns the tile at the given index in the meld
	 	getAllTiles - returns a list of all tiles in the meld
		getSize - returns how many tiles are in the meld
		isChi, isPon, isKan - returns true if the meld is of the corresponding type
*/
public class Meld implements Iterable<GameTile>, Comparable<Meld>, Cloneable {
	
	private static final int FU_DEFAULT = 0;
	
	
	
	//list of tiles in the meld
	private GameTileList mTiles;
	
	private MeldType mMeldType;
	private boolean mClosed;

	private Wind mOwnerSeatWind;
	
	private GameTile mCompletedTile;
	private Wind mPlayerResponsible;
	
	private int mFu;
	
	
	
	
	/*
	3-arg Constructor
	forms a meld from the given tiles, of the given meld type
	
	input: handTiles are the tiles that are coming from the player's hand
		   newTile is the "new" tile that is being added to form the meld
		   meldType is the type of meld that is being formed
	
	form the meld
	fu = 0
	*/
	public Meld(GameTileList handTiles, GameTile newTile, MeldType meldType){		
		
		__formMeld(handTiles, newTile, meldType);
		
		mFu = FU_DEFAULT;
	}
	//2-arg, takes list of tiles and meld type (used when making a meld only from hand tiles, so no "new" tile)
	//passes (handtiles 0 to n-1, handtile n, and meld type)
	public Meld(GameTileList handTiles, MeldType meldType){
		this(handTiles.subList(0, handTiles.size() - 1), handTiles.get(handTiles.size() - 1), meldType);
	}
	
	
	private Meld(Meld other){
		
		mTiles = other.getAllTiles();
		
		mOwnerSeatWind = other.mOwnerSeatWind;
		
		mClosed = other.mClosed;
		mFu = other.mFu;
		
		mCompletedTile = other.mCompletedTile;
		mPlayerResponsible = other.mPlayerResponsible;
	}
	public Meld clone(){return new Meld(this);}
	
	
	
	
	
	/*
	private method: __formMeld
	forms a meld of the given type with the given tiles
	
	input: handTiles are the tiles from the player's hand
		   newTile is the "new" tile that is added to form the meld
		   meldType is the type of meld that is being formed
	
	
	set onwer's seat wind = owner of the hand tiles
	set player responsible = player who discarded the newTile
	set tile that completed the meld = newTile
	set meld type
	set closed = (responsible's wind == owner's wind)
	
	add the hand tiles to the meld
	add the new tile to the meld
	sort the meld if it is a chi
	*/
	private void __formMeld(GameTileList handTiles, GameTile newTile, MeldType meldType){
		
		//set the owner's seat wind
		mOwnerSeatWind = handTiles.get(0).getOrignalOwner();
		
		//set the new tile as the tile that completed the meld
		mCompletedTile = newTile;
		//check who is responsible for discarding the new tile
		mPlayerResponsible = newTile.getOrignalOwner();
		
		//check if the new tile came from someone other than the owner
		//closed = false if the tile came from someone else
		mClosed = (mPlayerResponsible == mOwnerSeatWind);
		
		
		//set meld type
		mMeldType = meldType;
		
		
		//add the hand tiles to the meld
		mTiles = handTiles;
		//add the new tile to the meld
		mTiles.add(newTile);
		
		//sort the meld if it is a chi
		if (mMeldType.isChi()) Collections.sort(mTiles);
	}
	
	
	
	
	//uses the given tile to upgrade a minkou to a minkan
	public void upgradeToMinkan(GameTile handTile){
		
		if (!isPon()) return;
		
		//add the tile to the meld
		__addTile(handTile);
		
		//change meld type to kan
		mMeldType = MeldType.KAN; 
	}
	
	//adds a tile to a meld (needed for upgrading minkou to minkan)
	private void __addTile(GameTile t){mTiles.add(t);}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//returns the amount of fu the meld is worth
	public int calculateFu(){
		int fu = 0;
		
		mFu = fu;
		return mFu;
	}
	
	
	
	
	//accessors
	public boolean isClosed(){return mClosed;}
	public Wind getOwnerSeatWind(){return mOwnerSeatWind;}
	public Wind getResponsible(){return mPlayerResponsible;}
	

	//returns the tile at the given index in the meld, returns null if index is outside of the meld's range
	public GameTile getTile(int index){
		if (index >= 0 && index < mTiles.size()) return (GameTile)mTiles.get(index);
		return null;
	}
	//returns the first tile in the meld
	public GameTile getFirstTile(){return (GameTile)mTiles.get(0);}
	
	
	//returns a copy of the entire list of tiles
//	public List<GameTile> getAllTiles(){return new ConviniList<GameTile>(mTiles);}
	public GameTileList getAllTiles(){return mTiles.clone();}
	
	
	//returns how many tiles are in the meld
	public int size(){return mTiles.size();}
	
	
	
	
	
	
	
	public boolean isChi(){return mMeldType.isChi();}
	public boolean isPon(){return mMeldType.isPon();}
	public boolean isKan(){return mMeldType.isKan();}
//	public MeldType getMeldType(){return mMeldType;}
	
	
	
	
	
	
	
	
	
	
	//toString
	@Override
	public String toString(){
		String meldString = "";
		
		for (TileInterface t: mTiles) meldString += t.toString() + " ";
		
		//show closed or open
		if (mClosed == true) meldString += "  [Closed]";
		else meldString += "  [Open, called from: " + mPlayerResponsible + "'s " + mCompletedTile.toString() + "]";
		
		return meldString;
	}
	public String toStringCompact(){
		String meldString = "";
		for (TileInterface t: mTiles) meldString += t + " ";
		
		if (meldString != "") meldString = meldString.substring(0, meldString.length() - 1);
		return meldString;
	}
	

	//iterator, returns mTile's iterator
	@Override
	public Iterator<GameTile> iterator() {return mTiles.iterator();}
	
	
	
	@Override
	public int compareTo(Meld other) {
		
		//if the first tiles are different, return the comparison of the first tiles
		int tileCompare = getFirstTile().compareTo(other.getFirstTile());
		if (tileCompare != 0) return tileCompare;
		
		//if the first tiles are the same, compare by meld type
		return (mMeldType.compareTo(other.mMeldType));
	}
}
