package Card;

import java.io.Serializable;

public class TreasureState implements CardState, Serializable {
	
	static final long serialVersionUID = 1337;
	private int coins;
	private String playableTurn;

	Card card;
	
	public TreasureState(Card card){
		this.card = card;
	}
	
	public void completeCard(){
		this.coins= Integer.parseInt(card.dbOutput[5]);
		this.playableTurn = "buy";
	}
	
	public int getCoins(){
		return coins;
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
		return 0;
	}
}


