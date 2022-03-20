package net.runelite.client.plugins.spoonvm.overlays;

import net.runelite.api.Client;
import net.runelite.client.plugins.spoonvm.SpoonVMConfig;
import net.runelite.client.plugins.spoonvm.SpoonVMPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class Flash extends Overlay
{
    private final Client client;
    private final SpoonVMPlugin plugin;
    private final SpoonVMConfig config;
    private int timeout;

    @Inject
    private Flash(Client client, SpoonVMPlugin plugin, SpoonVMConfig config)
    {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(OverlayPriority.HIGH);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.isFlash() && (config.flashLowHp() || config.flashWarnings()))
        {
            final Color flash = graphics.getColor();
            graphics.setColor(config.flashColor());
            graphics.fill(new Rectangle(client.getCanvas().getSize()));
            graphics.setColor(flash);
            timeout++;
            if (timeout >= 50)
            {
                timeout = 0;
                plugin.setFlash(false);
            }
        }

        return null;
    }

}

