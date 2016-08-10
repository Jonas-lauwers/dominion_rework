package Engine;

import Card.Card;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.io.Serializable;

/**
 * An object representing stack to hold cards. It cant hold equal Card objects.
 * But it holds a counter with the number of the card in the stack.
 *
 * @author Jonas
 */
public class Stack implements Serializable, Iterable<Card> {
    
    static final long serialVersionUID = 1337;
    private final Map<Card, Integer> stack;
    private Card[] sorted;
    
    /**
     * Creates an empty card stack.
     */
    public Stack() {
        stack = new HashMap<>();
    }
    
    /**
     * Adds a card to the stack. Adds always in front,
     * so if 2 cards are of equal cost the last added will be in front of the stack.
     * 
     * @param card The card to add.
     * @param numberOfCards The value indicating how many cards are added in the stack.
     */
    public void add(Card card, int numberOfCards) {
        if(stack.containsKey(card)) {
            numberOfCards += stack.get(card);
        }
        stack.put(card, numberOfCards);
    }
    
    /**
     * Returns a sorted array with all the cards in the stack.
     * 
     * @return Sorted array with all cards in stack.
     */
    public Card[] getCards() {
        if(sorted == null) {
            sorted = new Card[stack.keySet().size()];
            stack.keySet().toArray(sorted);
            Arrays.sort(sorted);
        }
        return sorted;
    }
    
    /**
     * Returns the value of how much are in the stack of the given card.
     * 
     * @param card The card of which to return the value.
     * @return The number of cards in the stack. If card is not existing returns -1.
     */
    public int getNumberOfCards(Card card) {
        if(stack.containsKey(card)) {
            return stack.get(card);
        }
        return -1;
    }
    
    /**
     * Removes one card from the stack of card if it exists in the stack.
     * If it exists but the counter is 0 it does not remove it and returns false.
     * 
     * @param card The card to remove.
     * @return True if card is removed.
     */
    public boolean remove(Card card) {
        if(stack.containsKey(card) && stack.get(card) > 0) {
            add(card, -1);
            return true;
        }
        return false;
    }
    
    /**
     * Checks if the stack of the card is empty.
     * If card does not exist it also returns false.
     * 
     * @param card The card stack to check.
     * @return True if stack is empty, False if stack is not empty or if stack does not exits.
     */
    public boolean isEmpty(Card card) {
        if(stack.containsKey(card) && getNumberOfCards(card) == 0) {
            return true;
        }
        return false;
    }
    
    @Override
    public Iterator<Card> iterator() {
        return stack.keySet().iterator();
    }
    
    @Override
    public String toString() {
        if(sorted == null) {
            getCards();
        }
        String stackString = "";
        int counter = 1;
        for(Card c: sorted) {
            stackString += String.format("\t%02d) (%02d) %s", counter, this.getNumberOfCards(c), c.toString());
            if(counter < stack.size()) {
                    stackString += "\n";
                }
                counter++;
        }
        return stackString;
    }
}
