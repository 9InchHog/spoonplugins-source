package net.runelite.client.plugins.vanguards;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class VanguardsHighlight extends Overlay
{

    private final VanguardsPlugin plugin;
    private final VanguardsConfig config;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    VanguardsHighlight(Client client, VanguardsPlugin plugin, VanguardsConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.config = config;
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        if(!plugin.inVangs){
            return null;
        }
        if(config.showTile()){
            if(plugin.ranger != null){
                renderNpcOverlay(graphics,plugin.ranger,"Range",Color.GREEN);
            }
            if(plugin.mager != null){
                renderNpcOverlay(graphics,plugin.mager,"Mage",Color.BLUE);
            }
            if(plugin.meleer != null){
                renderNpcOverlay(graphics,plugin.meleer,"Melee",Color.RED);
            }
        }
        return null;
    }


    private void renderNpcOverlay(Graphics2D graphics, NPC actor, String name, Color color)
    {

        Shape objectClickbox = actor.getConvexHull();

        renderPoly(graphics, color, objectClickbox);

    }

    private void renderPoly(Graphics2D graphics, Color color, Shape polygon)
    {
        if (polygon != null)
        {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(1));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
            graphics.fill(polygon);
        }
    }
}
