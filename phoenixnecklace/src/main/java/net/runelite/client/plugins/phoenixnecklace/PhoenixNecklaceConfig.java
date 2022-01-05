package net.runelite.client.plugins.phoenixnecklace;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("phoenixnecklace")
public interface PhoenixNecklaceConfig extends Config {
    @ConfigItem(
            keyName = "showEverywhere",
            name = "Show Everywhere",
            description = "displays the infobox everywhere",
            position = 0
    )
    default boolean showEverywhere() {
        return true;
    }

    @ConfigItem(
            keyName = "showTob",
            name = "Show at Bloat",
            description = "displays the infobox only at Bloat",
            position = 1
    )
    default boolean showTob() {
        return true;
    }

    @ConfigItem(
            keyName = "showCorp",
            name = "Show at Corp",
            description = "displays the infobox only at Corp",
            position = 2
    )
    default boolean showCorp() {
        return true;
    }

    @ConfigItem(
            keyName = "showKq",
            name = "Show at KQ",
            description = "displays the infobox only at Kq",
            position = 3
    )
    default boolean showKq() {
        return true;
    }

    @ConfigItem(
            keyName = "showSpooder",
            name = "Show at Sarachnis",
            description = "displays the infobox only at Sarachnis",
            position = 4
    )
    default boolean showSpooder() {
        return true;
    }

    @ConfigItem(
            keyName = "showWild",
            name = "Show in Wild",
            description = "displays the infobox only in the Wild",
            position = 5
    )
    default boolean showWild() {
        return true;
    }

    @ConfigItem(
            keyName = "rockCake",
            name = "Rock Cake Proc",
            description = "Highlights the rock cake when you should proc pneck",
            position = 6
    )
    default RockCakeMode rockCake() {
        return RockCakeMode.OFF;
    }

    @Alpha
    @ConfigItem(
            keyName = "cakeColor",
            name = "Rock Cake Color",
            description = "Sets color of rock cake proc",
            position = 7
    )
    default Color cakeColor() {
        return Color.RED;
    }

    @Range(min = 0, max = 255)
    @ConfigItem(
            keyName = "cakeOpacity",
            name = "Rock Cake Opacity",
            description = "Sets opacity of rock cake proc overlay",
            position = 8
    )
    default int cakeOpacity() { return 100; }

    @ConfigItem(
            keyName = "sound",
            name = "Enable Sound",
            description = "Plays a sound when pnecks break",
            position = 9
    )
    default boolean sound() {
        return true;
    }

    @Range(min = 1, max = 50)
    @ConfigItem(
            keyName = "volume",
            name = "Alert Sound Volume",
            description = "Ding go loud. Gl your ears (20 is a good volume).",
            position = 10
    )
    default int volume() {
        return 20;
    }

    @Getter
    @RequiredArgsConstructor
    public enum RockCakeMode
    {
        OFF("Off"),
        OVERLAY("Overlay"),
        OUTLINE("Outline");

        private final String name;

        @Override
        public String toString()
        {
            return name;
        }
    }
}
