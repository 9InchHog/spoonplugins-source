package net.runelite.client.plugins.gwdessencehider;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("gwdessencehider")
public interface GwdEssenceHiderConfig extends Config {
    @ConfigItem(
            keyName = "textColor",
            name = "God Color",
            description = "Sets the color of each essence killcount to the color of the respective God",
            position = 0
    )
    default boolean textColor() {
        return false;
    }

    @ConfigItem(
            keyName = "godMode",
            name = "Display God Names",
            description = "Sets the length of the name of each God",
            position = 1
    )
    default GodMode godMode() {
        return GodMode.FULL_NAME;
    }

    @ConfigItem(
            keyName = "defaultColor",
            name = "Default Text Color",
            description = "Color when God Color is not selected",
            position = 2
    )
    default Color defaultColor() {
        return Color.CYAN;
    }

    @ConfigItem(
            keyName = "armaColor",
            name = "Armadyl Color",
            description = "Color of Armadyl killcount",
            position = 3
    )
    default Color armaColor() {
        return Color.DARK_GRAY;
    }

    @ConfigItem(
            keyName = "bandosColor",
            name = "Bandos Color",
            description = "Color of Bandos killcount",
            position = 4
    )
    default Color bandosColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "saraColor",
            name = "Saradomin Color",
            description = "Color of Saradomin killcount",
            position = 5
    )
    default Color saraColor() {
        return Color.CYAN;
    }

    @ConfigItem(
            keyName = "zammyColor",
            name = "Zamorak Color",
            description = "Color of Zamorak killcount",
            position = 6
    )
    default Color zammyColor() {
        return Color.RED;
    }

    @ConfigItem(
            keyName = "nexColor",
            name = "Ancient Color",
            description = "Color of Ancient killcount",
            position = 7
    )
    default Color nexColor() {
        return Color.MAGENTA;
    }

    public enum GodMode {
        FULL_NAME, NICKNAME
    }
}
