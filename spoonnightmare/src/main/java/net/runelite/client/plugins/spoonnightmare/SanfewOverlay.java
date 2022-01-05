package net.runelite.client.plugins.spoonnightmare;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class SanfewOverlay extends Overlay {
    private final Client client;

    private final SpoonNightmarePlugin plugin;

    private final SpoonNightmareConfig config;

    private final PanelComponent panelComponent = new PanelComponent();

    private int opacity = 5;

    @Inject
    private ItemManager itemManager;

    @Inject
    private SanfewOverlay(Client client, SpoonNightmarePlugin plugin, SpoonNightmareConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        if (config.parasiteTimer()) {
            if (plugin.preggers && plugin.parasiteTicks >= 0) {
                if (plugin.impregnated) {
                    if (opacity >= 80) {
                        opacity = 5;
                    } else {
                        opacity += 3;
                    }
                    panelComponent.setBackgroundColor(new Color(255, 0, 0, opacity));
                } else {
                    opacity = 80;
                    panelComponent.setBackgroundColor(new Color(0, 255, 0, opacity));
                }
                if (plugin.parasiteTicks < 10) {
                    panelComponent.getChildren().add(LineComponent.builder().left(" " + plugin.parasiteTicks).build());
                } else {
                    panelComponent.getChildren().add(LineComponent.builder().left(String.valueOf(plugin.parasiteTicks)).build());
                }
                panelComponent.setPreferredSize(new Dimension(24, 0));
                return panelComponent.render(graphics);
            }
            opacity = 5;
            return null;
        }
        return null;
    }
}
