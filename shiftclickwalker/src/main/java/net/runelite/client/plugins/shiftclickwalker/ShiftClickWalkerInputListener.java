package net.runelite.client.plugins.shiftclickwalker;

import net.runelite.client.input.KeyListener;

import javax.inject.Inject;
import java.awt.event.KeyEvent;

public class ShiftClickWalkerInputListener implements KeyListener {
    @Inject
    private ShiftClickWalkerPlugin plugin;

    @Inject
    private ShiftClickWalkerConfig config;

    public void keyTyped(KeyEvent event) {}

    public void keyPressed(KeyEvent event) {
        if (config.hotkey().matches(event)) {
            this.plugin.setHotKeyPressed(true);
        }
    }

    public void keyReleased(KeyEvent event) {
        if (config.hotkey().matches(event)) {
            this.plugin.setHotKeyPressed(false);
        }
    }
}
