package net.runelite.client.plugins.spoongauntlet;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("SpoonGauntlet")
public interface SpoonGauntletConfig extends Config {
    @ConfigSection(
            name = "Boss",
            description = "Boss settings",
            position = 0,
            closedByDefault = true
    )
    String boss = "boss";

    @ConfigSection(
            name = "Prep",
            description = "Prep settings",
            position = 1,
            closedByDefault = true
    )
    String prep = "prep";

    @ConfigSection(
            name = "Regular",
            description = "Resources for regular gauntlet",
            position = 2,
            closedByDefault = true
    )
    String reg = "reg";

    @ConfigSection(
            name = "Corrupted",
            description = "Resources for corrupted gauntlet",
            position = 3,
            closedByDefault = true
    )
    String corrupt = "corrupt";

    //------------------------------------------------------------//
    // Boss
    //------------------------------------------------------------//
    @ConfigItem(
            position = 0,
            keyName = "countBossAttacks",
            name = "Boss Attacks",
            description = "Count the attacks until the boss switches their style.",
            section = boss
    )
    default boolean countBossAttacks() {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "countBossAttacksImage",
            name = "Prayer Infobox",
            description = "Represents the boss attacks as an image.",
            section = boss
    )
    default boolean showBossAttackOverlay() {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "countBossAttacksPrayer",
            name = "Prayer Tab",
            description = "Shows what to pray in the prayer widget.",
            section = boss
    )
    default gauntletPrayerTab countBossAttacksPrayer() {
        return gauntletPrayerTab.OFF;
    }

