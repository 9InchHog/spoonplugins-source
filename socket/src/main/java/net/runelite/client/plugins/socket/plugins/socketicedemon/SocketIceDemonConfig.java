package net.runelite.client.plugins.socket.plugins.socketicedemon;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("icDemon")
public interface SocketIceDemonConfig extends Config {
  @ConfigSection(
          name = "<html><font color=#00aeef>Brazier",
          description = "Brazier Plugins",
          position = 0,
          closedByDefault = true
  )
  String brazierSection = "brazier";

  @ConfigItem(
          keyName = "display4Scav",
          name = "Display Overlay for CM Scaver",
          description = "Displays the overlay when you are in Scavs or Ice Demon. Have to be in Socket to accurately display the information.",
          position = 1
  )
  default boolean display4Scav() {
    return true;
  }

  @ConfigItem(
          keyName = "dumpMsg",
          name = "Dump Message",
          description = "Puts a message in game chat of when to dump based off your current raids party size",
          position = 2
  )
  default boolean dumpMsg() {
    return true;
  }

  @ConfigItem(
          keyName = "showTeamKindling",
          name = "Show Kindling Needed",
          description = "Shows the amount of kindling needed in the infobox",
          position = 3
  )
  default boolean showTeamKindling() {
    return true;
  }

  @ConfigItem(
          keyName = "showNames",
          name = "Show Names",
          description = "Shows the name of players and how many kindling they got",
          position = 4
  )
  default boolean showNames() { return true; }

  @ConfigItem(
          keyName = "iceDemonSpawnTicks",
          name = "Ice Demon Spawn Ticks",
          description = "Displays ticks until Ice Demon activates after finishing lighting",
          position = 5
  )
  default boolean iceDemonSpawnTicks() {
    return true;
  }

  @ConfigItem(
          keyName = "iceDemonHp",
          name = "Ice Demon HP",
          description = "Displays Ice Demon HP percent while lighting kindling",
          position = 6
  )
  default boolean iceDemonHp() {
    return true;
  }

  @ConfigItem(
          keyName = "highlightUnlitBrazier",
          name = "Highlight Unlit Brazier",
          description = "Draws a tile under unlit braziers",
          position = 0,
          section = brazierSection
  )
  default boolean highlightUnlitBrazier() {
    return true;
  }

  @Alpha
  @ConfigItem(
          keyName = "highlightBrazierColor",
          name = "Highlight Brazier Color",
          description = "Sets color of highlight unlit brazier plugin",
          position = 1,
          section = brazierSection
  )
  default Color highlightBrazierColor() {
    return Color.RED;
  }

  @Range(min = 0, max = 255)
  @ConfigItem(
          keyName = "highlightBrazierOpacity",
          name = "Highlight Brazier Opacity",
          description = "Sets Opacity of highlight unlit brazier plugin",
          position = 2,
          section = brazierSection
  )
  default int highlightBrazierOpacity() {
    return 50;
  }
}
