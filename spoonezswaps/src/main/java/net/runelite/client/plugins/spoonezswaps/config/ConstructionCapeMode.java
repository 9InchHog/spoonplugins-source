package net.runelite.client.plugins.spoonezswaps.config;

public enum ConstructionCapeMode
{
	OFF("Off"),
	INVENTORY("Inventory"),
	EQUIPPED("Worn"),
	ALWAYS("Both");

	private final String name;

	ConstructionCapeMode(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
