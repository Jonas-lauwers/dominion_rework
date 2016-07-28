package Card;

import java.io.Serializable;

public class ReactionState implements CardState, Serializable {
	
	static final long serialVersionUID = 1337;
	private int coins;
	private int draws;
	private int actions;
	private int buys;
	private String playableTurn;
	
	Card card;
	
	public ReactionState(Card card){
		this.card = card;
	}
	
	public void completeCard(){
		this.playableTurn = "any";
		this.coins = (card.dbOutput[5] == null ? 0 :  Integer.parseInt(card.dbOutput[5])); 
		this.draws = (card.dbOutput[4] == null ? 0 :  Integer.parseInt(card.dbOutput[4])); 
		this.actions = (card.dbOutput[2] == null ? 0 :  Integer.parseInt(card.dbOutput[2])); 
		this.buys = (card.dbOutput[3] == null ? 0 :  Integer.parseInt(card.dbOutput[3])); 
	}
	
	public int getCoins(){
		return coins;
	}
	
	public int getDraws(){
		return draws;
	}
	
	public int getActions(){
		return actions;
	}
	
	public int getBuys(){
		return buys;
	}
	
	public String getPlayableTurn(){
		return playableTurn;
	}
	
	public int getVictoryPoints(){
		return 0;
	}
}
