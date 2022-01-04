package net.runelite.client.plugins.theatre.Maiden;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;

import net.runelite.client.plugins.theatre.TheatreConfig;
import net.runelite.client.plugins.theatre.Maiden.Maiden;
import net.runelite.api.Client;
import net.runelite.api.Model;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;

public class MaidenMaxHitTooltip extends Overlay {
    private final Client client;
    private final TooltipManager tooltipManager;
    private final Maiden maiden;
    private final TheatreConfig config;

    @Inject
    private MaidenMaxHitTooltip(Client client, TooltipManager tooltipManager, Maiden maiden, TheatreConfig config) {
        this.client = client;
        this.tooltipManager = tooltipManager;
        this.maiden = maiden;
        this.config = config;
    }

    public Dimension render(Graphics2D graphics) {
        if (this.config.maidenMaxHit() && !this.client.isMenuOpen() && this.maiden.isMaidenActive()) {
            NPC maidenNpc = this.maiden.getMaidenNPC();
            Model model = maidenNpc.getModel();
            LocalPoint localPoint = maidenNpc.getLocalLocation();
            if (model != null && localPoint != null) {
                Shape clickbox = Perspective.getClickbox(this.client, model, maidenNpc.getOrientation(), localPoint);
                if (clickbox == null) {
                    return null;
                } else {
                    if (clickbox.contains((double)this.client.getMouseCanvasPosition().getX(), (double)this.client.getMouseCanvasPosition().getY())) {
                        int noPrayerMaxHit = (int)Math.floor(this.maiden.getMaxHit());
                        int prayerMaxHit = noPrayerMaxHit / 2;
                        int elyMaxHit = prayerMaxHit - (int)Math.floor((double)prayerMaxHit * 0.25D);
                        StringBuilder tooltip = (new StringBuilder()).append(ColorUtil.wrapWithColorTag("No Prayer:", new Color(255, 109, 97))).append(ColorUtil.wrapWithColorTag(" +" + Integer.toString(noPrayerMaxHit), new Color(-7278960))).append("</br>").append(ColorUtil.wrapWithColorTag("Prayer:", Color.ORANGE)).append(ColorUtil.wrapWithColorTag(" +" + Integer.toString(prayerMaxHit), new Color(-7278960))).append("</br>").append(ColorUtil.wrapWithColorTag("Elysian:", Color.CYAN)).append(ColorUtil.wrapWithColorTag(" +" + Integer.toString(elyMaxHit), new Color(-7278960)));
                        this.tooltipManager.add(new Tooltip(tooltip.toString()));
                    }

                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
