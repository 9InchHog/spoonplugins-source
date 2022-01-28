package net.runelite.client.plugins.socket.plugins.socketDPS;

import java.awt.Color;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.*;

@ConfigGroup("socketdpscounter")
public interface SocketDpsConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "displayOverlay",
            name = "Display Plugin Overlay",
            description = "Display's the plugins overlay. - Useful to only display on one client instead of all."
    )
    default boolean displayOverlay() {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "showTotal",
            name = "Show total damage",
            description = "Shows total damage done to a boss"
    )
    default boolean showTotal() {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "autoclear",
            name = "Auto clear on boss kill",
            description = "clears the damage tracker when a boss dies"
    )
    default boolean autoclear() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "clearanybosskill",
            name = "Clear if anyone see boss die",
            description = "Clears if any socket member sees a boss die (if same world option only reset on same world)"
    )
    default boolean clearAnyBossKill() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "onlybossdps",
            name = "Only track boss dps",
            description = "Only tracks boss damage"
    )
    default boolean onlyBossDps() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "onlysameworld",
            name = "Only track/reset on same world",
            description = "Only tracks damage and resets if any sees death on same world"
    )
    default boolean onlySameWorld() {
        return false;
    }

    @ConfigItem(
            position = 3,
            keyName = "highlightSelf",
            name = "Highlight self",
            description = "Highlights your name in overlay"
    )
    default boolean highlightSelf() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "highlightOtherPlayer",
            name = "Highlight specific players",
            description = "Highlights players in textbox"
    )
    default boolean highlightOtherPlayer() {
        return true;
    }

    @ConfigItem(
            position = 4,
            keyName = "playersToHighlight",
            name = "Players to Highlight",
            description = "Sets the players to highlight"
    )
    default String getPlayerToHighlight() {
        return "";
    }

    @ConfigItem(keyName = "npcToHighlight", name = "", description = "")
    void setPlayerToHighlight(String paramString);

    @ConfigItem(
            position = 6,
            keyName = "playerColor",
            name = "Highlight Color",
            description = "Color of the player highlight"
    )
    default Color getHighlightColor() {
        return Color.YELLOW;
    }

    @ConfigItem(
            position = 7,
            keyName = "backgroundStyle",
            name = "Background Style",
            description = "Sets the background to the style you select"
    )
    default backgroundMode backgroundStyle() { return backgroundMode.STANDARD; }

    @Alpha
    @ConfigItem(
            position = 8,
            keyName = "backgroundColor",
            name = "Background Color",
            description = "Sets the overlay color on the custom setting"
    )
    default Color backgroundColor() { return new Color(23, 23,23, 156); }

    @ConfigItem(
            position = 9,
            keyName = "showDifference",
            name = "Show Difference",
            description = "Shows the difference between your damage and the boostee"
    )
    default boolean showDifference() {
        return false;
    }

    @ConfigItem(
            position = 10,
            keyName = "earlyWarning",
            name = "At Risk Warning",
            description = "Show difference changes color to orange at this threshold",
            hidden = true,
            unhide = "showDifference"
    )
    default int earlyWarning() { return 100; }

    @ConfigItem(
            position = 11,
            keyName = "lateWarning",
            name = "Snipe Warning",
            description = "Show difference changes color to red at this threshold",
            hidden = true,
            unhide = "showDifference"
    )
    default int lateWarning() { return 50; }

    @ConfigItem(
            position = 12,
            keyName = "isMain",
            name = "Is Main Account",
            description = "Set the account as a main account"
    )
    default boolean isMain() {
        return false;
    }

    @ConfigItem(
            position = 13,
            keyName = "boostedPlayerName",
            name = "Boosted Player Name",
            description = "Sets the name of the player being boosted"
    )
    default String boostedPlayerName() {
        return "";
    }

    @Getter
    @RequiredArgsConstructor
    enum backgroundMode {
        STANDARD, CUSTOM, HIDE
    }
}
