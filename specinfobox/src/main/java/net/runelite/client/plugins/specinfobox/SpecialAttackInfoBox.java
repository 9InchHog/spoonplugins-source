package net.runelite.client.plugins.specinfobox;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

public class SpecialAttackInfoBox extends InfoBox {
    SpecialAttackInfoBox(Plugin plugin, BufferedImage img) {
        super(img, plugin);
    }

    public String getText() {
        return null;
    }

    public Color getTextColor() {
        return null;
    }
}
