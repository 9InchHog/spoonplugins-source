package net.runelite.client.plugins.spoonnightmare;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("Nightmare")
public interface SpoonNightmareConfig extends Config {
    @ConfigSection(name = "Nightmare", description = "Configuration for Nightmare", position = 0, closedByDefault = true)
    public static final String nightmareSection = "nightmare";

    @ConfigSection(name = "Hands", description = "Configuration for black hands", position = 1, closedByDefault = true)
    public static final String handsSection = "hands";

    @ConfigSection(name = "Totems", description = "Configuration for totems", position = 2, closedByDefault = true)
    public static final String totemsSection = "totems";

    @ConfigSection(name = "Husks", description = "Configuration for Husks", position = 3, closedByDefault = true)
    public static final String husksSection = "husks";

    @ConfigSection(name = "Spores", description = "Configuration for the spores", position = 4, closedByDefault = true)
    public static final String sporesSection = "spores";

    @ConfigSection(name = "Ticks", description = "Configuration for attack/event ticks", position = 5, closedByDefault = true)
    public static final String ticksSection = "ticks";

    @ConfigSection(name = "Stats", description = "Configuration for kill stats", position = 6, closedByDefault = true)
    public static final String statsSection = "stats";

    //Nightmare
    @ConfigItem(keyName = "prayer", name = "Easy Prayer", description = "Highlights Correct Prayer.", section = "nightmare")
    default boolean easyPrayer() {
        return true;
    }

    @Range(max = 3, min = 1)
    @ConfigItem(name = "Easy Prayer Width", keyName = "prayerStrokeSize", description = "Configure the stroke of marked prayers", section = "nightmare")
    default int prayerStrokeSize() {
        return 1;
    }

    @ConfigItem(name = "Easy Prayer Infobox", keyName = "prayerHelper", description = "Infobox showing what to pray during the fight", section = "nightmare")
    default boolean prayerHelper() {
        return false;
    }

    @ConfigItem(keyName = "parasiteTimer", name = "Parasite Spawn Timer", description = "Notifies you when a bug has crawled up ur ass so u dont 4get to drink sanfew and fuckin die. Also spawn timer for parasite.", section = "nightmare")
    default boolean parasiteTimer() {
        return true;
    }

    @ConfigItem(keyName = "p3Runway", name = "P3 Runway", description = "Jets on", section = "nightmare")
    default runwayMode p3Runway() {
        return runwayMode.COLOR;
    }

    @ConfigItem(keyName = "p3RunwayColor", name = "P3 Runway Color", description = "Runway Color", section = "nightmare")
    default Color p3RunwayColor() {
        return Color.RED;
    }

    @ConfigItem(keyName = "hidePrayers", name = "Hide Prayers", description = "Removes all unnecessary prayers for nightmare.", section = "nightmare")
    default boolean hidePrayer() {
        return false;
    }

    @ConfigItem(keyName = "swapPrayers", name = "Swap Prayers", description = "Swaps prayers during curse phase.", section = "nightmare")
    default boolean swapPrayers() {
        return false;
    }

    @ConfigItem(keyName = "a10Strafe", name = "A10 Strafe", description = "Brrrrrrrrrrrrrrrrrt", section = "nightmare")
    default boolean a10Strafe() { return false; }

    @Range(min = 1, max = 100)
    @ConfigItem(keyName = "a10StrafeVoulme", name = "A10 Volume", description = "Brrrrrrrrrrrrrrrrrt volume", section = "nightmare")
    default int a10StrafeVolume() { return 40; }

    @ConfigItem(keyName = "hideAttack", name = "Hide Attack", description = "Hides attack option on The Nightmare when parasites or totems are out", section = "nightmare")
    default hideAttackMode hideAttack() { return hideAttackMode.OFF; }

    @ConfigItem(keyName = "hideAttackIgnore", name = "Hide Attack Ignore", description = "Ignores hide attack when these events are happening", section = "nightmare")
    default hideAttackIgnoreMode hideAttackIgnore() { return hideAttackIgnoreMode.OFF; }

    @ConfigItem(keyName = "hideAttackSleepwalkers", name = "Hide Attack Sleepwalkers", description = "Removes the attack option on sleepwalkers during the final hell phase of phosani's", section = "nightmare")
    default boolean hideAttackSleepwalkers() {
        return false;
    }

    @ConfigItem(keyName = "lowFps", name = "Fps Boost", description = "Removes some game objects that can cause fps issues", section = "nightmare")
    default boolean lowFps() {return true;}

    //Hands
    @ConfigItem(keyName = "hands", name = "Black Hands", description = "So you don't catch these hands.", section = "hands")
    default handsMode nightmareHands() {
        return handsMode.TILE;
    }

