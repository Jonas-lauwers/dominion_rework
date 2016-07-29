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
		this.PlayingField.put("trash", new Deck());
		this.PlayingField.put("discard", new Deck());
		this.PlayingField.put("table", new Deck());
		this.PlayingField.put("deck", new Deck());
		this.PlayingField.put("hand", new Deck());
	}
	
	
	public Deck getDeck(String deckName){
		return this.PlayingField.get(deckName);
	}
	
        //TODO do we need to have this?
	public Map<String , Deck> getAllDecks(){
		return this.PlayingField;
	}
	
	public int getCoins(){
		return this.coins;
	}
	
	public void addCoins(int coins){
		this.coins += coins;
	}
	
	public int getActions(){
		return this.actions;
	}
	
	public void addActions(int actions) {
		this.actions += actions;
	}
	
	public int getBuys() {
		return this.buys;
	}
	
	public void addBuys(int buys) {
		this.buys += buys;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void endPlayerTurn() {
		this.PlayingField.get("hand").moveDeckTo(this.PlayingField.get("discard"));
		this.PlayingField.get("table").moveDeckTo(this.PlayingField.get("discard"));
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
