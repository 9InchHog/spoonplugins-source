package net.runelite.client.plugins.spawnpredictor.util;

public class FightCavesNpcSpawn {
    private final FightCavesNpc npc;

    private final int spawnLocation;

    public FightCavesNpcSpawn(FightCavesNpc npc, int spawnLocation) {
        this.npc = npc;
        this.spawnLocation = spawnLocation;
    }

    public FightCavesNpc getNpc() {
        return this.npc;
    }

    public int getSpawnLocation() {
        return this.spawnLocation;
    }

    public String toString() {
        return String.format("%s -> %d", new Object[] { this.npc.getName(), Integer.valueOf(this.spawnLocation) });
    }
}
