package net.runelite.client.plugins.specorb;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("specorb")
public interface SpecOrbConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "hideNormalWeapons",
            name = "Only Work on Spec Weapons",
            description = "Makes the spec orb clickable as soon as you click a weapon with a special attack."
    )
    default boolean hideNormalWeapons()
    {
        return false;
    }
}
