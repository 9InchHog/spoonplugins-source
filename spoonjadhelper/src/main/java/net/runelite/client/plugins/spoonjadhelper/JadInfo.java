package net.runelite.client.plugins.spoonjadhelper;

import net.runelite.api.NPC;

import java.awt.*;

public class JadInfo {
    public JadInfo(NPC jad, int ticks, Color color) {
        this.jad = jad;
        this.ticks = ticks;
        this.color = color;
    }
    public NPC jad;
    public int ticks;
    public Color color;
}
