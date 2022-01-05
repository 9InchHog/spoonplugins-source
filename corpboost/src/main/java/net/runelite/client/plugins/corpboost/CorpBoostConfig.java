package net.runelite.client.plugins.corpboost;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("corpboost")
public interface CorpBoostConfig extends Config{
    @ConfigSection(
            name = "Dark Core",
            description = "Dark Core Plugins",
            position = 1,
            closedByDefault = true
    )
    String coreSection = "core";

    @ConfigSection(
            name = "Boosting",
            description = "Boosting Plugins",
            position = 2,
            closedByDefault = true
    )
    String boostingSection = "boosting";

    @ConfigItem(
            keyName = "showCannon",
            name = "Mark Cannon Tiles",
            description = "Marks where you should place your cannon when boosting.",
            position = 1,
            section = boostingSection
    )
    default boolean showCannon()
    {
        return false;
    }

    @ConfigItem(
            keyName = "spearAlt",
            name = "Mark Spear Alt Tiles",
            description = "Marks where you should stand on spear alts.",
            position = 2,
            section = boostingSection
    )
    default boolean spearAlt()
    {
        return false;
    }

    @ConfigItem(
            keyName = "spearHealer",
            name = "Mark Spear Healer Tiles",
            description = "Marks where you should stand on spear healers.",
            position = 3,
            section = boostingSection
    )
    default boolean spearHealer()
    {
        return false;
    }

    @ConfigItem(
            keyName = "bowHealer",
            name = "Mark Bow Healer Tiles",
            description = "Marks where you should stand on bow healers.",
            position = 4,
            section = boostingSection
    )
    default boolean bowHealer()
    {
        return false;
    }

    @ConfigItem(
            keyName = "dwh",
            name = "Mark Dragon Warhammer Tiles",
            description="Marks where you should stand on Dragon Warhammer Accounts.",
            position = 5,
            section = "boosting"
    )
    default boolean dwh() {
        return false;
    }

    @ConfigItem(
            keyName = "dwh2",
            name = "Mark Backup Hammers",
            description = "Marks where you should stand on backup hammer accounts.",
            position = 6,
            section = "boosting"
    )
    default boolean dwh2() {
        return false;
    }

    @ConfigItem(
            keyName = "stunner",
            name = "Mark Stunner Tiles",
            description = "Marks where you should stand as the stunner.",
            position = 7,
            section = "boosting"
    )
    default boolean stunner() {
        return false;
    }

    @ConfigItem(
            keyName = "customer",
            name = "Mark Customer Tiles",
            description = "Marks where you should stand as the customer.",
            position = 5,
            section = boostingSection
    )
    default boolean customer()
    {
        return false;
    }

    //Core Section
    @ConfigItem(
            keyName = "coreArrow",
            name = "Core Hint Arrow",
            description = "Marks the dark core with an arrow",
            position = 1,
            section = coreSection
    )
    default boolean coreArrow()
    {
        return true;
    }

    @ConfigItem(
            keyName = "coreHighlight",
            name = "Core Highlight",
            description = "Highlight Core",
            position = 2,
            section = coreSection
    )
    default CoreHighlightMode coreHighlight()
    {
        return CoreHighlightMode.OFF;
    }

    @Alpha
    @ConfigItem(
            keyName = "coreHighlightColor",
            name = "Core Highlight Color",
            description = "Adjusts color of dark core highlight",
            position = 3,
            section = coreSection
    )
    default Color coreHighlightColor()
    {
        return Color.WHITE;
    }

    @Range(min = 1, max = 10)
    @ConfigItem(
            keyName = "coreHighlightWidth",
            name = "Core Highlight Width",
            description = "Adjusts width of dark core highlight",
            position = 4,
            section = coreSection
    )
    default int coreHighlightWidth() { return 2; }

    @Range(min = 1, max = 10)
    @ConfigItem(
            keyName = "coreHighlightGlow",
            name = "Core Highlight Glow",
            description = "Adjusts the glow of dark core highlight",
            position = 5,
            section = coreSection
    )
    default int coreHighlightGlow() { return 2; }

    @Range(min = 0, max = 255)
    @ConfigItem(
            keyName = "coreHighlightOpacity",
            name = "Core Highlight Opcaity",
            description = "Adjusts opcaity of dark core highlight",
            position = 6,
            section = coreSection
    )
    default int coreHighlightOpacity() { return 20; }

    @ConfigItem(
            keyName = "bpCore",
            name = "Stun Core MES",
            description = "Prioritizes attack on dark core when blowpipe and serp equipped",
            position = 7,
            section = coreSection
    )
    default boolean bpCore() { return true; }

    public enum CoreHighlightMode {
        OFF, AREA, HULL, TILE, TRUE_LOCATION, OUTLINE;
    }
}
