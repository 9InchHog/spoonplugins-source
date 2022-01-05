package net.runelite.client.plugins.coxprep;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("prep")
public interface CoxPrepConfig extends Config {
    @Range(min = 0)
    @ConfigItem(
            keyName = "brews",
            name = "Xeric's Aids",
            description = "How many Xeric's Aids your team wants to make",
            position = 1
    )
    default int brews() {
        return 0;
    }

    @Range(min = 0)
    @ConfigItem(
            keyName = "revites",
            name = "Revites",
            description = "How many Revites your team wants to make",
            position = 2
    )
    default int revites() {
        return 0;
    }

    @Range(min = 0)
    @ConfigItem(
            keyName = "enhances",
            name = "Prayer Enhances",
            description = "How many Prayer Enhances your team wants to make",
            position = 3
    )
    default int enhances() {
        return 0;
    }

    @Range(min = 0)
    @ConfigItem(
            keyName = "overloads",
            name = "Overloads",
            description = "How many Overloads your team wants to make",
            position = 4
    )
    default int overloads() {
        return 0;
    }

    @ConfigItem(
            keyName = "showSecondaries",
            name = "Show Secondaries",
            description = "Toggle whether or not to show secondaries overlay in scavs",
            position = 5
    )
    default boolean showSecondaries() {
        return false;
    }

    @ConfigItem(
            keyName = "showPots",
            name = "Show Pots Made",
            description = "Toggle whether or not to show how many pots you have made in prep",
            position = 6
    )
    default boolean showPots() {
        return false;
    }
}
