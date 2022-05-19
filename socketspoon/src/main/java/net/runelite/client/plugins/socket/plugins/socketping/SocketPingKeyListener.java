package net.runelite.client.plugins.socket.plugins.socketping;

import java.awt.event.KeyEvent;
import javax.inject.Inject;
import net.runelite.client.input.KeyListener;

public class SocketPingKeyListener implements KeyListener {
    final SocketPingConfig config;

    final SocketPing plugin;

    final SocketPingOverlay overlay;

    @Inject
    private SocketPingKeyListener(SocketPingConfig config, SocketPing plugin, SocketPingOverlay overlay) {
        this.config = config;
        this.plugin = plugin;
        this.overlay = overlay;
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        if (config.hotkey().matches(e))
            overlay.hotkeyPressed();
        if (config.hotkeyOmw().matches(e) || config.hotkeyQuestionMark().matches(e)
                || config.hotkeyAssistMe().matches(e) || config.hotkeyWarn().matches(e)) {
            overlay.setKeyheld(e);
            overlay.hotkeyPressed();
        }
    }

    public void keyReleased(KeyEvent e) {
        if (config.hotkey().matches(e))
            overlay.hotkeyReleased();
        if (config.hotkeyOmw().matches(e) || config
                .hotkeyQuestionMark().matches(e) || config
                .hotkeyAssistMe().matches(e) || config
                .hotkeyWarn().matches(e))
            overlay.reset();
    }
}