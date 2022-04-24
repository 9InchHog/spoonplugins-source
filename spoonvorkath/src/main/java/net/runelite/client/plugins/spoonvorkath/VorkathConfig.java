package net.runelite.client.plugins.spoonvorkath;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("spoonvorkath")
public interface VorkathConfig extends Config {
    @ConfigItem(name = "Phase Attack Counter", keyName = "phaseAttackCounter", description = "Shows how many attacks are left til next phase", position = 0)
    default boolean phaseAttackCounter() {
        return false;
    }

    @ConfigItem(name = "Display what phase is next", keyName = "nextPhaseName", description = "Tells you what phase is next", position = 1)
    default boolean nextPhaseName() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "zombifiedSpawn",
            name = "Zombified Spawn",
            description = "Highlights the Zombified Spawn"
    )
    default TileMode zombifiedSpawn()
    {
        return TileMode.OFF;
    }


    @ConfigItem(name = "Zombified Spawn Color", keyName = "zombifiedSpawnColor", description = "Configures the color of the Zombified Spawn", position = 3)
    default Color zombifiedSpawnColor() {
        return Color.RED;
    }

    @ConfigItem(
            position = 4,
            keyName = "afkNotifier",
            name = "AFK Notifier",
            description = "Sends a Notification if Pink Dragonfire Attack or Deadly Dragonfire Attack is shot."
    )
    default boolean afkNotifier()
    {
        return false;
    }

    @Range(min = 0, max = 255)
    @ConfigItem(
            position = 5,
            keyName = "vorkathDimmer",
            name = "Vorkath Dimmer",
            description = "Saves your eyes while hard grinding. Does nothing if 0"
    )
    default int vorkathDimmer()
    {
        return 0;
    }

    @ConfigItem(
            position = 6,
            keyName = "leftClickCast",
            name = "Left Click Cast Crumble Undead",
            description = "Left click cast crumble undead on the spawn without having to select the spell."
    )
    default boolean leftClickCast()
    {
        return false;
    }
}
