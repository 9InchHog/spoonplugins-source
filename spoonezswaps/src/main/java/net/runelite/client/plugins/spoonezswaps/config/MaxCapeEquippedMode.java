package net.runelite.client.plugins.spoonezswaps.config;

public enum MaxCapeEquippedMode
{
	OFF("Off"),
	TELE_TO_POH("Tele to POH"),
	CRAFTING_GUILD("Crafting Guild"),
	FISHING_TELEPORTS("Fishing Tele"),
	WARRIORS_GUILD("Warriors' Guild"),
	POH_PORTRALS("POH Portals"),
	OTHER_TELEPORTS("Other Teleports"),
	SPELLBOOK("Spellbook"),
	FEATURES("Features");

	private final String name;

	MaxCapeEquippedMode(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}

