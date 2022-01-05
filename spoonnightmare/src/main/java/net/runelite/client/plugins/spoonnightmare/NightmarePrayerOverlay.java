package net.runelite.client.plugins.spoonnightmare;

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

public class NightmarePrayerOverlay extends Overlay {
    private final Client client;

    private final SpoonNightmarePlugin plugin;

    private final SpoonNightmareConfig config;

    private final SpriteManager spriteManager;

    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    NightmarePrayerOverlay(Client client, SpoonNightmarePlugin plugin, SpoonNightmareConfig config, SpriteManager spriteManager) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.spriteManager = spriteManager;
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        if (config.prayerHelper() && plugin.isActiveFight() && plugin.getNightmareNpc() != null && !plugin.correctPray.equals("")) {
            InfoBoxComponent prayComponent = new InfoBoxComponent();
            Prayer prayer = null;

            if (plugin.correctPray.equalsIgnoreCase("magic")) {
                if (plugin.cursePhase) {
                    prayComponent.setImage(spriteManager.getSprite(NightmareAttack.MELEE_ATTACK.getSpriteId(), 0));
                    prayer = Prayer.PROTECT_FROM_MELEE;
                } else {
                    prayComponent.setImage(spriteManager.getSprite(NightmareAttack.MAGIC_ATTACK.getSpriteId(), 0));
                    prayer = Prayer.PROTECT_FROM_MAGIC;
                }
            } else if (plugin.correctPray.equalsIgnoreCase("missiles")) {
                if (plugin.cursePhase) {
                    prayComponent.setImage(spriteManager.getSprite(NightmareAttack.MAGIC_ATTACK.getSpriteId(), 0));
                    prayer = Prayer.PROTECT_FROM_MAGIC;
                } else {
                    prayComponent.setImage(spriteManager.getSprite(NightmareAttack.RANGED_ATTACK.getSpriteId(), 0));
                    prayer = Prayer.PROTECT_FROM_MISSILES;
                }
            } else if (plugin.correctPray.equalsIgnoreCase("melee")) {
                if (plugin.cursePhase) {
                    prayComponent.setImage(spriteManager.getSprite(NightmareAttack.RANGED_ATTACK.getSpriteId(), 0));
                    prayer = Prayer.PROTECT_FROM_MISSILES;
                } else {
                    prayComponent.setImage(spriteManager.getSprite(NightmareAttack.MELEE_ATTACK.getSpriteId(), 0));
                    prayer = Prayer.PROTECT_FROM_MELEE;
                }
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
