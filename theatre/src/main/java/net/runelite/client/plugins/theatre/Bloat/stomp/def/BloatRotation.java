//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.runelite.client.plugins.theatre.Bloat.stomp.def;

public enum BloatRotation {
    CLOCKWISE,
    COUNTER_CLOCKWISE,
    UNKNOWN;

    BloatRotation() {
    }

    public boolean isClockwise() {
        return this == CLOCKWISE;
    }
}
