package net.runelite.client.plugins.bingo;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("bingo")
public interface BingoConfig extends Config {
    @ConfigItem(keyName = "teamCode", name = "Team Code", description = "Your teams bingo code")
    default String getTeamCode() {
        return "";
    }

    @ConfigItem(keyName = "textColour", name = "Text Colour", description = "Configures the color")
    default Color getTextColour() {
        return Color.GREEN;
    }

    @ConfigItem(keyName = "freedomUnits", name = "American Andys", description = "Display the date incorrectly for Americans")
    default boolean getFreedomUnits() {
        return false;
    }
}
