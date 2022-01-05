package net.runelite.client.plugins.spoonsmithing;

import net.runelite.client.config.*;

@ConfigGroup("spoonSmithing")
public interface SpoonSmithingConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "smithingItems",
            name = "Allow Items",
            description = "Comma separated list to choose which smithing items to allow"
    )
    default String smithingItems() { return ""; }
}
