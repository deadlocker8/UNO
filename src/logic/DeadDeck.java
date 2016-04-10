package logic;

import java.util.ArrayList;

public class DeadDeck
{
	private ArrayList<Card> cards;

	public DeadDeck()
	{
		cards = new ArrayList<Card>();
	}
	
	public void add(Card card)
	{
		cards.add(card);
	}

	public ArrayList<Card> getCards()
	{
		return cards;
	}	
}