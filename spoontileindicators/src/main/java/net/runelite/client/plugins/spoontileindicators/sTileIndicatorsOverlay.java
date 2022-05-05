package net.runelite.client.plugins.spoontileindicators;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.RaveUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class sTileIndicatorsOverlay extends Overlay {
	private final Client client;

	private final sTileIndicatorsConfig config;

	private final RaveUtils raveUtils;

	private final BufferedImage ARROW_ICON;
	private LocalPoint lastDestination;
	private int gameCycle;

	@Inject
	private sTileIndicatorsOverlay(Client client, sTileIndicatorsConfig config, RaveUtils raveUtils) {
		this.client = client;
		this.config = config;
		this.raveUtils = raveUtils;
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
			} else renderRS3Tile(graphics, client.getLocalDestinationLocation(), config.highlightDestinationColor(), config.highlightDestinationStyle() == sTileIndicatorsConfig.TileStyle.RS3);
		}
		if (this.config.highlightCurrentTile()) {
			WorldPoint playerPos = this.client.getLocalPlayer().getWorldLocation();
			if (playerPos == null) {
				return null;
			}
			LocalPoint playerPosLocal = LocalPoint.fromWorld(this.client, playerPos);
			if (playerPosLocal == null) {
				return null;
			}
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
		if (config.overlaysBelowPlayer() && client.isGpu()) {
			removeActor(graphics, client.getLocalPlayer());
		}
		return null;
	}

	private void renderTile(Graphics2D graphics, LocalPoint dest, Color color, final double borderWidth, int opacity, int outlineAlpha, Color fillColor) {
		if (dest == null) {
			return;
		}
		Polygon poly = Perspective.getCanvasTilePoly(this.client, dest);
		if (poly == null) {
			return;
		}
		if (this.config.antiAlias()) {
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		if (config.rave()) {
			color = raveUtils.getColor(dest.hashCode(), true);
			Color raveFillColor = raveUtils.getColor(dest.hashCode(), true);
			fillColor = new Color(raveFillColor.getRed(), raveFillColor.getGreen(), raveFillColor.getBlue(), fillColor.getAlpha());
		}
		if (borderWidth == 0) {
			outlineAlpha = 0;
		}
		graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
		graphics.setStroke(new BasicStroke((float) borderWidth));
		graphics.draw(poly);
		graphics.setColor(new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), opacity));
		graphics.fill(poly);
	}

	private void renderRS3Tile(final Graphics2D graphics, final LocalPoint dest, Color color, boolean drawArrow)
	{
		if (dest == null)
		{
			return;
		}
		double size = 0.65 * (Math.min(5.0, client.getGameCycle() - gameCycle) / 5.0);

		final Polygon poly = getCanvasTargetTileAreaPoly(client, dest, size, client.getPlane(), 10);
		final Polygon shadow = getCanvasTargetTileAreaPoly(client, dest, size, client.getPlane(), 0);
		Point canvasLoc = Perspective.getCanvasImageLocation(client, dest, ARROW_ICON, 150 + (int) (20 * Math.sin(client.getGameCycle() / 10.0)));

		if (config.rave()) {
			color = raveUtils.getColor(dest.hashCode(), true);
		}

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

	private void removeActor(final Graphics2D graphics, final Actor actor) {
		final int clipX1 = client.getViewportXOffset();
		final int clipY1 = client.getViewportYOffset();
		final int clipX2 = client.getViewportWidth() + clipX1;
		final int clipY2 = client.getViewportHeight() + clipY1;
		Object origAA = graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		graphics.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		Model model = actor.getModel();
		int vCount = model.getVerticesCount();
		int[] x3d = model.getVerticesX();
		int[] y3d = model.getVerticesY();
		int[] z3d = model.getVerticesZ();

		int[] x2d = new int[vCount];
		int[] y2d = new int[vCount];

		int size = 1;
		if (actor instanceof NPC)
		{
			NPCComposition composition = ((NPC) actor).getTransformedComposition();
			if (composition != null)
			{
				size = composition.getSize();
			}
		}

		final LocalPoint lp = actor.getLocalLocation();

		final int localX = lp.getX();
		final int localY = lp.getY();
		final int northEastX = lp.getX() + Perspective.LOCAL_TILE_SIZE * (size - 1) / 2;
		final int northEastY = lp.getY() + Perspective.LOCAL_TILE_SIZE * (size - 1) / 2;
		final LocalPoint northEastLp = new LocalPoint(northEastX, northEastY);
		int localZ = Perspective.getTileHeight(client, northEastLp, client.getPlane());
		int rotation = actor.getCurrentOrientation();

		Perspective.modelToCanvas(client, vCount, localX, localY, localZ, rotation, x3d, z3d, y3d, x2d, y2d);

		boolean anyVisible = false;

		for (int i = 0; i < vCount; i++) {
			int x = x2d[i];
			int y = y2d[i];

			boolean visibleX = x >= clipX1 && x < clipX2;
			boolean visibleY = y >= clipY1 && y < clipY2;
			anyVisible |= visibleX && visibleY;
		}

		if (!anyVisible) return;

		int tCount = model.getFaceCount();
		int[] tx = model.getFaceIndices1();
		int[] ty = model.getFaceIndices2();
		int[] tz = model.getFaceIndices3();

		Composite orig = graphics.getComposite();
		graphics.setComposite(AlphaComposite.Clear);
		graphics.setColor(Color.WHITE);
		for (int i = 0; i < tCount; i++) {
			// Cull tris facing away from the camera
			if (getTriDirection(x2d[tx[i]], y2d[tx[i]], x2d[ty[i]], y2d[ty[i]], x2d[tz[i]], y2d[tz[i]]) >= 0)
			{
				continue;
			}
			Polygon p = new Polygon(
					new int[]{x2d[tx[i]], x2d[ty[i]], x2d[tz[i]]},
					new int[]{y2d[tx[i]], y2d[ty[i]], y2d[tz[i]]},
					3);
			graphics.fill(p);

		}
		graphics.setComposite(orig);
		graphics.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				origAA);
	}

	private int getTriDirection(int x1, int y1, int x2, int y2, int x3, int y3) {
		int x4 = x2 - x1;
		int y4 = y2 - y1;
		int x5 = x3 - x1;
		int y5 = y3 - y1;
		return x4 * y5 - y4 * x5;
	}
}
