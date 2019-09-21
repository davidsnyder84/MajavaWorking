package majava;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import majava.control.testcode.WallDemoer;
import majava.tiles.GameTile;
import majava.util.GTL;



//represents the wall (�R/��) of tiles used in the game
public class Wall {
	private static final int NUMBER_OF_DIFFERENT_TILES = 34;
	private static final int MAX_SIZE_WALL = NUMBER_OF_DIFFERENT_TILES * 4;	//136
	private static final int POS_LAST_NORMAL_WALL_TILE = 121;
//	private static final int POS_LAST_DEAD_WALL_TILE = 135;
//	private static final int FIRST_TILE_IN_WALL = 0;
	
	//dead wall counstants
	private static final int OFFSET_DEAD_WALL = POS_LAST_NORMAL_WALL_TILE + 1;
	private static final int MAX_SIZE_DEAD_WALL = 14;
	
	
	
	private final GameTile[] wallTiles;
	private int currentWallPosition;
	
	
	public Wall(){
		currentWallPosition = 0;
		
		wallTiles = generateStandardSetOf134Tiles();
		shuffle();
	}
	private void shuffle(){Collections.shuffle(Arrays.asList(wallTiles));}
	
	
	
	//fills the received TileLists with each player's starting hands
	public void takeStartingHands(List<GameTile> tilesE, List<GameTile> tilesS, List<GameTile> tilesW, List<GameTile> tilesN){
		
		//[East takes 4, South takes 4, West takes 4, North takes 4] x3
		for (int i = 0; i < 3; i++){
			tilesE.addAll(takeTiles(4));
			tilesS.addAll(takeTiles(4));
			tilesW.addAll(takeTiles(4));
			tilesN.addAll(takeTiles(4));
		}
		//East takes 2, South takes 1, West takes 1, North takes 1 x1
		tilesE.add(takeTile()); tilesE.add(takeTile());
		tilesS.add(takeTile());
		tilesW.add(takeTile());
		tilesN.add(takeTile());
	}
	
	
	//returns a list of the dora indicators tiles. if wantUraDora is true, the list will also contain the ura dora indicators
	private GTL getDoraIndicators(boolean wantUraDora){
		int indicatorsNeeded = getNumKansMade() + 1;
		GTL indicators = new GTL(); 
		
		for (int currentIndicator = 1; currentIndicator <= indicatorsNeeded; currentIndicator++)
			indicators = indicators.add(getDoraIndicator(currentIndicator));
		
		if (wantUraDora)
			for (int currentIndicator = 1; currentIndicator <= indicatorsNeeded; currentIndicator++)
				indicators = indicators.add(getUraDoraIndicator(currentIndicator));
		
		return indicators;
	}
	public GTL getDoraIndicators(){return getDoraIndicators(false);}
	public GTL getDoraIndicatorsWithUra(){return getDoraIndicators(true);}
	
	//returns the specified dora indicator tile
	private GameTile getDoraIndicator(int doraNumber){return getTile(indexOfDora(doraNumber));}
	private GameTile getUraDoraIndicator(int uraDoraNumber){return getTile(indexOfUraDora(uraDoraNumber));}
	private int indexOfDora(int doraNumber){return OFFSET_DEAD_WALL + 10 - 2*doraNumber;}
	private int indexOfUraDora(int uraDoraNumber){return indexOfDora(uraDoraNumber) + 1;}
	
	
	
	
	
	//removes a tile from the current wall position and returns it. returns the tile, or returns null if the wall was empty
	public GameTile takeTile(){
		GameTile takenTile = null;
		if (currentWallPosition <= POS_LAST_NORMAL_WALL_TILE){
			takenTile = removeTile(currentWallPosition);
			currentWallPosition++;
		}
		return takenTile;
	}
	//take multiple tiles at once
	public List<GameTile> takeTiles(int numberOfTilesToTake){
		ArrayList<GameTile> takenTiles = new ArrayList<GameTile>(numberOfTilesToTake);
		for (int i = 0; i < numberOfTilesToTake; i++)
			takenTiles.add(takeTile());
		return takenTiles;
	}
	
