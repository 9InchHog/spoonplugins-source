package net.runelite.client.plugins.spoongotr;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("SpoonGOTR")
public interface SpoonGOTRConfig extends Config {
	@ConfigSection(
			name = "Color Pickers",
			description = "Color picker settings",
			position = 0,
			closedByDefault = true
	)
	String colorSection = "colorSection";

	@Alpha
	@ConfigItem(
			keyName = "eleOverlayColor",
			name = "Elemental Color",
			description = "Sets the color of Active Guardian Overlay for elemental runes",
			position = 0,
			section = colorSection
	)
	default Color eleOverlayColor(){ return new Color(255, 0, 0, 20); }

	@Alpha
	@ConfigItem(
			keyName = "cataOverlayColor",
			name = "Catalytic Color",
			description = "Sets the color of Active Guardian Overlay for catalytic runes",
			position = 1,
			section = colorSection
	)
	default Color cataOverlayColor(){ return new Color(255, 0, 0, 20); }

	@Alpha
	@ConfigItem(
			keyName = "hugePortalColor",
			name = "Portal Color",
			description = "Sets the color of Huge Portal Overlay",
			position = 2,
			section = colorSection
	)
	default Color hugePortalColor(){ return new Color(255, 0, 0, 20); }

	@Alpha
	@ConfigItem(
			keyName = "bigGuyColor",
			name = "Big Guy Color",
			description = "Sets the color of Big Guy Overlay",
			position = 3,
			section = colorSection
	)
	default Color bigGuyColor(){ return new Color(255, 0, 0, 20); }

	@Alpha
	@ConfigItem(
			keyName = "unchargedTableColor",
			name = "Uncharged Cell Color",
			description = "Sets the color of Uncharged Cell Table Overlay",
			position = 4,
			section = colorSection
	)
	default Color unchargedTableColor(){ return new Color(255, 0, 0, 20); }

	@Alpha
	@ConfigItem(
			keyName = "essencePileColor",
			name = "Essence Pile Color",
			description = "Sets the color of Essence Pile Overlay",
			position = 5,
			section = colorSection
	)
	default Color essencePileColor(){ return new Color(255, 0, 0, 20); }

	@ConfigItem(
			keyName = "guardianOverlay",
			name = "Active Guardian Overlay",
			description = "Highlights the guardian that is currently active",
			position = 1
	)
	default boolean guardianOverlay(){ return false; }

	@ConfigItem(
			keyName = "showGuardianRune",
			name = "Show Guardian Rune",
			description = "Displays the respective rune on the active guardians",
			position = 2
	)
	default boolean showGuardianRune(){ return false; }

	@ConfigItem(
			keyName = "hugePortal",
			name = "Huge Portal Overlay",
			description = "Highlights the portals to the huge guardian remains",
			position = 3
	)
	default boolean hugePortal(){ return false; }

	@ConfigItem(
			keyName = "portalTimer",
			name = "Portal Timer",
			description = "Displays time until next portal spawns.<br>There is a 10 tick window it can spawn in so anywhere from 0 to -15",
			position = 4
	)
	default boolean portalTimer(){ return false; }

	@ConfigItem(
			keyName = "bigGuyOverlay",
			name = "Big Guy Overlay",
			description = "Highlights the big guy if you have guardian stones",
			position = 5
	)
	default boolean bigGuyOverlay(){ return false; }

	@ConfigItem(
			keyName = "hidePowerUp",
			name = "Hide Power Up Big Guy",
			description = "Removes power-up option on The Great Guardian when you have no guardian stones",
			position = 6
	)
	default boolean hidePowerUp(){ return false; }

	@ConfigItem(
			keyName = "hideNoCell",
			name = "Hide No Cell",
			description = "Removes assemble on essence pile and place-cell on barriers when you have no cell",
			position = 7
	)
	default boolean hideNoCell(){ return false; }

	@ConfigItem(
			keyName = "hideFlashbang",
			name = "Hide Flashbang",
			description = "I can see clearly now....",
			position = 8
	)
	default boolean hideFlashbang(){ return false; }

	@ConfigItem(
			keyName = "instanceTimer",
			name = "Instance Timer",
			description = "Displays how long the kill is taking",
			position = 9
	)
	default boolean instanceTimer(){ return false; }

	@ConfigItem(
			keyName = "unchargedTableOverlay",
			name = "Uncharged Table Overlay",
			description = "Highlights uncharged cell table when you have no uncharged cells",
			position = 10
	)
	default boolean unchargedTableOverlay(){ return false; }

	@ConfigItem(
			keyName = "timeTillStart",
			name = "Time Till Next Game",
			description = "Displays the time until the next game starts",
			position = 11
	)
	default boolean timeTillStart(){ return false; }

	@ConfigItem(
			keyName = "showRewardPoints",
			name = "Show Reward Points",
			description = "Displays your total elemental and catalytic reward points after games",
			position = 12
	)
	default boolean showRewardPoints(){ return false; }

	@ConfigItem(
			keyName = "essencePileOverlay",
			name = "Essence Pile Overlay",
			description = "Highlights the essence piles when there are less than the cap spawned",
			position = 13
	)
	default boolean essencePileOverlay(){ return false; }

	@ConfigItem(
			keyName = "guardiansOfTheRave",
			name = "Guardians of the Rave",
			description = "",
			position = 99
	)
	default RaveMode guardiansOfTheRave(){ return RaveMode.OFF; }

	enum RaveMode {
		OFF, RAVE, RAVEST, HELP
	}
}
