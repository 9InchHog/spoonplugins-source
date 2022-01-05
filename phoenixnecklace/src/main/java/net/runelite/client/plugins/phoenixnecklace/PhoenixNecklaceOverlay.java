package net.runelite.client.plugins.phoenixnecklace;

import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PhoenixNecklaceOverlay extends Overlay {
    @Inject
    private ItemManager itemManager;
    private final Client client;
    private final PhoenixNecklacePlugin plugin;
    private final PhoenixNecklaceConfig config;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private PhoenixNecklaceOverlay(final Client client, final PhoenixNecklacePlugin plugin, final PhoenixNecklaceConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        if(plugin.inRegion) {
            Color infoboxColor;
            if(plugin.pneckEquipped){
                infoboxColor = new Color(0, 255, 0, 25);
            }else{
                infoboxColor = new Color(255, 0, 0, 25);
            }
            BufferedImage img = itemManager.getImage(11090);
            ImageComponent imgComp = new ImageComponent(img);
            this.panelComponent.getChildren().add(imgComp);
            this.panelComponent.setPreferredSize(new Dimension(24, 24));
            this.panelComponent.setBackgroundColor(infoboxColor);
        }
        return this.panelComponent.render(graphics);
    }
}
