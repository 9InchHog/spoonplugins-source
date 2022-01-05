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
package net.runelite.client.plugins.pvpplayerindicators;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("playerindicators")
public interface PvPPlayerIndicatorsConfig extends Config
{
	@ConfigSection(
			name = "Highlight Options",
			description = "Toggle highlighted players by type (self, friends, etc.) and choose their highlight colors",
			position = 98,
			closedByDefault = true
	)
	String highlightSection = "highlightSection";

	@ConfigSection(
			name = "PvP Options",
			description = "Options for PvP",
			position = 99,
			closedByDefault = true
	)
	String pvpSection = "pvpSection";

	@ConfigItem(
			position = 0,
			keyName = "drawOwnName",
			name = "Highlight own player",
			description = "Configures whether or not your own player should be highlighted",
			section = highlightSection
	)
	default boolean highlightOwnPlayer()
	{
		return false;
	}

	@ConfigItem(
			position = 1,
			keyName = "ownNameColor",
			name = "Own player",
			description = "Color of your own player",
			section = highlightSection
	)
	default Color getOwnPlayerColor()
	{
		return new Color(0, 184, 212);
	}

	@ConfigItem(
			position = 2,
			keyName = "drawFriendNames",
			name = "Highlight friends",
			description = "Configures whether or not friends should be highlighted",
			section = highlightSection
	)
	default boolean highlightFriends()
	{
		return true;
	}

	@ConfigItem(
			position = 3,
			keyName = "friendNameColor",
			name = "Friend",
			description = "Color of friend names",
			section = highlightSection
	)
	default Color getFriendColor()
	{
		return new Color(0, 200, 83);
	}

	@ConfigItem(
			position = 4,
			keyName = "drawClanMemberNames",
			name = "Highlight friends chat members",
			description = "Configures if friends chat members should be highlighted",
			section = highlightSection
	)
	default boolean drawFriendsChatMemberNames()
	{
		return true;
	}

	@ConfigItem(
			position = 5,
			keyName = "clanMemberColor",
			name = "Friends chat",
			description = "Color of friends chat members",
			section = highlightSection
	)
	default Color getFriendsChatMemberColor()
	{
		return new Color(170, 0, 255);
	}

	@ConfigItem(
			position = 6,
			keyName = "drawTeamMemberNames",
			name = "Highlight team members",
			description = "Configures whether or not team members should be highlighted",
			section = highlightSection
	)
	default boolean highlightTeamMembers()
	{
		return true;
	}

	@ConfigItem(
			position = 7,
			keyName = "teamMemberColor",
			name = "Team member",
			description = "Color of team members",
			section = highlightSection
	)
	default Color getTeamMemberColor()
	{
		return new Color(19, 110, 247);
	}

	@ConfigItem(
			position = 8,
			keyName = "drawClanChatMemberNames",
			name = "Highlight clan members",
			description = "Configures whether or not clan members should be highlighted",
			section = highlightSection
	)
	default boolean highlightClanMembers()
	{
		return true;
	}

	@ConfigItem(
			position = 9,
			keyName = "clanChatMemberColor",
			name = "Clan member",
			description = "Color of clan members",
			section = highlightSection
	)
	default Color getClanMemberColor()
	{
		return new Color(36, 15, 171);
	}

	@ConfigItem(
			position = 10,
			keyName = "drawNonClanMemberNames",
			name = "Highlight others",
			description = "Configures whether or not other players should be highlighted",
			section = highlightSection
	)
	default boolean highlightOthers()
	{
		return false;
	}

	@ConfigItem(
			position = 11,
			keyName = "nonClanMemberColor",
			name = "Others",
			description = "Color of other players names",
			section = highlightSection
	)
	default Color getOthersColor()
	{
		return Color.RED;
	}

	@ConfigItem(
			position = 12,
			keyName = "drawPlayerHull",
			name = "Draw hulls of players",
			description = "Configures whether or not hulls of highlighted players should be drawn"
	)
	default boolean drawHull()
	{
		return false;
	}

	@ConfigItem(
			position = 13,
			keyName = "drawPlayerTiles",
			name = "Draw tiles under players",
			description = "Configures whether or not tiles under highlighted players should be drawn"
	)
	default boolean drawTiles()
	{
		return false;
	}

