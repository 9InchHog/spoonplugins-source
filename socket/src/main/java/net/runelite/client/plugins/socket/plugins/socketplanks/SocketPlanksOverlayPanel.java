package net.runelite.client.plugins.socket.plugins.socketplanks;

import java.awt.*;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;

public class SocketPlanksOverlayPanel extends OverlayPanel {
    private final Client client;

    private final SocketPlanksPlugin plugin;

    private final SocketPlanksConfig config;

    @Inject
    private SocketPlanksOverlayPanel(Client client, SocketPlanksPlugin plugin, SocketPlanksConfig config) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    public Dimension render(Graphics2D graphics) {
        if (config.splitTimer() && this.client.getVar(Varbits.IN_RAID) == 1 && plugin.planksDropped && plugin.splitTimerDelay > 0) {
            this.panelComponent.getChildren().clear();
            int seconds = (int) Math.floor(client.getVarbitValue(6386) * .6);

            if (plugin.chestBuiltTime == -1) {
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.WHITE)
                        .left("Total Time: ")
                        .right(plugin.secondsToTime(seconds - plugin.planksDroppedTime))
                        .build());
            } else {
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.WHITE)
                        .left("Total Time: ")
                        .right(plugin.secondsToTime(plugin.chestBuiltTime - plugin.planksDroppedTime))
                        .build());
            }

            if (plugin.planksPickedUp) {
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.WHITE)
                        .left("Picked up: ")
                        .right(plugin.planksPickedUpTimeStr)
                        .build());
            }

            if (plugin.chestBuilt) {
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.WHITE)
                        .left("Chest built: ")
                        .right(plugin.chestBuiltTimeStr)
                        .build());
            }
        }
        return super.render(graphics);
    }
}