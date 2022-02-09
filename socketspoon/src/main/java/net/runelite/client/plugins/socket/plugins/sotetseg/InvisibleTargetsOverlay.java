package net.runelite.client.plugins.socket.plugins.sotetseg;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;

public class InvisibleTargetsOverlay extends Overlay {
    private final Client client;

    private final SotetsegPlugin plugin;

    private final SotetsegConfig config;

    @Inject
    private InvisibleTargetsOverlay(Client client, SotetsegPlugin plugin, SotetsegConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGHEST);
    }

    public Dimension render(Graphics2D graphics) {
        if (config.warnBall() && plugin.invisibleTicks > 0) {
            for (Player players : client.getPlayers()) {
                if (plugin.invisibleTargets.contains(players.getName()) && players.getLocalLocation() != null && players.getName() != null) {
                    renderTile(graphics, players.getLocalLocation(), config.ballTargetColor());
                }
            }
        }
        return null;
    }

    private void renderTile(Graphics2D graphics, LocalPoint dest, Color color) {
        if (dest == null)
            return;
        Polygon poly = Perspective.getCanvasTilePoly(client, dest);
        if (poly == null)
            return;
        if (config.antiAlias()) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke((float) 2));
        graphics.draw(poly);
        graphics.setColor(new Color(0, 0, 0, 0));
    }
}
