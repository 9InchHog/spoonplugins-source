package net.runelite.client.plugins.cclumphelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("cClumpHelper")
public interface cClumpHelperConfig extends Config {
    @ConfigItem(keyName = "useKeyBind", name = "Hide by keybind", description = "Hides nylos when key is held down", position = 0, disabledBy = "useTime")
    default boolean useKeyBind() {
        return true;
    }

    @ConfigItem(keyName = "hotkey", name = "Toggle Key", description = "When you press this key it will toggle hiding nylos", position = 1)
    default Keybind hotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName = "useTime", name = "Hide by time", description = "Hides 70s X ticks after 50s spawn", position = 2, disabledBy = "useKeyBind")
    default boolean useTime() {
        return false;
    }

    @ConfigItem(keyName = "hideTime", name = "Time to hide", description = "Number of ticks to hide after 50s spawn", position = 3)
    default int tickAmount() {
        return 8;
    }
}
