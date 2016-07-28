package Engine;

import java.util.*;
import Card.Card;
import Database_connection.*;

import java.io.Serializable;

public class GameEngine implements Serializable{

	static final long serialVersionUID = 1337;
        
	final int MIN_NUMBER_PLAYERS = 2;
	final int MAX_NUMBER_PLAYERS = 4;
	private List<Player> playerstest;
	private int currentPlayer;
	private String[] expansions;
	private Deck choosableKingdomCards;
	private Map<String, Card> usedCards;
	private Map<String, Deck[]> gameTable;
	private String phase;	
	
	
	public GameEngine() {
		this.playerstest = new ArrayList<>();
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
			if (!playerstest.contains(new Player(playerName))) {
				playerstest.add(new Player(playerName));
			}
			else throw new IllegalArgumentException("Player already exists. Enter another name");
		}
		else throw new IllegalStateException("There are already " + this.getNumberOfPlayers() + " players in the game.");
	}
	
	public int getNumberOfPlayers() {
		return this.playerstest.size();
	}
	
	// set which expansions you wish to use, takes a array of strings
	public void setExpansions(String[] expansionNames) {
		this.expansions = expansionNames;
	}
	
	// set which expansion you wish to use, takes just a string
	public void setExpansions(String expansionName) {
		this.setExpansions(new String[] {expansionName});
	}
	
	// returns an string containing all cards of the given expansions.
	public String getChoosableKingdomCards() {
		ArrayList<String> tmp = new cardConnection().getKingdomCards(this.expansions);
		for(String cardName:tmp) {
			this.choosableKingdomCards.add(new Card(cardName));
		}
		return this.choosableKingdomCards.toString();
	}
	
	public String[] getChoosableKingdomCardsArray() {
		ArrayList<String> tmp = new cardConnection().getKingdomCards(this.expansions);
		for(String cardName:tmp) {
			this.choosableKingdomCards.add(new Card(cardName));
		}
		return this.choosableKingdomCards.toStringArray();
	}
	
	public String[][] getChoosablePremadeSets() {
		ArrayList<String> tmp = new PremadeSetsConnection().getAllSetNames();
		String[][] choosableSets = new String[tmp.size()][11];
		for(int i = 0; i < tmp.size(); i++) {
			choosableSets[i][0] = tmp.get(i);
			ArrayList<String> cardNames = new PremadeSetsConnection().getKingdomSet(tmp.get(i));
			for(int j = 0; j < cardNames.size(); j++) {
				choosableSets[i][j+1] = cardNames.get(j);
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
		for(int index: chosenCardsIndex) {
			Card card = this.choosableKingdomCards.getCard(index);
			usedCards.put(card.getName(), card);
			kingdomDecks[i] = new Deck(card, 10);
			i++;
		}
		Arrays.sort(kingdomDecks);
		gameTable.put("Kingdom", kingdomDecks);
	}
	
	public void usePresetDeck(String deckName){
		ArrayList<String> cardNames = new PremadeSetsConnection().getKingdomSet(deckName);
		if(cardNames.isEmpty()){
			throw new IllegalArgumentException();
		}
		Deck[] kingdomDecks = new Deck[cardNames.size()];
		int i = 0;
		for(String cardName:cardNames) {
			Card card = new Card(cardName);
			usedCards.put(card.getName(), card);
			kingdomDecks[i] = new Deck(card, 10);
			i++;
		}
		Arrays.sort(kingdomDecks);
		gameTable.put("Kingdom", kingdomDecks);
	}
	
	// should only be run after the above functions have been executed and will set up the rest of the game and start.
	public void startGame() {
		if (this.getNumberOfPlayers() < 2) {
			throw new IllegalStateException("Not enough players!");
		}
		if (this.expansions == null){
			throw new IllegalStateException("Please set expanions!");
		}
		if (this.getDeck("Kingdom") == null) {
			throw new IllegalStateException("Set kingdomCards!");
		}
		
		int victoryCardNumber;
		int curseCardNumber;
		if(this.getNumberOfPlayers() == 2){
			victoryCardNumber = 8;
			curseCardNumber = 10;
		}
		else if(this.getNumberOfPlayers() == 3){
			victoryCardNumber = 12;
			curseCardNumber = 20;
		}
		else {
			victoryCardNumber = 12;
			curseCardNumber = 30;
		}

		String[] moneyTypes = new String[] {"copper", "silver", "gold"};
		Deck[] moneyDecks = new Deck[moneyTypes.length];
		for(int i = 0; i < moneyTypes.length; i++) {
			usedCards.put(moneyTypes[i], new Card(moneyTypes[i]));
			moneyDecks[i] = new Deck(usedCards.get(moneyTypes[i]), 30);
		}
		
		String[] victoryTypes = new String[] {"estate", "duchy", "province"};
		Deck[] victoryDecks = new Deck[victoryTypes.length];
		for(int i = 0; i < victoryTypes.length; i++){
			usedCards.put(victoryTypes[i], new Card(victoryTypes[i]));
			victoryDecks[i] = new Deck(usedCards.get(victoryTypes[i]), victoryCardNumber);
		}
		
		Deck[] curseDeck = new Deck[1];
		usedCards.put("curse", new Card("curse"));
		curseDeck[0] = new Deck(usedCards.get("curse"), curseCardNumber);
		
		gameTable.put("Victory", victoryDecks);
		gameTable.put("Treasure", moneyDecks);
		gameTable.put("Curse", curseDeck);

		for (Player p: playerstest) {
			Deck playerDeck = p.getDeck("Deck");
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
	
	public Deck[] getDeck(String deckName) {
		return gameTable.get(deckName);
	}
	
	public Player getCurrentPlayer() {
		return playerstest.get(currentPlayer);
	}
	
	public String getPhase() {
		return this.phase;
	}
	public void setPhase(String whatPhase){
		this.phase = whatPhase;
	}
	public Map<String, Deck[]> getGameTable(){
		return this.gameTable;
	}
	
	private boolean emptyStackCheck(Deck d){
		if(d.isEmpty()){
			return true;
		}
		else return false;
	}

	public void drawCardFromTable(Deck deck, Card card, Player player, String toDeck){
		if (deck.size()!= 0) {
			deck.remove(0);
			player.getDeck(toDeck).add(card);
		}
	}
	
	public void drawCardFromTable(Deck deck, Card card, Player player) {
		drawCardFromTable(deck, card, player, "Discard");
	}

	public void drawCardsFromPlayerDeck(Player player, int number) {
		for (int i=0; i < number; i++ ) {
			if(!player.getDeck("Deck").isEmpty()) {
				player.getDeck("Hand").add(player.getDeck("Deck").pop());
			}
			else {
				discardDeck(player);
				i--;
			}
		}
	}
	
	public void discardDeck(Player player){
		player.getDeck("Discard").moveDeckTo(player.getDeck("Deck"));
		player.getDeck("Deck").shuffle();
	}
	
	public boolean checkGameEnd(){
		if(emptyStackCheck(gameTable.get("Victory")[2])){
			return true;
		}
		
		int emptyStacks = 0;
		
		for(Deck d : gameTable.get("Kingdom")){
			if(emptyStackCheck(d))
				emptyStacks++;
		}
		if(emptyStacks < 3){
			for(Deck d: gameTable.get("Victory")) {
				if (emptyStackCheck(d)) {
					emptyStacks++;
				}
			}
		}
		if(emptyStacks < 3){
			for(Deck d : gameTable.get("Treasure")){
				if(emptyStackCheck(d))
					emptyStacks++;
			}
		}
		
		
		return emptyStacks >= 3;
		
	}
	
	public boolean playCard(int cardNumber, boolean isAction) {
		Player player = getCurrentPlayer();
		Card card = player.getDeck("Hand").getCard(cardNumber);
		if (card.getPlayableTurn() != "never" && (!this.phase.equals("Buy"))) {
			if (card.getType().equals("Treasure"))
			{
			player.getDeck("Hand").moveCardToDeck(cardNumber, player.getDeck("Table"));
			getCurrentPlayer().setCoins(card.getCoins());
			}
			if ((player.getActions() != 0 && card.isAction()) || isAction) {
				if(!isAction) {
					player.getDeck("Hand").moveCardToDeck(cardNumber, player.getDeck("Table"));
					player.setActions(-1);
				}
				getCurrentPlayer().setCoins(card.getCoins());
				player.setActions(card.getActions());
				player.setBuys(card.getBuys());
				drawCardsFromPlayerDeck(player, card.getDraws());
				return true; 
			}
		}	
		return false;
	}
	
	public boolean playCard(int cardNumber) {
		return playCard(cardNumber, false);
	}
	
	public void buyCard(String deckList, int cardNumber, boolean isAction) {
		Player player = getCurrentPlayer();
		Deck deck = gameTable.get(deckList)[cardNumber];
		Card card = deck.getCard(0);
		if (player.getBuys() != 0 ) {
			if(card.getCost() <= player.getCoins()) {
				drawCardFromTable(deck, card, getCurrentPlayer());
				player.setCoins(-card.getCost());
				player.setBuys(-1);
				if(!isAction) {
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
		if ((player.getActions() == 0 || !player.getDeck("Hand").hasKingdomCards()) && !player.getDeck("Hand").hasCardOfType("Treasure")) {
			this.phase = "Buy";
		}
	}
	
	public void endTurn() {
		if (checkGameEnd()) {
			endGame();
		}
		else {
			getCurrentPlayer().endPlayerTurn();
			drawCardsFromPlayerDeck(getCurrentPlayer(), 5);
			
			if ((currentPlayer + 1) < this.getNumberOfPlayers()) {
				currentPlayer += 1;
			}
			else currentPlayer = 0;
			
			this.phase = "Action";
			/*if (!players[currentPlayer].getDeck("Hand").hasKingdomCards() || players[currentPlayer].getActions() == 0) {    // this code looks unnecessary
				this.phase = "Buy";
			}*/
		}
	}
	
	public void endGame() {
		this.phase = "end";
		for(Player p: playerstest) {
			p.endPlayerTurn();
			p.getDeck("Discard").moveDeckTo(p.getDeck("Deck"));
			p.setScore(p.getDeck("Deck").countVictoryPoints());
		}
	}
	
	public String getPlayerStatus(Player p) {
		String playerStatus = String.format("Player Overview of %s:\n", p.getName());
		playerStatus += String.format("Game phase: %s\n", this.phase);
		playerStatus += String.format("Coins:  %2d\t", p.getCoins());
		playerStatus += String.format("Buys: %2d\t", p.getBuys());
		playerStatus += String.format("Actions: %2d\n", p.getActions());
		playerStatus += String.format("Cards on table:\n %s", p.getDeck("Table").toString());
		playerStatus += String.format("Cards in hand:\n %s", p.getDeck("Hand").toString());
		return playerStatus;
	}
	
	public String getTableStatus() {
		String tableStatus = String.format("Overview of table:\n");
		tableStatus += String.format("Treasure Cards:\n\t01)%s\t02)%s\t03)%s", getDeck("Treasure")[0], getDeck("Treasure")[1],getDeck("Treasure")[2]);
		tableStatus += String.format("Victory Cards:\n\t01)%s\t02)%s\t03)%s", getDeck("Victory")[0], getDeck("Victory")[1],getDeck("Victory")[2]);
		tableStatus += String.format("Curse Cards:\n\t01)%s", getDeck("Curse")[0]);
		tableStatus += String.format("KingdomCards:\n\t01)%s\t02)%s\t03)%s\t04)%s\t05)%s\t06)%s\t07)%s\t08)%s\t09)%s\t10)%s", getDeck("Kingdom")[0], getDeck("Kingdom")[1], getDeck("Kingdom")[2], getDeck("Kingdom")[3], getDeck("Kingdom")[4], getDeck("Kingdom")[5], getDeck("Kingdom")[6], getDeck("Kingdom")[7], getDeck("Kingdom")[8], getDeck("Kingdom")[9]);
		return tableStatus;
	}
	
	// temporary test functions to test :P
	public void setDeckCount(int count) {
		gameTable.get("Victory")[2].setCount(count);
	}
	
	public List<Player> getPlayers() {
		return this.playerstest;
	}
	
	public void addVictoryCardsForTest() {
		this.getCurrentPlayer().getDeck("Discard").add(usedCards.get("estate"));
		this.getCurrentPlayer().getDeck("Hand").add(usedCards.get("province"));
		this.getCurrentPlayer().getDeck("Deck").add(usedCards.get("duchy"));
	}
	
	public void addVictoryAndGardenCardsForTest() {
		this.getCurrentPlayer().getDeck("Discard").add(usedCards.get("estate"));
		this.getCurrentPlayer().getDeck("Hand").add(usedCards.get("province"));
		this.getCurrentPlayer().getDeck("Deck").add(usedCards.get("Gardens"));
	}
	

	// Todo do all actions
	
	public void discardFromHand(Player player, int cardNumber){
		player.getDeck("Hand").moveCardToDeck(cardNumber, player.getDeck("Discard"));
	}
	
	public void trashFromHand(Player player, int cardNumber){
		player.getDeck("Hand").moveCardToDeck(cardNumber, player.getDeck("Trash"));
	}
	
	public boolean checkSpecificCard(String specification, String expectedValue){
		if(specification.equals(expectedValue)){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void getCardOfValue(String deckList, int cardNumber, int maxValue) {
		Deck deck = gameTable.get(deckList)[cardNumber];
		Card card = deck.getCard(0);
		if(card.getCost() <= maxValue) {
			drawCardFromTable(deck, card, getCurrentPlayer());
		}
	}
	
	public void trashPlayedCard(){
		Player player = getCurrentPlayer();
		player.getDeck("Table").moveCardToDeck((player.getDeck("Table").size()-1), player.getDeck("Trash"));
	}
	
	public Card getSelectedCard(int cardNumber){
		return getCurrentPlayer().getDeck("Hand").getCard(cardNumber);
	}
	
	@SuppressWarnings("unused")
	private boolean checkReactionCard(Player player){
		return player.getDeck("Hand").hasCardOfType("Reaction");
	}
}