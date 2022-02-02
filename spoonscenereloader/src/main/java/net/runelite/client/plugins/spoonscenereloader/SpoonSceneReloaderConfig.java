package net.runelite.client.plugins.spoonscenereloader;

import net.runelite.client.config.*;

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

    @ConfigItem(
            keyName = "showPanel",
            name = "Show Panel",
            description = "Shows the panel with the button to reload the scene",
            position = 3
    )
    default panelMode showPanel() { return panelMode.LOGGED_IN; }

    @Range(min = 0, max = 10)
    @ConfigItem(
            keyName = "panelPriority",
            name = "Panel Priority",
            description = "Only reload the scene in CoX",
            position = 4
    )
    default int panelPriority() { return 6; }

    enum panelMode {
        OFF, RAIDS, LOGGED_IN
    }
}
