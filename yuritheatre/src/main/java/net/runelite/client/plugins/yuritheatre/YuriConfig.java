package net.runelite.client.plugins.yuritheatre;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("YuriTheatreConfig")
public interface YuriConfig extends Config {
    @ConfigItem(position = 0, keyName = "showMaidenCrabsDistance", name = "Show Maiden Distance", description = "Shows the distance that the maiden crabs have till their target.")
    default boolean showMaidenCrabsDistance() {
        return true;
    }

    @ConfigItem(position = 1, keyName = "showNylcoasChangeTimer", name = "Show Nylocas Change", description = "Shows the amount of ticks left until the Nylocas boss changes phases.")
    default boolean showNylcoasChangeTimer() {
        return true;
    }

    @ConfigItem(position = 2, keyName = "showSotetsegBomb", name = "Show Sotetseg Bomb", description = "Shows the timer for Sotetseg's bomb.")
    default boolean showSotetsegBomb() {
        return true;
    }

    @ConfigItem(position = 4, keyName = "showVerzikLightning", name = "Show Verzik Lightning", description = "Shows the timer for Verzik's lightning.")
    default boolean showVerzikLightning() {
        return true;
    }

    @ConfigItem(position = 6, keyName = "showPrayerDrink", name = "Drink Prayer Potion", description = "Indicate when to drink a prayer potion.")
    default boolean showPrayerDrink() {
        return false;
    }

    @ConfigItem(position = 7, keyName = "queueLightningCycle", name = "Queue Verzik Lightning Cycle", description = "Indicate that Verzik's next auto attack will be a lightning.")
    default boolean queueLightningCycle() {
        return true;
    }

    @ConfigItem(position = 8, keyName = "showPurpleLanding", name = "Show Purple Landing", description = "Shows where the purple will land at Verzik.")
    default boolean showPurpleLanding() {
        return true;
    }

    @ConfigItem(position = 9, keyName = "showVerzikBomb", name = "Show Verzik Bomb", description = "Shows the timer for Verzik's bomb.")
    default boolean showVerzikBomb() {
        return true;
    }

    @ConfigItem(position = 10, keyName = "showSotetsegBombsLeft", name = "Show Sotetseg Attack Cycle", description = "Shows the count for Sotetseg's bomb.")
    default boolean showSotetsegBombsLeft() {
        return true;
    }

    @ConfigItem(position = 11, keyName = "showNylocasBarrage", name = "Show Nylocas Barrage", description = "Shows the barrage tiles for Nylocas tiles.")
    default boolean showNylocasBarrageTiles() {
        return false;
    }

    @ConfigItem(position = 11, keyName = "showNylocasPillarIndicator", name = "Show Nylocas Pillar", description = "Shows hint arrow on lowest Nylocas pillar.")
    default boolean showNylocasPillar() {
        return false;
    }

    @ConfigItem(position = 12, keyName = "showBloatDest", name = "Show Bloat Destination", description = "Shows where Bloat will be when you are off cooldown.")
    default boolean showBloatDest() {
        return false;
    }

    @ConfigItem(position = 12, keyName = "showBloatAvailable", name = "Show Bloat Attack", description = "Shows when you can attack Bloat.")
    default boolean showBloatAttack() {
        return false;
    }
}
