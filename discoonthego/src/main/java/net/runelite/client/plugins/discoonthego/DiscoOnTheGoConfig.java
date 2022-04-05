package net.runelite.client.plugins.discoonthego;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("discoonthego")
public interface DiscoOnTheGoConfig extends Config {
    @ConfigItem(keyName = "disco", name = "Disco Floor", description = "Bust a move")
    default boolean disco() { return true; }

    @Range(min = 1, max = 50)
    @ConfigItem(keyName = "discoSize", name = "Disco Radius", description = "Adjusts the size of the disco floor (tiles out from your player)")
    default int discoSize() { return 4; }

    @ConfigItem(keyName = "playerHelper", name = "Player Helper", description = "So you can see your players better")
    default boolean playerHelper() { return false; }

    @Alpha
    @ConfigItem(keyName = "playerHelperColor", name = "Player Helper Color", description = "Adjusts color of player helper")
    default Color playerHelperColor() { return Color.RED; }

    @ConfigItem(keyName = "groovin", name = "Groovin", description = "Embrace the disco")
    default boolean groovin() { return true; }
}
