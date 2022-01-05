package net.runelite.client.plugins.coxadditions.overlays;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class VespEnhanceOverlay extends Overlay {
    private final CoxAdditionsPlugin plugin;

    private final CoxAdditionsConfig config;

    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private VespEnhanceOverlay(CoxAdditionsPlugin plugin, CoxAdditionsConfig config) {
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    public Dimension render(Graphics2D graphics) {
        if (this.config.vespPrayerEnhance()) {
            this.panelComponent.getChildren().clear();
            if (this.plugin.prayerEnhanceActive && this.plugin.vespAlive) {
                this.panelComponent.setBackgroundColor(new Color(Color.MAGENTA.getRed(), Color.MAGENTA.getGreen(), Color.MAGENTA.getBlue(), 80));
                this.panelComponent.getChildren().add(LineComponent.builder().left(" " + this.plugin.prayerEnhanceTicks).build());
                this.panelComponent.setPreferredSize(new Dimension(24, 0));
                return this.panelComponent.render(graphics);
            }
        }
        return null;
    }
}

