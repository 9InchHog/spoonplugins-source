package net.runelite.client.plugins.socket.plugins.socketvangpots;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class SocketVangsOverlayPanel extends OverlayPanel {
    private final Client client;

    private final SocketVangPotsPlugin plugin;

    private final SocketVangPotsConfig config;

    @Inject
    private SocketVangsOverlayPanel(Client client, SocketVangPotsPlugin plugin, SocketVangPotsConfig config) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.MED);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        if (config.showPanel() && plugin.overloadsDropped > 0) {
            WorldPoint wp = client.getLocalPlayer().getWorldLocation();
            int plane = client.getPlane();
            int x = wp.getX() - client.getBaseX();
            int y = wp.getY() - client.getBaseY();
            int type = CoxUtil.getroom_type(client.getInstanceTemplateChunks()[plane][x / 8][y / 8]);
            if (type == CoxUtil.VANGUARDS || type == CoxUtil.FARMING) {
                panelComponent.getChildren().clear();
                panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth("Overloads: ") + 20, 0));
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.WHITE)
                        .left("Overloads: ")
                        .right(String.valueOf(plugin.overloadsDropped))
                        .build());
            }
        }
        return super.render(graphics);
    }
}