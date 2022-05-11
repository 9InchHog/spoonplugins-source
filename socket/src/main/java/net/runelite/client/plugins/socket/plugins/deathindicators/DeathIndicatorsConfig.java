package net.runelite.client.plugins.socket.plugins.deathindicators;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("deathindicators")
public interface DeathIndicatorsConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName = "showOutline",
            name = "Show Outline",
            description = "Shows outline when killed"
    )
    default boolean showOutline() { return false;}

    @ConfigItem(
            position = 1,
            keyName = "showHull",
            name = "Use Retard Outline",
            description = "Shows hull when killed"
    )
    default boolean showHull() { return false;}

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "outlineColor",
            name = "Dead Nylo Color",
            description = "Color of the outline on dead nylos"
    )
    default Color outlineColor() { return Color.RED;}

    @ConfigItem(
            position = 3,
            keyName = "hideNylo",
            name = "Hide Nylo",
            description = "Hides nylo when killed"
    )
    default boolean hideNylo() { return true;}

    @ConfigItem(
            position = 4,
            keyName = "deprioNylo",
            name = "Deprioritize Dead Nylo",
            description = "Deprioritizes attack option on Nylos when dead"
    )
    default boolean deprioNylo() { return false;}

    /*@ConfigItem(
            position = 2,
            keyName = "maidenMarkers",
            name = "Maiden Markers",
            description = "Maiden Outline"
    )
    default boolean maidenMarkers()
    {
        return false;
    }

    @ConfigItem(
			position = 3,
			keyName = "maidenProc",
			name = "Maiden Procs",
			description = "Shows when Maiden will proc"
	)
	default boolean maidenProc()
	{
		return true;
	}*/

}