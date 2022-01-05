package net.runelite.client.plugins.spoonnightmare;

import java.awt.*;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class YawnOverlay extends Overlay {
    private final Client client;

    private final SpoonNightmarePlugin plugin;

    private final SpoonNightmareConfig config;

    private final PanelComponent panelComponent = new PanelComponent();


    @Inject
    private YawnOverlay(Client client, SpoonNightmarePlugin plugin, SpoonNightmareConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        if (config.yawnTimer()) {
            if (plugin.yawning && plugin.yawnTicks >= 0) {
                if (plugin.yawnTicks < 10) {
                    panelComponent.getChildren().add(LineComponent.builder().left(" " + plugin.yawnTicks).build());
                } else {
                    panelComponent.getChildren().add(LineComponent.builder().left(String.valueOf(plugin.yawnTicks)).build());
                }
                panelComponent.setPreferredSize(new Dimension(24, 0));
                panelComponent.setBackgroundColor(new Color(52, 52, 52, 150));
            }
            return panelComponent.render(graphics);
        }
        return null;
    }
}
