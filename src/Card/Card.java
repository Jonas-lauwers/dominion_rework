package Card;

import java.io.Serializable;
import java.util.Objects;
import Database_connection.cardConnection;

/**
 * A card object, on creation gives the card a specific state depending on 
 * it's type. Is just a container to hold all information about the card.
 * 
 * @author Jonas Lauwers <jonas.lauwers AT gmail.org>
 */
public class Card implements Serializable, Comparable<Object> {

    static final long serialVersionUID = 1337;
    private final String name;
    private final String description;
    private final String type;
    private final int cost;
    final String[] dbOutput;
    
    CardState cardState;

    /**
     * Creates a new card based on the name. 
     * Gets all the card information from the database.
     * 
     * @param name The name of the card
     */
    public Card(String name) {
        dbOutput = new cardConnection().getCard(name);
        this.name = name.toLowerCase();
        this.cost = Integer.parseInt(dbOutput[1]);
        this.description = ( dbOutput[7] == null ? "" : dbOutput[7]);
        this.type = dbOutput[0];
        giveState();
        cardState.completeCard();

    }

    /**
     * Gives the card a state depending on it's type.
     */
    private void giveState() {
        switch (type) {
            case "Attack":
                cardState = new AttackState(this);
                break;
            case "Reaction":
                cardState = new ReactionState(this);
                break;
            case "Action":
                cardState = new ActionState(this);
                break;
            case "Treasure":
                cardState = new TreasureState(this);
                break;
            case "Victory":
                cardState = new VictoryState(this);
                break;
            case "Curse":
                cardState = new CurseState(this);
                break;
        }

    }

    /**
     * Return the name of the card.
     * 
     * @return The name of the card.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the description of the card.
     * 
     * @return The description of the card.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Return the cost value of the card.
     * 
     * @return The cost value of the card.
     */
    public int getCost() {
        return this.cost;
    }

    /**
     * Return the type of the card.
     * 
     * @return The type of the card.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Return the coins value of the card.
     *
     * @return The coins value of the card.
     */
    public int getCoins() {
        return cardState.getCoins();
    }

    /**
     * Return the buys value of the card.
     * 
     * @return The buys value of the card.
     */
    public int getBuys() {
        return cardState.getBuys();
    }

    /**
     * Return the actions value of the card.
     * 
     * @return The actions value of the card.
     */
    public int getActions() {
        return cardState.getActions();
    }

    /**
     * Return the draws value of the card.
     * 
     * @return The draws value of the card.
     */
    public int getDraws() {
        return cardState.getDraws();
    }

    /**
     * Return the victory value of the card.
     * 
     * @return The victory value of the card.
     */
    public int getVictoryPoints() {
        return cardState.getVictoryPoints();
    }

    /**
     * Returns whether the card is playable or not.
     * 
     * @return True if playable.
     */
    //TODO change the cardstate function to a more logic result
    public boolean isPlayable() {
        return !(cardState.getPlayableTurn().equals("never"));
    }

    /**
     * Returns whether the card is kingdom card or not.
     * 
     * @return True if kingdom card.
     */
    public boolean isKingdom() {
        if (this.type.equals("Action") || this.type.equals("Attack") || this.type.equals("Reaction")) {
            return true;
        }
        return false;
    }

    /**
     * Returns whether the card is treasure card or not.
     * 
     * @return True if treasure card.
     */
    public boolean isTreasure() {
        if (this.type.equals("Treasure")) {
            return true;
        }
        return false;
    }
    
    /**
     * Returns whether the card is victory card or not.
     * 
     * @return True if victory card.
     */
    public boolean isVictory() {
        if (this.type.equals("Victory")) {
            return true;
        }
        return false;
    }
    
    /**
     * Returns whether the card is reaction card or not.
     * 
     * @return True if reaction card.
     */
    public boolean isReaction() {
        if (this.type.equals("Reaction")) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        if (this.isKingdom() ) {
            return String.format("%-20s Cost: %1d - Actions: %1d - Buys: %1d - Coins: %1d - Cards: %1d - Description: %s", this.getName(), this.getCost(), this.getActions(), this.getBuys(), this.getCoins(), this.getDraws(), this.getDescription());
        }
        return String.format("%-10s Cost: %1d", this.getName(), this.getCost());
    }
    
    @Override
    public int compareTo(Object o) {
        if (o instanceof Card) {
            Card c = ((Card) o);
            if (this.getCost() > c.getCost()) {
                return 1;
            }
            else if (this.getCost() < c.getCost()) {
                return -1;
            }
            else {
                return 0;
            }
        }
        else {
            throw new IllegalArgumentException();
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Card) {
            Card c = ((Card) o);
            if(c.getName().equals(this.getName())) {
                return true;
            }
         }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.name);
        return hash;
    }
}
