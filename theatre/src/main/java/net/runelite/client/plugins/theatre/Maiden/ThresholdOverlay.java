//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.runelite.client.plugins.theatre.Maiden;

import net.runelite.client.plugins.theatre.TheatreConfig;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class ThresholdOverlay extends OverlayPanel {
    private final Maiden maidenHandler;
    private final TheatreConfig config;

    @Inject
    private ThresholdOverlay(Maiden maidenHandler, TheatreConfig config) {
        this.maidenHandler = maidenHandler;
        this.config = config;
        this.setPriority(OverlayPriority.HIGH);
        this.setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.panelComponent.setPreferredSize(new Dimension(100, 0));
    }

    public Dimension render(Graphics2D graphics) {
        if (this.config.maidenProcThreshold() && this.maidenHandler.getMaidenNPC() != null && this.maidenHandler.getMaidenNPC().getId() != 8363) {
            if (this.maidenHandler.getRealMaidenHp() >= this.maidenHandler.getThresholdHp()) {
                String maidenThresholdStr = Integer.toString(this.maidenHandler.getRealMaidenHp() - this.maidenHandler.getThresholdHp());
                this.panelComponent.getChildren().add(LineComponent.builder().left("DMG Left:").leftColor(Color.WHITE).right(maidenThresholdStr).rightColor(Color.GREEN).build());
            }

            return super.render(graphics);
        } else {
            return null;
        }
    }
}
