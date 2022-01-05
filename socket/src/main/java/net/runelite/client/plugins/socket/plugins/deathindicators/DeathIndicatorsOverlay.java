package net.runelite.client.plugins.socket.plugins.deathindicators;
import net.runelite.api.NPC;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

public class DeathIndicatorsOverlay extends Overlay
{

    private final DeathIndicatorsConfig config;
    private final DeathIndicatorsPlugin plugin;
    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    public DeathIndicatorsOverlay(DeathIndicatorsPlugin plugin, DeathIndicatorsConfig config, ModelOutlineRenderer modelOutlineRenderer)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if(config.showOutline())
        {
            for (NPC n : plugin.getDeadNylos())
            {
                //Shape objectClickbox = n.getConvexHull();
                //renderPoly(graphics, Color.red, objectClickbox);
                this.modelOutlineRenderer.drawOutline(n, 2, Color.red, 4);
            }
        }
        /*if(plugin.getMaidenNPC() != null && config.maidenMarkers())
        {
            Color maidenColor;
            if(plugin.getMaidenNPC().phase == 0)
            {
                maidenColor = Color.WHITE;
            }
            else if(plugin.getMaidenNPC().phase == 1)
            {
                maidenColor = Color.BLACK;
            }
            else if(plugin.getMaidenNPC().phase == 2)
            {
                maidenColor = Color.GREEN;
            }
            else
            {
                maidenColor = Color.BLUE;
            }
            Shape objectClickbox = plugin.getMaidenNPC().npc.getConvexHull();
            renderPoly(graphics, maidenColor, objectClickbox);
        }*/
        return null;
    }

    private void renderPoly(Graphics2D graphics, Color color, Shape polygon)
    {
        if (polygon != null)
        {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            graphics.fill(polygon);
        }
    }
}
