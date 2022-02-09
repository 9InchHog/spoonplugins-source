package net.runelite.client.plugins.socket.plugins.socketvangpots;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("socketvangpots")
public interface SocketVangPotsConfig extends Config {
    @ConfigItem(
            keyName = "showPanel",
            name = "Show Panel",
            description = "Displays pots panel"
    )
    default boolean showPanel() {
        return true;
    }

    @ConfigItem(
            keyName = "showChatMessage",
            name = "Chat Message",
            description = "Displays split timer"
    )
    default boolean showChatMessage() {
        return true;
    }
}