package net.runelite.client.plugins.dxpdrops;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("xpdrops")
public interface XpDropsConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "showFakeXpIcon",
            name = "Hide fake XP icon",
            description = "Hide icon for fake XP drops"
    )
    default boolean showFakeXpIcon() {
        return false;
    }

    @ConfigItem(
            position = 1,
            keyName = "customFontName",
            name = "Custom font name",
            description = "Custom font override. Leave blank to disable"
    )
    default String customFontName() {
        return "";
    }

    @ConfigItem(
            position = 2,
            keyName = "customFontSize",
            name = "Custom font size",
            description = "Custom font override size"
    )
    default int customFontSize() {
        return 12;
    }

    @ConfigItem(
            position = 3,
            keyName = "antiAlias",
            name = "Anti-aliasing",
            description = "Turns on anti-aliasing for the xp drops. Makes them smoother"
    )
    default boolean antiAlias()
    {
        return true;
    }

    @ConfigItem(
            position = 4,
            keyName = "inGameSettings",
            name = "Use In-Game Speed Settings",
            description = "Uses the in-game speed settings instead of a custom speed"
    )
    default boolean inGameSettings()
    {
        return true;
    }

    @Range(min = 1, max = 20)
    @ConfigItem(
            position = 5,
            keyName = "speed",
            name = "XP Drop Speed",
            description = "How fast you want XP drops to move <br> Default = 3, Slower = 2, Faster = 4"
    )
    default int speed()
    {
        return 3;
    }

    @ConfigItem(
            position = 6,
            keyName = "fade",
            name = "Fade out",
            description = "Fade out"
    )
    default boolean fade() {return true;}
}