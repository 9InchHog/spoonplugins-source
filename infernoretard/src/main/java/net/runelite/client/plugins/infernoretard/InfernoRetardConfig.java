package net.runelite.client.plugins.infernoretard;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("infernoretard")
public interface InfernoRetardConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "consumeClick",
            name = "Consume Click",
            description = "Consumes the click when on. Runs through when off."
    )
    default boolean consumeClick() {return true;}

    @ConfigItem(
            position = 1,
            keyName = "antibop",
            name = "Anti-bop",
            description = "Clicks through NPCs instead of kodai bopping"
    )
    default boolean antibop() {return true;}

    @ConfigItem(
            position = 2,
            keyName = "antikick",
            name = "Anti-kick",
            description = "Clicks through NPCs instead of kicking/punching"
    )
    default boolean antikick() {return true;}
}
