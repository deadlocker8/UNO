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
	private boolean lastPlayerDraw;
	private boolean skipped;
	private int counter;
	private boolean running;
	
	public Game(Controller controller, int numberOfAIs)
	{
		this.controller = controller;
		deck = new Deck();
		deadDeck = new DeadDeck();
		player = new Player("Spieler", this);
		ais = new ArrayList<AI>();
		for(int i = 1; i <= numberOfAIs; i++)
		{
			ais.add(new AI("AI " + i, i-1, this));
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
		lastPlayerDraw = false;
		skipped = false;

		player.initialize();

		player.drawCards(deck.drawCards(numberOfStartingCards, deadDeck));

		for(AI currentAI : ais)
		{
			currentAI.initialize();
			currentAI.drawCards(deck.drawCards(numberOfStartingCards, deadDeck));
		}
		
		deadDeck.add(deck.drawCard(deadDeck));
		lastCard = deadDeck.getCards().get(deadDeck.getCards().size()-1);	
		controller.setLastCard(lastCard);	
		if(lastCard.getType().equals(CardType.DRAW_FOUR) || lastCard.getType().equals(CardType.WILD))
		{
			wishColor = Color.ALL;
			controller.chosenWishColor = wishColor;
			controller.showCircleWishColor(wishColor);
		}
		
		start();
	}

	public int getGameCount()
	{
		return gameCount;
	}
	
	public void start()
	{
		running = true;
		Random random = new Random();
		currentPlayer = random.nextInt(ais.size() + 1) + 1;			
	
		counter = 0;	
		
		run();
	}
	
	private String run()
	{	
		if(player.getDeckSize() == 0)
		{						
			end(player.getName());	
			return null;
		}	
		
			for(AI winningAI : ais)
			{
				if(winningAI.getDeckSize() == 0)
				{
					end(winningAI.getName());
					return null;
				}
			}		
		
		System.out.println("ROUND: " + counter / 4 + 1);
		
		determineNextPlayer();				
		
		System.out.println("Player " + currentPlayer + "'s turn");
		
		if(skipped || !lastCard.getType().equals(CardType.SKIP))
		{
			if(lastCard.getType().equals(CardType.REVERSE) && !lastPlayerDraw)
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
							
			//TODO mark currentPlayer in UI
			
			if(currentPlayer == 1)
			{			
				controller.setLabelCurrentPlayer(player.getName() + " ist am Zug");
							
				controller.setValidPlayerDeck(player.getDeck(), player.getValidCards(lastCard, wishColor, challenge));					
				
				player.turn(lastCard, wishColor, challenge);										
			}
			else
			{	
			
				AI currentAI = ais.get(currentPlayer - 2);
				
				controller.setLabelCurrentPlayer(currentAI.getName() + " ist am Zug");
				
				controller.setAIDeck(currentAI, currentAI.getDeck());					
				
				currentAI.turn(lastCard, wishColor, challenge);							
			}
		}
		else
		{				
			if(!skipped)
			{	
				System.out.println("SKIPPED player " + currentPlayer);
				skipped = true;				
				run();
			}					
		}
		counter++;		
		
		return null;
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
		//TODO alert 
		
		controller.clearAllDecks(ais);
		
		System.err.println("Player " + name + " wins!");
		
		running = false;
		
		if(currentPlayer == 1)
		{
			controller.setLabelCurrentPlayer(player.getName() + " gewinnt!");
		}
		else
		{
			controller.setLabelCurrentPlayer(ais.get(currentPlayer - 2).getName() + " gewinnt!");
		}		
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
	
	public Player getPlayer()
	{
		return player;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public int getCurrentPlayer()
	{
		return currentPlayer;
	}
	
	public Controller getController()
	{
		return controller;
	}
	
	public void draw()
	{		
		challenge = false;
		challengeCounter = 0;	
		lastPlayerDraw = true;
		
		run();
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
		else
		{
			challenge = false;
			challengeCounter = 0;
		}
		
		lastPlayerDraw = false;
		skipped = false;
		controller.setLastCard(lastCard);		
		
		System.out.println("new lastCard: " + lastCard);
		System.out.println("new wishColor: " + this.wishColor);
		System.out.println("new challenge: " + challenge);
		System.out.println("new challengeCounter: " + challengeCounter);
		
		run();
	}
}