package net.runelite.client.plugins.socket.plugins.socketplanks;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("planks")
public interface SocketPlanksConfig extends Config {
    @ConfigItem(
            keyName = "splitTimer",
            name = "Split Timer",
            description = "Displays split timer"
    )
    default boolean splitTimer() {
        return true;
    }
}