package net.runelite.client.plugins.spoongotr;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class SpoonGOTRTimerPanel extends OverlayPanel {
    private SpoonGOTRPlugin plugin;

    private SpoonGOTRConfig config;

    private Client client;

    @Inject
    public SpoonGOTRTimerPanel(SpoonGOTRPlugin plugin, SpoonGOTRConfig config, Client client) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
		if (plugin.gameStart && config.portalTimer()) {
		    int ticksTillPortal = plugin.portalsSpawned > 0 ? 225 - plugin.ticksSincePortal : 256 - plugin.ticksSincePortal;
            if (ticksTillPortal >= -20) {
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .left("Next Portal:")
                        .right(String.valueOf(ticksTillPortal))
                        .build());
            }
            if (plugin.portalTicks > 0) {
                this.panelComponent.getChildren().add(LineComponent.builder()
                    .left("Active Portal:")
					.right(String.valueOf(plugin.portalTicks))
					.build());
            }

            this.panelComponent.setPreferredSize(new Dimension(70, 24));
		} else if (!plugin.gameStart && config.timeTillStart() && plugin.timeTillNextGame >= 0 && plugin.timeTillNextGame <= 110) {
            this.panelComponent.getChildren().add(LineComponent.builder()
                    .left("Start:")
                    .right(String.valueOf(104 - plugin.timeTillNextGame))
                    .build());
            this.panelComponent.setPreferredSize(new Dimension(70, 24));
        }
        return super.render(graphics);
    }
}
