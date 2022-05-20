package net.runelite.client.plugins.spawnpredictor.util;

import lombok.Getter;

public class FightCavesNpcSpawn {
    @Getter
    private final FightCavesNpc npc;

    @Getter
    private final int spawnLocation;

    public FightCavesNpcSpawn(FightCavesNpc npc, int spawnLocation) {
        this.npc = npc;
        this.spawnLocation = spawnLocation;
    }

    public String toString() {
        return String.format("%s -> %d", npc.getName(), spawnLocation);
    }
}
