package net.runelite.client.plugins.azscreenmarkers;

import net.runelite.client.input.MouseAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ScreenMarkerMouseListener extends MouseAdapter {
    private final azScreenMarkerPlugin plugin;

    ScreenMarkerMouseListener(azScreenMarkerPlugin plugin) {
        this.plugin = plugin;
    }

    public MouseEvent mouseClicked(MouseEvent event) {
        if (SwingUtilities.isMiddleMouseButton(event))
            return event;
        event.consume();
        return event;
    }

    public MouseEvent mousePressed(MouseEvent event) {
        if (SwingUtilities.isMiddleMouseButton(event))
            return event;
        if (SwingUtilities.isLeftMouseButton(event)) {
            Rectangle bounds = this.plugin.getSelectedWidgetBounds();
            if (bounds != null) {
                this.plugin.startCreation(bounds.getLocation(), bounds.getSize());
            } else {
                this.plugin.startCreation(event.getPoint());
            }
        } else if (this.plugin.isCreatingScreenMarker()) {
            this.plugin.finishCreation(true);
        }
        event.consume();
        return event;
    }

    public MouseEvent mouseReleased(MouseEvent event) {
        if (SwingUtilities.isMiddleMouseButton(event))
            return event;
        if (SwingUtilities.isLeftMouseButton(event) && this.plugin.isCreatingScreenMarker())
            this.plugin.completeSelection();
        event.consume();
        return event;
    }

    public MouseEvent mouseDragged(MouseEvent event) {
        if (!this.plugin.isCreatingScreenMarker())
            return event;
        if (SwingUtilities.isLeftMouseButton(event))
            this.plugin.resizeMarker(event.getPoint());
        return event;
    }
}
