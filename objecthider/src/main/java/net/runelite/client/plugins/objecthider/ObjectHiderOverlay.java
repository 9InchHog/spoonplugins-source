package net.runelite.client.plugins.objecthider;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

/**
 * ObjectHiderOverlay adds a magenta colored border around the currently
 * highlighted tile when in selection mode.
 */
public class ObjectHiderOverlay extends Overlay {
    private final Client client;
    private final ObjectHiderPlugin plugin;

    @Inject
    private ObjectHiderOverlay(Client client, ObjectHiderPlugin plugin, ObjectHiderConfig config) {
        this.client = client;
        this.plugin = plugin;
        // position, layer + priority copied from `plugins.tileindicator`
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.selectGroundObjectMode && client.getSelectedSceneTile() != null) {
            // create a polygon
            final Polygon poly = Perspective.getCanvasTilePoly(client, client.getSelectedSceneTile().getLocalLocation());
            if (poly != null) {
                // and render it
                OverlayUtil.renderPolygon(graphics, poly, Color.MAGENTA);
            }
        }
        return null;
    }
}
