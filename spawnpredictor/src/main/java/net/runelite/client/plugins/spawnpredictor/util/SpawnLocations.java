package net.runelite.client.plugins.spawnpredictor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SpawnLocations {
    NW(10, 50),
    C(30, 30),
    SE(50, 25),
    S(35, 15),
    SW(10, 15);

    private static final Logger log;

    private final int[] regionXY;

    static {
        log = LoggerFactory.getLogger(SpawnLocations.class);
    }

    public int[] getRegionXY() {
        return this.regionXY;
    }

    SpawnLocations(int... regionXY) {
        this.regionXY = regionXY;
    }

    public static SpawnLocations lookup(int sVal) {
        switch (sVal) {
            case 3:
            case 7:
            case 12:
                return NW;
            case 2:
            case 8:
            case 13:
                return C;
            case 0:
            case 5:
            case 9:
                return SE;
            case 6:
            case 11:
            case 14:
                return S;
            case 1:
            case 4:
            case 10:
                return SW;
        }
        log.warn("Invalid sVal -> {}", sVal);
        return null;
    }
}
