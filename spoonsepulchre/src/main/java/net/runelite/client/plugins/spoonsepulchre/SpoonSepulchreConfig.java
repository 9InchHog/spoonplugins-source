/*
 * Copyright (c) 2020 Dutta64 <https://github.com/Dutta64>
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
package net.runelite.client.plugins.spoonsepulchre;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("spoonsep")
public interface SpoonSepulchreConfig extends Config
{
	Color RED_OPAQUE = new Color(255, 0, 0, 255);
	Color RED_TRANSPARENT = new Color(255, 0, 0, 20);

	Color WHITE = new Color(255, 255, 255, 255);
	Color WHITE_TRANSPARENT = new Color(255, 255, 255, 20);

	@ConfigSection(
			keyName = "colors",
			name = "Colors",
			description = "Customize overlay colors.",
			position = 0,
			closedByDefault = true
	)
	String colors = "Colors";

	@ConfigSection(
		keyName = "overlays",
		position = 1,
		name = "Overlays",
		description = ""
	)
	String overlays = "Overlays";

	@ConfigSection(
		keyName = "other",
		name = "Other",
		description = "Other settings.",
		position = 2
	)
	String other = "Other";

	//Overlays Section
	@ConfigItem(
		position = 0,
		keyName = "highlightArrows",
		name = "Highlight arrows",
		description = "Overlay arrows with a colored outline.",
		section = overlays
	)
	default HighlightMode highlightArrows()
	{
		return HighlightMode.TILE;
	}

	@ConfigItem(
		position = 1,
		keyName = "highlightSwords",
		name = "Highlight swords",
		description = "Overlay swords with a colored outline.",
		section = overlays
	)
	default HighlightMode highlightSwords()
	{
		return HighlightMode.TILE;
	}

	@ConfigItem(
		position = 2,
		keyName = "highlightCrossbowmanStatue",
		name = "Crossbow statue animation",
		description = "Overlay shooting statues with a colored outline.",
		section = overlays
	)
	default crossbowMode highlightCrossbowStatues()
	{
		return crossbowMode.OUTLINE;
	}

	@ConfigItem(
		position = 3,
		keyName = "highlightWizardStatue",
		name = "Wizard statue tick counter",
		description = "Overlay wizard statues with a tick counter.",
		section = overlays
	)
	default boolean highlightWizardStatues()
	{
		return false;
	}

	@ConfigItem(
			position = 4,
			keyName = "highlightBlues",
			name = "Highlight good teleports",
			description = "Highlights the good teleports",
			section = overlays
	)
	default boolean highlightBlues()
	{
		return true;
	}

	@ConfigItem(
			position = 5,
			keyName = "highlightYellows",
			name = "Highlight bad teleports",
			description = "Highlights the bad teleports",
			section = overlays
	)
	default boolean highlightYellows()
	{
		return false;
	}

	@ConfigItem(
		position = 6,
		keyName = "highlightServerTile",
		name = "Player true location",
		description = "Highlight the tile the player is on, according to the server.",
		section = overlays
	)
	default boolean highlightServerTile()
	{
		return false;
	}

	@ConfigItem(
		position = 7,
		keyName = "renderDistance",
		name = "Render distance",
		description = "How far to render overlays from your player's position.",
		section = overlays
	)
	default RenderDistance renderDistance()
	{
		return RenderDistance.MEDIUM;
	}

	//Color Section
	@Alpha
	@ConfigItem(
		position = 0,
		keyName = "arrowsOutlineColor",
		name = "Arrows outline",
		description = "Change the overlay outline color of arrows.",
		section = colors
	)
	default Color arrowsOutlineColor()
	{
		return RED_OPAQUE;
	}

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = "arrowsFillColor",
		name = "Arrows fill",
		description = "Change the overlay fill color of arrows.",
		section = colors
	)
	default Color arrowsFillColor()
	{
		return RED_TRANSPARENT;
	}

	@Alpha
	@ConfigItem(
		position = 2,
		keyName = "swordsOutlineColor",
		name = "Swords outline",
		description = "Change the overlay outline color of swords.",
		section = colors
	)
	default Color swordsOutlineColor()
	{
		return RED_OPAQUE;
	}

	@Alpha
	@ConfigItem(
		position = 3,
		keyName = "swordsFillColor",
		name = "Swords fill",
		description = "Change the overlay fill color of swords.",
		section = colors
	)
	default Color swordsFillColor()
	{
		return RED_TRANSPARENT;
	}

	@Alpha
	@ConfigItem(
		position = 4,
		keyName = "crossbowStatueOutlineColor",
		name = "Crossbow outline",
		description = "Change the overlay outline color of the crossbow statues.",
		section = colors
	)
	default Color crossbowStatueOutlineColor()
	{
		return RED_OPAQUE;
	}

	@Alpha
	@ConfigItem(
		position = 5,
		keyName = "crossbowStatueFillColor",
		name = "Crossbow fill",
		description = "Change the overlay fill color of the crossbow statues.",
		section = colors
	)
	default Color crossbowStatueFillColor()
	{
		return RED_TRANSPARENT;
	}

	@Alpha
	@ConfigItem(
			position = 6,
			keyName = "goodTeleOutline",
			name = "Good teleport outline",
			description = "Change the overlay outline color of the good teleports.",
			section = colors
	)
	default Color goodTeleOutline()
	{
		return WHITE;
	}

	@Alpha
	@ConfigItem(
			position = 7,
			keyName = "goodTeleFill",
			name = "Good teleport fill",
			description = "Change the overlay fill color of the good teleports.",
			section = colors
	)
	default Color goodTeleFill()
	{
		return WHITE_TRANSPARENT;
	}

	@Alpha
	@ConfigItem(
			position = 8,
			keyName = "badTeleOutline",
			name = "Bad teleport outline",
			description = "Change the overlay outline color of the bad teleports.",
			section = colors
	)
	default Color badTeleOutline()
	{
		return RED_OPAQUE;
	}

	@Alpha
	@ConfigItem(
			position = 9,
			keyName = "badTeleFill",
			name = "Bad teleport fill",
			description = "Change the overlay fill color of the bad teleports.",
			section = colors
	)
	default Color badTeleFill()
	{
		return RED_TRANSPARENT;
	}

	@Alpha
	@ConfigItem(
		position = 10,
		keyName = "serverTileOutlineColor",
		name = "True location outline",
		description = "Change the overlay outline color of the player's server tile.",
		section = colors
	)
	default Color serverTileOutlineColor()
	{
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(
		position = 11,
		keyName = "serverTileFillColor",
		name = "True location fill",
		description = "Change the overlay fill color of the player's server tile.",
		section = colors
	)
	default Color serverTileFillColor()
	{
		return new Color(0, 0, 0, 0);
	}

	@ConfigItem(
		position = 12,
		keyName = "wizardStatueTickCounterColor",
		name = "Tick counter",
		description = "Change the overlay color of the wizard statue tick counter.",
		section = colors
	)
	default Color wizardStatueTickCounterColor()
	{
		return Color.RED;
	}

	@Range(min = 1, max = 20)
	@ConfigItem(
		position = 0,
		keyName = "wizardFontSize",
		name = "Tick counter font size",
		description = "Adjust the font size of the wizard statue tick counter.",
		section = other
	)
	@Units(Units.POINTS)
	default int wizardFontSize()
	{
		return 12;
	}

	@ConfigItem(
		position = 1,
		keyName = "fontStyle",
		name = "Font style",
		description = "Bold/Italics/Plain",
		section = other

	)
	default FontStyle fontStyle()
	{
		return FontStyle.PLAIN;
	}

	@ConfigItem(
		position = 2,
		keyName = "wizardFontShadow",
		name = "Tick counter font shadow",
		description = "Toggle font shadow of the wizard statue tick counter.",
		section = other
	)
	default boolean wizardFontShadow()
	{
		return false;
	}

	@Range(min = 1, max = 5)
	@ConfigItem(
		position = 3,
		keyName = "tileOutlineWidth",
		name = "Tile outline width",
		description = "Change width of tile outlines.",
		section = other
	)
	@Units(Units.POINTS)
	default int tileOutlineWidth()
	{
		return 1;
	}

	@Range(min = 1, max = 5)
	@ConfigItem(
			position = 4,
			keyName = "outlineWidth",
			name = "Outline width",
			description = "Change width of outlines.",
			section = other
	)
	@Units(Units.POINTS)
	default int outlineWidth()
	{
		return 1;
	}

	@Getter
	@AllArgsConstructor
	enum HighlightMode
	{
		NONE("None"),
		OUTLINE("Outline"),
		TILE("Tile"),
		TL("True Location");

		private final String name;

		@Override
		public String toString()
		{
			return name;
		}
	}

	@Getter
	@AllArgsConstructor
	enum FontStyle
	{
		BOLD("Bold", Font.BOLD),
		ITALIC("Italic", Font.ITALIC),
		PLAIN("Plain", Font.PLAIN);

		private final String name;
		private final int font;

		@Override
		public String toString()
		{
			return name;
		}
	}

	@Getter
	@AllArgsConstructor
	enum RenderDistance
	{
		SHORT("Short", 2350),
		MEDIUM("Medium", 3525),
		FAR("Far", 4700),
		UNCAPPED("Uncapped", 0);

		private final String name;
		private final int distance;

		@Override
		public String toString()
		{
			return name;
		}
	}

	enum crossbowMode {
		OFF, HULL, OUTLINE
	}
}
