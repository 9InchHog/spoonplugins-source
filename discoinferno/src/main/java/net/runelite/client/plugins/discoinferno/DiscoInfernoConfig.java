package net.runelite.client.plugins.discoinferno;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("trammps")
public interface DiscoInfernoConfig extends Config {
	@ConfigItem(
			keyName = "trammps",
			name = "The Trammps",
			description = "You know whats up."
	)
	default boolean trammps() {return true;}

	@Range(min = 0, max = 100)
	@ConfigItem(
			keyName = "volume",
			name = "Volume",
			description = "Volume."
	)
    default int volume() {return 30;}

	@ConfigItem(
			keyName = "boogie",
			name = "Boogie",
			description = "Its time to get down."
	)
	default boolean boogie() {return true;}
}
