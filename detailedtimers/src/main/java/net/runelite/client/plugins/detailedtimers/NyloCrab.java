package net.runelite.client.plugins.detailedtimers;

import net.runelite.api.NPC;

public class NyloCrab {
    public NPC npc;

    public int ticksAlive;

    public int startingID;

    NyloCrab(NPC npc) {
        this.npc = npc;
        this.ticksAlive = 0;
        this.startingID = this.npc.getId();
    }
}
