package net.runelite.client.plugins.socket.plugins.playerindicatorsextended;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.Text;

public class PlayerIndicatorsExtendedMinimapOverlay extends Overlay {
    private final Client client;

    private final PlayerIndicatorsExtendedPlugin plugin;

    private final PlayerIndicatorsExtendedConfig config;

    @Inject
    private PlayerIndicatorsExtendedMinimapOverlay(Client client, PlayerIndicatorsExtendedPlugin plugin, PlayerIndicatorsExtendedConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPriority(OverlayPriority.HIGHEST);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    public Dimension render(Graphics2D graphics) {
        for (Actor actor : this.plugin.getPlayers()) {
            String name = Text.sanitize(actor.getName());
            if (this.config.drawMinimap()) {
                Point minimapPoint = actor.getMinimapLocation();
                if (minimapPoint != null)
                    OverlayUtil.renderTextLocation(graphics, minimapPoint, name, this.config.nameColor());
            }
        }
        return null;
    }
}
