package majava.enums;


public enum GameEventType {
	START_OF_ROUND,
	DREW_TILE, PLAYER_TURN_START,
	NEW_DORA_INDICATOR,
	DISCARDED_TILE,
	MADE_OPEN_MELD,
	MADE_ANKAN, MADE_MINKAN, MADE_OWN_KAN,
	END_OF_ROUND,
	
	CALLED_TILE,
	DECLARED_RIICHI, DECLARED_OWN_KAN, DECLARED_TSUMO,
//	CALLED_TILE(Exclamation.UNKNOWN),
//	DECLARED_RIICHI(Exclamation.RIICHI), DECLARED_OWN_KAN(Exclamation.OWN_KAN), DECLARED_TSUMO(Exclamation.TSUMO),
	
	HUMAN_PLAYER_TURN_START, HUMAN_PLAYER_REACTION_START,
	END, START,
	UNKNOWN,
	NONE;
	
	
	public boolean isExclamation(){return (this == CALLED_TILE || this == DECLARED_RIICHI || this == DECLARED_OWN_KAN || this == DECLARED_TSUMO);}
	public boolean isForHuman(){return this == HUMAN_PLAYER_REACTION_START || this == HUMAN_PLAYER_TURN_START;}
	public boolean isStartEnd(){return this == START || this == END;}
}
