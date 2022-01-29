package net.runelite.client.plugins.corpboost;

import lombok.AccessLevel;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static net.runelite.api.ItemID.CANNONBALL;

public class CorpCannonOverlay extends Overlay {
    private static final int MAX_DISTANCE = 2350;

    private final Client client;
    private final CorpBoostPlugin plugin;
    private final CorpBoostConfig config;

    @Inject
    private ItemManager itemManager;

    @Setter(AccessLevel.PACKAGE)
    private boolean hidden;

    @Inject
    CorpCannonOverlay(Client client, CorpBoostPlugin plugin, CorpBoostConfig config) {
        setPosition(OverlayPosition.DYNAMIC);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        List<WorldPoint> spotPoints = plugin.getCannonSpotPoints();

        if (hidden || spotPoints.isEmpty() || !config.customer() || plugin.isCannonPlaced()) {
            return null;
        }

        for (WorldPoint spot : spotPoints) {
            if (spot.getPlane() != client.getPlane()) {
                continue;
            }

            LocalPoint spotPoint = LocalPoint.fromWorld(client, spot);
            LocalPoint localLocation = client.getLocalPlayer().getLocalLocation();

            if (spotPoint != null && localLocation.distanceTo(spotPoint) <= MAX_DISTANCE) {
                renderCannonSpot(graphics, client, spotPoint, itemManager.getImage(CANNONBALL), config.customerTileColor());
            }
        }

        return null;
    }

    private void renderCannonSpot(Graphics2D graphics, Client client, LocalPoint point, BufferedImage image, Color color) {
        //Render tile
        Polygon poly = Perspective.getCanvasTilePoly(client, point);

        if (poly != null) {
            renderTile(graphics, poly, color, config.tileWidth(), config.tileFillOpacity(), color.getAlpha());
        }

        //Render icon
        Point imageLoc = Perspective.getCanvasImageLocation(client, point, image, 0);

        if (imageLoc != null) {
            OverlayUtil.renderImageLocation(graphics, imageLoc, image);
        }
    }

    private void renderTile(Graphics2D graphics, Shape polygon, Color color, final double borderWidth, int opacity, int outlineAlpha) {
        if (polygon == null)
            return;
        if (borderWidth == 0) {
            outlineAlpha = 0;
        }
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
        graphics.setStroke(new BasicStroke((float) borderWidth));
        graphics.draw(polygon);
        graphics.setColor(new Color(0, 0, 0, opacity));
        graphics.fill(polygon);
    }
}
