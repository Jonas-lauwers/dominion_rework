/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.util.*;
import Card.Card;
/**
 *
 * @author Jonas
 */
public class Stack {
    
    static final long serialVersionUID = 1337;
    private Map<Card, Integer> stack;
    
    /**
     * Creates an empty card stack.
     */
    public Stack() {
        stack = new HashMap<>();
    }
    
    /**
     * Adds a card to the stack.
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
     * Returns the value of how much are in the stack of the given card.
     * @param card The card of which to return the value.
     * @return The number of cards in the stack.
     */
    public int getNumberOfCards(Card card) {
        if(stack.containsKey(card)) {
            return stack.get(card);
        }
        return -1;
    }
    
    /**
     * Removes one card from the stack of card if it exists in the stack and if the stack is not already 0.
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
     * Checks if the stack of the card is empty. If card does not exist it also returns false.
     * @param card The card stack to check.
     * @return True if stack is empty, False if stack is not empty or if stack does not exits.
     */
    public boolean isEmpty(Card card) {
        if(stack.containsKey(card) && getNumberOfCards(card) == 0) {
            return true;
        }
        return false;
    }

    //TODO This is only here for testing purpose
    public void setCount(Card card, int count) {
        if(stack.containsKey(card)) {    
            stack.put(card, count);
        }
    }
}