	//removes a tile from the end of the dead wall and returns it (for a rinshan draw)
	public GameTile takeTileFromDeadWall(){
		//even numbers are top row, odd are bottom row
		final int POS_KANDRAW_1 = 12, POS_KANDRAW_2 = 13, POS_KANDRAW_3 = 10, POS_KANDRAW_4 = 11;
		final Integer[] POS_KANDRAWS = {POS_KANDRAW_1, POS_KANDRAW_2, POS_KANDRAW_3, POS_KANDRAW_4};
		
		GameTile takenTile = removeTile(OFFSET_DEAD_WALL + POS_KANDRAWS[getNumKansMade()]);
		return takenTile;
	}
	
	
	private GameTile getTile(int index){return wallTiles[index];}
	public GameTile[] getTilesAsList(){return wallTiles.clone();}
	private GameTile getDeadWallTile(int index){return getTile(OFFSET_DEAD_WALL + index);}
	private void setTile(int index, GameTile tile){wallTiles[index] = tile;}
	private GameTile removeTile(int index){
		GameTile tile = getTile(index);
		setTile(index, null);
		return tile;
	}
	
	
	
	
	public boolean isEmpty(){return (numTilesLeftInWall() == 0);}
	//returns the number of tiles left in the wall (not including dead wall)
	public int numTilesLeftInWall(){return MAX_SIZE_WALL - currentWallPosition - MAX_SIZE_DEAD_WALL;}
	public int numTilesLeftInDeadWall(){return MAX_SIZE_DEAD_WALL - getNumKansMade();}
	
	private int getNumKansMade(){
		int numberOfTilesMissingFromDeadWall = 0;
		for (int index = 0; index < MAX_SIZE_DEAD_WALL; index++)
			if (getDeadWallTile(index) == null) numberOfTilesMissingFromDeadWall++;
//		for (GameTile t: deadWallTiles()) if (t == null) numberOfTilesMissingFromDeadWall++;
		return numberOfTilesMissingFromDeadWall;
	}
	private GameTile[] deadWallTiles(){
		GameTile[] deadWallSection = new GameTile[MAX_SIZE_DEAD_WALL];
		for (int index = 0; index < MAX_SIZE_DEAD_WALL; index++)
			deadWallSection[index] = getDeadWallTile(index);
		return deadWallSection;
	}
	
	
	
	//tostring
	@Override
	public String toString(){
		String wallString = "";
		
		final int TILES_PER_LINE = 17;
		for (int i = 0; i < numTilesLeftInWall() / TILES_PER_LINE + 1; i++){
			for (int j = 0; j < TILES_PER_LINE && (j + TILES_PER_LINE*i < numTilesLeftInWall()); j++)
				wallString += getTile(currentWallPosition + TILES_PER_LINE*i + j) + " ";
			if (TILES_PER_LINE*i < numTilesLeftInWall())
				wallString += "\n";
		}
		
		String deadWallString = toStringDeadWall();
		return ("Wall: " + numTilesLeftInWall() + "\n" + wallString + "\n\n" + deadWallString);
	}
	
	//string representation of deadwall
	public String toStringDeadWall(){
		String dwString = "";
		String topRow = "", bottomRow = "";
		
		for (int tile = 0; tile < MAX_SIZE_DEAD_WALL / 2; tile++){
			topRow += deadWallTileToString(2*tile) + " ";
			bottomRow += deadWallTileToString(2*tile + 1) + " ";
		}
		
		dwString = "DeadWall: " + numTilesLeftInDeadWall() + "\n" + topRow + "\n" + bottomRow;
		return dwString;
	}
	
	
	private String singleTileToString(int index){
		if (getTile(index) == null) return "  ";
		else return getTile(index).toString();
	}
	private String deadWallTileToString(int index){return singleTileToString(OFFSET_DEAD_WALL + index);}
	
	
	
	
	
	
	//DEMO METHODS
	public void DEMOloadDebugWall(){WallDemoer.loadDebugWall(wallTiles, currentWallPosition);}
	public void DEMOexhaustWall(){currentWallPosition = 68;}
//	public void printWall(){System.out.println(toString());}
//	public void printDeadWall(){System.out.println(toStringDeadWall());}
	
	
	public static GameTile[] generateStandardSetOf134Tiles(){
		final GameTile[] tiles = new GameTile[MAX_SIZE_WALL];
		final int IDM5 = 5, IDP5 = 14, IDS5 = 23;
		int index = 0;
		//fill the set with 4 of each tile, in sequential order
		//make red doras accordingly for fives (1 in man, 2 in pin, 1 in sou)
		for (int id = 1; id <= NUMBER_OF_DIFFERENT_TILES; id++){
			tiles[index++] = new GameTile(id);
			tiles[index++] = new GameTile(id);
			
			if (id == IDP5) tiles[index++] = new GameTile(id, true);
			else            tiles[index++] = new GameTile(id);
			
			if (id == IDM5 || id == IDP5 || id == IDS5) tiles[index++] = new GameTile(id, true);
			else                                        tiles[index++] = new GameTile(id);
		}
		return tiles;
	}
}
