package net.runelite.client.plugins.defaulttab;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("defaulttab")
public interface DefaultTabConfig extends Config {
    @ConfigItem(name = "Default Tab", keyName = "defaultTab", description = "", position = 0)
    default DefaultTab getDefaultTab() {
        return DefaultTab.INVENTORY;
    }

    public enum DefaultTab {
        COMBAT_OPTIONS("Combat", 0),
        SKILLS("Skills", 1),
        QUEST("Quest/Minigames", 2),
        INVENTORY("Inventory", 3),
        EQUIPMENT("Equipment", 4),
        PRAYER("Prayer", 5),
        SPELLBOOK("Spellbook", 6),
        CLAN_CHAT("Clan Chat", 7),
        FILIST("Friends List", 9),
        ACCOUNT("Account", 8),
        LOGOUT("Logout", 10),
        SETTINGS("Settings", 11),
        EMOTES("Emotes", 12),
        MUSIC_PLAYER("Music Player", 13);

        DefaultTab(String name, int index) {
            this.name = name;
            this.index = index;
        }

        private final String name;

        private final int index;

        public String getName() {
            return this.name;
        }

        public int getIndex() {
            return this.index;
        }

        public String toString() {
            return this.name;
        }
    }
}
