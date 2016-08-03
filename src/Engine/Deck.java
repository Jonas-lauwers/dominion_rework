package Engine;

import java.util.*;
import Card.Card;

import java.io.Serializable;
//Comparable<Object>
public class Deck implements Serializable {

    static final long serialVersionUID = 1337;
    private ArrayList<Card> deck;
    private boolean isSingleCardDeck;
    private int[] numberOfCards;
    
    /**
     * creates a simple deck with no limit of cards, where each added cards is regarded as unique.
     */
    public Deck() {
        deck = new ArrayList<Card>();
        this.isSingleCardDeck = false;
    }

    /**
     * Creates a deck with a limit number of cards and keeps a counter for each of those cards of how much of them are added.
     * @param numberOfCards The total number of unique cards that will be added. 
     */
    public Deck(int numberOfCards) {
        this();
        this.numberOfCards = new int[numberOfCards];
        this.isSingleCardDeck = true;
    }

    /**
     * Add a card to deck, does nothing for a deck with card limit if it's full.
     * @param card The card to add.
     */
    public void add(Card card) {
        if(isSingleCardDeck) {
            if(deck.contains(card)) {
                numberOfCards[deck.indexOf(card)] += 1;
            }
            else if(deck.size() < numberOfCards.length) {
                deck.add(card);
                numberOfCards[deck.indexOf(card)] = 1;
            }
        }
        else {
            this.add(deck.size(),card);
        }
    }
    
    /**
     * Add card to the deck at a certain index, does not work for a deck with card limit.
     * @param index The index to place the card.
     * @param card The card to add.
     */
    public void add(int index, Card card) {
        if (!isSingleCardDeck) {
            deck.add(index, card);
        }
    }

    
    //TODO check whether we need this since we just can check on existance and remove it ....
//    public Card getCard(int cardNumber) {
//        if (!isSingleCardDeck) {
//            return deck.get(cardNumber);
//        } else {
//            return deck.get(0);
//        }
//    }
    
    /**
     * Remove the card from the deck. Lowers counter of numbered deck by 1 if it exits.
     * @param card The card to remove
     */
    //TODO check if integer version of this is still needed
    public void remove(Card card) {
        if(!isSingleCardDeck) {
            deck.remove(card);
        }
        else {
            if(deck.contains(card)) {
                if(numberOfCards[deck.indexOf(card)] > 0 ) {
                    numberOfCards[deck.indexOf(card)]--;
                }
            }
        }
    }
    
//    public void remove(int cardNumber) {
//        if (!isSingleCardDeck) {
//            deck.remove(cardNumber);
//        } 
//        else {
//            numberOfCards[cardNumber]--;
//        }
//    }

    /**
     * Remove the first card of the deck and returns it. Works only on not limited card decks.
     * Return Returns the first card.
     */
    public Card pop() {
        if(!isSingleCardDeck && !deck.isEmpty()) {
            return deck.remove(0);
        }
        return null;
    }
//    public Card pop() {
//        if (!isSingleCardDeck) {
//            Card firstCard = null;
//            for (Card c : deck) {
//                if (c != null && firstCard == null) {
//                    firstCard = c;
//                    deck.remove(c);
//                    return firstCard;
//                }
//            }
//            return firstCard;
//        }
//        return null;
//    }

    /**
     * Shuffles the deck. Should not be used on limited card decks since the card count will not be alligned
     */
    public void shuffle() {
        Collections.shuffle(deck);
    }

    /**
     * Move a deck to another. Only for normal decks.
     * @param deck The deck where you want to move the original deck to.
     */
    public void moveDeckTo(Deck deck) {
        if(!isSingleCardDeck) {
            deck.deck.addAll(this.deck);
            deck.isSingleCardDeck = this.isSingleCardDeck;
            //deck.numberOfCards = this.numberOfCards;
            this.deck.clear();
        }
    }