    @ConfigItem(keyName = "handsColor", name = "Black Hands Color", description = "Color of the grabby boys.", section = "hands")
    default Color nightmareHandsColor() {
        return Color.CYAN;
    }

    @Range(max = 5, min = 0)
    @ConfigItem(name = "Hands Width Size", keyName = "handsSize", description = "Configure the stroke of hands.", section = "hands")
    default int handsWidth() {
        return 1;
    }

    @Range(max = 255, min = 0)
    @ConfigItem(name = "Hands Opacity", keyName = "handsOpacity", description = "Configure the opacity of them hands boi.", section = "hands")
    default int handsOpacity() {
        return 50;
    }

    @ConfigItem(keyName = "muteHands", name = "Mute Hands", description = "Mutes the sound of the black hands spawning", section = "hands", position = 98)
    default boolean muteHands() { return false; }

    @Range(max = 4, min = 0)
    @ConfigItem(name = "Hands Glow", keyName = "handsGlow", description = "Configure the glow of the hands", section = "hands")
    default int handsGlow() {
        return 0;
    }

    @ConfigItem(keyName = "raveHands", name = "Rave Hands", description = "I have seen the sun.... its radiance is.... blinding", section = "hands", position = 99)
    default boolean raveHands() {
        return false;
    }

    @ConfigItem(keyName = "handsDistance", name = "Hands Distance", description = "Toggle to only highlight hands within a certain distance", section = "hands")
    default boolean handsDistance() {
        return false;
    }

    @Range(max = 15, min = 1)
    @ConfigItem(name = "Hands Distance Limit", keyName = "handsDistanceLimit", description = "Sets how close the hands have to be to highlight", section = "hands")
    default int handsDistanceLimit() {
        return 6;
    }

    @ConfigItem(keyName = "handsTicks", name = "Hands Ticks", description = "Time until they get up in that ass", section = "hands", position = 97)
    default boolean handsTicks() { return false; }

    //Totems
    @ConfigItem(keyName = "totemHighlight", name = "Totem Highlight", description = "Highlights which totems are alive.", section = "totems", position = 0)
    default totemHighlightMode totemHighlight() {
        return totemHighlightMode.AREA;
    }

    @ConfigItem(keyName = "totemHighlightColor", name = "Totem Highlight Color", description = "Sets the color the totems are highlighted", section = "totems", position = 1)
    default Color totemHighlightColor() {
        return Color.GREEN;
    }

    @ConfigItem(name = "Totem Color Mode", keyName = "totemColorMode", description = "May as well full send it", section = "totems", position = 2)
    default totemColorMode totemColorMode() {return totemColorMode.COLOR;}

    @ConfigItem(name = "Totem HP", keyName = "totemHP", description = "Displays the hp of the totems.", section = "totems")
    default boolean totemHP() {
        return false;
    }

    @Range(min = 10, max = 30)
    @ConfigItem(name = "Totem HP Size", keyName = "totemHPSize", description = "Configures the text size of the totem hp", section = "totems")
    default int totemHPSize() {
        return 15;
    }

    @Range(min = 0, max = 4)
    @ConfigItem(name = "Totem Glow", keyName = "totemGlow", description = "Sets the amount of glow on active totems when outline is on", section = "totems", position = 98)
    default int totemGlow() { return 4; }

    @Range(min = 1, max = 5)
    @ConfigItem(name = "Totem Width", keyName = "totemWidth", description = "Sets the width of the active totem outline", section = "totems")
    default int totemWidth() { return 2; }

    //Husks
    @ConfigItem(name = "Husk Highlight", keyName = "huskHighlight", description = "Highlights the Husk based on attack styles", section = "husks", position = 0)
    default boolean huskHighlight() {
        return false;
    }

    @ConfigItem(name = "Husk Target", keyName = "huskTarget", description = "Highlights the player the husks are going to target", section = "husks", position = 1)
    default boolean huskTarget() {
        return false;
    }

    @ConfigItem(name = "Husk Target Color", keyName = "huskTargetColor", description = "Color of the tile highlighting who the husks are targetting", section = "husks", position = 2)
    default Color huskTargetColor() {
        return Color.RED;
    }

    @Range(max = 5, min = 0)
    @ConfigItem(name = "Husk Width", keyName = "huskWidth", description = "Configure the width of husks highlight", section = "husks", position = 3)
    default int huskWidth() {
        return 1;
    }

    @Range(max = 255, min = 0)
    @ConfigItem(name = "Husk Opacity", keyName = "huskOpacity", description = "Configure the opacity of husks highlight", section = "husks", position = 4)
    default int huskOpacity() {
        return 50;
    }
    
