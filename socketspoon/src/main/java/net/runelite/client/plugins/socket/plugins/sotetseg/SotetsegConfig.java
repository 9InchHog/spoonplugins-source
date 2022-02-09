/*
 * Copyright (c) 2020, Charles Xu <github.com/kthisiscvpv>
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

package net.runelite.client.plugins.socket.plugins.sotetseg;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("Socket Sotetseg Config")
public interface SotetsegConfig extends Config
{
	@ConfigSection(
			name = "Maze Solving",
			description = "Options for maze solving - cSotetseg",
			position = 99,
			closedByDefault = true
	)
	public static final String solving = "solving";

	@ConfigItem(
		position = 1,
		keyName = "getTileColor",
		name = "Tile Color",
		description = "The color of the tiles."
	)
	default Color getTileColor()
	{
		return new Color(0, 0, 0);
	}

	@ConfigItem(
		position = 2,
		keyName = "getTileTransparency",
		name = "Tile Transparency",
		description = "The color transparency of the tiles. Ranges from 0 to 255, inclusive."
	)
	default int getTileTransparency()
	{
		return 50;
	}

	@ConfigItem(
		position = 3,
		keyName = "getTileOutline",
		name = "Tile Outline Color",
		description = "The color of the outline of the tiles."
	)
	default Color getTileOutline()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		position = 4,
		keyName = "getTileOutlineSize",
		name = "Tile Outline Size",
		description = "The size of the outline of the tiles."
	)
	default double getTileOutlineSize()
	{
		return 1;
	}

	@ConfigItem(
			position = 4,
			keyName = "streamerMode",
			name = "Streamer Mode",
			description = "Send Maze Info to team but don't display maze overlay on your screen."
	)
	default boolean streamerMode() {
		return false;
	}

	@ConfigItem(
			position = 5,
			keyName = "testOverlay",
			name = "Show Test Tiles",
			description = "Shows test tiles to allow you to change your tile outline settings"
	)
	default boolean showTestOverlay()
	{
		return false;
	}

	@ConfigItem(
			position = 6,
			keyName = "warnBall",
			name = "Warns if invisible ball is sent",
			description = "Warns you if the ball was sent while you were chosen since it's invisible otherwise"
	)
	default boolean warnBall() { return true;}

	@ConfigItem(
			position = 7,
			keyName = "ballTargetColor",
			name = "Invisible Target Color",
			description = "Warns you if the ball was sent while you were chosen since it's invisible otherwise"
	)
	default Color ballTargetColor() { return new Color(188, 74, 74);}

	@ConfigItem(
			position = 8,
			keyName = "trueMaze",
			name = "Maze True Tile",
			description = "Shows your true tile location only when the maze is active"
	)
	default boolean trueMaze() { return false;}

	@ConfigItem(
			position = 9,
			keyName = "trueMazeColor",
			name = "Maze True Tile Color",
			description = "Color for the maze true tile"
	)
	default Color trueMazeColor() { return Color.RED;}

	@ConfigItem(
			position = 10,
			keyName = "trueMazeThicc",
			name = "Maze True Tile Width",
			description = "Width for the maze true location tile"
	)
	default double trueMazeThicc() { return 2;}

	@ConfigItem(
			position = 11,
			keyName = "antiAlias",
			name = "Tile Anti-Aliasing",
			description = "Turns on anti-aliasing for the tiles. Makes them more smoother."
	)
	default boolean antiAlias()
	{
		return false;
	}

	//------------------------------------------------------------//
	// Maze solving
	//------------------------------------------------------------//
	@ConfigItem(
		position = 5,
		keyName = "flashScreenOnChosen",
		name = "Flash Screen on Chosen",
		description = "Flash your screen when you're chosen to run the maze",
		section = solving
	)
	default boolean flashScreen()
	{
		return false;
	}

	@ConfigItem(
		position = 6,
		keyName = "hideScreenFlash",
		name = "Hide White Screen",
		description = "Remove the white flash when you're teleported at the start of the maze phase",
		section = solving
	)
	default boolean hideScreenFlash()
	{
		return false;
	}

	@ConfigItem(
		position = 7,
		keyName = "solveMaze",
		name = "Solve the Maze",
		description = "Adds an overlay for the best way to run sotetseg maze",
		section = solving
	)
	default boolean solveMaze()
	{
		return true;
	}

	@ConfigItem(
		position = 8,
		keyName = "numbersOn",
		name = "Add Numbers",
		description = "Adds number overlay to tiles you need to click",
		hidden = true,
		unhide = "solveMaze",
		section = solving
	)
	default boolean numbersOn()
	{
		return true;
	}

	@ConfigItem(
		position = 9,
		keyName = "getFontSize",
		name = "Font Size",
		description = "Size of font for numbers",
		hidden = true,
		unhide = "solveMaze",
		section = solving
	)
	default int getFontSize()
	{
		return 10;
	}

	@ConfigItem(
		position = 10,
		keyName = "highlightTiles",
		name = "Add Outline",
		description = "Adds tile highlight to tiles you need to click",
		hidden = true,
		unhide = "solveMaze",
		section = solving
	)
	default boolean highlightTiles()
	{
		return true;
	}

	@ConfigItem(
			position = 11,
			keyName = "solvedTileWidth",
			name = "Solved Tile Width",
			description = "Adjusts tile width of the tiles you need to click",
			hidden = true,
			unhide = "solveMaze",
			section = solving
	)
	default double solvedTileWidth()
	{
		return 1;
	}

	@ConfigItem(
			position = 12,
			keyName = "solvedTileOpacity",
			name = "Solved Tile Opacity",
			description = "Adjusts tile opacity of the tiles you need to click",
			hidden = true,
			unhide = "solveMaze",
			section = solving
	)
	default int solvedTileOpacity()
	{
		return 50;
	}

	@ConfigItem(
		position = 13,
		keyName = "getHighlightTileOutline",
		name = "Solved Tile Color",
		description = "The color of the outline of the highlighted tiles",
		hidden = true,
		unhide = "solveMaze",
		section = solving
	)
	default Color getHighlightTileOutline()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		position = 14,
		keyName = "showDPSSplits",
		name = "Show Between Maze Splits",
		description = "Shows time between mazes",
		hidden = true,
		unhide = "solveMaze",
		section = solving
	)
	default boolean showBetweenSplits()
	{
		return false;
	}

	@ConfigItem(
		position = 15,
		keyName = "showMazeSplits",
		name = "Show Maze Splits",
		description = "Shows maze splits",
		hidden = true,
		unhide = "solveMaze",
		section = solving
	)
	default boolean showMazeSplits()
	{
		return false;
	}

	@ConfigItem(
		position = 16,
		keyName = "showDetailedSplits",
		name = "Show Detailed Splits",
		description = "Adds extra information to splits",
		hidden = true,
		unhide = "solveMaze",
		section = solving
	)
	default boolean showDetailedSplits()
	{
		return false;
	}

	@ConfigItem(
		position = 17,
		keyName = "splitMessageColor",
		name = "Time Splits Message Color",
		description = "Color of splits in chat box",
		hidden = true,
		unhide = "solveMaze",
		section = solving
	)
	default Color getSplitsMessageColor()
	{
		return Color.RED;
	}

	@ConfigItem(
			position = 18,
			keyName = "getFontSizeInstanceTimer",
			name = "Font Size Instance Timer",
			description = "Size of font for Instance Timer",
			section = solving
	)
	default int getFontSizeInstanceTimer()
	{
		return 14;
	}

	@ConfigItem(
		position = 19,
		keyName = "showSotetsegInstanceTimer",
		name = "Sotetseg Instance Timer",
		description = "Show when Sote can be attacked after the maze",
		section = solving
	)
	default boolean showSotetsegInstanceTimer()
	{
		return true;
	}

	@ConfigItem(
			position = 20,
			keyName = "sotetsegInstanceTimerColor",
			name = "Sotetseg Timer Color",
			description = "Color of the timer for when Sote can be attacked after the maze",
			hidden = true,
			unhide = "showSotetsegInstanceTimer",
			section = solving
	)
	default Color sotetsegInstanceTimerColor()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		position = 21,
		keyName = "showSotetsegInstanceTimerPlayer",
		name = "Instance Timer On Player",
		description = "Show when Sote can be attacked after the maze",
		section = solving
	)
	default boolean showSotetsegInstanceTimerPlayer()
	{
		return true;
	}

	@ConfigItem(
			position = 22,
			keyName = "sotetsegInstanceTimerPlayerColor",
			name = "Player Timer Color",
			description = "Color for when Sote can be attacked after the maze",
			hidden = true,
			unhide = "showSotetsegInstanceTimerPlayer",
			section = solving
	)
	default Color sotetsegInstanceTimerPlayerColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		position = 23,
		keyName = "isChosenText",
		name = "Add Chosen Text",
		description = "Adds the text from the config below onto the screen when you've been chosen to run the maze",
		section = solving
	)
	default boolean isChosenText()
	{
		return true;
	}

	@ConfigItem(
		position = 24,
		keyName = "customChosenText",
		name = "Custom Chosen Text",
		description = "Adds the text from this config onto the screen when you've been chosen to run the maze",
		hidden = true,
		unhide = "isChosenText",
		section = solving
	)
	default String customChosenText()
	{
		return "You have been chosen.";
	}

	@ConfigItem(
		position = 25,
		keyName = "chosenTextDuration",
		name = "Chosen Text Duration",
		description = "How long the chosen text should stay on the screen in seconds",
		hidden = true,
		unhide = "isChosenText",
		section = solving
	)
	@Units(" sec")
	default int chosenTextDuration()
	{
		return 3;
	}
}