    /**
     * Move a card from one deck to another. Only for normal decks.
     * @param card The card to move.
     * @param deck The deck where to move the card to.
     */
    // duplicate moveCardToDeck ... check if the integer version is needed
    public void moveCardToDeck(Card card, Deck deck) {
        if(!isSingleCardDeck) {
            this.remove(card);
            deck.add(card);
        }
    }

//    public void moveCardToDeck(int cardNumber, Deck deck) {
//        Card card = this.getCard(cardNumber);
//        this.remove(cardNumber);
//        deck.add(card);
//    }

    /**
     * Check if the deck contains any kingdom cards.
     * @return Returns true if it contains kingdomcards.
     */
    public boolean hasKingdomCards() {
        boolean hasKingdomCards = false;
        for (Card c : deck) {
            if (c.getType().equals("Action") || c.getType().equals("Attack") || c.getType().equals("Reaction")) {
                hasKingdomCards = true;
            }
        }
        return hasKingdomCards;
    }
    
    /**
     * Check if the deck contains cards that can be played.
     * @return True if it does.
     */
    public boolean hasPlayableCards() {
        boolean hasPlayableCards = false;
        for(Card c: deck) {
            if(!c.getPlayableTurn().equals("never")) {
                hasPlayableCards = true;
            }
        }
        return hasPlayableCards;
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
    
    /**
     * Checks if the deck contains cards of the type.
     * @param cardType The card type you want to look for
     * @return True if it does contain cards.
     */
    public boolean hasCardOfType(String cardType) {
        for (Card c : deck) {
            if (c.getType().equals(cardType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts all the victorypoints  that are in the given deck. Takes in acount the Garden card.
     * @return The total amount of points in the deck.
     */
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

    /**
     * Checks whether a normal deck is empty. For limited card decks us isEmpty(Card card)
     * @return True if deck is empty.
     */
    public boolean isEmpty() {
        if (!isSingleCardDeck) {
            return deck.isEmpty();
        }
        throw new IllegalStateException();
    }

    /**
     * Checks whether a limited card deck has no cards left of the given card.
     * @param card The card to check on.
     * @return True if cards 0 number of cards.
     */
    public int isEmpty(Card card) {
        if(isSingleCardDeck) {
            return numberOfCards[deck.indexOf(card)];
        }
        throw new IllegalStateException();
    }
    
    /**
     * Returns the size of a normal deck. For limited card deck use size(Card card).
     * @return The size of a normal deck, or if the deck is limited amount it will return -1.
     */
    public int size() {
        if (!isSingleCardDeck) {
            return deck.size();
        } else {
            return -1;
        }
    }
    
    /**
     * Returns the size of the deck or the size of a limited deck's card.
     * @param card The card of wich you want to return the number.
     * @return The total number of that card in the deck.
     */
    public int size(Card card) {
        if(!isSingleCardDeck)
            return this.size();
        else
            return numberOfCards[deck.indexOf(card)];
    }

    //TODO mae the cards comparable so we can sort them in the limited card decks( for kingdomCards deck for example)
//    @Override
//    public int compareTo(Object o) {
//        if (o instanceof Deck) {
//            Deck d = ((Deck) o);
//            if (this.isSingleCardDeck && d.isSingleCardDeck) {
//                if (this.getCard(0).getCost() > d.getCard(0).getCost()) {
//                    return 1;
//                } else if (this.getCard(0).getCost() < d.getCard(0).getCost()) {
//                    return -1;
//                } else {
//                    return 0;
//                }
//            } else {
//                return 0;
//            }
//        } else {
//            throw new IllegalArgumentException();
//        }
//    }

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
    public void setCount(Card card, int count) {
        if(isSingleCardDeck) {
            
            this.numberOfCards[deck.indexOf(card)] = count;
        }
    }

    //TODO verify if we will need this after refactoring GameEngine
    public String[] toStringArray() {
        if (deck.isEmpty()) {
            return new String[]{};
        }
//        if (!isSingleCardDeck) {
            String[] deckArray = new String[deck.size()];
            for(Card c : deck) {
                deckArray[deckArray.length] = c.getName();
            }
//            for (int i = 0; i < deck.size(); i++) {
//                deckArray[i] = this.getCard(i).getName();
//            }
            return deckArray;
//        } else {
//            return new String[]{this.getCard(0).getName(), String.valueOf(this.numberOfCards)};
//        }
    }

}
