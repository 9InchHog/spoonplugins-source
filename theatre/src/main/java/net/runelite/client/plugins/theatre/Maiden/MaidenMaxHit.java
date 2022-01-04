package net.runelite.client.plugins.theatre.Maiden;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

public enum MaidenMaxHit {
    OFF("Off"),
    REGULAR("Regular"),
    ELY("Elysian"),
    BOTH("Both");

    private final String name;

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    private MaidenMaxHit(String name) {
        this.name = name;
    }
}
