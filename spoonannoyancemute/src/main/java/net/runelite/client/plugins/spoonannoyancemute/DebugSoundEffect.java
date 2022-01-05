package net.runelite.client.plugins.spoonannoyancemute;

import net.runelite.api.Actor;

public class DebugSoundEffect {
    public String type;
    public Actor source;
    public int id;
    public int ticks;
    public DebugSoundEffect(String type, Actor source, int id, int ticks) {
        this.type = type;
        this.source = source;
        this.id = id;
        this.ticks = ticks;
    }
}
