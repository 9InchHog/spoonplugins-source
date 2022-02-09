package net.runelite.client.plugins.socket.plugins.deathindicators;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("deathindicators")
public interface DeathIndicatorsConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName = "showOutline",
            name = "Show outline",
            description = "Shows outline when killed"
    )
    default boolean showOutline() { return false;}

    @ConfigItem(
            position = 1,
            keyName = "hideNylo",
            name = "Hide Nylo",
            description = "Hides nylo when killed"
    )
    default boolean hideNylo() { return true;}

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
