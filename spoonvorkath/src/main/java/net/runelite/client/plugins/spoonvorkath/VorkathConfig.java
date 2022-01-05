package net.runelite.client.plugins.spoonvorkath;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

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
}
