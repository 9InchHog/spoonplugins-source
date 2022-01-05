package net.runelite.client.plugins.grotesqueguardians;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.InfoBoxComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class GGPrayerOverlay extends Overlay {
    private final Client client;

    private final GGConfig config;

    private final GGPlugin plugin;

    private final SpriteManager spriteManager;

    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    GGPrayerOverlay(GGPlugin plugin, GGConfig config, Client client, SpriteManager spriteManager) {
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        this.spriteManager = spriteManager;
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    public Dimension render(Graphics2D graphics) {
        if (!this.config.prayerHelper())
            return null;
        this.panelComponent.getChildren().clear();
        InfoBoxComponent prayComponent = new InfoBoxComponent();
        if (this.plugin.inGarg && this.plugin.getDuskNPC() != null) {
            if (this.plugin.getCurrentAttackStyle() == GGPlugin.DuskAttackStyles.MELEE) {
                prayComponent.setImage(this.spriteManager.getSprite(129, 0));
            } else if (this.plugin.getCurrentAttackStyle() == GGPlugin.DuskAttackStyles.RANGE) {
                prayComponent.setImage(this.spriteManager.getSprite(128, 0));
            }
            prayComponent.setPreferredSize(new Dimension(40, 40));
            this.panelComponent.getChildren().add(prayComponent);
            this.panelComponent.setPreferredSize(new Dimension(40, 0));
            this.panelComponent.setBorder(new Rectangle(0, 0, 0, 0));
        }
        return this.panelComponent.render(graphics);
    }
}
