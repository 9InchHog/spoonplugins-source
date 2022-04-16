package net.runelite.client.plugins.coxadditions.overlays;

import java.awt.*;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class OlmPhasePanel extends OverlayPanel {
    private final Client client;
    private final CoxAdditionsPlugin plugin;
    private final CoxAdditionsConfig config;

    @Inject
    public OlmPhasePanel(Client client, CoxAdditionsPlugin plugin, CoxAdditionsConfig config) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        if(config.olmPhasePanel() && this.client.getVarbitValue(Varbits.IN_RAID) == 1 && !plugin.olmPhase.equals("")) {
            Color color = plugin.olmPhase.equals("Acid") ? Color.GREEN : plugin.olmPhase.equals("Flame") ? Color.RED : Color.MAGENTA;
            this.panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(plugin.olmPhase) + 3, 0));
            this.panelComponent.getChildren().add(TitleComponent.builder()
                .color(color)
                .text(plugin.olmPhase)
                .build());
        }
        return super.render(graphics);
    }
}

