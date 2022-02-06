package net.runelite.client.plugins.spoonnex;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("SpoonNex")
public interface SpoonNexConfig extends Config {
	@ConfigSection(
			name = "Specials",
			description = "Configuration for Specials",
			position = 99,
			closedByDefault = true
	)
	String specSection = "Specials";

	@ConfigSection(
			name = "Timer",
			description = "Configuration for Kill/Phase Timers",
			position = 100,
			closedByDefault = true
	)
	String timerSection = "Timer";

	@ConfigItem(
			keyName = "attacksTilSpecial",
			name = "Attacks Until Special",
			description = "Shows attacks until Nex uses her next special<br>Last Warning turns red when next attack is a special",
			position = 0,
			section = specSection
	)
	default AttacksTilSpecialMode attacksTilSpecial(){ return AttacksTilSpecialMode.OFF; }

	@ConfigItem(
			keyName = "specialTicks",
			name = "Special Ticks",
			description = "Displays ticks until special is over next to Attacks Until Special",
			position = 1,
			section = specSection
	)
	default boolean specialTicks(){ return false; }

	@ConfigItem(
			keyName = "virus",
			name = "Infected Players",
			description = "Down with the sickness... Oh, ah, ah, ah, ah",
			position = 2,
			section = specSection
	)
	default VirusMode virus(){ return VirusMode.OFF; }

	@ConfigItem(
			keyName = "noEscape",
			name = "No Escape Sound",
			description = "A10 Strafe 2: Electric Boogaloo",
			position = 3,
			section = specSection
	)
	default NoEscapeMode noEscape(){ return NoEscapeMode.NEX; }

	@ConfigItem(
			keyName = "noEscapeRunway",
			name = "No Escape Runway",
			description = "Gotta go fast",
			position = 4,
			section = specSection
	)
	default NoEscapeRunwayMode noEscapeRunway(){ return NoEscapeRunwayMode.OFF; }

	@ConfigItem(
			keyName = "noEscapeRunwayColor",
			name = "No Escape Color",
			description = "Sets color of No Escape Runway",
			position = 5,
			section = specSection
	)
	default Color noEscapeRunwayColor(){ return Color.RED; }

	@ConfigItem(
			keyName = "shadowSpots",
			name = "Shadow Spots",
			description = "Highlights shadow spots on the ground during shadow phase",
			position = 6,
			section = specSection
	)
	default ShadowsMode shadowSpots(){ return ShadowsMode.OFF; }

	@ConfigItem(
			keyName = "sacrifice",
			name = "Blood Sacrifice AoE",
			description = "Bring that ass here boi",
			position = 7,
			section = specSection
	)
	default boolean sacrifice(){ return false; }

	@ConfigItem(
			keyName = "icePrison",
			name = "Ice Prison Tiles",
			description = "help",
			position = 8,
			section = specSection
	)
	default boolean icePrison(){ return false; }

	@ConfigItem(
			keyName = "containThis",
			name = "Contain This AoE",
			description = "Highlights an AoE around Nex for the Contain This special",
			position = 9,
			section = specSection
	)
	default boolean containThis(){ return false; }

	@ConfigItem(
			keyName = "wrathWarning",
			name = "Wrath Warning",
			description = "Inshallah habibi",
			position = 10,
			section = specSection
	)
	default boolean wrathWarning() { return false; }

	@ConfigItem(
			keyName = "killTimer",
			name = "Kill Timer",
			description = "Display either an infobox or panel with kill/phase times",
			position = 0,
			section = timerSection
	)
	default KillTimerMode killTimer() { return KillTimerMode.OFF; }

	@ConfigItem(
			keyName = "phaseNameType",
			name = "Phase Name Type",
			description = "Display phases in timers and messages as either numbers(P1, P2, P3) or name(Smoke, shadow, blood)",
			position = 1,
			section = timerSection
	)
	default PhaseNameTypeMode phaseNameType() { return PhaseNameTypeMode.NUMBER; }

	@ConfigItem(
			keyName = "phaseChatMessages",
			name = "Phase Chat Message",
			description = "Puts message in chatbox for each phase",
			position = 2,
			section = timerSection
	)
	default boolean phaseChatMessages() { return false; }

	@ConfigItem(
			keyName = "showMinionSplit",
			name = "Show Minion Split",
			description = "Shows boss and minion times for each phase",
			position = 3,
			section = timerSection
	)
	default boolean showMinionSplit() { return false; }

