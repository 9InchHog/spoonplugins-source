package net.runelite.client.plugins.hoptimer;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class HopTimerOverlay extends OverlayPanel {
    private final Client client;
    private final HopTimerPlugin plugin;

    @Inject
    private HopTimerOverlay(final Client client, final HopTimerPlugin plugin){
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics){
        if(plugin.hopTicks > 0) {
            this.panelComponent.getChildren().clear();
            this.panelComponent.setPreferredSize(new Dimension(50, 0));
            this.panelComponent.getChildren().add(TitleComponent.builder()
                    .color(Color.WHITE)
                    .text(plugin.hopSecondsDisplay)
                    .build());
        }else if(plugin.hopTicks == 0 && plugin.canHop && plugin.canHopTicks > 0){
            this.panelComponent.getChildren().clear();
            this.panelComponent.setPreferredSize(new Dimension(50, 0));
            this.panelComponent.getChildren().add(TitleComponent.builder()
                    .color(Color.GREEN)
                    .text("HOP")
                    .build());
        }
        return super.render(graphics);
    }
}
