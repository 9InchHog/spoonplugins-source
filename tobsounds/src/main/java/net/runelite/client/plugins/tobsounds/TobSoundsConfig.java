package net.runelite.client.plugins.tobsounds;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("Bloat")
public interface TobSoundsConfig extends Config {
    @Range(min = 1, max = 100)
    @ConfigItem(
            position = 1,
            keyName = "soundsVolume",
            name = "Sound volume",
            description = "Volume"
    )
    default int soundVolume() {
        return 20;
    }

    @ConfigItem(
            keyName = "bloatSounds",
            name = "Bloat Sounds",
            description = "Plays sounds for bloat shut down and stomp",
            position = 2
    )
    default boolean bloatSounds() {
        return true;
    }

    @ConfigItem(
            keyName = "lootSounds",
            name = "Loot Sounds",
            description = "So you can be happy or dissapointed from the sound alone",
            position = 3
    )
    default boolean lootSounds() {
        return true;
    }

    @ConfigItem(
            keyName = "tankGay",
            name = "Tank Gay",
            description = "MIAOW",
            position = 4
    )
    default boolean tankGay() {
        return false;
    }

    @ConfigItem(
            keyName = "tobDeath",
            name = "Tob Death",
            description = "Horse go Neigh",
            position = 5
    )
    default boolean tobDeath() {
        return false;
    }

    @ConfigItem(
            keyName = "verzikBounce",
            name = "Verzik Bounce",
            description = "Waaaaaaaaaaaaaaaaah!",
            position = 6
    )
    default boolean verzikBounce() {
        return false;
    }
}
