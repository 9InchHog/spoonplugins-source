package net.runelite.client.plugins.bonylo;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.*;

class BoNyloTileOverlay extends Overlay {
    private static final Logger log = LoggerFactory.getLogger(BoNyloTileOverlay.class);
    private final Client client;
    private final BoNyloConfig config;
    private final BoNyloPlugin plugin;
    private Graphics2D graphics;

    @Inject
    private BoNyloTileOverlay(Client client, BoNyloPlugin plugin, BoNyloConfig config) {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        return null;
    }

    public void renderTile(Graphics2D graphics, NPC npc, Color col) {
        int size = npc.getComposition().getSize();
        Polygon poly = Perspective.getCanvasTileAreaPoly(client, npc.getLocalLocation(), size);
        if (poly != null) {
            graphics.setStroke(new BasicStroke((float) config.tileStrokeWidth()));
            graphics.setColor(col);
            graphics.draw(poly);
            if (config.enableTile()) {
                graphics.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), (int)Math.round((double) config.tileFillAlpha() * 2.55D)));
                graphics.fill(poly);
            }
        }

    }
}