package net.runelite.client.plugins.keydrag;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("keydrag")
public interface KeyDragConfig extends Config {
    @ConfigItem(keyName = "dragDelay", name = "Drag Delay", description = "Configures the inventory drag delay in client ticks (20ms)", position = 1)
    default int dragDelay() {
        return 30;
    }

    @ConfigItem(keyName = "hotkey", name = "On/Off Toggle", description = "Toggles anti drag on or off", position = 2)
    default Keybind hotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "disableOnCtrl",
            name = "Disable On Control Pressed",
            description = "Configures whether to ignore the delay while holding control.",
            position = 3
    )
    default boolean disableOnCtrl() {
        return false;
    }

    @ConfigItem(
            keyName = "displayOverlay",
            name = "Display On/Off Overlay",
            description = "Configures whether to show an overlay for if anti drag is on or off.",
            position = 4
    )
    default boolean displayOverlay() {
        return false;
    }
}
