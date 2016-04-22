package logic;

import java.util.ArrayList;

public class Player
{
	private String name;
	private ArrayList<Card> deck;
	private int winsInARow;	
	private Game game;
	
	public Player(String name, Game game)
	{	
		this.name = name;
		deck = new ArrayList<Card>();
		winsInARow = 0;
		this.game = game;
	}
	
	public void initialize()
	{
		deck = new ArrayList<Card>();
	}
	
	public void win()
	{
		winsInARow++;
	}
	
	public void resetWinsInARow()
	{
		winsInARow = 0;
	}
	
	public int getwinsInARow()
	{
		return winsInARow;
	}
	
	public void drawCard(Card card)
	{
		deck.add(card);
		if(getNumberOfDrawFourCards() >= 2)
		{
			try
			{							
				game.getController().handler.unlockAchievement(8);						
				game.getController().handler.saveAndLoad();
			}
			catch(Exception e)
			{							
			} 
		}		
		
		game.getController().setPlayerDeck(deck);
	}
	
	public void drawCards(ArrayList<Card> cards)
	{
		deck.addAll(cards);
		game.getController().setPlayerDeck(deck);
		game.getController().hideInfo();
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
				if(lastCard.getType().equals(CardType.DRAW_TWO))
				{
					if(game.getController().settings.isAllowChallengePlusTwo())
					{
						if(currentCard.getType().equals(CardType.DRAW_TWO) || currentCard.getType().equals(CardType.DRAW_FOUR))
						{
							validCards.add(currentCard);
						}
					}
				}
				else // lastCard == +4
				{
					if(game.getController().settings.isAllowChallengePlusFourWithFour())
					{
						if(currentCard.getType().equals(CardType.DRAW_FOUR))
						{
							validCards.add(currentCard);
						}						
					}
					
					if(game.getController().settings.isAllowChallengePlusFourWithTwo())
					{
						if(currentCard.getType().equals(CardType.DRAW_TWO))
						{
							if(wishColor == Color.ALL)
							{
								validCards.add(currentCard);
							}
							else if(currentCard.getColor().equals(wishColor))
							{
								validCards.add(currentCard);
							}
						}					
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
			else if(wishColor.equals(Color.ALL))
			{
				for(Card currentCard : deck)
				{								
					if(!currentCard.getType().equals(CardType.WILD) && !currentCard.getType().equals(CardType.DRAW_FOUR))
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
				game.setShowingInfo(true);
				game.getController().showInfo("Du kannst nicht kontern. Ziehe " + game.getChallengeCounter() + " Karten.", game.getChallengeCounter());
			}
			else
			{			
				System.out.println("No valid cards --> please draw");				
			}		
		}
		else
		{
			System.out.println("choose");			
		}	
	}
	
	private int getNumberOfDrawFourCards()
	{
		int counter = 0;
		for(Card card : deck)
		{
			if(card.getType().equals(CardType.DRAW_FOUR))
			{
				counter++;
			}
		}
		return counter;
	}
}