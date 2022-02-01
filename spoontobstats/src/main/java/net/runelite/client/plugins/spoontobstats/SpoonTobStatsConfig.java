package net.runelite.client.plugins.spoontobstats;

import net.runelite.client.config.*;

@ConfigGroup("spoontobstats")
public interface SpoonTobStatsConfig extends Config
{
    @ConfigSection(
            name = "Chat Messages",
            description = "Settings for messages in the chatbox",
            position = 0
    )
    String chatSettings = "chatSettings";

    @ConfigItem(
            keyName = "dmgMsg",
            name = "Damage Message",
            description = "Shows personal damage and percent damage in the chat",
            section = chatSettings,
            position = 0
    )
    default boolean dmgMsg() { return false; }

    @ConfigItem(
            keyName = "healMsg",
            name = "Healed Message",
            description = "Shows the amount healed",
            section = chatSettings,
            position = 1
    )
    default boolean healMsg() { return false; }

    @ConfigItem(
            position = 2,
            keyName = "msgTiming",
            name = "Split Message Timing",
            description = "Displays the chat messages either after the completed room time or actively during each room",
            section = chatSettings
    )
    default msgTimeMode msgTiming() { return msgTimeMode.ACTIVE; }

    @ConfigItem(
            position = 3,
            keyName = "simpleMessage",
            name = "Simple Time Messages",
            description = "Puts a simplified chat message, rather than the longer and more detailed split messages",
            section = chatSettings
    )
    default boolean simpleMessage(){ return true; }

    @ConfigItem(
            position = 4,
            keyName = "preciseTimers",
            name = "Precise Timers",
            description = "Respect in-game precise timer setting",
            section = chatSettings
    )
    default PreciseTimersSetting preciseTimers() {return PreciseTimersSetting.INGAME_SETTING;}

    @ConfigItem(
            position = 5,
            keyName = "oldRoomMsg",
            name = "Old Room Messages",
            description = "Remove the mode in () from room complete message and makes it a single line",
            section = chatSettings
    )
    default boolean oldRoomMsg() {
        return false;
    }

    //Infobox Section
    @ConfigSection(
            name = "Infobox",
            description = "Settings for the infoboxes",
            position = 1
    )
    String infoBoxSettings = "infoBoxSettings";

    @ConfigItem(
            keyName = "showInfoBoxes",
            name = "Infoboxes",
            description = "Show info boxes",
            section = infoBoxSettings,
            position = 1
    )
    default boolean showInfoBoxes() { return true; }

    @ConfigItem(
            keyName = "infoBoxText",
            name = "Infobox Text",
            description = "The text displayed on the info box",
            section = infoBoxSettings,
            position = 1
    )
    default InfoboxText infoBoxText() { return InfoboxText.TIME; }

    @ConfigItem(
            keyName = "infoBoxTooltip",
            name = "Infobox Tooltip",
            description = "Display info box tooltip",
            section = infoBoxSettings,
            position = 2
    )
    default boolean infoBoxTooltip() { return true; }

    @ConfigItem(
            keyName = "infoBoxTooltipDmg",
            name = "Infobox Tooltip Damage",
            description = "Display damage info in the info box tooltip",
            section = infoBoxSettings,
            position = 3
    )
    default boolean infoBoxTooltipDmg() { return true; }

    @ConfigItem(
            keyName = "infoBoxTooltipHealed",
            name = "Infobox Tooltip Healed",
            description = "Display amount healed in the info box tooltip",
            section = infoBoxSettings,
            position = 4
    )
    default boolean infoBoxTooltipHealed() { return true; }

    @ConfigItem(
            keyName = "infoBoxTooltipSplits",
            name = "Infobox Tooltip Splits",
            description = "Display splits in the info box tooltip",
            section = infoBoxSettings,
            position = 5
    )
    default boolean infoBoxTooltipSplits() { return true; }

    //No section
    @ConfigItem(
            name = "Show Timer Overlay",
            keyName = "timerOverlay",
            description = "Show a timer overlay on screen",
            position = 100
    )
    default boolean timerOverlay() { return true; }

    @ConfigItem(
            name = "Simple Timer",
            keyName = "simpleOverlay",
            description = "Show a simple version of the timer overlay",
            position = 101
    )
    default boolean simpleOverlay() { return false; }

    @ConfigItem(
            name = "Custom Font Name",
            keyName = "fontName",
            description = "Custom font override. Leave blank to disable",
            position = 102
    )
    default String fontName() {
        return "sansserif";
    }

    @ConfigItem(
            name = "Custom Font Size",
            keyName = "fontsSize",
            description = "",
            position = 103
    )
    default int fontSize() { return 11; }

    @ConfigItem(
            name = "Custom Weight",
            keyName = "fontWeight",
            description = "Sets the custom font weight",
            position = 104
    )
    default FontWeight fontWeight() { return FontWeight.PLAIN; }

    @ConfigItem(
            name = "Maiden Flash",
            keyName = "flash",
            description = "Flash the screen on fast maiden procs (north all mager cannot freeze 1+2)",
            position = 105
    )
    default boolean flash() { return true; }

    @ConfigItem(
            name = "Time Exporter",
            keyName = "timeExporter",
            description = "Exports Tob times to .txt files in .openosrs/times",
            position = 106
    )
    default boolean timeExporter() { return false; }

    enum msgTimeMode {
        ACTIVE, ROOM_END
    }

    enum PreciseTimersSetting {
        TICKS, SECONDS, INGAME_SETTING
    }

    enum FontWeight {
        PLAIN(0), BOLD(1), ITALIC(2);

        public int weight;
        FontWeight(int i) {
            weight = i;
        }

        public int getWeight() {
            return weight;
        }
    }
}
