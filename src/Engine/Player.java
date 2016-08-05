package Engine;

import java.util.*;

import java.io.Serializable;

/**
 * A Container class for players in dominion. Holds all the necessary information
 * for a player.
 * @author Jonas Lauwers <jonas.lauwers AT gmail.org>
 */
public class Player implements Serializable {

    static final long serialVersionUID = 1337;
    private final String name;
    private int actions;
    private int buys;
    private int coins;
    private int score;
    private final Map<String, Deck> playingField;

    /**
     * Initialise a player object.
     * @param name The name of the player.
     */
    public Player(String name) {
        this.name = name;
        this.actions = 1;
        this.buys = 1;
        this.coins = 0;
        this.score = 0;
        this.playingField = new HashMap<>();
        this.playingField.put("trash", new Deck());
        this.playingField.put("discard", new Deck());
        this.playingField.put("table", new Deck());
        this.playingField.put("deck", new Deck());
        this.playingField.put("hand", new Deck());
    }

    /**
     * Get the deck of the specified deckName.
     * @param deckName The name of the deck.
     * @return The deck object or null if the deckName does not exist.
     */
    public Deck getDeck(String deckName) {
        return this.playingField.get(deckName);
    }
    
    /**
     * Get the value of the players coins.
     * @return Value of coins.
     */
    public int getCoins() {
        return this.coins;
    }

    /**
     * Add the value coins to the coins of the player.
     * @param coins The value of coins to add.
     */
    public void addCoins(int coins) {
        this.coins += coins;
    }

    /**
     * Get the value of the players actions.
     * @return Value of actions.
     */
    public int getActions() {
        return this.actions;
    }

    /**
     * Add the value actions to the actions of the player.
     * @param actions The value of actions to add.
     */
    public void addActions(int actions) {
        this.actions += actions;
    }
    
    /**
     * Get the value of the players buys.
     * @return Value of buys.
     */
    public int getBuys() {
        return this.buys;
    }

    /**
     * Add the value buys to the buys of the player.
     * @param buys The value of buys to add.
     */
    public void addBuys(int buys) {
        this.buys += buys;
    }

    /**
     * Get the value of the players name.
     * @return Value of the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the value of the players score.
     * @return Value of score.
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Set the value score of the score of the player.
     * @param score The value of score.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Reset player to it's turn start state.
     */
    //TODO move to engine probably .... player doesn't know what it's initial state is, engine does.
    public void endPlayerTurn() {
        this.playingField.get("hand").moveDeckTo(this.playingField.get("discard"));
        this.playingField.get("table").moveDeckTo(this.playingField.get("discard"));
        this.buys = 1;
        this.actions = 1;
        this.coins = 0;
    }

    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Player) {
            Player p = (Player) o;
            if (this.name.equals(p.name)) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }
}
