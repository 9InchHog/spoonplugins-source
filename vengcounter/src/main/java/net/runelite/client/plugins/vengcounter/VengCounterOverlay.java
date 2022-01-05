package net.runelite.client.plugins.vengcounter;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.*;
import java.util.ArrayList;
import java.util.Map;

public class VengCounterOverlay extends OverlayPanel
{

    private VengCounterPlugin plugin;
    private VengCounterConfig config;
    private Client client;

    @Inject
    public VengCounterOverlay(VengCounterPlugin plugin,Client client,VengCounterConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setPosition(OverlayPosition.DETACHED);
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        this.plugin = plugin;
        this.client = client;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, "Reset", "Veng counter"));
    }


    @Override
    public Dimension render(Graphics2D g)
    {
        List<LayoutableRenderableEntity> elems = panelComponent.getChildren();
        elems.clear();
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(plugin.activity.entrySet());

        if (list.size() != 0) elems.add(TitleComponent.builder().text("Veng counter").color(config.titleColor()).build());
        int total = 0;
        for (Map.Entry<String, Integer> e : list)
        {
            total += e.getValue();
            if(e.getKey().equals(client.getLocalPlayer().getName()))
            {
                elems.add(LineComponent.builder().leftColor(config.selfColor()).rightColor(config.selfColor()).left(e.getKey()).right(e.getValue().toString()).build());
            }
            else
            {
                elems.add(LineComponent.builder().left(e.getKey()).right(e.getValue().toString()).leftColor(config.otherColor()).rightColor(config.otherColor()).build());

            }
        }
        if (config.totalEnabled())
        {
            if (list.size() != 0) elems.add(LineComponent.builder().left("Total").leftColor(config.totalColor()).rightColor(config.totalColor()).right(String.valueOf(total)).build());
        }
        return super.render(g);
    }
}
