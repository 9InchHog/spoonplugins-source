package net.runelite.client.plugins.coxadditions.overlays;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

public class CoxItemOverlay extends WidgetItemOverlay {
    private final Client client;

    private final ItemManager itemManager;

    private final CoxAdditionsPlugin plugin;

    private final CoxAdditionsConfig config;

    @Inject
    public CoxItemOverlay(final Client client, ItemManager itemManager, CoxAdditionsPlugin plugin, CoxAdditionsConfig config) {
        this.client = client;
        this.itemManager = itemManager;
        this.plugin = plugin;
        this.config = config;
        showOnInterfaces(271, 551);
    }

    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget) {
        if(this.client.getVarbitValue(Varbits.IN_RAID) == 1) {
            if(config.highlightChest() != CoxAdditionsConfig.HighlightChestMode.OFF) {
                if (!this.config.highlightChestItems().equals("") && plugin.chestHighlightIdList.size() > 0) {
                    if (plugin.chestHighlightIdList.contains(itemId)) {
                        if(config.highlightChest() == CoxAdditionsConfig.HighlightChestMode.UNDERLINE) {
                            underlineItem(graphics, itemId, itemWidget, this.config.highlightChestItemsColor());
                        }else if(config.highlightChest() == CoxAdditionsConfig.HighlightChestMode.OUTLINE) {
                            highlightItem(graphics, itemId, itemWidget, this.config.highlightChestItemsColor());
                        }
                    }
                }

                if (!this.config.highlightChestItems2().equals("") && plugin.chestHighlightIdList2.size() > 0) {
                    if (plugin.chestHighlightIdList2.contains(itemId)) {
                        if(config.highlightChest() == CoxAdditionsConfig.HighlightChestMode.UNDERLINE) {
                            underlineItem(graphics, itemId, itemWidget, this.config.highlightChestItemsColor2());
                        }else if(config.highlightChest() == CoxAdditionsConfig.HighlightChestMode.OUTLINE) {
                            highlightItem(graphics, itemId, itemWidget, this.config.highlightChestItemsColor2());
                        }
                    }
                }
            }
        }
    }

    private void highlightItem(Graphics2D graphics, int itemId, WidgetItem itemWidget, Color color) {
        Rectangle bounds = itemWidget.getCanvasBounds();
        BufferedImage outline = this.itemManager.getItemOutline(itemId, itemWidget.getQuantity(), color);
        graphics.drawImage(outline, (int)bounds.getX(), (int)bounds.getY(), (ImageObserver)null);
    }

    private void underlineItem(Graphics2D graphics, int itemId, WidgetItem itemWidget, Color color) {
        Rectangle bounds = itemWidget.getCanvasBounds();
        int heightOffSet = (int) bounds.getY() + (int) bounds.getHeight() + 2;
        graphics.setColor(color);
        graphics.drawLine((int) bounds.getX(), heightOffSet, (int) bounds.getX() + (int) bounds.getWidth(), heightOffSet);
    }
}
