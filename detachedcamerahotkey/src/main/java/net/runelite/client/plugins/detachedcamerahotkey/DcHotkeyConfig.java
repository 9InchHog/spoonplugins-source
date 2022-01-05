package net.runelite.client.plugins.detachedcamerahotkey;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup("detachedcamerahotkey")
public interface DcHotkeyConfig extends Config {
    @ConfigItem(
            name = "Enable/Disable Hotkey",
            keyName = "dcHotkey",
            description = "Set a hotkey to enable/disable the detached camera",
            position = 0
    )
    default Keybind getDCHotkey() {
        return new Keybind(112, 128);
    }

    @ConfigItem(
            name = "Camera Speed",
            keyName = "dcSpeed",
            description = "Dynamically change the detached camera speed",
            position = 1
    )
    @Range(min = 1)
    default int getDCSpeed() {
        return 12;
    }
}
