package net.runelite.client.plugins.spoonezswaps.config;

public enum CraftingCapeMode
{
	OFF("Off"),
	INVENTORY("Inventory"),
	EQUIPPED("Worn"),
	ALWAYS("Both");

	private final String name;

	CraftingCapeMode(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
