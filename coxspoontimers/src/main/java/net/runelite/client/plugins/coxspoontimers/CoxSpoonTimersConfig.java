package net.runelite.client.plugins.coxspoontimers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("coxspoontimers")
public interface CoxSpoonTimersConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName = "preciseTimers",
            name = "Precise Timers",
            description = "Respect in-game precise timer setting"
    )
    default PreciseTimersSetting preciseTimers() {
        return PreciseTimersSetting.RESPECT_INGAME_SETTING;
    }

    @ConfigItem(
            position = 1,
            keyName = "showIcePopTime",
            name = "Time Ice demon pop-out",
            description = "Partial room timer for Ice Demon"
    )
    default boolean showIcePopTime() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "showOlmPhaseTimers",
            name = "Time Olm phases",
            description = "Phase timers for Olm"
    )
    default boolean showOlmPhaseTimers() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "showOlmMageHand",
            name = "Time Olm mage hand",
            description = "Mage hand timer for Olm"
    )
    default boolean showOlmMageHand() {
        return true;
    }

    @ConfigItem(
            position = 4,
            keyName = "showMuttadileTreeCutTime",
            name = "Time Muttadile tree cut",
            description = "Partial room timer for Muttadiles"
    )
    default boolean showMuttadileTreeCutTime() {
        return true;
    }

    @ConfigItem(
            keyName = "displayOverlay",
            name = "Display current room time overlay",
            description = "Displays an overlay that shows how long the current room is taking",
            position = 5
    )
    default boolean displayOverlay()
    {
        return true;
    }

    @ConfigItem(
            keyName = "export",
            name = "Export Times",
            description = "Exports room, floor, and total times to a .txt",
            position = 6
    )
    default boolean export()
    {
        return true;
    }
}
