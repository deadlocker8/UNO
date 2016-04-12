package application;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import logic.Color;

public class ColorChooserController
{

	@FXML private Rectangle rectYellow;
	@FXML private Rectangle rectRed;
	@FXML private Rectangle rectBlue;
	@FXML private Rectangle rectGreen;

	public void init(Stage stage, Controller controller)
	{
		rectYellow.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				controller.chosenWishColor = Color.YELLOW;
				stage.close();
			}
		});

		rectRed.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				controller.chosenWishColor = Color.RED;
				stage.close();
			}
		});

		rectBlue.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				controller.chosenWishColor = Color.BLUE;
				stage.close();
			}
		});

		rectGreen.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				controller.chosenWishColor = Color.GREEN;
				stage.close();
			}
		});
		
		//TODO prevent closing without choosing a color --> but cancel button
	}
}