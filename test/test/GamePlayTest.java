/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import Card.Card;
import Engine.Deck;
import Engine.GameEngine;
import Engine.Player;
import java.util.Iterator;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Jonas
 */
public class GamePlayTest {

    private GameEngine ge;

    @Before
    public void setUp() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.addPlayer("Vera");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 18});
        ge.startGame();
    }

    @Test
    //Test if player can play 1 Treasure card and checks whether the game phase changed correctly
    public void playOneTreasureCardInBuyPhaseTest() {
        Deck deck = ge.getCurrentPlayer().getDeck("hand");
        int coins = 0;
        for (Card c : deck) {
            if (c.isTreasure()) {
                coins = c.getCoins();
                ge.playCard(c);
                break;
            }
        }
        assertEquals(coins, ge.getCurrentPlayer().getCoins());
    }

    @Test
    //Test if player can play all Treasure cards and checks whether the game phase changed correctly
    public void playAllTreasureCardInBuyPhaseTest() {
        Deck deck = ge.getCurrentPlayer().getDeck("hand");
        int coins = 0;
        for (int i = 0; i < deck.size(); i++) {
            Card c = deck.getCard(i);
            if (c.isTreasure()) {
                coins += c.getCoins();
                ge.playCard(c);
                i--;
            }
        }
        assertEquals(coins, ge.getCurrentPlayer().getCoins());
    }

    @Test
    //Test if player can buy 1 card in buy phase
    public void buyOneCardInBuyPhaseTest() {
        Deck deck = ge.getCurrentPlayer().getDeck("hand");
        for (int i = 0; i < deck.size(); i++) {
            Card c = deck.getCard(i);
            if (c.isTreasure()) {
                ge.playCard(c);
                i--;
            }
        }
        for (Card c : ge.getStack("kingdom")) {
            if (c.getCost() <= ge.getCurrentPlayer().getCoins()) {
                if (ge.buyCard(c, "kingdom")) {
                    break;
                }
            }
        }
        assertEquals(1, ge.getCurrentPlayer().getDeck("discard").size());
    }

    @Test
    //Test if player can not buy 2nd card when he has only 1 buy in buy phase
    public void buyMoreCardsThenBuysInBuyPhase() {
        Deck deck = ge.getCurrentPlayer().getDeck("hand");
        for (int i = 0; i < deck.size(); i++) {
            Card c = deck.getCard(i);
            if (c.isTreasure()) {
                ge.playCard(c);
                i--;
            }
        }
        for (Card c : ge.getStack("kingdom")) {
            if (c.getCost() <= ge.getCurrentPlayer().getCoins()) {
                if (ge.buyCard(c, "kingdom")) {
                }
            }
        }
        assertEquals(1, ge.getCurrentPlayer().getDeck("discard").size());
    }

    @Test
    //Test if player bought a card he can not play any other card
    public void buyCardThenPlayCardTest() {
        for(Card c: ge.getStack("treasure")) {
            if(c.getName().equals("copper")) {
                ge.buyCard(c, "treasure");
                break;
            }
        }
        for (Card c : ge.getStack("kingdom")) {
            if(ge.buyCard(c, "kingdom")) {
                fail();
            }
            break;
        }
    }
    
    @Test
    //Test if player changes after ending buy phase
    //Since the game starts in buy phase only need to change one time.
    public void changeTurnAndEndTurnTest() {
        String phase = ge.getPhase();
        Player p = ge.getCurrentPlayer();
        ge.endPhase();
        assertNotEquals(phase, ge.getPhase());
        assertNotEquals(p, ge.getCurrentPlayer());
    }

}
