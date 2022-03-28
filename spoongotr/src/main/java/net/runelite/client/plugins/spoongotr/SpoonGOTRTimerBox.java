package net.runelite.client.plugins.spoongotr;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
public class SpoonGOTRTimerBox extends InfoBox {
    private final SpoonGOTRConfig config;
    private final SpoonGOTRPlugin plugin;
    private final Client client;

    SpoonGOTRTimerBox(BufferedImage image, SpoonGOTRConfig config, SpoonGOTRPlugin plugin, Client client) {
        super(image, plugin);
        this.config = config;
        this.plugin = plugin;
        this.client = client;
        setPriority(InfoBoxPriority.LOW);
    }

    @Override
    public String getText() {
        return plugin.ticksToSeconds(client.getTickCount() - plugin.startTick);
    }

    @Override
    public Color getTextColor() {
        return Color.WHITE;
    }

    @Override
    public String getTooltip() {
        return "Time: " + plugin.ticksToSeconds(client.getTickCount() - plugin.startTick);
    }

    @Override
    public boolean render() {
        return config.instanceTimer() && plugin.gameStart;
    }
}
