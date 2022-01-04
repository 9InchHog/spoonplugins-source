//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.runelite.client.plugins.theatre.Maiden;

import net.runelite.client.plugins.theatre.Maiden.MaidenMaxHit;
import net.runelite.client.plugins.theatre.TheatreConfig;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class MaidenMaxHitOverlay extends OverlayPanel {
    private final Maiden maidenHandler;
    private final TheatreConfig config;

    @Inject
    private MaidenMaxHitOverlay(Maiden maidenHandler, TheatreConfig config) {
        this.maidenHandler = maidenHandler;
        this.config = config;
        this.setPriority(OverlayPriority.HIGH);
        this.setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.panelComponent.setPreferredSize(new Dimension(85, 0));
    }

    public Dimension render(Graphics2D graphics) {
        if (this.config.maidenMaxHitOverlay() != MaidenMaxHit.OFF && this.maidenHandler.getMaidenNPC() != null) {
            //byte max = this.maidenHandler.getMaxHit();
            //byte elyMax = (byte)(max - (int)Math.floor((double)max * 0.25D));
            int noPrayerMaxHit = (int)Math.floor(maidenHandler.getMaxHit());
            int prayerMaxHit = noPrayerMaxHit / 2;
            int elyMaxHit = prayerMaxHit - (int)Math.floor((double)prayerMaxHit * 0.25D);
            LineComponent reg = LineComponent.builder().left("Max Hit:").leftColor(Color.WHITE).right(Integer.toString(prayerMaxHit)).rightColor(Color.GREEN).build();
            LineComponent ely = LineComponent.builder().left("Ely Max Hit:").leftColor(Color.WHITE).right(Integer.toString(elyMaxHit)).rightColor(Color.GREEN).build();
            switch(this.config.maidenMaxHitOverlay()) {
                case REGULAR:
                    this.panelComponent.getChildren().add(reg);
                    break;
                case ELY:
                    this.panelComponent.getChildren().add(ely);
                    break;
                case BOTH:
                    this.panelComponent.getChildren().add(reg);
                    this.panelComponent.getChildren().add(ely);
                    break;
                default:
                    throw new IllegalStateException("Invalid 'maidenMaxHit' config state -> state: " + this.config.maidenMaxHitOverlay().getName());
            }

            return super.render(graphics);
        } else {
            return null;
        }
    }
}
