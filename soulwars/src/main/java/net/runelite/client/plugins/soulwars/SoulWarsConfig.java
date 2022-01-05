package net.runelite.client.plugins.soulwars;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;

@ConfigGroup("soulwars")
public interface SoulWarsConfig extends Config {
    @ConfigItem(
            keyName = "takeBandageTable",
            name = "Swap Bandages Table",
            description = "Swap take-from with take options",
            position = 1
    )
    default BandageTableMode takeBandageTable() { return BandageTableMode.TAKE_FROM; }

    @ConfigItem(
            keyName = "takePowerTable",
            name = "Swap Potion of Power Table",
            description = "Swap take-from with take options",
            position = 2
    )
    default PowerTableMode takePowerTable() { return PowerTableMode.TAKE_FROM; }

    @ConfigItem(
            name = "Barricade Highlight",
            keyName = "barricadeHighlight",
            description = "Highlights the barricades according to their team",
            position = 3
    )
    default boolean barricadeHighlight() { return true; }

    @Range(min = 1, max = 5)
    @ConfigItem(
            name = "Barricade Tile Width",
            keyName = "barricadesThiCC",
            description = "Width for the Barricade highlights",
            position = 4)
    default int barricadesThiCC() { return 2 ; }

    @ConfigItem(
            keyName = "removeCast",
            name = "Remove Cast on Teammates",
            description = "Removes the cast option on teammates",
            position = 5
    )
    default boolean removeCast() { return true; }

    @ConfigItem(
            keyName = "showAvatarDamage",
            name = "Show Avatar Damage",
            description = "Displays how much damage you have done to the avatar",
            position = 6
    )
    default boolean showAvatarDamage() { return false; }

    public enum BandageTableMode {
        TAKE_FROM, TAKE_1, TAKE_5, TAKE_10;
    }

    public enum PowerTableMode {
        TAKE_FROM, TAKE_1, TAKE_5, TAKE_10;
    }
}
