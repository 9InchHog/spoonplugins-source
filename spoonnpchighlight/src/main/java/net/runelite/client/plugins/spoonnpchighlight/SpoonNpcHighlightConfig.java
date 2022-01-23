package net.runelite.client.plugins.spoonnpchighlight;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("SpoonNpcHighlight")
public interface SpoonNpcHighlightConfig extends Config {
    @ConfigSection(
            name = "Tile",
            description = "Tile Plugins",
            position = 1,
            closedByDefault = true
    )
    String tileSection = "tile";

    @ConfigSection(
            name = "True Tile",
            description = "True Tile Plugins",
            position = 2,
            closedByDefault = true
    )
    String trueTileSection = "trueTile";

    @ConfigSection(
            name = "South West Tile",
            description = "South West Tile Plugins",
            position = 3,
            closedByDefault = true
    )
    String swTileSection = "swTile";

    @ConfigSection(
            name = "Hull",
            description = "Hull Plugins",
            position = 4,
            closedByDefault = true
    )
    String hullSection = "hull";

    @ConfigSection(
            name = "Area",
            description = "Area Plugins",
            position = 5,
            closedByDefault = true
    )
    String areaSection = "area";

    @ConfigSection(
            name = "Outline",
            description = "Outline Plugins",
            position = 6,
            closedByDefault = true
    )
    String outlineSection = "outline";

    @ConfigSection(
            name = "<html><font color=#ff0000><i><b>TURBO MODE<b><i>",
            description = "Fuckin send it",
            position = 7,
            closedByDefault = true
    )
    String turboSection = "section";

