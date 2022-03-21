package net.runelite.client.plugins.spoongauntlet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.HeadIcon;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.InfoBoxComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class GauntletSpriteOverlay extends Overlay {
    private final Client client;

    private final SpoonGauntletConfig config;

    private final SpoonGauntletPlugin plugin;

    private BufferedImage rangePrayIcon;

    private BufferedImage magePrayIcon;

    protected final SpriteManager spriteManager;

    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private GauntletSpriteOverlay(Client client, SpoonGauntletConfig config, SpoonGauntletPlugin plugin, SpriteManager spriteManager) {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPriority(OverlayPriority.HIGH);
        this.config = config;
        this.client = client;
        this.plugin = plugin;
        this.spriteManager = spriteManager;
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        BufferedImage img = null;
        if (GauntletUtils.inRaid(this.client)) {
            if(GauntletUtils.inBoss(this.client) && this.config.showBossAttackOverlay()) {
                if (this.plugin.currentPhase == SpoonGauntletPlugin.BossAttackPhase.MAGIC) {
                    img = spriteManager.getSprite(127, 0);
                } else if (this.plugin.currentPhase == SpoonGauntletPlugin.BossAttackPhase.RANGE) {
                    img = spriteManager.getSprite(128, 0);
                }
            } else if (config.showPrepBossPrayer() && !GauntletUtils.inBoss(client) && plugin.hunllef != null) {
                if (plugin.hunllef.getComposition().getOverheadIcon() == HeadIcon.MAGIC) {
                    img = spriteManager.getSprite(127, 0);
                } else if (plugin.hunllef.getComposition().getOverheadIcon() == HeadIcon.RANGED) {
                    img = spriteManager.getSprite(128, 0);
                } else if (plugin.hunllef.getComposition().getOverheadIcon() == HeadIcon.MELEE) {
                    img = spriteManager.getSprite(129, 0);
                }
            }

            if (img != null) {
                InfoBoxComponent prayComponent = new InfoBoxComponent();
                prayComponent.setImage(img);
                prayComponent.setBackgroundColor(this.plugin.bossCounter == 1 ? new Color(255, 0, 0, 25) : new Color(0, 255, 0, 25));
                prayComponent.setPreferredSize(new Dimension(40, 40));
                this.panelComponent.getChildren().add(prayComponent);
                this.panelComponent.setPreferredSize(new Dimension(40, 0));
                this.panelComponent.setBorder(new Rectangle(0, 0, 0, 0));
            }
        }
        return this.panelComponent.render(graphics);
    }
}
