package net.runelite.client.plugins.tmorph;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import static net.runelite.client.plugins.tmorph.TMorphPlugin.TMORPH_GROUP;

@ConfigGroup(TMORPH_GROUP)
public interface TMorphConfig extends Config {
	@ConfigItem(
			position = 0,
			keyName = "headSlotTMorphs",
			name = "Head Slot",
			description = "Transform pair for the head slot"
	)
	default String getHeadSlotTMorphs()
	{
		return "";
	}

	@ConfigItem(
			position = 1,
			keyName = "capeSlotTMorphs",
			name = "Cape Slot",
			description = "Transform pair for the cape slot"
	)
	default String getCapeSlotTMorphs()
	{
		return "";
	}

	@ConfigItem(
			position = 2,
			keyName = "ammySlotTMorphs",
			name = "Ammy Slot",
			description = "Transform pair for the ammy slot"
	)
	default String getAmmySlotTMorphs()
	{
		return "";
	}

	@ConfigItem(
			position = 3,
			keyName = "torsoSlotTMorphs",
			name = "Torso Slot",
			description = "Transform pair for the torso slot"
	)
	default String getTorsoSlotTMorphs()
	{
		return "";
	}

	@ConfigItem(
			position = 4,
			keyName = "legsSlotTMorphs",
			name = "Legs Slot",
			description = "Transform pair for the legs slot"
	)
	default String getLegsSlotTMorphs()
	{
		return "";
	}

	@ConfigItem(
			position = 5,
			keyName = "gloveSlotTMorphs",
			name = "Glove Slot",
			description = "Transform pair for the glove slot"
	)
	default String getGloveSlotTMorphs()
	{
		return "";
	}

	@ConfigItem(
			position = 6,
			keyName = "bootSlotTMorphs",
			name = "Boot Slot",
			description = "Transform pair for the boot slot"
	)
	default String getBootSlotTMorphs()
	{
		return "";
	}

	@ConfigItem(
			position = 6,
			keyName = "weaponSlotTMorphs",
			name = "Weapon Slot",
			description = "Transform pair for the weapon slot"
	)
	default String getWeaponSlotTMorphs()
	{
		return "";
	}

	@ConfigItem(
			position = 6,
			keyName = "shieldSlotTMorphs",
			name = "Shield Slot",
			description = "Transform pair for the shield slot"
	)
	default String getShieldSlotTMorphs()
	{
		return "";
	}

	@ConfigItem(
			position = 7,
			keyName = "animationTmorphs",
			name = "Replace Animations",
			description = ""
	)
	default String animationTmorphs()
	{
		return "";
	}

	@ConfigItem(
			position = 8,
			keyName = "graphicTmorphs", 
			name = "Replace Graphcis", 
			description = "Replace graphics on players with different ones"
	)
    default String graphicTmorphs() {return "";}

	@ConfigItem(
			position = 9,
			keyName = "shouldReplacePoseAnimationTmorphs",
			name = "Enable replace pose animations",
			description = ""
	)
	default boolean shouldReplacePoseAnimationTmorphs()
	{
		return false;
	}

	@ConfigItem(
			position = 10,
			keyName = "poseAnimationTmorphs",
			name = "Replace pose animations",
			description = ""
	)
	default String poseAnimationTmorphs()
	{
		return "";
	}

	@ConfigItem(
			position = 11,
			keyName = "soundEffectTmorphs",
			name = "Replace sound effects",
			description = "This replaces every sound with the given id even if it isn't yours"
	)
	default String soundEffectTmorphs()
	{
		return "";
	}

	/*@ConfigItem(
			position = 100,
			keyName = "inventoryTmorphs",
			name = "Inventory Tmorphs",
			description = "Transform pair for inventory"
	)
	default String inventoryTmorphs() { return "-25731,22323 - holy/reg sang"; }*/
}
