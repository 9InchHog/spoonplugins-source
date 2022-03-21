package net.runelite.client.plugins.spoonnex;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class SpoonNexHpToPhasePanel extends OverlayPanel{
    private SpoonNexPlugin plugin;

    private SpoonNexConfig config;

    private Client client;

    @Inject
    public SpoonNexHpToPhasePanel(SpoonNexPlugin plugin, SpoonNexConfig config, Client client) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
    }
    
    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        if(plugin.nex != null && (config.hpToPhase() == SpoonNexConfig.HpToPhaseMode.PANEL || config.hpToPhase() == SpoonNexConfig.HpToPhaseMode.BOTH) && plugin.nex.phase < 5){
            this.panelComponent.getChildren().add(LineComponent.builder()
                .left("HP:")
                .right(plugin.hpToPhase > 0 ? String.valueOf(plugin.hpToPhase) : "0")
                .build());

            this.panelComponent.setPreferredSize(new Dimension(60, 24));
        }
        return super.render(graphics);
    }
}
