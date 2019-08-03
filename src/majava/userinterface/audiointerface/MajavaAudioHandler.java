package majava.userinterface.audiointerface;


import utility.SoundClipPlayer;
import majava.enums.Exclamation;
import majava.events.GameplayEvent;
import majava.userinterface.GameUI;

public class MajavaAudioHandler extends GameUI{
	
	private SoundClipPlayer soundDiscard,
							soundExclamationChi, soundExclamationPon, soundExclamationKan, soundExclamationGeneric,
							soundHumanReactionChance,
							soundRoundStart,
							soundRoundEndRyuukyoku, soundRoundEndVictory
							;
	
	private SoundClipPlayer soundMostRecentMeld;
	
	
	
	public MajavaAudioHandler(){
		setSleepTimes(0, 0, 0);
		setupSoundPlayers();
	}
	
	
	private void setupSoundPlayers(){
		soundDiscard = new SoundClipPlayer("/res/audio/thunk.wav");
		soundExclamationChi = new SoundClipPlayer("/res/audio/meldChi.wav");
		soundExclamationPon = new SoundClipPlayer("/res/audio/meldPon.wav");
		soundExclamationKan = new SoundClipPlayer("/res/audio/meldKan.wav");
		soundExclamationGeneric = new SoundClipPlayer("/res/audio/exclamation.wav");
		soundHumanReactionChance = new SoundClipPlayer("/res/audio/popup.wav");
		soundRoundStart = new SoundClipPlayer("/res/audio/roundStart.wav");
		soundRoundEndRyuukyoku = new SoundClipPlayer("/res/audio/roundEndRyuukyoku.wav");
		soundRoundEndVictory = new SoundClipPlayer("/res/audio/roundEndVictory.wav");
		
		soundMostRecentMeld = null;
	}
	
	
	private void playSound(SoundClipPlayer sound){
		sound.playSound();
	}
	
	

	
	@Override
	protected void displayEventDiscardedTile(GameplayEvent event){
		playSound(soundDiscard);
	}
	
	@Override
	protected void displayEventMadeOpenMeld(GameplayEvent event){
		playSound(soundForMostRecentMeld());
	}
	@Override
	protected void displayEventMadeOwnKan(GameplayEvent event){
		playSound(soundExclamationKan);
	}
	
	@Override
	protected void displayEventHumanReactionStart(GameplayEvent event){
		playSound(soundHumanReactionChance);
	}
	
	@Override
	protected void displayEventStartOfRound(GameplayEvent event){
		playSound(soundRoundStart);
	}
	
	@Override
	protected void displayEventEndOfRound(GameplayEvent event){
		SoundClipPlayer soundRoundEnd = null;
		
		if (gameState.getResultSummary().isVictory()) soundRoundEnd = soundRoundEndVictory;
		else soundRoundEnd = soundRoundEndRyuukyoku;
		
		playSound(soundRoundEnd);
	}
	
	@Override
	protected void showExclamation(Exclamation exclamation, int seat){
		playSound(soundExclamationGeneric);
		
		//set this variable, to be used later in displayEventMadeOpenMeld
		switch(exclamation){
		case CHI: soundMostRecentMeld = soundExclamationChi; break;
		case PON: soundMostRecentMeld = soundExclamationPon; break;
		case KAN: soundMostRecentMeld = soundExclamationKan; break;
		default: soundMostRecentMeld = SoundClipPlayer.NULL_SOUND_PLAYER; break;
		}
	}
	
	private SoundClipPlayer soundForMostRecentMeld(){
		return soundMostRecentMeld;
	}
	
	
	
	
	
	@Override
	public void printErrorRoundAlreadyOver(){}
	
	@Override
	public void startUI(){}
	
	@Override
	public void endUI(){}
	
	@Override
	protected void displayEventPlayerTurnStart(GameplayEvent event){}
	
	@Override
	protected void displayEventDrewTile(GameplayEvent event){}
	
	@Override
	protected void displayEventNewDoraIndicator(GameplayEvent event){}
	
	@Override
	protected void displayEventHumanTurnStart(GameplayEvent event){}
	
}