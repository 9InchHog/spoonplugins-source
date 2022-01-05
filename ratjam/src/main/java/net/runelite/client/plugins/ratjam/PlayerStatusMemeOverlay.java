package net.runelite.client.plugins.ratjam;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayerStatusMemeOverlay extends OverlayPanel {
    private final Client client;
    private final PlayerStatusMemePlugin plugin;

    @Inject
    private PlayerStatusMemeOverlay(final Client client, final PlayerStatusMemePlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
    	if(this.client.getLocalPlayer() != null) {
			Point base = Perspective.localToCanvas(this.client, this.client.getLocalPlayer().getLocalLocation(), this.client.getPlane(), this.client.getLocalPlayer().getLogicalHeight() / 2 - 10);
			if (base != null) {
				base = new Point(0, 0);
				BufferedImage icon = ImageUtil.loadImageResource(PlayerStatusMemePlugin.class, plugin.ratJamFrame + ".png");
				if (icon != null) {
					graphics.drawImage(icon, base.getX(), base.getY(), this.client.getCanvasWidth(), this.client.getCanvasHeight(), null);
				}
			}
		}
        return super.render(graphics);
    }
}
