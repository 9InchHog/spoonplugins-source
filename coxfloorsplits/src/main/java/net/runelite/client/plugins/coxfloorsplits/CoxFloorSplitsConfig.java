package net.runelite.client.plugins.coxfloorsplits;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("raidpointsoverlay")
public interface CoxFloorSplitsConfig extends Config
{
    @ConfigItem(
            keyName = "raidsTimer",
            name = "Display elapsed raid time",
            description = "Display elapsed raid time",
            position = 1
    )
    default boolean raidsTimer()
    {
        return true;
    }

    @ConfigItem(
            keyName = "floorSplits",
            name = "Display floor split times",
            description = "Displays the time for each floor and olm in the overlay",
            position = 2
    )
    default boolean showFloorSplits()
    {
        return true;
    }

    @ConfigItem(
            keyName = "displayOlm",
            name = "Display active olm time",
            description = "Displays a live counter for olm",
            position = 3
    )
    default boolean displayOlm()
    {
        return true;
    }
}
