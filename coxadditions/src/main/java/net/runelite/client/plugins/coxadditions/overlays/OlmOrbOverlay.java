package net.runelite.client.plugins.coxadditions.overlays;

import net.runelite.api.*;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.plugins.coxadditions.utils.OrbInfoBox;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;

@Singleton
public class OlmOrbOverlay extends Overlay {
    private final Client client;
    private final CoxAdditionsPlugin plugin;
    private final CoxAdditionsConfig config;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private SkillIconManager skillIconManager;

    @Inject
    private OlmOrbOverlay(final Client client, final CoxAdditionsPlugin plugin, final CoxAdditionsConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if(!plugin.orbStyle.equals("") && config.olmOrbs()) {
            this.panelComponent.getChildren().clear();
            Prayer prayer;
            BufferedImage img;
            Color color;

            for (Projectile p : this.client.getProjectiles()) {
                if ((p.getId() == 1341 && plugin.orbStyle.equals("mage")) || (p.getId() == 1343 && plugin.orbStyle.equals("range")) || (p.getId() == 1345 && plugin.orbStyle.equals("melee"))) {
                    if (plugin.orbStyle.equals("mage")) {
                        img = skillIconManager.getSkillImage(Skill.MAGIC);
                        prayer = Prayer.PROTECT_FROM_MAGIC;
                    }else if (plugin.orbStyle.equals("range")) {
                        img = skillIconManager.getSkillImage(Skill.RANGED);
                        prayer = Prayer.PROTECT_FROM_MISSILES;
                    }else {
                        img = skillIconManager.getSkillImage(Skill.ATTACK);
                        prayer = Prayer.PROTECT_FROM_MELEE;
                    }

                    if (!this.client.isPrayerActive(prayer)) {
                        color = new Color(255, 0, 0, 25);
                    } else {
                        color = new Color(0, 255, 0, 25);
                    }
                    OrbInfoBox infoBox = new OrbInfoBox();
                    infoBox.setImage(img);
                    infoBox.setBackgroundColor(color);
                    this.panelComponent.getChildren().add(infoBox);
                    this.panelComponent.setPreferredSize(new Dimension(40, 0));
                    this.panelComponent.setBorder(new Rectangle(0, 0, 0, 0));

                    return this.panelComponent.render(graphics);
                }
            }
        }
        return null;
    }
}
