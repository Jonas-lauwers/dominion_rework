package test;

import static org.junit.Assert.*;

import org.junit.Test;

import Database_connection.cardConnection;
import java.util.ArrayList;

public class cardConnectionTest {

	@Test
	public void testGetCards() {
	//	System.out.println(cardConnection.getCards());
	}
	
	@Test
	public void testGetKingdomCards() {
            assertEquals(25,new cardConnection().getKingdomCards(new String[]{"Dominion"}).size());
	}
	
	public void testGetKingdomCardsWithTwoExpansions() {
            assertEquals(38,new cardConnection().getKingdomCards(new String[]{"Dominion","Cornucopia"}).size());
	}
}
