package net.runelite.client.plugins.coxadditions.overlays;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class OlmOverlay extends Overlay {
    private final Client client;

    private final CoxAdditionsPlugin plugin;

    private final CoxAdditionsConfig config;

    private final PanelComponent panelComponent = new PanelComponent();

    private int opacity = 5;

    @Inject
    private ItemManager itemManager;

    @Inject
    private OlmOverlay(Client client, CoxAdditionsPlugin plugin, CoxAdditionsConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        if (this.config.burnTickCounter() && this.plugin.burningActive) {
            this.opacity = 80;
            this.panelComponent.setBackgroundColor(new Color(255, 0, 0, this.opacity));

            if (this.plugin.burningTicks < 10) {
                this.panelComponent.getChildren().add(LineComponent.builder().left(" " + this.plugin.burningTicks).build());
            } else {
                this.panelComponent.getChildren().add(LineComponent.builder().left(String.valueOf(this.plugin.burningTicks)).build());
            }
            this.panelComponent.setPreferredSize(new Dimension(24, 0));
            return this.panelComponent.render(graphics);
        }else if (this.config.acidTickCounter() && this.plugin.acidActive) {
            this.opacity = 80;
            this.panelComponent.setBackgroundColor(new Color(0, 255, 0, this.opacity));

            if (this.plugin.acidTicks < 10) {
                this.panelComponent.getChildren().add(LineComponent.builder().left(" " + this.plugin.acidTicks).build());
            } else {
                this.panelComponent.getChildren().add(LineComponent.builder().left(String.valueOf(this.plugin.acidTicks)).build());
            }
            this.panelComponent.setPreferredSize(new Dimension(24, 0));
            return this.panelComponent.render(graphics);
        }else if (this.config.crystalTickCounter() && this.plugin.crystalsActive) {
            this.opacity = 80;
            this.panelComponent.setBackgroundColor(new Color(159, 0, 162, this.opacity));

            if (this.plugin.acidTicks < 10) {
                this.panelComponent.getChildren().add(LineComponent.builder().left(" " + this.plugin.crystalsTicks).build());
            } else {
                this.panelComponent.getChildren().add(LineComponent.builder().left(String.valueOf(this.plugin.crystalsTicks)).build());
            }
            this.panelComponent.setPreferredSize(new Dimension(24, 0));
            return this.panelComponent.render(graphics);
        }
        return null;
    }
}

