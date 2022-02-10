/*
 * Copyright (c) 2017, Aria <aria@ar1as.space>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.client.plugins.spoongrounditems;

import net.runelite.client.config.*;
import net.runelite.client.plugins.spoongrounditems.config.*;

import java.awt.*;

@ConfigGroup("grounditems")
public interface SpoonGroundItemsConfig extends Config
{
	@ConfigSection(
		name = "Highlighted Items",
		description = "The highlighted and hidden item lists",
		position = 0,
		closedByDefault = true
	)
	String highlightedItems = "highlightedItems";

	@ConfigSection(
			name = "Hidden Items",
			description = "The highlighted and hidden item lists",
			position = 1,
			closedByDefault = true
	)
	String hiddenItems = "hiddenItems";

	@ConfigSection(
			name = "Highlight Options",
			description = "The highlighted and hidden item lists",
			position = 2,
			closedByDefault = true
	)
	String highlight = "highlight";

	@ConfigSection(
			name = "Value Options",
			description = "The highlighted and hidden item lists",
			position = 3,
			closedByDefault = true
	)
	String value = "value";

	@ConfigSection(
			name = "Miscellaneous",
			description = "The highlighted and hidden item lists",
			position = 4,
			closedByDefault = true
	)
	String misc = "misc";

	//------------------------------------------------------------//
	// Highlighted items
	//------------------------------------------------------------//
	@ConfigItem(
		keyName = "highlightedItems",
		name = "Highlighted Items",
		description = "Configures specifically highlighted ground items. Format: (item), (item)",
		position = 0,
		section = highlightedItems
	)
	default String getHighlightItems()
	{
		return "";
	}

	@ConfigItem(
		keyName = "highlightedItems",
		name = "",
		description = ""
	)
	void setHighlightedItem(String key);

	@Alpha
	@ConfigItem(
			keyName = "highlightedColor",
			name = "Highlighted items",
			description = "Configures the color for highlighted items",
			position = 1,
			section = highlightedItems
	)
	default Color highlightedColor()
	{
		return Color.decode("#AA00FF");
	}

	@ConfigItem(
			keyName = "showHighlightedOnly",
			name = "Show Highlighted items only",
			description = "Configures whether or not to draw items only on your highlighted list",
			position = 2,
			section = highlightedItems
	)
	default boolean showHighlightedOnly()
	{
		return false;
	}

	@ConfigItem(
			keyName = "highlightValueCalculation",
			name = "Highlight Value Calculation",
			description = "Configures which coin value is used to determine highlight color",
			position = 3,
			section = highlightedItems
	)
	default ValueCalculationMode valueCalculationMode()
	{
		return ValueCalculationMode.HIGHEST;
	}

	//------------------------------------------------------------//
	// Hidden items
	//------------------------------------------------------------//
	@ConfigItem(
		keyName = "hiddenItems",
		name = "Hidden Items",
		description = "Configures hidden ground items. Format: (item), (item)",
		position = 0,
		section = hiddenItems
	)
	default String getHiddenItems()
	{
		return "Vial, Ashes, Coins, Bones, Bucket, Jug, Seaweed";
	}

	@ConfigItem(
		keyName = "hiddenItems",
		name = "",
		description = ""
	)
	void setHiddenItems(String key);

	@Alpha
	@ConfigItem(
			keyName = "hiddenColor",
			name = "Hidden items",
			description = "Configures the color for hidden items in right-click menu and when holding ALT",
			position = 1,
			section = hiddenItems
	)
	default Color hiddenColor()
	{
		return Color.GRAY;
	}

	@ConfigItem(
			keyName = "recolorMenuHiddenItems",
			name = "Recolor Menu Hidden Items",
			description = "Configures whether or not hidden items in right-click menu will be recolored",
			position = 2,
			section = hiddenItems
	)
	default boolean recolorMenuHiddenItems()
	{
		return false;
	}

	@ConfigItem(
		keyName = "dontHideUntradeables",
		name = "Do not hide untradeables",
		description = "Configures whether or not untradeable items ignore hiding under settings",
		position = 3,
		section = hiddenItems
	)
	default boolean dontHideUntradeables()
	{
		return true;
	}

	@ConfigItem(
			keyName = "hideUnderValue",
			name = "Hide under value",
			description = "Configures hidden ground items under both GE and HA value",
			position = 4,
			section = hiddenItems
	)
	default int getHideUnderValue()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "removeIgnored",
			name = "Hide Hidden",
			description = "Remove take option for items that are on the hidden items list.",
			position = 5,
			disabledBy = "rightClickHidden",
			section = hiddenItems
	)
	default boolean removeIgnored()
	{
		return false;
	}

	@ConfigItem(
			keyName = "rightClickHidden",
			name = "Right click hidden items",
			description = "Places hidden items below the 'Walk here' option, making it so that you need to right click to pick them up",
			position = 6,
			disabledBy = "removeIgnored",
			section = hiddenItems
	)
	default boolean rightClickHidden()
	{
		return false;
	}

	//------------------------------------------------------------//
	// Highlight
	//------------------------------------------------------------//
	@Alpha
	@ConfigItem(
			keyName = "defaultColor",
			name = "Default items",
			description = "Configures the color for default, non-highlighted items",
			position = 0,
			section = highlight
	)
	default Color defaultColor()
	{
		return Color.WHITE;
	}

	@ConfigItem(
		keyName = "highlightTiles",
		name = "Highlight Tiles",
		description = "Configures whether or not to highlight tiles containing ground items",
		position = 1,
		section = highlight
	)
	default boolean highlightTiles()
	{
		return false;
	}

	@ConfigItem(
		keyName = "notifyHighlightedDrops",
		name = "Notify for Highlighted drops",
		description = "Configures whether or not to notify for drops on your highlighted list",
		position = 2,
		section = highlight
	)
	default boolean notifyHighlightedDrops()
	{
		return false;
	}

	@ConfigItem(
		keyName = "notifyTier",
		name = "Notify tier",
		description = "Configures which price tiers will trigger a notification on drop",
		position = 3,
		section = highlight
	)
	default HighlightTier notifyTier()
	{
		return HighlightTier.OFF;
	}

	@ConfigItem(
		keyName = "itemHighlightMode",
		name = "Item Highlight Mode",
		description = "Configures how ground items will be highlighted",
		position = 4,
		section = highlight
	)
	default ItemHighlightMode itemHighlightMode()
	{
		return ItemHighlightMode.BOTH;
	}

	@ConfigItem(
		keyName = "menuHighlightMode",
		name = "Menu Highlight Mode",
		description = "Configures what to highlight in right-click menu",
		position = 5,
		section = highlight
	)
	default MenuHighlightMode menuHighlightMode()
	{
		return MenuHighlightMode.NAME;
	}

	@ConfigItem(
			keyName = "groundItemTimers",
			name = "Despawn timer",
			description = "Shows despawn timers for items you've dropped and received as loot",
			position = 6,
			section = highlight
	)
	default DespawnTimerMode groundItemTimers()
	{
		return DespawnTimerMode.OFF;
	}

	@ConfigItem(
			keyName = "textOutline",
			name = "Text Outline",
			description = "Use an outline around text instead of a text shadow",
			position = 7,
			section = highlight
	)
	default boolean textOutline()
	{
		return false;
	}

	@ConfigItem(
			keyName = "showLootbeamForHighlighted",
			name = "Highlighted item lootbeams",
			description = "Configures lootbeams to show for all highlighted items.",
			position = 8,
			section = highlight
	)
	default boolean showLootbeamForHighlighted()
	{
		return false;
	}

	@ConfigItem(
			keyName = "showLootbeamTier",
			name = "Lootbeam tier",
			description = "Configures which price tiers will trigger a lootbeam",
			position = 9,
			section = highlight
	)
	default HighlightTier showLootbeamTier()
	{
		return HighlightTier.HIGH;
	}

	@ConfigItem(
			keyName = "lootbeamStyle",
			name = "Lootbeam Style",
			description = "Style of lootbeam to use",
			position = 10,
			section = highlight
	)
	default Lootbeam.Style lootbeamStyle()
	{
		return Lootbeam.Style.MODERN;
	}

	@ConfigItem(
			keyName = "raveLootBeams",
			name = "Rave Loot Beams",
			description = "catJam",
			position = 11,
			section = highlight
	)
	default RaveLootBeamMode raveLootBeams() { return RaveLootBeamMode.OFF; }

	//------------------------------------------------------------//
	// Value
	//------------------------------------------------------------//
	@Alpha
	@ConfigItem(
		keyName = "lowValueColor",
		name = "Low value items",
		description = "Configures the color for low value items",
		position = 0,
		section = value
	)
	default Color lowValueColor()
	{
		return Color.decode("#66B2FF");
	}

	@ConfigItem(
		keyName = "lowValuePrice",
		name = "Low value price",
		description = "Configures the start price for low value items",
		position = 1,
		section = value
	)
	default int lowValuePrice()
	{
		return 20000;
	}

	@Alpha
	@ConfigItem(
		keyName = "mediumValueColor",
		name = "Medium value items",
		description = "Configures the color for medium value items",
		position = 2,
		section = value
	)
	default Color mediumValueColor()
	{
		return Color.decode("#99FF99");
	}

	@ConfigItem(
		keyName = "mediumValuePrice",
		name = "Medium value price",
		description = "Configures the start price for medium value items",
		position = 3,
		section = value
	)
	default int mediumValuePrice()
	{
		return 100000;
	}

	@Alpha
	@ConfigItem(
		keyName = "highValueColor",
		name = "High value items",
		description = "Configures the color for high value items",
		position = 4,
		section = value
	)
	default Color highValueColor()
	{
		return Color.decode("#FF9600");
	}

	@ConfigItem(
		keyName = "highValuePrice",
		name = "High value price",
		description = "Configures the start price for high value items",
		position = 5,
		section = value
	)
	default int highValuePrice()
	{
		return 1000000;
	}

	@Alpha
	@ConfigItem(
		keyName = "insaneValueColor",
		name = "Insane value items",
		description = "Configures the color for insane value items",
		position = 6,
		section = value
	)
	default Color insaneValueColor()
	{
		return Color.decode("#FF66B2");
	}

	@ConfigItem(
		keyName = "insaneValuePrice",
		name = "Insane value price",
		description = "Configures the start price for insane value items",
		position = 7,
		section = value
	)
	default int insaneValuePrice()
	{
		return 10000000;
	}

	@ConfigItem(
			keyName = "priceDisplayMode",
			name = "Price Display Mode",
			description = "Configures which price types are shown alongside ground item name",
			position = 8,
			section = value
	)
	default PriceDisplayMode priceDisplayMode()
	{
		return PriceDisplayMode.BOTH;
	}

	@ConfigItem(
			keyName = "sortByGEPrice",
			name = "Sort by GE price",
			description = "Sorts ground items by GE price, instead of alch value",
			position = 9,
			section = value
	)
	default boolean sortByGEPrice()
	{
		return false;
	}

	//------------------------------------------------------------//
	// Misc
	//------------------------------------------------------------//
	@ConfigItem(
			keyName = "showMenuItemQuantities",
			name = "Show Menu Item Quantities",
			description = "Configures whether or not to show the item quantities in the menu",
			position = 0,
			section = misc
	)
	default boolean showMenuItemQuantities()
	{
		return true;
	}

	@ConfigItem(
			keyName = "collapseEntries",
			name = "Collapse ground item menu",
			description = "Collapses ground item menu entries together and appends count",
			position = 1,
			section = misc
	)
	default boolean collapseEntries()
	{
		return false;
	}

	@ConfigItem(
			keyName = "onlyShowLoot",
			name = "Only show loot",
			description = "Only shows drops from NPCs and players",
			position = 2,
			section = misc
	)
	default boolean onlyShowLoot()
	{
		return false;
	}

	@ConfigItem(
			keyName = "doubleTapDelay",
			name = "Double-tap delay",
			description = "Delay for the double-tap Hotkey to hide ground items. 0 to disable.",
			position = 3,
			section = misc
	)
	@Units(Units.MILLISECONDS)
	default int doubleTapDelay()
	{
		return 250;
	}

	@ConfigItem(
			keyName = "hotkey",
			name = "Hotkey",
			description = "Configures the hotkey used by the Spoon Ground Items plugin",
			position = 4,
			section = misc
	)
	default Keybind hotkey()
	{
		return Keybind.ALT;
	}

	public enum RaveLootBeamMode {
		OFF, FLOW, RAVE, EPILEPSY
	}
}
