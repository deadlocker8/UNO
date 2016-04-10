package logic;

import java.util.ArrayList;
import java.util.Random;

import application.Controller;

public class Game
{
	private Deck deck;
	private DeadDeck deadDeck;
	private Player player;
	private ArrayList<AI> ais;
	private int gameCount;
	private int challengeCounter;
	private int currentPlayer;
	private Card lastCard;
	private Color wishColor;
	private boolean challenge;
	private Direction direction;
	private Controller controller;

	public Game(Controller controller, int numberOfAIs)
	{
		this.controller = controller;
		deck = new Deck();
		deadDeck = new DeadDeck();
		player = new Player("Spieler", this);
		ais = new ArrayList<AI>();
		for(int i = 0; i < numberOfAIs; i++)
		{
			ais.add(new AI("AI " + i, this));
		}

		gameCount = 0;
		challengeCounter = 0;
	}

	public void newGame(int numberOfStartingCards)
	{
		deck = new Deck();
		deck.shuffle();
		deadDeck = new DeadDeck();
		gameCount++;
		challengeCounter = 0;
		lastCard = null;
		wishColor = null;
		challenge = false;
		direction = Direction.RIGHT;

		player.initialize();

		player.drawCards(deck.drawCards(numberOfStartingCards, deadDeck));

		for(AI currentAI : ais)
		{
			currentAI.initialize();
			currentAI.drawCards(deck.drawCards(numberOfStartingCards, deadDeck));
		}
		
		deadDeck.add(deck.drawCard(deadDeck));
		lastCard = deadDeck.getCards().get(deadDeck.getCards().size()-1);	
		
		start();
	}

	public int getGameCount()
	{
		return gameCount;
	}
	
	private void start()
	{
		Random random = new Random();
		currentPlayer = random.nextInt(ais.size() + 1) + 1;		
	
		//while(true)
		//{
			if(!lastCard.getType().equals(CardType.SKIP))
			{
				if(lastCard.getType().equals(CardType.REVERSE))
				{
					if(direction.equals(Direction.RIGHT))
					{
						direction = Direction.LEFT;
					}
					else
					{
						direction = Direction.RIGHT;
					}
					//TODO show icon direction in UI
				}	
				
				determineNextPlayer();
				
				//DEBUG
				currentPlayer = 1;
				//TODO mark currentPlayer in UI
				
				if(currentPlayer == 1)
				{						
					controller.setValidPlayerDeck(player.getDeck(), player.getValidCards(lastCard, wishColor, challenge));					
					
					//player.turn(lastCard, wishColor, challenge);
//					controller.setPlayerDeck(player.getDeck());
					
					if(player.getDeckSize() == 0)
					{						
						end(player.getName());
//						break;
					}					
				}
				else
				{							
					ais.get(currentPlayer - 2).turn(lastCard, wishColor, challenge);						
					
					if(ais.get(currentPlayer - 2).getDeckSize() == 0)
					{
						end(ais.get(currentPlayer - 2).getName());
//						break;
					}
				}
			}			
		//}
	}
	
	private void determineNextPlayer()
	{
		if(direction.equals(Direction.RIGHT))
		{
			if(currentPlayer == ais.size() + 1)
			{
				currentPlayer = 1;
			}
			else
			{
				currentPlayer++;
			}
		}
		else
		{
			if(currentPlayer == 1)
			{
				currentPlayer = ais.size() + 1;
			}
			else
			{
				currentPlayer--;
			}
		}
	}

	private void end(String name)
	{
		//TODO in UI
		System.out.println("Player " + name + " wins!");
	}

	public Deck getDeck()
	{
		return deck;
	}

	public DeadDeck getDeadDeck()
	{
		return deadDeck;
	}
	
	public int getChallengeCounter()
	{
		return challengeCounter;
	}
	
	public void playCard(Card card, Color wishColor)
	{
		deadDeck.add(card);
		lastCard = card;
		this.wishColor = wishColor;
	
		if(card.getType().equals(CardType.DRAW_TWO))
		{
			challenge = true;
			challengeCounter += 2;
		}	
		else if(card.getType().equals(CardType.DRAW_FOUR))
		{
			challenge = true;
			challengeCounter += 4;			
		}
		
		controller.setLastCard(lastCard);
		
		System.out.println("new lastCard: " + lastCard);
		System.out.println("new wishColor: " + this.wishColor);
		System.out.println("new challenge: " + challenge);
		System.out.println("new challengeCounter: " + challengeCounter);
	}
}