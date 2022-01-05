package net.runelite.client.plugins.keydrag;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.TitleComponent;

class KeyDragOverlay extends OverlayPanel {
    private final Client client;

    private final KeyDragPlugin plugin;

    private final KeyDragConfig config;

    @Inject
    KeyDragOverlay(Client client, KeyDragPlugin plugin, KeyDragConfig config) {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPriority(OverlayPriority.HIGH);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Drag Overlay"));
    }

    public Dimension render(Graphics2D graphics) {
        if (this.config.displayOverlay()) {
            this.panelComponent.getChildren().clear();
            this.panelComponent.setPreferredSize(new Dimension(50, 0));
            String status = "";

            if(this.plugin.toggleDrag){
                status = "On";
            }else{
                status = "Off";
            }
            this.panelComponent.getChildren().add(TitleComponent.builder().text(status).color(Color.WHITE).build());
        }
        return super.render(graphics);
    }
}
