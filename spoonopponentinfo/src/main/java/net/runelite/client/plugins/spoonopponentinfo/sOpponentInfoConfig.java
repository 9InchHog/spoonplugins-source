package net.runelite.client.plugins.spoonopponentinfo;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("opponentinfo")
public interface sOpponentInfoConfig extends Config {
    @ConfigItem(
            keyName = "lookupOnInteraction",
            name = "Lookup players on interaction",
            description = "Display a combat stat comparison panel on player interaction. (follow, trade, challenge, attack, etc.)",
            position = 0
    )
    default boolean lookupOnInteraction() {
        return false;
    }

    @ConfigItem(
            keyName = "hitpointsDisplayStyle",
            name = "Hitpoints display style",
            description = "Show opponent's hitpoints as a value (if known), percentage, or both",
            position = 1
    )
    default sHitpointsDisplayStyle hitpointsDisplayStyle() {
        return sHitpointsDisplayStyle.HITPOINTS;
    }

    @ConfigItem(
            keyName = "showOpponentsOpponent",
            name = "Show opponent's opponent",
            description = "Toggle showing opponent's opponent if within a multi-combat area",
            position = 2
    )
    default boolean showOpponentsOpponent() {
        return true;
    }

    @ConfigItem(
            keyName = "showOpponentsInMenu",
            name = "Show opponents in menu",
            description = "Marks opponents names in the menu which you are attacking or are attacking you (NPC only)",
            position = 3
    )
    default boolean showOpponentsInMenu() {
        return false;
    }

    @ConfigItem(
            keyName = "showAggroWarning",
            name = "Show aggro warning",
            description = "If opponent's opponent is on, this will highlight your name red when you are the target.",
            position = 4
    )
    default boolean showAggroWarning() {
        return true;
    }

    @ConfigItem(
            keyName = "hpHud",
            name = "Don't show if NPC has Hp Hud",
            description = "If an NPC has the Hp Hud and display health bar is turned on in the in game settings, the opponent info overlay will be hidden",
            position = 5
    )
    default boolean hpHud() {
        return false;
    }

    @ConfigItem(
            keyName = "showDumbStar",
            name = "Show dumb star",
            description = "Adds that stupid fucking star next to the NPC name. The star breaks some plugins",
            position = 6
    )
    default boolean showDumbStar() { return false; }
}
