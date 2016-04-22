package achievements;

/**
 * Exception, falls auf ein Achievement Methoden angewendet werden,
 * die nur bei einem incrementalAchievement m�glich sind, das aktuelle jedoch kein incrementalAchievement ist
 * @author Robert
 *
 */

@SuppressWarnings("serial")
public class NotIncrementalAchievementException extends Exception
{
	
}