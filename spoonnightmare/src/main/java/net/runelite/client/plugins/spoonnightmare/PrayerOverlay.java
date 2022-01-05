package net.runelite.client.plugins.spoonnightmare;

import java.awt.*;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class PrayerOverlay extends Overlay {
    private final Client client;

    private final SpoonNightmarePlugin plugin;

    private final SpoonNightmareConfig config;

    @Inject
    private PrayerOverlay(Client client, SpoonNightmarePlugin plugin, SpoonNightmareConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGHEST);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    public Dimension render(Graphics2D graphics) {
        Widget prayerVisible = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
        if (prayerVisible != null && !prayerVisible.isHidden() && !prayerVisible.isSelfHidden() && config.easyPrayer() && !plugin.correctPray.equals("")) {
            Point startLoc;
            switch (plugin.correctPray) {
                case "melee":
                    if (plugin.cursePhase) {
                        startLoc = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES).getCanvasLocation();
                    } else {
                        startLoc = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE).getCanvasLocation();
                    }
                    drawBox(graphics, startLoc.getX(), startLoc.getY());
                    break;
                case "missiles":
                    if (plugin.cursePhase) {
                        startLoc = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC).getCanvasLocation();
                    } else {
                        startLoc = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES).getCanvasLocation();
                    }
                    drawBox(graphics, startLoc.getX(), startLoc.getY());
                    break;
                case "magic":
                    if (plugin.cursePhase) {
                        startLoc = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE).getCanvasLocation();
                    } else {
                        startLoc = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC).getCanvasLocation();
                    }
                    drawBox(graphics, startLoc.getX(), startLoc.getY());
                    break;
            }
        }
        return null;
    }

    private void drawBox(Graphics2D graphics, int startX, int startY) {
        if (startX != 0 || startY != 0) {
            if(plugin.cursePhase){
                if ((plugin.correctPray.equals("missiles") && client.isPrayerActive(Prayer.PROTECT_FROM_MAGIC)) ||
                        (plugin.correctPray.equals("magic") && client.isPrayerActive(Prayer.PROTECT_FROM_MELEE)) ||
                        (plugin.correctPray.equals("melee") && client.isPrayerActive(Prayer.PROTECT_FROM_MISSILES))) {
                    graphics.setColor(Color.GREEN);
                } else {
                    graphics.setColor(Color.RED);
                }
            }else {
                if ((plugin.correctPray.equals("missiles") && client.isPrayerActive(Prayer.PROTECT_FROM_MISSILES)) ||
                        (plugin.correctPray.equals("magic") && client.isPrayerActive(Prayer.PROTECT_FROM_MAGIC)) ||
                        (plugin.correctPray.equals("melee") && client.isPrayerActive(Prayer.PROTECT_FROM_MELEE))) {
                    graphics.setColor(Color.GREEN);
                } else {
                    graphics.setColor(Color.RED);
                }
            }
            graphics.setStroke(new BasicStroke(config.prayerStrokeSize()));
            graphics.drawLine(startX, startY, startX + 33, startY);
            graphics.drawLine(startX + 33, startY, startX + 33, startY + 33);
            graphics.drawLine(startX + 33, startY + 33, startX, startY + 33);
            graphics.drawLine(startX, startY + 33, startX, startY);
        }
    }
}
