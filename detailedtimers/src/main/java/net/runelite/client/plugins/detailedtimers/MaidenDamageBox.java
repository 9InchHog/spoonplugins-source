package net.runelite.client.plugins.detailedtimers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

public class MaidenDamageBox extends InfoBox {
    private final DetailedTimersPlugin plugin;

    public MaidenDamageBox(BufferedImage image, DetailedTimersPlugin plugin) {
        super(image, plugin);
        this.plugin = plugin;
        setPriority(InfoBoxPriority.MED);
        setTooltip("Maiden max hit");
    }

    public String getText() {
        return getMaxOffPrayer(this.plugin.getMaidenLeaked()) + " " + getMaxOnPrayer(this.plugin.getMaidenLeaked());
    }

    private int getMaxOffPrayer(int leaked) {
        return (int)(36.5D + 3.5D * leaked);
    }

    private int getMaxOnPrayer(int leaked) {
        return (int)(Math.floor(36.5D + 3.5D * leaked) / 2.0D);
    }

    public Color getTextColor() {
        return Color.WHITE;
    }

    public boolean render() {
        return this.plugin.isMaidenActive();
    }
}
