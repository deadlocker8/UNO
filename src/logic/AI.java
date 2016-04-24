package logic;

import java.util.ArrayList;

public class AI
{
	private String name;
	private int id;
	private ArrayList<Card> deck;
	private int wins;
	private Game game;

	public AI(String name, int id, Game game)
	{
		this.name = name;
		this.id = id;
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

	public int getID()
	{
		return id;
	}

	public void drawCard(Card card)
	{
		deck.add(card);
		game.getController().setAIDeck(this);
	}

	public void drawCards(ArrayList<Card> cards)
	{
		deck.addAll(cards);
		game.getController().setAIDeck(this);
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
				System.out.println("draw " + game.getChallengeCounter() + " cards");
				ArrayList<Card> drawedCards = game.getDeck().drawCards(game.getChallengeCounter(), game.getDeadDeck());
				if(game.isRunning())
				{
					game.getController().moveCardFromDeckToAI(this, drawedCards);
				}
				System.out.println("deack after draw: " + deck);
			}
			else
			{
				System.out.println("draw one card");
				ArrayList<Card> drawedCards = new ArrayList<Card>();
				drawedCards.add(game.getDeck().drawCard(game.getDeadDeck()));
				if(game.isRunning())
				{
					game.getController().moveCardFromDeckToAI(this, drawedCards);
				}
				System.out.println("deack after draw: " + deck);
			}
		}
		else
		{
			System.out.println("choose");
			System.out.println("AI chooses: " + getHighestValuedCard(validDeck));

			Card playedCard = getHighestValuedCard(validDeck);
			Color newWishColor = null;

			if(playedCard.getType().equals(CardType.WILD) || playedCard.getType().equals(CardType.DRAW_FOUR))
			{
				newWishColor = getBestColor();				
			}

			if(game.isRunning())
			{
				game.getController().moveAICardToDeadDeck(this, game.getCurrentPlayer(), playedCard, getCardPositionInDeck(playedCard), newWishColor);
			}
		}
	}

	private Card getHighestValuedCard(ArrayList<Card> validDeck)
	{
		Card highestValuedCard = validDeck.get(0);
		for(Card currentCard : validDeck)
		{
			if(currentCard.getValue() > highestValuedCard.getValue())
			{
				highestValuedCard = currentCard;
			}
		}

		return highestValuedCard;
	}
	
	private int getCardPositionInDeck(Card card)
	{
		for(int i = 0; i < deck.size(); i++)
		{
			if(deck.get(i).equals(card))
			{
				return i;
			}
		}
		return 0;
	}

	private Color getBestColor()
	{
		int[] times = new int[4];

		for(Card currentCard : deck)
		{
			switch(currentCard.getColor())
			{
				case YELLOW:
					times[0]++;
					break;
				case RED:
					times[0]++;
					break;
				case BLUE:
					times[0]++;
					break;
				case GREEN:
					times[0]++;
					break;
				default:
					break;
			}
		}

		int maxIndex = 0;
		for(int i = 1; i < times.length; i++)
		{
			int newnumber = times[i];
			if((newnumber > times[maxIndex]))
			{
				maxIndex = i;
			}
		}
		
		switch(maxIndex)
		{
			case 0:	return Color.YELLOW;
			case 1: return Color.RED;
			case 2: return Color.BLUE;
			case 3: return Color.GREEN;
			default: return null;
		}		
	}
}