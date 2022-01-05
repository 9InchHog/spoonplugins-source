package net.runelite.client.plugins.reorderprayers;

import net.runelite.api.Prayer;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("reorderprayers")
public interface ReorderPrayersConfig extends Config {
    @ConfigItem(
            keyName = "unlockPrayerReordering",
            name = "Unlock Prayer Reordering",
            description = "Configures whether or not you can reorder the prayers",
            position = 1
    )
    default boolean unlockPrayerReordering() {
        return false;
    }

    @ConfigItem(
            keyName = "unlockPrayerReordering",
            name = "",
            description = ""
    )
    void unlockPrayerReordering(boolean var1);

    @ConfigItem(
            keyName = "prayerOrder",
            name = "Prayer Order",
            description = "Configures the order of the prayers",
            hidden = true,
            position = 2
    )
    default String prayerOrder() {
        return ReorderPrayersPlugin.prayerOrderToString(Prayer.values());
    }

    @ConfigItem(
            keyName = "prayerOrder",
            name = "",
            description = ""
    )
    void prayerOrder(String var1);
}