package achievements;

import java.io.Serializable;
/**
 * Repr�sentiert ein Achievement
 * @author Robert
 *
 */
@SuppressWarnings("serial")
public class Achievement implements Serializable
{
	private String name;
	private String description;
	private String iconLocked;
	private String iconUnlocked;
	private Status status;
	private boolean incremental;
	private int startValue;
	private int endValue;
	private int currentValue;
	
	/**
	 * Enum f�r den Status des Achievements
	 * @author Robert
	 */
	public enum Status
	{
		//Hidden = Locked --> aber nur Name und Platzhalter-Icon wird angezeigt	
		HIDDEN, LOCKED, UNLOCKED
	}

	/**
	 * Konstruktor f�r ein normales Achievement
	 * @param name String - Nam
	 * @param description String - Beschreibungstext
	 * @param iconLocked String - Pfad zum Icon solange das Achievement noch nicht freigeschaltet wurde
	 * @param iconUnlocked String - Pfad zum Icon, wenn das Achievement freigeschaltet wurde
	 * @param status Status - Status
	 */	
	public Achievement(String name, String description, String iconLocked, String iconUnlocked, Status status)
	{	
		this.name = name;
		this.description = description;
		this.iconLocked = iconLocked;
		this.iconUnlocked = iconUnlocked;
		this.status = status;
		this.incremental = false;
	}
	
	/**
	 * Konstruktor f�r ein inkrementierbares Achievement
	 * @param name String - Nam
	 * @param description String - Beschreibungstext
	 * @param iconLocked String - Pfad zum Icon solange das Achievement noch nicht freigeschaltet wurde
	 * @param iconUnlocked String - Pfad zum Icon, wenn das Achievement freigeschaltet wurde
	 * @param status Status - Status
	 * @param startValue int - Startwert
	 * @param endValue int - Endwert (Wenn erreicht oder �berschritten wird Achievement freigeschaltet)
	 * @param currentValue - aktuller Wert
	 */
	public Achievement(String name, String description, String iconLocked, String iconUnlocked, Status status, int startValue, int endValue, int currentValue)
	{	
		this.name = name;
		this.description = description;
		this.iconLocked = iconLocked;
		this.iconUnlocked = iconUnlocked;
		this.status = status;
		this.incremental = true;
		this.startValue = startValue;
		this.endValue = endValue;
		this.currentValue = currentValue;
	}

	/**
	 * Gibt den Namen zur�ck
	 * @return String - Name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gibt den Beschreibungstext zur�ck
	 * @return String - Beschreibung
	 */
	public String getDescription()
	{
		return description;		
	}

	/**
	 * Gibt den Pfad zum Icon f�r das nicht freigeschaltete Achievement zur�ck
	 * @return String - Pfad zu "iconLocked"
	 */
	public String getIconLocked()
	{
		return iconLocked;
	}
	
	/**
	 * Gibt den Pfad zum Icon f�r das  freigeschaltete Achievement zur�ck
	 * @return String - Pfad zu "iconUnlocked"
	 */
	public String getIconUnlocked()
	{
		return iconUnlocked;
	}

	/**
	 * Gibt den Status zur�ck
	 * @return Status - Status
	 */
	public Status getStatus()
	{
		return status;
	}

	/**
	 * gibt zur�ck, ob das Achievemnt ein inkrementierbares Achievement ist
	 * @return boolean - ist inkrementierbar
	 */
	public boolean isIncremental()
	{
		return incremental;
	}
	
	/**
	 * gibt den Startwert zur�ck
	 * @return int - Startwert
	 */
	public int getStartValue()
	{
		return startValue;
	}

	/**
	 * gibt den Endwert zur�ck
	 * @return int - Endwert
	 */
	public int getEndValue()
	{
		return endValue;
	}

	/**
	 * gibt den aktuellen Wert zur�ck
	 * @return int - aktueller Wert
	 */
	public int getCurrentValue()
	{
		return currentValue;
	}
	
	/**
	 * setzt den aktuellen Wert
	 * @param value int - aktueller Wert
	 */
	public void setCurrentValue(int value)
	{
		this.currentValue = value;
	}
	
	/**
	 * erh�ht den aktuellen Wert
	 * @param value int - Einheiten, um die der aktuelle Wert erh�ht werden soll
	 */
	public void incrementCurrentValue(int value)
	{
		if(!status.equals(Status.UNLOCKED))
		{
			if(this.currentValue < endValue)
			{
				this.currentValue += value;
			}		
		}
	}

	/**
	 * setzt den Status
	 * @param status Status - Status
	 */
	public void setStatus(Status status)
	{
		this.status = status;
	}
	
	/**
	 * toString()-Implementierung (Ausgabe f�r Debugging)
	 * @return String - Ausgabe
	 */
	@Override
	public String toString()
	{
		return "name: " + name + ", description: " + description + ", iconLocked: " + iconLocked + ", iconUnlocked : " + iconUnlocked + ", status: " + status + ", incremental: " + incremental + ", startValue: " + startValue + ", endValue: "
				+ endValue + ", currentValue:" + currentValue + "]";
	}	
}