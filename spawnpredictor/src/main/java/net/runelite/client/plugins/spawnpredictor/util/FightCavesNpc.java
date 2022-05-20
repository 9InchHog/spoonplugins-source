package net.runelite.client.plugins.spawnpredictor.util;

import lombok.Getter;

public enum FightCavesNpc {
    BAT("Bat", 1),
    BLOB("Blob", 2),
    RANGE("Ranger", 3),
    MELEE("Meleer", 4),
    MAGE("Mager", 5),
    JAD("Jad", 5);

    @Getter
    private final String name;

    @Getter
    private final int size;

    FightCavesNpc(String name, int size) {
        this.name = name;
        this.size = size;
    }
}