    //Shrooms
    @ConfigItem(keyName = "highlightSpores", name = "Highlight Spores", description = "Highlights spores that will make you yawn", section = "spores")
    default boolean highlightSpores() {
        return false;
    }

    @ConfigItem(keyName = "sporeBorderColor", name = "Spores Outline Color", description = "Sets border color of spore aoe", section = "spores")
    default Color sporeBorderColor() {
        return new Color(255, 0, 0, 255);
    }

    @ConfigItem(name = "Spores Tick Counter", keyName = "sporesTickCounter", description = "Countdown when the spores will disappear.", section = "spores")
    default boolean sporesTickCounter() { return false;}

    @ConfigItem(name = "Spores Tick Color", keyName = "sporesTickColor", description = "Color of the spores tick counter.", section = "spores")
    default Color sporesTickColor() {return Color.WHITE;}

    @Range(max = 3, min = 0)
    @ConfigItem(name = "Spore Width", keyName = "sporeWidth", description = "Configure the width of spores.", section = "spores")
    default int sporeWidth() { return 1;}

    @Range(max = 255, min = 0)
    @ConfigItem(name = "Spore Opacity", keyName = "sporeOpacity", description = "Configure the opacity of spores.", section = "spores")
    default int sporeOpacity() {return 50;}

    @ConfigItem(name = "Yawn Timer", keyName = "yawnTimer", description = "Overlay to display ticks left till you can run again", section = "spores")
    default boolean yawnTimer() { return false; }

    //Ticks
    @ConfigItem(name = "Tick Counter", keyName = "tickCounter", description = "Displays when Nightmare will attack again", section = "ticks")
    default boolean tickCounter() { return false; }

    @ConfigItem(name = "Event Tick Counter", keyName = "eventTickCounter", description = "Displays the durations of flowers, surge, and intermission phases.", section = "ticks")
    default boolean eventTickCounter() { return false; }

    @ConfigItem(name = "Text Outline", keyName = "textOutline", description = "Outlines the text for overlays", section = "ticks", position = 97)
    default boolean txtOutline() { return true; }

    @Alpha
    @ConfigItem(keyName = "ticksCounterColor", name = "Tick Counter Color", description = "Configures the color of the tick counter", section = "ticks", position = 98)
    default Color tickCounterColor() { return Color.WHITE; }

    @Range(min = 1, max = 50)
    @ConfigItem(keyName = "ticksCounterSize", name = "Tick Counter Size", description = "Configures the font size of the tick counter", section = "ticks", position = 99)
    default int tickCounterSize() {return 20;}

    @ConfigItem(name = "Match Style Color", keyName = "matchStyleColor", description = "Tick counter changes color to match the attack style of The Nightmare", section = "ticks", position = 96)
    default boolean matchStyleColor() { return false; }

    //Stats
    @ConfigItem(name = "Display Chat Message", keyName = "displayStatsMsg", description = "Outputs a game message when The Nightmare dies displaying the stats of the kill based on whats checked below", section = "stats", position = 0)
    default boolean displayStatsMsg() { return false; }

    @ConfigItem(name = "Husk Sets Spawned", keyName = "huskStats", description = "Shows how many sets of husks spawned", section = "stats", position = 1)
    default boolean huskStats() { return false; }

    @ConfigItem(name = "Parasites Spawned", keyName = "parasiteStats", description = "Shows how many parasites spawned", section = "stats", position = 2)
    default boolean parasiteStats() { return false; }

    @ConfigItem(name = "Sleepwalkers Leaked", keyName = "sleepwalkerStats", description = "Shows how many sleepwalkers were let through", section = "stats", position = 3)
    default boolean sleepwalkerStats() { return false; }

    @ConfigItem(name = "Total Healing", keyName = "healingStats", description = "Shows how much The Nightmare and the totems were healed during the fight", section = "stats", position = 4)
    default healingStatsMode healingStats() { return healingStatsMode.OFF; }

    public enum hideAttackMode {
        OFF, PARASITE, TOTEM, HUSK, ALL;
    }

    public enum hideAttackIgnoreMode {
        OFF, FLOWERS, SPORES, BOTH;
    }

    public enum handsMode {
        OFF, TILE, OUTLINE;
    }

    public enum totemHighlightMode {
        OFF, AREA, OUTLINE;
    }

    public enum runwayMode {
        OFF, COLOR, RAVE, RAVEST;
    }

    public enum totemColorMode {
        COLOR, RAVE, RAVEST;
    }

    public enum healingStatsMode {
        OFF, BOSS, TOTEMS, BOTH;
    }
}
