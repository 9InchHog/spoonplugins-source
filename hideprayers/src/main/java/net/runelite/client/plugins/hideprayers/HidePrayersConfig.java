package net.runelite.client.plugins.hideprayers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.hideprayers.util.*;

@ConfigGroup("spoonprayerhider")
public interface HidePrayersConfig extends Config {
	@ConfigSection(
		name = "Custom Prayers",
		description = "",
		position = 0,
		closedByDefault = true
	)
	String customSection = "Custom Prayers";

	@ConfigSection(
		name = "PvM Presets",
		description = "",
		position = 1,
		closedByDefault = true
	)
	String pvmSection = "PvM Prayers";

	@ConfigSection(
		name = "PvP Presets",
		description = "",
		position = 2,
		closedByDefault = true
	)
	String pvpSection = "PvP Prayers";

	@ConfigItem(
		position = 0,
		keyName = "showindividualprayers",
		name = "Hide Custom Prayers",
		description = "Hide/Show Prayers.",
		section = customSection
	)
	default boolean showindividualprayers()
	{
		return false;
	}

	@ConfigItem(
		position = 0,
		name = "Show Prayers List",
		keyName = "prayerList",
		description = "Shows the prayers that you type into this box",
		section = customSection
	)
	default String prayerList() {
		return "";
	}

// ----------------------------------------------------------- //

	@ConfigItem(
		position = 0,
		keyName = "getarmadylprayers",
		name = "Enable Armadyl Prayers",
		description = "Shows prayers for Armadyl",
		section = pvmSection
	)
	default boolean getarmadylprayers()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "armadyl",
		name = "Armadyl",
		description = "Shows prayers for Armadyl",
		section = pvmSection,
		hidden = true,
		unhide = "getarmadylprayers"
	)
	default Armadyl armadyl()
	{
		return Armadyl.DISABLED;
	}

	@ConfigItem(
		position = 2,
		keyName = "getbarrowsprayers",
		name = "Enable Barrows Prayers",
		description = "Shows prayers for Barrows",
		section = pvmSection
	)
	default boolean getbarrowsprayers()
	{
		return false;
	}

	@ConfigItem(
		position = 3,
		keyName = "barrows",
		name = "Barrows",
		description = "Shows prayers for Barrows",
		section = pvmSection,
		hidden = true,
		unhide = "getbarrowsprayers"
	)
	default Barrows barrows()
	{
		return Barrows.DISABLED;
	}

	@ConfigItem(
		position = 4,
		keyName = "getbandosprayers",
		name = "Enable Bandos Prayers",
		description = "Shows prayers for Bandos",
		section = pvmSection
	)
	default boolean getbandosprayers()
	{
		return false;
	}

	@ConfigItem(
		position = 5,
		keyName = "bandos",
		name = "Bandos",
		description = "Shows prayers for Bandos",
		section = pvmSection,
		hidden = true,
		unhide = "getbandosprayers"
	)
	default Bandos bandos()
	{
		return Bandos.DISABLED;
	}

	@ConfigItem(
		position = 6,
		keyName = "getcerberusprayers",
		name = "Enable Cerberus Prayers",
		description = "Shows prayers for Cerberus",
		section = pvmSection
	)
	default boolean getcerberusprayers()
	{
		return false;
	}

	@ConfigItem(
		position = 7,
		keyName = "cerberus",
		name = "Cerberus",
		description = "Shows prayers for Cerberus",
		section = pvmSection,
		hidden = true,
		unhide = "getcerberusprayers"
	)
	default Cerberus cerberus()
	{
		return Cerberus.DISABLED;
	}

	@ConfigItem(
		position = 8,
		keyName = "getsaradominprayers",
		name = "Enable Saradomin Prayers",
		description = "Shows prayers for Saradomin",
		section = pvmSection
	)
	default boolean getsaradominprayers()
	{
		return false;
	}

	@ConfigItem(
		position = 9,
		keyName = "saradomin",
		name = "Saradomin",
		description = "Shows prayers for Saradomin",
		section = pvmSection,
		hidden = true,
		unhide = "getsaradominprayers"
	)
	default Saradomin saradomin()
	{
		return Saradomin.DISABLED;
	}

	@ConfigItem(
		position = 10,
		keyName = "getvorkathprayers",
		name = "Enable Vorkath Prayers",
		description = "Shows prayers for Vorkath",
		section = pvmSection
	)
	default boolean getvorkathprayers()
	{
		return false;
	}

	@ConfigItem(
		position = 11,
		keyName = "vorkath",
		name = "Vorkath",
		description = "Shows prayers for Vorkath",
		section = pvmSection,
		hidden = true,
		unhide = "getvorkathprayers"
	)
	default Vorkath vorkath()
	{
		return Vorkath.DISABLED;
	}

	@ConfigItem(
		position = 12,
		keyName = "getzamorakprayers",
		name = "Enable Zamorak Prayers",
		description = "Shows prayers for Zamorak",
		section = pvmSection
	)
	default boolean getzamorakprayers()
	{
		return false;
	}

	@ConfigItem(
		position = 13,
		keyName = "zamorak",
		name = "Zamorak",
		description = "Shows prayers for Zamorak",
		section = pvmSection,
		hidden = true,
		unhide = "getzamorakprayers"
	)
	default Zamorak zamorak()
	{
		return Zamorak.DISABLED;
	}

	@ConfigItem(
		position = 14,
		keyName = "getzulrahprayers",
		name = "Enable Zulrah Prayers",
		description = "Shows prayers for Zulrah",
		section = pvmSection
	)
	default boolean getzulrahprayers()
	{
		return false;
	}

	@ConfigItem(
		position = 15,
		keyName = "zulrah",
		name = "Zulrah",
		description = "Shows prayers for Zulrah",
		section = pvmSection,
		hidden = true,
		unhide = "getzulrahprayers"
	)
	default Zulrah zulrah()
	{
		return Zulrah.DISABLED;
	}

// ----------------------------------------------------------- //

	@ConfigItem(
		position = 0,
		keyName = "getpvpprayers",
		name = "Enable PVP Prayers",
		description = "Shows prayers based on prayer build",
		section = pvpSection
	)
	default boolean getpvpprayers()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "pvpprayers",
		name = "PVP Prayers",
		description = "Shows prayers based on prayer build",
		section = pvpSection
	)
	default PVPPrayers pvpprayers()
	{
		return PVPPrayers.DISABLED;
	}

	@ConfigItem(
		position = 2,
		keyName = "HideRapidHealRestore",
		name = "Hide Rapid Heal and Rapid Restore",
		description = "Hides the Rapid Heal and Rapid Restore prayers",
		section = pvpSection
	)
	default boolean HideRapidHealRestore()
	{
		return false;
	}
}
