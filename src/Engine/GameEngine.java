package Engine;

import java.util.*;
import Database_connection.*;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

public class GameEngine implements Serializable {

    static final long serialVersionUID = 1337;

    final static int MIN_NUMBER_PLAYERS = 2;
    final static int MAX_NUMBER_PLAYERS = 4;
    private final List<Player> players;
    private final Deck choosableKingdomCards;
    private final Map<String, Stack> gameTable;
    private int currentPlayer;
    private String[] expansions;
    private String phase;
    private int cardActionsToPlay;
    private int cardActionsPlayed;

    public GameEngine() {
        this.players = new ArrayList<>();
        this.gameTable = new HashMap<>();
        this.choosableKingdomCards = new Deck();
        this.phase = "init";
        this.currentPlayer = 0;
        this.cardActionsToPlay = 0;
        this.cardActionsPlayed = 0;
    }

    public int getMaxNumberOfPlayers() {
        return GameEngine.MAX_NUMBER_PLAYERS;
    }

    // adds a player to the game if the playername is not already existing and if there aren't already enough players in the game.
    public void addPlayer(String playerName) {
        if (this.getNumberOfPlayers() < GameEngine.MAX_NUMBER_PLAYERS) {
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

    public int getNumberOfChoosableKingdomCards() {
        return choosableKingdomCards.size();
    }

    // takes an array of ints containing the index of the chosen kingdomcards and adds them to the game.
    public boolean setPlayableKingdomCards(int[] chosenCardsIndex) {
        Stack kingdomStack = new Stack();
        for (int index : chosenCardsIndex) {
            Card card = this.choosableKingdomCards.getCard(index);
            if(card == null) {
                return false;
            }
            kingdomStack.add(card, 10);
        }
        gameTable.put("kingdom", kingdomStack);
        return true;
    }

    public void usePresetDeck(String deckName) {
        ArrayList<String> cardNames = new PremadeSetsConnection().getKingdomSet(deckName);
        if (cardNames.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Stack kingdomStack = new Stack();
        for (String cardName : cardNames) {
            Card card = new Card(cardName);
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
            moneyStack.add(card, 30);
        }

        String[] victoryTypes = new String[]{"estate", "duchy", "province"};
        Stack victoryStack = new Stack();
        for (int i = 0; i < victoryTypes.length; i++) {
            Card card = new Card(victoryTypes[i]);
            victoryStack.add(card, victoryCardNumber);
        }

        Stack curseStack = new Stack();
        curseStack.add(new Card("curse"), curseCardNumber);

        gameTable.put("victory", victoryStack);
        gameTable.put("treasure", moneyStack);
        gameTable.put("curse", curseStack);

        for (Player p : players) {
            Deck playerDeck = p.getDeck("deck");
            Card card = gameTable.get("treasure").getCards()[0];
            for (int i = 0; i < 7; i++) {
                playerDeck.add(card);
            }
            card = gameTable.get("victory").getCards()[0];
            for (int i = 0; i < 3; i++) {
                playerDeck.add(card);
            }
            playerDeck.shuffle();
            drawCardsFromPlayerDeck(p, 5);
        }
        
        this.phase = "action";
        checkPhaseChange();
    }

    //INFO: here starts gameplay:
    //Card Actions:
    /**
     * Play a given card and execute all the actions of the card.
     * Does not actions if card is not playable
     *
     * @param card The card to play.
     * @return False if card requires user interaction.
     * Otherwise always returns true even if card is not playable.
     */
    public boolean playCard(Card card) {
        if (card.isPlayable()) {
            Player player = getCurrentPlayer();
            if (card.isTreasure()) {
                moveCardFromHandToDeck(card, "table");
                player.addCoins(card.getAddCoins());
            }
            if ((player.getActions() > 0) && card.isKingdom()) {
                moveCardFromHandToDeck(card, "table");
                player.addActions(card.getAddActions() - 1);
                player.addBuys(card.getAddBuys());
                player.addCoins(card.getAddCoins());
                drawCardsFromPlayerDeck(player, card.getDraws());
                this.cardActionsToPlay = card.getNumberOfActions();
                if(cardActionsToPlay > 0) {
                    return playActions(card);
                }
            }
        }
        return true;
    }

    /**
     * Play the actions of a card.
     * 
     * @param boolean True if all actions of the card are played.
     */
    private boolean playActions(Card card) {
        Object[] action = card.getActions(cardActionsPlayed);
        String method = (String) action[0];
        Class[] paramClass = null;
        Object[] params = null;
        
        
        if(action.length > 1) {
            paramClass = new Class[action.length - 1];
            params = new Object[paramClass.length];
            for(int i = 1; i < action.length; i++) {
                paramClass[i - 1] = action[i].getClass();
                params[i-1] = action[i];
            }
        }
        try {
            Method m = getClass().getDeclaredMethod(method, paramClass);
            m.invoke(this,params);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /*
    - tell engine to set request to throw away a card and make engine repeat that x times the player has cards in his hand.
    - put all the chosen cards in a temp deck and remove from hand so player can see update hand to choose next.
    - if it happend x times or player wanted to stop in between, trash all the cards from the temp deck and add x times cards as the player trashed.
    */
    /** Implement this cellar card function
        int discardCards = 0;
        userInput = getUserInput("Enter a cardnumber you want to discard or 0 to continue: ", 0, player.getDeck("hand").size());
        while (userInput != 0) {
            discardCards++;
            gameEngine.discardFromHand(player, userInput - 1);
            showPlayerStatus(player);
            userInput = getUserInput("Enter a cardnumber you want to discard or 0 to continue: ", 0, player.getDeck("hand").size());
        }
        gameEngine.drawCardsFromPlayerDeck(player, discardCards);
    **/

    /**
     * Buy a card from a stack on the game table.
     *
     * @param card The card to buy
     * @param stackName The name of the stack to buy from.
     * @return True if player has buys, enough coins and the stack wasn't empty
     */
    public boolean buyCard(Card card, String stackName) {
        Player player = getCurrentPlayer();
        if (player.getBuys() != 0 && (card.getCost() <= player.getCoins())) {
            Stack stack = getStack(stackName);
            if (drawCardFromTable(stack, card, player, "discard")) {
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
     * Draw a number of cards from the player's deck to the player's hand.
     * 
     * @param player The player that wants to draw cards.
     * @param number The number of cards he wants to draw.
     */
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
    
    /**
     * Put the discard deck and deck deck together into deck deck and shuffle.
     * 
     * @param player 
     */
    public void discardDeck(Player player) {
        player.getDeck("discard").moveDeckTo(player.getDeck("deck"));
        player.getDeck("deck").shuffle();
    }
    
    /**
     * Move card from hand to discard of the current player.
     *
     * @param card The card to discard.
     */
    private boolean moveCardFromHandToDeck(Card card, String deckName) {
        Player player = getCurrentPlayer();
        return player.getDeck("hand").moveCardToDeck(card, player.getDeck(deckName));
    }

    /**
     * Draw a card from the stack on the game table and put in in the players
     * deck.
     *
     * @param stack Stack to draw from.
     * @param card Card to draw.
     * @param player Player who draws the card.
     * @param toDeck Deck in which to put the card.
     * @return True if card is in stack.
     */
    public boolean drawCardFromTable(Stack stack, Card card, Player player, String toDeck) {
        if (stack.remove(card)) {
            player.getDeck(toDeck).add(card);
            return true;
        }
        return false;
    }

    //Engine actions:

    /**
     * Return the current playing player.
     * 
     * @return The current player.
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }
    
    /**
     * Get the stack of the name stack.
     * 
     * @param stack The name of the stack.
     * @return The stack of the name stack.
     */
    public Stack getStack(String stack) {
        return gameTable.get(stack.toLowerCase());
    }
    
    /**
     * If player has no actions or has no playable cards in hand then change.
     * 
     * phase to "Buy".
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
        if (phase.equals("action")) {
            phase = "buy";
        } else {
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
     *
     * @return True if the province stack or 3 stacks in total are empty.
     */
    public boolean checkGameEnd() {
        int emptyStacks = 0;
        for (Map.Entry<String, Stack> entry : gameTable.entrySet()) {
            if (!entry.getKey().equals("curse") && !entry.getKey().equals("treasure")) {
                Stack s = entry.getValue();
                for (Card c : s.getCards()) {
                    if (c.getName().equals("province") && s.isEmpty(c)) {
                        return true;
                    }
                    if (s.isEmpty(c)) {
                        emptyStacks++;
                    }
                    if (emptyStacks >= 3) {
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
    
    public void setRequest(String message) {
        System.err.println(message);
    }

    //TODO: update these to be compliant to reworked functions.
    //Original functions before refactoring:

    public String getPhase() {
        return this.phase;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    // Todo do all actions
    public void discardFromHand(Player player, int cardNumber) {
        player.getDeck("hand").moveCardToDeck(cardNumber, player.getDeck("discard"));
    }

    public void trashFromHand(Player player, int cardNumber) {
        player.getDeck("hand").moveCardToDeck(cardNumber, player.getDeck("trash"));
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
            drawCardFromTable(stack, card, getCurrentPlayer(), "discard");
        }
    }

    public void trashPlayedCard() {
        Player player = getCurrentPlayer();
        player.getDeck("table").moveCardToDeck((player.getDeck("table").size()- 1), player.getDeck("trash"));
    }

    public String getPlayerStatus(Player p) {
        String playerStatus = String.format("Player Overview of %s:\n", p.getName());
        playerStatus += String.format("Game phase: %s\n", this.phase);
        playerStatus += String.format("Coins:  %2d\t", p.getCoins());
        playerStatus += String.format("Buys: %2d\t", p.getBuys());
        playerStatus += String.format("Actions: %2d\n", p.getActions());
        playerStatus += String.format("Cards on table:\n%s\n", p.getDeck("table").toString());
        playerStatus += String.format("Cards in hand:\n%s", p.getDeck("hand").toString());
        return playerStatus;
    }

    public String getTableStatus() {
        String tableStatus = String.format("Overview of table:\n");
        tableStatus += String.format("Treasure Cards:\n%s\n", getStack("treasure"));
        tableStatus += String.format("Victory Cards:\n%s\n", getStack("victory"));
        tableStatus += String.format("Curse Cards:\n%s\n", getStack("curse"));
        tableStatus += String.format("KingdomCards:\n%s", getStack("kingdom"));
        return tableStatus;
    }
}