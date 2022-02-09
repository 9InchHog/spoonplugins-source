package net.runelite.client.plugins.socket.plugins.socketworldhopper;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

class SocketWorldHopperPingOverlay extends Overlay {
    private static final int Y_OFFSET = 11;
    private static final int X_OFFSET = 1;

    private final Client client;
    private final SocketWorldHopperPlugin worldHopperPlugin;
    private final SocketWorldHopperConfig worldHopperConfig;

    @Inject
    private SocketWorldHopperPingOverlay(Client client, SocketWorldHopperPlugin worldHopperPlugin, SocketWorldHopperConfig worldHopperConfig) {
        this.client = client;
        this.worldHopperPlugin = worldHopperPlugin;
        this.worldHopperConfig = worldHopperConfig;
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!worldHopperConfig.displayPing())
        {
            return null;
        }

        final int ping = worldHopperPlugin.getCurrentPing();
        if (ping < 0)
        {
            return null;
        }

        final String text = ping + " ms";
        final int textWidth = graphics.getFontMetrics().stringWidth(text);
        final int textHeight = graphics.getFontMetrics().getAscent() - graphics.getFontMetrics().getDescent();

        // Adjust ping offset for logout button
        Widget logoutButton = client.getWidget(WidgetInfo.RESIZABLE_MINIMAP_LOGOUT_BUTTON);
        int xOffset = X_OFFSET;
        if (logoutButton != null && !logoutButton.isHidden())
        {
            xOffset += logoutButton.getWidth();
        }

        final int width = (int) client.getRealDimensions().getWidth();
        final Point point = new Point(width - textWidth - xOffset, textHeight + Y_OFFSET);
        OverlayUtil.renderTextLocation(graphics, point, text, Color.YELLOW);

        return null;
    }
}