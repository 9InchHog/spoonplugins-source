package net.runelite.client.plugins.coxspoontimers;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.plugins.coxspoontimers.utils.MiscUtil;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class CoxSpoonTimersOverlay extends OverlayPanel {
    private final Client client;

    private final CoxSpoonTimersPlugin plugin;

    private final CoxSpoonTimersConfig config;

    @Inject
    private CoxSpoonTimersOverlay(Client client, CoxSpoonTimersPlugin plugin, CoxSpoonTimersConfig config) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.LOW);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Cox Spoon Timers Overlay"));
    }

    public Dimension render(Graphics2D graphics) {
        if(config.displayOverlay()) {
            if (plugin.in_raid && !plugin.raidOver) {
                if (plugin.olmActive) {
                    panelComponent.getChildren().clear();
                    panelComponent.setPreferredSize(new Dimension(130, 0));
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Current Phase:")
                            .rightColor(Color.WHITE)
                            .right(MiscUtil.to_mmss_precise(plugin.clock() - plugin.split))
                            .build());
                } else {
                    panelComponent.getChildren().clear();
                    panelComponent.setPreferredSize(new Dimension(130, 0));
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Current Room:")
                            .rightColor(Color.WHITE)
                            .right(MiscUtil.to_mmss_precise(plugin.clock() - plugin.split))
                            .build());
                }
            }
        }
        return super.render(graphics);
    }
}
