package net.runelite.client.plugins.theatre;

import java.awt.Color;
import java.awt.Font;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.plugins.theatre.Maiden.MaidenMaxHit;

@ConfigGroup("Theatre")
public interface TheatreConfig extends Config
{
	//Config Sections
	@ConfigSection(
		name = "General",
		description = "General Configurartion",
		position = 0,
		keyName = "generalSection",
		closedByDefault = true
	)
	String generalSection = "General";
	
	@ConfigSection(
		name = "Maiden",
		description = "Maiden's Configuration",
		position = 1,
		keyName = "maidenSection",
		closedByDefault = true
	)
	String maidenSection = "Maiden";
	
	@ConfigSection(
		name = "Bloat",
		description = "Bloat's Configuration",
		position = 2,
		keyName = "bloatSection",
		closedByDefault = true
	)
	String bloatSection = "Bloat";
	
	@ConfigSection(
		name = "Nylocas",
		description = "Nylocas' Configuration",
		position = 3,
		keyName = "nylocasSection",
		closedByDefault = true
	)
	String nylocasSection = "Nylocas";
	
	@ConfigSection(
		name = "Sotetseg",
		description = "Sotetseg's Configuration",
		position = 4,
		keyName = "sotetsegSection",
		closedByDefault = true
	)
	String sotetsegSection = "Sotetseg";
	
	@ConfigSection(
		name = "Xarpus",
		description = "Xarpus's Configuration",
		position = 5,
		keyName = "xarpusSection",
		closedByDefault = true
	)
	String xarpusSection = "Xarpus";
	
	@ConfigSection(
		name = "Verzik",
		description = "Verzik's Configuration",
		position = 6,
		keyName = "verzikSection",
		closedByDefault = true
	)
	String verzikSection = "Verzik";

	//General Section
	@Range(max = 20)
	@ConfigItem(
		position = 0,
		keyName = "theatreFontSize",
		name = "Theatre Overlay Font Size",
		description = "Sets the font size for all theatre text overlays.",
		section = generalSection
	)
	default int theatreFontSize()
	{
		return 12;
	}

	@ConfigItem(
		keyName = "fontStyle",
		name = "Font Style",
		description = "Bold/Italics/Plain.",
		position = 1,
		section = generalSection
	)
	default FontStyle fontStyle()
	{
		return FontStyle.BOLD;
	}

