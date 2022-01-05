package net.runelite.client.plugins.spoontempoross;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("tempoross")
public interface SpoonTemporossConfig extends Config {
    @ConfigSection(
            name = "Fishing",
            description = "Fishing plugins",
            position = 1,
            closedByDefault = true
    )
    String fishingSection = "fishing";

    @ConfigSection(
            name = "Island",
            description = "Totem and Shrine plugins",
            position = 2,
            closedByDefault = true
    )
    String islandSection = "island";

    @ConfigSection(
            name = "Fire",
            description = "Fire and cloud plugins",
            position = 3,
            closedByDefault = true
    )
    String fireSection = "fire";

    //Fishing
    @ConfigItem(keyName = "highlightFish", name = "Highlight Fish", description = "Highlights the fishing spots", position = 0, section = fishingSection)
    default boolean highlightFish() {
        return true;
    }

    @ConfigItem(keyName = "fishColor", name = "Fish Color", description = "Color of the fishing spots highlight", position = 1, section = fishingSection)
    default Color fishColor() {
        return Color.GREEN;
    }

    @ConfigItem(keyName = "jumpingFish", name = "Highlight Jumping Fish", description = "Highlights the jumping fish spots and adds a hint arrow", position = 2, section = fishingSection)
    default boolean jumpingFish() {
        return true;
    }

    @ConfigItem(keyName = "jumpingFishColor", name = "Jumping Fish Color", description = "Color of the jumping fish highlight", position = 3, section = fishingSection)
    default Color jumpingFishColor() {
        return Color.CYAN;
    }

    @ConfigItem(keyName = "highlightAmmoCrate", name = "Highlight Ammo Crate", description = "Highlights the ammo crate when you have cooked or raw fish", position = 4, section = fishingSection)
    default boolean highlightAmmoCrate() { return true;}

    @ConfigItem(keyName = "ammoCrateColor", name = "Ammo Crate Color", description = "Color of the ammo crate highlight", position = 5, section = fishingSection)
    default Color ammoCrateColor() {
        return Color.CYAN;
    }

    @ConfigItem(keyName = "highlightPool", name = "Highlight Shining Pool", description = "Highlights the pools when Tempoross is vulnerable", position = 6, section = fishingSection)
    default boolean highlightPool() { return true;}

    @ConfigItem(keyName = "highlightPoolColor", name = "Shining Pool Color", description = "Color of the pool highlight", position = 7, section = fishingSection)
    default Color poolColor() {
        return Color.GREEN;
    }

    @ConfigItem(keyName = "vulnTicks", name = "Vulnerable Ticks", description = "Displays ticks until Tempoross pools are active", position = 8, section = fishingSection)
    default boolean vulnTicks() { return true;}

    @ConfigItem(keyName = "vulnColor", name = "Vulnerable Ticks Color", description = "Color of the vulnerable ticks display", position = 9, section = fishingSection)
    default Color vulnColor() {
        return Color.WHITE;
    }

    @ConfigItem(keyName = "displayFishCount", name = "Display Fish Count", description = "Shows the number of fish in your inventory", position = 10, section = fishingSection)
    default boolean displayFishCount() { return false;}

    //Island
    @ConfigItem(keyName = "highlightTotem", name = "Highlight Totem", description = "Highlights the totem pole when a wave is coming", position = 0, section = islandSection)
    default boolean highlightTotem() { return true;}

    @ConfigItem(keyName = "highlightTotemColor", name = "Totem Color", description = "Color of the totem pole highlight", position = 1, section = islandSection)
    default Color totemColor() {
        return Color.GREEN;
    }

    @ConfigItem(keyName = "highlightShrine", name = "Highlight Shrine", description = "Highlights the shrine when you have raw fish", position = 2, section = islandSection)
    default boolean highlightShrine() { return true;}

    @ConfigItem(keyName = "highlightShrineColor", name = "Shrine Color", description = "Color of the shrine highlight", position = 3, section = islandSection)
    default Color shrineColor() {
        return Color.GREEN;
    }

    @ConfigItem(keyName = "highlightRepair", name = "Repair Totem", description = "Highlights the broken masts and totems", position = 4, section = islandSection)
    default boolean highlightRepair() { return true;}

    @ConfigItem(keyName = "highlightRepairColor", name = "Repair Totem Color", description = "Color of the repair highlight", position = 5, section = islandSection)
    default Color repairColor() {
        return Color.RED;
    }

    //Fire
    @ConfigItem(keyName = "highlightFire", name = "Highlight Fire", description = "Highlights the fires", position = 0, section = fireSection)
    default boolean highlightFire() { return true;}

    @ConfigItem(keyName = "fireColor", name = "Fire Color", description = "Color of the fire highlight", position = 1, section = fireSection)
    default Color fireColor() {
        return Color.RED;
    }

    @ConfigItem(keyName = "highlightCloud", name = "Highlight Cloud", description = "Highlights the clouds", position = 2, section = fireSection)
    default boolean highlightCloud() { return true;}

    @ConfigItem(keyName = "cloudColor", name = "Cloud Color", description = "Color of the cloud highlight", position = 3, section = fireSection)
    default Color cloudColor() {return Color.RED; }

    @ConfigItem(keyName = "fireTicks", name = "Fire Ticks", description = "Displays ticks before fire is created", position = 4, section = fireSection)
    default boolean fireTicks() { return true;}

    @ConfigItem(keyName = "fireTicksColor", name = "Fire Ticks Color", description = "Color of the fire ticks display", position = 5, section = fireSection)
    default Color fireTicksColor() {
        return Color.WHITE;
    }

    @Range(min = 1, max = 10)
    @ConfigItem(keyName = "tileThiCC", name = "Tile Width", description = "Changes the tile width for cloud aoe, fishing spots, shining pool, and ammo crate", position = 99)
    default int tileThiCC() {
        return 2;
    }
}
