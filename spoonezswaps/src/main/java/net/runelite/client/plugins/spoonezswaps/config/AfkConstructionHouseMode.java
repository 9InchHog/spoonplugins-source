package net.runelite.client.plugins.spoonezswaps.config;

import lombok.Getter;

import java.awt.event.KeyEvent;

public enum AfkConstructionHouseMode {
    MAHOGANY_TABLES("Mahogany Tables", KeyEvent.VK_6),
    GNOME_BENCH("Gnome Bench", KeyEvent.VK_2),
    TEAK_BENCH("Teak Bench", KeyEvent.VK_1),
    MYTH_CAPE("Myth Cape", KeyEvent.VK_4),
    OAK_DOOR("Oak Dungeon Door", KeyEvent.VK_1),
    OAK_LARDER("Oak Larder", KeyEvent.VK_2);

    @Getter
    private final String name;

    @Getter
    private final int key;

    public String toString() {
        return this.name;
    }

    AfkConstructionHouseMode(String name, int key) {
        this.name = name;
        this.key = key;
    }
}