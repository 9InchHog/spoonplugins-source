package net.runelite.client.plugins.spoonscenereloader;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("scenereloader")
public interface SpoonSceneReloaderConfig extends Config {
    @ConfigItem(
            keyName = "hotkey",
            name = "Hotkey",
            description = "Hotkey to reload the scene",
            position = 1
    )
    default Keybind hotkey() {
        return new Keybind(82,128);
    }

    @ConfigItem(
            keyName = "raidsOnly",
            name = "Only reload in CoX",
            description = "Only reload the scene in CoX",
            position = 2
    )
    default boolean raidsOnly() { return false; }
}
