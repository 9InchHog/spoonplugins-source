package net.runelite.client.plugins.detailedtimers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("detailedTimers")
public interface DetailedTimersConfig extends Config {
    @ConfigItem(position = 0, keyName = "showMaidenMax", name = "Show Maiden Max", description = "Shows Maiden's max hit as an infobox")
    default boolean showMaidenMax() {
        return false;
    }

    @ConfigItem(position = 1, keyName = "showChinning", name = "Show wrong chinning distance", description = "Tells you if a player chins from less than 4 or more than 6 tiles away")
    default boolean showChinning() {
        return true;
    }

    @ConfigItem(position = 2, keyName = "showN3Barrage", name = "Show wrong barrage", description = "Tells you if a player casts barrage on S3 instead of N3")
    default boolean show3Cast() {
        return true;
    }

    @ConfigItem(position = 3, keyName = "showMaidenSpecs", name = "Show Maiden Specs", description = "Tells you if anyone specced late on maiden")
    default boolean showMaidenSpecs() {
        return true;
    }

    @ConfigItem(position = 4, keyName = "showSoteSpecs", name = "Show Sotetseg Specs", description = "Tells you if anyone specced late on sotetseg")
    default boolean showSoteSpecs() {
        return true;
    }

    @ConfigItem(position = 5, keyName = "showUnchargedScythe", name = "Show uncharged scythe", description = "Tells you if anyone swings with an uncharged scythe")
    default boolean showUnchargedScythe() {
        return true;
    }

    @ConfigItem(position = 6, keyName = "showUnchargedSerp", name = "Show uncharged serp", description = "Tells you if someone swings with an uncharged serp")
    default boolean showUnchargedSerp() {
        return true;
    }

    @ConfigItem(position = 7, keyName = "showMissingSwitches", name = "Show missing switches", description = "Tells you if someone specs without all their gear on")
    default boolean showMissingSwitches() {
        return true;
    }

    @ConfigItem(position = 8, keyName = "showLeakedCrabs", name = "Show leaked crabs", description = "Show crabs that leak at maiden")
    default boolean showLeakedCrabs() {
        return true;
    }

    @ConfigItem(position = 9, keyName = "showScuffedSpawns", name = "Show Scuffed Spawns", description = "Tells you in chat box if spawns are scuffed at maiden")
    default boolean showScuffedSpawns() {
        return true;
    }

    @ConfigItem(position = 10, keyName = "showHammerBop", name = "Show Hammer Bop", description = "Tells you if a player hammer bops")
    default boolean showHammerBop() {
        return true;
    }

    @ConfigItem(position = 11, keyName = "showBGSWhack", name = "Show BGS Whack", description = "Tells you if a player BGS Swings")
    default boolean showBGSWhack() {
        return true;
    }

    @ConfigItem(position = 12, keyName = "showBloodFury", name = "Show Blood Fury", description = "Tells you if a player is using a blood fury")
    default boolean showBloodFury() {
        return false;
    }

    @ConfigItem(position = 13, keyName = "showChallyPoke", name = "Show Chally Poke", description = "Tells you if someone chally pokes")
    default boolean showChallyPoke() {
        return true;
    }

    @ConfigItem(position = 14, keyName = "showKodaiBop", name = "Show Kodai Bop", description = "Tells you if someone bopped with kodai")
    default boolean showKodaiBop() {
        return true;
    }

    @ConfigItem(position = 15, keyName = "bigsAliveDuration", name = "Show Bigs Alive Duration", description = "Tells you how long a big was alive for.")
    default boolean showBigsAlive() {
        return false;
    }

    @ConfigItem(position = 100, keyName = "auth", name = "Authentication", description = "")
    default String getAuthenticationKey() {
        return "";
    }
}
