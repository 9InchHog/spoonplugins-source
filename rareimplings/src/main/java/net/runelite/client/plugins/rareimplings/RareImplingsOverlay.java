package net.runelite.client.plugins.rareimplings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class RareImplingsOverlay extends Overlay {
    private final Client client;

    private final RareImplingsConfig config;

    @Inject
    RareImplingsOverlay(Client client, RareImplingsPlugin plugin, RareImplingsConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    public Dimension render(Graphics2D g) {
        WorldPoint wp = this.client.getLocalPlayer().getWorldLocation();
        if (this.config.show() && wp.getRegionID() == 10307)
            for (NPC npc : this.client.getNpcs()) {
                String desc;
                int index = npc.getIndex();
                if (npc.getName() == null || "null".equals(npc.getName())) {
                    desc = "null";
                } else {
                    desc = npc.getName().replace(" impling", "");
                }
                if (index == 13051) {
                    renderNpcOverlay(g, npc, index + " NW (" + desc + ")", desc, Color.RED);
                    continue;
                }
                if (index == 13100) {
                    renderNpcOverlay(g, npc, index + " SE (" + desc + ")", desc, Color.RED);
                    continue;
                }
                if (index <= 20000)
                    continue;
                renderNpcOverlay(g, npc, index + " DYN (" + desc + ")", desc, Color.RED);
            }
        return null;
    }

    private void renderNpcOverlay(Graphics2D graphics, NPC actor, String text, String desc, Color color) {
        int size = 1;
        LocalPoint lp = actor.getLocalLocation();
        Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, 1);
        renderPoly(graphics, tilePoly);
        Point textLocation = actor.getCanvasTextLocation(graphics, text, actor.getLogicalHeight() + 40);
        if (textLocation != null)
            OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
        Point impLocation = actor.getMinimapLocation();
        if (impLocation == null || color == null)
            return;
        OverlayUtil.renderMinimapLocation(graphics, impLocation, color);
        textLocation = new Point(impLocation.getX() + 1, impLocation.getY());
        OverlayUtil.renderTextLocation(graphics, textLocation, desc, color);
    }

    private void renderPoly(Graphics2D graphics, Shape polygon) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        if (polygon != null) {
            graphics.setColor(Color.WHITE);
            graphics.setStroke(new BasicStroke(1.0F));
            graphics.draw(polygon);
        }
    }
}
