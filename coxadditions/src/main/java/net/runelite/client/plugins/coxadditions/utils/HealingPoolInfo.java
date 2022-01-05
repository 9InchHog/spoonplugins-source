package net.runelite.client.plugins.coxadditions.utils;

import net.runelite.api.coords.LocalPoint;

public class HealingPoolInfo {
    public HealingPoolInfo(LocalPoint lp, int ticks) {
        this.lp = lp;
        this.ticks = ticks;
    }
    public LocalPoint lp;
    public int ticks;
}
