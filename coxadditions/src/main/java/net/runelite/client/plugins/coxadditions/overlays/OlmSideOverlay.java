package net.runelite.client.plugins.coxadditions.overlays;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class OlmSideOverlay extends Overlay {
    Client client;

    CoxAdditionsConfig config;

    CoxAdditionsPlugin plugin;

    @Inject
    public OlmSideOverlay(Client client, CoxAdditionsConfig config, CoxAdditionsPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        if (this.config.olmSide() && this.plugin.getOlmTile() != null) {
            LocalPoint lp = this.plugin.getOlmTile();
            if (lp != null){
                WorldPoint wp = WorldPoint.fromLocal(this.client, lp);
                drawTile(graphics, wp, config.olmSideColor(), 1, 255, 10);
            }
        }
        return null;
    }

    protected void drawTile(Graphics2D graphics, WorldPoint point, Color color, int strokeWidth, int outlineAlpha, int fillAlpha) {
        if(this.client.getLocalPlayer() != null) {
            WorldPoint playerLocation = this.client.getLocalPlayer().getWorldLocation();
            if (point.distanceTo(playerLocation) < 32) {
                LocalPoint lp = LocalPoint.fromWorld(this.client, point);
                if (lp != null) {
                    Polygon poly = Perspective.getCanvasTilePoly(this.client, lp);
                    if (poly != null) {
                        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
                        graphics.setStroke(new BasicStroke(strokeWidth));
                        graphics.draw(poly);
                        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
                        graphics.fill(poly);
                    }
                }
            }
        }
    }
}
