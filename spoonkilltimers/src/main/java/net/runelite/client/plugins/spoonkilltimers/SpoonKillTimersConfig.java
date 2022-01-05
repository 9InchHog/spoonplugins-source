package net.runelite.client.plugins.spoonkilltimers;

import net.runelite.client.config.*;

@ConfigGroup("spoonKillTimers")
public interface SpoonKillTimersConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "timerMode",
            name = "Timer Mode",
            description = "Toggle how to display the timer"
    )
    default timerMode timerMode() {
        return timerMode.INFOBOX;
    }

    @ConfigItem(
            position = 1,
            keyName = "hespori",
            name = "Hespori",
            description = "Displays a kill timer for Hespori"
    )
    default boolean hespori() {return false;}

    @ConfigItem(
            position = 2,
            keyName = "zulrah",
            name = "Zulrah",
            description = "Displays a kill timer for Zulrah"
    )
    default boolean zulrah() {return false;}

    @ConfigItem(
            position = 3,
            keyName = "vorkath",
            name = "Vorkath",
            description = "Displays a kill timer for Vorkath"
    )
    default boolean vorkath() {return false;}

    @ConfigItem(
            position = 4,
            keyName = "hydra",
            name = "Alchemical Hydra",
            description = "Displays a kill timer for the Alchemical Hydra"
    )
    default boolean hydra() {return false;}

    @ConfigItem(
            position = 5,
            keyName = "grotesqueGuardians",
            name = "Grotesque Guardians",
            description = "Displays a kill timer for the Grotesque Guardians"
    )
    default boolean grotesqueGuardians() {return false;}

    @ConfigItem(
            position = 6,
            keyName = "seren",
            name = "Seren",
            description = "Displays a kill timer for Seren"
    )
    default boolean seren() {return false;}

    @ConfigItem(
            position = 7,
            keyName = "glough",
            name = "Glough",
            description = "Displays a kill timer for Glough"
    )
    default boolean glough() {return false;}

    @ConfigItem(
            position = 8,
            keyName = "galvek",
            name = "Galvek",
            description = "Displays a kill timer for Galvek"
    )
    default boolean galvek() {return false;}

    public static enum timerMode {
        INFOBOX, PANEL;
    }
}
