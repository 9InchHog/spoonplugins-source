package net.runelite.client.plugins.raidspoints;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("raidspoints")
public interface RaidsPointsConfig extends Config {
    @ConfigItem(
            position = 13,
            keyName = "ptsPanelUpdate",
            name = "Enable points panel",
            description = "Enable the panel"
    )
    default boolean ptsPanel() {
        return true;
    }

    @ConfigItem(
            position = 14,
            keyName = "soloPanel",
            name = "Solo Points Only",
            description = "Display Only Solo Points Info"
    )
    default boolean soloPanel() {
        return false;
    }
}
