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
    private Map<String, Stack> gameTable;
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
        Stack kingdomStack = new Stack();
        for (int index : chosenCardsIndex) {
            Card card = this.choosableKingdomCards.getCard(index);
            usedCards.put(card.getName(), card);
            kingdomStack.add(card, 10);
        }
        gameTable.put("kingdom", kingdomStack);
    }

    public void usePresetDeck(String deckName) {
        ArrayList<String> cardNames = new PremadeSetsConnection().getKingdomSet(deckName);
        if (cardNames.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Stack kingdomStack = new Stack();
        for (String cardName : cardNames) {
            Card card = new Card(cardName);
            usedCards.put(card.getName(), card);
            kingdomStack.add(card, 10);
        }
        gameTable.put("kingdom", kingdomStack);
    }

    // should only be run after the above functions have been executed and will set up the rest of the game and start.
    public void startGame() {
        if (this.getNumberOfPlayers() < 2) {
            throw new IllegalStateException("Not enough players!");
        }
        if (this.expansions == null) {
            throw new IllegalStateException("Please set expanions!");
        }
        if (this.getStack("kingdom") == null) {
            throw new IllegalStateException("Set kingdomCards!");
        }

        int victoryCardNumber;
        int curseCardNumber;
        switch (this.getNumberOfPlayers()) {
            case 2:
                victoryCardNumber = 8;
                curseCardNumber = 10;
                break;
            case 3:
                victoryCardNumber = 12;
                curseCardNumber = 20;
                break;
            default:
                victoryCardNumber = 12;
                curseCardNumber = 30;
                break;
        }

        String[] moneyTypes = new String[]{"copper", "silver", "gold"};
        Stack moneyStack = new Stack();
        for (int i = 0; i < moneyTypes.length; i++) {
            Card card = new Card(moneyTypes[i]);
            usedCards.put(card.getName(), card);
            moneyStack.add(card, 30);
        }

        String[] victoryTypes = new String[]{"estate", "duchy", "province"};
        Stack victoryStack = new Stack();
        for (int i = 0; i < victoryTypes.length; i++) {
            Card card = new Card(victoryTypes[i]);
            usedCards.put(card.getName(), card);
            victoryStack.add(card, victoryCardNumber);
        }

        Stack curseStack = new Stack();
        usedCards.put("curse", new Card("curse"));
        curseStack.add(usedCards.get("curse"), curseCardNumber);

        gameTable.put("victory", victoryStack);
        gameTable.put("treasure", moneyStack);
        gameTable.put("curse", curseStack);

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
        this.phase = "action";
        checkPhaseChange();
    }

    //INFO: here starts gameplay:
    //TODO:: look for a way to edit the currentplayer variable to a player instead of a int (maybe iterator over the list?)
    
    //Card Actions:
    
    /**
     * Play a given card and execute all the actions of the card.
     * @param card The card to play.
     * @return True if card is played.
     */
    public boolean playCard(Card card) {
        Player player = getCurrentPlayer();
        if (card.isPlayable()) {
            boolean played = false;
            if((player.getActions() > 0) && card.isKingdom()) {
                playAction(card);
                played = true;
            }
            if(card.isTreasure()) {
                playTreasure(card);
                played = true;
            }
            if(played) {
                moveCardFromHandToDiscard(card);
                checkPhaseChange();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Play the card as an action card.
     * @param card The action card to play.
     */
    private void playAction(Card card) {
        Player player = getCurrentPlayer();
        player.addActions(card.getActions() - 1);
        player.addBuys(card.getBuys());
        player.addCoins(card.getCoins());
        drawCardsFromPlayerDeck(player, card.getDraws());
    }
    
    /**
     * Play a card as a treasure card.
     * @param card The treasure card to play.
     */
    private void playTreasure(Card card) {
        Player player = getCurrentPlayer();
        player.addCoins(card.getCoins());
    }
          
    /**
     * Buy a card from a stack on the game table.
     * @param card The card to buy
     * @param stackName The name of the stack to buy from.
     * @return True if player has buys, enough coins and the stack wasn't empty
     */
    public boolean buyCard(Card card, String stackName) {
        Player player = getCurrentPlayer();
        if (player.getBuys() != 0 && (card.getCost() <= player.getCoins())) {
            Stack stack = getStack(stackName);
            if (drawCardFromTable(stack, card, player)) {
                player.addCoins(-card.getCost());
                player.addBuys(-1);
                this.phase = "buy";
                return true;
            }
        }
        return false;
    }
    
    //Deck actions
    
    /**
     * Move card from hand to discard of the current player.
     * @param card The card to discard.
     */
    private boolean moveCardFromHandToDiscard(Card card) {
        Player player = getCurrentPlayer();
        return player.getDeck("hand").moveCardToDeck(card, player.getDeck("table"));
    }
    
    /**
     * Draw a card from the stack on the game table and put in in the players deck.
     * @param stack Stack to draw from.
     * @param card  Card to draw.
     * @param player Player who draws the card.
     * @param toDeck Deck in which to put the card.
     * @return True if card is in stack.
     */
    private boolean drawCardFromTable(Stack stack, Card card, Player player, String toDeck) {
        if (stack.remove(card)) {
            player.getDeck(toDeck).add(card);
            return true;
        }
        return false;
    }
    
    /**
     * Draw a card from the stack on the game table and put in in the players discard deck.
     * @param stack Stack to draw from.
     * @param card  Card to draw.
     * @param player Player who draws the card
     * @return True if card is in stack.
     */
    private boolean drawCardFromTable(Stack stack, Card card, Player player) {
        return drawCardFromTable(stack, card, player, "discard");
    }
    //TODO: remove this it's only temporary
    public boolean drawCardFromTable(String stack, Card card, Player player, String toDeck) {
        return drawCardFromTable(getStack(stack), card, player, toDeck);
    }   
    
    
    //Engine actions:
    
    /**
     * If player has no actions or has no playable cards in hand then change phase to "Buy".
     */
    private void checkPhaseChange() {
        Player player = getCurrentPlayer();
        if (player.getActions() == 0 || !player.getDeck("hand").hasKingdomCards()) {
            this.phase = "buy";
        }
    }
    
    /**
     * Change between phase or end turn.
     */
    public void endPhase() {
        if(phase.equals("action")) {
            phase = "buy";
        }
        else {
            endTurn();
        }
    }
    
    /**
     * End turn.
     */
    //TODO: recheck this for rework or clean up.
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

            this.phase = "action";
        }
    }
    
    /**
     * Checks whether the goals to end the game has been reached.
     * @return True if the province stack or 3 stacks in total are empty.
     */
    public boolean checkGameEnd() {
        int emptyStacks = 0;
        for(Map.Entry<String,Stack> entry:gameTable.entrySet()) {
            if(!entry.getKey().equals("curse") && !entry.getKey().equals("treasure")) {
                Stack s = entry.getValue();
                for(Card c:s.getCards()) {
                    if(c.getName().equals("province") && s.isEmpty(c)) {
                        return true;
                    }
                    if(s.isEmpty(c)) {
                        emptyStacks++;
                    }
                    if(emptyStacks >=3) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Ends the game and counts the score for all player.
     */
    public void endGame() {
        this.phase = "end";
        for (Player p : players) {
            p.endPlayerTurn();
            p.getDeck("discard").moveDeckTo(p.getDeck("deck"));
            p.setScore(p.getDeck("deck").countVictoryPoints());
        }
    }
       
       
       
    //TODO: update these to be compliant to reworked functions.
    //Original functions before refactoring:
    public Stack getStack(String stack) {
        return gameTable.get(stack.toLowerCase());
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    public String getPhase() {
        return this.phase;
    }

    public void setPhase(String whatPhase) {
        this.phase = whatPhase.toLowerCase();
    }

    public Map<String, Stack> getGameTable() {
        return this.gameTable;
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
        this.getCurrentPlayer().getDeck("deck").add(usedCards.get("gardens"));
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
    
    public void getCardOfValue(String stackName, Card card, int maxValue, String toDeck) {
        Stack stack = getStack(stackName);
        if (card.getCost() <= maxValue) {
            drawCardFromTable(stack, card, getCurrentPlayer(), toDeck);
        }
    }

    public void getCardOfValue(String stackName, Card card, int maxValue) {
        Stack stack = getStack(stackName);
        if (card.getCost() <= maxValue) {
            drawCardFromTable(stack, card, getCurrentPlayer());
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
        return player.getDeck("hand").hasReactionCards();
    }

    public String getPlayerStatus(Player p) {
        String playerStatus = String.format("Player Overview of %s:\n", p.getName());
        playerStatus += String.format("Game phase: %s\n", this.phase);
        playerStatus += String.format("Coins:  %2d\t", p.getCoins());
        playerStatus += String.format("Buys: %2d\t", p.getBuys());
        playerStatus += String.format("Actions: %2d\n", p.getActions());
        playerStatus += String.format("Cards on table:\n%s", p.getDeck("table").toString());
        playerStatus += String.format("Cards in hand:\n%s", p.getDeck("hand").toString());
        return playerStatus;
    }
    
    public String getTableStatus() {
        String tableStatus = String.format("Overview of table:\n");
        tableStatus += String.format("Treasure Cards:\n%s", getStack("treasure"));
        tableStatus += String.format("Victory Cards:\n%s", getStack("victory"));
        tableStatus += String.format("Curse Cards:\n%s", getStack("curse"));
        tableStatus += String.format("KingdomCards:\n%s", getStack("kingdom"));
        return tableStatus;
    }

    // temporary test functions to test :P
    public void setDeckCount(int count) {
        gameTable.get("victory").setCount(new Card("province"), 0);
    }

}
