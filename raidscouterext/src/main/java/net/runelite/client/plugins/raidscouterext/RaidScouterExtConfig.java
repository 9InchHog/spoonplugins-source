package net.runelite.client.plugins.raidscouterext;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import java.awt.Color;

@ConfigGroup("spoonraidscouter")
public interface RaidScouterExtConfig extends Config {
    @ConfigItem(
            position = 1,
            keyName = "scoutOverlay",
            name = "Show scout overlay",
            description = "Display an overlay that shows the current raid layout (when entering lobby)"
    )
    default boolean scoutOverlay() { return true; }

    @ConfigItem(
            position = 2,
            keyName = "hideBackground",
            name = "Hide scouter background",
            description = "Removes the scouter background, and makes it transparent."
    )
    default boolean hideBackground()
    {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "displayFloorBreak",
            name = "Layout floor break",
            description = "Displays floor break in layout"
    )
    default boolean displayFloorBreak() { return false; }

    @ConfigItem(
            position = 4,
            keyName = "ptsHr",
            name = "Show pts/hr message",
            description = "Puts a message showing how many points an hour you are getting"
    )
    default boolean ptsHr() { return false; }

    @ConfigItem(
            position = 5,
            keyName = "hideRopeless",
            name = "Hide ropeless raids",
            description = "Completely hides raids missing a tightrope"
    )
    default ropelessMode hideRopeless() { return ropelessMode.OFF; }

    @ConfigItem(
            position = 6,
            keyName = "hideCustom",
            name = "Hide specific raids",
            description = "Completely hides any raid not specified in the list"
    )
    default boolean hideCustom() { return false; }

    @ConfigItem(
            position = 7,
            keyName = "hideCustomList",
            name = "Specific raids list",
            description = "Completely hides any raid not specified in the list"
    )
    default String hideCustomList() { return "msm,mtm,vtv,tvg,gvtv,mgv,tmg"; }

    @ConfigItem(
            position = 8,
            keyName = "showRecommendedItems",
            name = "Show recommended items",
            description = "Adds overlay with recommended items to scouter"
    )
    default boolean showRecommendedItems() { return false; }

    @ConfigItem(
            position = 9,
            keyName = "recommendedItems",
            name = "Recommended items",
            description = "User-set recommended items in the form: [muttadiles,ice barrage,zamorak godsword],[tekton,elder maul], ..."
    )
    default String recommendedItems()
    { return "[muttadiles,iron axe],[vasa,ghrazi rapier],[thieving,lockpick],[shamans,antidote++(4)],[mystics,salve(ei)],[tekton,inquisitor's mace],[tightrope,phoenix necklace],[guardians, 3rd age pickaxe]"; }


    @ConfigItem(
            position = 10,
            keyName = "highlightedRooms",
            name = "Highlighted rooms",
            description = "Display highlighted rooms in a different color on the overlay. Separate with comma (full name)"
    )
    default String highlightedRooms() { return ""; }

    @ConfigItem(
            position = 11,
            keyName = "highlightColor",
            name = "Highlight color",
            description = "The color of highlighted rooms"
    )
    default Color highlightColor() { return Color.MAGENTA; }

    @ConfigItem(
            position = 12,
            keyName = "hideMissingHighlighted",
            name = "Hide missing highlighted",
            description = "Completely hides raids missing highlighted room(s)"
    )
    default boolean hideMissingHighlighted() { return false; }

    @ConfigItem(
            position = 13,
            keyName = "highlightedShowThreshold",
            name = "Show threshold",
            description = "The number of highlighted rooms needed to show the raid. 0 means no threshold."
    )
    default int highlightedShowThreshold() { return 0; }

    @ConfigItem(
            position = 14,
            keyName = "hideBlacklist",
            name = "Hide raids with blacklisted",
            description = "Completely hides raids containing blacklisted room(s)"
    )
    default boolean hideBlacklisted() { return false; }

    @ConfigItem(
            position = 15,
            keyName = "hideMissingLayout",
            name = "Hide missing layout",
            description = "Completely hides raids missing a whitelisted layout"
    )
    default boolean hideMissingLayout() { return false; }

    @ConfigItem(
            position = 99,
            keyName = "screenshotHotkey",
            name = "Scouter screenshot hotkey",
            description = "Hotkey used to screenshot the scouting overlay"
    )
    default Keybind screenshotHotkey() { return Keybind.NOT_SET; }

    enum ropelessMode{
        OFF,ROPE, CRABS_AND_ROPE
    }
}
