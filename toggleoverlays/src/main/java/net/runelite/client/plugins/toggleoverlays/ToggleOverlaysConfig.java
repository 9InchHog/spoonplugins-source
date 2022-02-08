package net.runelite.client.plugins.toggleoverlays;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("sToggleOverlays")
public interface ToggleOverlaysConfig extends Config {
    @ConfigItem(
            keyName = "hotKey",
            name = "Overlay Toggle",
            description = "Pressing this key will toggle overlays",
            position = 0
    )
    default Keybind hotKey() {
        return Keybind.NOT_SET;
    }
}
