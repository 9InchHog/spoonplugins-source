package net.runelite.client.plugins.spoonrunecraft;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup("runecraft")
public interface sRunecraftConfig extends Config {
	@ConfigSection(name = "Rift", description = "Abyss rift overlay settings", position = 99, closedByDefault = true)
	public static final String riftSection = "rifts";

	@ConfigSection(name = "Misc.", description = "Misc. runecrafting settings", position = 1, closedByDefault = true)
	public static final String rcSection = "rc";

	@ConfigSection(name = "Zeah", description = "Zeah runecrafting settings", position = 0, closedByDefault = true)
	public static final String zeahSection = "zeah";

	@ConfigItem(
			keyName = "showDenseRunestoneIndicator",
			name = "Show mining indicator",
			description = "Configures whether to display an indicator when dense runestone is ready to be mined",
			position = 0,
			section = "zeah"
	)
	default boolean showDenseRunestoneIndicator() { return true; }

	@ConfigItem(
			keyName = "showDenseRunestoneClickbox",
			name = "Show click box",
			description = "Configures whether to display a click box when dense runestone is ready to be mined",
			position = 1,
			section = "zeah"
	)
	default boolean showDenseRunestoneClickbox()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showDenseRunestoneClickboxAvailable",
			name = "Active Color",
			description = "Configures Color of available dense runestone",
			position = 2,
			section = "zeah"
	)
	default Color showDenseRunestoneClickboxAvailable() { return Color.GREEN; }

	@ConfigItem(
			keyName = "showDenseRunestoneClickboxUnavailable",
			name = "Depleted Color",
			description = "Configures color of unavailable dense runestone",
			position = 3,
			section = "zeah"
	)
	default Color showDenseRunestoneClickboxUnavailable()
	{
		return Color.RED;
	}

	@ConfigItem(keyName = "lavas", name = "Lavas", description = "Swaps Ring of dueling left click depending on location.", position = 0, section = "rc")
	default boolean lavas() {
		return false;
	}

	@ConfigItem(keyName = "essPouch", name = "Swap essence pouch", description = "Makes essence pouch left-click fill in bank", position = 1, section = "rc")
	default boolean essPouch() {
		return false;
	}

	@ConfigItem(keyName = "leftClickTrade", name = "Left Click Trade", description = "Make default left click option 'trade' on players when near Mind/Fire/Astral altar. Removes clickbox from nearby distractions.", position = 2, section = "rc")
	default boolean getLeftClickTrade() {
		return false;
	}

	@ConfigItem(keyName = "leftClickOffer", name = "Left Click Offer-All", description = "Make default left click option Offer-all in trade.", position = 3, section = "rc")
	default boolean getLeftClickOfferAll() {
		return false;
	}

	@ConfigItem(keyName = "defaultLavas", name = "Default gear tab", description = "Sets the default tab to your gear tab when you close the bank - requires lavas to be on", position = 4, section = "rc")
	default boolean defaultLavas() {
		return false;
	}

	@ConfigItem(keyName = "showRifts", name = "Show Rifts in Abyss", description = "Configures whether the rifts in the abyss will be displayed", position = 2, section = "rifts")
	default boolean showRifts() {
		return true;
	}

	@ConfigItem(keyName = "showClickBox", name = "Show Rift click box", description = "Configures whether to display the click box of the rift", position = 3, section = "rifts")
	default boolean showClickBox() {
		return true;
	}

	@ConfigItem(keyName = "showAir", name = "Show Air rift", description = "Configures whether to display the air rift", position = 4, section = "rifts")
	default boolean showAir() {
		return true;
	}

	@ConfigItem(keyName = "showBlood", name = "Show Blood rift", description = "Configures whether to display the Blood rift", position = 5, section = "rifts")
	default boolean showBlood() {
		return true;
	}

	@ConfigItem(keyName = "showBody", name = "Show Body rift", description = "Configures whether to display the Body rift", position = 6, section = "rifts")
	default boolean showBody() {
		return true;
	}

	@ConfigItem(keyName = "showChaos", name = "Show Chaos rift", description = "Configures whether to display the Chaos rift", position = 7, section = "rifts")
	default boolean showChaos() {
		return true;
	}

	@ConfigItem(keyName = "showCosmic", name = "Show Cosmic rift", description = "Configures whether to display the Cosmic rift", position = 8, section = "rifts")
	default boolean showCosmic() {
		return true;
	}

	@ConfigItem(keyName = "showDeath", name = "Show Death rift", description = "Configures whether to display the Death rift", position = 9, section = "rifts")
	default boolean showDeath() {
		return true;
	}

	@ConfigItem(keyName = "showEarth", name = "Show Earth rift", description = "Configures whether to display the Earth rift", position = 10, section = "rifts")
	default boolean showEarth() {
		return true;
	}

	@ConfigItem(keyName = "showFire", name = "Show Fire rift", description = "Configures whether to display the Fire rift", position = 11, section = "rifts")
	default boolean showFire() {
		return true;
	}

	@ConfigItem(keyName = "showLaw", name = "Show Law rift", description = "Configures whether to display the Law rift", position = 12, section = "rifts")
	default boolean showLaw() {
		return true;
	}

	@ConfigItem(keyName = "showMind", name = "Show Mind rift", description = "Configures whether to display the Mind rift", position = 13, section = "rifts")
	default boolean showMind() {
		return true;
	}

	@ConfigItem(keyName = "showNature", name = "Show Nature rift", description = "Configures whether to display the Nature rift", position = 14, section = "rifts")
	default boolean showNature() {
		return true;
	}

	@ConfigItem(keyName = "showSoul", name = "Show Soul rift", description = "Configures whether to display the Soul rift", position = 15, section = "rifts")
	default boolean showSoul() {
		return true;
	}

	@ConfigItem(keyName = "showWater", name = "Show Water rift", description = "Configures whether to display the Water rift", position = 16, section = "rifts")
	default boolean showWater() {
		return true;
	}

	@ConfigItem(keyName = "hightlightDarkMage", name = "Highlight Dark Mage NPC", description = "Configures whether to highlight the Dark Mage when pouches are degraded", position = 4, section = "rc")
	default boolean hightlightDarkMage() {
		return true;
	}

	@ConfigItem(keyName = "degradingNotification", name = "Notify when pouch degrades", description = "Send a notification when a pouch degrades", position = 5, section = "rc")
	default boolean degradingNotification() {
		return true;
	}
}
