package net.runelite.client.plugins.spawnpredictor.overlays;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.plugins.spawnpredictor.SpawnPredictorConfig;
import net.runelite.client.plugins.spawnpredictor.SpawnPredictorPlugin;
import net.runelite.client.plugins.spawnpredictor.util.StartLocations;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

public class RotationOverlayPanel extends OverlayPanel {
    private final SpawnPredictorPlugin plugin;

    private final SpawnPredictorConfig config;

    @Inject
    private RotationOverlayPanel(Client client, SpawnPredictorPlugin plugin, SpawnPredictorConfig config) {
        this.plugin = plugin;
        this.config = config;
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    public Dimension render(Graphics2D graphics) {
        if (!plugin.isLocatedAtTzhaars() || !config.lobbyRotationInfoOverlay())
            return null;
        int rotation = plugin.getRotationCol();
        panelComponent.setPreferredSize(new Dimension(125, 0));
        if (config.includeUTCTime())
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time:")
                    .leftColor(Color.WHITE)
                    .right(plugin.getUTCFormatted())
                    .rightColor(Color.ORANGE)
                    .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Current Rotation:")
                .leftColor(Color.WHITE)
                .right(Integer.toString(StartLocations.translateRotation(rotation)))
                .rightColor(Color.GREEN)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Next:")
                .leftColor(Color.WHITE)
                .right("[T - " + (60 - plugin.getUTCTime().getSecond()) + "s, Rot: " +
                        ((rotation + 1 > 15) ? "4" : Integer.toString(StartLocations.translateRotation(rotation + 1))) + "]")
                .rightColor(Color.YELLOW)
                .build());
        return super.render(graphics);
    }
}
