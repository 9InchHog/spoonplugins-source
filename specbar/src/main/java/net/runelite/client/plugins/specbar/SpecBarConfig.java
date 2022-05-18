package net.runelite.client.plugins.specbar;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("specbar")
public interface SpecBarConfig extends Config
{
    @ConfigItem(
            position = 2,
            keyName = "specbarid",
            name = "Spec bar widget id",
            description = "Configures the id for the specbar widget since it can change after update"
    )
    default int specbarid()
    {
        return 35;
    }
}