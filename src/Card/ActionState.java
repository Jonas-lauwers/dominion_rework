package Card;

import java.io.Serializable;

public class ActionState implements CardState, Serializable{
	
	
	static final long serialVersionUID = 1337;
	private int coins;
	private int draws;
	private int actions;
	private int buys;
	private String playableTurn;
	
	Card card;
	
	public ActionState(Card card){
		this.card = card;
	}
	
	public void completeCard(){
		this.playableTurn = "action";
		this.coins = (card.dbOutput[5] == null ? 0 :  Integer.parseInt(card.dbOutput[5])); //meegeven
		this.draws = (card.dbOutput[4] == null ? 0 :  Integer.parseInt(card.dbOutput[4])); //meegeven
		this.actions = (card.dbOutput[2] == null ? 0 :  Integer.parseInt(card.dbOutput[2])); //meegeven
		this.buys = (card.dbOutput[3] == null ? 0 :  Integer.parseInt(card.dbOutput[3])); //meegeven
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
