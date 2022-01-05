package net.runelite.client.plugins.spoonvm;

import net.runelite.api.GameObject;

public class SpoonVMObjects {
    public SpoonVMObjects(GameObject obj, int ticks) {
        this.obj = obj;
        this.ticks = ticks;
    }
    public GameObject obj;
    public int ticks;
}
