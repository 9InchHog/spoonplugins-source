package net.runelite.client.plugins.vmswimshamer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;


@ConfigGroup("swimshamer")
public interface SwimShamerConfig extends Config
{
    @ConfigItem(
            keyName = "webhookEnabled",
            name = "Discord Webhook",
            description = "Allows you to send death photos automatically to a discord webhook. Read the github page for info.",
            position = 1
    )
    default boolean webhookEnabled()
    {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "webhookLink",
            name = "Webhook URL",
            description = "Put your webhook link here, the full thing copied from discord."
    )
    default String webhookLink()
    {
        return "";
    }
}

