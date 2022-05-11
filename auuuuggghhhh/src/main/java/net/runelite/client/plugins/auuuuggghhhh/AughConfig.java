package net.runelite.client.plugins.auuuuggghhhh;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("augh")
public interface AughConfig extends Config {
    @Range(max = 100)
    @ConfigItem(
            keyName = "volume",
            name = "Volume Level",
            description = "Adjust the volume from 0 to 100"
    )
    default int volume() {return 50;}

    @ConfigItem(
            keyName = "dogs",
            name = "Dogs",
            description = "Enter text to replace dogs task with, leave blank to ignore"
    )
    default String dogs() {return "";}

    @ConfigItem(
            keyName = "monkeys",
            name = "Monkeys",
            description = "Enter text to replace monkeys task with, leave blank to ignore"
    )
    default String monkeys() {return "";}

    @ConfigItem(
            keyName = "rats",
            name = "Rats",
            description = "Enter text to replace rats task with, leave blank to ignore"
    )
    default String rats() {return "";}

    @ConfigItem(
            keyName = "dwarves",
            name = "Dwarves",
            description = "Enter text to replace dwarves task with, leave blank to ignore"
    )
    default String dwarves() {return "";}
}
