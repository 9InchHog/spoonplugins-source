package net.runelite.client.plugins.spoongauntlet;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.client.plugins.spoongauntlet.resources.OverlayUtil;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class PrayerWidgetOverlay extends Overlay
{
    private final Client client;
    private final SpoonGauntletPlugin plugin;
    private final SpoonGauntletConfig config;

    @Inject
    PrayerWidgetOverlay(final Client client, final SpoonGauntletPlugin plugin, final SpoonGauntletConfig config)
    {
        super(plugin);

        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        determineLayer();
    }

    @Override
    public Dimension render(final Graphics2D graphics2D) {
        if (config.countBossAttacksPrayer() != SpoonGauntletConfig.gauntletPrayerTab.OFF && GauntletUtils.inBoss(client)) {
            Color phaseColor = Color.WHITE;
            Prayer prayer = Prayer.PROTECT_FROM_MELEE;
            if (plugin.currentPhase == SpoonGauntletPlugin.BossAttackPhase.MAGIC) {
                phaseColor = Color.CYAN;
                prayer = Prayer.PROTECT_FROM_MAGIC;
            } else {
                phaseColor = Color.GREEN;
                prayer = Prayer.PROTECT_FROM_MISSILES;
            }

            final Rectangle rectangle = OverlayUtil.renderPrayerOverlay(graphics2D, client, prayer, phaseColor);

            if (rectangle == null) {
                return null;
            }

            // Overlay tick count on the prayer widget

            final int ticksUntilAttack = plugin.bossTicks;

            final String text = String.valueOf(ticksUntilAttack);

            final int fontSize = 16;
            final int fontStyle = Font.BOLD;
            final Color fontColor = ticksUntilAttack == 1 ? Color.WHITE : phaseColor;

            final int x = (int) (rectangle.getX() + rectangle.getWidth() / 2);
            final int y = (int) (rectangle.getY() + rectangle.getHeight() / 2);

            final Point point = new Point(x, y);

            final Point canvasPoint = new Point(point.getX() - 3, point.getY() + 6);

            if (plugin.bossTicks > 0 && config.countBossAttacksPrayer() == SpoonGauntletConfig.gauntletPrayerTab.TICKS_AND_BOX) {
                OverlayUtil.renderTextLocation(graphics2D, text, fontSize, fontStyle, fontColor, canvasPoint, true, 0);
            }
        }
        return null;
    }

    public void determineLayer()
    {
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }
}
