package net.runelite.client.plugins.coxoverloadtimers;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("coxoverloadtimers")
public interface OverloadTimerConfig extends Config {
    String GROUP_NAME = "coxoverloadtimers";

    @ConfigItem(
            name = "Unit of Time",
            keyName = "overloadUnitOfTime",
            description = "Changes the Unit of Time for CoX Overloads",
            position = 0
    )
    default OverloadTimerConfig.UnitsOfTime ovlUnitOfTime() {
        return OverloadTimerConfig.UnitsOfTime.REGULAR;
    }

    @ConfigItem(
            name = "Prewarn",
            keyName = "ovlPrewarn",
            description = "Changes the text color on the CoX Overload timer when you're x seconds away from the 15 second modulo",
            position = 1
    )
    default boolean ovlPrewarn() {
        return false;
    }

    @ConfigItem(
            name = "Prewarn Gap",
            keyName = "ovlPrewarnGap",
            description = "Changes the threshold for when to prewarn",
            position = 2
    )
    @Range(
            max = 14,
            min = 1
    )
    @Units("s")
    default int ovlPrewarnGap() {
        return 2;
    }

    @ConfigItem(
            name = "Prewarn Color",
            keyName = "ovlPrewarnColor",
            description = "Configures the color for the prewarn",
            position = 3
    )
    default Color ovlPrewarnColor() {
        return Color.YELLOW;
    }

    @ConfigItem(
            name = "15 Second Interval",
            keyName = "ovl15SecondModulo",
            description = "Changes the text color on the CoX Overload timer when the timer is on the 15 second range",
            position = 4
    )
    default boolean ovl15SecondModulo() {
        return false;
    }

    @ConfigItem(
            name = "Interval Color",
            keyName = "ovl15SecondModuloColor",
            description = "Configures the color for the 15 second intervals",
            position = 5
    )
    default Color ovl15SecondModuloColor() {
        return Color.GREEN;
    }

    enum UnitsOfTime {
        REGULAR("Regular"),
        SECONDS("Seconds"),
        GAME_TICKS("Game Ticks");

        private final String name;

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        UnitsOfTime(String name) {
            this.name = name;
        }
    }
}
