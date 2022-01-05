package net.runelite.client.plugins.spoonrunecraft;

import java.awt.event.KeyEvent;
import javax.inject.Inject;
import net.runelite.client.input.KeyListener;

public class ShiftWalkerInputListener implements KeyListener {
    @Inject
    private sRunecraftPlugin plugin;

    public void keyTyped(KeyEvent event) {}

    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == 16)
            this.plugin.setHotKeyPressed(true);
    }

    public void keyReleased(KeyEvent event) {
        if (event.getKeyCode() == 16)
            this.plugin.setHotKeyPressed(false);
    }
}
