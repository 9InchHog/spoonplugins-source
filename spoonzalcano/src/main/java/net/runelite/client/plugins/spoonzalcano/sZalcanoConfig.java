package net.runelite.client.plugins.spoonzalcano;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("zalcano")
public interface sZalcanoConfig extends Config {
    @ConfigItem(keyName = "Golem", name = "Golem Spawns", description = "Will notify you of Golem spawns and tag the tile its spawning on as well as the Golem NPC.", position = 0)
    default boolean golem() {
        return false;
    }

    @ConfigItem(keyName = "GolemColour", name = "Golem Colour", description = "Colour of golem tile/tag", position = 1)
    default Color golemColor() {
        return Color.GREEN;
    }

    @ConfigItem(keyName = "glowingRock", name = "Glowing Rock", description = "Highlight Glowing Rock and notify you when it's exploding.", position = 2)
    default boolean glowingRock() {
        return false;
    }

    @ConfigItem(keyName = "glowingRockColour", name = "Glowing Rock Colour", description = "Colour of the Glowing Rock outline.", position = 3)
    default Color glowingRockColour() {
        return Color.GREEN;
    }

    @ConfigItem(keyName = "explodingRockColour", name = "Exploding Rock Colour", description = "Colour of exploding rock.", position = 4)
    default Color glowingRockExplosionColour() {
        return Color.RED;
    }

    @ConfigItem(keyName = "beybladeTimer", name = "Blue Pool Timer", description = "Countdown timer for maximum length a blue pool can stay active for. Useful for small teams only.", position = 5)
    default boolean beybladeTimer() {
        return false;
    }

    @ConfigItem(keyName = "dangerousTiles", name = "Danger Tiles", description = "Highlights the exact squares to avoid during orange/blue pool phase. Makees finding a path easy.", position = 6)
    default boolean dangerousTiles() {
        return false;
    }

    @ConfigItem(keyName = "dangerousTilesColor", name = "Danger Tiles Color", description = "Color.", position = 7)
    default Color dangerousTileColor() {
        return Color.RED;
    }

    @ConfigItem(keyName = "fallingRocks", name = "Falling Rocks", description = "Highlights the squares falling rocks will land on.", position = 8)
    default boolean fallingRocks() {
        return false;
    }

    @ConfigItem(keyName = "fallingRocksColor", name = "Falling Rocks Color", description = "Color.", position = 9)
    default Color fallingRocksColor() {
        return Color.RED;
    }
}
