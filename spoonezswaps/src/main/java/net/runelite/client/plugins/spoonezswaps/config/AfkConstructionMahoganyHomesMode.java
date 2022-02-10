package net.runelite.client.plugins.spoonezswaps.config;

import lombok.Getter;

import java.awt.event.KeyEvent;

@Getter
public enum AfkConstructionMahoganyHomesMode {
    TEAK_HOMES(KeyEvent.VK_3),
    MAHOGANY_HOMES(KeyEvent.VK_4);

    private final int key;

    AfkConstructionMahoganyHomesMode(int keyEvent) {
        this.key = keyEvent;
    }

    public int getKey() {
        return key;
    }
}
