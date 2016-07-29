package Engine;

import java.util.*;
import Card.Card;
import Database_connection.*;

import java.io.Serializable;

public class GameEngine implements Serializable {

    static final long serialVersionUID = 1337;

    final int MIN_NUMBER_PLAYERS = 2;
    final int MAX_NUMBER_PLAYERS = 4;
    private List<Player> players;
    private int currentPlayer;
    private String[] expansions;
    private Deck choosableKingdomCards;
    private Map<String, Card> usedCards;
    private Map<String, Deck[]> gameTable;
    private String phase;

    public GameEngine() {
        this.players = new ArrayList<>();
        this.usedCards = new HashMap<>();
        this.gameTable = new HashMap<>();
        this.choosableKingdomCards = new Deck();
    }

    public int getMaxNumberOfPlayers() {
        return this.MAX_NUMBER_PLAYERS;
    }

    // adds a player to the game if the playername is not already existing and if there aren't already enough players in the game.
    public void addPlayer(String playerName) {
        if (this.getNumberOfPlayers() < this.MAX_NUMBER_PLAYERS) {
            if (!players.contains(new Player(playerName))) {
                players.add(new Player(playerName));
            } else {
                throw new IllegalArgumentException("Player already exists. Enter another name");
            }
        } else {
            throw new IllegalStateException("There are already " + this.getNumberOfPlayers() + " players in the game.");
        }
    }

    public int getNumberOfPlayers() {
        return this.players.size();
    }

    // set which expansions you wish to use, takes a array of strings
    public void setExpansions(String[] expansionNames) {
        this.expansions = expansionNames;
    }

    // set which expansion you wish to use, takes just a string
    public void setExpansions(String expansionName) {
        this.setExpansions(new String[]{expansionName});
    }

    // returns an string containing all cards of the given expansions.
    public String getChoosableKingdomCards() {
        ArrayList<String> tmp = new cardConnection().getKingdomCards(this.expansions);
        for (String cardName : tmp) {
            this.choosableKingdomCards.add(new Card(cardName));
        }
        return this.choosableKingdomCards.toString();
    }

    public String[] getChoosableKingdomCardsArray() {
        ArrayList<String> tmp = new cardConnection().getKingdomCards(this.expansions);
        for (String cardName : tmp) {
            this.choosableKingdomCards.add(new Card(cardName));
        }
        return this.choosableKingdomCards.toStringArray();
    }

    public String[][] getChoosablePremadeSets() {
        ArrayList<String> tmp = new PremadeSetsConnection().getAllSetNames();
        String[][] choosableSets = new String[tmp.size()][11];
        for (int i = 0; i < tmp.size(); i++) {
            choosableSets[i][0] = tmp.get(i);
            ArrayList<String> cardNames = new PremadeSetsConnection().getKingdomSet(tmp.get(i));
            for (int j = 0; j < cardNames.size(); j++) {
                choosableSets[i][j + 1] = cardNames.get(j);
            }

        }
        return choosableSets;
    }

    public int getNumberOfChoosableKingdomCards() {
        return choosableKingdomCards.size();
    }

    // takes an array of ints containing the index of the chosen kingdomcards and adds them to the game.
    public void setPlayableKingdomCards(int[] chosenCardsIndex) {
        Deck[] kingdomDecks = new Deck[chosenCardsIndex.length];
        int i = 0;
        for (int index : chosenCardsIndex) {
            Card card = this.choosableKingdomCards.getCard(index);
            usedCards.put(card.getName(), card);
            kingdomDecks[i] = new Deck(card, 10);
            i++;
        }
        Arrays.sort(kingdomDecks);
        gameTable.put("kingdom", kingdomDecks);
    }