    @ConfigItem(
            position = 2,
            keyName = "countPlayerAttacks",
            name = "Player Attacks",
            description = "Count the player attacks until the boss switches their prayer.",
            section = boss
    )
    default boolean countPlayerAttacks() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "attackTextStyle",
            name = "Attack Text Style",
            description = "Changes the way the attack counters are displayed on Hunllef",
            section = boss
    )
    default attackTextStyleMode attackTextStyle() {
        return attackTextStyleMode.STEROIDS;
    }

    @ConfigItem(
            position = 4,
            keyName = "attackCounterType",
            name = "Attack Counter Style",
            description = "Changes the player/boss attack counter to display just the numbers or with text.",
            section = boss
    )
    default attackCounterTypeMode attackCounterType() {
        return attackCounterTypeMode.SIMPLE;
    }

    @ConfigItem(
            position = 5,
            keyName = "textColor",
            name = "Attack Style Color",
            description = "If you need an explanation... I don't know what to tell you.",
            section = boss
    )
    default boolean textColor() {
        return true;
    }

    @ConfigItem(
            position = 6,
            keyName = "uniquePrayerAudio",
            name = "Prayer Deactivated Audio",
            description = "Plays a unique sound whenever the boss is about to shut down your prayer.",
            section = boss
    )
    default boolean uniquePrayerAudio() {
        return true;
    }

    @ConfigItem(
            position = 7,
            keyName = "overlayBoss",
            name = "Highlight Boss",
            description = "Overlay the boss with an color denoting it's current attack style.",
            section = boss
    )
    default bossOverlayMode overlayBoss() {
        return bossOverlayMode.OFF;
    }

    @ConfigItem(
            position = 8,
            keyName = "overlayBossPrayer",
            name = "Boss Style Icon",
            description = "Overlay the boss with an icon denoting it's current attack style.",
            section = boss
    )
    default boolean overlayBossPrayer() {
        return false;
    }

    @ConfigItem(
            position = 9,
            keyName = "overlayTornadoes",
            name = "Show Tornadoes",
            description = "Display the amount of ticks left until the tornadoes decay.",
            section = boss
    )
    default tornadoMode overlayTornadoes() {
        return tornadoMode.BOTH;
    }

    @ConfigItem(
            position = 10,
            keyName = "tornadoColor",
            name = "Tornado Color",
            description = "Shows the tornadoes true location.",
            section = boss
    )
    default Color tornadoColor() {
        return Color.WHITE;
    }

    @ConfigItem(
            keyName = "wrongStyleOutline",
            name = "Wrong Style Outline",
            description = "Outlines the boss with the correct style when you are not praying correctly",
            position = 11,
            section = boss
    )
    default boolean wrongStyleOutline() { return false; }

    @Range(min = 1, max = 50)
    @ConfigItem(
            keyName = "wrongStyleOutlineThiCC",
            name = "Outline Width",
            description = "Adjusts the width of wrong style outline",
            position = 12,
            section = boss/*,
            hidden = true,
            unhide = "wrongStyleOutline"*/
    )
    default int wrongStyleOutlineThiCC() { return 2; }

    @ConfigItem(
            keyName = "gauntletPrayerNotifier",
            name = "Prayer Notifier",
            description = "Plays a sound effect when to switch prayers at Hunllef",
            position = 13,
            section = boss
    )
    default gauntletPrayerNotifierMode gauntletPrayerNotifier() { return gauntletPrayerNotifierMode.OFF; }

    @Range(min = 1, max = 100)
    @ConfigItem(
            keyName = "gauntletPrayerNotifierVolume",
            name = "Prayer Volume",
            description = "Adjusts the volume of the prayer notifier",
            position = 14,
            section = boss
    )
    default int gauntletPrayerNotifierVolume() { return 30; }

    @ConfigItem(
            keyName = "stompNotifier",
            name = "Stomp Notifier",
            description = "Tf you doin under there boi",
            position = 15,
            section = boss
    )
    default boolean stompNotifier() { return false; }

    @Range(min = 1, max = 100)
    @ConfigItem(
            keyName = "gauntletStompNotifierVolume",
            name = "Stomp Volume",
            description = "Adjusts the volume of the stomp notifier",
            position = 16,
            section = boss/*,
            hidden = true,
            unhide = "stompNotifier"*/
    )
    default int gauntletStompNotifierVolume() { return 30; }

    @ConfigItem(
            keyName = "attackCounterUp",
            name = "Attack Count Up",
            description = "Makes boss and player attacks count up instead of down",
            position = 17,
            section = boss
    )
    default boolean attackCounterUp() { return false; }

    //------------------------------------------------------------//
    // Prep
    //------------------------------------------------------------//
    @ConfigItem(
            position = 0,
            keyName = "highlightResourcesColor",
            name = "Highlight Resources",
            description = "Highlights all the resources in each room.",
            section = prep
    )
    default resourceMode resourceMode() {return resourceMode.OUTLINE;}

    @ConfigItem(
            position = 1,
            keyName = "highlightResourcesIcons",
            name = "Resource Icons",
            description = "Highlights all the resources in each room with an icon.",
            section = prep
    )
    default boolean highlightResourcesIcons() {return true;}

    @ConfigItem(
            position = 2,
            keyName = "lowFps",
            name = "Tick If Your Fps Sucks",
            description = ":feelsjinimyman:",
            section = prep
    )
    default boolean lowFps() {return false;}

    @ConfigItem(
            position = 3,
            keyName = "resourceTracker",
            name = "Resource Tracker",
            description = "",
            section = prep
    )
    default boolean resourceTracker() {return false;}

    @ConfigItem(
            position = 4,
            keyName = "verticalResourceOverlay",
            name = "Vertical Resource Overlay",
            description = "",
            section = prep
    )
    default boolean verticalResourceOverlay() {return true;}

    @Alpha
    @ConfigItem(
            position = 5,
            keyName = "treeResourceColor",
            name = "Tree resource color",
            description = "",
            section = prep
    )
    default Color treeResourceColor() {return new Color(184, 134, 11, 255);}

    @Alpha
    @ConfigItem(
            position = 6,
            keyName = "rockResourceColor",
            name = "Rock resource color",
            description = "",
            section = prep
    )
    default Color rockResourceColor() {return new Color(178, 34, 34, 255);}

    @Alpha
    @ConfigItem(
            position = 7,
            keyName = "plantResourceColor",
            name = "Plant resource color",
            description = "",
            section = prep
    )
    default Color plantResourceColor() {return new Color(50, 205, 50, 255);}

    @Alpha
    @ConfigItem(
            position = 8,
            keyName = "linumResourceColor",
            name = "Linum resource color",
            description = "",
            section = prep
    )
    default Color linumResourceColor() {return new Color(255, 255, 255, 255);}

    @Alpha
    @ConfigItem(
            position = 9,
            keyName = "fishResourceColor",
            name = "Fish resource color",
            description = "",
            section = prep
    )
    default Color fishResourceColor() {return new Color(10, 206, 209, 255);}

    @ConfigItem(
            name = "Outline starting room utilities",
            description = "Outline various utilities in the starting room.",
            position = 10,
            keyName = "utilitiesOutline",
            section = prep
    )
    default boolean utilitiesOutline() {return false;}

    @Range(min = 2, max = 12)
    @ConfigItem(
            name = "Outline width",
            description = "Change the width of the utilities outline.",
            position = 11,
            keyName = "utilitiesOutlineWidth",
            section = prep,
            hidden = true,
            unhide = "utilitiesOutline"
    )
    @Units(Units.POINTS)
    default int utilitiesOutlineWidth() {return 4;}

    @Alpha
    @ConfigItem(
            name = "Outline color",
            description = "Change the color of the utilities outline.",
            position = 12,
            keyName = "utilitiesOutlineColor",
            section = prep,
            hidden = true,
            unhide = "utilitiesOutline"
    )
    default Color utilitiesOutlineColor() {return Color.MAGENTA;}

    @ConfigItem(
            name = "Hide Enter Boss",
            description = "Removes left click option to enter Hunllef if you have raw fish in your inventory",
            position = 13,
            keyName = "hideEnterBoss",
            section = prep
    )
    default boolean hideEnterBoss() {return false;}

    //------------------------------------------------------------//
    // Regular
    //------------------------------------------------------------//
    @ConfigItem(
            position = 0,
            keyName = "teleportCrystalCount",
            name = "Amount of Teleport crystals",
            description = "Amount of teleport crystals you expect to need",
            section = reg
    )
    default int teleportCrystalCount() {return 0;}

    @ConfigItem(
            position = 1,
            keyName = "potionCount",
            name = "Amount of potions",
            description = "Amount of potions you expect to need",
            section = reg
    )
    default int potionCount() {return 1;}

    @ConfigItem(
            position = 2,
            keyName = "fishCount",
            name = "Amount of fish",
            description = "Amount of fish you expect to need",
            section = reg
    )
    default int fishCount() {return 4;}

    @ConfigItem(
            position = 3,
            keyName = "armorTier",
            name = "Tier of armor you want",
            description = "",
            section = reg
    )
    default GauntletTier armorTier() {return GauntletTier.NONE;}

    @ConfigItem(
            position = 6,
            keyName = "halberdTier",
            name = "halberd you want",
            description = "",
            section = reg
    )
    default GauntletTier halberdTier() {return GauntletTier.NONE;}

    @ConfigItem(
            position = 7,
            keyName = "bowTier",
            name = "bow you want",
            description = "",
            section = reg
    )
    default GauntletTier bowTier() {return GauntletTier.TIER3;}

    @ConfigItem(
            position = 8,
            keyName = "staffTier",
            name = "staff you want",
            description = "",
            section = reg
    )
    default GauntletTier staffTier() {return GauntletTier.TIER3;}

    @ConfigItem(
            position = 9,
            keyName = "extraShards",
            name = "extra shards you want",
            description = "",
            section = reg
    )
    default int extraShards()
    {
        return 0;
    }

    //------------------------------------------------------------//
    // Corrupted
    //------------------------------------------------------------//
    @ConfigItem(
            position = 0,
            keyName = "corruptedTeleportCrystalCount",
            name = "(C) Amount of Teleport crystals",
            description = "Amount of teleport crystals you expect to need",
            section = corrupt
    )
    default int corruptedTeleportCrystalCount() {return 0;}

    @ConfigItem(
            position = 1,
            keyName = "corruptedPotionCount",
            name = "(C) Amount of potions",
            description = "Amount of potions you expect to need",
            section = corrupt
    )
    default int corruptedPotionCount() {return 2;}

    @ConfigItem(
            position = 2,
            keyName = "corruptedFishCount",
            name = "(C) Amount of fish",
            description = "Amount of fish you expect to need",
            section = corrupt
    )
    default int corruptedFishCount() {return 8;}

    @ConfigItem(
            position = 3,
            keyName = "corruptedArmorTier",
            name = "(C) Tier of armor you want",
            description = "",
            section = corrupt
    )
    default GauntletTier corruptedArmorTier() {return GauntletTier.TIER1;}

    @ConfigItem(
            position = 6,
            keyName = "corruptedHalberdTier",
            name = "(C) halberd you want",
            description = "",
            section = corrupt
    )
    default GauntletTier corruptedHalberdTier() {return GauntletTier.NONE;}

    @ConfigItem(
            position = 7,
            keyName = "corruptedBowTier",
            name = "(C) bow you want",
            description = "",
            section = corrupt
    )
    default GauntletTier corruptedBowTier() {return GauntletTier.TIER3;}

    @ConfigItem(
            position = 8,
            keyName = "corruptedStaffTier",
            name = "(C) staff you want",
            description = "",
            section = corrupt
    )
    default GauntletTier corruptedStaffTier() {return GauntletTier.TIER3;}

    @ConfigItem(
            position = 9,
            keyName = "corruptedExtraShards",
            name = "(C) extra shards you want",
            description = "",
            section = corrupt
    )
    default int corruptedExtraShards() {return 0;}

    //------------------------------------------------------------//
    // Other
    //------------------------------------------------------------//
    @ConfigItem(
            position = 86,
            keyName = "showTrueTile",
            name = "Show True Tile",
            description = "Displays your true tile"
    )
    default showTrueTileMode showTrueTile() {
        return showTrueTileMode.OFF;
    }

    @Range(min = 0, max = 10)
    @ConfigItem(
            position = 87,
            keyName = "showTrueTileWidth",
            name = "True Tile Width",
            description = "Sets the width of Show True Tile"
    )
    default double showTrueTileWidth() {return 2;}

    @Range(min = 0, max = 255)
    @ConfigItem(
            position = 88,
            keyName = "showTrueTileOpacity",
            name = "True Tile Opacity",
            description = "Sets the fill opacity of Show True Tile"
    )
    default int showTrueTileOpacity() {return 20;}

    @ConfigItem(
            position = 89,
            keyName = "showTrueTileColor",
            name = "True Tile Color",
            description = "Sets color of Show True Tile"
    )
    default Color showTrueTileColor() {
        return Color.CYAN;
    }

    @ConfigItem(
            position = 90,
            keyName = "displayTimerWidget",
            name = "Timer Infobox",
            description = "Display a timer widget that tracks your gauntlet progress."
    )
    default boolean displayTimerWidget() {
        return true;
    }

    @ConfigItem(
            position = 91,
            keyName = "displayTimerChat",
            name = "Chat Timer",
            description = "Display a chat message that tracks your gauntlet progress."
    )
    default boolean displayTimerChat() {
        return true;
    }

    @Range(min = 1, max = 50)
    @ConfigItem(
            position = 92,
            keyName = "iconSize",
            name = "Global Icon Size",
            description = "Globally change the size of icons. { Range: 1 to 50 }"
    )
    default int iconSize() {return 20;}

    @ConfigItem(
            position = 98,
            keyName = "fontStyle",
            name = "Font Style",
            description = ""
    )
    default GauntletFont fontStyle()
    {
        return GauntletFont.CUSTOM;
    }

    @ConfigItem(
            position = 99,
            keyName = "fontSize",
            name = "Font Size",
            description = ""
    )
    default int fontSize() { return 18; }

    enum GauntletTier
    {
        NONE("None"),
        TIER1("Tier 1"),
        TIER2("Tier 2"),
        TIER3("Tier 3");

        public String name;

        GauntletTier(String name)
        {
            this.name = name;
        }
    }

    enum GauntletFont
    {
        SMALL("Small"),
        REGULAR("Regular"),
        BOLD("Bold"),
        CUSTOM("Custom");

        public String name;

        GauntletFont(String name)
        {
            this.name = name;
        }
    }

    enum bossOverlayMode {
        OFF, TRUE_LOCATION, OUTLINE, HULL, TILE
    }

    enum attackTextStyleMode {
        STEROIDS, BLUELITE
    }

    enum attackCounterTypeMode {
        SIMPLE, DETAILED
    }

    enum resourceMode {
        OFF, OUTLINE, TILE
    }

    enum tornadoMode {
        OFF, TICKS, TILE, BOTH
    }

    enum gauntletPrayerNotifierMode {
        OFF, DING, LOUD
    }

    enum gauntletPrayerTab
    {
        OFF("Off"),
        BOX("Box"),
        TICKS_AND_BOX("Ticks + Box");

        public String name;

        gauntletPrayerTab(String name)
        {
            this.name = name;
        }
    }

    enum showTrueTileMode {
        OFF, BOSS, BOTH
    }
}

