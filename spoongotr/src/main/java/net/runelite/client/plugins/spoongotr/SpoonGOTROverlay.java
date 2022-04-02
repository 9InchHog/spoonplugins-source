package net.runelite.client.plugins.spoongotr;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Singleton
class SpoonGOTROverlay extends Overlay {
	private final SpoonGOTRPlugin plugin;
	private final Client client;
	private final SpoonGOTRConfig config;
	private final ItemManager itemManager;

	@Inject
	SpoonGOTROverlay(final SpoonGOTRPlugin plugin, final Client client, final SpoonGOTRConfig config, ItemManager itemManager) {
		this.config = config;
		this.plugin = plugin;
		this.client = client;
		this.itemManager = itemManager;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		if (plugin.gameStart) {
			if (config.guardianOverlay() && plugin.guardians.size() > 0) {
				for (GameObject obj : plugin.guardians) {
					Animation animation = ((DynamicObject) obj.getRenderable()).getAnimation();
					if (animation != null && animation.getId() == 9363 && obj.getClickbox() != null) {
						Color color = obj.getId() >= 43701 && obj.getId() <= 43704 ? config.eleOverlayColor() : config.cataOverlayColor();
						drawClickbox(graphics, color, obj.getClickbox());
						if (config.showGuardianRune())
							OverlayUtil.renderImageLocation(client, graphics, obj.getLocalLocation(), itemManager.getImage(plugin.runeIdMap.get(obj.getId())), 140);
					}
				}
			}

			if (config.hugePortal() && plugin.hugePortal != null && plugin.portalTicks > 0 && plugin.hugePortal.getClickbox() != null) {
				drawClickbox(graphics, config.hugePortalColor(), plugin.hugePortal.getClickbox());

				Font oldFont = graphics.getFont();
				graphics.setFont(new Font("Arial", Font.BOLD, 12));
				String text = String.valueOf(plugin.portalTicks);
				Point textLoc = plugin.hugePortal.getCanvasTextLocation(graphics, text, 0);
				if (textLoc != null) {
					Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
					OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
					OverlayUtil.renderTextLocation(graphics, textLoc, text, Color.WHITE);
				}
				graphics.setFont(oldFont);
			}

			if (config.bigGuyOverlay() && plugin.bigGuy != null && plugin.hasGuardianStone && plugin.bigGuy.getConvexHull() != null) {
				drawClickbox(graphics, config.bigGuyColor(), plugin.bigGuy.getConvexHull());
			}

			if (config.essencePileOverlay() && plugin.eleGuardian != null && plugin.cataGuardian != null && plugin.hasCell
					&& plugin.eleGuardian.getClickbox() != null && plugin.cataGuardian.getClickbox() != null && !plugin.guardianWidgetText.equals("")) {
				int spawnedGuards = Integer.parseInt(plugin.guardianWidgetText.split("/")[0]);
				int guardCap = Integer.parseInt(plugin.guardianWidgetText.split("/")[1]);
				if (spawnedGuards < guardCap) {
					drawClickbox(graphics, config.essencePileColor(), plugin.eleGuardian.getClickbox());
					drawClickbox(graphics, config.essencePileColor(), plugin.cataGuardian.getClickbox());
				}
			}

			if (config.guardiansOfTheRave() != SpoonGOTRConfig.RaveMode.OFF && plugin.guardians.size() > 0) {
				if(plugin.bigGuy != null && plugin.bigGuy.getComposition() != null) {
					drawRaveFloor(graphics, plugin.bigGuy.getWorldLocation(), plugin.bigGuy.getComposition().getSize(), 0);
				}

				if (config.guardiansOfTheRave() != SpoonGOTRConfig.RaveMode.RAVE) {
					GameObject guardian = plugin.guardians.get(plugin.rngGuardianNum);
					Shape shape = guardian.getConvexHull();
					if (shape != null) {
						graphics.setColor(plugin.raveGuardianColor);
						graphics.fill(shape);
					}

					drawRaveFloor(graphics, plugin.unchargedCellTable.getWorldLocation(), 2, 0);
					drawRaveFloor(graphics, plugin.eleGuardian.getWorldLocation(), 2, 0);
					drawRaveFloor(graphics, plugin.cataGuardian.getWorldLocation(), 2, 0);
				}

				if (config.guardiansOfTheRave() == SpoonGOTRConfig.RaveMode.HELP) {
					int index = 0;
					for (GameObject obj : plugin.guardians) {
						drawRaveFloor(graphics, obj.getWorldLocation(), 2, index);
						index += 11;
					}
				}
			}
		}

		if (config.unchargedTableOverlay() && plugin.unchargedCellTable != null && !plugin.hasUnchargedCells && plugin.unchargedCellTable.getClickbox() != null) {
			drawClickbox(graphics, config.unchargedTableColor(), plugin.unchargedCellTable.getClickbox());
		}
		return null;
	}

	private void drawClickbox(Graphics2D graphics, Color color, Shape shape) {
		graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 255));
		graphics.draw(shape);
		graphics.setColor(color);
		graphics.fill(shape);
	}

	private void drawRaveFloor(Graphics2D graphics, WorldPoint wp, int size, int colorIndexStart) {
		int index = colorIndexStart;
		for (int y=-1; y<size+1; y++) {
			int increment = y == -1 || y == size ? 1 : size + 1;
			for (int x=-1; x<size+1; x += increment) {
				WorldPoint w = new WorldPoint(wp.getX() + x, wp.getY() + y, client.getPlane());
				LocalPoint lp = LocalPoint.fromWorld(client, w);
				if (lp != null) {
					Polygon poly = Perspective.getCanvasTilePoly(client, lp);
					if (poly != null) {
						Color color = plugin.guardianColors.get(index);
						graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
						graphics.setStroke(new BasicStroke(2));
						graphics.draw(poly);
						graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
						graphics.fill(poly);
					}
				}
				index++;
			}
			index++;
		}
	}
}
