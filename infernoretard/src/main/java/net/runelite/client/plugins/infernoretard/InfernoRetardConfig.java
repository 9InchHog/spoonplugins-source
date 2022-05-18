package net.runelite.client.plugins.infernoretard;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.util.Set;

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

    @ConfigItem(
            position = 3,
            keyName = "spellbookCheck",
            name = "Remove Enter on Wrong Spellbook",
            description = "Makes it so you can't enter the inferno with the spellbooks you choose"
    )
    default Set<spellbook> spellbookCheck() {
        return Set.of(spellbook.NORMAL, spellbook.ANCIENT, spellbook.LUNAR, spellbook.ARCEUUS);
    }

    @ConfigItem(
            position = 4,
            keyName = "noTrident",
            name = "Remove Enter - Uncharged Trident",
            description = "Removes enter if you are on the Arceuus spellbook and have an uncharged trident in your inventory or equipped"
    )
    default boolean noTrident() {return true;}

    enum spellbook {
        NORMAL, ANCIENT, LUNAR, ARCEUUS
    }
}
