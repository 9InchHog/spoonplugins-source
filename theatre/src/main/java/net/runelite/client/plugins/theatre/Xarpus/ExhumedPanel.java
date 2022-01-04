//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.runelite.client.plugins.theatre.Xarpus;

import net.runelite.client.plugins.theatre.TheatreConfig;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class ExhumedPanel extends OverlayPanel
{
    private final Xarpus plugin;
    private final TheatreConfig config;

    @Inject
    private ExhumedPanel(Xarpus plugin, TheatreConfig config)
    {
        this.plugin = plugin;
        this.config = config;
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    public Dimension render(Graphics2D graphics)
    {
        if (config.xarpusExhumedCountOverlay() && plugin.isXarpusActive() && plugin.getExhumedCounter() != null)
        {
            String count = Integer.toString(plugin.getExhumedCounter().getCount());
            panelComponent.getChildren().add(LineComponent.builder().left("Exhumeds:").leftColor(Color.WHITE).right(count).rightColor(Color.GREEN).build());

            return super.render(graphics);
        }
        else
        {
            return null;
        }
    }
}
