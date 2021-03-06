package test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import Engine.GameEngine;
import Engine.Player;
import Engine.Card;
import Database_connection.SaveGameConnection;
import Engine.Stack;

public class GameEngineTest {

    private GameEngine ge;

    @Test
    public void addPlayerTest() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
    }

    @Test
    public void addTooMuchPlayersTest() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.addPlayer("kamiel");
        ge.addPlayer("joske");
        try {
            ge.addPlayer("gejost");
            fail();
        } catch (IllegalStateException e) {
            assertEquals("There are already 4 players in the game.", e.getMessage());
        }
    }

    @Test
    public void addSamePlayerTest() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        try {
            ge.addPlayer("jonas");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Player already exists. Enter another name", e.getMessage());
        }
    }

    @Test
    public void setKingdomCardsTest() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
    }

    @Test
    public void premadeDeckTest() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.usePresetDeck("FirstGame");
    }

    @Test
    public void setKingdomCardsImpossibleIndexesTest() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        assertFalse(ge.setPlayableKingdomCards(new int[]{25, 26, 27, 28, 29, 30, 31, 32, 33, 34}));
    }

    @Test
    public void changePlayerTest() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        ge.startGame();
        assertEquals("jonas", ge.getCurrentPlayer().getName());
        ge.endTurn();
        assertEquals("emiel", ge.getCurrentPlayer().getName());
    }

    @Test
    public void changePlayerFiveTimes() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.addPlayer("joske");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        ge.startGame();
        for (int i = 0; i < 5; i++) {
            ge.endTurn();
        }
        assertEquals("joske", ge.getCurrentPlayer().getName());
    }

    @Test
    public void endGameTestNeutral() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        ge.startGame();
        Card card = new Card("province");
        Stack stack = ge.getStack("victory");
        if (stack.getNumberOfCards(card) > 0) {
            stack.add(card, -stack.getNumberOfCards(card));
        }
        ge.endTurn();
        List<Player> players = ge.getPlayers();
        assertEquals(3, players.get(0).getScore());
        assertEquals(3, players.get(1).getScore());
    }

    @Test
    public void endGameTestWinner() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 9, 15, 13, 14});
        ge.startGame();
        Player player = ge.getCurrentPlayer();
        player.getDeck("discard").add(new Card("estate"));
        player.getDeck("hand").add(new Card("province"));
        player.getDeck("deck").add(new Card("duchy"));
        player.getDeck("discard").add(new Card("estate"));
        player.getDeck("hand").add(new Card("province"));
        player.getDeck("deck").add(new Card("duchy"));
        player.getDeck("discard").add(new Card("estate"));
        player.getDeck("hand").add(new Card("province"));
        player.getDeck("deck").add(new Card("gardens"));
        Card card = new Card("province");
        Stack stack = ge.getStack("victory");
        if (stack.getNumberOfCards(card) > 0) {
            stack.add(card, -stack.getNumberOfCards(card));
        }
        ge.endTurn();
        List<Player> players = ge.getPlayers();
        assertEquals(31, players.get(0).getScore());
        assertEquals(3, players.get(1).getScore());
    }

    //Tests of card actions
    @Test
    public void discardFromHandOneCard() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        ge.startGame();
        ge.discardFromHand(ge.getCurrentPlayer(), 1);
        assertEquals(4, ge.getCurrentPlayer().getDeck("hand").size());
    }

    @Test
    public void discardFromHandthreeCards() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        ge.startGame();
        ge.discardFromHand(ge.getCurrentPlayer(), 1);
        ge.discardFromHand(ge.getCurrentPlayer(), 1);
        ge.discardFromHand(ge.getCurrentPlayer(), 1);
        assertEquals(2, ge.getCurrentPlayer().getDeck("hand").size());
    }

    @Test
    public void discardFromHandthreeCardsRandom() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        ge.startGame();
        ge.discardFromHand(ge.getCurrentPlayer(), 1);
        ge.discardFromHand(ge.getCurrentPlayer(), 3);
        ge.discardFromHand(ge.getCurrentPlayer(), 2);
        assertEquals(2, ge.getCurrentPlayer().getDeck("hand").size());
    }

    @Test
    public void trashFromHandoneCard() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        ge.startGame();
        ge.trashFromHand(ge.getCurrentPlayer(), 1);
        assertEquals(1, ge.getCurrentPlayer().getDeck("trash").size());
    }

    @Test
    public void trashFromHandThreeCards() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        ge.startGame();
        ge.trashFromHand(ge.getCurrentPlayer(), 1);
        ge.trashFromHand(ge.getCurrentPlayer(), 1);
        ge.trashFromHand(ge.getCurrentPlayer(), 1);
        assertEquals(3, ge.getCurrentPlayer().getDeck("trash").size());
    }

    @Test
    public void getCardOfValuefive() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 18});
        ge.startGame();
        for (Card c : ge.getStack("kingdom").getCards()) {
            if (c.getCost() == 5) {
                ge.getCardOfValue("kingdom", c, 5);
                break;
            }
        }
        assertEquals(1, ge.getCurrentPlayer().getDeck("discard").size());
    }

    @Test
    public void getCardOfValuefiveWhileMaxValueIsFour() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 18});
        ge.startGame();
        for (Card c : ge.getStack("kingdom").getCards()) {
            if (c.getCost() == 5) {
                ge.getCardOfValue("kingdom", c, 4);
                break;
            }
        }
        assertEquals(0, ge.getCurrentPlayer().getDeck("discard").size());
    }

    @Test
    public void trashPlayerdCard() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 18});
        ge.startGame();
        Card trashcard = new Card("copper");
        ge.getCurrentPlayer().getDeck("hand").add(trashcard);
        ge.playCard(trashcard);
        ge.trashPlayedCard();
        assertEquals(trashcard, ge.getCurrentPlayer().getDeck("trash").getCard(0));
    }
    
    @Test
    public void loadGame() {
        try {
            ge = new SaveGameConnection().loadGame("test");
            ge.getPlayerStatus(ge.getCurrentPlayer());
        }
        catch (Exception e) {
            fail();
        }
    }
    
//    @Test
//    public void actionListWithOneActionNoParamsTest() {
//        ge = new GameEngine();
//        ge.addPlayer("jonas");
//        ge.addPlayer("emiel");
//        ge.setExpansions("Dominion");
//        ge.getChoosableKingdomCards();
//        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 18});
//        ge.startGame();
//        Card trashcard = new Card("cellar");
//        ge.getCurrentPlayer().getDeck("hand").add(trashcard);
//        ge.playCard(trashcard);
//        assertEquals(trashcard, ge.getCurrentPlayer().getDeck("trash").getCard(0));
//    }
    
    @Test
    public void actionListWithOneActionOneParamsTest() {
        ge = new GameEngine();
        ge.addPlayer("jonas");
        ge.addPlayer("emiel");
        ge.setExpansions("Dominion");
        ge.getChoosableKingdomCards();
        ge.setPlayableKingdomCards(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 18});
        ge.startGame();
        Card trashcard = new Card("cellar");
        ge.getCurrentPlayer().getDeck("hand").add(trashcard);
        ge.playCard(trashcard);
        //assertEquals(trashcard, ge.getCurrentPlayer().getDeck("trash").getCard(0));
    }
}
