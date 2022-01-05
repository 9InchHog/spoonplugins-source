package net.runelite.client.plugins.bonylo;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("bonylo")
public interface BoNyloConfig extends Config {
    @ConfigSection(
            name = "Text Options",
            description = "",
            position = 0,
            closedByDefault = false
    )
    String textOptions = "textOptions";
    @ConfigSection(
            name = "End Message Options",
            description = "",
            position = 1,
            closedByDefault = false
    )
    String endMessageOptions = "endMessageOptions";
    @ConfigSection(
            name = "Low Tick Nylo Display Options",
            description = "",
            position = 2,
            closedByDefault = false
    )
    String lowTickOptions = "lowTickOptions";
    @ConfigSection(
            name = "Low Tick Nylo BOX",
            description = "",
            position = 3,
            closedByDefault = false
    )
    String boxOptions = "boxOptions";
    @ConfigSection(
            name = "Low Tick Nylo HULL",
            description = "",
            position = 4,
            closedByDefault = false
    )
    String hullOptions = "hullOptions";
    @ConfigSection(
            name = "Low Tick Nylo TILE",
            description = "",
            position = 5,
            closedByDefault = false
    )
    String tileOptions = "tileOptions";
    @ConfigSection(
            name = "Debug options",
            description = "",
            position = 6,
            closedByDefault = true
    )
    String debugOptions = "debugOptions";

    @ConfigItem(
            keyName = "textSize",
            name = "Text Size",
            description = "11 is default",
            position = 1,
            section = textOptions
    )
    default int textSize() {return 11;}

    @ConfigItem(
            keyName = "zOffText",
            name = "Text zOffset",
            description = "0 is default",
            position = 2,
            section = textOptions
    )
    default int zOffText() {return 0;}

    @ConfigItem(
            keyName = "enableBold",
            name = "Enable Bold Font",
            description = "",
            position = 3,
            section = textOptions
    )
    default boolean enableBold() {return true;}

    @ConfigItem(
            keyName = "enableMonochrome",
            name = "Enable Monochrome",
            description = "Disables all colour if you for some reason hate colour...",
            position = 4,
            section = textOptions
    )
    default boolean enableMonochrome() {return false;}

    @ConfigItem(
            keyName = "textAA",
            name = "Text Anti-Aliasing",
            description = "Makes the text smoother",
            position = 5,
            section = textOptions
    )
    default boolean textAA() {return false;}

    @ConfigItem(
            keyName = "enableSplitsMessage",
            name = "Enable splits and boss rotation message",
            description = "Displays splits",
            position = 0,
            section = endMessageOptions
    )
    default boolean enableSplitsMessage() {return true;}

    @ConfigItem(
            keyName = "enablePrePostSplits",
            name = "Enable Pre/Post Cap Split Msg",
            description = "Displays split distribution pre and post cap",
            position = 1,
            section = endMessageOptions
    )
    default boolean enablePrePostSplits() {
        return true;
    }

    @ConfigItem(
            keyName = "trackCleanupSplits",
            name = "Track Cleanup Splits",
            description = "Displays splits from wave 29-cleanup separately from post cap splits",
            position = 2,
            section = endMessageOptions
    )
    default boolean trackCleanupSplits() {
        return true;
    }

    @ConfigItem(
            keyName = "enablePrePostSplits",
            name = "Splits Message On Boss Spawn",
            description = "Displays split messages on boss spawn, leave it off if you want to see the split messages on boss despawn",
            position = 3,
            section = endMessageOptions
    )
    default boolean enableGroupAfterSpawn() {return true;}

    @Units(" ticks")
    @Range(max = 52, min = 1)
    @ConfigItem(
            keyName = "BoxLowThreshold",
            name = "Low Tick Threshold",
            description = "Set threshold for box to disappear over low tick nylo",
            position = 1,
            section = lowTickOptions
    )
    default int BoxLowThreshold() {return 26;}

    @Range(max = 31, min = 1)
    @ConfigItem(
            keyName = "enableAfterWave",
            name = "Enable After Wave",
            description = "Set which wave you want to start displaying low tick nylo boxes",
            position = 2,
            section = lowTickOptions
    )
    default int enableAfterWave() {return 1;}

    @ConfigItem(
            keyName = "useSpoonTob",
            name = "Use Spoon Tob Overlay Settings",
            description = "Uses the Spoon Tob interactive nylo overlay to change what is highlighting in Bonylo",
            position = 2,
            section = lowTickOptions
    )
    default boolean useSpoonTob() {return false;}

    @ConfigItem(
            keyName = "enableBoxRange",
            name = "Enable Rangers",
            description = "",
            position = 3,
            section = lowTickOptions
    )
    default boolean enableBoxRange() {return true;}

    @ConfigItem(
            keyName = "enableBoxRange",
            name = "",
            description = "",
            hidden = true
    )
    void setEnableBoxRange(boolean var1);

