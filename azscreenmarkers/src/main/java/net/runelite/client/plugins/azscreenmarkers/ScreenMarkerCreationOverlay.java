package net.runelite.client.plugins.azscreenmarkers;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

class ScreenMarkerCreationOverlay extends Overlay {
    private final azScreenMarkerPlugin plugin;

    @Inject
    private ScreenMarkerCreationOverlay(azScreenMarkerPlugin plugin) {
        this.plugin = plugin;
        setPosition(OverlayPosition.DETACHED);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGH);
    }

    public Dimension render(Graphics2D graphics) {
        ScreenMarker marker = this.plugin.getCurrentMarker();
        if (marker == null)
            return null;
        int thickness = marker.getBorderThickness();
        int offset = thickness / 2;
        int width = (getBounds()).width - thickness;
        int height = (getBounds()).height - thickness;
        graphics.setStroke(createStripedStroke(thickness));
        graphics.setColor(marker.getColor());
        graphics.drawRect(offset, offset, width, height);
        return getBounds().getSize();
    }

    private Stroke createStripedStroke(int thickness) {
        return new BasicStroke(thickness, 0, 2, 0.0F, new float[] { 9.0F }, 0.0F);
    }
}
