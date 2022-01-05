package net.runelite.client.plugins.togglechat;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("bToggleChat")
public interface ToggleChatConfig extends Config {
    @ConfigItem(
            keyName = "hotKey",
            name = "Chat Toggle",
            description = "Pressing this key will toggle the chatbox",
            position = 0
    )
    default Keybind hotKey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "removeBlueTabs",
            name = "Remove Blue Chat Tabs",
            description = "For the chat-closed gamers - removes the annoying tab flashing",
            position = 1
    )
    default boolean removeBlueTabs() {return false;}
}
