package net.runelite.client.plugins.animationcooldown;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("animationcooldownB")
public interface AnimationCooldownConfig extends Config {
    @ConfigSection(
            name = "Overlay Settings",
            description = "",
            position = 0,
            closedByDefault = true
    )
    String overlaySection = "overlay";

    @ConfigItem(
            name = "Debug",
            keyName = "animDebug",
            description = "Sends a chat message with all the information needed to add to the lists",
            position = 1
    )
    default boolean animDebug() {return false;}

    @ConfigItem(
            name = "Track Other Players",
            keyName = "trackOtherPlayers",
            description = "",
            position = 2
    )
    default boolean trackOtherPlayers() {return true;}

    @ConfigItem(
            name = "Implement Blowpipe Fix",
            keyName = "blowpipeFix",
            description = "Implements the fix for the blowpipe (Enable this if you want it to work)",
            position = 3
    )
    default boolean blowpipeFix() {return true;}

    @ConfigItem(
            name = "Max Lost Ticks",
            keyName = "maxLostTicks",
            description = "",
            position = 4
    )
    @Range(max = 50, min = 0)
    @Units(" ticks")
    default int maxLostTicks() {return 0;}

    @ConfigItem(
            name = "Weapon List",
            keyName = "customWeaponList",
            description = "weapon_id,animation_id,ticks<br>Example: 22325,8056,5",
            position = 5
    )
    default String weaponList() {return "";}

    @ConfigItem(
            name = "Animation List",
            keyName = "customAnimationList",
            description = "animation_id,ticks<br>Example: 8056,5",
            position = 6
    )
    default String animationList() {return "1336,12\n" + "7154,12\n" + "7155,12\n";}

    @ConfigItem(
            name = "Only Show In Raids",
            keyName = "raidsOnly",
            description = "Only shows the ticks in Raids",
            position = 6
    )
    default boolean raidsOnly() {return true;}

    @ConfigItem(
            name = "Font Type",
            keyName = "animationCooldownFontType",
            description = "",
            position = 0,
            section = "overlay"
    )
    default FontType animationCooldownFontType() {return FontType.BOLD;}

    @ConfigItem(
            name = "Z Offset",
            keyName = "animationCooldownOffset",
            description = "",
            position = 1,
            section = "overlay"
    )
    @Range(max = 300, min = 0)
    @Units("px")
    default int animationCooldownOffset() {return 150;}

    @ConfigItem(
            name = "Tick Counter",
            keyName = "tickCounterColor",
            description = "",
            position = 2,
            section = "overlay"
    )
    @Alpha
    default Color tickCounterColor() {return Color.WHITE;}

    @ConfigItem(
            name = "Lost Ticks",
            keyName = "lostTicksColor",
            description = "",
            position = 3,
            section = "overlay"
    )
    @Alpha
    default Color lostTicksColor() {return Color.RED;}
}
