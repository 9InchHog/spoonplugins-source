package net.runelite.client.plugins.spoonsepulchre;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class SepulchreTrueTileOverlay extends Overlay {
    private static final int CROSSBOW_STATUE_ANIM_DEFAULT = 8681;
    private static final int CROSSBOW_STATUE_ANIM_FINAL = 8685;

    private final Client client;
    private final SpoonSepulchrePlugin plugin;
    private final SpoonSepulchreConfig config;

    private Player player;

    @Inject
    SepulchreTrueTileOverlay(final Client client, final SpoonSepulchrePlugin plugin, final SpoonSepulchreConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(final Graphics2D graphics2D) {
        if (!plugin.isPlayerInSepulchre()) {
            return null;
        }

        player = client.getLocalPlayer();

        if (player == null) {
            return null;
        }

        renderServerTile(graphics2D);

        return null;
    }

    private void renderServerTile(final Graphics2D graphics2D) {
        if (!config.highlightServerTile()) {
            return;
        }

        final WorldPoint worldPoint = player.getWorldLocation();

        if (worldPoint == null) {
            return;
        }

        final LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);

        if (localPoint == null) {
            return;
        }

        final Polygon polygon = Perspective.getCanvasTilePoly(client, localPoint);

        if (polygon == null) {
            return;
        }

        drawStrokeAndFill(graphics2D, config.serverTileOutlineColor(), config.serverTileFillColor(), config.tileOutlineWidth(), polygon);
    }

    private static void drawStrokeAndFill(final Graphics2D graphics2D, final Color outlineColor, final Color fillColor, final float strokeWidth, final Shape shape) {
        graphics2D.setColor(outlineColor);
        final Stroke originalStroke = graphics2D.getStroke();
        graphics2D.setStroke(new BasicStroke(strokeWidth));
        graphics2D.draw(shape);
        graphics2D.setColor(fillColor);
        graphics2D.fill(shape);
        graphics2D.setStroke(originalStroke);
    }
}
