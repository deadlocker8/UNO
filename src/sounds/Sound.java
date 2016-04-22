package sounds;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Klasse zum Laden und Abspielen einer MP3-Datei
 * @author Robert
 *
 */
public class Sound
{
	/**
	 * Lädt die angegebene MP3-Datei und spielt sie ab
	 * @param file String - Dateiname	 * 
	 */
	public static void playSound(String file)
	{		
		try
		{
			String path = Sound.class.getResource(file + ".mp3").toURI().toURL().toString();
			Media sound = new Media(path);
			MediaPlayer mediaPlayer = new MediaPlayer(sound);
			mediaPlayer.setAutoPlay(true);		
		}
		catch(MalformedURLException | URISyntaxException e)
		{
			e.printStackTrace();
		} 
	}
}