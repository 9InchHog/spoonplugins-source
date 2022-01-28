package net.runelite.client.plugins.spoontob.rooms.Maiden;

import net.runelite.api.Client;
import net.runelite.api.Model;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;

public class MaidenMaxHitToolTip extends Overlay {
    private final Client client;
    private final TooltipManager tooltipManager;
    private final Maiden maiden;
    private final SpoonTobConfig config;

    @Inject
    private MaidenMaxHitToolTip(Client client, TooltipManager tooltipManager, Maiden maiden, SpoonTobConfig config) {
        this.client = client;
        this.tooltipManager = tooltipManager;
        this.maiden = maiden;
        this.config = config;
    }

    public Dimension render(Graphics2D graphics) {
        if (config.maidenMaxHit() != SpoonTobConfig.MaidenMaxHitTTMode.OFF && !client.isMenuOpen() && maiden.isMaidenActive()) {
            NPC maidenNpc = maiden.getMaidenNPC();
            Model model = maidenNpc.getModel();
            LocalPoint localPoint = maidenNpc.getLocalLocation();
            if (model != null && localPoint != null) {
                Shape clickbox = Perspective.getClickbox(client, model, maidenNpc.getOrientation(), localPoint);
                if (clickbox != null) {
                    if (clickbox.contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY())) {
                        int noPrayerMaxHit = (int) Math.floor(maiden.getMaxHit());
                        int prayerMaxHit = noPrayerMaxHit / 2;
                        int elyMaxHit = prayerMaxHit - (int) Math.floor((double) prayerMaxHit * 0.25D);
                        switch(config.maidenMaxHit()) {
                            case REGULAR:
                                tooltipManager.add(new Tooltip(ColorUtil.wrapWithColorTag("No Prayer:", new Color(255, 109, 97))
                                        + ColorUtil.wrapWithColorTag(" +" + noPrayerMaxHit, new Color(-7278960)) + "</br>"
                                        + ColorUtil.wrapWithColorTag("Prayer:", Color.ORANGE) + ColorUtil.wrapWithColorTag(" +" + prayerMaxHit, new Color(-7278960))));
                                break;
                            case ELY:
                                tooltipManager.add(new Tooltip(ColorUtil.wrapWithColorTag("No Prayer:", new Color(255, 109, 97))
                                        + ColorUtil.wrapWithColorTag(" +" + noPrayerMaxHit, new Color(-7278960)) + "</br>"
                                        + ColorUtil.wrapWithColorTag("Elysian:", Color.CYAN) + ColorUtil.wrapWithColorTag(" +" + elyMaxHit, new Color(-7278960))));
                                break;
                            case BOTH:
                                tooltipManager.add(new Tooltip(ColorUtil.wrapWithColorTag("No Prayer:", new Color(255, 109, 97))
                                        + ColorUtil.wrapWithColorTag(" +" + noPrayerMaxHit, new Color(-7278960)) + "</br>"
                                        + ColorUtil.wrapWithColorTag("Prayer:", Color.ORANGE) + ColorUtil.wrapWithColorTag(" +" + prayerMaxHit, new Color(-7278960)) + "</br>"
                                        + ColorUtil.wrapWithColorTag("Elysian:", Color.CYAN) + ColorUtil.wrapWithColorTag(" +" + elyMaxHit, new Color(-7278960))));
                                break;
                            default:
                                throw new IllegalStateException("Invalid 'maidenMaxHit' config state -> state: " + config.maidenMaxHit().getName());
                        }
                    }
                }
            }
        }
        return null;
    }
}
