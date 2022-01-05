package net.runelite.client.plugins.deasyscape;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("easyscape")
public interface DEasyScapeConfig extends Config {
    @ConfigItem(keyName = "removeExamine", name = "Remove Examine", description = "Removes Examine from the list of menu options.")
    default boolean removeExamine() {
        return true;
    }

    @ConfigItem(keyName = "removeObjects", name = "Remove Ground Items", description = "Removes interaction with the listed ground items.")
    default boolean removeGroundItems() {
        return true;
    }

    @ConfigItem(keyName = "removedObjects", name = "Ground Items", description = "Items listed here will have all ground interaction be removed.")
    default String removedGroundItems() {
        return "";
    }

    @ConfigItem(keyName = "removeInvObjects", name = "Remove Inv Items", description = "Removes interaction with the listed inventory items.")
    default boolean removeInvItems() {
        return true;
    }

    @ConfigItem(keyName = "removedInvObjects", name = "Inv Items", description = "Items listed here will have all inventory interaction be removed.")
    default String removedInvItems() {
        return "";
    }

    @ConfigItem(keyName = "removeNpcs", name = "Remove NPCs", description = "Removes interaction with the listed NPCs.")
    default boolean removeNpcs() {
        return true;
    }

    @ConfigItem(keyName = "removedNpcs", name = "NPCs", description = "NPCs listed here will have all interaction be removed.")
    default String removedNpcs() {
        return "";
    }

    @ConfigItem(keyName = "removeGameObjects", name = "Remove Objects", description = "Removes interaction with the listed objects.")
    default boolean removeObjects() {
        return true;
    }

    @ConfigItem(keyName = "removedGameObjects", name = "Objects", description = "Objects listed here will have all interaction be removed.")
    default String removedObjects() {
        return "";
    }
}
