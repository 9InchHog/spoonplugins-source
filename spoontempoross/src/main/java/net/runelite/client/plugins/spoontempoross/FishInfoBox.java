package net.runelite.client.plugins.spoontempoross;

import java.awt.Color;
import java.awt.image.BufferedImage;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.infobox.InfoBox;

public class FishInfoBox extends InfoBox {
    private SpoonTemporossPlugin plugin;

    private Client client;

    public FishInfoBox(BufferedImage img, Client client, SpoonTemporossPlugin plugin) {
        super(img, plugin);
        this.plugin = plugin;
        this.client = client;
    }

    public String getText() {
        return String.valueOf(this.plugin.fishCount);
    }

    public Color getTextColor() {
        return Color.WHITE;
    }

    public String getTooltip() {
        return "Total fish: " + this.plugin.fishCount;
    }
}
