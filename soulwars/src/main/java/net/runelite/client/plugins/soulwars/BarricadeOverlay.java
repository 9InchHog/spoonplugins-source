package net.runelite.client.plugins.soulwars;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class BarricadeOverlay extends Overlay {
    private final Client client;
    private final SoulWarsPlugin plugin;
    private final SoulWarsConfig config;

    @Inject
    private BarricadeOverlay(final Client client, final SoulWarsPlugin plugin, final SoulWarsConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (this.config.barricadeHighlight() && this.plugin.barricadesActive)
            highlightBarricades(graphics);
        return null;
    }

    private void highlightBarricades(Graphics2D graphics) {
        for (NPC npc : this.plugin.barricades) {
            int npcId = npc.getId();
            Color color;
            if (npcId == 10539) {
                color = new Color(0, 0, 255);
            } else if (npcId == 10540) {
                color = new Color(255, 0, 0);
            } else {
                continue;
            }
            LocalPoint lp = npc.getLocalLocation();
            Polygon poly = Perspective.getCanvasTilePoly(this.client, lp);
            renderPoly(graphics, color, poly, this.config.barricadesThiCC());
        }
    }

    private void renderPoly(Graphics2D graphics, Color color, Shape polygon, int width) {
        if (polygon != null) {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(width));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
            graphics.fill(polygon);
        }
    }
}
