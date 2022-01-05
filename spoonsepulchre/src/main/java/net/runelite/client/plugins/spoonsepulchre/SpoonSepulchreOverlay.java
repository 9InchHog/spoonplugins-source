package net.runelite.client.plugins.spoonsepulchre;

import com.google.common.base.Strings;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

public class SpoonSepulchreOverlay extends Overlay
{
	private static final int CROSSBOW_STATUE_ANIM_DEFAULT = 8681;
	private static final int CROSSBOW_STATUE_ANIM_FINAL = 8685;

	private final Client client;
	private final SpoonSepulchrePlugin plugin;
	private final SpoonSepulchreConfig config;
	private final ModelOutlineRenderer modelOutlineRenderer;

	private Player player;

	@Inject
	SpoonSepulchreOverlay(final Client client, final SpoonSepulchrePlugin plugin, final SpoonSepulchreConfig config, final ModelOutlineRenderer modelOutlineRenderer) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.modelOutlineRenderer = modelOutlineRenderer;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(final Graphics2D graphics2D) {
		if (!plugin.isPlayerInSepulchre()) {
			return null;
		}

		player = client.getLocalPlayer();

		if (player == null) {
			return null;
		}

		renderArrows(graphics2D);

		renderSwords(graphics2D);

		renderCrossbowStatues(graphics2D);

		renderWizardStatues(graphics2D);

		//renderServerTile(graphics2D);

		renderTeleports(graphics2D);

		return null;
	}

	private void renderArrows(final Graphics2D graphics2D) {
		final SpoonSepulchreConfig.HighlightMode highlightMode = config.highlightArrows();

		if (highlightMode.equals(SpoonSepulchreConfig.HighlightMode.NONE) || plugin.getArrows().isEmpty()) {
			return;
		}

		for (final NPC npc : plugin.getArrows()) {
			if (isOutsideRenderDistance(npc.getLocalLocation())) {
				continue;
			}
			renderNpcHighlight(graphics2D, config.arrowsOutlineColor(), config.arrowsFillColor(), npc, highlightMode);
		}
	}

	private void renderSwords(final Graphics2D graphics2D)
	{
		final SpoonSepulchreConfig.HighlightMode highlightMode = config.highlightSwords();

		if (highlightMode.equals(SpoonSepulchreConfig.HighlightMode.NONE) || plugin.getSwords().isEmpty()) {
			return;
		}
		for (final NPC npc : plugin.getSwords()) {
			if (isOutsideRenderDistance(npc.getLocalLocation())) {
				continue;
			}
			renderNpcHighlight(graphics2D, config.swordsOutlineColor(), config.swordsFillColor(), npc, highlightMode);
		}
	}

	private void renderNpcHighlight(final Graphics2D graphics2D, final Color outlineColor, final Color fillColor, final NPC npc, final SpoonSepulchreConfig.HighlightMode highlightMode) {
		final NPCComposition npcComposition = npc.getTransformedComposition();

		if (npcComposition == null) {
			return;
		}

		if (highlightMode.equals(SpoonSepulchreConfig.HighlightMode.OUTLINE)) {
			modelOutlineRenderer.drawOutline(npc, config.outlineWidth(), outlineColor, 4);
		}

		if (highlightMode.equals(SpoonSepulchreConfig.HighlightMode.TILE)) {
			int size = 1;

			final NPCComposition composition = npc.getTransformedComposition();

			if (composition != null) {
				size = composition.getSize();
			}

			final LocalPoint localPoint = npc.getLocalLocation();
			final Polygon polygon = Perspective.getCanvasTileAreaPoly(client, localPoint, size);

			if (polygon != null) {
				drawStrokeAndFill(graphics2D, outlineColor, fillColor, config.tileOutlineWidth(), polygon);
			}
		}

		if (highlightMode.equals(SpoonSepulchreConfig.HighlightMode.TL)) {
			final NPCComposition composition = npc.getTransformedComposition();
			int size = 1;
			if (composition != null) {
				size = composition.getSize();
			}
			LocalPoint lp = LocalPoint.fromWorld(this.client, npc.getWorldLocation());
			if (lp != null) {
				lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
				Polygon polygon = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
				if (polygon != null) {
					drawStrokeAndFill(graphics2D, outlineColor, fillColor, config.tileOutlineWidth(), polygon);
				}
			}
		}
	}

	private void renderCrossbowStatues(final Graphics2D graphics2D) {
		if (config.highlightCrossbowStatues() == SpoonSepulchreConfig.crossbowMode.OFF || plugin.getCrossbowStatues().isEmpty()) {
			return;
		}

		for (final GameObject gameObject : plugin.getCrossbowStatues()) {
			if (!gameObject.getWorldLocation().isInScene(client)
				|| isOutsideRenderDistance(gameObject.getLocalLocation())) {
				continue;
			}

			final DynamicObject dynamicObject = (DynamicObject) gameObject.getRenderable();

			if (dynamicObject.getAnimationID() == CROSSBOW_STATUE_ANIM_DEFAULT || dynamicObject.getAnimationID() == CROSSBOW_STATUE_ANIM_FINAL) {
				continue;
			}

			if (config.highlightCrossbowStatues() == SpoonSepulchreConfig.crossbowMode.HULL) {
				final Shape shape = gameObject.getConvexHull();

				if (shape != null) {
					drawStrokeAndFill(graphics2D, config.crossbowStatueOutlineColor(), config.crossbowStatueFillColor(), 1.0f, shape);
				}
			} else {
				modelOutlineRenderer.drawOutline(gameObject, 2, config.crossbowStatueOutlineColor(), 4);
			}
		}
	}

