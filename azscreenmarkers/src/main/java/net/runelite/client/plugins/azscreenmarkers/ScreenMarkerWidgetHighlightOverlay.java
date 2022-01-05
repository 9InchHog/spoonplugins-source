package net.runelite.client.plugins.azscreenmarkers;

import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

class ScreenMarkerWidgetHighlightOverlay extends Overlay {
    private final azScreenMarkerPlugin plugin;

    private final Client client;

    @Inject
    private ScreenMarkerWidgetHighlightOverlay(azScreenMarkerPlugin plugin, Client client) {
        this.plugin = plugin;
        this.client = client;
        setPosition(OverlayPosition.DETACHED);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGH);
    }

    public Dimension render(Graphics2D graphics) {
        if (!this.plugin.isCreatingScreenMarker() || this.plugin.isDrawingScreenMarker())
            return null;
        MenuEntry[] menuEntries = this.client.getMenuEntries();
        if (this.client.isMenuOpen() || menuEntries.length == 0) {
            this.plugin.setSelectedWidgetBounds(null);
            return null;
        }
        MenuEntry menuEntry = menuEntries[menuEntries.length - 1];
        int childIdx = menuEntry.getParam0();
        int widgetId = menuEntry.getParam1();
        int groupId = WidgetInfo.TO_GROUP(widgetId);
        int componentId = WidgetInfo.TO_CHILD(widgetId);
        Widget widget = this.client.getWidget(groupId, componentId);
        if (widget == null) {
            this.plugin.setSelectedWidgetBounds(null);
            return null;
        }
        Rectangle bounds = null;
        if (childIdx > -1) {
            if (widget.getType() == 2) {
                WidgetItem widgetItem = widget.getWidgetItem(childIdx);
                if (widgetItem != null)
                    bounds = widgetItem.getCanvasBounds();
            } else {
                Widget child = widget.getChild(childIdx);
                if (child != null)
                    bounds = child.getBounds();
            }
        } else {
            bounds = widget.getBounds();
        }
        if (bounds == null) {
            this.plugin.setSelectedWidgetBounds(null);
            return null;
        }
        drawHighlight(graphics, bounds);
        this.plugin.setSelectedWidgetBounds(bounds);
        return null;
    }

    private static void drawHighlight(Graphics2D graphics, Rectangle bounds) {
        graphics.setColor(Color.GREEN);
        graphics.draw(bounds);
    }
}
