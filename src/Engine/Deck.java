package Engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.io.Serializable;
import Card.Card;


/**
 * An object representing deck to hold cards. It can hold equal Card objects.
 * Has functions to check if it contains cards of a certain type.
 *
 * @author Jonas Lauwers <jonas.lauwers AT gmail.org>
 */
public class Deck implements Serializable, Iterable<Card> {

    static final long serialVersionUID = 1337;
    private final ArrayList<Card> deck;

    /**
     * Creates an empty deck.
     */
    public Deck() {
        deck = new ArrayList<>();
    }

    /**
     * Add a card to the end of the deck.
     *
     * @param card The card to add.
     */
    public void add(Card card) {
        this.add(deck.size(), card);
    }

    /**
     * Add card to the deck at a certain index.
     * If index is smaller than 0 then it adds the card at the beginning of the deck.
     * If index is greater then it's size it adds the card at the end of the deck.
     *
     * @param index The index to place the card.
     * @param card The card to add.
     */
    public void add(int index, Card card) {
        if(index < 0) {
            deck.add(0, card);
        }
        else if(index >= deck.size()) {
            deck.add(card);
        }
        else {
            deck.add(index, card);
        }
    }

    /**
     * Returns the Card thats found at the index cardNumber
     *
     * @param cardNumber The index of the card.
     * @return The card at the index or null if index doesn't exist.
     */
    public Card getCard(int cardNumber) {
        if (0 <= cardNumber && cardNumber < deck.size()) {
            return deck.get(cardNumber);
        }
        return null;
    }

    /**
     * Remove the first occurrence of card from the deck.
     *
     * @param card The card to remove
     * @return True if card was in deck.
     */
    //TODO: check if this version of this is still needed.
    public boolean remove(Card card) {
        return deck.remove(card);
    }

    /**
     * Remove the card at index cardNumber.
     *
     * @param cardNumber The index of the card.
     * @return True if index existed
     */
    //TODO: check if this version of this is still needed.
    public boolean remove(int cardNumber) {
        if(0 <= cardNumber && cardNumber < deck.size()) {
            deck.remove(cardNumber);
            return true;
        }
        return false;
    }

    /**
     * Remove the first card of the deck and returns it.
     *
     * @return Returns the first card or null if deck is emtpy.
     *
     */
    public Card pop() {
        if (!deck.isEmpty()) {
            return deck.remove(0);
        }
        return null;
    }

    /**
     * Shuffles the deck.
     */
    public void shuffle() {
        Collections.shuffle(deck);
    }

    /**
     * Move the deck completely to another deck and empty it.
     *
     * @param deck The deck where you want to move the original deck to.
     * @return False if the given deck does not exists.
     */
    public boolean moveDeckTo(Deck deck) {
        if (deck != null) {
            deck.deck.addAll(this.deck);
            this.deck.clear();
            return true;
        }
        return false;
    }

    /**
     * Move a card from one deck to another.
     *
     * @param card The card to move.
     * @param deck The deck where to move the card to.
     * @return True if card exists in deck.
     */
    //TODO: Check if this version is needed.
    public boolean moveCardToDeck(Card card, Deck deck) {
        if (this.deck.contains(card)) {
            this.remove(card);
            deck.add(card);
            return true;
        }
        return false;
    }

    /**
     * Move a card from one deck to another.
     *
     * @param cardNumber The index of the card to move.
     * @param deck The deck where to move the card to.
     * @return True if card exists in deck.
     */
    //TODO: Check if this version is needed.
    public boolean moveCardToDeck(int cardNumber, Deck deck) {
        if (0 <= cardNumber && cardNumber < this.deck.size()) {
            deck.add(this.getCard(cardNumber));
            this.remove(cardNumber);
            return true;
        }
        return false;
    }

    /**
     * Check if the deck contains cards that can be played.
     *
     * @return True if it does.
     */
    public boolean hasPlayableCards() {
        for (Card c : deck) {
            if (c.isPlayable()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if the deck contains any kingdom cards.
     *
     * @return Returns true if it contains kingdom cards.
     */
    public boolean hasKingdomCards() {
        for (Card c : deck) {
            if (c.isKingdom()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the deck contains any reaction cards.
     *
     * @return Returns true if it contains reaction cards.
     */
    public boolean hasReactionCards() {
        for (Card c : deck) {
            if (c.isReaction()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the deck contains any victory cards.
     *
     * @return Returns true if it contains victory cards.
     */
    public boolean hasVictoryCards() {
        for (Card c : deck) {
            if (c.isVictory()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the deck contains any treasure cards.
     *
     * @return Returns true if it contains treasure cards.
     */
    public boolean hasTreasureCards() {
        for (Card c : deck) {
            if (c.isTreasure()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts all the victory points that are in the given deck. 
     * Takes in account the Garden card.
     *
     * @return The total amount of points in the deck.
     */
    //TODO: probably try to move this to engine, only engine knows how to count this stuff.
    public int countVictoryPoints() {
        int victoryPoints = 0;
        int gardenCard = 0;
        if (this.hasVictoryCards()) {
            for (Card c : deck) {
                if (c.isVictory()) {
                    victoryPoints += c.getVictoryPoints();
                    if (c.getName().equals("gardens")) {
                        gardenCard++;
                    }
                }
            }
            if (gardenCard > 0) {
                victoryPoints += deck.size() / 10 * gardenCard;
            }
        }
        return victoryPoints;
    }

    /**
     * Checks whether a normal deck is empty.
     *
     * @return True if deck is empty.
     */
    public boolean isEmpty() {
        return deck.isEmpty();
    }

    /**
     * Returns the size of a normal deck.
     *
     * @return The size of a normal deck.
     */
    public int size() {
        return deck.size();
    }
    
    @Override
    public String toString() {
        if (deck.isEmpty()) {
            return "";
        } else {
            String deckContent = "";
            int i = 1;
            for (Card c : deck) {
                deckContent += String.format("%02d) %s", i, c.toString());
                if(i < deck.size()) {
                    deckContent += "\n";
                }
                i++;
            }
            return deckContent;
        }
    }

    @Override
    public Iterator<Card> iterator() {
        return deck.iterator();
    }

}
