package net.runelite.client.plugins.tmorph;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("NPCTMorph")
public interface TMorphNPCConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "npcID",
            name = "NPC ID",
            description = "Change your character model to an NPC model"
    )
    default int npcID() {
        return -1;
    }
}
