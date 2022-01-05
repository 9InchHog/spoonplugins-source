package net.runelite.client.plugins.tickdebug;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(TickDebugConfig.GROUP)
public interface TickDebugConfig extends Config {
    String GROUP = "tickdebug";
    @ConfigItem(
            keyName = "showDif",
            name = "Show Difference",
            description = "Displays the difference from 600 ms",
            position = 0
    )
    default boolean showDif()
    {
        return false;
    }
}
