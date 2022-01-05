package net.runelite.client.plugins.phoenixnecklace;

import net.runelite.api.*;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;


public class RockCakeOverlay extends WidgetItemOverlay {
    private final ItemManager itemManager;
    private final PhoenixNecklacePlugin plugin;
    private final PhoenixNecklaceConfig config;

    @Inject
    private RockCakeOverlay(ItemManager itemManager, PhoenixNecklacePlugin plugin, PhoenixNecklaceConfig config) {
        this.itemManager = itemManager;
        this.plugin = plugin;
        this.config = config;
        showOnInventory();
        showOnEquipment();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget) {
        if(config.rockCake() != PhoenixNecklaceConfig.RockCakeMode.OFF && plugin.pneckEquipped) {
            if (plugin.currentHp - plugin.cakeDamage < plugin.maxHp * .2) {
                if (itemId == 7510) {
                    highlightItem(graphics, itemId, itemWidget, config.cakeColor());
                }
            }
        }
    }

    private void highlightItem(Graphics2D graphics, int itemId, WidgetItem itemWidget, Color color)
    {
        final Rectangle bounds = itemWidget.getCanvasBounds();

        if(config.rockCake() == PhoenixNecklaceConfig.RockCakeMode.OVERLAY) {
            ItemComposition item = itemManager.getItemComposition(itemId);
            final BufferedImage image = itemManager.getImage(itemId, itemWidget.getQuantity(), item.isStackable());
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), config.cakeOpacity());
            final BufferedImage overlayedImage = overlay(image, color);
            graphics.drawImage(overlayedImage, (int) bounds.getX(), (int) bounds.getY(), null);
        } else if(config.rockCake() == PhoenixNecklaceConfig.RockCakeMode.OUTLINE) {
            final BufferedImage outline = itemManager.getItemOutline(itemId, itemWidget.getQuantity(), color);
            graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);
        }
    }

    private BufferedImage overlay(BufferedImage image, Color color)
    {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage overlayed = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = overlayed.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setColor(color);
        g.fillRect(0, 0, w, h);
        g.dispose();
        return overlayed;
    }
}
