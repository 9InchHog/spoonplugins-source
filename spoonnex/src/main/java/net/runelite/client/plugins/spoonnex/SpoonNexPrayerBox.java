package net.runelite.client.plugins.spoonnex;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.InfoBoxComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class SpoonNexPrayerBox extends Overlay {
    private final Client client;

    private final SpoonNexPlugin plugin;

    private final SpoonNexConfig config;

    private final SpriteManager spriteManager;

    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    SpoonNexPrayerBox(Client client, SpoonNexPlugin plugin, SpoonNexConfig config, SpriteManager spriteManager) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.spriteManager = spriteManager;
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        if (config.prayerHelper() && plugin.nex != null) {
            InfoBoxComponent prayComponent = new InfoBoxComponent();
            Prayer prayer = null;

            if (plugin.nex.phase == 2) {
                prayComponent.setImage(spriteManager.getSprite(128, 0));
                prayer = Prayer.PROTECT_FROM_MISSILES;
            } else if (plugin.nex.phase == 5 && plugin.nex.npc != null && plugin.nex.npc.getInteracting().getName() != null && client.getLocalPlayer() != null
                    && plugin.nex.npc.getInteracting().getName().equals(client.getLocalPlayer().getName()) && plugin.nex.npc.getLocalLocation().distanceTo(client.getLocalPlayer().getLocalLocation()) <= 1) {
                prayComponent.setImage(spriteManager.getSprite(129, 0));
                prayer = Prayer.PROTECT_FROM_MELEE;
            } else {
                prayComponent.setImage(spriteManager.getSprite(127, 0));
                prayer = Prayer.PROTECT_FROM_MAGIC;
            }

            if (!client.isPrayerActive(prayer)) {
                prayComponent.setBackgroundColor(new Color(255, 0, 0, 25));
            } else {
                prayComponent.setBackgroundColor(new Color(0, 255, 0, 25));
            }
            prayComponent.setPreferredSize(new Dimension(40, 40));
            panelComponent.getChildren().add(prayComponent);
            panelComponent.setPreferredSize(new Dimension(40, 0));
            panelComponent.setBorder(new Rectangle(0, 0, 0, 0));
        }
        return panelComponent.render(graphics);
    }
}
