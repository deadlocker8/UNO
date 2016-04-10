package application;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import logic.Card;
import logic.Game;

public class Controller
{
	@FXML private Label labelLastCard;
	@FXML private Rectangle iconLastCard;
	@FXML private HBox hboxPlayerDeck;
	@FXML private StackPane iconDeck;
	
	public Game game;
	
	public Stage stage;
	public Image icon = new Image("images/icon.png");
	private final ResourceBundle bundle = ResourceBundle.getBundle("application/", Locale.GERMANY);

	public void init()
	{
		iconLastCard.setArcWidth(20);
		iconLastCard.setArcHeight(20);	
		
		iconDeck = createBackCard();
		
		game = new Game(this, 3);
		game.newGame(5);
	}

	public void setStage(Stage stage)
	{
		this.stage = stage;
	}
	
	public void setLastCard(String color, String text)
	{
		iconLastCard.setFill(Color.web(color));
		labelLastCard.setText(text);
	}
	
	private StackPane createBackCard()
	{		
		Rectangle newCard = new Rectangle(57.0, 90.0);		
		newCard.setStroke(Color.BLACK);
		newCard.setStrokeWidth(2.0);
		newCard.setArcWidth(20);
		newCard.setArcHeight(20);
		
		ImageView imageView = new ImageView(new Image("images/card-back.png"));	
		imageView.setFitHeight(90.0);
		imageView.setFitWidth(57.0);
		
		StackPane pane = new StackPane();
		pane.getChildren().add(newCard);
		pane.getChildren().add(imageView);
		
		return pane;
	}
	
	private StackPane createCard(Card card, boolean valid)
	{		
		Rectangle newCard = new Rectangle(57.0, 90.0);		
		newCard.setStroke(Color.BLACK);
		newCard.setStrokeWidth(2.0);
		newCard.setArcWidth(20);
		newCard.setArcHeight(20);
		
		ImageView imageView = new ImageView(new Image("images/" + card.getType() + "-" + card.getColor() + ".png"));
		imageView.setFitHeight(90.0);
		imageView.setFitWidth(57.0);	
		
		StackPane pane = new StackPane();
		pane.getChildren().add(newCard);
		pane.getChildren().add(imageView);
		
		return pane;
	}
		
	public void setPlayerDeck(ArrayList<Card> deck)
	{
		hboxPlayerDeck.getChildren().clear();		
		
		for(Card currentCard : deck)
		{
			hboxPlayerDeck.getChildren().add(createCard(currentCard, true));
			HBox.setMargin(hboxPlayerDeck.getChildren().get(hboxPlayerDeck.getChildren().size() - 1), new Insets(0,15,0,0));			
		}
	}	
	
	public void setValidPlayerDeck(ArrayList<Card> deck, ArrayList<Card> validDeck)
	{	
		for(Card currentCard : deck)
		{
			if(validDeck.contains(currentCard))
			{
				hboxPlayerDeck.getChildren().add(createCard(currentCard, true));
			}		
			else
			{
				hboxPlayerDeck.getChildren().add(createCard(currentCard, false));
			}
			
			HBox.setMargin(hboxPlayerDeck.getChildren().get(hboxPlayerDeck.getChildren().size() - 1), new Insets(0,15,0,0));				
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