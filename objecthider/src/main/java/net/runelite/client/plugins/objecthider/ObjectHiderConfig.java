package net.runelite.client.plugins.objecthider;

import net.runelite.client.config.*;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup("objecthider")
public interface ObjectHiderConfig extends Config
{
    @ConfigSection(
            name = "IDs to hide",
            description = "The list of Object IDs to hide",
            position = 0,
            closedByDefault = true
    )
    String listSection = "listSection";

    @ConfigItem(
            keyName = "objectList",
            name = "Game Object List",
            description = "List of game objects to hide, seperated by a comma.",
            position = 0,
            section = listSection
    )
    default String objectList() {return "";}

    @ConfigItem(
            keyName = "objectList",
            name = "",
            description = ""
    )
    void setGameObjectsToHide(String objectList);

    @ConfigItem(
            keyName = "toHide",
            name = "Ground Object List",
            description = "List of Ground Objects IDs to hide",
            position = 1,
            section = listSection
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

    //No section
    @ConfigItem(
            keyName = "gameObjectHotkey",
            name = "Game Object Hotkey",
            description = "When you hold this key and right-click a tile, you'll hide the Game Object on it",
            position = 1
    )
    default Keybind hideGameObjectKey() {
        return new Keybind(KeyEvent.VK_UNDEFINED, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
    }

    @ConfigItem(
            keyName = "hotkey",
            name = "Ground Object Hotkey",
            description = "When you hold this key and right-click a tile, you'll hide the Ground Object on it",
            position = 1
    )
    default Keybind hideGroundObjectKey() {
        return new Keybind(KeyEvent.VK_UNDEFINED, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
    }

    @ConfigItem(
            keyName = "hideAll",
            name = "Hide All - Ground Objects",
            description = "Whether to hide all Ground Objects regardless of the list",
            position = 2
    )
    default boolean getHideAll() {
        return false;
    }
}
