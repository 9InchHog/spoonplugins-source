package net.runelite.client.plugins.socket.plugins.socketping;

import java.awt.event.MouseEvent;
import javax.inject.Inject;
import net.runelite.client.input.MouseListener;

public class SocketPingMouseListener implements MouseListener {
    final SocketPingOverlay overlay;

    @Inject
    SocketPingMouseListener(SocketPingOverlay overlay) {
        this.overlay = overlay;
    }

    public MouseEvent mouseClicked(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    public MouseEvent mousePressed(MouseEvent mouseEvent) {
        return overlay.leftMousePressed(mouseEvent);
    }

    public MouseEvent mouseReleased(MouseEvent mouseEvent) {
        return overlay.leftMouseReleased(mouseEvent);
    }

    public MouseEvent mouseEntered(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    public MouseEvent mouseExited(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    public MouseEvent mouseDragged(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    public MouseEvent mouseMoved(MouseEvent mouseEvent) {
        return mouseEvent;
    }
}
