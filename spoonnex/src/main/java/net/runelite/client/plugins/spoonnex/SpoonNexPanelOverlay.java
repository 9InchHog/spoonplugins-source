package net.runelite.client.plugins.spoonnex;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class SpoonNexPanelOverlay extends OverlayPanel{
    private SpoonNexPlugin plugin;

    private SpoonNexConfig config;

    private Client client;

    @Inject
    public SpoonNexPanelOverlay(SpoonNexPlugin plugin, SpoonNexConfig config, Client client) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        if(config.playerCounter() && plugin.isInNexChamber()){
            this.panelComponent.getChildren().add(LineComponent.builder()
                    .leftColor(Color.WHITE)
                    .left("Players: ")
                    .right(String.valueOf(client.getPlayers().size()))
                    .build());
        }
        return super.render(graphics);
    }
}
