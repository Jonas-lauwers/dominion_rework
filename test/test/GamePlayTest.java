/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import Engine.Deck;
import Engine.GameEngine;
import org.junit.*;
import static org.junit.Assert.assertEquals;

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
        ge.setPlayableKingdomCards(new int[] {0,1,2,3,4,5,6,7,8,18});
        ge.startGame();
    }
    
    @Test
    public void playOneTreasureCardTest() {
        Deck deck = ge.getCurrentPlayer().getDeck("hand");
        int coins = 0;
        for(int i = 0; i < deck.size(); i++) {
            if(deck.getCard(i).isTreasure()) {
                coins = deck.getCard(i).getCoins();
                ge.playCard(deck.getCard(i));
                break;
            }
            
        }
        assertEquals(coins, ge.getCurrentPlayer().getCoins());
    }
    
    @Test
    public void playAllTreasureCardTest() {
        Deck deck = ge.getCurrentPlayer().getDeck("hand");
        int coins = 0;
        for(int i = 0; i < deck.size(); i++) {
            if(deck.getCard(i).isTreasure()) {
                coins += deck.getCard(i).getCoins();
                ge.playCard(deck.getCard(i));
            }
        }
        assertEquals(coins, ge.getCurrentPlayer().getCoins());
    }
    
}
