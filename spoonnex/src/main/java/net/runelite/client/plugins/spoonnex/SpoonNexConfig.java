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
			name = "No Escape",
			description = "A10 Strafe 2: Electric Boogaloo",
			position = 3,
			section = specSection
	)
	default boolean noEscape(){ return false; }

	@Range(min = 0, max = 100)
    @ConfigItem(
		keyName = "noEscapeVolume",
		name = "No Escape Volume", 
		description = "For those struggling dodging No Escape, this plugin may be...... fortunate", 
		position = 4,
		section = specSection
	)
    default int noEscapeVolume() { return 40; }

	@ConfigItem(
			keyName = "shadowSpots",
			name = "Shadow Spots",
			description = "Highlights shadow spots on the ground during shadow phase",
			position = 5,
			section = specSection
	)
	default ShadowsMode shadowSpots(){ return ShadowsMode.OFF; }

	@ConfigItem(
			keyName = "sacrifice",
			name = "Blood Sacrifice AoE",
			description = "Bring that ass here boi",
			position = 6,
			section = specSection
	)
	default boolean sacrifice(){ return false; }

	@ConfigItem(
			keyName = "icePrison",
			name = "Ice Prison Tiles",
			description = "help",
			position = 7,
			section = specSection
	)
	default boolean icePrison(){ return false; }

	@ConfigItem(
			keyName = "containThis",
			name = "Contain This AoE",
			description = "Highlights an AoE around Nex for the Contain This special",
			position = 8,
			section = specSection
	)
	default boolean containThis(){ return false; }

	@ConfigItem(
			keyName = "wrathWarning",
			name = "Wrath Warning",
			description = "Inshallah habibi",
			position = 9,
			section = specSection
	)
	default boolean wrathWarning() { return false; }

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

	public enum MageHighlightMode {
		OFF, ARROW, TILE, BOTH
	}

	public enum ShadowsMode {
		OFF, COLOR, RAVE
	}

	public enum AttacksTilSpecialMode {
		OFF, ON, LAST_ATT
	}

	public enum VirusMode {
		OFF, TILE, AREA
	}
}
