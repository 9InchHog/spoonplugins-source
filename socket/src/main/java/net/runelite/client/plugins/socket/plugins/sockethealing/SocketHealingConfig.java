package net.runelite.client.plugins.socket.plugins.sockethealing;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("sockethealing")
public interface SocketHealingConfig extends Config {
    @Range(min = -50, max = 50)
    @ConfigItem(position = 1, keyName = "getIndicatorXOffset", name = "Hp Overlay X Offset", description = "This is the horizontal offset of the health overlay.")
    default int getIndicatorXOffset() {
        return -8;
    }

    @Range(min = -50, max = 50)
    @ConfigItem(position = 2, keyName = "getIndicatorYOffset", name = "Hp Overlay Y Offset", description = "This is the vertical offset of the health overlay.")
    default int getIndicatorYOffset() {
        return 5;
    }

    @Range(max = 10, min = -20)
    @ConfigItem(position = 3, keyName = "overlayOffset", name = "Player Overlay Height", description = "This adjusts the height of the health overlay.")
    default int overlayOffset() {
        return -15;
    }

    @Range(max = 20, min = 1)
    @ConfigItem(position = 4, keyName = "refreshRate", name = "Socket Refresh Rate", description = "This is how many ticks you would like in-between updating the information.")
    default int refreshRate() {
        return 5;
    }

    @ConfigItem(name = "Font Type", keyName = "healingFontType", description = "Dynamically change the font for all health overlays", position = 5)
    default SocketFontType healingFontType() {
        return SocketFontType.CUSTOM;
    }

    @Range(max = 20, min = 10)
    @ConfigItem(position = 6, keyName = "fontSize", name = "Font Size", description = "Shows total damage done to a boss")
    default int fontSize() { return 12; }

    @Range(max = 100, min = 50)
    @ConfigItem(position = 7, keyName = "greenZone", name = "High Hp", description = "Sets the bottom amount for the health to be displayed as high hp. (Max 100, Min 50)")
    default int greenZone() {
        return 75;
    }

    @ConfigItem(position = 8, keyName = "greenZoneColor", name = "High Hp Color", description = "Sets the color the high hp is set to")
    default Color greenZoneColor() {
        return Color.GREEN;
    }

    @Range(max = 75, min = 25)
    @ConfigItem(position = 9, keyName = "orangeZone", name = "Middle Hp", description = "Sets the bottom amount for the health to be displayed as middle hp. (Max 75, Min 25)")
    default int orangeZone() {
        return 50;
    }

    @ConfigItem(position = 10, keyName = "orangeZoneColor", name = "Middle Hp Color", description = "Sets the color the middle hp range is set to")
    default Color orangeZoneColor() {
        return Color.ORANGE;
    }

    @ConfigItem(position = 11, keyName = "redZoneColor", name = "Low Hp Color", description = "Sets the color the lower hp range is set to")
    default Color redZoneColor() { return Color.RED; }

    @Range(max = 255, min = 0)
    @ConfigItem(position = 12, keyName = "opacity", name = "Highlight Opacity", description = "Sets the opacity for the highlight styles. (Max 255, Min 0)")
    default int opacity() {
        return 255;
    }

    @Range(max = 5, min = 1)
    @ConfigItem(position = 13, keyName = "hpThiCC", name = "Highlight Width", description = "Sets the width for the highlight styles. (Max 5, Min 1)")
    default int hpThiCC() {
        return 2;
    }

    @ConfigItem(position = 14, keyName = "highlightHull", name = "Highlight Hull", description = "Configures whether or not selected players should be highlighted by hull", disabledBy = "highlightOutline")
    default boolean highlightHull() {
        return true;
    }

    @ConfigItem(position = 15, keyName = "highlightOutline", name = "Highlight Outline", description = "Configures whether or not selected players outlines should be highlighted", disabledBy = "highlightHull")
    default boolean highlightOutline() {return false;}

    @Range(max = 4, min = 0)
    @ConfigItem(position = 16, keyName = "glow", name = "Outline Glow", description = "Sets the glow for the outline highlight style. (Max 4, Min 0)")
    default int glow() {
        return 4;
    }

    @ConfigItem(position = 17, keyName = "displayHealth", name = "Display Health On All Players*", description = "Turns off the hp displayed. Will still allow you to highlight custom players.")
    default boolean displayHealth() {
        return true;
    }

    @ConfigItem(position = 18, keyName = "highlightPlayerNames", name = "Highlighted Player Names", description = "Names listed here will be added to hull highlight list")
    default String highlightedPlayerNames() { return ""; }

    @ConfigItem(position = 19, keyName = "hpPlayerNames", name = "Display Health On Players", description = "Names listed here will be added to the display hp list")
    default String hpPlayerNames() { return ""; }

    @ConfigItem(position = 20, keyName = "hpMenu", name = "Show Hp In Menu", description = "Shows the amount of hp players have in the right click menu")
    default boolean hpMenu() { return false; }

    @ConfigItem(position = 21, keyName = "showName", name = "Show Name in Overlay", description = "Shows the name of the player next to their hp")
    default boolean showName() { return false; }

    /*@ConfigItem(position = 22, keyName = "healOtherMES", name = "Heal Other MES", description = "Prioritizes the lowest hp player in socket for heal other")
    default boolean healOtherMES() { return false; }*/

    public enum SocketFontType {
        REGULAR, BOLD, SMALL, CUSTOM;
    }
}