    //Tile Section
    @ConfigItem(
            position = 1,
            keyName = "tileHighlight",
            name = "Tile Highlight",
            description = "Highlights npc's tile",
            section = tileSection
    )
    default boolean tileHighlight() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "tileNames",
            name = "Tile Names",
            description = "List of npc's to highlight tile",
            section = tileSection
    )
    default String tileNames() {
        return "";
    }

    @ConfigItem(
            keyName = "tileNames",
            name = "",
            description = ""
    )
    void setTileNames(String names);

    @ConfigItem(
            position = 3,
            keyName = "tileIds",
            name = "Tile IDs",
            description = "List of npc's to highlight tile",
            section = tileSection
    )
    default String tileIds() {
        return "";
    }

    @ConfigItem(
            keyName = "tileIds",
            name = "",
            description = ""
    )
    void setTileIds(String ids);

    //True Tile Section
    @ConfigItem(
            position = 1,
            keyName = "trueTileHighlight",
            name = "True Tile Highlight",
            description = "Highlights npc's true tile",
            section = trueTileSection
    )
    default boolean trueTileHighlight() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "trueTileNames",
            name = "True Tile Names",
            description = "List of npc's to highlight true tile",
            section = trueTileSection
    )
    default String trueTileNames() {
        return "";
    }

    @ConfigItem(
            keyName = "trueTileNames",
            name = "",
            description = ""
    )
    void setTrueTileNames(String names);

    @ConfigItem(
            position = 3,
            keyName = "trueTileIds",
            name = "True Tile IDs",
            description = "List of npc's to highlight true tile",
            section = trueTileSection
    )
    default String trueTileIds() {
        return "";
    }

    @ConfigItem(
            keyName = "trueTileIds",
            name = "",
            description = ""
    )
    void setTrueTileIds(String ids);

    //SW Tile Section
    @ConfigItem(
            position = 1,
            keyName = "swTileHighlight",
            name = "South West Tile Highlight",
            description = "Highlights npc's south west tile",
            section = swTileSection
    )
    default boolean swTileHighlight() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "swTileNames",
            name = "South West Tile Names",
            description = "List of npc's to highlight south west tile",
            section = swTileSection
    )
    default String swTileNames() {return "";}

    @ConfigItem(
            keyName = "swTileNames",
            name = "",
            description = ""
    )
    void setSwTileNames(String names);

    @ConfigItem(
            position = 3,
            keyName = "swTileIds",
            name = "South West Tile IDs",
            description = "List of npc's to highlight south west tile",
            section = swTileSection
    )
    default String swTileIds() {
        return "";
    }

    @ConfigItem(
            keyName = "swTileIds",
            name = "",
            description = ""
    )
    void setSwTileIds(String ids);

    //Hull Section
    @ConfigItem(
            position = 1,
            keyName = "hullHighlight",
            name = "Hull Highlight",
            description = "Highlight npc's hull",
            section = hullSection
    )
    default boolean hullHighlight() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "hullNames",
            name = "Hull Names",
            description = "List of npc's to highlight hull",
            section = hullSection
    )
    default String hullNames() {
        return "";
    }

    @ConfigItem(
            keyName = "hullNames",
            name = "",
            description = ""
    )
    void setHullNames(String names);

    @ConfigItem(
            position = 3,
            keyName = "hullIds",
            name = "Hull IDs",
            description = "List of npc's to highlight hull",
            section = hullSection
    )
    default String hullIds() {
        return "";
    }

    @ConfigItem(
            keyName = "hullIds",
            name = "",
            description = ""
    )
    void setHullIds(String ids);

    //Area Section
    @ConfigItem(
            position = 1,
            keyName = "areaHighlight",
            name = "Area Highlight",
            description = "Highlights npc's area",
            section = areaSection
    )
    default boolean areaHighlight() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "areaNames",
            name = "Area Names",
            description = "List of npc's to highlight area",
            section = areaSection
    )
    default String areaNames() {
        return "";
    }

    @ConfigItem(
            keyName = "areaNames",
            name = "",
            description = ""
    )
    void setAreaNames(String names);

    @ConfigItem(
            position = 3,
            keyName = "areaIds",
            name = "Area IDs",
            description = "List of npc's to highlight area",
            section = areaSection
    )
    default String areaIds() {
        return "";
    }

    @ConfigItem(
            keyName = "areaIds",
            name = "",
            description = ""
    )
    void setAreaIds(String ids);

    //Outline Section
    @ConfigItem(
            position = 1,
            keyName = "outlineHighlight",
            name = "Outline Highlight",
            description = "Highlights npc's outline",
            section = outlineSection
    )
    default boolean outlineHighlight() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "outlineNames",
            name = "Outline Names",
            description = "List of npc's to highlight outline",
            section = outlineSection
    )
    default String outlineNames() {
        return "";
    }

    @ConfigItem(
            keyName = "outlineNames",
            name = "",
            description = ""
    )
    void setOutlineNames(String names);

    @ConfigItem(
            position = 3,
            keyName = "outlineIds",
            name = "Outline IDs",
            description = "List of npc's to highlight outline",
            section = outlineSection
    )
    default String outlineIds() {
        return "";
    }

    @ConfigItem(
            keyName = "outlineIds",
            name = "",
            description = ""
    )
    void setOutlineIds(String ids);

    @Range(min = 0, max = 50)
    @ConfigItem(
            position = 5,
            keyName = "outlineThiCC",
            name = "Outline Width",
            description = "Sets the width of outline highlights",
            section = outlineSection
    )
    default int outlineThiCC() {
        return 2;
    }

    @Range(min = 0, max = 5)
    @ConfigItem(
            position = 6,
            keyName = "outlineFeather",
            name = "Outline Feather",
            description = "Sets the feather of the outline highlights",
            section = outlineSection
    )
    default int outlineFeather() {
        return 2;
    }

    //Turbo section
    @ConfigItem(
            position = 1,
            keyName = "turboHighlight",
            name = "Turbo Highlight",
            description = "Highlights npc's outline",
            section = turboSection
    )
    default boolean turboHighlight() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "turboNames",
            name = "Turbo Names",
            description = "List of npc's to do things with",
            section = turboSection
    )
    default String turboNames() {
        return "";
    }

    @ConfigItem(
            keyName = "turboNames",
            name = "",
            description = ""
    )
    void setTurboNames(String names);

    @ConfigItem(
            position = 3,
            keyName = "turboIds",
            name = "Turbo IDs",
            description = "Can be used to see ghosts if you really want to",
            section = turboSection
    )
    default String turboIds() {
        return "";
    }

    @ConfigItem(
            position = 7,
            keyName = "tagStyleMode",
            name = "Tag Style",
            description = "Sets which highlight style the npc tagged is added too")
    default tagStyleMode tagStyleMode() { return tagStyleMode.TILE; }

    @Alpha
    @ConfigItem(
            position = 8,
            keyName = "highlightColor",
            name = "Highlight Color",
            description = "Sets color of npc highlights"
    )
    default Color highlightColor() {
        return Color.CYAN;
    }

    @Alpha
    @ConfigItem(
            position = 9,
            keyName = "fillColor",
            name = "Fill Color",
            description = "Sets the fill color of npc highlights"
    )
    default Color fillColor() {
        return new Color(0, 255, 255,20);
    }

    @Range(min = 0, max = 50)
    @ConfigItem(
            position = 10,
            keyName = "highlightThiCC",
            name = "Highlight Width",
            description = "Sets the width of npc highlights"
    )
    default double highlightThiCC() {
        return 2;
    }

    @ConfigItem(
            position = 11,
            keyName = "interactingHighlight",
            name = "Interacting Highlight",
            description = "Highlights NPC's targeting you on Color mode. Both puts the name of the NPC's target above the NPC"
    )
    default interactingHighlightMode interactingHighlight() {
        return interactingHighlightMode.OFF;
    }

    @Alpha
    @ConfigItem(
            position = 12,
            keyName = "interactingColor",
            name = "Interacting Color",
            description = "Sets the fill color of Interacting Highlight"
    )
    default Color interactingColor() {
        return Color.RED;
    }

    @ConfigItem(
            position = 13,
            keyName = "highlightMenuNames",
            name = "Highlight Menu Names",
            description = "Highlights names in right click menu entry"
    )
    default boolean highlightMenuNames() {
        return false;
    }

    @ConfigItem(
            position = 14,
            keyName = "antiAlias",
            name = "Anti-Aliasing",
            description = "Turns on anti-aliasing for the tiles. Makes them smoother."
    )
    default boolean antiAlias()
    {
        return true;
    }

    @ConfigItem(
            position = 15,
            keyName = "ignoreDeadNpcs",
            name = "Ignore Dead NPCs",
            description = "Doesn't highlight dead NPC's"
    )
    default boolean ignoreDeadNpcs() {
        return false;
    }

    @ConfigItem(
            position = 16,
            keyName = "ignoreDeadExclusion",
            name = "Ignore Dead Exclusion List",
            description = "List of npc's to not remove highlight when dead"
    )
    default String ignoreDeadExclusion() {
        return "";
    }

    @ConfigItem(
            position = 17,
            keyName = "deadNpcMenuColor",
            name = "Dead NPC Menu Color",
            description = "Highlights names in right click menu entry when NPC is dead"
    )
    Color deadNpcMenuColor();

    @ConfigItem(
            position = 18,
            keyName = "respawnTimer",
            name = "Respawn Timer",
            description = "Marks tile and shows timer for when a marker NPC will respawn"
    )
    default respawnTimerMode respawnTimer() {
        return respawnTimerMode.OFF;
    }

    @Alpha
    @ConfigItem(
            position = 19,
            keyName = "respawnTimerColor",
            name = "Respawn Time Color",
            description = "Sets the color of the text for Respawn Timer"
    )
    default Color respawnTimerColor() {
        return Color.WHITE;
    }

    @ConfigItem(
            position = 20,
            keyName = "displayName",
            name = "Display Name",
            description = "Shows name of NPC's in the list above them"
    )
    default String displayName() {
        return "";
    }

    @ConfigItem(
            position = 21,
            keyName = "npcMinimapMode",
            name = "Highlight Minimap",
            description = "Highlights NPC on minimap and or displays name"
    )
    default npcMinimapMode npcMinimapMode() {
        return npcMinimapMode.OFF;
    }

    public enum tagStyleMode {
        TILE, TRUE_TILE, SW_TILE, HULL, AREA, OUTLINE, TURBO;
    }

    public enum interactingHighlightMode {
        OFF, COLOR, BOTH;
    }

    public enum respawnTimerMode {
        OFF, TICKS, SECONDS;
    }

    public enum npcMinimapMode {
        OFF, DOT, NAME, BOTH
    }
}
