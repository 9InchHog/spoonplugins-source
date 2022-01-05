package net.runelite.client.plugins.specinfobox;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("specinfobox")
public interface SpecIBConfig extends Config {
    public static final String GROUP_NAME = "specinfobox";

    @ConfigItem(name = "Spec Threshold", keyName = "specThreshold", description = "", position = 0)
    @Range(max = 100, min = 0)
    @Units("%")
    default int specThreshold() {
        return 100;
    }
}
