package net.runelite.client.plugins.spoonslayer;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class TargetOverlay extends Overlay {
    private final Client client;

    private final SpoonSlayerConfig config;

    private final SpoonSlayerPlugin plugin;

    private ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    TargetOverlay(Client client, SpoonSlayerConfig config, SpoonSlayerPlugin plugin, ModelOutlineRenderer modelOutlineRenderer) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        if (this.config.highlightTargets() == SpoonSlayerConfig.TileMode.OFF)
            return null;
        List<NPC> targets = this.plugin.getTargets();
        for (NPC target : targets) {
            if (this.config.highlightTargets() == SpoonSlayerConfig.TileMode.AREA) {
                renderTargetOverlay(graphics, target, this.config.getTargetColor());
                continue;
            }
            if (this.config.highlightTargets() == SpoonSlayerConfig.TileMode.TILE) {
                NPCComposition npcComp = target.getComposition();
                int size = npcComp.getSize();
                LocalPoint lp = target.getLocalLocation();
                Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.plugin.client, lp, size);
                renderPoly(graphics, this.config.getTargetColor(), tilePoly, config.targetThiCC());
                continue;
            }
            if (this.config.highlightTargets() == SpoonSlayerConfig.TileMode.HULL) {
                Shape objectClickbox = target.getConvexHull();
                if (objectClickbox != null) {
                    graphics.setColor(this.config.getTargetColor());
                    graphics.setStroke(new BasicStroke(this.config.targetThiCC()));
                    graphics.draw(objectClickbox);
                }
                continue;
            }
            if (this.config.highlightTargets() == SpoonSlayerConfig.TileMode.TRUE_LOCATIONS) {
                int size = 1;
                NPCComposition composition = target.getTransformedComposition();
                if (composition != null)
                    size = composition.getSize();
                LocalPoint lp = LocalPoint.fromWorld(this.client, target.getWorldLocation());
                if (lp != null) {
                    lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
                    Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
                    renderPoly(graphics, this.config.getTargetColor(), tilePoly, config.targetThiCC());
                }
                continue;
            }
            if (this.config.highlightTargets() == SpoonSlayerConfig.TileMode.SOUTH_WEST_TILE) {
                int size = 1;
                NPCComposition composition = target.getTransformedComposition();
                if (composition != null)
                    size = composition.getSize();
                LocalPoint localPoint = target.getLocalLocation();
                int x = localPoint.getX() - (size - 1) * 128 / 2;
                int y = localPoint.getY() - (size - 1) * 128 / 2;
                Polygon tilePoly = Perspective.getCanvasTilePoly(this.client, new LocalPoint(x, y));
                renderPoly(graphics, this.config.getTargetColor(), tilePoly, config.targetThiCC());
                continue;
            }
            if (this.config.highlightTargets() == SpoonSlayerConfig.TileMode.OUTLINE)
                this.modelOutlineRenderer.drawOutline(target, config.targetThiCC(), this.config.getTargetColor(), config.outlineFeather());
        }
        return null;
    }

    private void renderTargetOverlay(Graphics2D graphics, NPC actor, Color color) {
        Shape objectClickbox = actor.getConvexHull();
        if (objectClickbox != null) {
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), this.config.opacity()));
            graphics.fill(actor.getConvexHull());
        }
    }

    private void renderPoly(Graphics2D graphics, Color color, Shape polygon) {
        if (polygon != null) {
            if (this.config.antiAlias()) {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            } else
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2.0F));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), this.config.opacity()));
            graphics.fill(polygon);
        }
    }

    private void renderPoly(Graphics2D graphics, Color color, Polygon polygon, int width) {
        if (polygon != null) {
            if (this.config.antiAlias()) {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            } else
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            int outlineAlpha;
            if (config.targetThiCC() > 0) {
                outlineAlpha = 255;
            } else {
                outlineAlpha = 0;
            }
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
            graphics.setStroke(new BasicStroke(width));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), this.config.opacity()));
            graphics.fill(polygon);
        }
    }
}
