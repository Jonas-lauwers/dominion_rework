package Card;

import Engine.GameEngine;
import Database_connection.cardConnection;

import java.io.Serializable;

public class Card implements Serializable, Comparable<Object> {

    static final long serialVersionUID = 1337;
    private String name;
    String[] dbOutput;
    private String description;
    private int cost;
    private String type;
    protected GameEngine ge;

    CardState cardState;

    public Card(String name) {
        dbOutput = new cardConnection().getCard(name);
        this.name = name.toLowerCase();
        this.cost = Integer.parseInt(dbOutput[1]);
        this.description = dbOutput[7];
        this.type = dbOutput[0];
        giveState();
        cardState.completeCard();

    }

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

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getCost() {
        return this.cost;
    }

    public String getType() {
        return this.type;
    }

    public int getCoins() {
        return cardState.getCoins();
    }

    public int getBuys() {
        return cardState.getBuys();
    }

    public int getActions() {
        return cardState.getActions();
    }

    //TODO change the cardstate function to a more logic result
    public boolean isPlayable() {
        return !(cardState.getPlayableTurn().equals("never"));
    }

    public int getDraws() {
        return cardState.getDraws();
    }

    public int getVictoryPoints() {
        return cardState.getVictoryPoints();
    }

    public boolean isAction() {
        if (this.type.equals("Action") || this.type.equals("Attack") || this.type.equals("Reaction")) {
            return true;
        }
        return false;
    }

    public boolean isTreasure() {
        if (this.type.equals("Treasure")) {
            return true;
        }
        return false;
    }

    public String toString() {
        if (this.type.equals("Treasure") || this.type.equals("Victory") || this.name.equals("Curse")) {
            return String.format("%-10s Cost: %1d\n", this.getName(), this.getCost());
        }
        if (this.description != null) {
            return String.format("%-20s Cost: %1d - Actions: %1d - Buys: %1d - Coins: %1d - Cards: %1d - Description: %s\n", this.getName(), this.getCost(), this.getActions(), this.getBuys(), this.getCoins(), this.getDraws(), this.getDescription());
        }
        return String.format("%-20s Cost: %1d - Actions: %1d - Buys: %1d - Coins: %1d - Cards: %1d\n", this.getName(), this.getCost(), this.getActions(), this.getBuys(), this.getCoins(), this.getDraws());
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
}
