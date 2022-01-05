package net.runelite.client.plugins.spoonjadhelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

import java.awt.*;

@ConfigGroup("spoonJadHelper")
public interface SpoonJadHelperConfig extends Config {
	@Range(min = 5, max = 50)
	@ConfigItem(
			keyName = "sixJadSize",
			name = "Text Size",
			description = "Sets the text size of the ticks overlay",
			position = 0
	)
    default int sixJadSize() {return 30;}

	@ConfigItem(
			keyName = "jadChallengeSpawn",
			name = "Jad Challenge Spawn",
			description = "Displays where the Jads will spawn in the Jad Challenges",
			position = 1
	)
    default boolean jadChallengeSpawn() {return false;}

	@ConfigItem(
			keyName = "jadChallengeSpawnColor",
			name = "Spawn Color",
			description = "Sets the color of the Jad spawn overlay",
			position = 2
	)
    default Color jadChallengeSpawnColor() {return Color.CYAN;}

	@Range(min = 0, max = 255)
	@ConfigItem(
			keyName = "jadChallengeSpawnOpacity",
			name = "Spawn Opacity",
			description = "Sets the opacity of the Jad spawn overlay",
			position = 3
	)
	default int jadChallengeSpawnOpacity() {return 30;}

	@ConfigItem(
			keyName = "bonk",
			name = "Bonk",
			description = "Bonk",
			position = 1
	)
	default boolean bonk() {return false;}
}
