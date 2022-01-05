package net.runelite.client.plugins.objecthider;

import net.runelite.client.config.*;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup("objecthider")
public interface ObjectHiderConfig extends Config
{
    @ConfigSection(
            name = "IDs to hide",
            description = "The list of Ground Object IDs to hide",
            position = 0,
            closedByDefault = true
    )
    String listSection = "listSection";

    @ConfigItem(
            keyName = "toHide",
            name = "",
            description = "List of Ground Objects IDs to hide",
            position = 0,
            section = "listSection"
    )
    default String getGroundObjectsToHide() {
        return "";
    }

    @ConfigItem(
            keyName = "toHide",
            name = "",
            description = ""
    )
    void setGroundObjectsToHide(String groundObjectsToHide);

    @ConfigItem(
            keyName = "hotkey",
            name = "Hotkey",
            description = "When you hold this key and right-click a tile, you'll hide the Ground Object on it",
            position = 1
    )
    default Keybind hideGroundObjectKey() {
        return new Keybind(KeyEvent.VK_UNDEFINED, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
    }

    @ConfigItem(
            keyName = "hideAll",
            name = "Hide All",
            description = "Whether to hide all Ground Objects regardless of the list",
            position = 2
    )
    default boolean getHideAll() {
        return false;
    }
}
