package Engine;

import java.util.*;

import java.io.Serializable;

public class Player implements Serializable{
	
	static final long serialVersionUID = 1337;
	private String name;
	private int actions;
	private int buys;
	private int coins;
	private int score;
	private Map<String , Deck> PlayingField;
	
	public Player(String name) {
		this.name = name;
		this.actions = 1;
		this.buys = 1;
		this.coins = 0;
		this.score = 0;
		this.PlayingField = new HashMap<>();
		this.PlayingField.put("Trash", new Deck());
		this.PlayingField.put("Discard", new Deck());
		this.PlayingField.put("Table", new Deck());
		this.PlayingField.put("Deck", new Deck());
		this.PlayingField.put("Hand", new Deck());
	}
	
	
	public Deck getDeck(String deckName){
		return this.PlayingField.get(deckName);
	}
	
	public Map<String , Deck> getAllDecks(){
			return this.PlayingField;
	}
	
	public int getCoins(){
		return this.coins;
	}
	
	public void setCoins(int coins){
		this.coins += coins;
	}
	
	public int getActions(){
		return this.actions;
	}
	
	public void setActions(int actions) {
		this.actions += actions;
	}
	
	public int getBuys() {
		return this.buys;
	}
	
	public void setBuys(int buys) {
		this.buys += buys;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void endPlayerTurn() {
		this.PlayingField.get("Hand").moveDeckTo(this.PlayingField.get("Discard"));
		this.PlayingField.get("Table").moveDeckTo(this.PlayingField.get("Discard"));
		this.buys = 1;
		this.actions = 1;
		this.coins = 0;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getScore() {
		return this.score;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Player) {
			Player p = (Player) o;
			if (this.name.equals(p.name)) {
				return true;
			}
			else return false;
		}
		else throw new IllegalArgumentException();
	}
}
