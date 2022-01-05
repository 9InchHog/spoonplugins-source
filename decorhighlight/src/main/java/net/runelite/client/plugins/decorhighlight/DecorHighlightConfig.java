package net.runelite.client.plugins.decorhighlight;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("decorhighlight")
public interface DecorHighlightConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "highlightColor",
            name = "Highlight Color",
            description = ""
    )
    default Color highlightColor() {
        return Color.RED;
    }

    @ConfigItem(
            position = 1,
            keyName = "graphicsObjectsToHighlight",
            name = "Highlight graphics objects",
            description = ""
    )
    default String graphicsObjectsToHighlight() {
        return "";
    }

    @ConfigItem(
            position = 2,
            keyName = "groundDecorToHighlight",
            name = "Highlight ground decor",
            description = ""
    )
    default String groundDecorToHighlight() {
        return "";
    }

    @ConfigItem(
            position = 3,
            keyName = "antiAlias",
            name = "Anti-aliasing",
            description = "Makes lines smoother"
    )
    default boolean antiAlias() { return true; }
}
