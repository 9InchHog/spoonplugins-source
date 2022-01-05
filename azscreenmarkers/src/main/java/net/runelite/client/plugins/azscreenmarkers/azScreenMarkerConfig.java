package net.runelite.client.plugins.azscreenmarkers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("azScreenmarks")
public interface azScreenMarkerConfig extends Config {
    @ConfigItem(keyName = "key1", name = "Cox chest ids", description = "", position = 1)
    default String chestIds() {
        return "29769, 29770, 29779";
    }

    @ConfigItem(keyName = "key2", name = "Cox chest distance", description = "Name the marker 'cox bank'", position = 2)
    default int chestDist() {
        return 7;
    }

    @ConfigItem(keyName = "key23", name = "^ Name the marker 'cox bank'", description = "", position = 3)
    default Object idt() {
        return null;
    }

    @ConfigItem(keyName = "boardIds", name = "Cox board ids", description = "", position = 4)
    default String boardIds() {
        return "29776";
    }

    @ConfigItem(keyName = "boardDist", name = "Cox board distance", description = "Name the marker 'cox board'", position = 5)
    default int boardDist() {
        return 7;
    }

    @ConfigItem(keyName = "idtBoard", name = "^ Name the marker 'cox board'", description = "", position = 6)
    default Object idtBoard() {
        return null;
    }
}
