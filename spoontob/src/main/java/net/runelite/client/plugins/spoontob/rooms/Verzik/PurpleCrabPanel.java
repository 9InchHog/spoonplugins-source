package net.runelite.client.plugins.spoontob.rooms.Verzik;

import net.runelite.api.Client;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PurpleCrabPanel extends OverlayPanel {
    private SpoonTobPlugin plugin;

    private SpoonTobConfig config;

    private Client client;

    private Verzik verzik;

    @Inject
    public PurpleCrabPanel(SpoonTobPlugin plugin, SpoonTobConfig config, Client client, Verzik verzik) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        this.verzik = verzik;
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        if(config.purpleCrabInfobox() && this.verzik.isVerzikActive() && this.verzik.getVerzikPhase() == Verzik.Phase.PHASE2) {
            Color color = Color.WHITE;
            if(verzik.purpleAttacksLeft > 0) {
                if (verzik.purpleAttacksLeft == 1) {
                    color = Color.RED;
                }
                this.panelComponent.getChildren().add(TitleComponent.builder()
                        .color(color)
                        .text(Integer.toString(verzik.purpleAttacksLeft))
                        .build());
            }else {
                BufferedImage img = ImageUtil.loadImageResource(SpoonTobPlugin.class, "purpleNylo.png");
                ImageComponent imgComp = new ImageComponent(img);
                this.panelComponent.getChildren().add(imgComp);
            }
            this.panelComponent.setPreferredSize(new Dimension(24, 24));
        }
        return super.render(graphics);
    }
}

