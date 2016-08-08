package test;

import static org.junit.Assert.*;
import org.junit.Test;
import Engine.Deck;
import Engine.Card;

public class DeckTest {
	Deck deck = new Deck();
	Card gold = new Card("Gold");
	Card duchy = new Card("Duchy");
	Card laboratory = new Card("Laboratory");
	Deck newDeck = new Deck();
	
	@Test
	public void addOneCardToDeck() {
		deck.add(duchy);
		assertEquals(1,deck.size());
	}
	
	@Test
	public void addTwoCardsToDeck() {
		deck.add(duchy);
		deck.add(laboratory);
		assertEquals(2,deck.size());
	}
	
	@Test
	public void shuffleDeck() {
		deck.add(duchy);
		deck.add(laboratory);
		deck.add(gold);
		deck.add(duchy);
		deck.add(gold);
		deck.add(laboratory);
		deck.add(duchy);
		deck.add(laboratory);
		deck.add(gold);
		deck.shuffle();
		assertNotEquals("Duchy: 5,Laboratory: 5,Gold: 6,", deck.toString());
	}
	
	@Test
	public void moveDecktoNewDeck() {
		deck.add(duchy);
		deck.add(laboratory);
		deck.add(gold);
		deck.add(duchy);
		deck.add(gold);
		deck.add(laboratory);
		deck.add(duchy);
		deck.moveDeckTo(newDeck);
		assertEquals(0, deck.size());
		assertEquals(7, newDeck.size());
	}
}
