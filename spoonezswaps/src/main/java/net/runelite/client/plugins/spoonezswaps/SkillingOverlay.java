package net.runelite.client.plugins.spoonezswaps;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class SkillingOverlay extends Overlay {
    private final Client client;

    private final SpoonEzSwapsPlugin plugin;

    private final SpoonEzSwapsConfig config;

    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public SkillingOverlay(Client client, SpoonEzSwapsPlugin plugin, SpoonEzSwapsConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "SkillingOverlay"));
    }

    public Dimension render(Graphics2D graphics) {
        if (config.getStringAmulet() || config.getBakePie()) {
            panelComponent.getChildren().clear();
            if (config.getStringAmulet() && plugin.totalAmuletCount > 0) {
                Color color = plugin.totalAmuletCount - plugin.strungAmuletCount <= 5 ? Color.RED : Color.GREEN;
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Amulets: ")
                        .right(plugin.strungAmuletCount + " / " + plugin.totalAmuletCount)
                        .leftColor(color)
                        .rightColor(color)
                        .build());
            }

            if (config.getBakePie() && plugin.totalPieCount > 0) {
                Color color = plugin.totalPieCount - plugin.cookedPieCount <= 5 ? Color.RED : Color.GREEN;
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Pies: ")
                        .right(plugin.cookedPieCount + " / " + plugin.totalPieCount)
                        .leftColor(color)
                        .rightColor(color)
                        .build());
            }
            return panelComponent.render(graphics);
        }
        return null;
    }
}
