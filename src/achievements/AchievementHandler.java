package achievements;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import notification.Notification;
import sounds.Sound;
import achievements.Achievement.Status;

/**
 * Verwaltet die Achievements
 * @author Robert
 *
 */
public class AchievementHandler
{
	private ArrayList<Achievement> achievements;
	private Notification notification;
	private Stage owner;
	private String path;

	//verschiedene Konstanten, die lediglich für die Erzeugung der ListView verwendet werden
	private final int iconSize = 50;
	private final String colorUnlocked = "#FFFFFF";
	private final int anchorPanePadding = 15;
	private final int anchorPaneBackgroundRadius = 10;

	/**
	 * Konstruktor
	 * @param owner Stage - Owner
	 */
	public AchievementHandler(Stage owner)
	{
		achievements = new ArrayList<Achievement>();
		notification = new Notification();
		this.owner = owner;
	}
	
	/**
	 * setzt den Pfad zum Lesen und Speichern der Achievementsdatei
	 * @param path
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * fügt ein Achievement zur Liste hinzu
	 * @param achievement
	 */
	public void addAchievement(Achievement achievement)
	{
		achievements.add(achievement);
	}

	/**
	 * Löscht die gesamte Achievementliste
	 */
	public void clearAchievementsList()
	{
		achievements.clear();
	}

	/**
	 * gibt die Achievementliste zurück
	 * @return ArrayList<Achievement> - Achievements
	 */
	public ArrayList<Achievement> getAchievements()
	{
		return achievements;
	}

	/**
	 * entfernt das Achievement an der gegebenen Position aus der Liste
	 * @param position int - Position, die gelöscht werden soll
	 */
	public void removeAchievement(int position)
	{
		achievements.remove(position);
	}

	/**
	 * gibt die Anzahl aller Achievements zurück
	 * @return int - Anzahl der Achievements
	 */
	public int getNumberofAchievements()
	{
		return achievements.size();
	}

	/**
	 * gibt die Anzahl der freigeschalteten Achievements zurück
	 * @return int - Anzahl der freigeschalteten Achievements
	 */
	public int getNumberOfUnlockedAchievements()
	{
		int counter = 0;
		for(int i = 0; i < achievements.size(); i++)
		{
			if(achievements.get(i).getStatus().equals(Status.UNLOCKED))
			{
				counter++;
			}
		}
		return counter;
	}

	/**
	 * gibt die Anzahl der nicht freigeschalteten Achievements zurück
	 * @return int - Anzahl der nicht freigeschalteten Achievements
	 */
	public int getNumberOfLockedAchievements()
	{
		int counter = 0;
		for(int i = 0; i < achievements.size(); i++)
		{
			Status status = achievements.get(i).getStatus();
			if(status.equals(Status.LOCKED) || status.equals(Status.HIDDEN))
			{
				counter++;
			}
		}
		return counter;
	}

	/**
	 * gibt eine AnchorPane zurück, die eine ListView beinhaltet
	 * diese stellt die Achievements mit Hilfe der AchievementCell dar
	 * @return
	 */
	public AnchorPane getAchievementList()
	{
		AnchorPane list = new AnchorPane();

		ListView<Achievement> listView = new ListView<>();
		listView.setFocusTraversable(false);
		Label placeHolder = new Label("Keine Achievements verfügbar");
		placeHolder.setStyle("-fx-font-size: 16px;");
		listView.setPlaceholder(placeHolder);
		list.getChildren().add(listView);
		listView.getItems().addAll(achievements);
		listView.setFixedCellSize(105);		
		listView.setStyle("-fx-background-color: transparent;");

		listView.setCellFactory(new Callback<ListView<Achievement>, ListCell<Achievement>>()
		{
			@Override
			public ListCell<Achievement> call(ListView<Achievement> arg0)
			{
				return new AchievementCell();
			}
		});

		AnchorPane.setTopAnchor(listView, 0.0);
		AnchorPane.setLeftAnchor(listView, 0.0);
		AnchorPane.setRightAnchor(listView, 0.0);
		AnchorPane.setBottomAnchor(listView, 0.0);
		
		return list;
	}