	@ConfigItem(
			keyName = "usePrecise",
			name = "Use Precise",
			description = "Uses precise time for timers with decimals",
			position = 4,
			section = timerSection
	)
	default boolean usePrecise() { return false; }

	@Range(min = 1, max = 100)
	@ConfigItem(
			keyName = "textSize",
			name = "Text Size",
			description = "Sets the text size for all timers",
			position = 0
	)
	default int textSize(){ return 14; }

	@ConfigItem(
			keyName = "invulnerableTicks",
			name = "Invulnerable Ticks",
			description = "Shows ticks until Nex can be attacked again during transitions",
			position = 1
	)
	default boolean invulnerableTicks(){ return false; }

	@ConfigItem(
		keyName = "mageHighlight",
		name = "Mage Highlight",
		description = "Highlight the currently active ancient mage",
		position = 2
	)
	default MageHighlightMode mageHighlight(){ return MageHighlightMode.OFF; }

	@ConfigItem(
			keyName = "mageHighlightColor",
			name = "Mage Color",
			description = "Sets color of Mage Highlight tile",
			position = 3
	)
	default Color mageHighlightColor(){ return Color.CYAN; }

	@ConfigItem(
			keyName = "tankHighlight",
			name = "Tank Highlight",
			description = "Highlight who Nex is currently targetting",
			position = 4
	)
	default boolean tankHighlight(){ return false; }

	@ConfigItem(
			keyName = "tankHighlightColor",
			name = "Tank Color",
			description = "Sets color of Tank Highlight tile",
			position = 5
	)
	default Color tankHighlightColor(){ return Color.RED; }

	@ConfigItem(
			keyName = "nexWheelchair",
			name = "Null Wheelchair",
			description = "Consumes clicks when Nex is invulnerable",
			position = 6
	)
	default boolean nexWheelchair(){ return false; }

	@ConfigItem(
			keyName = "audio",
			name = "RS3 Voice Audio",
			description = "If only they put them in by default",
			position = 7
	)
	default boolean audio() { return false; }

	@Range(min = 0, max = 100)
	@ConfigItem(
			keyName = "audioVolume",
			name = "Audio Volume",
			description = "Sets the volume for all audio clips",
			position = 8
	)
	default int audioVolume() { return 40; }

	@ConfigItem(
			keyName = "playerCounter",
			name = "Player Counter",
			description = "Displays how many players are alive in the current instance",
			position = 9
	)
	default boolean playerCounter() { return false; }

	@ConfigItem(
			keyName = "prayerHelper",
			name = "Prayer Helper",
			description = "Is it really that hard?",
			position = 10
	)
	default boolean prayerHelper() { return false; }

	@ConfigItem(
			keyName = "SetInputName",
			name = "Set Input Name",
			description = "Set Input of nex instance name",
			position = 11
	)
	default String setInputName() {return "";}

	@ConfigItem(
			keyName = "shouldSetInput",
			name = "Should Set Input",
			description = "Set Input of nex instance name",
			position = 12
	)
	default boolean getShouldSetInput() {return false;}

	@ConfigItem(
			keyName = "showTenTile",
			name = "Ten Tile Range",
			description = "range of nex's sight",
			position = 13
	)
	default boolean showTenTile() {
		return false;
	}

	@ConfigItem(
			keyName = "olmPTSD",
			name = "Olm PTSD",
			description = "Makes Smoke phase a little more familiar",
			position = 97
	)
	default boolean olmPTSD(){ return false; }

	@ConfigItem(
			keyName = "forWhy",
			name = "Just fuck me up right good",
			description = "For why?",
			position = 98
	)
	default boolean forWhy(){ return false; }

	enum MageHighlightMode {
		OFF, ARROW, TILE, BOTH
	}

	enum ShadowsMode {
		OFF, COLOR, RAVE
	}

	enum AttacksTilSpecialMode {
		OFF, ON, LAST_ATT
	}

	enum VirusMode {
		OFF, TILE, AREA
	}

	enum NoEscapeMode {
		NEX, CCR
	}

	enum NoEscapeRunwayMode {
		OFF, COLOR, RAVE, RAVEST
	}

	enum KillTimerMode {
		OFF, INFOBOX, PANEL
	}

	enum PhaseNameTypeMode {
		NUMBER, NAME
	}
}