	//Maiden Section
	@ConfigItem(
		position = 0,
		keyName = "maidenBlood",
		name = "Maiden Blood Attack Marker",
		description = "Highlights Maiden's Blood Pools.",
		section = maidenSection
	)
	default boolean maidenBlood()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "maidenSpawns",
		name = "Maiden Blood Spawns Marker",
		description = "Highlights Maiden Blood Spawns (Tomatoes).",
		section = maidenSection
	)
	default boolean maidenSpawns()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "maidenReds",
		name = "Maiden Reds Health Overlay",
		description = "Displays the health of each red crab.",
		section = maidenSection
	)
	default boolean maidenRedsHealth()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "maidenRedsHealthMenu",
		name = "Maiden Reds Health Menu",
		description = "Displays the health of each red crab in right click menu entry.",
		section = maidenSection
	)
	default boolean maidenRedsHealthMenu()
	{
		return true;
	}

	@ConfigItem(
		position = 4,
		keyName = "maidenRedsDistance",
		name = "Maiden Reds Distance Overlay",
		description = "Displays the distance of each red crab to reach Maiden.",
		section = maidenSection
	)
	default boolean maidenRedsDistance()
	{
		return true;
	}

	@ConfigItem(
		position = 5,
		name = "Maiden Reds Freeze Timers",
		keyName = "maidenRedsFreezeTimers",
		description = "Displays how long each red crab is frozen for",
		section = maidenSection
	)
	default boolean maidenRedsFreezeTimers()
	{
		return false;
	}

	@ConfigItem(
		position = 6,
		name = "Maiden Reds Scuffed Indicators",
		keyName = "maidenRedsSpawnIndicators",
		description = "Displays polygons/shapes on each red crab if scuffed spawn",
		section = maidenSection
	)
	default RenderingTypes maidenRedsSpawnIndicators() {
		return RenderingTypes.OFF;
	}

	@ConfigItem(
		position = 7,
		name = "Scuffed Color",
		keyName = "maidenScuffedColor",
		description = "Configures the color for 'Scuffed' spawned states for 'Reds Spawn Indicators'",
		section = maidenSection
	)
	@Alpha
	default Color maidenScuffedColor() {
		return Color.RED;
	}

	@ConfigItem(
		position = 8,
		keyName = "MaidenTickCounter",
		name = "Maiden Tank Tick Counter",
		description = "Displays the tick counter for when she decides who to choose for tanking.",
		section = maidenSection
	)
	default boolean maidenTickCounter()
	{
		return true;
	}

	@ConfigItem(
		position = 9,
		keyName = "maidenProcThreshold",
		name = "Maiden Proc Threshold",
		description = "Shows a rough estimate on the damage needed to proc next phase",
		section = maidenSection
	)
	default boolean maidenProcThreshold()
	{
		return true;
	}

	@ConfigItem(
		position = 10,
		name = "Maiden Max Hit (Tooltip)",
		keyName = "maidenMaxHit",
		description = "When hovering over Maiden's clickbox it will display her max hits for:<br>No Prayer<br>Prayer<br>Elysian Spirit Shield",
		section = maidenSection
	)
	default boolean maidenMaxHit() {
		return false;
	}

	@ConfigItem(
		position = 11,
		name = "Maiden Max Hit (Overlay)",
		keyName = "maidenMaxHitOverlay",
		description = "Overlay that will display her max hits for:<br>No Prayer<br>Prayer<br>Elysian Spirit Shield",
		section = maidenSection
	)
	default MaidenMaxHit maidenMaxHitOverlay() {
		return MaidenMaxHit.OFF;
	}

	@ConfigItem(
		position = 12,
		name = "Blood Spawn MES",
		keyName = "maidenBloodSpawnsMES",
		description = "Removes the Ice Barrage and Ice Blitz cast option on Blood Spawns",
		section = maidenSection
	)
	default boolean maidenBloodSpawnsMES() {
		return false;
	}

	//Bloat Section
	@ConfigItem(
		position = 0,
		keyName = "bloatIndicator",
		name = "Bloat Tile Indicator",
		description = "Highlights Bloat's Tile.",
		section = bloatSection
	)
	default boolean bloatIndicator()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = "bloatIndicatorColorUP",
		name = "Bloat Indicator Color - UP",
		description = "Select a color for when Bloat is UP.",
		section = bloatSection
	)
	default Color bloatIndicatorColorUP()
	{
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(
		position = 2,
		keyName = "bloatIndicatorColorTHRESH",
		name = "Bloat Indicator Color - THRESHOLD",
		description = "Select a color for when Bloat UP and goes over 37 ticks, which allows you to know when he can go down.",
		section = bloatSection
	)
	default Color bloatIndicatorColorTHRESH()
	{
		return Color.ORANGE;
	}

	@Alpha
	@ConfigItem(
		position = 3,
		keyName = "bloatIndicatorColorDOWN",
		name = "Bloat Indicator Color - DOWN",
		description = "Select a color for when Bloat is DOWN.",
		section = bloatSection
	)
	default Color bloatIndicatorColorDOWN()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		position = 4,
		keyName = "bloatIndicatorColorWARN",
		name = "Bloat Indicator Color - WARN",
		description = "Select a color for when Bloat is DOWN and about to get UP.",
		section = bloatSection
	)
	default Color bloatIndicatorColorWARN()
	{
		return Color.RED;
	}

	@ConfigItem(
		position = 5,
		keyName = "bloatTickCounter",
		name = "Bloat Tick Counter",
		description = "Displays the tick counter for how long Bloat has been DOWN or UP.",
		section = bloatSection
	)
	default boolean bloatTickCounter()
	{
		return true;
	}
	
	@ConfigItem(
		position = 6,
		keyName = "BloatTickCountStyle",
		name = "Bloat Tick Time Style",
		description = "Count up or Count down options on bloat downed state",
		section = bloatSection
	)
	default BLOATTIMEDOWN BloatTickCountStyle()
	{
		return BLOATTIMEDOWN.COUNTDOWN;
	}

	@ConfigItem(
		position = 7,
		keyName = "bloatHands",
		name = "Bloat Hands Overlay",
		description = "Highlights the tiles where Bloat's hands will fall.",
		section = bloatSection
	)
	default boolean bloatHands()
	{
		return true;
	}

	@ConfigItem(
		position = 8,
		keyName = "bloatHandsTickCounter",
		name = "Bloat Hands Tick Counter",
		description = "Tick counter for Bloat Hands.",
		section = bloatSection
	)
	default boolean bloatHandsTickCounter()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		position = 9,
		keyName = "bloatHandsColor",
		name = "Bloat Hands Overlay Color",
		description = "Select a color for the Bloat Hands Overlay to be.",
		section = bloatSection
	)
	default Color bloatHandsColor()
	{
		return Color.CYAN;
	}

	@Range(max = 10)
	@ConfigItem(
		position = 10,
		keyName = "bloatHandsWidth",
		name = "Bloat Hands Overlay Thickness",
		description = "Sets the stroke width of the tile overlay where the hands fall. (BIGGER = THICKER).",
		section = bloatSection
	)
	default int bloatHandsWidth()
	{
		return 2;
	}

	@ConfigItem(
		position = 11,
		name = "Hide Bloat Tank",
		keyName = "hideBloatTank",
		description = "Hides the entire Bloat tank in the center of the room",
		section = bloatSection
	)
	default boolean hideBloatTank()
	{
		return false;
	}

	@ConfigItem(
		position = 12,
		name = "Hide Ceiling Chains",
		keyName = "hideCeilingChains",
		description = "Hides the chains hanging from the ceiling in the Bloat room",
		section = bloatSection
	)
	default boolean hideCeilingChains()
	{
		return false;
	}

	@ConfigItem(
		position = 13,
		name = "Stomp Safespot Lines",
		keyName = "bloatStompSafespots",
		description = "Displays two 'Safespot Lines' to show the safe tiles for when Bloat goes down<br>If ANY are incorrect, please screenshot the exact moment or record it.",
		section = bloatSection
	)
	default boolean bloatStompSafespots() {
		return false;
	}

	@ConfigItem(
		position = 14,
		name = "SS Lines Color",
		keyName = "bloatStompSafespotsColor",
		description = "Configures the color for Bloat's Stomp Safespot Lines",
		section = bloatSection
	)
	@Alpha
	default Color bloatStompSafespotsColor() {
		return new Color(255, 0, 255, 255);
	}

	//Nylocas Section
	@ConfigItem(
		position = 0,
		keyName = "nyloPillars",
		name = "Nylocas Pillar Health Overlay",
		description = "Displays the health percentage of the pillars.",
		section = nylocasSection
	)
	default boolean nyloPillars()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "nyloExplosions",
		name = "Nylocas Explosion Warning",
		description = "Highlights a Nylocas that is about to explode.",
		section = nylocasSection
	)
	default boolean nyloExplosions()
	{
		return true;
	}

	@Range(max = 52)
	@ConfigItem(
		position = 2,
		keyName = "nyloExplosionDisplayTicks",
		name = "Nylocas Display Last Ticks",
		description = "Displays the last 'x' amount of ticks for a Nylocas. (ex: to see the last 10 ticks, you set it to 10).",
		section = nylocasSection
	)
	default int nyloExplosionDisplayTicks()
	{
		return 52;
	}

	@ConfigItem(
		position = 3,
		keyName = "nyloExplosionDisplayStyle",
		name = "Nylocas Display Explosion Style",
		description = "How to display when a nylocas is about to explode.",
		section = nylocasSection
	)
	default EXPLOSIVENYLORENDERSTYLE nyloExplosionOverlayStyle()
	{
		return EXPLOSIVENYLORENDERSTYLE.TILE;
	}

	@ConfigItem(
		position = 4,
		keyName = "nyloTimeAlive",
		name = "Nylocas Tick Time Alive",
		description = "Displays the tick counter of each nylocas spawn (Explodes on 52).",
		section = nylocasSection
	)
	default boolean nyloTimeAlive()
	{
		return true;
	}

	@ConfigItem(
		position = 5,
		keyName = "nyloTimeAliveCountStyle",
		name = "Nylocas Tick Time Alive Style",
		description = "Count up or Count down options on the tick time alive.",
		section = nylocasSection
	)
	default NYLOTIMEALIVE nyloTimeAliveCountStyle()
	{
		return NYLOTIMEALIVE.COUNTUP;
	}

	@ConfigItem(
		position = 6,
		keyName = "nyloTimeAliveMenu",
		name = "Nylocas Time Alive Menu Options",
		description = "Displays the time alive of the Nylocas in the right click menu option",
		section = nylocasSection
	)
	default boolean nyloTimeAliveMenu()
	{
		return false;
	}

	@ConfigItem(
		position = 7,
		keyName = "nyloRecolorMenu",
		name = "Nylocas Recolor Menu Options",
		description = "Recolors the menu options of each Nylocas to it's respective attack style.",
		section = nylocasSection
	)
	default boolean nyloRecolorMenu()
	{
		return true;
	}

	@ConfigItem(
		position = 8,
		keyName = "nyloHighlightOverlay",
		name = "Nylocas Highlight Overlay",
		description = "Select your role to highlight respective Nylocas to attack.",
		section = nylocasSection
	)
	default boolean nyloHighlightOverlay()
	{
		return true;
	}

	@Range(min = 1, max = 3)
	@ConfigItem(
		position = 9,
		keyName = "nyloTileWidth",
		name = "Nylocas Tile Width",
		description = "",
		section = nylocasSection
	)
	default int nyloTileWidth()
	{
		return 1;
	}

	@ConfigItem(
		position = 10,
		keyName = "nyloAliveCounter",
		name = "Nylocas Alive Counter Panel",
		description = "Displays how many Nylocas are currently alive.",
		section = nylocasSection
	)
	default boolean nyloAlivePanel()
	{
		return true;
	}

	@ConfigItem(
		position = 11,
		keyName = "nyloAggressiveOverlay",
		name = "Highlight Aggressive Nylocas",
		description = "Highlights aggressive Nylocas after they spawn.",
		section = nylocasSection
	)
	default boolean nyloAggressiveOverlay()
	{
		return true;
	}

	@ConfigItem(
		position = 12,
		keyName = "nyloAggressiveOverlayStyle",
		name = "Highlight Aggressive Nylocas Style",
		description = "Highlight style for aggressive Nylocas after they spawn.",
		section = nylocasSection
	)
	default AGGRESSIVENYLORENDERSTYLE nyloAggressiveOverlayStyle()
	{
		return AGGRESSIVENYLORENDERSTYLE.TILE;
	}

	@ConfigItem(
		position = 13,
		keyName = "removeNyloEntries",
		name = "Remove Attack Options",
		description = "Removes the attack options for Nylocas immune to your current attack style.",
		section = nylocasSection
	)
	default boolean removeNyloEntries()
	{
		return true;
	}

	@ConfigItem(
		position = 14,
		keyName = "removeNyloChinsEntries",
		name = "Remove Chins Attack Options",
		description = "Removes the attack options for Nylocas immune to your chins.",
		section = nylocasSection
	)
	default boolean removeNyloChinsEntries()
	{
		return false;
	}

	@ConfigItem(
		position = 15,
		keyName = "removeNyloBossEntries",
		name = "Remove Boss Attack Options",
		description = "Removes the attack options for the Nylocas Boss immune to your current attack style.",
		section = nylocasSection
	)
	default boolean removeNyloBossEntries()
	{
		return false;
	}

	@ConfigItem(
		position = 16,
		keyName = "nylocasWavesHelper",
		name = "Nylocas Waves Helper",
		description = "Overlay's squares with wave numbers on nylo entry bridges for upcoming nylos",
		section = nylocasSection
	)
	default boolean nyloWavesHelper()
	{
		return false;
	}

	@ConfigItem(
		position = 17,
		keyName = "nylocasTicksUntilWave",
		name = "Nylocas Ticks Until Wave",
		description = "Prints how many ticks until the next wave could spawn",
		section = nylocasSection
	)
	default boolean nyloTicksUntilWaves()
	{
		return false;
	}

	@ConfigItem(
		position = 18,
		keyName = "nyloInstanceTimer",
		name = "Nylocas Instance Timer",
		description = "Displays an instance timer when the next set will potentially spawn - ENTER ON ZERO.",
		section = nylocasSection
	)
	default boolean nyloInstanceTimer()
	{
		return true;
	}

	@ConfigItem(
		position = 19,
		keyName = "nyloStallMessage",
		name = "Nylocas Stall Wave Messages",
		description = "Sends a chat message when you have stalled the next wave of Nylocas to spawn due to being capped.",
		section = nylocasSection
	)
	default boolean nyloStallMessage()
	{
		return true;
	}

	@ConfigItem(
		position = 20,
		keyName = "nylocasBigSplitsHelper",
		name = "Nylocas Big Splits",
		description = "Tells you when bigs will spawn little nylos",
		section = nylocasSection
	)
	default boolean bigSplits()
	{
		return false;
	}

	@ConfigItem(
		position = 21,
		keyName = "nylocasBigSplitsHighlightColor",
		name = "Highlight Color",
		description = "Color of the NPC highlight",
		section = nylocasSection,
		hidden = true,
		unhide = "nylocasBigSplitsHelper"
	)
	@Alpha
	default Color getBigSplitsHighlightColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
		position = 22,
		keyName = "nylocasBigSplitsTileColor2",
		name = "Highlight Color Tick 2",
		description = "Color of the NPC highlight on tick 1",
		section = nylocasSection,
		hidden = true,
		unhide = "nylocasBigSplitsHelper"
	)
	@Alpha
	default Color getBigSplitsTileColor2()
	{
		return Color.ORANGE;
	}

	@ConfigItem(
		position = 23,
		keyName = "nylocasBigSplitsTileColor1",
		name = "Highlight Color Tick 1",
		description = "Color of the NPC highlight on tick 0",
		section = nylocasSection,
		hidden = true,
		unhide = "nylocasBigSplitsHelper"
	)
	@Alpha
	default Color getBigSplitsTileColor1()
	{
		return Color.RED;
	}

	@ConfigItem(
		position = 24,
		keyName = "nylocasBigSplitsTextColor2",
		name = "Text Color Tick 2",
		description = "Color of the baby tick counter on tick 2",
		section = nylocasSection,
		hidden = true,
		unhide = "nylocasBigSplitsHelper"
	)
	@Alpha
	default Color getBigSplitsTextColor2()
	{
		return Color.ORANGE;
	}

	@ConfigItem(
		position = 25,
		keyName = "nylocasBigSplitsTextColor1",
		name = "Text Color Tick 1",
		description = "Color of the baby tick counter on tick 1",
		section = nylocasSection,
		hidden = true,
		unhide = "nylocasBigSplitsHelper"
	)
	@Alpha
	default Color getBigSplitsTextColor1()
	{
		return Color.RED;
	}

	@ConfigItem(
		position = 26,
		keyName = "nyloBossAttackTickCount",
		name = "Nylocas Boss Attack Tick Counter",
		description = "Displays the ticks left until the Nylocas Boss will attack next (LEFT-MOST).",
		section = nylocasSection
	)
	default boolean nyloBossAttackTickCount()
	{
		return false;
	}

	@ConfigItem(
		position = 27,
		keyName = "nyloBossSwitchTickCount",
		name = "Nylocas Boss Switch Tick Counter",
		description = "Displays the ticks left until the Nylocas Boss will switch next (MIDDLE).",
		section = nylocasSection
	)
	default boolean nyloBossSwitchTickCount()
	{
		return true;
	}

	@ConfigItem(
		position = 28,
		keyName = "nyloBossTotalTickCount",
		name = "Nylocas Boss Total Tick Counter",
		description = "Displays the total ticks since the Nylocas Boss has spawned (RIGHT-MOST).",
		section = nylocasSection
	)
	default boolean nyloBossTotalTickCount()
	{
		return false;
	}

	@ConfigItem(
		position = 29,
		name = "Hide Pillars",
		keyName = "nyloHidePillars",
		description = "Hides the Nylocas Pillars in the Nylocas Room<br>Disabling this feature whilst in the Nylocas room will cause a stutter to refresh the scene",
		section = nylocasSection
	)
	default boolean nyloHidePillars() {
		return false;
	}

	//Sotetseg Section
	@ConfigItem(
		position = 0,
		keyName = "sotetsegMaze",
		name = "Sotetseg Maze",
		description = "Memorizes Solo Mazes and displays tiles of other chosen players.",
		section = sotetsegSection
	)
	default boolean sotetsegMaze()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "sotetsegOrbAttacksTicks",
		name = "Sotetseg Small Attack Orb Ticks",
		description = "Displays the amount of ticks until it will hit you (change prayers when you see 1).",
		section = sotetsegSection
	)
	default boolean sotetsegOrbAttacksTicks()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "sotetsegAutoAttacksTicks",
		name = "Sotetseg Auto Attack Ticks",
		description = "Displays a tick counter for when Sotetseg will attack next.",
		section = sotetsegSection
	)
	default boolean sotetsegAutoAttacksTicks()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "sotetsegAttackCounter",
		name = "Sotetseg Attack Counter",
		description = "Countdown until death ball.",
		section = sotetsegSection
	)
	default boolean sotetsegAttackCounter()
	{
		return true;
	}

	@ConfigItem(
		position = 4,
		keyName = "sotetsegBigOrbTicks",
		name = "Sotetseg Big Ball Tick Overlay",
		description = "Displays how many ticks until the ball will explode (eat when you see 0).",
		section = sotetsegSection
	)
	default boolean sotetsegBigOrbTicks()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		position = 5,
		keyName = "sotetsegBigOrbTickColor",
		name = "Sotetseg Big Ball Tick Color",
		description = "Select a color for the Sotetseg Big Ball tick countdown text.",
		section = sotetsegSection
	)
	default Color sotetsegBigOrbTickColor()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
		position = 6,
		keyName = "sotetsegBigOrbTileColor",
		name = "Sotetseg Big Ball Tile Color",
		description = "Select a color for the Sotetseg Big Ball tile color.",
		section = sotetsegSection
	)
	default Color sotetsegBigOrbTileColor()
	{
		return new Color(188, 74, 74);
	}

	@ConfigItem(
		position = 7,
		keyName = "sotetsegOrbIcons",
		name = "Sotetseg Orb Icons",
		description = "Small orb icons",
		section = sotetsegSection
	)
	default SOTETSEGORBICONS sotetsegOrbIcons()
	{
		return SOTETSEGORBICONS.ALL;
	}

	//Xarpus Section
	@ConfigItem(
		position = 0,
		keyName = "xarpusInstanceTimer",
		name = "Xarpus Instance Timer",
		description = "Displays the Xarpus Instance timer to be tick efficient with the first spawn of an exhumed - ENTER ON ZERO.",
		section = xarpusSection
	)
	default boolean xarpusInstanceTimer()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "xarpusExhumed",
		name = "Xarpus Exhumed Markers",
		description = "Highlights the tiles of exhumed spawns.",
		section = xarpusSection
	)
	default boolean xarpusExhumed()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "xarpusExhumedTick",
		name = "Xarpus Exhumed Ticks",
		description = "Displays how many ticks until the exhumeds will despawn.",
		section = xarpusSection
	)
	default boolean xarpusExhumedTick()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "xarpusExhumedCount",
		name = "Xarpus Exhumed Infobox Count",
		description = "Displays and counts down the exhumeds",
		section = xarpusSection
	)
	default boolean xarpusExhumedCount()
	{
		return true;
	}

	@ConfigItem(
		position = 4,
		keyName = "xarpusExhumedCountOverlay",
		name = "Xarpus Exhumed Overlay Count",
		description = "Displays and counts down the exhumeds",
		section = xarpusSection
	)
	default boolean xarpusExhumedCountOverlay()
	{
		return false;
	}

	@ConfigItem(
		position = 5,
		keyName = "xarpusHealingCount",
		name = "Xarpus Healing Infobox Count",
		description = "Displays the healing done from exhumeds",
		section = xarpusSection
	)
	default boolean xarpusHealingCount()
	{
		return true;
	}

	@ConfigItem(
		position = 6,
		keyName = "xarpusTickP2",
		name = "Xarpus Attack Tick - P2",
		description = "Displays a tick counter for when Xarpus faces a new target to spit at.",
		section = xarpusSection
	)
	default boolean xarpusTickP2()
	{
		return true;
	}

	@ConfigItem(
		position = 7,
		keyName = "xarpusTickP3",
		name = "Xarpus Attack Tick - P3",
		description = "Displays a tick counter for when Xarpus will rotate.",
		section = xarpusSection
	)
	default boolean xarpusTickP3()
	{
		return true;
	}

	@ConfigItem(
		position = 8,
		name = "Line of Sight",
		keyName = "xarpusLineOfSight",
		description = "Displays Xarpus's Line of Sight on P3<br>Melee Tiles: Displays only the melee tiles that Xarpus can see<br>Square: Displays the whole region that Xarpus can see",
		section = xarpusSection
	)
	default XARPUS_LINE_OF_SIGHT xarpusLineOfSight()
	{
		return XARPUS_LINE_OF_SIGHT.OFF;
	}

	@Alpha
	@ConfigItem(
		position = 9,
		name = "Line of Sight Color",
		keyName = "xarpusLineOfSightColor",
		description = "Customize the color for Xarpus's Line of Sight",
		section = xarpusSection
	)
	default Color xarpusLineOfSightColor()
	{
		return Color.RED;
	}

	//Verzik Section
	@ConfigItem(
		position = 0,
		keyName = "verzikTileOverlay",
		name = "Verzik Tile Indicator",
		description = "Highlights Verzik's tile - If you are next to or inside of the indicator, you can be meleed.",
		section = verzikSection
	)
	default boolean verzikTileOverlay()
	{
		return true;
	}

	@ConfigItem(
		position = 0,
		keyName = "verzikTileOverlayP3",
		name = "Verzik Tile Indicator P3 Only",
		description = "Highlights Verzik's tile P3 ONLY - If you are next to or inside of the indicator, you can be meleed.",
		section = verzikSection
	)
	default boolean verzikTileOverlayP3()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "verzikProjectiles",
		name = "Verzik Range Tile Markers",
		description = "Highlights the tiles of Verzik's range projectiles.",
		section = verzikSection
	)
	default boolean verzikProjectiles()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "verzikProjectilesTicks",
		name = "Verzik Range Tile Tick Counter",
		description = "Shows a tick counter for Verzik's range projectiles.",
		section = verzikSection
	)
	default boolean verzikProjectilesTicks()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		position = 3,
		keyName = "verzikProjectilesColor",
		name = "Verzik Range Tile Markers Color",
		description = "Select a color for the Verzik's Range Projectile Tile Overlay to be.",
		section = verzikSection
	)
	default Color verzikProjectilesColor()
	{
		return new Color(255, 0, 0, 50);
	}

	@ConfigItem(
		position = 4,
		keyName = "VerzikRedHP",
		name = "Verzik Reds Health Overlay",
		description = "Displays the health of red crabs during Verzik.",
		section = verzikSection
	)
	default boolean verzikReds()
	{
		return true;
	}

	@ConfigItem(
		position = 5,
		keyName = "verzikAutosTick",
		name = "Verzik Attack Tick Counter",
		description = "Displays the ticks until Verzik will attack next.",
		section = verzikSection
	)
	default boolean verzikAutosTick()
	{
		return true;
	}

	@ConfigItem(
		position = 6,
		keyName = "verzikAttackCounter",
		name = "Verzik Attack Counter",
		description = "Displays Verzik's Attack Count (useful for when P2 reds as they despawn after the 7th attack).",
		section = verzikSection
	)
	default boolean verzikAttackCounter()
	{
		return true;
	}

	@ConfigItem(
		position = 7,
		keyName = "verzikTotalTickCounter",
		name = "Verzik Total Tick Counter",
		description = "Displays the total amount of ticks Verzik has been alive for.",
		section = verzikSection
	)
	default boolean verzikTotalTickCounter()
	{
		return true;
	}

	@ConfigItem(
		position = 8,
		keyName = "verzikNyloPersonalWarning",
		name = "Verzik Nylo Direct Aggro Warning",
		description = "Highlights the Nylocas that are targeting YOU and ONLY you.",
		section = verzikSection
	)
	default boolean verzikNyloPersonalWarning()
	{
		return true;
	}

	@ConfigItem(
		position = 9,
		keyName = "verzikNyloOtherWarning",
		name = "Verzik Nylo Indirect Aggro Warnings",
		description = "Highlights the Nylocas that are targeting OTHER players.",
		section = verzikSection
	)
	default boolean verzikNyloOtherWarning()
	{
		return true;
	}

	@ConfigItem(
		position = 10,
		keyName = "lightningAttackHelper",
		name = "Lightning Attack Helper",
		description = "Displays the number of attacks before a lightning ball.",
		section = verzikSection
	)
	default boolean lightningAttackHelper()
	{
		return true;
	}

	@ConfigItem(
		position = 11,
		keyName = "lightningAttackTick",
		name = "Lightning Attack Tick",
		description = "Displays the number of ticks before a lightning ball hits you.",
		section = verzikSection
	)
	default boolean lightningAttackTick()
	{
		return true;
	}

	@ConfigItem(
		position = 12,
		keyName = "verzikAttackPurpleNyloMES",
		name = "Remove Purple Nylo MES",
		description = "Removes the ability to attack the Purple nylo if you cannot poison it",
		section = verzikSection
	)
	default boolean purpleCrabAttackMES()
	{
		return false;
	}

	@ConfigItem(
		position = 13,
		keyName = "weaponSet",
		name = "Poison Weapons",
		description = "If a weapon is added to this set, it will NOT deprio attack on Nylocas Athanatos.",
		section = verzikSection
	)
	default String weaponSet()
	{
		return "12926, 12006, 22292, 12899";
	}

	@ConfigItem(
		position = 13,
		keyName = "verzikNyloExplodeAOE",
		name = "Verzik Nylo Explosion Area",
		description = "Highlights the area of explosion for the Nylocas (Personal or Indirect Warnings MUST be enabled).",
		section = verzikSection
	)
	default boolean verzikNyloExplodeAOE()
	{
		return true;
	}

	@ConfigItem(
		position = 14,
		keyName = "verzikDisplayTank",
		name = "Verzik Display Tank",
		description = "Highlights the tile of the player tanking to help clarify.",
		section = verzikSection
	)
	default boolean verzikDisplayTank()
	{
		return true;
	}

	@ConfigItem(
		position = 15,
		keyName = "verzikYellows",
		name = "Verzik Yellows Overlay",
		description = "Highlights the yellow pools and displays the amount of ticks until you can move away or tick eat.",
		section = verzikSection
	)
	default boolean verzikYellows()
	{
		return true;
	}

	@ConfigItem(
		position = 16,
		keyName = "verzikRemoveAttackYellows",
		name = "Verzik Remove Attack On Yellows",
		description = "Remove the attack option on Verzik during Yellows.",
		section = verzikSection
	)
	default boolean verzikRemoveAttackYellows()
	{
		return false;
	}

	@ConfigItem(
		position = 17,
		keyName = "verzikGreenBall",
		name = "Verzik Green Ball Tank",
		description = "Displays who the green ball is targeting.",
		section = verzikSection
	)
	default boolean verzikGreenBall()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		position = 18,
		keyName = "verzikGreenBallColor",
		name = "Verzik Green Ball Highlight Color",
		description = "Select a color for the Verzik's Green Ball Tile Overlay to be.",
		section = verzikSection
	)
	default Color verzikGreenBallColor()
	{
		return new Color(59, 140, 83);
	}

	@ConfigItem(
		position = 19,
		keyName = "verzikGreenBallMarker",
		name = "Verzik Green Ball Marker",
		description = "Choose between a tile or 3-by-3 area marker.",
		section = verzikSection
	)
	default VERZIKBALLTILE verzikGreenBallMarker()
	{
		return VERZIKBALLTILE.TILE;
	}

	@ConfigItem(
		position = 20,
		keyName = "verzikGreenBallTick",
		name = "Verzik Green Ball Tick",
		description = "Displays the number of ticks until the green ball nukes you.",
		section = verzikSection
	)
	default boolean verzikGreenBallTick()
	{
		return true;
	}

	@ConfigItem(
		position = 21,
		keyName = "verzikTornado",
		name = "Verzik Personal Tornado Highlight",
		description = "Displays the tornado that is targeting you.",
		section = verzikSection
	)
	default boolean verzikTornado()
	{
		return true;
	}

	@ConfigItem(
		position = 22,
		keyName = "verzikPersonalTornadoOnly",
		name = "Verzik ONLY Highlight Personal",
		description = "Displays the tornado that is targeting you ONLY after it solves which one is targeting you.",
		section = verzikSection
	)
	default boolean verzikPersonalTornadoOnly()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		position = 23,
		keyName = "verzikTornadoColor",
		name = "Verzik Tornado Highlight Color",
		description = "Select a color for the Verzik Tornadoes Overlay to be.",
		section = verzikSection
	)
	default Color verzikTornadoColor()
	{
		return Color.RED;
	}

	//Miscellaneous Configuration Items
	@ConfigItem(
		keyName = "highlightMelee",
		name = "",
		description = "",
		hidden = true
	)
	default boolean getHighlightMeleeNylo()
	{
		return false;
	}

	@ConfigItem(
		keyName = "highlightMelee",
		name = "",
		description = "",
		hidden = true
	)
	void setHighlightMeleeNylo(boolean set);

	@ConfigItem(
		keyName = "highlightMage",
		name = "",
		description = "",
		hidden = true
	)
	default boolean getHighlightMageNylo()
	{
		return false;
	}

	@ConfigItem(
		keyName = "highlightMage",
		name = "",
		description = "",
		hidden = true
	)
	void setHighlightMageNylo(boolean set);

	@ConfigItem(
		keyName = "highlightRange",
		name = "",
		description = "",
		hidden = true
	)
	default boolean getHighlightRangeNylo()
	{
		return false;
	}

	@ConfigItem(
		keyName = "highlightRange",
		name = "",
		description = "",
		hidden = true
	)
	void setHighlightRangeNylo(boolean set);

	@Getter(AccessLevel.PACKAGE)
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
			return getName();
		}
	}
	
	enum BLOATTIMEDOWN
	{
		COUNTUP,
		COUNTDOWN
	}

	enum NYLOTIMEALIVE
	{
		COUNTUP,
		COUNTDOWN
	}

	enum EXPLOSIVENYLORENDERSTYLE
	{
		TILE,
		RECOLOR_TICK
	}

	enum AGGRESSIVENYLORENDERSTYLE
	{
		TILE,
		HULL
	}

	enum XARPUS_LINE_OF_SIGHT
	{
		OFF,
		SQUARE,
		MELEE_TILES;
	}

	enum VERZIKBALLTILE
	{
		TILE,
		AREA
	}

	enum SOTETSEGORBICONS
	{
		ALL,
		YOURS,
		OFF
	}

	enum RenderingTypes
	{
		OFF,
		TILE,
		HULL,
		OUTLINE
	}
}
