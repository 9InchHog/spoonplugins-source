package net.runelite.client.plugins.spoontileindicators;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.util.ImageUtil;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class sTileIndicatorsOverlay extends Overlay {
	private final Client client;

	private final sTileIndicatorsConfig config;

	private final BufferedImage ARROW_ICON;
	private LocalPoint lastDestination;
	private int gameCycle;

	@Inject
	private sTileIndicatorsOverlay(Client client, sTileIndicatorsConfig config) {
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.HIGHEST);

		ARROW_ICON = ImageUtil.loadImageResource(sTileIndicatorsPlugin.class, "arrow.png");
	}

	public Dimension render(Graphics2D graphics) {
		Color fillColor;
		if (config.highlightHoveredTile())
		{
			// If we have tile "selected" render it
			if (client.getSelectedSceneTile() != null)
			{
				if (config.hoveredTileFillColor()) {
					fillColor = config.highlightHoveredColor();
				} else {
					fillColor = Color.BLACK;
				}
				renderTile(graphics, client.getSelectedSceneTile().getLocalLocation(), config.highlightHoveredColor(), config.hoveredTileBorderWidth(),
						config.hoveredTileOpacity(), config.highlightHoveredColor().getAlpha(), fillColor);
			}
		}
		if (this.config.highlightDestinationTile()) {
			if (lastDestination == null || !lastDestination.equals(client.getLocalDestinationLocation()))
			{
				gameCycle = client.getGameCycle();
				lastDestination = client.getLocalDestinationLocation();
			}

			if (config.destinationTileFillColor()) {
				fillColor = config.highlightDestinationColor();
			} else {
				fillColor = Color.BLACK;
			}
			if (config.highlightDestinationStyle() == sTileIndicatorsConfig.TileStyle.DEFAULT) {
				renderTile(graphics, this.client.getLocalDestinationLocation(), this.config.highlightDestinationColor(), config.destinationTileBorderWidth(),
						config.destinationTileOpacity(), config.highlightDestinationColor().getAlpha(), fillColor);
			} else if (config.highlightDestinationStyle() == sTileIndicatorsConfig.TileStyle.RS3) {
				renderRS3Tile(graphics, client.getLocalDestinationLocation(), config.highlightDestinationColor(), true);
			} else {
				renderRS3Tile(graphics, client.getLocalDestinationLocation(), config.highlightDestinationColor(), false);
			}
		}
		if (this.config.highlightCurrentTile()) {
			WorldPoint playerPos = this.client.getLocalPlayer().getWorldLocation();
			if (playerPos == null)
				return null;
			LocalPoint playerPosLocal = LocalPoint.fromWorld(this.client, playerPos);
			if (playerPosLocal == null)
				return null;
			if (config.trueTileFillColor()) {
				fillColor = config.highlightCurrentColor();
			} else {
				fillColor = Color.BLACK;
			}
			renderTile(graphics, playerPosLocal, config.highlightCurrentColor(), config.trueTileBorderWidth(), config.trueTileOpacity(), config.highlightCurrentColor().getAlpha(), fillColor);
		}
		if (this.config.highlightOtherCurrentTile()) {
			for (Player player : client.getPlayers()) {
				if (player != null && player.getName() != null && !player.getName().equals(this.client.getLocalPlayer().getName())) {
					WorldPoint playerPos = player.getWorldLocation();
					if (playerPos != null) {
						LocalPoint playerPosLocal = LocalPoint.fromWorld(this.client, playerPos);
						if (playerPosLocal != null) {
							if (config.trueTileFillColor()) {
								fillColor = config.highlightOtherCurrentColor();
							} else {
								fillColor = Color.BLACK;
							}
							renderTile(graphics, playerPosLocal, config.highlightOtherCurrentColor(), config.trueTileBorderWidth(), config.trueTileOpacity(), config.highlightOtherCurrentColor().getAlpha(), fillColor);
						}
					}
				}
			}
		}
		return null;
	}

	private void renderTile(Graphics2D graphics, LocalPoint dest, Color color, final double borderWidth, int opacity, int outlineAlpha, Color fillColor) {
		if (dest == null)
			return;
		Polygon poly = Perspective.getCanvasTilePoly(this.client, dest);
		if (poly == null)
			return;
		if (this.config.antiAlias()) {
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		} else
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		if (borderWidth == 0) {
			outlineAlpha = 0;
		}
		graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
		graphics.setStroke(new BasicStroke((float) borderWidth));
		graphics.draw(poly);
		graphics.setColor(new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), opacity));
		graphics.fill(poly);
	}

	private void renderRS3Tile(final Graphics2D graphics, final LocalPoint dest, final Color color, boolean drawArrow)
	{
		if (dest == null)
		{
			return;
		}
		double size = 0.65 * (Math.min(5.0, client.getGameCycle() - gameCycle) / 5.0);

		final Polygon poly = getCanvasTargetTileAreaPoly(client, dest, size, client.getPlane(), 10);
		final Polygon shadow = getCanvasTargetTileAreaPoly(client, dest, size, client.getPlane(), 0);
		Point canvasLoc = Perspective.getCanvasImageLocation(client, dest, ARROW_ICON, 150 + (int) (20 * Math.sin(client.getGameCycle() / 10.0)));

		if (poly != null)
		{

			final Stroke originalStroke = graphics.getStroke();
			graphics.setStroke(new BasicStroke((float) config.destinationTileBorderWidth()));
			graphics.setColor(new Color(0x8D000000, true));
			graphics.draw(shadow);
			graphics.setColor(color);
			graphics.draw(poly);
			graphics.setStroke(originalStroke);
		}

		if (canvasLoc != null && drawArrow && shadow != null)
		{
			// TODO: improve scale as you zoom out
			double imageScale = 0.8 * Math.min(client.get3dZoom() / 500.0, 1);
			graphics.drawImage(ARROW_ICON, (int) (shadow.getBounds().width / 2 + shadow.getBounds().x - ARROW_ICON.getWidth() * imageScale / 2), canvasLoc.getY(), (int) (ARROW_ICON.getWidth() * imageScale), (int) (ARROW_ICON.getHeight() * imageScale), null);
		}
	}

	public static Polygon getCanvasTargetTileAreaPoly(
			@Nonnull Client client,
			@Nonnull LocalPoint localLocation,
			double size,
			int plane,
			int zOffset)
	{
		final int sceneX = localLocation.getSceneX();
		final int sceneY = localLocation.getSceneY();

		if (sceneX < 0 || sceneY < 0 || sceneX >= Perspective.SCENE_SIZE || sceneY >= Perspective.SCENE_SIZE)
		{
			return null;
		}

		Polygon poly = new Polygon();
		int resolution = 64;
		final int height = Perspective.getTileHeight(client, localLocation, plane) - zOffset;

		for (int i = 0; i < resolution; i++) {
			double angle = ((float) i / resolution) * 2 * Math.PI;
			double offsetX = Math.cos(angle);
			double offsetY = Math.sin(angle);
			int x = (int) (localLocation.getX() + (offsetX * Perspective.LOCAL_TILE_SIZE * size));
			int y = (int) (localLocation.getY() + (offsetY * Perspective.LOCAL_TILE_SIZE * size));
			net.runelite.api.Point p = Perspective.localToCanvas(client, x, y, height);
			if (p == null) {
				continue;
			}
			poly.addPoint(p.getX(), p.getY());

		}

		return poly;
	}
}