	/**
	 * gibt eine AnchorPane zurück, die die Zusammenfassung aller Achievements anzeigt
	 * (Bsp.: 5/10 Achievements freigeschaltet = 50%)
	 * @return
	 */
	public AnchorPane getSummary()
	{
		AnchorPane summary = new AnchorPane();
		summary.setPrefHeight(50);

		HBox hbox = new HBox();
		VBox vbox = new VBox();

		Image image = new Image("notification/summary.png");

		ImageView view = new ImageView(image);
		view.setFitWidth(iconSize);
		view.setFitHeight(iconSize);

		hbox.getChildren().add(view);

		Label labelName = new Label(getNumberOfUnlockedAchievements() + " von " + getNumberofAchievements() + " Achievements freigeschaltet");
		labelName.setStyle("-fx-font-weight: bold; -fx-font-size: 17px;");
		labelName.prefWidthProperty().bind(vbox.widthProperty());

		vbox.getChildren().add(labelName);

		ProgressBar progressbar = new ProgressBar();
		progressbar.setProgress(1.0 * getNumberOfUnlockedAchievements() / getNumberofAchievements());
		progressbar.prefWidthProperty().bind(vbox.widthProperty().multiply(0.68));

		Label labelProgress = new Label((int)((1.0 * getNumberOfUnlockedAchievements() / getNumberofAchievements()) * 100) + " %");
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
		VBox.setMargin(progress, new Insets(10, 0, 0, 0));

		summary.setStyle("-fx-padding:" + anchorPanePadding + "; -fx-background-radius: " + anchorPaneBackgroundRadius + "; -fx-background-color: " + colorUnlocked + ";");

		hbox.getChildren().add(vbox);
		HBox.setHgrow(vbox, Priority.ALWAYS);
		HBox.setMargin(vbox, new Insets(0, 0, 0, 25));
		hbox.setAlignment(Pos.CENTER);

		summary.setMinHeight(95);
		summary.getChildren().add(hbox);
		AnchorPane.setTopAnchor(hbox, 0.0);
		AnchorPane.setLeftAnchor(hbox, 0.0);
		AnchorPane.setRightAnchor(hbox, 0.0);
		AnchorPane.setBottomAnchor(hbox, 0.0);

		return summary;
	}

	/**
	 * überprüft alle inkrementierbaren Achievements, ob sie ihren Endwert erreicht bzw. überschritten haben
	 * Wenn ja, wird der Status auf UNLOCKED gesetzt und eine Notification am unteren rechten Bildschrimrand geöffnet,
	 * sowie ein Sound abgespielt.
	 */
	public void checkAllIncrementalAchievements()
	{
		for(int i = 0; i < achievements.size(); i++)
		{
			Achievement current = achievements.get(i);

			if(current.isIncremental())
			{
				if(current.getStatus() != Status.UNLOCKED)
				{
					if(current.getCurrentValue() >= current.getEndValue())
					{
						current.setStatus(Status.UNLOCKED);
	
						Sound.playSound("unlocked");
	
						Image image;
						if(current.getIconUnlocked() != null)
						{
							File file = new File(current.getIconUnlocked());
							if(file.exists())
							{
								image = new Image(file.toURI().toString());
							}
							else
							{
								image = new Image("notification/trophyUnlocked.png");
							}
						}
						else
						{
							image = new Image("notification/trophyUnlocked.png");
						}
	
						ImageView view = new ImageView(image);
						view.setFitWidth(40);
						view.setFitHeight(40);
	
						notification.setTitle("Achievement freigeschaltet");
						notification.setDescription(current.getDescription());
						notification.setIcon(image);
						notification.setOwner(owner);
						if(notification.isOpen())
						{
							notification.close();
						}
						notification.show();
					}
				}
			}
		}
	}

