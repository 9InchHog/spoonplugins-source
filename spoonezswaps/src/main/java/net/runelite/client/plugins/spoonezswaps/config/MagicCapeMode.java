package net.runelite.client.plugins.spoonezswaps.config;

public enum MagicCapeMode
{
	OFF("Off"),
	INVENTORY("Inventory"),
	EQUIPPED("Worn"),
	ALWAYS("Both");

	private final String name;

	MagicCapeMode(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
