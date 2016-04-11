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
import javafx.scene.layout.HBox;
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
	@FXML private HBox hboxPlayerDeck;
	@FXML private HBox hboxAI1;
	@FXML private Label labelCurrentPlayer;
	@FXML private AnchorPane mainPane;

	public Game game;
	public Color chosenWishColor;

	public Stage stage;
	public Image icon = new Image("images/icon.png");
	private final ResourceBundle bundle = ResourceBundle.getBundle("application/", Locale.GERMANY);

	public void init()
	{
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

		// DEBUG
		game = new Game(this, 1);
		game.newGame(5);
	}

	public void setStage(Stage stage)
	{
		this.stage = stage;
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
		imageView.setFitHeight(90.0);
		imageView.setFitWidth(57.0);

		return imageView;
	}

	private ImageView createCard(Card card, boolean valid)
	{
		ImageView imageView = new ImageView(new Image("images/" + card.getType() + "-" + card.getColor() + ".png"));
		imageView.setFitHeight(90.0);
		imageView.setFitWidth(57.0);

		if(!valid)
		{
			WritableImage snapshot = imageView.snapshot(new SnapshotParameters(), null);
			for(int x = 0; x < snapshot.getWidth(); x++)
			{
				for(int y = 0; y < snapshot.getHeight(); y++)
				{
					javafx.scene.paint.Color oldColor = snapshot.getPixelReader().getColor(x, y);
					snapshot.getPixelWriter().setColor(x, y, new javafx.scene.paint.Color(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue(), oldColor.getOpacity() * 0.3));
				}
			}
			imageView.setImage(snapshot);
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
		Point2D cardPosition = view.localToScene(Point2D.ZERO);
		Point2D deckPosition = iconLastCard.localToScene(Point2D.ZERO);	
	
		TranslateTransition translateTransition = new TranslateTransition();
		translateTransition.setDuration(Duration.millis(500));
		translateTransition.setNode(view);
		translateTransition.setCycleCount(1);
		translateTransition.setAutoReverse(false);	
		translateTransition.setFromX(0);
		translateTransition.setFromY(0);
		translateTransition.setToX(- (cardPosition.getX() - deckPosition.getX()));
		translateTransition.setToY(- (cardPosition.getY() - deckPosition.getY()));	
		translateTransition.setOnFinished(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				game.playCard(game.getPlayer().playCard(card), newWishColor);
			}
		});

		translateTransition.play();
	}

	public void moveAICardToDeadDeck(AI ai, Card card, Color newWishColor)
	{
		ObservableList<Node> nodes = mainPane.getChildren();
		Iterator<Node> iterator = nodes.iterator();
		ArrayList<Node> possibleNodes = new ArrayList<Node>();
		while(iterator.hasNext()) 
		{
			Node current = iterator.next();
			if(current.getId().equals("ai"))
			{
				possibleNodes.add(current);				
			}
		}		
		
		
		Random random = new Random();
		int viewNumber = random.nextInt(possibleNodes.size());	

		ImageView view = (ImageView)possibleNodes.get(viewNumber);		
		view.setImage(new Image("images/" + card.getType() + "-" + card.getColor() + ".png"));	

		Point2D cardPosition = view.localToScene(Point2D.ZERO);
		Point2D deckPosition = iconLastCard.localToScene(Point2D.ZERO);		

		System.out.println(view.localToScene(Point2D.ZERO));
		//TODO cardPosition is always 0, 0		
		
		TranslateTransition translateTransition = new TranslateTransition();
		translateTransition.setDuration(Duration.millis(500));
		translateTransition.setNode(view);
		translateTransition.setCycleCount(1);
		translateTransition.setAutoReverse(false);	
		translateTransition.setFromX(0);
		translateTransition.setFromY(0);
		translateTransition.setToX((deckPosition.getX() - cardPosition.getX()));
		translateTransition.setToY((deckPosition.getY() - cardPosition.getY()));	
		translateTransition.setOnFinished(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				game.playCard(ai.playCard(card), newWishColor);
			}
		});

		translateTransition.play();	}

	public void setPlayerDeck(ArrayList<Card> deck)
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
		
		int counter = 1;
		
		for(Card currentCard : deck)
		{
			ImageView current = createCard(currentCard, true);
			
			current.setId("player");
			mainPane.getChildren().add(current);
			AnchorPane.setBottomAnchor(current, 30.0);
			AnchorPane.setLeftAnchor(current, 100.0 + (counter * (57 + 14)));	
			
			//TODO if larger then maxWidth
			counter++;
		}
	}

	public void setValidPlayerDeck(ArrayList<Card> deck, ArrayList<Card> validDeck)
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
			AnchorPane.setBottomAnchor(current, 30.0);
			AnchorPane.setLeftAnchor(current, 100.0 + (counter * (57 + 14)));			
			
			//TODO if larger then maxWidth
			counter++;
		}
	}

	// TODO other AIs
	public void setAI1Deck(ArrayList<Card> deck)
	{
		ObservableList<Node> nodes = mainPane.getChildren();
		Iterator<Node> iterator = nodes.iterator();
		while(iterator.hasNext()) 
		{
			if(iterator.next().getId().equals("ai"))
			{
				iterator.remove();
			}
		}
		
		int counter = 1;
		
		for(Card currentCard : deck)
		{
			ImageView current = createBackCard();
			
			current.setId("ai");
			mainPane.getChildren().add(current);
			AnchorPane.setTopAnchor(current, 30.0);
			AnchorPane.setLeftAnchor(current, 100.0 + (counter * (57 + 14)));	
			
			//TODO if larger then maxWidth
			counter++;
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