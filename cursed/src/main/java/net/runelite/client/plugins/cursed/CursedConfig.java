package net.runelite.client.plugins.cursed;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("SpoonCursed")
public interface CursedConfig extends Config {

    @ConfigItem(keyName = "swapEssenceRunning", name = "Essence Running", description = "Don't", position = 0)
    default boolean swapEssenceRunning() { return false; }

    @ConfigItem(keyName = "why", name = "Why?", description = "Seriously, wtf?", position = 1)
    default boolean why() { return false; }

    @ConfigItem(keyName = "clientTicks", name = "Client Ticks", description = "This exists now I guess", position = 2)
    default boolean clientTicks() { return false; }

    @ConfigItem(keyName = "gameTicks", name = "Game Ticks", description = "Just a normal metronome", position = 3)
    default boolean gameTicks() { return false; }

    @ConfigItem(keyName = "magicTrick", name = "Magic Trick", description = "Tada! It's..... Gone! <br><html><font color=#ff0000>WARNING! Can crash clients in certain areas", position = 4)
    default boolean magicTrick() { return false; }

    @ConfigItem(keyName = "npcEpilepsy", name = "NPC Epilepsy", description = "Get help", position = 5)
    default boolean npcEpilepsy() { return false; }
	
	@ConfigItem(keyName = "raveProjectiles", name = "Rave Projectiles", description = "Never enough rave plugins", position = 6)
    default boolean raveProjectiles() { return false; }
	
	@ConfigItem(keyName = "pulsingPlayers", name = "Pulsing Players", description = "Lets just see what happens", position = 7)
    default boolean pulsingPlayers() { return false; }

    @ConfigItem(keyName = "psychedelicNpcs", name = "Psychedelic NPC", description = "Woah maaaaaaan", position = 8)
    default boolean psychedelicNpcs() { return false; }

    @ConfigItem(keyName = "skinwalkers", name = "Skinwalkers", description = "They are not what they seem", position = 9)
    default boolean skinwalkers() { return false; }

    @ConfigItem(keyName = "catJam", name = "Cat Jam", description = "bumpin", position = 10)
    default boolean catJam() { return false; }

    @Range(min = 0, max = 100)
    @ConfigItem(keyName = "catJamVolume", name = "Cat Jam Volume", description = "Oh god no my ears", position = 11)
    default int catJamVolume() { return 40; }
}
