package net.runelite.client.plugins.outgoingchatfilter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("OutgoingChatFilterConfig")
public interface OutgoingChatFilterConfig extends Config {
  @ConfigItem(position = 0, keyName = "filteredWords", name = "Filtered words", description = "Words to filter from your message, replaced by asterisks")
  default String getWordsToFilter() {
    return "";
  }
}
