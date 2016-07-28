package Card;

import java.io.Serializable;

public class CurseState implements CardState, Serializable{

	static final long serialVersionUID = 1337;
	private int victoryPoints;
	private String playableTurn;
	
	Card card;
	
	public CurseState(Card card){
		this.card = card;
	}
	
	public void completeCard(){
		this.victoryPoints=Integer.parseInt(card.dbOutput[6]);
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
	

