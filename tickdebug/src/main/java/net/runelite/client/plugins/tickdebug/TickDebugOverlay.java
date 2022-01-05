package net.runelite.client.plugins.tickdebug;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class TickDebugOverlay extends Overlay {
    private static final int Y_OFFSET = 22;

    private static final int X_OFFSET = 1;

    private final Client client;

    private final TickDebugPlugin tickDebugPlugin;

    private final TickDebugConfig config;

    @Inject
    private TickDebugOverlay(Client client, TickDebugPlugin worldHopperPlugin, TickDebugConfig config) {
        this.client = client;
        this.tickDebugPlugin = worldHopperPlugin;
        this.config = config;
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.DYNAMIC);
    }

    public Dimension render(Graphics2D graphics) {
        Color c;
        int delay = tickDebugPlugin.last_tick_dur_ms;
        //String text = delay + " ms";
        String text;
        if (config.showDif()) {
            if (delay > 600) {
                text = delay - 600 + " ms";
            } else {
                text = 600 - delay + " ms";
            }
        } else {
            text = delay + " ms";
        }
        int textWidth = graphics.getFontMetrics().stringWidth(text);
        int textHeight = graphics.getFontMetrics().getAscent() -
                graphics.getFontMetrics().getDescent();
        Widget logoutButton = this.client
                .getWidget(WidgetInfo.RESIZABLE_MINIMAP_LOGOUT_BUTTON);
        int xOffset = 1;
        if (logoutButton != null && !logoutButton.isHidden())
            xOffset += logoutButton.getWidth();
        int width = (int)this.client.getRealDimensions().getWidth();
        Point point = new Point(width - textWidth - xOffset,
                textHeight + 22);
        if (delay >= 800 || delay <= 400) {
            c = Color.RED;
        } else if (delay >= 700 || delay <= 500) {
            c = Color.ORANGE;
        } else {
            c = Color.YELLOW;
        }
        OverlayUtil.renderTextLocation(graphics, point, text, c);
        return null;
    }
}
