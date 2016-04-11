package logic;

import java.util.ArrayList;

public class Player
{
	private String name;
	private ArrayList<Card> deck;
	private int wins;	
	private Game game;
	
	public Player(String name, Game game)
	{	
		this.name = name;
		deck = new ArrayList<Card>();
		wins = 0;
		this.game = game;
	}
	
	public void initialize()
	{
		deck = new ArrayList<Card>();
	}
	
	public void win()
	{
		wins++;
	}
	
	public int getWins()
	{
		return wins;
	}
	
	public void drawCard(Card card)
	{
		deck.add(card);
		game.getController().setPlayerDeck(deck);
	}
	
	public void drawCards(ArrayList<Card> cards)
	{
		deck.addAll(cards);
		game.getController().setPlayerDeck(deck);
	}
	
	public Card playCard(Card card)
	{
		deck.remove(card);
		return card;
	}
	
	public ArrayList<Card> getValidCards(Card lastCard, Color wishColor, boolean challenge)
	{	
		ArrayList<Card> validCards = new ArrayList<Card>();
		
		if(challenge)
		{
			for(Card currentCard : deck)
			{	
				if(wishColor == null)
				{
					if(currentCard.getType().equals(CardType.DRAW_TWO) || currentCard.getType().equals(CardType.DRAW_FOUR))
					{
						validCards.add(currentCard);
					}
				}
				else
				{
					if(currentCard.getColor().equals(wishColor) && currentCard.getType().equals(CardType.DRAW_TWO) || currentCard.getType().equals(CardType.DRAW_FOUR))
					{
						validCards.add(currentCard);
					}
				}
			}
		}
		else
		{
			if(wishColor == null)
			{	
				for(Card currentCard : deck)
				{								
					if(currentCard.getColor().equals(lastCard.getColor()) || currentCard.getType().equals(lastCard.getType()) || currentCard.getType().equals(CardType.WILD) || currentCard.getType().equals(CardType.DRAW_FOUR))
					{
						validCards.add(currentCard);
					}						
				}
			}
			else
			{
				for(Card currentCard : deck)
				{
					if(currentCard.getColor().equals(wishColor))
					{
						validCards.add(currentCard);
					}	
				}
			}		
		}		
	
		return validCards;
	}
	
	public int getDeckSize()
	{
		return deck.size();
	}
	
	public String getName()
	{
		return name;
	}
	
	public ArrayList<Card> getDeck()
	{
		return deck;
	}
	
	public void turn(Card lastCard, Color wishColor, boolean challenge)
	{
		System.out.println("All cards on hand: \n" + deck);
		ArrayList<Card> validDeck = getValidCards(lastCard, wishColor, challenge);
		System.out.println("validCards: \n" + validDeck);
		if(validDeck.size() == 0)
		{
			if(challenge)
			{
				//TODO notification
				drawCards(game.getDeck().drawCards(game.getChallengeCounter(), game.getDeadDeck()));	
				System.out.println("You can't challenge --> please draw " + game.getChallengeCounter() + " cards");
				game.draw();
			}
			else
			{			
				System.out.println("No valid cards --> please draw");				
			}		
		}
		else
		{
			System.out.println("choose");
			//playerInput (draw or turnCard)		
		}	
	}
}