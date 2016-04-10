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

	public Color getColor()
	{
		return color;
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