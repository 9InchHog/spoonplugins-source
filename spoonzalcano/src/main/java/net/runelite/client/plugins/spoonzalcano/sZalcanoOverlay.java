package net.runelite.client.plugins.spoonzalcano;

import java.awt.*;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Projectile;
import net.runelite.api.Tile;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class sZalcanoOverlay extends Overlay {
    private final Client client;

    private final sZalcanoPlugin plugin;

    private final sZalcanoConfig config;

    @Inject
    private sZalcanoOverlay(Client client, sZalcanoPlugin plugin, sZalcanoConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        for (GameObject obj : this.plugin.aqewsBeyblades.keySet()) {
            Tile tile = this.plugin.aqewsBeyblades.get(obj);
            if (tile.getPlane() == this.client.getPlane() && this.plugin.correctRegion) {
                Polygon tilePoly = null;
                if (obj.getId() == 36199) {
                    if (this.config.dangerousTiles()) {
                        tilePoly = Perspective.getCanvasTileAreaPoly(this.client, obj.getLocalLocation(), 3);
                        graphics.setColor(this.config.dangerousTileColor());
                        if (tilePoly == null)
                            continue;
                        graphics.drawPolygon(tilePoly);
                    }
                    continue;
                }
                if (obj.getId() == 36200 &&
                        this.config.beybladeTimer()) {
                    String textOverlay = Integer.toString(this.plugin.tickCounterForBeyblades);
                    Point textLoc = Perspective.getCanvasTextLocation(this.client, graphics, obj.getLocalLocation(), textOverlay, 0);
                    if (textLoc == null)
                        continue;
                    Font oldFont = graphics.getFont();
                    graphics.setFont(new Font("Arial", 1, 20));
                    Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
                    OverlayUtil.renderTextLocation(graphics, pointShadow, textOverlay, Color.BLACK);
                    OverlayUtil.renderTextLocation(graphics, textLoc, textOverlay, Color.YELLOW);
                    graphics.setFont(oldFont);
                }
            }
        }
        if (this.config.glowingRock())
            for (GameObject obj : this.plugin.glowingRock.keySet()) {
                Tile tile = this.plugin.glowingRock.get(obj);
                if (tile.getPlane() == this.client.getPlane() && this.plugin.correctRegion) {
                    Shape poly = obj.getConvexHull();
                    if (poly == null)
                        continue;
                    //graphics.setColor(this.config.glowingRockColour());
                    //graphics.draw(poly);
                    Color fillColor = new Color(this.config.glowingRockColour().getRed(), this.config.glowingRockColour().getGreen(),
                            this.config.glowingRockColour().getBlue(), 50);
                    graphics.setColor(fillColor);
                    graphics.fill(poly);
                    if (!this.plugin.safeOnRock) {
                        //graphics.setColor(this.config.glowingRockExplosionColour());
                        //graphics.draw(poly);
                        Color rockGoBoom = new Color(
                                this.config.glowingRockExplosionColour().getRed(), this.config.glowingRockExplosionColour().getGreen(),
                                this.config.glowingRockExplosionColour().getBlue(), 50);
                        graphics.setColor(rockGoBoom);
                        graphics.fill(poly);
                    }
                }
            }
        if (this.config.golem() &&
                this.plugin.correctRegion) {
            for (Projectile p : this.plugin.golem.keySet()) {
                Polygon tilepoly = Perspective.getCanvasTilePoly(this.client, this.plugin.golem.get(p));
                if (tilepoly == null)
                    continue;
                graphics.setColor(new Color(this.config.golemColor().getRed(), this.config.golemColor().getGreen(), this.config.golemColor().getBlue(), 255));
                graphics.drawPolygon(tilepoly);
                graphics.setColor(new Color(this.config.golemColor().getRed(), this.config.golemColor().getGreen(), this.config.golemColor().getBlue(), 50));
                graphics.fillPolygon(tilepoly);
            }
            if (this.plugin.golemNPC != null) {
                Shape npcPoly = this.plugin.golemNPC.getConvexHull();
                //graphics.setColor(this.config.golemColor());
                //graphics.draw(npcPoly);
                Color fillColor = new Color(this.config.golemColor().getRed(), this.config.golemColor().getGreen(), this.config.golemColor().getBlue(), 50);
                graphics.setColor(fillColor);
                graphics.fill(npcPoly);
            }
        }
        for (GraphicsObject obj : this.plugin.fallingRocks) {
            if (this.plugin.correctRegion)
                if (this.config.fallingRocks())
                    if (this.plugin.fallingRocks.size() > 0) {
                        Polygon tilepoly = Perspective.getCanvasTilePoly(this.client, obj.getLocation());
                        if (tilepoly == null)
                            continue;
                        graphics.setColor(new Color(this.config.fallingRocksColor().getRed(), this.config.fallingRocksColor().getGreen(), this.config.fallingRocksColor().getBlue(), 255));
                        graphics.drawPolygon(tilepoly);
                        graphics.setColor(new Color(this.config.fallingRocksColor().getRed(), this.config.fallingRocksColor().getGreen(), this.config.fallingRocksColor().getBlue(), 50));
                        graphics.fillPolygon(tilepoly);
                    }
        }
        return null;
    }
}
