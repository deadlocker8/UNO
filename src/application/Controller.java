package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.AI;
import logic.Card;
import logic.CardType;
import logic.Color;
import logic.Game;

public class Controller
{
	@FXML private ImageView iconLastCard;
	@FXML private ImageView iconDeck;
	@FXML private Label labelCurrentPlayer;
	@FXML private AnchorPane mainPane;
	@FXML private Label labelWishColor;
	@FXML private Circle circleWishColor;
	
	public Game game;
	public Color chosenWishColor;

	public Stage stage;
	public Image icon = new Image("images/icon.png");
	private final ResourceBundle bundle = ResourceBundle.getBundle("application/", Locale.GERMANY);
	
	private final double CARD_HEIGHT = 90.0;
	private final double CARD_WIDTH = 57.0;	
	private final double CARD_SPACING_HORIZONTAL_LARGE = 14.0;
	private final double CARD_SPACING_HORIZONTAL_MEDIUM = -3.0;
	private final double CARD_SPACING_HORIZONTAL_SMALL = -25.0;	
	private final double CARD_SPACING_HORIZONTAL_ULTRA_SMALL = -35.0;	
	
	private Point2D PLAYER_STARTING_POINT;
	private final Point2D AI_1_STARTING_POINT = new Point2D(100.0, 30.0);
	
	private final javafx.scene.paint.Color COLOR_YELLOW = javafx.scene.paint.Color.web("#FFAA00");
	private final javafx.scene.paint.Color COLOR_RED = javafx.scene.paint.Color.web("#FF5555");
	private final javafx.scene.paint.Color COLOR_BLUE = javafx.scene.paint.Color.web("#5555FD");
	private final javafx.scene.paint.Color COLOR_GREEN = javafx.scene.paint.Color.web("#55AA55");
	private final javafx.scene.paint.Color COLOR_CARD_INVALID = javafx.scene.paint.Color.web("#CCCCCC");
	
		
	public void init()
	{
		PLAYER_STARTING_POINT = new Point2D(100.0, stage.getScene().getHeight() - 30.0 - CARD_HEIGHT);
		
		iconDeck.setImage(createEmptyBackCard());
		iconDeck.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				if(game.isRunning() && game.getCurrentPlayer() == 1)
				{
					game.getPlayer().drawCard(game.getDeck().drawCard(game.getDeadDeck()));
					setPlayerDeck(game.getPlayer().getDeck());

					// TODO move card toPlayerDeck moveCardToDeadDeck(imageView,
					// card, newWishColor);
					// --> in "onFinish"
					game.draw();
				}
			}
		});

	
		startGame();
	}

	public void setStage(Stage stage)
	{
		this.stage = stage;	
	}

	public void startGame()
	{
		hideCircleWishColor();
		
		// DEBUG
		game = new Game(this, 1);
		game.newGame(5);
	}
	
	public void showCircleWishColor(Color color)
	{
		switch(color)
		{
			case YELLOW:	circleWishColor.setFill(COLOR_YELLOW);
							break;
			case RED:		circleWishColor.setFill(COLOR_RED);
							break;
			case BLUE:		circleWishColor.setFill(COLOR_BLUE);
							break;
			case GREEN:		circleWishColor.setFill(COLOR_GREEN);
							break;
			case ALL:		//TODO show quartered circle (all four colors)
							break;
			default: 		break;
		}
		
		labelWishColor.setVisible(true);
		circleWishColor.setVisible(true);	
	}
	
	public void hideCircleWishColor()
	{
		labelWishColor.setVisible(false);
		circleWishColor.setVisible(false);	
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

		if(!valid)
		{
			WritableImage snapshot = imageView.snapshot(new SnapshotParameters(), null);
			
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
						javafx.scene.paint.Color oldColor = snapshot.getPixelReader().getColor(x, y).desaturate().desaturate().desaturate();
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
		translateTransition.setToX(- (view.getX() - deckPosition.getX()));
		translateTransition.setToY(- (view.getY() - deckPosition.getY()));	
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
					hideCircleWishColor();
				}
				setPlayerDeck(game.getPlayer().getDeck());
				game.playCard(game.getPlayer().playCard(card), newWishColor);				
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
		translateTransition.setToX(- (view.getX() - deckPosition.getX()));
		translateTransition.setToY(- (view.getY() - deckPosition.getY()));	
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
					hideCircleWishColor();
				}
				setAIDeck(ai, ai.getDeck());
				game.playCard(ai.playCard(card), newWishColor);
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
		
		for(Card currentCard : deck)
		{
			ImageView current = createCard(currentCard, true);
			
			current.setId("player");
			mainPane.getChildren().add(current);
			if((deck.size() * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_LARGE)) > (stage.getScene().getWidth() - PLAYER_STARTING_POINT.getX() * 2))
			{
				if((deck.size() * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_MEDIUM)) > (stage.getScene().getWidth() - PLAYER_STARTING_POINT.getX() * 2))
				{
					if((deck.size() * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_SMALL)) > (stage.getScene().getWidth() - PLAYER_STARTING_POINT.getX() * 2))
					{
						current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_ULTRA_SMALL)));
					}
					else
					{
						current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_SMALL)));
					}
				}
				else
				{
					current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_MEDIUM)));	
				}
			}
			else
			{
				current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_LARGE)));	
			}
			
			current.setY(PLAYER_STARTING_POINT.getY());		
			
			counter++;
		}
	}

	public void setValidPlayerDeck(ArrayList<Card> deck, ArrayList<Card> validDeck)
	{
		clearPlayerDeck();
		
		int counter = 1;
		
		for(Card currentCard : deck)
		{
			ImageView current;
					
			if(validDeck.contains(currentCard))
			{
				current = createCard(currentCard, true);
			}
			else
			{
				current = createCard(currentCard, false);
			}
			
			current.setId("player");

			mainPane.getChildren().add(current);
			
			double maxWidth = stage.getScene().getWidth() - (PLAYER_STARTING_POINT.getX() * 2) - CARD_WIDTH;
			
			if((deck.size() * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_LARGE)) > maxWidth)
			{
				if((deck.size() * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_MEDIUM)) > maxWidth)
				{
					if((deck.size() * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_SMALL)) > maxWidth)
					{
						current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_ULTRA_SMALL)));
					}
					else
					{
						current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_SMALL)));
					}
				}
				else
				{
					current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_MEDIUM)));	
				}
			}
			else
			{
				current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_LARGE)));	
			}
			
			current.setY(PLAYER_STARTING_POINT.getY());							
			
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
	
	public void setAIDeck(AI ai, ArrayList<Card> deck)
	{
		clearAIDeck(ai);
		
		int counter = 1;
		
		for(Card currentCard : deck)
		{
			ImageView current = createBackCard();
			
			current.setId("ai" + ai.getID());
			
			//TODO other AIs (vertical) --> flip imageViews by 90 degrees
			mainPane.getChildren().add(current);
			double maxWidth = stage.getScene().getWidth() - (AI_1_STARTING_POINT.getX() * 2) - CARD_WIDTH;
			
			if((deck.size() * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_LARGE)) > maxWidth)
			{
				if((deck.size() * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_MEDIUM)) > maxWidth)
				{
					if((deck.size() * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_SMALL)) > maxWidth)
					{
						current.setX(AI_1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_ULTRA_SMALL)));
					}
					else
					{
						current.setX(AI_1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_SMALL)));
					}
				}
				else
				{
					current.setX(AI_1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_MEDIUM)));	
				}
			}
			else
			{
				current.setX(AI_1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_HORIZONTAL_LARGE)));	
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