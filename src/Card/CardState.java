package Card;

public interface CardState {

	static final long serialVersionUID = 1337;
	public void completeCard();
	public int getCoins();
	public int getBuys();
	public int getActions();
	public String getPlayableTurn();
	public int getVictoryPoints();
	public int getDraws();
}