	@ConfigItem(
			position = 14,
			keyName = "drawPlayerTL",
			name = "Draw true location tiles under players",
			description = "Configures whether or not true location tiles under highlighted players should be drawn"
	)
	default boolean drawTL()
	{
		return false;
	}

	@ConfigItem(
			position = 15,
			keyName = "playerNamePosition",
			name = "Name position",
			description = "Configures the position of drawn player names, or if they should be disabled"
	)
	default PvPPlayerNameLocation playerNamePosition()
	{
		return PvPPlayerNameLocation.ABOVE_HEAD;
	}

	@ConfigItem(
			position = 16,
			keyName = "drawMinimapNames",
			name = "Draw names on minimap",
			description = "Configures whether or not minimap names for players with rendered names should be drawn"
	)
	default boolean drawMinimapNames()
	{
		return false;
	}

	@ConfigItem(
			position = 17,
			keyName = "colorPlayerMenu",
			name = "Colorize player menu",
			description = "Color right click menu for players"
	)
	default boolean colorPlayerMenu()
	{
		return true;
	}

	@ConfigItem(
			position = 18,
			keyName = "clanMenuIcons",
			name = "Show friends chat ranks",
			description = "Add friends chat rank to right click menu and next to player names"
	)
	default boolean showFriendsChatRanks()
	{
		return true;
	}

	@ConfigItem(
			position = 19,
			keyName = "clanchatMenuIcons",
			name = "Show clan chat ranks",
			description = "Add clan chat rank to right click menu and next to player names"
	)
	default boolean showClanChatRanks() { return true; }

	@ConfigItem(
			position = 20,
			keyName = "playerAlertSound",
			name = "Alert Sound",
			description = "Ding.",
			section = pvpSection
	)
	default boolean playerAlertSound() {
		return false;
	}

	@Range(min = 1, max = 50)
	@ConfigItem(
			position = 21,
			keyName = "playerAlertSoundVolume",
			name = "Alert Sound Volume",
			description = "Ding go loud. Gl your ears (20 is a good volume).",
			section = pvpSection
	)
	default int playerAlertSoundVolume() {
		return 20;
	}

	@ConfigItem(
			position = 22,
			keyName = "highlightTargets",
			name = "Highlight attackable players in wilderness.",
			description = "Highlights players that the current player can attack based on combat/wilderness levels",
			section = pvpSection
	)
	default TargetHighlightMode highlightTargets() {
		return TargetHighlightMode.OFF;
	}

	@ConfigItem(
			position = 23,
			keyName = "targetColor",
			name = "Target color",
			description = "Color of attackable targets",
			section = pvpSection
	)
	default Color getTargetColor() {
		return Color.RED;
	}

	@ConfigItem(
			position = 24,
			keyName = "showCombat",
			name = "Show Combat Levels",
			description = "Show the combat level of attackable players next to their name.",
			section = pvpSection
	)
	default boolean showCombatLevel() {
		return false;
	}

	@ConfigItem(
			position = 25,
			keyName = "showAgility",
			name = "Show Agility Levels",
			description = "Show the agility level of attackable players next to their name while in the wilderness.",
			section = pvpSection
	)
	default boolean showAgilityLevel() {
		return false;
	}

	@ConfigItem(
			position = 26,
			keyName = "agilityFormat",
			name = "Show Agility Format",
			description = "Whether to show the agility level as text, or as icons (1 skull >= 1st threshold, 2 skulls >= 2nd threshold).",
			section = pvpSection
	)
	 default AgilityFormats agilityFormat() {return AgilityFormats.TEXT;}

	@ConfigItem(
			position = 27,
			keyName = "agilityFirstThreshold",
			name = "First Threshold",
			description = "When showing agility as icons, show one icon for agility >= this level.",
			section = pvpSection
	)
	default int agilityFirstThreshold() { return 70; }

	@ConfigItem(
			position = 28,
			keyName = "agilitySecondThreshold",
			name = "Second Threshold",
			description = "When showing agility as icons, show two icons for agility >= this level.",
			section = pvpSection
	)
	default int agilitySecondThreshold() { return 84; }

	@Getter
	@RequiredArgsConstructor
	public enum TargetHighlightMode {
		OFF("Off"),
		HULL("Hull"),
		TILE("Tile"),
		TRUE_LOCATION("True Location");

		private final String name;

		@Override
		public String toString() {
			return name;
		}
	}

	public enum AgilityFormats {
		TEXT, ICONS;
	}
}