package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import achievements.Achievement;
import achievements.AchievementHandler;
import achievements.Achievement.Status;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.AI;
import logic.Card;
import logic.CardType;
import logic.Color;
import logic.Direction;
import logic.Game;
import logic.Player;

public class Controller
{
	@FXML private ImageView iconLastCard;
	@FXML private ImageView iconDeck;
	@FXML private Label labelCurrentPlayer;
	@FXML private AnchorPane mainPane;
	@FXML private Label labelWishColor;
	@FXML private Circle circleWishColor;
	@FXML private ImageView imageViewWishColor;
	@FXML private HBox hboxInfo;
	@FXML private Label labelInfo;
	@FXML private Button buttonInfo;
	@FXML private Label labelChallengeCounter;
	@FXML private ImageView imageViewDirection;
	@FXML private Label labelDirection;
	@FXML private Label labelAI1Name;
	@FXML private Label labelAI2Name;
	@FXML private Label labelAI3Name;
	@FXML private Button buttonStart;	
	@FXML private MenuBar menuBar;
	@FXML private Menu menu1;	
	@FXML private MenuItem menuItem1;
	@FXML private MenuItem menuItem2;
	@FXML private MenuItem menuItem3;
	@FXML private ImageView imageViewLogo;
	@FXML private Label labelLogo;
	@FXML private Button buttonNewGame;
	

	public Game game;
	public Color chosenWishColor;
	public int drawCounter;
	public Settings settings;
	public AchievementHandler handler;
	private int secretCounter;

	public Stage stage;
	public Image icon = new Image("images/icon.png");
	private final ResourceBundle bundle = ResourceBundle.getBundle("application/", Locale.GERMANY);

	private final double CARD_HEIGHT = 90.0;
	private final double CARD_WIDTH = 57.0;	
	
	private final double CARD_SPACING_LARGE = 14.0;
	private final double CARD_SPACING_MEDIUM = 0.0;
	private final double CARD_SPACING_SMALL = - 25.0;
	private final double CARD_SPACING_ULTRA_SMALL = - 35.0;	

	private Point2D PLAYER_STARTING_POINT;
	private final Point2D AI_1_STARTING_POINT = new Point2D(100.0, 75.0);	
	private Point2D AI_2_STARTING_POINT;
	private Point2D AI_3_STARTING_POINT;

	private final javafx.scene.paint.Color COLOR_YELLOW = javafx.scene.paint.Color.web("#FFAA00");
	private final javafx.scene.paint.Color COLOR_RED = javafx.scene.paint.Color.web("#FF5555");
	private final javafx.scene.paint.Color COLOR_BLUE = javafx.scene.paint.Color.web("#5555FD");
	private final javafx.scene.paint.Color COLOR_GREEN = javafx.scene.paint.Color.web("#55AA55");	

