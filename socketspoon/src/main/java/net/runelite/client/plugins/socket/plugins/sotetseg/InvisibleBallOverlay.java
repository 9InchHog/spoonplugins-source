package net.runelite.client.plugins.socket.plugins.sotetseg;

import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;

public class InvisibleBallOverlay extends Overlay {
    private final Client client;

    private final SotetsegPlugin plugin;

    private final SotetsegConfig config;

    private final PanelComponent panelComponent = new PanelComponent();

    private int opacity = 5;

    @Inject
    private InvisibleBallOverlay(Client client, SotetsegPlugin plugin, SotetsegConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        if (config.warnBall() && plugin.invisibleTicks > 0 && (plugin.isInUnderWorld() || plugin.isInOverWorld())) {
            if (opacity >= 80) {
                opacity = 5;
            } else {
                opacity += 3;
            }
            panelComponent.setBackgroundColor(new Color(255, 0, 0, opacity));
            panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth("INVISIBLE") + 10, 0));
            panelComponent.getChildren().add(LineComponent.builder().left("INVISIBLE").build());
            return panelComponent.render(graphics);
        }
        return null;
    }
}
