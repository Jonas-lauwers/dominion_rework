package Card;

import java.io.Serializable;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import Database_connection.cardConnection;

/**
 * A card object, on creation gives the card a specific state depending on it's
 * type. Is just a container to hold all information about the card.
 *
 * @author Jonas Lauwers <jonas.lauwers AT gmail.org>
 */
public class Card implements Serializable, Comparable<Object> {

    static final long serialVersionUID = 1337;
    private final String name;
    private final String description;
    private final int cost;
    private final boolean isKingdom;
    private final String type;
    private final List<String> extendendTypes;
    private final int coins;
    private final int draws;
    private final int actions;
    private final int buys;
    private final int victoryPoints;

    /**
     * Creates a new card based on the name. Gets all the card information from
     * the database.
     *
     * @param name The name of the card
     */
    //recheck all db values and rework.
    public Card(String name) {
        String[] dbOutput = new cardConnection().getCard(name);
        this.name = name.toLowerCase();
        this.description = (dbOutput[7] == null ? "" : dbOutput[7]);
        this.cost = Integer.parseInt(dbOutput[1]);
        this.isKingdom = (Integer.parseInt(dbOutput[8]) == 0 ? false : true);
        this.type = dbOutput[0];
        this.extendendTypes = new ArrayList<>();
        for(int i = 9; i <= 10; i++) {
            if(dbOutput[i] != null) {
                extendendTypes.add(dbOutput[i]);
            }
        }
        this.actions = Integer.parseInt(dbOutput[2]);
        this.buys = Integer.parseInt(dbOutput[3]);
        this.draws = Integer.parseInt(dbOutput[4]);
        this.coins = Integer.parseInt(dbOutput[5]);
        this.victoryPoints=Integer.parseInt(dbOutput[6]);
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
     * Return the number of coins to be added by the card.
     *
     * @return The coins value of the card.
     */
    public int getAddCoins() {
        return coins;
    }

    /**
     * Return the number of buys to be added by the card.
     *
     * @return The buys value of the card.
     */
    public int getAddBuys() {
        return buys;
    }

    /**
     * Return the number of actions to be added the card.
     *
     * @return The actions value of the card.
     */
    public int getAddActions() {
        return actions;
    }

    /**
     * Return the draws value of the card.
     *
     * @return The draws value of the card.
     */
    public int getDraws() {
        return draws;
    }

    /**
     * Return the victory value of the card.
     *
     * @return The victory value of the card.
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Returns whether the card is playable or not.
     *
     * @return True if playable.
     */
    public boolean isPlayable() {
        if (this.isAction() || this.isTreasure()) {
            return true;
        }
        return false;
    }

    /**
     * Returns whether the card is kingdom card or not.
     *
     * @return True if kingdom card.
     */
    public boolean isKingdom() {
        return isKingdom;
    }

    /**
     * Returns whether the card is treasure card or not.
     *
     * @return True if treasure card.
     */
    public boolean isTreasure() {
        return this.type.equals("Treasure") || extendendTypes.contains("Treasure");
    }

    /**
     * Returns whether the card is victory card or not.
     *
     * @return True if victory card.
     */
    public boolean isVictory() {
        return this.type.equals("Victory") || extendendTypes.contains("Victory");
    }

    /**
     * Returns whether the card is victory card or not.
     *
     * @return True if victory card.
     */
    public boolean isAction() {
        return this.type.equals("Action") || extendendTypes.contains("Action");
    }

    /**
     * Returns whether the card is curse card or not.
     *
     * @return True if reaction card.
     */
    public boolean isCurse() {
        return this.type.equals("Curse");
    }

    /**
     * Returns whether the card is reaction card or not.
     *
     * @return True if reaction card.
     */
    public boolean isReaction() {
        return extendendTypes.contains("Reaction");
    }

    /**
     * Returns whether the card is attack card or not.
     *
     * @return True is attack card.
     */
    public boolean isAttack() {
        return extendendTypes.contains("Attack");
    }
    
    /**
     * Returns whether the card is attack card or not.
     *
     * @return True is attack card.
     */
    public boolean isduration() {
        return extendendTypes.contains("Duration");
    }

    @Override
    public String toString() {
        if (this.isKingdom()) {
            return String.format("%-15s Cost: %1d - Actions: %1d - Buys: %1d - Coins: %1d - Cards: %1d - Description: %s", this.getName(), this.getCost(), this.getAddActions(), this.getAddBuys(), this.getAddCoins(), this.getDraws(), this.getDescription());
        }
        return String.format("%-15s Cost: %1d", this.getName(), this.getCost());
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Card) {
            Card c = ((Card) o);
            if (this.getCost() > c.getCost()) {
                return 1;
            } else if (this.getCost() < c.getCost()) {
                return -1;
            } else {
                return 0;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Card) {
            Card c = ((Card) o);
            if (c.getName().equals(this.getName())) {
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