	public void init()
	{
		imageViewWishColor.setImage(new Image("/images/circle-all.png"));

		PLAYER_STARTING_POINT = new Point2D(100.0, stage.getScene().getHeight() - 50.0 - CARD_HEIGHT);
		AI_2_STARTING_POINT = new Point2D(stage.getScene().getWidth() - CARD_HEIGHT - 30, 70.0);
		AI_3_STARTING_POINT = new Point2D(60.0, 70.0);
		
		clearAll();
		showNeutralUI();
		
		settings = new Settings();
		try
		{
			settings.load();
		}
		catch(Exception e)
		{			
			e.printStackTrace();
		}	
		
		handler = new AchievementHandler(stage);
		handler.setPath(System.getenv("APPDATA") + "/Deadlocker/UNO/achievements.save");
		try
		{
			handler.loadAchievements();
		}
		catch(Exception e)
		{
			//falls die Datei nicht existiert, wird versucht die neu zu erzeugen
			createAchievements();	
			try
			{
				handler.loadAchievements();
			}
			catch(Exception ex)
			{				
			}
		}
		
		iconLastCard.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				if(secretCounter < 5)
				{
					secretCounter++;
					if(secretCounter == 5)
					{
						try
						{							
							handler.unlockAchievement(9);
							handler.saveAndLoad();
						}
						catch(Exception e)
						{							
						}
					}
				}
				else
				{					
				}
			}			
		});
	}

	public void setStage(Stage stage)
	{
		this.stage = stage;
	}

	public void startGame()
	{
		clearAll();
		menuItem2.setDisable(true);
		
		drawCounter = 0;
		labelCurrentPlayer.setVisible(true);
		
		iconDeck.setImage(createEmptyBackCard());
		iconDeck.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				if(game.isRunning() && game.getCurrentPlayer() == 1 && !game.isShowingInfo())
				{
					Card drawedCard = game.getDeck().drawCard(game.getDeadDeck());
					ArrayList<Card> allCards = new ArrayList<Card>();
					allCards.add(drawedCard);				
					moveCardFromDeckToPlayer(allCards);			
				}
			}
		});
		
		game = new Game(this, settings.getNumberOfAIs(), settings.getAiSpeed());
		setLabelNames(game.getPlayer(), game.getAIs());
		game.newGame(settings.getNumberOfStartingCards());
		
		buttonStart.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				buttonStart.setVisible(false);
				game.start();				
			}
		});
		buttonStart.setVisible(true);		
	}
	
	public void showNeutralUI()
	{
		imageViewLogo.setVisible(true);
		labelLogo.setVisible(true);
		buttonNewGame.setVisible(true);
	}
	
	public void hideNeutralUI()
	{
		imageViewLogo.setVisible(false);
		labelLogo.setVisible(false);
		buttonNewGame.setVisible(false);
	}
	
	public void setLabelNames(Player player, ArrayList<AI> ais)
	{	
		labelAI2Name.setVisible(false);
		labelAI3Name.setVisible(false);
		
		labelAI1Name.setText(ais.get(0).getName());
		labelAI1Name.setVisible(true);		
		
		if(ais.size() >= 2)
		{
			labelAI2Name.setText(ais.get(1).getName());
			labelAI2Name.setVisible(true);
		}
		
		if(ais.size() == 3)
		{		
			labelAI1Name.setText(ais.get(1).getName());
			labelAI2Name.setText(ais.get(2).getName());
			labelAI3Name.setText(ais.get(0).getName());
			labelAI3Name.setVisible(true);
		}
	}	

	public void showCircleWishColor(Color color)
	{
		hideImageViewWishColor();

		switch(color)
		{
			case YELLOW:
				circleWishColor.setFill(COLOR_YELLOW);
				circleWishColor.setVisible(true);
				break;
			case RED:
				circleWishColor.setFill(COLOR_RED);
				circleWishColor.setVisible(true);
				break;
			case BLUE:
				circleWishColor.setFill(COLOR_BLUE);
				circleWishColor.setVisible(true);
				break;
			case GREEN:
				circleWishColor.setFill(COLOR_GREEN);
				circleWishColor.setVisible(true);
				break;
			case ALL:
				showImageViewWishColor();
				break;
			default:
				break;
		}

		labelWishColor.setVisible(true);
	}

	public void showImageViewWishColor()
	{
		hideCircleWishColor();

		imageViewWishColor.setVisible(true);
	}

	public void hideCircleWishColor()
	{
		labelWishColor.setVisible(false);
		circleWishColor.setVisible(false);
	}

	public void hideImageViewWishColor()
	{
		imageViewWishColor.setVisible(false);
		circleWishColor.setVisible(false);
	}

	public void hideWishColor()
	{
		hideCircleWishColor();
		hideImageViewWishColor();
	}

	public void hideInfo()
	{		
		hboxInfo.setVisible(false);
	}

	public void showInfo(String text, int numberOfCards)
	{
		labelInfo.setText(text);
		buttonInfo.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				if(game.getChallengeCounter() > 10 )
				{
					try
					{							
						handler.unlockAchievement(5);									
						handler.saveAndLoad();
					}
					catch(Exception e)
					{							
					}
					
				}
				moveCardFromDeckToPlayer(game.getDeck().drawCards(game.getChallengeCounter(), game.getDeadDeck()));				
			}
		});

		hboxInfo.setVisible(true);
	}
	
	public void hideLabelChallengeCounter()
	{
		labelChallengeCounter.setVisible(false);
	}
	
	public void showLabelChallengeCounter(String text)
	{
		labelChallengeCounter.setText(text);
		labelChallengeCounter.setVisible(true);
	}
	
	public void hideImageViewDirection()
	{
		imageViewDirection.setVisible(false);
		labelDirection.setVisible(false);
	}
	
	public void setImageViewDirection(Direction direction)
	{
		imageViewDirection.setVisible(true);
		labelDirection.setVisible(true);
		
		if(direction.equals(Direction.RIGHT))
		{
			imageViewDirection.setImage(new Image("/images/DIRECTION_RIGHT.png"));
		}
		else
		{
			imageViewDirection.setImage(new Image("/images/DIRECTION_LEFT.png"));
		}
	}

	public void setLabelCurrentPlayer(String text)
	{
		labelCurrentPlayer.setText(text);
	}

	public void setLastCard(Card card)
	{
		iconLastCard.setImage(createCard(card, true).getImage());
	}

	private Image createEmptyBackCard()
	{
		return new Image("images/card-back.png");
	}

	private ImageView createBackCard()
	{
		ImageView imageView = new ImageView(new Image("images/card-back.png"));
		imageView.setFitHeight(CARD_HEIGHT);
		imageView.setFitWidth(CARD_WIDTH);

		return imageView;
	}

	private ImageView createCard(Card card, boolean valid)
	{
		ImageView imageView = new ImageView(new Image("images/" + card.getType() + "-" + card.getColor() + ".png"));
		imageView.setFitHeight(CARD_HEIGHT);
		imageView.setFitWidth(CARD_WIDTH);
		imageView.setSmooth(true);

		if(!valid)
		{
			SnapshotParameters parameters = new SnapshotParameters();
			parameters.setFill(javafx.scene.paint.Color.TRANSPARENT);
			WritableImage snapshot = imageView.snapshot(parameters, null);

			if(card.getType().equals(CardType.DRAW_FOUR) && card.getType().equals(CardType.WILD))
			{
				for(int x = 0; x < snapshot.getWidth(); x++)
				{
					for(int y = 0; y < snapshot.getHeight(); y++)
					{
						javafx.scene.paint.Color oldColor = snapshot.getPixelReader().getColor(x, y).desaturate().desaturate().brighter();
						snapshot.getPixelWriter().setColor(x, y, new javafx.scene.paint.Color(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue(), oldColor.getOpacity() * 1.0));
					}
				}
				imageView.setImage(snapshot);
			}
			else
			{
				for(int x = 0; x < snapshot.getWidth(); x++)
				{
					for(int y = 0; y < snapshot.getHeight(); y++)
					{
						javafx.scene.paint.Color oldColor = snapshot.getPixelReader().getColor(x, y).darker().desaturate();						
						snapshot.getPixelWriter().setColor(x, y, new javafx.scene.paint.Color(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue(), oldColor.getOpacity() * 1.0));
					}
				}
				imageView.setImage(snapshot);
			}
		}
		Controller main = this;

		imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				if(game.isRunning() && game.getCurrentPlayer() == 1)
				{
					if(valid)
					{
						if(card.getType().equals(CardType.WILD) || card.getType().equals(CardType.DRAW_FOUR))
						{
							try
							{
								FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/ColorChooser.fxml"));

								Parent root = (Parent)fxmlLoader.load();
								Stage newStage = new Stage();
								newStage.setScene(new Scene(root, 300, 300));
								newStage.setTitle("Wunschfarbe");
								newStage.initOwner(stage);

								newStage.getIcons().add(icon);

								ColorChooserController newController = fxmlLoader.getController();
								newController.init(newStage, main);

								newStage.initModality(Modality.APPLICATION_MODAL);
								newStage.setResizable(false);
								newStage.showAndWait();

							}
							catch(IOException e1)
							{
								e1.printStackTrace();
							}
						}
						else
						{
							chosenWishColor = null;
						}

						moveCardToDeadDeck(imageView, card, chosenWishColor);
					}
				}
			}
		});

		return imageView;
	}

	public void moveCardToDeadDeck(ImageView view, Card card, Color newWishColor)
	{
		Point2D deckPosition = iconLastCard.localToScene(Point2D.ZERO);

		TranslateTransition translateTransition = new TranslateTransition();
		translateTransition.setDuration(Duration.millis(500));
		translateTransition.setNode(view);
		translateTransition.setCycleCount(1);
		translateTransition.setAutoReverse(false);
		translateTransition.setFromX(0);
		translateTransition.setFromY(0);
		translateTransition.setToX( - (view.getX() - deckPosition.getX()));
		translateTransition.setToY( - (view.getY() - deckPosition.getY()));
		translateTransition.setOnFinished(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				if(newWishColor != null)
				{
					showCircleWishColor(newWishColor);
				}
				else
				{
					hideWishColor();
				}
				Card playedCard	= game.getPlayer().playCard(card);
				
				if(playedCard.getType().equals(CardType.DRAW_FOUR) && game.getDeadDeck().getCards().get(game.getDeadDeck().getCards().size()-1).getType().equals(CardType.DRAW_FOUR) && game.getChallengeCounter() > 0)
				{
					try
					{							
						handler.unlockAchievement(6);						
						handler.saveAndLoad();
					}
					catch(Exception e)
					{							
					}
				}
				
				if(playedCard.getType().equals(CardType.WILD))
				{
					try
					{							
						handler.unlockAchievement(7);						
						handler.saveAndLoad();
					}
					catch(Exception e)
					{							
					}
				}		
				
				setPlayerDeck(game.getPlayer().getDeck());
				game.playCard(playedCard, newWishColor);
			}
		});

		translateTransition.play();
	}

	public void moveAICardToDeadDeck(AI ai, int currentPlayer, Card card, Color newWishColor)
	{
		ObservableList<Node> nodes = mainPane.getChildren();
		ArrayList<Integer> possibleNodes = new ArrayList<Integer>();
		for(int i = 0; i < nodes.size(); i++)
		{
			Node current = nodes.get(i);
			if(current.getId().contains("ai" + ai.getID()))
			{
				possibleNodes.add(i);
			}
		}

		Random random = new Random();
		int viewNumber = random.nextInt(possibleNodes.size());

		ImageView view = (ImageView)mainPane.getChildren().get(possibleNodes.get(viewNumber));
		view.setImage(new Image("images/" + card.getType() + "-" + card.getColor() + ".png"));

		Point2D deckPosition = iconLastCard.localToScene(Point2D.ZERO);

		TranslateTransition translateTransition = new TranslateTransition();
		translateTransition.setDuration(Duration.millis(500));
		translateTransition.setNode(view);
		translateTransition.setCycleCount(1);
		translateTransition.setAutoReverse(false);
		translateTransition.setFromX(0);
		translateTransition.setFromY(0);
		translateTransition.setToX( - (view.getX() - deckPosition.getX()));
		translateTransition.setToY( - (view.getY() - deckPosition.getY()));
		translateTransition.setOnFinished(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				if(newWishColor != null)
				{
					showCircleWishColor(newWishColor);
				}
				else
				{
					hideWishColor();
				}
				Card playedCard = ai.playCard(card);
				setAIDeck(ai);
				game.playCard(playedCard, newWishColor);
			}
		});

		translateTransition.play();
	}
	
	public void moveCardFromDeckToPlayer(ArrayList<Card> cards)
	{
		Point2D deckPosition = iconDeck.localToScene(Point2D.ZERO);
		
		ImageView view = createCard(cards.get(drawCounter), true);
		view.setId("drawAnimation");
		view.setX(deckPosition.getX());
		view.setY(deckPosition.getY());
		mainPane.getChildren().add(view);	
		
		TranslateTransition translateTransition = new TranslateTransition();
		translateTransition.setDuration(Duration.millis(500));
		translateTransition.setNode(view);
		translateTransition.setCycleCount(1);
		translateTransition.setAutoReverse(false);
		translateTransition.setFromX(0);
		translateTransition.setFromY(0);
		translateTransition.setToX( - (view.getX() - getPositionOfRightCard(null)));
		translateTransition.setToY( - (view.getY() - PLAYER_STARTING_POINT.getY()));
		translateTransition.setOnFinished(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				ObservableList<Node> nodes = mainPane.getChildren();
				Iterator<Node> iterator = nodes.iterator();
				while(iterator.hasNext())
				{
					if(iterator.next().getId().equals("drawAnimation"))
					{
						iterator.remove();
					}
				}
				
				game.getPlayer().drawCard(cards.get(drawCounter));
				setPlayerDeck(game.getPlayer().getDeck());
				drawCounter++;
				
				if(drawCounter < cards.size())
				{				
					moveCardFromDeckToPlayer(cards);
				}
				else				
				{
					game.setShowingInfo(false);
					hideInfo();
					drawCounter = 0;
					game.draw();
				}						
			}
		});

		translateTransition.play();
	}
	
	private double getPositionOfRightCard(AI ai)
	{	
		if(ai == null)
		{
			double maxWidth = stage.getScene().getWidth() - (PLAYER_STARTING_POINT.getX() * 2) - CARD_WIDTH;
			int deckSize = game.getPlayer().getDeckSize();
			if((deckSize * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxWidth)
			{
				if((deckSize * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxWidth)
				{
					if((deckSize * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxWidth)
					{
						return PLAYER_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL;
					}
					else
					{
						return PLAYER_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL;
					}
				}
				else
				{
					return PLAYER_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM;
				}
			}
			else
			{
				return PLAYER_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE;
			}	
		}
		//AI 1 (Above Player)
		else
		{
			double maxWidth = stage.getScene().getWidth() - (AI_1_STARTING_POINT.getX() * 2) - CARD_WIDTH;
			int deckSize = ai.getDeckSize();
			if((deckSize * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxWidth)
			{
				if((deckSize * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxWidth)
				{
					if((deckSize * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxWidth)
					{
						return AI_1_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL;
					}
					else
					{
						return AI_1_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL;
					}
				}
				else
				{
					return AI_1_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM;
				}
			}
			else
			{
				return AI_1_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE;
			}	
		}		
	}
	
	private double getPositionOfBottomCard(AI ai)
	{			
		double maxHeight = stage.getScene().getHeight() - ((AI_2_STARTING_POINT.getY() + 40.0) * 2) - CARD_WIDTH;
		int deckSize = ai.getDeckSize();					

		if((deckSize * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxHeight)
		{
			if((deckSize * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxHeight)
			{
				if((deckSize * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxHeight)
				{
					return AI_2_STARTING_POINT.getY() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL;
				}
				else
				{
					return AI_2_STARTING_POINT.getY() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL;
				}
			}
			else
			{
				return AI_2_STARTING_POINT.getY() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM;
			}
		}
		else
		{
			return AI_2_STARTING_POINT.getY() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE;
		}			
	}
	
	@SuppressWarnings("unused")
	public void moveCardFromDeckToAI(AI ai, ArrayList<Card> cards)
	{
		Card card = game.getDeck().drawCard(game.getDeadDeck());
		
		Point2D deckPosition = iconDeck.localToScene(Point2D.ZERO);
		
		ImageView view = createBackCard();
		view.setId("drawAnimation");
		view.setX(deckPosition.getX());
		view.setY(deckPosition.getY());
		mainPane.getChildren().add(view);	
		
		TranslateTransition translateTransition = new TranslateTransition();
		translateTransition.setDuration(Duration.millis(500));
		translateTransition.setNode(view);
		translateTransition.setCycleCount(1);
		translateTransition.setAutoReverse(false);
		translateTransition.setFromX(0);
		translateTransition.setFromY(0);
		
		switch(ai.getID())
		{
			case 1:		translateTransition.setToX( - (view.getX() - getPositionOfRightCard(ai)));
						translateTransition.setToY( - (view.getY() - AI_1_STARTING_POINT.getY()));
						break;
			case 2:		translateTransition.setToX( - (view.getX() - AI_2_STARTING_POINT.getX()));
						translateTransition.setToY( - (view.getY() - getPositionOfBottomCard(ai)));
						break;				
			case 3:		translateTransition.setToX( - (view.getX() - AI_3_STARTING_POINT.getX()));
						translateTransition.setToY( - (view.getY() - getPositionOfBottomCard(ai)));
						break;				
			default:	break;
		}
	
		translateTransition.setOnFinished(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				ObservableList<Node> nodes = mainPane.getChildren();
				Iterator<Node> iterator = nodes.iterator();
				while(iterator.hasNext())
				{
					if(iterator.next().getId().equals("drawAnimation"))
					{
						iterator.remove();
					}
				}
				
				ai.drawCard(cards.get(drawCounter));
				setAIDeck(ai);
				drawCounter++;
				
				if(drawCounter < cards.size())
				{				
					moveCardFromDeckToAI(ai, cards);
				}
				else				
				{
					game.setShowingInfo(false);
					hideInfo();
					drawCounter = 0;
					game.draw();
				}						
			}
		});

		translateTransition.play();
	}

	public void clearPlayerDeck()
	{
		ObservableList<Node> nodes = mainPane.getChildren();
		Iterator<Node> iterator = nodes.iterator();
		while(iterator.hasNext())
		{
			if(iterator.next().getId().equals("player"))
			{
				iterator.remove();
			}
		}
	}

	public void setPlayerDeck(ArrayList<Card> deck)
	{
		clearPlayerDeck();

		int counter = 1;

		for(int i = 0; i < deck.size(); i++)
		{			
			ImageView current = createCard(deck.get(i), true);

			current.setId("player");
			mainPane.getChildren().add(current);			

			if(i == 0)
			{
				current.setX(AI_1_STARTING_POINT.getX() + CARD_WIDTH);
			}
			else
			{	
				double maxWidth = stage.getScene().getWidth() - (PLAYER_STARTING_POINT.getX() * 2) - CARD_WIDTH;
				if((deck.size() * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxWidth)
				{
					if((deck.size() * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxWidth)
					{
						if((deck.size() * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxWidth)
						{
							current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL);
						}
						else
						{
							current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL);
						}
					}
					else
					{
						current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM);
					}
				}
				else
				{
					current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE);
				}
			}

			current.setY(PLAYER_STARTING_POINT.getY());

			counter++;
		}
	}

	public void setValidPlayerDeck(ArrayList<Card> deck, ArrayList<Card> validDeck)
	{
		clearPlayerDeck();

		int counter = 1;

		for(int i = 0; i < deck.size(); i++)
		{			
			Card currentCard = deck.get(i);
			ImageView current;

			if(validDeck.contains(currentCard))
			{
				current = createCard(currentCard, true);
				current.setY(PLAYER_STARTING_POINT.getY() - CARD_HEIGHT/4);
			}
			else
			{
				current = createCard(currentCard, false);
				current.setY(PLAYER_STARTING_POINT.getY());
			}

			current.setId("player");

			mainPane.getChildren().add(current);

			if(i == 0)
			{
				current.setX(AI_1_STARTING_POINT.getX() + CARD_WIDTH);
			}
			else
			{	
				double maxWidth = stage.getScene().getWidth() - (PLAYER_STARTING_POINT.getX() * 2) - CARD_WIDTH;
				if((deck.size() * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxWidth)
				{
					if((deck.size() * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxWidth)
					{
						if((deck.size() * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxWidth)
						{
							current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL);
						}
						else
						{
							current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL);
						}
					}
					else
					{
						current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM);
					}
				}
				else
				{
					current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE);
				}
			}
			
			counter++;
		}
	}

	public void clearAIDeck(AI ai)
	{
		ObservableList<Node> nodes = mainPane.getChildren();
		Iterator<Node> iterator = nodes.iterator();
		while(iterator.hasNext())
		{
			if(iterator.next().getId().contains("ai" + ai.getID()))
			{
				iterator.remove();
			}
		}
	}

	public void setAIDeck(AI ai)
	{
		clearAIDeck(ai);
		
		ArrayList<Card> deck = ai.getDeck();

		int counter = 1;
		
		for(int i = 0; i < deck.size(); i++)
		{
			ImageView current = createBackCard();

			current.setId("ai" + ai.getID());
			
			mainPane.getChildren().add(current);
			
			double maxWidth;
			double maxHeight;
			int deckSize;			
			
			switch(ai.getID())
			{
				case 1:	maxWidth = stage.getScene().getWidth() - ((AI_1_STARTING_POINT.getX() + 25.0) * 2) - CARD_WIDTH;
						deckSize = ai.getDeckSize();

						if(i == 0)
						{
							current.setX(AI_1_STARTING_POINT.getX() + CARD_WIDTH);
						}
						else
						{					
							if((deckSize * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxWidth)
							{
								if((deckSize * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxWidth)
								{
									if((deckSize * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxWidth)
									{
										current.setX(AI_1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL);
									}
									else
									{
										current.setX(AI_1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL);
									}
								}
								else
								{
									current.setX(AI_1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM);
								}
							}
							else
							{
								current.setX(AI_1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE);
							}
						}
		
						current.setY(AI_1_STARTING_POINT.getY());
						break;
						
				case 2:	maxHeight = stage.getScene().getHeight() - ((AI_2_STARTING_POINT.getY() + 40.0) * 2) - CARD_WIDTH;
						deckSize = ai.getDeckSize();
						
						current.setRotate(-90.0);						
						
						if(i == 0)
						{
							current.setY(AI_2_STARTING_POINT.getY() + CARD_WIDTH);							
						}
						else
						{						
							if((deckSize * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxHeight)
							{
								if((deckSize * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxHeight)
								{
									if((deckSize * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxHeight)
									{
										current.setY(AI_2_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL);
									}
									else
									{
										current.setY(AI_2_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL);
									}
								}
								else
								{
									current.setY(AI_2_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM);
								}
							}
							else
							{
								current.setY(AI_2_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE);
							}
						}
		
						current.setX(AI_2_STARTING_POINT.getX());
						break;
						
				case 3:	maxHeight = stage.getScene().getHeight() - ((AI_3_STARTING_POINT.getY() + 40.0) * 2) - CARD_WIDTH;
						deckSize = ai.getDeckSize();
						
						current.setRotate(90.0);
		
						if(i == 0)
						{
							current.setY(AI_3_STARTING_POINT.getY() + CARD_WIDTH);
						}
						else
						{							
							if((deckSize * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxHeight)
							{
								if((deckSize * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxHeight)
								{
									if((deckSize * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxHeight)
									{
										current.setY(AI_3_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL);
									}
									else
									{
										current.setY(AI_3_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL);
									}
								}
								else
								{
									current.setY(AI_3_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM);
								}
							}
							else
							{
								current.setY(AI_3_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE);
							}
						}
		
						current.setX(AI_3_STARTING_POINT.getX());
						break;
				default: break;
			}	
			
			counter++;
		}
	}

	public void clearAllDecks(ArrayList<AI> ais)
	{
		clearPlayerDeck();

		for(AI currentAI : ais)
		{
			clearAIDeck(currentAI);
		}
	}	

	public void openSettings()
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/Settings.fxml"));

			Parent root = (Parent)fxmlLoader.load();
			Stage newStage = new Stage();
			newStage.setScene(new Scene(root, 600, 400));
			newStage.setTitle("Einstellungen");
			newStage.initOwner(stage);

			newStage.getIcons().add(icon);			
			SettingsController newController = fxmlLoader.getController();
			newController.init(newStage, this);

			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setResizable(false);
			newStage.showAndWait();

		}
		catch(IOException e1)
		{
			e1.printStackTrace();
		}
	}	
	
	public void clearAll()
	{
		hideNeutralUI();
		menuItem2.setDisable(false);
		hideWishColor();
		hideInfo();
		labelCurrentPlayer.setVisible(false);
		hideLabelChallengeCounter();
		hideImageViewDirection();
		labelAI1Name.setVisible(false);
		labelAI2Name.setVisible(false);
		labelAI3Name.setVisible(false);
		buttonStart.setVisible(false);	
		iconDeck.setImage(null);
		iconLastCard.setImage(null);	
	}
	
	private void createAchievements()
	{		
		AchievementHandler handler = new AchievementHandler(stage);
		handler.setPath(System.getenv("APPDATA") + "/Deadlocker/UNO/achievements.save");
		handler.addAchievement(new Achievement("Anfänger", "Gewinne dein erstes Spiel", null, null, Status.LOCKED));
		handler.addAchievement(new Achievement("Fortgeschrittener", "Gewinne insgesamt 10 Spiele", null, null, Status.LOCKED, 0, 10, 0));
		handler.addAchievement(new Achievement("Experte", "Gewinne insgesamt 50 Spiele", null, null, Status.LOCKED, 0, 50, 0));
		
		handler.addAchievement(new Achievement("Glückssträhne", "Gewinne hintereinander 3 Spiele", null, null, Status.LOCKED, 0, 3, 0));
		handler.addAchievement(new Achievement("Läuft bei dir!", "Gewinne hintereinander 5 Spiele", null, null, Status.LOCKED, 0, 5, 0));
		
		handler.addAchievement(new Achievement("Arme Sau", "Du musst mehr als 10 Karten ziehen", null, null, Status.LOCKED));
		handler.addAchievement(new Achievement("Gegenangriff", "Kontere eine +4", null, null, Status.LOCKED));
		handler.addAchievement(new Achievement("Wunschkonzert", "Wünsch dir eine Farbe", null, null, Status.LOCKED));
		handler.addAchievement(new Achievement("Cheatest du?", "Besitze zwei +4 Karten gleichzeitig", null, null, Status.LOCKED));		
		
		handler.addAchievement(new Achievement("Unmöglich", "Klicke 5 mal auf den Ablagestapel", null, null, Status.HIDDEN));		

		try
		{
			handler.saveAchievements();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	private void buttonAchievements()
	{ 	
		Stage newStage = new Stage();
		newStage.setMinHeight(250);
		newStage.setMinWidth(250);

		AnchorPane root = new AnchorPane();

		try
		{
			handler.loadAchievements();
		}
		catch(Exception e)
		{
		}

		AnchorPane list = handler.getAchievementList();
		AnchorPane summary = handler.getSummary();

		root.getChildren().add(summary);
		root.getChildren().add(list);
		
		newStage.setResizable(false);

		AnchorPane.setTopAnchor(summary, 50.0);
		AnchorPane.setLeftAnchor(summary, 25.0);
		AnchorPane.setRightAnchor(summary, 50.0);

		AnchorPane.setTopAnchor(list, 180.0);
		AnchorPane.setLeftAnchor(list, 25.0);
		AnchorPane.setRightAnchor(list, 25.0);
		AnchorPane.setBottomAnchor(list, 25.0);

		root.setStyle("-fx-background-color: #3F3F3F;");

		Scene scene = new Scene(root, 800, 600);
		newStage.setScene(scene);

		newStage.setTitle("Achievements");
		newStage.initModality(Modality.APPLICATION_MODAL);
		newStage.getIcons().add(new Image("/images/icon.png"));
		newStage.setResizable(true);
		newStage.show();
	}
	
	public void about()
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Über " + bundle.getString("app.name"));
		alert.setHeaderText(bundle.getString("app.name"));
		alert.setContentText("Version:     " + bundle.getString("version.name") + "\r\nDatum:      " + bundle.getString("version.date") + "\r\nAutor:        Robert Goldmann\r\n");
		Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(icon);
		alert.showAndWait();
	}	
}