    @ConfigItem(
            keyName = "enableBoxMage",
            name = "Enable Mages",
            description = "",
            position = 4,
            section = lowTickOptions
    )
    default boolean enableBoxMage() {return true;}

    @ConfigItem(
            keyName = "enableBoxMage",
            name = "",
            description = "",
            hidden = true
    )
    void setEnableBoxMage(boolean var1);

    @ConfigItem(
            keyName = "enableBoxMelee",
            name = "Enable Melees",
            description = "",
            position = 5,
            section = lowTickOptions
    )
    default boolean enableBoxMelee() {return true;}

    @ConfigItem(
            keyName = "enableBoxMelee",
            name = "",
            description = "",
            hidden = true
    )
    void setEnableBoxMelee(boolean var1);

    @ConfigItem(
            keyName = "enableBoxBigs",
            name = "Enable Bigs",
            description = "Displays box/hull on big nylo too",
            position = 6,
            section = lowTickOptions
    )
    default boolean enableBoxBigs() {return true;}

    @ConfigItem(
            keyName = "enableBox",
            name = "Enable Box",
            description = "Puts box around nylo that are below tick threshold",
            position = 1,
            section = boxOptions
    )
    default boolean enableBox() {return false;}

    @Units("px")
    @Range(max = 500, min = 0)
    @ConfigItem(
            keyName = "boxStrokeWidth",
            name = "Box Stroke Width",
            description = "Thickness of box outline",
            position = 2,
            section = boxOptions
    )
    default int boxStrokeWidth() {return 0;}

    @Units("%")
    @Range(max = 100, min = 0)
    @ConfigItem(
            keyName = "boxFillAlpha",
            name = "Box Fill Opacity",
            description = "0: transparent, 100 = opaque",
            position = 4,
            section = boxOptions
    )
    default int boxFillAlpha() {return 50;}

    @Units("px")
    @Range(max = 500, min = 0)
    @ConfigItem(
            keyName = "boxSize",
            name = "Box Size",
            description = "increase box size - default of 0 will just be a box that hugs text",
            position = 5,
            section = boxOptions
    )
    default int boxSize() {return 0;}

    @ConfigItem(
            keyName = "enableHull",
            name = "Enable Clickbox Outline",
            description = "Puts hull around nylo that are below tick threshold",
            position = 1,
            section = hullOptions
    )
    default boolean enableHull() {return false;}

    @Units("px")
    @ConfigItem(
            keyName = "hullStrokeWidth",
            name = "Hull Stroke Width",
            description = "",
            position = 2,
            section = hullOptions
    )
    default int hullStrokeWidth() {return 1;}

    @Units("%")
    @Range(max = 100, min = 0)
    @ConfigItem(
            keyName = "hullFillAlpha",
            name = "Hull Fill Opacity",
            description = "0: transparent, 100 = opaque",
            position = 4,
            section = hullOptions
    )
    default int hullFillAlpha() {return 50;}

    @ConfigItem(
            keyName = "enablePixelOutline",
            name = "Enable Model Outline",
            description = "",
            position = 5,
            section = hullOptions
    )
    default boolean enablePixelOutline() {return false;}

    @Units("px")
    @ConfigItem(
            keyName = "pixelWidth",
            name = "Enable Pixel Outline",
            description = "",
            position = 6,
            section = hullOptions
    )
    default int pixelWidth() {return 1;}

    @ConfigItem(
            keyName = "enableTile",
            name = "Enable Tile",
            description = "Puts tile around nylo that are below tick threshold",
            position = 1,
            section = tileOptions
    )
    default boolean enableTile() {return false;}

    @Units("px")
    @Range(max = 500, min = 0)
    @ConfigItem(
            keyName = "tileStrokeWidth",
            name = "Tile Stroke Width",
            description = "Thickness of tile outline",
            position = 2,
            section = tileOptions
    )
    default int tileStrokeWidth() {return 0;}

    @Units("%")
    @Range(max = 100, min = 0)
    @ConfigItem(
            keyName = "tileFillAlpha",
            name = "Tile Fill Opacity",
            description = "0: transparent, 100 = opaque",
            position = 4,
            section = tileOptions
    )
    default int tileFillAlpha() {return 50;}

    @ConfigItem(
            keyName = "enableDebug",
            name = "Enable Debug (Fally Guards)",
            description = "Enable to test settings @Fally. If numbers don't appear turn whole plugin off and on. ",
            position = 1,
            section = debugOptions
    )
    default boolean enableDebug() {return false;}

    @ConfigItem(
            keyName = "debugmsgs",
            name = "Debug Messages",
            description = "just for error checking",
            position = 2,
            section = debugOptions
    )
    default boolean debugmsgs() {return false;}

    @ConfigItem(
            keyName = "debugmsgs",
            name = "Debug RH Messages",
            description = "just for error checking will clog your client.log so please turn this off ",
            position = 3,
            section = debugOptions
    )
    default boolean debugRHmsgs() {return false;}
}