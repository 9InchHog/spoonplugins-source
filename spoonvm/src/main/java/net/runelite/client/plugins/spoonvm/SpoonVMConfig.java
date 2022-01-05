package net.runelite.client.plugins.spoonvm;

import net.runelite.client.config.*;

@ConfigGroup("spoonvm")
public interface SpoonVMConfig extends Config
{
    @ConfigSection(
            name = "Notification",
            description = "Configuration for notifications.",
            position = 0,
            closedByDefault = true
    )
    public static final String alertSection = "alert";

    @ConfigSection(
            name = "Overlays",
            description = "Configuration for overlays.",
            position = 0,
            closedByDefault = true
    )
    public static final String overlaySection = "overlay";

    @ConfigItem(
            name = "Warning Style",
            keyName = "warningStyle",
            description = "Notification style for the warnings.",
            position = 0,
            section = "alert"
    )
    default WarningStyle warningStyle() {
        return WarningStyle.NOTIFIER;
    }

    @ConfigItem(
            keyName = "ventWarning",
            name = "Vent Shift Notification",
            description = "Show warning in advance of vents resetting 5 minutes into game",
            position = 1,
            section = "alert"
    )
    default boolean showVentWarning()
    {
        return true;
    }

    @ConfigItem(
            keyName = "ventWarningTime",
            name = "Vent Shift Warning Time",
            description = "Number of seconds before the vents reset",
            position = 1,
            section = "alert"
    )
    @Range(
            max = 60,
            min = 1
    )
    @Units(Units.SECONDS)
    default int ventWarningTime()
    {
        return 25;
    }

    @ConfigItem(
            keyName = "eruptionWarning",
            name = "Eruption Notification",
            description = "Show warning in advance of the volcano erupting",
            position = 2,
            section = "alert"
    )
    default boolean showEruptionWarning()
    {
        return true;
    }

    @ConfigItem(
            keyName = "eruptionWarningTime",
            name = "Eruption Warning Time",
            description = "Number of seconds before the volcano erupts",
            position = 3,
            section = "alert"
    )
    @Range(
            max = 60,
            min = 30
    )
    @Units(Units.SECONDS)
    default int eruptionWarningTime()
    {
        return 40;
    }

    @ConfigItem(
            keyName = "platformWarning",
            name = "Platform Despawn Notification",
            description = "Show warning for when platform below you is about to disappear",
            position = 4,
            section = "alert"
    )
    default boolean showPlatformWarning()
    {
        return true;
    }

    @ConfigItem(
            keyName = "boulderMovement",
            name = "Boulder Movement Notification",
            description = "Notify when current boulder stage is complete",
            position = 5,
            section = "alert"
    )
    default boolean showBoulderWarning()
    {
        return false;
    }

    @ConfigItem(
            keyName = "platformHighlight",
            name = "Platform Timer Style",
            description = "Highlights the last stage of the platforms with a tile overlay (Green = start/ red =about to disappear).",
            position = 0,
            section = "overlay"
    )
    default PlatformTimerStyle platformHighlight() { return PlatformTimerStyle.BOTH; }

    @ConfigItem(
            keyName = "swimWarning",
            name = "Swim Warning",
            description = "Surfs uup bitches.",
            position = 0,
            section = "overlay"
    )
    default SwimWarningStyle swimWarning() { return SwimWarningStyle.OFF; }

    @ConfigItem(
            position = 3,
            keyName = "rockRespawn",
            name = "Rock Respawn",
            description = "Respawn timer on the rocks you pick up to block chambers.",
            section = "overlay"
    )
    default RockRespawnStyle rockRespawn() { return RockRespawnStyle.BOTH; }

    @ConfigItem(
            position = 4,
            keyName = "drinkPrayer",
            name = "Prayer Warning",
            description = "Tick if you have low prayer/need to use ppots during Volcanic Mine.",
            section = "overlay"
    )
    default boolean drinkPrayer() { return false; }

    @ConfigItem(
            name = "Hide Attack Lava Beast",
            keyName = "hideLavaBeast",
            description = "Hides the attack option for lava beasts.",
            position = 95
    )
    default boolean hideLavaBeast() {
        return false;
    }

    @ConfigItem(
            name = "Remove East Gas Holes",
            keyName = "eastGas",
            description = "Removes the feel option from the east gas holes.",
            position = 96
    )
    default boolean eastGas() {
        return false;
    }

    @ConfigItem(
            name = "Font Type",
            keyName = "vmFontType",
            description = "Dynamically change the font for all Volcanic Mine Overlays",
            position = 98
    )
    default FontType vmFontType() {
        return FontType.SMALL;
    }

    @ConfigItem(
            name = "Text Outline",
            keyName = "vmTxtOutline",
            description = "Adds a black outline around text on overlays",
            position = 99
    )
    default boolean txtOutline() {
        return false;
    }

    public enum PlatformTimerStyle {
        OFF, TILE, TEXT, BOTH;
    }

    public enum WarningStyle {
        NOTIFIER, SOUND;
    }

    public enum SwimWarningStyle {
        OFF, TEXT, OVERLAY, BOTH;
    }

    public enum RockRespawnStyle {
        OFF, TILE, TEXT, BOTH;
    }
}
