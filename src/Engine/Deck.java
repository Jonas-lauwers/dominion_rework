package Engine;

import java.util.*;
import Card.Card;

import java.io.Serializable;
//Comparable<Object>

public class Deck implements Serializable, Iterable<Card> {

    static final long serialVersionUID = 1337;
    private ArrayList<Card> deck;

    /**
     * Creates an empty deck.
     */
    public Deck() {
        deck = new ArrayList<Card>();
    }

    /**
     * Add a card to deck.
     *
     * @param card The card to add.
     */
    public void add(Card card) {
        this.add(deck.size(), card);
    }

    /**
     * Add card to the deck at a certain index.
     *
     * @param index The index to place the card.
     * @param card The card to add.
     */
    public void add(int index, Card card) {
        deck.add(index, card);
    }

    /**
     * Returns the Card thats found at the index cardNumber
     *
     * @param cardNumber The index of the card.
     * @return The card at the index or null if index doesn't exist.
     */
    public Card getCard(int cardNumber) {
        if (cardNumber < deck.size()) {
            return deck.get(cardNumber);
        }
        return null;
    }

    /**
     * Remove the card from the deck.
     *
     * @param card The card to remove
     * @return True if card was in deck.
     */
    //TODO: check if this version of this is still needed.
    public boolean remove(Card card) {
        return deck.remove(card);
    }

    /**
     * Remove the card at index cardNumber.
     *
     * @param cardNumber The index of the card.
     * @return True if index existed
     */
    //TODO: check if this version of this is still needed.
    public boolean remove(int cardNumber) {
        try {
            deck.remove(cardNumber);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Remove the first card of the deck and returns it.
     *
     * @return Returns the first card.
     *
     */
    public Card pop() {
        if (!deck.isEmpty()) {
            return deck.remove(0);
        }
        return null;
    }

    /**
     * Shuffles the deck.
     */
    public void shuffle() {
        Collections.shuffle(deck);
    }

    /**
     * Move the deck completely to another deck and empty it.
     *
     * @param deck The deck where you want to move the original deck to.
     * @return False if the given deck does not exists.
     */
    public boolean moveDeckTo(Deck deck) {
        if (deck != null) {
            deck.deck.addAll(this.deck);
            this.deck.clear();
            return true;
        }
        return false;
    }

    /**
     * Move a card from one deck to another.
     *
     * @param card The card to move.
     * @param deck The deck where to move the card to.
     * @return True i card exists in deck.
     */
    //TODO: Check if this version is needed.
    public boolean moveCardToDeck(Card card, Deck deck) {
        if (this.deck.contains(card)) {
            this.remove(card);
            deck.add(card);
            return true;
        }
        return false;
    }

    /**
     * Move a card from one deck to another.
     *
     * @param cardNumber The index of the card to move.
     * @param deck The deck where to move the card to.
     * @return True if card exists in deck.
     */
    //TODO: Check if this version is needed.
    public boolean moveCardToDeck(int cardNumber, Deck deck) {
        if (cardNumber < this.deck.size()) {
            deck.add(this.getCard(cardNumber));
            this.remove(cardNumber);
            return true;
        }
        return false;
    }

//    /**
//     * Checks if the deck contains cards of the type.
//     * @param cardType The card type you want to look for
//     * @return True if it does contain cards.
//     */
//    private boolean hasCardOfType(String cardType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//        for (Card c : deck) {
//            Method method = this.getClass().getDeclaredMethod("is"+cardType.replace(cardType.charAt(0), (char)(cardType.charAt(0)+26)));
//            method.invoke(c);
//            if (c.getType().equals(cardType)) {
//                return true;
//            }
//        }
//        return false;
//    }
    //TODO: make above function functional and clean up below!!!
    /**
     * Check if the deck contains any kingdom cards.
     *
     * @return Returns true if it contains kingdom cards.
     */
    public boolean hasKingdomCards() {
        for (Card c : deck) {
            if (c.getType().equals("Action") || c.getType().equals("Attack") || c.getType().equals("Reaction")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the deck contains cards that can be played.
     *
     * @return True if it does.
     */
    public boolean hasPlayableCards() {
        for (Card c : deck) {
            if (c.isPlayable()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasReactionCards() {
        for (Card c : deck) {
            if (c.isReaction()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVictoryCards() {
        for (Card c : deck) {
            if (c.isVictory()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTreasureCards() {
        for (Card c : deck) {
            if (c.isTreasure()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts all the victory points that are in the given deck. Takes in acount
     * the Garden card.
     *
     * @return The total amount of points in the deck.
     */
    //TODO: clean up and delegate some stuff to cards!
    public int countVictoryPoints() {
        int victoryPoints = 0;
        int gardenCard = 0;
        if (!deck.isEmpty()) {
            for (Card c : deck) {
                if (c.getName().equals("gardens")) {
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
     * Checks whether a normal deck is empty. For limited card decks us
     * isEmpty(Card card)
     *
     * @return True if deck is empty.
     */
    public boolean isEmpty() {
        return deck.isEmpty();
    }

    /**
     * Returns the size of a normal deck.
     *
     * @return The size of a normal deck.
     */
    public int size() {
        return deck.size();
    }

    //TODO: mae the cards comparable so we can sort them in the limited card decks( for kingdomCards deck for example)
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
            int i = 1;
            for (Card c : deck) {
                deckContent += String.format("\t%02d) %s", i, c.toString());
                i++;
            }
            return deckContent;
        }
    }

    //TODO: verify if we will need this after refactoring GameEngine
    public String[] toStringArray() {
        if (deck.isEmpty()) {
            return new String[]{};
        }
//        if (!isSingleCardDeck) {
        String[] deckArray = new String[deck.size()];
        for (Card c : deck) {
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

    @Override
    public Iterator<Card> iterator() {
        return deck.iterator();
    }

}
