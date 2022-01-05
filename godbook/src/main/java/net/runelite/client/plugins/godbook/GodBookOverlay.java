package net.runelite.client.plugins.godbook;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GodBookOverlay extends Overlay {
    private static final Logger log = LoggerFactory.getLogger(GodBookOverlay.class);

    private final Client client;

    private final GodBookConfig config;

    private final GodBookPlugin plugin;

    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private GodBookOverlay(Client client, GodBookPlugin plugin, GodBookConfig config) {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }

    public Dimension render(Graphics2D graphics) {
        if (this.plugin.isActive()) {
            this.panelComponent.getChildren().clear();
            String overlayTitle = "Tick:";
            this.panelComponent.getChildren().add(TitleComponent.builder().text(overlayTitle).color(Color.green).build());
            int max = 0;
            int i;
            for (i = 0; i < this.plugin.getNames().size(); i++) {
                int width = graphics.getFontMetrics().stringWidth(this.plugin.getNames().get(i)) + graphics.getFontMetrics().stringWidth(overlayTitle);
                if (width > max)
                    max = width;
            }
            this.panelComponent.setPreferredSize(new Dimension(max + 10, 0));
            for (i = 0; i < this.plugin.getTicks().size(); i++)
                this.panelComponent.getChildren().add(LineComponent.builder().left(this.plugin.getNames().get(i)).right(Integer.toString(((Integer)this.plugin.getTicks().get(i)).intValue())).build());
        }
        return this.panelComponent.render(graphics);
    }
}
