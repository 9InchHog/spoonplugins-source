package net.runelite.client.plugins.socket.plugins.socketbosstimer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("bosstimers")
public interface SocketBossTimersConfig extends Config {
    @ConfigItem(
            name = "Show If Anyone Sees Boss Kill",
            keyName = "socketBossKill",
            description = "Shows the respawn timer if anyone in Socket sees the boss die",
            position = 0
    )
    default boolean socketBossKill() { return true; }

    @ConfigItem(
            name = "Enable Multi-World Timers",
            keyName = "mutliWorldTimers",
            description = "Ability to see multiple respawn timers for a boss across different worlds",
            position = 1
    )
    default boolean multiWorldTimers() {
        return true;
    }

    @ConfigItem(
            name = "Notify On Time",
            keyName = "notifyOnTime",
            description = "Flash Screen red to warn of boss spawning",
            position = 2
    )
    default boolean notifyOnTime() {
        return false;
    }

    @ConfigItem(
            name = "Notify Time",
            keyName = "warningDelay",
            description = "Time before spawn to flash screen",
            position = 3
    )
    default int notifyTime() {
        return 3;
    }
}