    public void usePresetDeck(String deckName) {
        ArrayList<String> cardNames = new PremadeSetsConnection().getKingdomSet(deckName);
        if (cardNames.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Deck[] kingdomDecks = new Deck[cardNames.size()];
        int i = 0;
        for (String cardName : cardNames) {
            Card card = new Card(cardName);
            usedCards.put(card.getName(), card);
            kingdomDecks[i] = new Deck(card, 10);
            i++;
        }
        Arrays.sort(kingdomDecks);
        gameTable.put("kingdom", kingdomDecks);
    }

    // should only be run after the above functions have been executed and will set up the rest of the game and start.
    public void startGame() {
        if (this.getNumberOfPlayers() < 2) {
            throw new IllegalStateException("Not enough players!");
        }
        if (this.expansions == null) {
            throw new IllegalStateException("Please set expanions!");
        }
        if (this.getDeck("kingdom") == null) {
            throw new IllegalStateException("Set kingdomCards!");
        }

        int victoryCardNumber;
        int curseCardNumber;
        if (this.getNumberOfPlayers() == 2) {
            victoryCardNumber = 8;
            curseCardNumber = 10;
        } else if (this.getNumberOfPlayers() == 3) {
            victoryCardNumber = 12;
            curseCardNumber = 20;
        } else {
            victoryCardNumber = 12;
            curseCardNumber = 30;
        }

        String[] moneyTypes = new String[]{"copper", "silver", "gold"};
        Deck[] moneyDecks = new Deck[moneyTypes.length];
        for (int i = 0; i < moneyTypes.length; i++) {
            usedCards.put(moneyTypes[i], new Card(moneyTypes[i]));
            moneyDecks[i] = new Deck(usedCards.get(moneyTypes[i]), 30);
        }

        String[] victoryTypes = new String[]{"estate", "duchy", "province"};
        Deck[] victoryDecks = new Deck[victoryTypes.length];
        for (int i = 0; i < victoryTypes.length; i++) {
            usedCards.put(victoryTypes[i], new Card(victoryTypes[i]));
            victoryDecks[i] = new Deck(usedCards.get(victoryTypes[i]), victoryCardNumber);
        }

        Deck[] curseDeck = new Deck[1];
        usedCards.put("curse", new Card("curse"));
        curseDeck[0] = new Deck(usedCards.get("curse"), curseCardNumber);

        gameTable.put("victory", victoryDecks);
        gameTable.put("treasure", moneyDecks);
        gameTable.put("curse", curseDeck);

        for (Player p : players) {
            Deck playerDeck = p.getDeck("deck");
            for (int i = 0; i < 7; i++) {
                playerDeck.add(usedCards.get("copper"));
            }
            for (int i = 0; i < 3; i++) {
                playerDeck.add(usedCards.get("estate"));
            }
            playerDeck.shuffle();
            drawCardsFromPlayerDeck(p, 5);
        }

        currentPlayer = 0;
        this.phase = "Action";
    }

    //INFO here starts gameplay:
    
    //TODO clean up function there is repetition.
    //TODO update to make it shift the actual playing to a function specific to the type of card( treasure, kingdom, victory)
    //TODO add functionality to 
    public boolean playCard(Card card) {
        Player player = getCurrentPlayer();
        //Card card = player.getDeck("hand").getCard(cardNumber);
        if (card.getPlayableTurn() != "never" && (!this.phase.equals("Buy"))) {
            if (player.getActions() != 0 && card.isAction()) {
                player.addActions(card.getActions() - 1);
                player.addBuys(card.getBuys());
                player.addCoins(card.getCoins());
                drawCardsFromPlayerDeck(player, card.getDraws());
                player.getDeck("hand").moveCardToDeck(card, player.getDeck("table"));
                return true;
            }
            if (card.isTreasure()) {
                player.addCoins(card.getCoins());
                player.getDeck("hand").moveCardToDeck(card, player.getDeck("table"));
                return true;
            }
        }
        return false;
    }

    //Original functions before refactoring:
    public Deck[] getDeck(String deckName) {
        return gameTable.get(deckName);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    public String getPhase() {
        return this.phase;
    }

    public void setPhase(String whatPhase) {
        this.phase = whatPhase;
    }

    public Map<String, Deck[]> getGameTable() {
        return this.gameTable;
    }

    private boolean emptyStackCheck(Deck d) {
        if (d.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public void drawCardFromTable(Deck deck, Card card, Player player, String toDeck) {
        if (deck.size() != 0) {
            deck.remove(0);
            player.getDeck(toDeck).add(card);
        }
    }

    public void drawCardFromTable(Deck deck, Card card, Player player) {
        drawCardFromTable(deck, card, player, "discard");
    }

    public void drawCardsFromPlayerDeck(Player player, int number) {
        for (int i = 0; i < number; i++) {
            if (!player.getDeck("deck").isEmpty()) {
                player.getDeck("hand").add(player.getDeck("deck").pop());
            } else {
                discardDeck(player);
                i--;
            }
        }
    }

    public void discardDeck(Player player) {
        player.getDeck("discard").moveDeckTo(player.getDeck("deck"));
        player.getDeck("deck").shuffle();
    }

    public boolean checkGameEnd() {
        if (emptyStackCheck(gameTable.get("victory")[2])) {
            return true;
        }

        int emptyStacks = 0;

        for (Deck d : gameTable.get("kingdom")) {
            if (emptyStackCheck(d)) {
                emptyStacks++;
            }
        }
        if (emptyStacks < 3) {
            for (Deck d : gameTable.get("victory")) {
                if (emptyStackCheck(d)) {
                    emptyStacks++;
                }
            }
        }
        if (emptyStacks < 3) {
            for (Deck d : gameTable.get("treasure")) {
                if (emptyStackCheck(d)) {
                    emptyStacks++;
                }
            }
        }

        return emptyStacks >= 3;

    }

//	public boolean playCard(int cardNumber, boolean isAction) {
//		Player player = getCurrentPlayer();
//		Card card = player.getDeck("hand").getCard(cardNumber);
//		if (card.getPlayableTurn() != "never" && (!this.phase.equals("Buy"))) {
//			if (card.getType().equals("Treasure"))
//			{
//			player.getDeck("hand").moveCardToDeck(cardNumber, player.getDeck("table"));
//			getCurrentPlayer().addCoins(card.getCoins());
//			}
//			if ((player.getActions() != 0 && card.isAction()) || isAction) {
//				if(!isAction) {
//					player.getDeck("hand").moveCardToDeck(cardNumber, player.getDeck("table"));
//					player.addActions(-1);
//				}
//				getCurrentPlayer().addCoins(card.getCoins());
//				player.addActions(card.getActions());
//				player.addBuys(card.getBuys());
//				drawCardsFromPlayerDeck(player, card.getDraws());
//				return true; 
//			}
//		}	
//		return false;
//	}
//	
//	public boolean playCard(int cardNumber) {
//		return playCard(cardNumber, false);
//	}
    public void buyCard(String deckList, int cardNumber, boolean isAction) {
        Player player = getCurrentPlayer();
        Deck deck = gameTable.get(deckList)[cardNumber];
        Card card = deck.getCard(0);
        if (player.getBuys() != 0) {
            if (card.getCost() <= player.getCoins()) {
                drawCardFromTable(deck, card, getCurrentPlayer());
                player.addCoins(-card.getCost());
                player.addBuys(-1);
                if (!isAction) {
                    this.phase = "Buy";
                }
            }
        }
    }

    public void buyCard(String deckList, int cardNumber) {
        buyCard(deckList, cardNumber, false);
    }

    public void checkPhaseChange() {
        Player player = getCurrentPlayer();
        if ((player.getActions() == 0 || !player.getDeck("hand").hasKingdomCards()) && !player.getDeck("hand").hasCardOfType("Treasure")) {
            this.phase = "Buy";
        }
    }

    public void endTurn() {
        if (checkGameEnd()) {
            endGame();
        } else {
            getCurrentPlayer().endPlayerTurn();
            drawCardsFromPlayerDeck(getCurrentPlayer(), 5);

            if ((currentPlayer + 1) < this.getNumberOfPlayers()) {
                currentPlayer += 1;
            } else {
                currentPlayer = 0;
            }

            this.phase = "Action";
            /*if (!players[currentPlayer].getDeck("Hand").hasKingdomCards() || players[currentPlayer].getActions() == 0) {    // this code looks unnecessary
				this.phase = "Buy";
			}*/
        }
    }

    public void endGame() {
        this.phase = "end";
        for (Player p : players) {
            p.endPlayerTurn();
            p.getDeck("discard").moveDeckTo(p.getDeck("deck"));
            p.setScore(p.getDeck("deck").countVictoryPoints());
        }
    }

    public String getPlayerStatus(Player p) {
        String playerStatus = String.format("Player Overview of %s:\n", p.getName());
        playerStatus += String.format("Game phase: %s\n", this.phase);
        playerStatus += String.format("Coins:  %2d\t", p.getCoins());
        playerStatus += String.format("Buys: %2d\t", p.getBuys());
        playerStatus += String.format("Actions: %2d\n", p.getActions());
        playerStatus += String.format("Cards on table:\n %s", p.getDeck("table").toString());
        playerStatus += String.format("Cards in hand:\n %s", p.getDeck("hand").toString());
        return playerStatus;
    }

    public String getTableStatus() {
        String tableStatus = String.format("Overview of table:\n");
        tableStatus += String.format("Treasure Cards:\n\t01)%s\t02)%s\t03)%s", getDeck("treasure")[0], getDeck("treasure")[1], getDeck("treasure")[2]);
        tableStatus += String.format("Victory Cards:\n\t01)%s\t02)%s\t03)%s", getDeck("victory")[0], getDeck("victory")[1], getDeck("victory")[2]);
        tableStatus += String.format("Curse Cards:\n\t01)%s", getDeck("curse")[0]);
        tableStatus += String.format("KingdomCards:\n\t01)%s\t02)%s\t03)%s\t04)%s\t05)%s\t06)%s\t07)%s\t08)%s\t09)%s\t10)%s", getDeck("kingdom")[0], getDeck("kingdom")[1], getDeck("kingdom")[2], getDeck("kingdom")[3], getDeck("kingdom")[4], getDeck("kingdom")[5], getDeck("kingdom")[6], getDeck("kingdom")[7], getDeck("kingdom")[8], getDeck("kingdom")[9]);
        return tableStatus;
    }

    // temporary test functions to test :P
    public void setDeckCount(int count) {
        gameTable.get("victory")[2].setCount(count);
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public void addVictoryCardsForTest() {
        this.getCurrentPlayer().getDeck("discard").add(usedCards.get("estate"));
        this.getCurrentPlayer().getDeck("hand").add(usedCards.get("province"));
        this.getCurrentPlayer().getDeck("deck").add(usedCards.get("duchy"));
    }

    public void addVictoryAndGardenCardsForTest() {
        this.getCurrentPlayer().getDeck("discard").add(usedCards.get("estate"));
        this.getCurrentPlayer().getDeck("hand").add(usedCards.get("province"));
        this.getCurrentPlayer().getDeck("deck").add(usedCards.get("Gardens"));
    }

    // Todo do all actions
    public void discardFromHand(Player player, int cardNumber) {
        player.getDeck("hand").moveCardToDeck(cardNumber, player.getDeck("discard"));
    }

    public void trashFromHand(Player player, int cardNumber) {
        player.getDeck("hand").moveCardToDeck(cardNumber, player.getDeck("trash"));
    }

    public boolean checkSpecificCard(String specification, String expectedValue) {
        if (specification.equals(expectedValue)) {
            return true;
        } else {
            return false;
        }
    }

    public void getCardOfValue(String deckList, int cardNumber, int maxValue) {
        Deck deck = gameTable.get(deckList)[cardNumber];
        Card card = deck.getCard(0);
        if (card.getCost() <= maxValue) {
            drawCardFromTable(deck, card, getCurrentPlayer());
        }
    }

    public void trashPlayedCard() {
        Player player = getCurrentPlayer();
        player.getDeck("table").moveCardToDeck((player.getDeck("table").size() - 1), player.getDeck("trash"));
    }

    public Card getSelectedCard(int cardNumber) {
        return getCurrentPlayer().getDeck("hand").getCard(cardNumber);
    }

    @SuppressWarnings("unused")
    private boolean checkReactionCard(Player player) {
        return player.getDeck("hand").hasCardOfType("Reaction");
    }
}
