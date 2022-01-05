package net.runelite.client.plugins.rareimplings;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("rareimplings")
public interface RareImplingsConfig extends Config {
    @ConfigItem(keyName = "drawOverlay", name = "Show indices on scene", description = "")
    default boolean show() {
        return true;
    }
}
