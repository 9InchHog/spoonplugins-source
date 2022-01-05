package net.runelite.client.plugins.spoontobstats;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class MaidenFlash extends Overlay {
    private final Client client;
    private final SpoonTobStatsPlugin plugin;
    private final SpoonTobStatsConfig config;
    private int timeout;

    @Inject
    private MaidenFlash(Client client, SpoonTobStatsPlugin plugin, SpoonTobStatsConfig config) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(OverlayPriority.HIGH);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        {
            if (plugin.isFlash() && config.flash())
            {
                final Color flash = graphics.getColor();
                graphics.setColor(new Color(255, 0, 0, 70));
                graphics.fill(new Rectangle(client.getCanvas().getSize()));
                graphics.setColor(flash);
                timeout++;
                if (timeout >= 50)
                {
                    timeout = 0;
                    plugin.setFlash(false);
                }
            }

        }
        return null;
    }

}
