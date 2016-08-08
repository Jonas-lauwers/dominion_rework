/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import Engine.Card;
import Engine.Stack;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Jonas
 */
public class StackTest {
    
    private Card[] cards;
    private Stack stack;
    
    @Before
    public void setUp() {
        String[] test = new String[] {"cellar", "moat", "chapel", "chancellor", "village", "woodcutter", "workshop", "bureaucrat", "militia", "moneylender" };
        cards = new Card[test.length];
        for(int i = 0; i < test.length; i++) {
            cards[i] = new Card(test[i]);
        }
        stack = new Stack();
    }

    @Test
    public void addOneCardTest() {
        stack.add(cards[0], 10);
        assertEquals(1, stack.getCards().length);
        assertEquals(10, stack.getNumberOfCards(cards[0]));
    }
    
    @Test
    public void addTwoSameCardTest() {
        stack.add(cards[0], 10);
        stack.add(cards[0], 2);
        assertEquals(1, stack.getCards().length);
        assertEquals(12, stack.getNumberOfCards(cards[0]));
    }

    @Test
    public void addTenDifferentCardsTest() {
        for(Card c: cards) {
            stack.add(c, 10);
        }
        assertEquals(10, stack.getCards().length);
    }

    @Test
    public void removeOneCardTest() {
        stack.add(cards[0],3);
        stack.remove(cards[0]);
        assertEquals(2, stack.getNumberOfCards(cards[0]));
    }
    
    @Test
    public void removeCardThatDoesntExitsTest() {
        stack.add(cards[0],1);
        if(stack.remove(cards[1])) {
            fail();
        }
        assertEquals(1, stack.getNumberOfCards(cards[0]));
        assertEquals(1, stack.getCards().length);
    }
    
    @Test
    public void removeCardFromZeroStack() {
        stack.add(cards[0],0);
        if(stack.remove(cards[0])) {
            fail();
        }
        assertEquals(0, stack.getNumberOfCards(cards[0]));
        assertEquals(1, stack.getCards().length);
    }
    
    //INFO keep in mind that when adding to the stack they get added in front.
    @Test
    public void toStringText() {
        stack.add(cards[0],0);
        stack.add(cards[1],0);
        stack.add(cards[3],0);
        assertEquals("\t01) (00) "+cards[1]+"\n\t02) (00) "+cards[0]+"\n\t03) (00) "+cards[3], stack.toString());
    }
    
}
