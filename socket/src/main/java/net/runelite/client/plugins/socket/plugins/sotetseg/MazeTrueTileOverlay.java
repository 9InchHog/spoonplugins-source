package net.runelite.client.plugins.socket.plugins.sotetseg;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class MazeTrueTileOverlay extends Overlay {
    private final Client client;

    private final SotetsegConfig config;

    private final SotetsegPlugin plugin;

    private final SotetsegOverlay overlay;

    @Inject
    private MazeTrueTileOverlay(Client client, SotetsegConfig config, SotetsegPlugin plugin, SotetsegOverlay overlay) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.overlay = overlay;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGHEST);
    }

    public Dimension render(Graphics2D graphics) {
        if (config.trueMaze()) {
            if (plugin.isSotetsegActive() && !plugin.mazePings.isEmpty()) {
                WorldPoint playerPos = client.getLocalPlayer().getWorldLocation();
                if (playerPos == null)
                    return null;
                LocalPoint playerPosLocal = LocalPoint.fromWorld(client, playerPos);
                if (playerPosLocal == null)
                    return null;
                renderTile(graphics, playerPosLocal, config.trueMazeColor());
            }
        }
        return null;
    }

    private void renderTile(Graphics2D graphics, LocalPoint dest, Color color) {
        if (dest == null)
            return;
        Polygon poly = Perspective.getCanvasTilePoly(client, dest);
        if (poly == null)
            return;
        if (config.antiAlias()) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke((float) config.trueMazeThicc()));
        graphics.draw(poly);
        graphics.setColor(new Color(0, 0, 0, 50));
    }
}
