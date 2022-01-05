package net.runelite.client.plugins.spawnpredictor.util;

import java.util.EnumSet;
import java.util.HashMap;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum StartLocations {
    _1(1, 2, 4),
    _2(2, 13, 0),
    _3(3, 8, 10),
    _4(4, 12, 14),
    _5(5, 7, 9),
    _6(6, 3, 5),
    _7(7, 6, 14),
    _8(8, 6, 0),
    _9(9, 14, 1),
    _10(10, 5, 7),
    _11(11, 0, 2),
    _12(12, 9, 11),
    _13(13, 1, 3),
    _14(14, 10, 12),
    _15(15, 4, 6);

    private static final Logger log;

    private final int r;

    private final int rsVal;

    private final int rfVal;

    private static final HashMap<Integer, Pair<Integer, Integer>> lookupMap;

    public int getR() {
        return this.r;
    }

    public int getRsVal() {
        return this.rsVal;
    }

    public int getRfVal() {
        return this.rfVal;
    }

    public static HashMap<Integer, Pair<Integer, Integer>> getLookupMap() {
        return lookupMap;
    }

    StartLocations(int r, int rsVal, int rfVal) {
        this.r = r;
        this.rsVal = rsVal;
        this.rfVal = rfVal;
    }

    public static int translateRotation(int r) {
        switch (r) {
            case 1:
                return 4;
            case 2:
                return 2;
            case 3:
                return 9;
            case 4:
                return 11;
            case 5:
                return 13;
            case 6:
                return 1;
            case 7:
                return 6;
            case 8:
                return 15;
            case 9:
                return 10;
            case 10:
                return 8;
            case 11:
                return 5;
            case 12:
                return 3;
            case 13:
                return 12;
            case 14:
                return 14;
            case 15:
                return 7;
        }
        log.warn("Invalid Rotation Column Number -> {}", Integer.valueOf(r));
        return -1;
    }

    static {
        log = LoggerFactory.getLogger(StartLocations.class);
        lookupMap = new HashMap<>();
        EnumSet.<StartLocations>allOf(StartLocations.class).forEach(n -> lookupMap.put(Integer.valueOf(n.getR()), new MutablePair(Integer.valueOf(n.getRsVal()), Integer.valueOf(n.getRfVal()))));
    }
}