	private void renderWizardStatues(final Graphics2D graphics2D)
	{
		if (!config.highlightWizardStatues() || plugin.getWizardStatues().isEmpty()) {
			return;
		}

		for (final SpoonSepulchreWizardStatue sepulchreGameObject : plugin.getWizardStatues()) {
			final GameObject gameObject = sepulchreGameObject.getGameObject();

			if (!gameObject.getWorldLocation().isInScene(client) || isOutsideRenderDistance(gameObject.getLocalLocation())) {
				continue;
			}

			final int ticksLeft = sepulchreGameObject.getTicksUntilNextAnimation();

			if (ticksLeft <= 0) {
				continue;
			}

			final String ticksLeftStr = String.valueOf(ticksLeft);

			final Color color = (ticksLeft == 1 ? Color.WHITE : config.wizardStatueTickCounterColor());

			final Point canvasPoint = gameObject.getCanvasTextLocation(graphics2D, ticksLeftStr, 0);

			renderTextLocation(graphics2D, ticksLeftStr, config.wizardFontSize(), config.fontStyle().getFont(), color, canvasPoint, config.wizardFontShadow(), 0);
		}
	}

	/*private void renderServerTile(final Graphics2D graphics2D)
	{
		if (!config.highlightServerTile()) {
			return;
		}

		final WorldPoint worldPoint = player.getWorldLocation();

		if (worldPoint == null) {
			return;
		}

		final LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);

		if (localPoint == null) {
			return;
		}

		final Polygon polygon = Perspective.getCanvasTilePoly(client, localPoint);

		if (polygon == null) {
			return;
		}

		drawStrokeAndFill(graphics2D, config.serverTileOutlineColor(), config.serverTileFillColor(), config.tileOutlineWidth(), polygon);
	}*/

	private void renderTeleports(final Graphics2D graphics2D) {
		int size = 1;
		for (GraphicsObject graphicsObject : this.client.getGraphicsObjects()){
			LocalPoint lp = graphicsObject.getLocation();
			if (lp != null) {
				lp = new LocalPoint(lp.getX(), lp.getY());
				Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
			if (graphicsObject.getId() == 1816 || graphicsObject.getId() == 1800) {
				if (config.highlightYellows()) {
					drawStrokeAndFill(graphics2D, config.badTeleOutline(), config.badTeleFill(), config.tileOutlineWidth(), tilePoly);
				}
			}

			if (graphicsObject.getId() == 1815 || graphicsObject.getId() == 1799) {
				if (config.highlightBlues()) {
						drawStrokeAndFill(graphics2D, config.goodTeleOutline(), config.goodTeleFill(), config.tileOutlineWidth(), tilePoly);
					}
				}
			}
		}
	}

	private boolean isOutsideRenderDistance(final LocalPoint localPoint)
	{
		final int maxDistance = config.renderDistance().getDistance();

		if (maxDistance == 0) {
			return false;
		}

		return localPoint.distanceTo(player.getLocalLocation()) >= maxDistance;
	}

	private static void drawStrokeAndFill(final Graphics2D graphics2D, final Color outlineColor, final Color fillColor, final float strokeWidth, final Shape shape) {
		graphics2D.setColor(outlineColor);
		final Stroke originalStroke = graphics2D.getStroke();
		graphics2D.setStroke(new BasicStroke(strokeWidth));
		graphics2D.draw(shape);
		graphics2D.setColor(fillColor);
		graphics2D.fill(shape);
		graphics2D.setStroke(originalStroke);
	}

	public static void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint, boolean shadows, int yOffset) {
		graphics.setFont(new Font("Arial", fontStyle, fontSize));
		if (canvasPoint != null) {
			final Point canvasCenterPoint = new Point(
				canvasPoint.getX(),
				canvasPoint.getY() + yOffset);
			final Point canvasCenterPoint_shadow = new Point(
				canvasPoint.getX() + 1,
				canvasPoint.getY() + 1 + yOffset);
			if (shadows) {
				renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
			}
			renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
		}
	}

	public static void renderTextLocation(Graphics2D graphics, Point txtLoc, String text, Color color) {
		if (Strings.isNullOrEmpty(text)) {
			return;
		}

		int x = txtLoc.getX();
		int y = txtLoc.getY();

		graphics.setColor(Color.BLACK);
		graphics.drawString(text, x + 1, y + 1);

		graphics.setColor(color);
		graphics.drawString(text, x, y);
	}
}
