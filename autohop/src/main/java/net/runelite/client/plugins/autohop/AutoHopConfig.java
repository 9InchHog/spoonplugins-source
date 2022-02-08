package net.runelite.client.plugins.autohop;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("autohop")
public interface AutoHopConfig extends Config
{
    @ConfigSection(
            name = "Hop",
            description = "Hopping settings.",
            position = 0,
            closedByDefault = true
    )
    public static final String hopSection = "hop";

    @ConfigSection(
            name = "Worlds",
            description = "Configuration for worlds.",
            position = 1,
            closedByDefault = true
    )
    public static final String worldsSection = "worlds";

    @ConfigSection(
            name = "Ignore",
            description = "Toggle who to ignore.",
            position = 1,
            closedByDefault = true
    )
    public static final String ignoreSection = "ignore";

    @ConfigItem(
            keyName = "disableGrandExchange",
            name = "Disable at Grand Exchange",
            description = "Don't hop if your player is at the grand exchange",
            section = "hop",
            position = 1
    )
    default boolean disableGrandExchange()
    {
        return false;
    }

    @ConfigItem(
            keyName = "disableFeroxEnclave",
            name = "Disable at Ferox Enclave",
            description = "Don't hop if your player is at the Ferox Enclave",
            section = "hop",
            position = 2
    )
    default boolean disableFeroxEnclave()
    {
        return false;
    }

    @ConfigItem(
            keyName = "cmbBracket",
            name = "Within combat bracket",
            description = "Only hop if the player is within your combat bracket",
            section = "hop",
            position = 3
    )
    default boolean cmbBracket()
    {
        return true;
    }

    @ConfigItem(
            keyName = "alwaysHop",
            name = "Hop on player spawn",
            description = "Hop when a player  spawns",
            section = "hop",
            position = 4
    )
    default boolean alwaysHop()
    {
        return true;
    }

    @ConfigItem(
            keyName = "chatHop",
            name = "Hop on chat message",
            description = "Hop whenever any message is entered into chat",
            section = "hop",
            position = 5
    )
    default boolean chatHop()
    {
        return false;
    }

    @ConfigItem(
            keyName = "hopRadius",
            name = "Hop radius",
            description = "Hop only when another player enters radius",
            section = "hop",
            position = 6
    )
    default boolean hopRadius()
    {
        return false;
    }

    @ConfigItem(
            keyName = "playerRadius",
            name = "Player radius",
            description = "Radius (tiles) for player to be within to trigger hop",
            section = "hop",
            position = 7
    )
    default int playerRadius()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "skulledHop",
            name = "Skulled",
            description = "Hop when a player within your combat bracket spawns that has a skull",
            section = "hop",
            position = 8
    )
    default boolean skulledHop() {
        return true;
    }

    @ConfigItem(
            keyName = "underHop",
            name = "Log under",
            description = "Hop when a player within your combat bracket spawns underneath you",
            section = "hop",
            position = 9
    )
    default boolean underHop()
    {
        return true;
    }

    @ConfigItem(
            keyName = "returnInventory",
            name = "Return to inventory",
            description = "Return to inventory after hopping",
            section = "hop",
            position = 10
    )
    default boolean returnInventory()
    { return false; }

    @ConfigItem(
            keyName = "autoCloseChatbox",
            name = "Auto Close Chatbox",
            description = "Automatically close chatbox messages that prevent you from world hopping such as inventory full message when fishing dark crabs.",
            section = "hop",
            position = 11
    )
    default boolean autoCloseChatbox() {return false;}

    @ConfigItem(
            keyName = "ignoredPlayers",
            name = "Hop Players Blacklist",
            description = "Will not hop when these players are visible",
            section = "hop",
            position = 12
    )
    default String ignoredPlayers() {return "";}

    @ConfigItem(
            keyName = "american",
            name = "American",
            description = "Allow hopping to American worlds",
            section = "worlds",
            position = 13
    )
    default boolean american()
    {
        return true;
    }

    @ConfigItem(
            keyName = "unitedkingdom",
            name = "UK",
            description = "Allow hopping to UK worlds",
            section = "worlds",
            position = 14
    )
    default boolean unitedkingdom()
    {
        return true;
    }

    @ConfigItem(
            keyName = "germany",
            name = "German",
            description = "Allow hopping to German worlds",
            section = "worlds",
            position = 15
    )
    default boolean germany()
    {
        return true;
    }

    @ConfigItem(
            keyName = "australia",
            name = "Australian",
            description = "Allow hopping to Australian worlds",
            section = "worlds",
            position = 16
    )
    default boolean australia()
    {
        return true;
    }

    @ConfigItem(
            keyName = "friends",
            name = "Friends",
            description = "Don't hop when the player spawned is on your friend list",
            section = "ignore",
            position = 18
    )
    default boolean friends()
    {
        return true;
    }

    @ConfigItem(
            keyName = "clanmembers",
            name = "Clan members",
            description = "Don't hop when the player spawned is in your clan chat",
            section = "ignore",
            position = 19
    )
    default boolean clanmember()
    {
        return true;
    }
}
