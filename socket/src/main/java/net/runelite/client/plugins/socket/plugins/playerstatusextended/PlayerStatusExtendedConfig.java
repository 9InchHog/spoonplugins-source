package net.runelite.client.plugins.socket.plugins.playerstatusextended;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("playerstatusextendedconfig")
public interface PlayerStatusExtendedConfig extends Config {
    @ConfigItem(
            keyName = "where",
            name = "Region identifier",
            description = "Only sends message if the person is in this region",
            position = 1
    )
    default Where where() {
        return Where.TOB_AND_COX;
    }

    @ConfigItem(
            keyName = "col",
            name = "Message color",
            description = "",
            position = 2
    )
    default Color col() {
        return Color.WHITE;
    }

    @ConfigItem(
            keyName = "lvlonly",
            name = "Only check level",
            description = "Turn on to stop receiving x bowed without rigour etc and only str lvl checks.",
            position = 3
    )
    default boolean lvlOnly() {
        return false;
    }

    @ConfigItem(
            keyName = "exemptPl",
            name = "Exempt players",
            description = "Names of players whose leech messages will be ignored",
            position = 4
    )
    default String ePlayers() {
        return "McNeill, Azotize, Kourend";
    }

    public enum Where {
        TOB, COX, TOB_AND_COX, EVERYWHERE;
    }
}
