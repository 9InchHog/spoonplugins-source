package net.runelite.client.plugins.ariatob;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("AriaTobConfig")

public interface AriaTobConfig extends Config
{
    @ConfigSection(name = "Nylos", description = "", position = 0)
    public static final String nyloSection = "Nylo";


    @ConfigItem(
            keyName = "nyloPrio",
            name = "Low Tick Nylo MES",
            description = "Prioritises low tick nylos for left click.",
            section = nyloSection
    )
    default boolean nyloPrio()
    {
        return false;
    }

    @ConfigItem(
            keyName = "nyloPrioSmalls",
            name = "Prioritise Smalls",
            description = "Prioritises small over big nylos.<br>Needs Low Tick Nylo MES on",
            section = nyloSection
            //hidden = true,
            //unhide = "nyloPrio"
    )
    default boolean nyloPrioSmalls()
    {
        return false;
    }

    @ConfigItem(
            keyName = "nyloPrio35s",
            name = "Prioritise bigs over 35+",
            description = "Prioritises bigs over nylos above 35 ticks.<br>Needs Prioritise Smalls enabled.",
            section = nyloSection
            //hidden = true,
            //unhide = "nyloPrioBigs"
    )
    default boolean nyloPrio35s()
    {
        return false;
    }
}
