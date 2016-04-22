package achievements;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import achievements.Achievement.Status;

/**
 * eigene ListCell, um die Achievements darzustellen
 * @author Robert
 *
 */
public class AchievementCell extends ListCell<Achievement>
{
	private int iconSize = 50;
	private String colorLocked = "#C6C6C6";
	private String colorUnlocked = "#FFFFFF";
	private int anchorPanePadding = 15;
	private int anchorPaneBackgroundRadius = 10;

	@Override
	protected void updateItem(Achievement item, boolean empty)
	{
		super.updateItem(item, empty);
		
		if(!empty)
		{
			AnchorPane anchorPaneAchievement = new AnchorPane();		

			HBox hbox = new HBox();
			VBox vbox = new VBox();

			if(item.getStatus().equals(Status.HIDDEN))
			{
				//für versteckte Erfolge wird das Standard Platzhaltericon verwendet
				Image image = new Image("notification/trophyLocked.png");

				ImageView view = new ImageView(image);
				view.setFitWidth(iconSize);
				view.setFitHeight(iconSize);

				hbox.getChildren().add(view);

				Label labelName = new Label(item.getName());
				labelName.setStyle("-fx-font-weight: bold; -fx-font-size: 17px;");
				labelName.prefWidthProperty().bind(vbox.widthProperty());

				Label labelDescription = new Label("???");
				labelDescription.setStyle("-fx-font-size: 16px;");
				labelDescription.prefWidthProperty().bind(vbox.widthProperty());

				vbox.getChildren().add(labelName);
				vbox.getChildren().add(labelDescription);
				vbox.setAlignment(Pos.CENTER);
				
				anchorPaneAchievement.setStyle("-fx-background-radius: " + anchorPaneBackgroundRadius + "; -fx-background-color: " + colorLocked + ";");
			}
			else if(item.getStatus().equals(Status.LOCKED))
			{
				Image image;
				
				if(item.getIconLocked() != null)
				{								
					File file = new File(item.getIconLocked());			
					if(file.exists())
					{
						image = new Image(file.toURI().toString());
					}
					else
					{
						//falls der Pfad nicht existiert oder ungültig ist wird das Standardicon genommen
						image = new Image("notification/trophyLocked.png");
					}
				}
				else
				{
					//falls der Pfad nicht existiert oder ungültig ist wird das Standardicon genommen
					image = new Image("notification/trophyLocked.png");
				}

				ImageView view = new ImageView(image);
				view.setFitWidth(iconSize);
				view.setFitHeight(iconSize);

				hbox.getChildren().add(view);

				Label labelName = new Label(item.getName());
				labelName.setStyle("-fx-font-weight: bold; -fx-font-size: 17px;");
				labelName.prefWidthProperty().bind(vbox.widthProperty());

				Label labelDescription = new Label(item.getDescription());
				labelDescription.setStyle("-fx-font-size: 16px;");
				labelDescription.prefWidthProperty().bind(vbox.widthProperty());

				vbox.getChildren().add(labelName);
				vbox.getChildren().add(labelDescription);

				if(item.isIncremental())
				{
					ProgressBar progressbar = new ProgressBar();
					progressbar.setProgress(1.0 * item.getCurrentValue() / item.getEndValue());
					progressbar.prefWidthProperty().bind(vbox.widthProperty().multiply(0.65));
					progressbar.setStyle("-fx-accent: #888888;");

					Label labelProgress = new Label(item.getCurrentValue() + "/" + item.getEndValue());
					labelProgress.setStyle("-fx-font-size: 15px;");

					HBox progress = new HBox();
					progress.getChildren().add(progressbar);
					progress.getChildren().add(labelProgress);
					HBox.setHgrow(progressbar, Priority.ALWAYS);
					HBox.setMargin(labelProgress, new Insets(0, 0, 0, 10));
					progress.setAlignment(Pos.CENTER_LEFT);
					progress.setMinWidth(300);

					vbox.getChildren().add(progress);

					progress.prefWidthProperty().bind(vbox.widthProperty());
					VBox.setMargin(progress, new Insets(3, 0, 0, 0));
				}
				else
				{
					vbox.setAlignment(Pos.CENTER);
				}
				anchorPaneAchievement.setStyle("-fx-background-radius: " + anchorPaneBackgroundRadius + "; -fx-background-color:" + colorLocked + ";");
			}
			else
			{
				Image image;
				if(item.getIconUnlocked() != null)
				{	
					File file = new File(item.getIconUnlocked());			
					if(file.exists())
					{
						image = new Image(file.toURI().toString());
					}
					else
					{
						//falls der Pfad nicht existiert oder ungültig ist wird das Standardicon genommen
						image = new Image("notification/trophyUnlocked.png");
					}
				}
				else
				{
					//falls der Pfad nicht existiert oder ungültig ist wird das Standardicon genommen
					image = new Image("notification/trophyUnlocked.png");
				}

				ImageView view = new ImageView(image);
				view.setFitWidth(iconSize);
				view.setFitHeight(iconSize);

				hbox.getChildren().add(view);

				Label labelName = new Label(item.getName());
				labelName.setStyle("-fx-font-weight: bold; -fx-font-size: 17px;");
				labelName.prefWidthProperty().bind(vbox.widthProperty());

				Label labelDescription = new Label(item.getDescription());
				labelDescription.setStyle("-fx-font-size: 16px;");
				labelDescription.prefWidthProperty().bind(vbox.widthProperty());

				vbox.getChildren().add(labelName);
				vbox.getChildren().add(labelDescription);

				if(item.isIncremental())
				{
					ProgressBar progressbar = new ProgressBar();
					progressbar.setProgress(1.0 * item.getCurrentValue() / item.getEndValue());
					progressbar.prefWidthProperty().bind(vbox.widthProperty().multiply(0.68));

					Label labelProgress = new Label(item.getCurrentValue() + "/" + item.getEndValue());
					labelProgress.setStyle("-fx-font-size: 15px;");
					labelProgress.setAlignment(Pos.CENTER);

					HBox progress = new HBox();
					progress.getChildren().add(progressbar);
					progress.getChildren().add(labelProgress);
					HBox.setHgrow(progressbar, Priority.ALWAYS);
					HBox.setMargin(labelProgress, new Insets(0, 0, 0, 10));
					progress.setAlignment(Pos.CENTER_LEFT);
					progress.setMinWidth(300);

					vbox.getChildren().add(progress);

					progress.prefWidthProperty().bind(vbox.widthProperty());
					VBox.setMargin(progress, new Insets(3, 0, 0, 0));
				}
				else
				{
					vbox.setAlignment(Pos.CENTER);
				}
				anchorPaneAchievement.setStyle("-fx-background-radius: " + anchorPaneBackgroundRadius + "; -fx-background-color: " + colorUnlocked + ";");			
			}

			hbox.getChildren().add(vbox);
			HBox.setHgrow(vbox, Priority.ALWAYS);
			HBox.setMargin(vbox, new Insets(0, 0, 0, 25));
			hbox.setAlignment(Pos.CENTER);
			
			anchorPaneAchievement.getChildren().add(hbox);
			AnchorPane.setTopAnchor(hbox, 15.0);
			AnchorPane.setLeftAnchor(hbox, 15.0);
			AnchorPane.setRightAnchor(hbox, 15.0);
			AnchorPane.setBottomAnchor(hbox, 15.0);			
			
			vbox.setMinHeight(65);
			setStyle("-fx-background-color: transparent; -fx-padding: " + anchorPanePadding + " 10px " + anchorPanePadding +" 0px");						
			setGraphic(anchorPaneAchievement);		
		}
		else
		{
			setStyle("-fx-background-color: transparent;");
			setGraphic(null);
		}
	}
}