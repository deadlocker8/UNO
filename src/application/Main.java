package application;

import achievements.Achievement.Status;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application
{
	@Override
	public void start(Stage stage)
	{
		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainGUI.fxml"));
			Parent root = (Parent)loader.load();

			Scene scene = new Scene(root, 800, 650, false, SceneAntialiasing.BALANCED);

			stage.setResizable(true);
			stage.setTitle("UNO");
			stage.setScene(scene);		
			stage.setResizable(false);
			
			Controller controller = (Controller)loader.getController();
			controller.setStage(stage);			
			controller.init();			
			
			stage.setOnCloseRequest(new EventHandler<WindowEvent>()
			{				
				@Override
				public void handle(WindowEvent event)
				{
					try
					{
						if(controller.handler.getAchievements().get(3).getStatus().equals(Status.LOCKED))
						{
							controller.handler.resetAchievement(3);
						}
						if(controller.handler.getAchievements().get(4).getStatus().equals(Status.LOCKED))
						{
							controller.handler.resetAchievement(4);
						}	
						controller.handler.saveAndLoad();
					}
					catch(Exception e)
					{						
					}					
				}
			});
			
			
			
			
			stage.getIcons().add(new Image("images/icon.png"));
			stage.show();		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}