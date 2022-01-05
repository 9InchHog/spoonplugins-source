package net.runelite.client.plugins.spawnpredictor.util;

public enum FightCavesNpc {
    BAT("Bat", 1),
    BLOB("Blob", 2),
    RANGE("Ranger", 3),
    MELEE("Meleer", 4),
    MAGE("Mager", 5),
    JAD("Jad", 5);

    private final String name;

    private final int size;

    FightCavesNpc(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.size;
    }
}
