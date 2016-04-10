package logic;

import java.util.ArrayList;
import java.util.Random;

public class AI
{
	private String name;
	private ArrayList<Card> deck;
	private int wins;
	private Game game;

	public AI(String name, Game game)
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
	}

	public void drawCards(ArrayList<Card> cards)
	{
		deck.addAll(cards);
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
					if(currentCard.getColor().equals(lastCard.getColor()) || currentCard.getType().equals(lastCard.getType()) || currentCard.getType().equals(CardType.WILD)
							|| currentCard.getType().equals(CardType.DRAW_FOUR))
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
				drawCards(game.getDeck().drawCards(game.getChallengeCounter(), game.getDeadDeck()));	
				System.out.println("draw " + game.getChallengeCounter() + " cards");
				System.out.println("deack after draw: " + deck);
				game.draw();
			}
			else
			{
				drawCard(game.getDeck().drawCard(game.getDeadDeck()));	
				System.out.println("draw one card");
				System.out.println("deack after draw: " + deck);
				game.draw();
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
				Random random = new Random();
				int colorInt = random.nextInt(4) + 1;
				switch(colorInt)
				{
					case 1: newWishColor = Color.YELLOW;
							break;
					case 2: newWishColor = Color.RED;
							break;
					case 3: newWishColor = Color.BLUE;
							break;
					case 4: newWishColor = Color.GREEN;
							break;
				}			
			}		
			
			game.playCard(playCard(playedCard), newWishColor);
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
}