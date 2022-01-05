package net.runelite.client.plugins.grotesqueguardians;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("grotesqueguardians")
public interface GGConfig extends Config {
    @ConfigSection(name = "Highlights", description = "Configuration for highlights", position = 0, closedByDefault = true)
    public static final String highlightSections = "highlights";

    @ConfigSection(name = "Tick Counters", description = "Configuration for tick counters", position = 1, closedByDefault = true)
    public static final String tickCounterSections = "tickCounters";

    @ConfigSection(name = "Utilities", description = "Utilities to assist during the fight", position = 2, closedByDefault = true)
    public static final String utilitySection = "utilities";

    @ConfigItem(name = "AoE Warnings", keyName = "highlightAOE", description = "Highlights the AoE in a 3x3 for the options below", position = 3, section = "highlights")
    default boolean highlightAOE() {
        return false;
    }

    @ConfigItem(name = "Highlight Lightning", keyName = "highlightLightning", description = "Highlights the lightning that spawns", position = 4, section = "highlights")
    default boolean highlightLightning() {
        return false;
    }

    @Alpha
    @ConfigItem(name = "Lightning Color", keyName = "lightningColor", description = "Configures the color of marked lightning", position = 5, section = "highlights")
    default Color lightningColor() {
        return Color.YELLOW;
    }

    @ConfigItem(name = "Highlight Falling Rocks", keyName = "highlightFallingRocks", description = "Highlights the falling rocks that spawns", position = 6, section = "highlights")
    default boolean highlightFallingRocks() {
        return false;
    }

    @Alpha
    @ConfigItem(name = "Falling Rocks Color", keyName = "fallingRocksColor", description = "Configures the color of marked falling rocks", position = 7, section = "highlights")
    default Color fallingRocksColor() {
        return Color.RED;
    }

    @ConfigItem(name = "Highlight Dawns Stone Orb", keyName = "highlightStoneOrbs", description = "Highlights the stone orb that Dawn throws", position = 8, section = "highlights")
    default boolean highlightStoneOrb() {
        return false;
    }

    @Alpha
    @ConfigItem(name = "Stone Orb Color", keyName = "stoneOrbColor", description = "Configures the color of the marked stone orb", position = 9, section = "highlights")
    default Color stoneOrbColor() {
        return Color.WHITE;
    }

    @ConfigItem(
            keyName = "opacity",
            name = "Opacity",
            description = "The opacity of AoE attacks from 0 to 255 (0 being solid and 255 being transparent)",
            position = 10,
            section = "highlights"
    )
    default int opacity()
    {
        return 50;
    }

    @ConfigItem(
            keyName = "width",
            name = "Width",
            description = "The width of AoE attacks.)",
            position = 11,
            section = "highlights"
    )
    default int width()
    {
        return 2;
    }

    @ConfigItem(name = "Dawn Attack Tick Counter", keyName = "dawnTickCounter", description = "Displays when Dawn can attack and her transition times", position = 10, section = "tickCounters")
    default boolean dawnTickCounter() {
        return false;
    }

    @ConfigItem(name = "Dusk Attack Tick Counter", keyName = "duskTickCounter", description = "Displays when Dusk can attack and his transition times", position = 11, section = "tickCounters")
    default boolean duskTickCounter() {
        return false;
    }

    @ConfigItem(name = "Healing Orb Tick Counter", keyName = "healingOrbTickCounter", description = "Displays when Dawn will heal off her orbs", position = 12, section = "tickCounters")
    default boolean healingOrbCounter() {
        return false;
    }

    @ConfigItem(name = "Step Back Warning", keyName = "stepBackWarning", description = "Displays to when to step back during dusk glowing wing attack", position = 13, section = "utilities")
    default boolean stepBackWarning() {
        return false;
    }

    @ConfigItem(name = "Prayer Helper", keyName = "prayerHelper", description = "Displays what to pray during phase 4", position = 14, section = "utilities")
    default boolean prayerHelper() {
        return false;
    }

    @ConfigItem(name = "Dusk Overlay", keyName = "duskOverlay", description = "Overlay on dusk with a color denoting whether its attackable or transitioning", position = 15, section = "utilities")
    default boolean duskOverlay() {
        return false;
    }

    @ConfigItem(
            keyName = "fightOpacity",
            name = "Opacity",
            description = "The opacity of Dawn, Dusk, and the healing orbs.)",
            position = 13,
            section = "tickCounters"
    )
    default int fightOpacity()
    {
        return 20;
    }

    @ConfigItem(
            keyName = "fightWidth",
            name = "Tile Width",
            description = "The width of Dawn, Dusk, and the healing orbs highlights.)",
            position = 14,
            section = "tickCounters"
    )
    default int fightWidth()
    {
        return 2;
    }

    @ConfigItem(name = "Dawn Overlay", keyName = "dawnOverlay", description = "Overlay on dawn with a color denoting whether its attackable or transitioning", position = 16, section = "utilities")
    default boolean dawnOverlay() {
        return false;
    }



    @ConfigItem(name = "Splits", keyName = "splits", description = "Splits timer for each phase during the fight", position = 17, section = "utilities")
    default boolean splitsTimer() {
        return false;
    }
}
