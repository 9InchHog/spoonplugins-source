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

    @ConfigSection(
            name = "Other",
            description = "Other Plugins",
            position = 3,
            closedByDefault = true
    )
    public static final String otherSection = "other";

    @ConfigItem(
            keyName = "boosterRole",
            name = "Booster Role",
            description = "Booster role helps determine which tiles per role to highlight",
            position = 1,
            section = boostingSection
    )
    default boosterRoleMode boosterRole() {return boosterRoleMode.OFF;}

    @ConfigItem(
            keyName = "spearAlt",
            name = "Mark Spear Alt Tiles",
            description = "Marks where you should stand on spear alts.",
            position = 2,
            section = boostingSection
    )
    default boolean spearAlt() {return false;}

    @ConfigItem(
            keyName = "healer",
            name = "Mark Healer Tiles",
            description = "Marks where you should stand on spear healers. <br> Depends on xfer/stunner role highlights",
            position = 3,
            section = boostingSection
    )
    default boolean healer() {return false;}

    @ConfigItem(
            keyName = "dwh",
            name = "Mark DWH Tiles",
            description="Marks where you should stand on dwh/inq Accounts.",
            position = 5,
            section = "boosting"
    )
    default boolean dwh() {return false;}

    @ConfigItem(
            keyName = "customer",
            name = "Mark Customer Tiles",
            description = "Marks where you should stand and place the cannon as the customer.",
            position = 6,
            section = boostingSection
    )
    default boolean customer() {return false;}

    @Alpha
    @ConfigItem(
            keyName = "stunTileColor",
            name = "Stunner Role Tile Color",
            description = "Sets the color for the booster doing the stunner role.",
            position = 7,
            section = boostingSection
    )
    default Color stunTileColor() {return new Color(37, 197, 79, 150);}

    @Alpha
    @ConfigItem(
            keyName = "xferTileColor",
            name = "Xfer Role Tile Color",
            description = "Sets the color for the booster doing the xfer role.",
            position = 8,
            section = boostingSection
    )
    default Color xferTileColor() {return new Color(0, 255, 255, 150);}

    @Alpha
    @ConfigItem(
            keyName = "customerTileColor",
            name = "Customer Tile Color",
            description = "Sets the color for the customer's tiles.",
            position = 9,
            section = boostingSection
    )
    default Color customerTileColor() {return new Color(255, 200, 0, 150);}

    @ConfigItem(
            keyName = "tileWidth",
            name = "Tile Width",
            description = "Sets the width for the role tiles.",
            position = 10,
            section = boostingSection
    )
    default double tileWidth() { return 2; }

    @ConfigItem(
            keyName = "tileFillOpacity",
            name = "Tile Fill Opacity",
            description = "Sets the fill opacity for the role tiles.",
            position = 11,
            section = boostingSection
    )
    default int tileFillOpacity() { return 50; }

    //------------------------------------------------------------//
    // Core Section
    //------------------------------------------------------------//
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
            name = "Core Highlight Style",
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

    @ConfigItem(
            keyName = "unchargedSerp",
            name = "Uncharged Serp Warning",
            description = "Warns when a player has an uncharged serp helm.",
            position = 8,
            section = coreSection
    )
    default boolean unchargedSerp() { return true; }

    @ConfigItem(
            keyName = "serpColor",
            name = "Uncharged Serp Color",
            description = "Color to highlight a player who has an uncharged serp helm.",
            position = 9,
            section = coreSection
    )
    default Color serpColor() { return Color.RED; }

    @ConfigItem(
            keyName = "serpWidth",
            name = "Uncharged Serp Width",
            description = "Width of the uncharged serp highlight",
            position = 10,
            section = coreSection
    )
    default double serpWidth() { return 2; }

    @ConfigItem(
            keyName = "coreStunTicks",
            name = "Show Stunned Core Ticks",
            description = "Displays how many ticks the core has been stunned for",
            position = 11,
            section = coreSection
    )
    default boolean coreStunTicks() { return true; }

    enum CoreHighlightMode {
        OFF, AREA, HULL, TILE, TRUE_LOCATION, OUTLINE;
    }

    enum boosterRoleMode {
        OFF, XFER, STUNNER, BOTH
    }

    @ConfigItem(
            keyName = "hideBlack",
            name = "Hide Black Screen",
            description = "Hide black screen when entering house",
            position = 0,
            section = "other"
    )
    default boolean hideBlack() {
        return false;
    }

    @ConfigItem(
            keyName = "hideMore",
            name = "Hide House Icon",
            description = "Also hides the little house icon when entering house",
            position = 1,
            section = otherSection
    )
    default boolean hideMore() {
        return false;
    }

    @ConfigItem(
            keyName = "hideOps",
            name = "Hide House Options",
            description = "Hides leave/expel house ops",
            position = 2,
            section = otherSection
    )
    default boolean hideOps() {
        return false;
    }

    @ConfigItem(
            keyName = "hidePlayers",
            name = "Hide Players",
            description = "hides all players when entering corp cave, reappear when boss spawns or failsafe of 60ticks later",
            position = 3,
            section = otherSection
    )
    default boolean hidePlayersTillBossSpawn() {
        return false;
    }

    @Range(min = 10, max = 60)
    @ConfigItem(keyName = "hidePlayersDuration",
            name = "Hide duration",
            description = "duration in ticks to hide players for",
            position = 4,
            section = otherSection
    )
    @Units(Units.TICKS)
    default int hidePlayersDuration() {
        return 60;
    }
}
