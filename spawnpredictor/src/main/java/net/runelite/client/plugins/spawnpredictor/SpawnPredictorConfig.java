package net.runelite.client.plugins.spawnpredictor;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("spawnpredictor")
public interface SpawnPredictorConfig extends Config {
    @ConfigItem(name = "Debug", keyName = "debugOverlay", description = "", position = 0)
    default boolean debug() {
        return false;
    }

    @ConfigItem(name = "Lobby Rotation Info", keyName = "lobbyRotationInfoOverlay", description = "Displays information about the current/upcoming rotation in the lobby area", position = 1)
    default boolean lobbyRotationInfoOverlay() {
        return false;
    }

    @ConfigItem(name = "Include UTC Time", keyName = "includeUTCTime", description = "", position = 2)
    default boolean includeUTCTime() {
        return false;
    }

    @ConfigItem(name = "Display Mode", keyName = "displayMode", description = "", position = 3)
    default DisplayMode displayMode() {
        return DisplayMode.OFF;
    }

    @ConfigItem(name = "Overlay Stroke Size", keyName = "overlayStrokeSize", description = "", position = 4)
    @Range(max = 3, min = 1)
    @Units("px")
    default int overlayStrokeSize() {
        return 2;
    }

    @ConfigItem(name = "Multicolor Names", keyName = "multicolorNames", description = "Color the overlay names inside the Fight Caves to it's respective color.<br>Example: Current Wave Color=White -> Name=White, Next Wave Color=Green -> Name=Green", position = 5)
    default boolean multicolorNames() {
        return false;
    }

    @ConfigItem(name = "Current Wave Color", keyName = "currentWaveColor", description = "", position = 6)
    @Alpha
    default Color currentWaveColor() {
        return Color.WHITE;
    }

    @ConfigItem(name = "Next Wave Color", keyName = "nextWaveColor", description = "", position = 7)
    @Alpha
    default Color nextWaveColor() {
        return Color.GREEN;
    }

    public enum DisplayMode {
        OFF("Off"),
        CURRENT_WAVE("Current Wave"),
        NEXT_WAVE("Next Wave"),
        BOTH("Both? Obviously..Dumbass");

        private final String name;

        DisplayMode(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }
    }
}
