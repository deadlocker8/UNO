package logic;

public class Card
{
	private CardType type;
	private Color color;
	private int value;	
	
	public Card(CardType type, Color color, int value)
	{
		this.type = type;
		this.color = color;
		this.value = value;
	}

	public CardType getType()
	{
		return type;
	}
	
	public String getTypeBeautyfied()
	{
		switch(type)
		{
			case ZERO:		return "0";
			case ONE:		return "1";
			case TWO:		return "2";
			case THREE:		return "3";
			case FOUR:		return "4";	
			case FIVE:		return "5";	
			case SIX:		return "6";
			case SEVEN:		return "7";
			case EIGHT:		return "8";
			case NINE:		return "9";
			case REVERSE:	return "<-->";
			case SKIP:		return "X";
			case DRAW_TWO:	return "+2";
			case WILD:		return "*";
			case DRAW_FOUR:	return "+4";
			default:		return "";	
		}
	}

	public Color getColor()
	{
		return color;
	}
	
	public String getColorAsHex()
	{
		switch(color)
		{
			case YELLOW:	return "#FFFF00";
			case RED:		return "#FF0000";
			case BLUE:		return "#0000FF";
			case GREEN:		return "#00FF00";	
			case BLACK:		return "#000000";	
			default:		return "#000000";	
		}
	}

	public int getValue()
	{
		return value;
	}
	
	public boolean equals(Card other)
	{
		return type.equals(other.getType()) && color.equals(other.getColor());
	}		

	@Override
	public String toString()
	{
		return "(" + type + ", " + color + ", value=" + value + ")\n";
	}	
}