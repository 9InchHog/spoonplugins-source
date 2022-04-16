package net.runelite.client.plugins.coxadditions.overlays;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Varbits;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;

public class MeatTreeCycleOverlay extends OverlayPanel {
    private final Client client;
    private final CoxAdditionsPlugin plugin;
    private final CoxAdditionsConfig config;

    @Inject
    public MeatTreeCycleOverlay(Client client, CoxAdditionsPlugin plugin, CoxAdditionsConfig config) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Cox Additions instance timer"));
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        if(config.meatTreeChopCycle() == CoxAdditionsConfig.meatTreeChopCycleMode.INFOBOX && this.client.getVarbitValue(Varbits.IN_RAID) == 1 && plugin.startedChopping && plugin.meatTreeAlive) {
            this.panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth("Tick:   ") + 15, 0));
            this.panelComponent.getChildren().add(LineComponent.builder()
                    .left("Tick: ")
                    .right(String.valueOf(this.plugin.ticksToChop))
                    .build());
        }
        return super.render(graphics);
    }
}

