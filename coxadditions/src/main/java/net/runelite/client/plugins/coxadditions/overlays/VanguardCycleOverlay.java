package net.runelite.client.plugins.coxadditions.overlays;
import java.awt.*;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

public class VanguardCycleOverlay extends OverlayPanel {
    private final Client client;

    private final CoxAdditionsPlugin plugin;

    private final CoxAdditionsConfig config;

    @Inject
    private VanguardCycleOverlay(Client client, CoxAdditionsPlugin plugin, CoxAdditionsConfig config) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.LOW);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Vanguard Tick Cycle"));
    }

    public Dimension render(Graphics2D graphics) {
        if (this.config.vangsCycle() != CoxAdditionsConfig.VangsTicksMode.OFF) {
            this.panelComponent.getChildren().clear();
            if (this.plugin.vangsAlive) {
                this.panelComponent.setPreferredSize(new Dimension(110, 0));

                if(this.config.vangsCycle() == CoxAdditionsConfig.VangsTicksMode.BOTH) {
                    if(plugin.vangsTicks >= 20) {
                        this.panelComponent.getChildren().add(LineComponent.builder()
                                .left("Ticks Alive:")
                                .rightColor(Color.RED)
                                .right(this.plugin.vangs4Ticks + " (" + this.plugin.vangsTicks + ")")
                                .build());
                    }else{
                        this.panelComponent.getChildren().add(LineComponent.builder()
                                .left("Ticks Alive:")
                                .right(this.plugin.vangs4Ticks + " (" + this.plugin.vangsTicks + ")")
                                .build());
                    }
                }else if(this.config.vangsCycle() == CoxAdditionsConfig.VangsTicksMode.TOTAL_TICKS) {
                    if(plugin.vangsTicks >= 20){
                        this.panelComponent.getChildren().add(LineComponent.builder()
                                .left("Ticks Alive:")
                                .right(String.valueOf(this.plugin.vangsTicks))
                                .rightColor(Color.RED)
                                .build());
                    }else{
                        this.panelComponent.getChildren().add(LineComponent.builder()
                                .left("Ticks Alive:")
                                .right(String.valueOf(this.plugin.vangsTicks))
                                .build());
                    }
                }else if(this.config.vangsCycle() == CoxAdditionsConfig.VangsTicksMode.FOUR_TICK_CYCLE) {
                    if (plugin.vangsTicks >= 20) {
                        this.panelComponent.getChildren().add(LineComponent.builder()
                                .left("Ticks Alive:")
                                .right(String.valueOf(this.plugin.vangs4Ticks))
                                .rightColor(Color.RED)
                                .build());
                    } else {
                        this.panelComponent.getChildren().add(LineComponent.builder()
                                .left("Ticks Alive:")
                                .right(String.valueOf(this.plugin.vangs4Ticks))
                                .build());
                    }
                }
            }
        }
        return super.render(graphics);
    }
}
