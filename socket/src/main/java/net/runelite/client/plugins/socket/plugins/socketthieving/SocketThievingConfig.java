package net.runelite.client.plugins.socket.plugins.socketthieving;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("thieving")
public interface SocketThievingConfig extends Config {

    @ConfigItem(
            keyName = "grubRate",
            name = "Average grubs per chest for non-Socket",
            description = "Average grubs per chest that contained some, for use in count estimation",
            position = 1
    )
    default int grubRate() {
        return 225;
    }

    @ConfigItem(
            keyName = "gumdropFactor",
            name = "Gumdrop Highlight Factor",
            description = "Highlight bat chests in pretty colors",
            position = 2
    )
    default int gumdropFactor() {
        return 0;
    }

    @ConfigItem(
            keyName = "highlightBats",
            name = "Highlight Potential Bats",
            description = "Highlight bat chests",
            position = 3
    )
    default boolean highlightBatChests() {
        return true;
    }

    @ConfigItem(
            keyName = "thievingRateMsg",
            name = "Chest Success Rate Message",
            description = "Puts a game message of your success rate in the completed thieving room",
            position = 4
    )
    default boolean thievingRateMsg() { return true; }

    @ConfigItem(
            keyName = "display4Prep",
            name = "Display Overlay for CM Prepper",
            description = "Displays the overlay when you are in Prep or Thieving. Have to be in Socket to accurately display the information.",
            position = 5
    )
    default boolean display4Prep() {
        return true;
    }

    @ConfigItem(
            keyName = "covidMsg",
            name = "Bats Message",
            description = "Quarantine you disgusting fuck",
            position = 6
    )
    default boolean covidMsg() {
        return true;
    }

    @ConfigItem(
            keyName = "dumpMsg",
            name = "Dump Message",
            description = "Puts a message in game chat of when to dump based off your current raids party size",
            position = 7
    )
    default boolean dumpMsg() {
        return true;
    }

    @ConfigItem(
            keyName = "displayMinGrubs",
            name = "Display Minimum Grubs Needed",
            description = "Puts how many grubs you need minimum per team size next to the total amount of grubs",
            position = 8
    )
    default boolean displayMinGrubs() {
        return true;
    }
}
