package net.runelite.client.plugins.socket.plugins.playerindicatorsextended;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("PlayerNamesConfig")
public interface PlayerIndicatorsExtendedConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "nameColor",
            name = "Socket Player Name Color",
            description = "Name color"
    )
    default Color nameColor() {
        return Color.decode("0x8686BE");
    }

    @ConfigItem(
            position = 1,
            keyName = "drawMinimap",
            name = "Show on Mini Map",
            description = "Show on Mini map"
    )
    default boolean drawMinimap() {
        return true;
    }
}
