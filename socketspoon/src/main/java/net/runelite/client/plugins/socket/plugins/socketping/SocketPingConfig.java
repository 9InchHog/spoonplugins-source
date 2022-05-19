package net.runelite.client.plugins.socket.plugins.socketping;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ModifierlessKeybind;

@ConfigGroup("socketping")
public interface SocketPingConfig extends Config {
    public enum HotkeyTrigger {
        HOLD, TOGGLE;
    }

    @ConfigItem(
            position = 0,
            keyName = "hotkeyTrigger",
            name = "Hotkey trigger",
            description = "The should you hold the hotkey or toggle by pressing it?"
    )
    default HotkeyTrigger hotkeyTrigger() {
        return HotkeyTrigger.HOLD;
    }

    @ConfigItem(
            position = 1,
            keyName = "hotkey",
            name = "Socket Ping Hotkey",
            description = "The key that will trigger ping (accepts modifiers)"
    )
    default ModifierlessKeybind hotkey() {
        return new ModifierlessKeybind(0, 0);
    }

    @ConfigItem(
            position = 3,
            keyName = "targetColor",
            name = "Target color",
            description = "Color for the radial and overlay"
    )
    default Color targetColor() {
        return new Color(255, 255, 255, 255);
    }

    @ConfigItem(
            position = 4,
            keyName = "warnColor",
            name = "Warn color",
            description = "Color for the radial and overlay"
    )
    default Color warnColor() {
        return new Color(200, 0, 0);
    }

    @ConfigItem(
            position = 5,
            keyName = "omwColor",
            name = "On my way color",
            description = "Color for the radial and overlay"
    )
    default Color omwColor() {
        return new Color(0, 180, 80, 255);
    }

    @ConfigItem(
            position = 6,
            keyName = "assistMeColor",
            name = "Assist me color",
            description = "Color for the radial and overlay"
    )
    default Color assistMeColor() {
        return new Color(0, 100, 255, 255);
    }

    @ConfigItem(
            position = 7,
            keyName = "questionMarkColor",
            name = "Question mark color",
            description = "Color for the radial and overlay"
    )
    default Color questionMarkColor() {
        return new Color(255, 220, 0, 255);
    }

    @ConfigItem(
            position = 8,
            keyName = "overlayBorderWidth",
            name = "Overlay border",
            description = "The width of the overlay border for npcs, gameobjects, players"
    )
    default int overlayBorderWidth() {
        return 5;
    }

    @ConfigItem(
            position = 9,
            keyName = "tileBorderWidth",
            name = "Tile border width",
            description = "The width of the tile border"
    )
    default int tileBorderWidth() {
        return 2;
    }

    @ConfigItem(
            position = 10,
            keyName = "pingDecayTarget",
            name = "Target ping decay time",
            description = "The amount of frames it takes for the Target ping to disappear"
    )
    default int pingDecayTarget() {
        return 150;
    }

    @ConfigItem(
            position = 11,
            keyName = "pingDecayWarn",
            name = "Warn ping decay time",
            description = "The amount of frames it takes for the Warn ping to disappear"
    )
    default int pingDecayWarn() {
        return 150;
    }

    @ConfigItem(
            position = 12,
            keyName = "pingDecayOmw",
            name = "On my way ping decay time",
            description = "The amount of frames it takes for the On my way ping to disappear"
    )
    default int pingDecayOmw() {
        return 150;
    }

    @ConfigItem(
            position = 13,
            keyName = "pingDecayAssistMe",
            name = "Assist me ping decay time",
            description = "The amount of frames it takes for the Assist me ping to disappear"
    )
    default int pingDecayAssistMe() {
        return 150;
    }

    @ConfigItem(
            position = 14,
            keyName = "pingDecayQuestionMark",
            name = "Question mark ping decay time",
            description = "The amount of frames it takes for the Question mark ping to disappear"
    )
    default int pingDecayQuestionMark() {
        return 150;
    }

    @ConfigItem(
            position = 15,
            keyName = "hotkeyWarn",
            name = "Warn Ping Hotkey",
            description = "The key that will trigger Warn ping (accepts modifiers)"
    )
    default ModifierlessKeybind hotkeyWarn() {
        return new ModifierlessKeybind(0, 0);
    }

    @ConfigItem(
            position = 16,
            keyName = "hotkeyOmw",
            name = "On my way Ping Hotkey",
            description = "The key that will trigger On my way ping (accepts modifiers)"
    )
    default ModifierlessKeybind hotkeyOmw() {
        return new ModifierlessKeybind(0, 0);
    }

    @ConfigItem(
            position = 17,
            keyName = "hotkeyAssistMe",
            name = "Assist me Ping Hotkey",
            description = "The key that will trigger Assist me ping (accepts modifiers)"
    )
    default ModifierlessKeybind hotkeyAssistMe() {
        return new ModifierlessKeybind(0, 0);
    }

    @ConfigItem(
            position = 18,
            keyName = "hotkeyQuestionMark",
            name = "Question mark Ping Hotkey",
            description = "The key that will trigger Question mark ping (accepts modifiers)"
    )
    default ModifierlessKeybind hotkeyQuestionMark() {
        return new ModifierlessKeybind(0, 0);
    }
}
