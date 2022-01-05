package net.runelite.client.plugins.spoonslayer;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;

import java.awt.image.BufferedImage;

class TaskCounter extends Counter
{
    TaskCounter(BufferedImage img, Plugin plugin, int amount)
    {
        super(img, plugin, amount);
    }
}
