package net.runelite.client.plugins.vengcounter;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("vengcounter")
public interface VengCounterConfig extends Config {
    @ConfigItem(
            keyName = "resetInstance",
            name = "Reset on new instances",
            description = "",
            position = 1
    )
    default boolean instance()
    {
        return true;
    }
    @Alpha
    @ConfigItem(
            keyName = "selfColor",
            name = "Your color",
            description = "",
            position = 3
    )
    default Color selfColor()
    {
        return Color.green;
    }
    @ConfigItem(
            keyName = "totalEnabled",
            name = "Show total ticks",
            description = "",
            position = 5
    )
    default boolean totalEnabled()
    {
        return true;
    }
    @Alpha
    @ConfigItem(
            keyName = "totalColor",
            name = "Total color",
            description = "",
            position = 6
    )
    default Color totalColor()
    {
        return Color.RED;
    }
    @Alpha
    @ConfigItem(
            keyName = "otherColor",
            name = "Other players color",
            description = "",
            position = 4
    )
    default Color otherColor()
    {
        return Color.white;
    }
    @Alpha
    @ConfigItem(
            keyName = "titleColor",
            name = "Title color",
            description = "",
            position = 2
    )
    default Color titleColor()
    {
        return Color.white;
    }
}
