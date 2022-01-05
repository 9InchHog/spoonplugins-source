package net.runelite.client.plugins.coxadditions.overlays;

import java.awt.*;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.Projectile;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class OrbPrayerTabOverlay extends Overlay {
    private final Client client;

    private final CoxAdditionsPlugin plugin;

    private final CoxAdditionsConfig config;

    @Inject
    private OrbPrayerTabOverlay(Client client, CoxAdditionsPlugin plugin, CoxAdditionsConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGHEST);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }

    private void drawBox(Graphics2D graphics, int startX, int startY) {
        if (startX != 0 || startY != 0) {
            if ((plugin.orbStyle.equals("range") && this.client.isPrayerActive(Prayer.PROTECT_FROM_MISSILES)) || (plugin.orbStyle.equals("mage") && this.client.isPrayerActive(Prayer.PROTECT_FROM_MAGIC))
                    || (plugin.orbStyle.equals("melee") && this.client.isPrayerActive(Prayer.PROTECT_FROM_MELEE))) {
                graphics.setColor(Color.GREEN);
            } else {
                graphics.setColor(Color.RED);
            }
            graphics.setStroke(new BasicStroke(config.prayerStrokeSize()));
            graphics.drawLine(startX, startY, startX + 33, startY);
            graphics.drawLine(startX + 33, startY, startX + 33, startY + 33);
            graphics.drawLine(startX + 33, startY + 33, startX, startY + 33);
            graphics.drawLine(startX, startY + 33, startX, startY);
        }
    }

    public Dimension render(Graphics2D graphics) {
        Widget prayerVisible = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
        if (prayerVisible != null && !prayerVisible.isHidden() && !prayerVisible.isSelfHidden() && this.config.olmOrbs()) {
            for (Projectile p : this.client.getProjectiles()) {
                if (this.plugin.orbStyle.equals("melee") && p.getId() == 1345) {
                    Point startLoc = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE).getCanvasLocation();
                    drawBox(graphics, startLoc.getX(), startLoc.getY());
                } else if (this.plugin.orbStyle.equals("range") && p.getId() == 1343) {
                    Point startLoc = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES).getCanvasLocation();
                    drawBox(graphics, startLoc.getX(), startLoc.getY());
                } else if (this.plugin.orbStyle.equals("mage") && p.getId() == 1341) {
                    Point startLoc = this.client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC).getCanvasLocation();
                    drawBox(graphics, startLoc.getX(), startLoc.getY());
                } 
            }
        }
        return null;
    }
}

