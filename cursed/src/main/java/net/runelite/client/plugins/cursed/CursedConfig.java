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

    @ConfigItem(keyName = "diabloBrews", name = "Diablo Brews", description = "Some good ole nostalgia", position = 12)
    default boolean diabloBrews() { return false; }

    @Range(min = 0, max = 100)
    @ConfigItem(keyName = "diabloBrewsVolume", name = "Diablo Brews Volume", description = "Trying not to go deaf by 30", position = 13)
    default int diabloBrewsVolume() { return 40; }

    @ConfigItem(keyName = "bigDie", name = "Big Die", description = "Big die", position = 14)
    default boolean bigDie() { return false; }

    @ConfigItem(keyName = "coxDepression", name = "CoX Depression", description = "Emotional damage!", position = 15)
    default boolean coxDepression() { return false; }

    @Range(min = 0, max = 100)
    @ConfigItem(keyName = "coxDepressionVolume", name = "CoX Depression Volume", description = "Slightly lessens the damage", position = 16)
    default int coxDepressionVolume() { return 40; }

    @ConfigItem(keyName = "gtaCa", name = "GTA CA", description = "Mission passed. Respect earned.", position = 17)
    default boolean gtaCa() { return false; }

    @Range(min = 0, max = 100)
    @ConfigItem(keyName = "gtaCaVolume", name = "GTA CA Volume", description = "Another volume setting so im not deaf by 30", position = 18)
    default int gtaCaVolume() { return 40; }

    @ConfigItem(keyName = "immersiveHp", name = "Immersive HP", description = "You are hurt. Get to cover!", position = 19)
    default boolean immersiveHp() { return false; }
}