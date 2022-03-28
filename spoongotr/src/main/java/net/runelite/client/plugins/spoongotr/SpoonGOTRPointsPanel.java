package net.runelite.client.plugins.spoongotr;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class SpoonGOTRPointsPanel extends OverlayPanel {
    private SpoonGOTRPlugin plugin;

    private SpoonGOTRConfig config;

    private Client client;

    @Inject
    public SpoonGOTRPointsPanel(SpoonGOTRPlugin plugin, SpoonGOTRConfig config, Client client) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
		if (!plugin.gameStart && config.showRewardPoints() && plugin.showPoints && !plugin.elePoints.equals("") && !plugin.cataPoints.equals("")) {
			this.panelComponent.getChildren().add(LineComponent.builder()
                    .left("Elemental:")
					.right(plugin.elePoints)
					.build());
            this.panelComponent.getChildren().add(LineComponent.builder()
                    .left("Catalytic:")
                    .right(plugin.cataPoints)
                    .build());
            this.panelComponent.setPreferredSize(new Dimension(150, 24));
		}
        return super.render(graphics);
    }
}
