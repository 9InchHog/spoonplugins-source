package net.runelite.client.plugins.spoongroundmarkers;

import lombok.RequiredArgsConstructor;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup(sGroundMarkerConfig.GROUND_MARKER_CONFIG_GROUP)
public interface sGroundMarkerConfig extends Config {
	String GROUND_MARKER_CONFIG_GROUP = "groundMarker";
	String SHOW_IMPORT_EXPORT_KEY_NAME = "showImportExport";
	String SHOW_CLEAR_KEY_NAME = "showClear";

	@RequiredArgsConstructor
	enum amount {
		ONE("1"),
		TWO("2"),
		THREE("3"),
		FOUR("4"),
		FIVE("5"),
		SIX("6"),
		SEVEN("7"),
		EIGHT("8"),
		NINE("9"),
		TEN("10"),
		ELEVEN("11"),
		TWELVE("12");

		private final String name;

		@Override
		public String toString()
		{
			return name;
		}

		public int toInt()
		{
			return Integer.parseInt(name);
		}
	}

	@ConfigSection(
			name = "Group Colors",
			description = "Colors for ground marker groups 1-12.",
			position = 0,
			closedByDefault = true
	)
	public static final String groupSections = "groupSection";

	@ConfigItem(
		position = 1,
		keyName = "amount",
		name = "Amount of groups",
		description = "The amount of inventory groups"
	)
	default amount getAmount()
	{
		return amount.FOUR;
	}

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = "markerColor",
		name = "Default tile Color",
		description = "Will not have color with No Outline",
		section = "groupSection"
	)
	default Color markerColor()
	{
		return Color.YELLOW;
	}

	@Alpha
	@ConfigItem(
		position = 2,
		keyName = "markerColor2",
		name = "Group 2 tile color",
		description = "Configures the color of the 2nd group of marked tiles",
		section = "groupSection"
	)
	default Color markerColor2()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		position = 3,
		keyName = "markerColor3",
		name = "Group 3 tile color",
		description = "Configures the color of the 3rd group of marked tiles",
		section = "groupSection"
	)
	default Color markerColor3()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
		position = 4,
		keyName = "markerColor4",
		name = "Group 4 tile color",
		description = "Configures the color of the 4th group of marked tiles",
		section = "groupSection"
	)
	default Color markerColor4()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		position = 5,
		keyName = "markerColor5",
		name = "Group 5 tile color",
		description = "Configures the color of the 5th group of marked tiles",
		section = "groupSection"
	)
	default Color markerColor5()
	{
		return Color.BLACK;
	}

	@Alpha
	@ConfigItem(
		position = 6,
		keyName = "markerColor6",
		name = "Group 6 tile color",
		description = "Configures the color of the 6th group of marked tiles",
		section = "groupSection"
	)
	default Color markerColor6()
	{
		return Color.GRAY;
	}

	@Alpha
	@ConfigItem(
		position = 7,
		keyName = "markerColor7",
		name = "Group 7 tile color",
		description = "Configures the color of the 7th group of marked tiles",
		section = "groupSection"
	)
	default Color markerColor7()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
		position = 8,
		keyName = "markerColor8",
		name = "Group 8 tile color",
		description = "Configures the color of the 8th group of marked tiles",
		section = "groupSection"
	)
	default Color markerColor8()
	{
		return Color.MAGENTA;
	}

	@Alpha
	@ConfigItem(
		position = 9,
		keyName = "markerColor9",
		name = "Group 9 tile color",
		description = "Configures the color of the 9th group of marked tiles",
		section = "groupSection"
	)
	default Color markerColor9()
	{
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(
		position = 10,
		keyName = "markerColor10",
		name = "Group 10 tile color",
		description = "Configures the color of the 10th group of marked tiles",
		section = "groupSection"
	)
	default Color markerColor10()
	{
		return Color.ORANGE;
	}

	@Alpha
	@ConfigItem(
		position = 11,
		keyName = "markerColor11",
		name = "Group 11 tile color",
		description = "Configures the color of the 11th group of marked tiles",
		section = "groupSection"
	)
	default Color markerColor11()
	{
		return Color.PINK;
	}

	@Alpha
	@ConfigItem(
		position = 12,
		keyName = "markerColor12",
		name = "Group 12 tile color",
		description = "Configures the color of the 12th group of marked tiles",
		section = "groupSection"
	)
	default Color markerColor12()
	{
		return Color.LIGHT_GRAY;
	}

	@Alpha
	@ConfigItem(
			keyName = "labelColor",
			position = 13,
			name = "Label Color",
			description = "Sets the label text color if Label Group Color is not toggled on",
			section = "groupSection"
	)
	default Color labelColor() { return Color.WHITE; }

	@ConfigItem(
		position = 14,
		keyName = "showMinimap",
		name = "Show on minimap",
		description = "Shows marked tiles on the minimap"
	)
	default boolean showMinimap()
	{
		return false;
	}

	@Range(min = 1, max = 100)
	@ConfigItem(
		position = 15,
		keyName = "minimapOpacity",
		name = "Minimap opacity",
		description = "The opacity of the minimap markers"
	)
	@Units(Units.PERCENT)
	default int minimapOverlayOpacity()
	{
		return 100;
	}

	@Range(min = 0, max = 255)
	@ConfigItem(
			keyName = "opacity",
			position = 16,
			name = "Opacity",
			description = "The opacity of ground markers from 0 to 255 (0 being black and 255 being transparent)"
	)
	default int opacity()
	{
		return 50;
	}

	@ConfigItem(
			keyName = "tileThiCC",
			position = 17,
			name = "Tile Width",
			description = "Configures the width of ground markers"
	)
	default double tileThiCC() { return 2; }

	@ConfigItem(
			keyName = "tileFill",
			position = 18,
			name = "Tile Fill Color",
			description = "Changes the fill color to the color of the group"
	)
	default boolean tileFill() { return false; }

	@ConfigItem(
			keyName = "labelGroupColor",
			position = 19,
			name = "Label Group Color",
			description = "Sets the label text color to the tiles group"
	)
	default boolean labelGroupColor() { return false; }

	@ConfigItem(
			keyName = "tileSize",
			name = "Tile Size",
			description = "Changes the tile size. Multiple tile sizes will mark around the tile selected",
			position = 20
	)
	default TileSize tileSize() { return TileSize.ONE; }

	@ConfigItem(
			keyName = SHOW_IMPORT_EXPORT_KEY_NAME,
			name = "Show Import/Export options",
			description = "Show the Import/Export options on the minimap right-click menu",
			position = 21
	)
	default boolean showImportExport() { return true; }

	@ConfigItem(
			keyName = SHOW_CLEAR_KEY_NAME,
			name = "Show Clear option",
			description = "Show the Clear option on the minimap right-click menu, which deletes all currently loaded markers",
			position = 22
	)
	default boolean showClear() { return false; }
}
