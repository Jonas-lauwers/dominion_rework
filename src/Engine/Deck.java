package Engine;

import java.util.*;
import Card.Card;

import java.io.Serializable;

public class Deck implements Comparable<Object>, Serializable {

    static final long serialVersionUID = 1337;
    private ArrayList<Card> deck;
    private boolean isSingleCardDeck;
    private int numberOfCards;

    public Deck() {
        deck = new ArrayList<Card>();
        this.isSingleCardDeck = false;
    }

    public Deck(Card card, int numberOfCards) {
        this();
        this.numberOfCards = numberOfCards;
        this.isSingleCardDeck = true;
        this.add(card);
    }

    //TODO something wrong here .... why check for singlecarddeck and deck.size
    public void add(Card card) {
        if (!(isSingleCardDeck && deck.size() == 1)) {
            add(deck.size(), card);
        }
    }

    public void add(int index, Card card) {
        if (!isSingleCardDeck || deck.isEmpty()) {
            deck.add(index, card);
        }
    }

    public Card getCard(int cardNumber) {
        if (!isSingleCardDeck) {
            return deck.get(cardNumber);
        } else {
            return deck.get(0);
        }
    }
    
    //TODO check if integer version of this is still neede
    public void remove(Card card) {
        if(!isSingleCardDeck) {
            deck.remove(card);
        }
        else {
            if(deck.contains(card)) {
                numberOfCards--;
            }
        }
    }

    public void remove(int cardNumber) {
        if (!isSingleCardDeck) {
            deck.remove(cardNumber);
        } else {
            numberOfCards--;
        }
    }

    //TODO rework the way to return the first card in the deck and return the card if the deck is singlecarddeck
    public Card pop() {
        if (!isSingleCardDeck) {
            Card firstCard = null;
            for (Card c : deck) {
                if (c != null && firstCard == null) {
                    firstCard = c;
                    deck.remove(c);
                    return firstCard;
                }
            }
            return firstCard;
        }
        return null;
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public void moveDeckTo(Deck deck) {
        deck.deck.addAll(this.deck);
        deck.isSingleCardDeck = this.isSingleCardDeck;
        deck.numberOfCards = this.numberOfCards;
        this.deck.clear();
    }

    // duplicate moveCardToDeck ... check if the integer version is needed
    public void moveCardToDeck(Card card, Deck deck) {
        this.remove(card);
        deck.add(card);
    }

    public void moveCardToDeck(int cardNumber, Deck deck) {
        Card card = this.getCard(cardNumber);
        this.remove(cardNumber);
        deck.add(card);
    }

    public boolean hasKingdomCards() {
        boolean hasKingdomCards = false;
        for (Card c : deck) {
            if (c.getType().equals("Action") || c.getType().equals("Attack") || c.getType().equals("Reaction")) {
                hasKingdomCards = true;
            }
        }
        return hasKingdomCards;
    }

//	public boolean hasReactionCards(){
//		for(Card c : deck){
//			if(c.getType().equals("Reaction")){
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	public boolean hasVictoryCards(){
//		for(Card c : deck){
//			if(c.getType().equals("Victory")){
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	public boolean hasTreasureCards(){
//		for(Card c : deck){
//			if(c.getType().equals("Treasure")){
//				return true;
//			}
//		}
//		return false;
//	}
    public boolean hasCardOfType(String cardType) {
        for (Card c : deck) {
            if (c.getType().equals(cardType)) {
                return true;
            }
        }
        return false;
    }

    public int countVictoryPoints() {
        int victoryPoints = 0;
        int gardenCard = 0;
        if (!deck.isEmpty()) {
            for (Card c : deck) {
                if (c.getName().equals("Gardens")) {
                    gardenCard++;
                } else if (c.getType().equals("Victory")) {
                    victoryPoints += c.getVictoryPoints();
                }
            }
            if (gardenCard > 0) {
                victoryPoints += deck.size() / 10 * gardenCard;
            }
        }
        return victoryPoints;
    }

    public boolean isEmpty() {
        if (!isSingleCardDeck) {
            return deck.isEmpty();
        } else {
            return numberOfCards == 0;
        }
    }

    public int size() {
        if (!isSingleCardDeck) {
            return deck.size();
        } else {
            return numberOfCards;
        }
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Deck) {
            Deck d = ((Deck) o);
            if (this.isSingleCardDeck && d.isSingleCardDeck) {
                if (this.getCard(0).getCost() > d.getCard(0).getCost()) {
                    return 1;
                } else if (this.getCard(0).getCost() < d.getCard(0).getCost()) {
                    return -1;
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        if (deck.isEmpty()) {
            return "\n";
        } else {
            String deckContent = "";
            if (!isSingleCardDeck) {
                int i = 1;
                for (Card c : deck) {
                    deckContent += String.format("\t %02d) %s", i, c.toString());
                    i++;
                }
            } else {
                deckContent = String.format("(%02d) %s", this.numberOfCards, deck.get(0).toString());
            }
            return deckContent;
        }
    }

    //TODO This is only here for testing purpose
    public void setCount(int count) {
        this.numberOfCards = count;
    }

    //TODO verify if we will need this after refactoring GameEngine
    public String[] toStringArray() {
        if (deck.isEmpty()) {
            return new String[]{};
        }
        if (!isSingleCardDeck) {
            String[] deckArray = new String[deck.size()];
            for (int i = 0; i < deck.size(); i++) {
                deckArray[i] = this.getCard(i).getName();
            }
            return deckArray;
        } else {
            return new String[]{this.getCard(0).getName(), String.valueOf(this.numberOfCards)};
        }
    }

}
