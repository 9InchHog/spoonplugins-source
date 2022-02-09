package net.runelite.client.plugins.socket.plugins.socketba;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SocketBAItemOverlay extends WidgetItemOverlay {
    private final Client client;

    private final ItemManager itemManager;

    private final SocketBAPlugin plugin;

    private final SocketBAConfig config;

    @Inject
    public SocketBAItemOverlay(final Client client, ItemManager itemManager, SocketBAPlugin plugin, SocketBAConfig config) {
        this.client = client;
        this.itemManager = itemManager;
        this.plugin = plugin;
        this.config = config;
        showOnInterfaces(164, 149);
    }

    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget) {
        if(this.client.getVar(Varbits.IN_GAME_BA) == 1) {
            if(config.correctItemHighlight() != SocketBAConfig.correctItemHighlightMode.OFF) {
                boolean highlight = false;
                if (plugin.role.equals("Defender")) {
                    if((itemId == 10513 && plugin.defCall.equals("Crackers")) || (itemId == 10514 && plugin.defCall.equals("Tofu")) || (itemId == 10515 && plugin.defCall.equals("Worms"))){
                        highlight = true;
                    }
                }else if (plugin.role.equals("Attacker")) {
                    if((plugin.attCall.toLowerCase().contains("aggressive") && (itemId == 22229 || (config.meleeSpecHighlight() && itemId == 23987)))
                        || (plugin.attCall.toLowerCase().contains("accurate") && (itemId == 22228 || (config.meleeSpecHighlight() && itemId == 21015)))
                        || (plugin.attCall.toLowerCase().contains("controlled") && (itemId == 22227 || (config.meleeSpecHighlight() && itemId == 23987)))
                        || (plugin.attCall.toLowerCase().contains("defensive") && (itemId == 22230 || (config.meleeSpecHighlight() && itemId == 23987)))){
                        highlight = true;
                    }
                }else if (plugin.role.equals("Healer")) {
                    if((itemId == 10539 && plugin.healCall.contains("Tofu")) || (itemId == 10540 && plugin.healCall.contains("Worms")) || (itemId == 10541 && plugin.healCall.contains("Meat"))){
                        highlight = true;
                    }
                }

                if(highlight) {
                    if (config.correctItemHighlight() == SocketBAConfig.correctItemHighlightMode.OUTLINE) {
                        highlightItem(graphics, itemId, itemWidget, this.config.correctItemColor());
                    } else if (config.correctItemHighlight() == SocketBAConfig.correctItemHighlightMode.UNDERLINE) {
                        underlineItem(graphics, itemId, itemWidget, this.config.correctItemColor());
                    }else if (config.correctItemHighlight() == SocketBAConfig.correctItemHighlightMode.BOX) {
                        drawBox(graphics, itemWidget.getCanvasLocation().getX(), itemWidget.getCanvasLocation().getY(), itemWidget.getCanvasBounds().height, itemWidget.getCanvasBounds().width);
                    }
                }
            }
        }
    }

    private void highlightItem(Graphics2D graphics, int itemId, WidgetItem itemWidget, Color color) {
        Rectangle bounds = itemWidget.getCanvasBounds();
        BufferedImage outline = this.itemManager.getItemOutline(itemId, itemWidget.getQuantity(), color);
        graphics.drawImage(outline, (int)bounds.getX(), (int)bounds.getY(), null);
    }

    private void underlineItem(Graphics2D graphics, int itemId, WidgetItem itemWidget, Color color) {
        Rectangle bounds = itemWidget.getCanvasBounds();
        int heightOffSet = (int) bounds.getY() + (int) bounds.getHeight() + 2;
        graphics.setColor(color);
        graphics.drawLine((int) bounds.getX(), heightOffSet, (int) bounds.getX() + (int) bounds.getWidth(), heightOffSet);
    }

    private void drawBox(Graphics2D graphics, int startX, int startY, int height, int width) {
        graphics.setColor(config.meleeStyleHighlightColor());
        graphics.setStroke(new BasicStroke(1));
        graphics.drawLine(startX, startY, startX + width, startY);
        graphics.drawLine(startX + width, startY, startX + width, startY + height);
        graphics.drawLine(startX + width, startY + height, startX, startY + height);
        graphics.drawLine(startX, startY + height, startX, startY);
    }
}
