package net.runelite.client.plugins.discoonthego;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class DiscoOnTheGoOverlay extends OverlayPanel {
    private final Client client;
    private final DiscoOnTheGoPlugin plugin;
    private final DiscoOnTheGoConfig config;

    @Inject
    private DiscoOnTheGoOverlay(final Client client, final DiscoOnTheGoPlugin plugin, final DiscoOnTheGoConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
		if(config.playerHelper()){
			for(Player player : this.client.getPlayers()){
				Shape box = player.getConvexHull();
				if (box != null) {
					Color color = new Color(config.playerHelperColor().getRed(), config.playerHelperColor().getGreen(), config.playerHelperColor().getBlue(), plugin.pulseOpacity);
					graphics.setColor(color);
					graphics.fill(box);
				}
			}
		}

		if(config.disco() && this.client.getLocalPlayer() != null) {
			WorldPoint playerWp = this.client.getLocalPlayer().getWorldLocation();
			WorldPoint start = new WorldPoint(playerWp.getX() - config.discoSize(), playerWp.getY() - config.discoSize(), playerWp.getPlane());
			int index = 0;
			for (int i = 0; i < ((config.discoSize() * 2) + 1); i++) {
				for (int j = 0; j < ((config.discoSize() * 2) + 1); j++) {
					WorldPoint wp = new WorldPoint(start.getX() + i, start.getY() + j, start.getPlane());
					Polygon poly = Perspective.getCanvasTilePoly(client, LocalPoint.fromWorld(client, wp));
					if (poly != null) {
						renderPoly(graphics, plugin.discoColors.get(index), poly, 100, 100);
					}
					index++;
				}
			}
		}
        return super.render(graphics);
    }

    private void renderPoly(Graphics2D graphics, Color color, Shape polygon, int insideOpacity, int outlineOpacity){
        if (polygon != null){
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineOpacity));
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), insideOpacity));
            graphics.fill(polygon);
        }
    }
}
