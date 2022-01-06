package net.runelite.client.plugins.nex;

import lombok.Getter;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("NexConfig")
public interface NexConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "getFontSize",
            name = "Font Size",
            description = "Changes the size of the font displayed in this plugin."
    )
    default int getFontSize() {
        return 12;
    }

    @ConfigItem(
            position = 1,
            keyName = "showTargetableEntity",
            name = "Show Targetable",
            description = "Shows which NPC is currently targetable."
    )
    default boolean showTargetableEntity() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "showInvulnerability",
            name = "Show Invulnerability",
            description = "Shows the ticks left until Nex can take damage."
    )
    default boolean showInvulnerability() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "showPlayersWithVirus",
            name = "Show Sick Players",
            description = "Shows which nearby players are sick."
    )
    default boolean showPlayersWithVirus() {
        return true;
    }

    public enum VirusIndicator {
        DISABLED(false, false),
        TILE(true, false),
        HULL(false, true),
        TILE_AND_HULL(true, true);

        VirusIndicator(boolean tileVisible, boolean hullVisible) {
            this.tileVisible = tileVisible;
            this.hullVisible = hullVisible;
        }

        @Getter
        private boolean tileVisible;

        @Getter
        private boolean hullVisible;
    }

    @ConfigItem(
            position = 4,
            keyName = "getVirusIndicator",
            name = "Sickness Indicator", description = "Determines the indicator to use on sick players.")
    default VirusIndicator getVirusIndicator() {
        return VirusIndicator.TILE;
    }

    @ConfigItem(
            position = 5,
            keyName = "highlightShadows",
            name = "Highlight Shadows",
            description = "Highlight shadows underneath your player."
    )
    default boolean highlightShadows() {
        return true;
    }

    @ConfigItem(
            position = 6,
            keyName = "showBloodSacrificeTimer",
            name = "Show Blood Sacrifice Timer",
            description = "Show the amount of time remaining until Nex's blood sacrifice commences."
    )
    default boolean showBloodSacrificeTimer() {
        return true;
    }

    @ConfigItem(
            position = 7,
            keyName = "showBloodSacrificeRange",
            name = "Show Blood Sacrifice Range",
            description = "Show the effective danger range of Nex's blood sacrifice ability."
    )
    default boolean showBloodSacrificeRange() {
        return true;
    }

    @ConfigItem(
            position = 8,
            keyName = "showIceShardRange",
            name = "Show Ice Shard Range",
            description = "Show the effective danger range of Nex's ice shard attack."
    )
    default boolean showIceShardRange() {
        return true;
    }

    @ConfigItem(
            position = 9,
            keyName = "showWrathRange",
            name = "Show Wrath Range",
            description = "Shows the effective danger range of Nex's Wrath range."
    )
    default boolean showWrathRange() {
        return true;
    }
}