	/**
	 * Erhöht das angegebene Achievement um die angegebene Anzahl von Einheiten
	 * @param position int - Position des Achievements
	 * @param value - Anzahl der Einheiten um die das Achievement erhöht werden soll
	 * @throws NotIncrementalAchievementException - wenn as Achievement nicht inkrementierbar ist
	 * @throws IndexOutOfBoundsException - falls die angegebene Position nicht im Bereich der Liste liegt
	 */
	public void incrementAchievement(int position, int value) throws NotIncrementalAchievementException, IndexOutOfBoundsException
	{
		Achievement current = achievements.get(position);

		if(current.isIncremental())
		{
			current.incrementCurrentValue(value);
		}
		else
		{
			throw new NotIncrementalAchievementException();
		}
	}

	/**
	 * schaltet das angegebene Achievement frei
	 * (Notification erscheint am unteren rechten Bildschrimrand und ein Sound wird abgespielt.
	 * @param position int - Position
	 */
	public void unlockAchievement(int position)
	{
		Achievement current = achievements.get(position);

		if(!current.isIncremental() && current.getStatus() != Status.UNLOCKED)
		{
			current.setStatus(Status.UNLOCKED);			

			Sound.playSound("unlocked");

			Image image;
			if(current.getIconUnlocked() != null)
			{		
				File file = new File(current.getIconUnlocked());
				if(file.exists())
				{
					image = new Image(file.toURI().toString());
				}
				else
				{
					image = new Image("notification/trophyUnlocked.png");
				}
			}
			else
			{
				image = new Image("notification/trophyUnlocked.png");			
			}
			
			notification.setTitle("Achievement freigeschaltet!");
			notification.setDescription(current.getName());
			notification.setIcon(image);
			notification.setOwner(owner);
			if(notification.isOpen())
			{
				notification.close();
			}
			notification.show();
		}
	}

	/**
	 * Setzt das angegebene Achievement zurück
	 * --> Status wird auf LOCKED gesetzt
	 * --> wenn inkrementierbar, dann wird er aktuelle Wert auf den Startwert gesetzt	 * 
	 * @param position
	 */
	public void resetAchievement(int position)
	{
		Achievement current = achievements.get(position);

		if(current.isIncremental())
		{
			current.setCurrentValue(current.getStartValue());
		}

		if(current.getStatus().equals(Status.UNLOCKED))
		{
			current.setStatus(Status.LOCKED);
		}

		achievements.set(position, current);
	}

	/**
	 * setzt alle Achievements zurück
	 */
	public void resetAllAchievements()
	{
		for(int i = 0; i < achievements.size(); i++)
		{
			resetAchievement(i);
		}
	}

	/**
	 * speichert die Achievements in eine Datei ab
	 * (Speicherort wird festgelegt mit setPath())
	 * @throws IllegalArgumentException - falls kein Pfad gesetzt wurde
	 * @throws Exception - falls beim Speichern ein Fehler auftritt
	 */
	public void saveAchievements() throws IllegalArgumentException, Exception
	{
		if(path != null)
		{			
			FileOutputStream fout = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fout);

			for(int i = 0; i < achievements.size(); i++)
			{
				oos.writeObject(achievements.get(i));
			}
			oos.close();			
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * liest die Achievements aus einer Datei ein
	 * (Speicherort wird festgelegt mit setPath())
	 * @throws IllegalArgumentException - falls kein Pfad gesetzt wurde
	 * @throws Exception - falls beim Lesen ein Fehler auftritt
	 */
	public void loadAchievements() throws IllegalArgumentException, Exception
	{
		if(path != null)
		{			
			FileInputStream fin = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fin);

			achievements = new ArrayList<Achievement>();

			while(true)
			{
				try
				{
					Achievement current = (Achievement)ois.readObject();		
					achievements.add(current);
				}
				catch(EOFException e)
				{
					ois.close();
					break;
				}				
			}					
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}

	/**
	 * speichert und lädt die Achievements
	 * (nützlich nach Veränderung eines Achievements während des Spiels)
	 * @throws IllegalArgumentException - falls kein Pfad gesetzt wurde
	 * @throws Exception - falls beim Lesen ein Fehler auftritt
	 */
	public void saveAndLoad() throws IllegalArgumentException, Exception
	{
		saveAchievements();
		loadAchievements();
	}
}