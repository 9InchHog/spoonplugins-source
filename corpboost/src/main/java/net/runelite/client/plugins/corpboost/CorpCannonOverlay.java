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

        if (hidden || spotPoints.isEmpty() || !config.showCannon() || plugin.isCannonPlaced()) {
            return null;
        }

        for (WorldPoint spot : spotPoints) {
            if (spot.getPlane() != client.getPlane()) {
                continue;
            }

            LocalPoint spotPoint = LocalPoint.fromWorld(client, spot);
            LocalPoint localLocation = client.getLocalPlayer().getLocalLocation();

            if (spotPoint != null && localLocation.distanceTo(spotPoint) <= MAX_DISTANCE) {
                renderCannonSpot(graphics, client, spotPoint, itemManager.getImage(CANNONBALL), Color.RED);
            }
        }

        return null;
    }

    private void renderCannonSpot(Graphics2D graphics, Client client, LocalPoint point, BufferedImage image, Color color) {
        //Render tile
        Polygon poly = Perspective.getCanvasTilePoly(client, point);

        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, color);
        }

        //Render icon
        Point imageLoc = Perspective.getCanvasImageLocation(client, point, image, 0);

        if (imageLoc != null) {
            OverlayUtil.renderImageLocation(graphics, imageLoc, image);
        }
    }
}
