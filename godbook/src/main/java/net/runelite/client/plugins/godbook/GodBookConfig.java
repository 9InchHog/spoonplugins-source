package net.runelite.client.plugins.godbook;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("God Book")
public interface GodBookConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "booleanConfig",
            name = "Only show In Verzik/Xarp",
            description = "Test"
    )
    default boolean verzikOnly() {
        return false;
    }

    @ConfigItem(
            position = 1,
            keyName = "animations",
            name = "Animations",
            description = "Animations to look for (separated by comma eg 1335,7153)"
    )
    default String animations() {
        return "";
    }

    @ConfigItem(
            position = 2,
            keyName = "ticks",
            name = "Ticks",
            description = "How many ticks the counter remains active for"
    )
    default int maxTicks() {
        return 157;
    }

    @ConfigItem(
            position = 3,
            keyName = "leftClick",
            name = "Press Key to Preach",
            description = "Presses key for you"
    )
    default boolean leftClick() {
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = "keyToPress",
            name = "Preach Option to Press",
            description = "They key to press for the preferred preach option",
            hidden = true,
            unhide = "leftClick"
    )
    default key keyToPress() {
        return key.ONE;
    }

    enum key {
        ONE, TWO, THREE, FOUR
    }
}
