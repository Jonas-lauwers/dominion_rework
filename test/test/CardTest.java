package test;

import static org.junit.Assert.*;


import org.junit.Test;
import Card.Card;

public class CardTest {

	@Test
	public void convertToStringTest(){
		Card c = new Card("Gold");
		assertEquals("gold       Cost: 6", c.toString());
		
		c = new Card("Village");
		assertEquals("village              Cost: 3 - Actions: 2 - Buys: 0 - Coins: 0 - Cards: 1 - Description: ", c.toString());
		
		c = new Card("Cellar");
		assertEquals("cellar               Cost: 2 - Actions: 1 - Buys: 0 - Coins: 0 - Cards: 0 - Description: Discard any number of cards. +1 Card per card discarded.", c.toString());
	}
	

}
