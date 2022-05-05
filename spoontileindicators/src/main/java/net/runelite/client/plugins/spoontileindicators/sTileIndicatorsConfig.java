/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
package net.runelite.client.plugins.spoontileindicators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("tileindicators")
public interface sTileIndicatorsConfig extends Config
{
	@ConfigSection(
			name = "Destination Tile",
			description = "",
			position = 0,
			closedByDefault = true
	)
	String destination = "destination";

	@ConfigSection(
			name = "Hovered Tile",
			description = "",
			position = 1,
			closedByDefault = true
	)
	String hovered = "hovered";

	@ConfigSection(
			name = "True Tile",
			description = "",
			position = 2,
			closedByDefault = true
	)
	String trueTile = "trueTile";

	//Destination Tiles
	@Alpha
	@ConfigItem(
		keyName = "highlightDestinationColor",
		name = "Destination tile",
		description = "Configures the highlight color of current destination",
		position = 1,
		section = destination
	)
	default Color highlightDestinationColor()
	{
		return Color.GRAY;
	}

	@ConfigItem(
		keyName = "highlightDestinationTile",
		name = "Highlight destination tile",
		description = "Highlights tile player is walking to",
		position = 2,
		section = destination
	)
	default boolean highlightDestinationTile()
	{
		return true;
	}

	@ConfigItem(
			keyName = "destinationTileBorderWidth",
			name = "Destination tile width",
			description = "Width of the destination tile marker border",
			position = 3,
			section = destination
	)
	default double destinationTileBorderWidth()
	{
		return 1;
	}

	@ConfigItem(
			keyName = "destinationTileOpacity",
			name = "Destination tile opacity",
			description = "How filled in you want the destination tile to be",
			position = 4,
			section = destination
	)
	default int destinationTileOpacity()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "destinationTileFillColor",
			name = "Destination tile fill color",
			description = "Fills the color of the destination tile with the destination tile color. Black if off",
			position = 5,
			section = destination
	)
	default boolean destinationTileFillColor()
	{
		return false;
	}

	@ConfigItem(
			keyName = "highlightDestinationStyle",
			name = "Destination style",
			description = "The style to display the destination tile in",
			position = 6,
			section = destination
	)
	default TileStyle highlightDestinationStyle() { return TileStyle.DEFAULT; }

	//Hovered Tiles
	@Alpha
	@ConfigItem(
		keyName = "highlightHoveredColor",
		name = "Hovered tile",
		description = "Configures the highlight color of hovered tile",
		position = 1,
		section = hovered
	)
	default Color highlightHoveredColor()
	{
		return new Color(0, 0, 0, 0);
	}

	@ConfigItem(
			keyName = "highlightHoveredTile",
			name = "Highlight hovered tile",
			description = "Highlights tile player is hovering with mouse",
			position = 2,
			section = hovered
	)
	default boolean highlightHoveredTile()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hoveredTileBorderWidth",
			name = "Hovered tile width",
			description = "Width of the hovered tile marker border",
			position = 3,
			section = hovered
	)
	default double hoveredTileBorderWidth()
	{
		return 2;
	}

	@ConfigItem(
			keyName = "hoveredTileOpacity",
			name = "Hovered tile opacity",
			description = "How filled in you want the hovered tile to be",
			position = 4,
			section = hovered
	)
	default int hoveredTileOpacity()
	{
		return 50;
	}

	@ConfigItem(
			keyName = "hoveredTileFillColor",
			name = "Hovered tile fill color",
			description = "Fills the color of the hovered tile with the hovered tile color. Black if off",
			position = 5,
			section = hovered
	)
	default boolean hoveredTileFillColor()
	{
		return false;
	}

	//True Tiles
	@Alpha
	@ConfigItem(
		keyName = "highlightCurrentColor",
		name = "True tile",
		description = "Configures the highlight color of current true tile",
		position = 1,
		section = trueTile
	)
	default Color highlightCurrentColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		keyName = "highlightCurrentTile",
		name = "Highlight true tile",
		description = "Highlights true tile player is on as seen by server",
		position = 2,
		section = trueTile
	)
	default boolean highlightCurrentTile()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
			keyName = "highlightOtherCurrentColor",
			name = "Others true tile",
			description = "Configures the highlight color of current true tile",
			position = 3,
			section = trueTile
	)
	default Color highlightOtherCurrentColor() { return new Color(37, 197, 79, 255); }

	@ConfigItem(
			keyName = "highlightOtherCurrentTile",
			name = "Highlight others true tile",
			description = "Highlights true tile player is on as seen by server",
			position = 4,
			section = trueTile
	)
	default boolean highlightOtherCurrentTile()
	{
		return false;
	}

	@ConfigItem(
			name = "True Tile Width",
			keyName = "trueTileBorderWidth",
			description = "Outline width for the true tile indicators overlay",
			position = 5,
			section = trueTile
	)
	default double trueTileBorderWidth() { return 1 ; }

	@Range(min = 0, max = 255)
	@ConfigItem(
			name = "True Tile Opacity",
			keyName = "trueTileOpacity",
			description = "Opacity for the true tile overlays",
			position = 6,
			section = trueTile
	)
	default int trueTileOpacity() { return 0 ; }

	@ConfigItem(
			keyName = "trueTileFillColor",
			name = "True tile fill color",
			description = "Fills the color of the true tile with the true tile color. Black if off",
			position = 7,
			section = trueTile
	)
	default boolean trueTileFillColor()
	{
		return false;
	}

	@ConfigItem(
			keyName = "antiAlias",
			name = "Anti-Aliasing",
			description = "Turns on anti-aliasing for the tiles. Makes them smoother.",
			position = 16
	)
	default boolean antiAlias()
	{
		return true;
	}

	@ConfigItem(
			keyName = "overlaysBelowPlayer",
			name = "Draw overlays below player",
			description = "Requires GPU. Draws overlays below the player",
			position = 17
	)
	default boolean overlaysBelowPlayer() { return false; }

	@ConfigItem(
			keyName = "rave",
			name = "Rave tile indicators",
			description = "Turns on rave for the tiles.",
			position = 18
	)
	default boolean rave()
	{
		return false;
	}

	@Getter
	@AllArgsConstructor
	public enum TileStyle {
		DEFAULT("Default"),
		RS3("Rs3"),
		RS3_NO_ARROW("Rs3(no arrow)");

		private String name;

		@Override
		public String toString() {
			return getName();
		}
	}
}
