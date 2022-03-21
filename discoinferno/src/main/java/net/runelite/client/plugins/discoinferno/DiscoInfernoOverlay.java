package net.runelite.client.plugins.discoinferno;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class DiscoInfernoOverlay extends OverlayPanel {
    private final Client client;
    private final DiscoInfernoPlugin plugin;
    private final DiscoInfernoConfig config;

    @Inject
    private DiscoInfernoOverlay(final Client client, final DiscoInfernoPlugin plugin, final DiscoInfernoConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
		if (config.boogie()){
			ArrayList<Color> colorList = new ArrayList<>(Arrays.asList(Color.blue, Color.red, Color.green, Color.yellow, Color.cyan, Color.magenta, Color.orange));
			if(this.client.getScene().getTiles().length > 0){
				for(int i=0; i<1000; i++){
					Random rand = new Random();
					int rng = rand.nextInt(6);
					plugin.discoColors.add(colorList.get(rng));
				}
			}

			if (plugin.isInInferno()) {
				renderTile(graphics);
			}
		}
        return super.render(graphics);
    }

	private void renderTile(Graphics2D graphics) {
    	int z = this.client.getPlane();
		int index = 0;

		int minX = 0;
		int maxX = 0;
		int minY = 0;
		int maxY = 0;
		if(plugin.entered && plugin.sceneX == 0 && plugin.sceneY == 0){
			minX = 41;
			maxX = 70;
			minY = 33;
			maxY = 63;
		}else {
			plugin.sceneX = -1;
			plugin.sceneY = -1;
			minX = 33;
			maxX = 62;
			minY = 25;
			maxY = 55;
		}
		for (int x = minX; x < maxX; ++x) {
			for (int y = minY; y < maxY; ++y) {
				Tile[][][] tiles = this.client.getScene().getTiles();
				Tile tile = tiles[z][x][y];
				WorldPoint wp = tile.getWorldLocation();
				Player player = client.getLocalPlayer();
				if (player != null) {
					Polygon poly = Perspective.getCanvasTilePoly(client, LocalPoint.fromWorld(client, wp));
					if (poly != null) {
						renderPolygon(graphics, poly, plugin.discoColors.get(index));
					}
					index++;
				}
			}
		}
	}

	public static void renderPolygon(Graphics2D graphics, Shape poly, Color color) {
		graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
		final Stroke originalStroke = graphics.getStroke();
		graphics.setStroke(new BasicStroke(2));
		graphics.draw(poly);
		graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
		graphics.fill(poly);
		graphics.setStroke(originalStroke);
	}
}
