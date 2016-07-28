package Card;

import java.io.Serializable;

public class VictoryState implements CardState, Serializable {
	
	static final long serialVersionUID = 1337;
	private int victoryPoints;
	private String playableTurn;

	Card card;
	
	public VictoryState(Card card){
		this.card = card;
	}
	
	public void completeCard(){
		this.victoryPoints = (card.dbOutput[6] == null ? 0 : Integer.parseInt(card.dbOutput[6]));
		this.playableTurn = "never";
	}
	
	public int getCoins(){
		return 0;
	}
	
	public int getDraws(){
		return 0;
	}
	
	public int getActions(){
		return 0;
	}
	
	public int getBuys(){
		return 0;
	}
	
	public String getPlayableTurn(){
		return playableTurn;
	}
	
	public int getVictoryPoints(){
		return victoryPoints;
	}
}

