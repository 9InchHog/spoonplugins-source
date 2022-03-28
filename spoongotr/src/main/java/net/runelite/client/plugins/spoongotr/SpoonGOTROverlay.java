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
						Color outline;
						Color fill;
						if (obj.getId() >= 43701 && obj.getId() <= 43704) {
							outline = new Color(config.eleOverlayColor().getRed(), config.eleOverlayColor().getGreen(), config.eleOverlayColor().getBlue(), 255);
							fill = config.eleOverlayColor();
						} else {
							outline = new Color(config.cataOverlayColor().getRed(), config.cataOverlayColor().getGreen(), config.cataOverlayColor().getBlue(), 255);
							fill = config.cataOverlayColor();
						}
						graphics.setColor(outline);
						graphics.draw(obj.getClickbox());
						graphics.setColor(fill);
						graphics.fill(obj.getClickbox());
						if (config.showGuardianRune())
							OverlayUtil.renderImageLocation(client, graphics, obj.getLocalLocation(), itemManager.getImage(plugin.runeIdMap.get(obj.getId())), 140);
					}
				}
			}

			if (config.hugePortal() && plugin.hugePortal != null && plugin.portalTicks > 0 && plugin.hugePortal.getClickbox() != null) {
				graphics.setColor(new Color(config.hugePortalColor().getRed(), config.hugePortalColor().getGreen(), config.hugePortalColor().getBlue(), 255));
				graphics.draw(plugin.hugePortal.getClickbox());
				graphics.setColor(config.hugePortalColor());
				graphics.fill(plugin.hugePortal.getClickbox());

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
				graphics.setColor(new Color(config.bigGuyColor().getRed(), config.bigGuyColor().getGreen(), config.bigGuyColor().getBlue(), 255));
				graphics.draw(plugin.bigGuy.getConvexHull());
				graphics.setColor(config.bigGuyColor());
				graphics.fill(plugin.bigGuy.getConvexHull());
			}

			if (config.essencePileOverlay() && plugin.eleGuardian != null && plugin.cataGuardian != null && plugin.hasCell
					&& plugin.eleGuardian.getClickbox() != null && plugin.cataGuardian.getClickbox() != null && !plugin.guardianWidgetText.equals("")) {
				int spawnedGuards = Integer.parseInt(plugin.guardianWidgetText.split("/")[0]);
				int guardCap = Integer.parseInt(plugin.guardianWidgetText.split("/")[1]);
				if (spawnedGuards < guardCap) {
					graphics.setColor(new Color(config.essencePileColor().getRed(), config.essencePileColor().getGreen(), config.essencePileColor().getBlue(), 255));
					graphics.draw(plugin.eleGuardian.getClickbox());
					graphics.draw(plugin.cataGuardian.getClickbox());
					graphics.setColor(config.essencePileColor());
					graphics.fill(plugin.eleGuardian.getClickbox());
					graphics.fill(plugin.cataGuardian.getClickbox());
				}
			}
		}

		if (config.unchargedTableOverlay() && plugin.unchargedCellTable != null && !plugin.hasUnchargedCells && plugin.unchargedCellTable.getClickbox() != null) {
			graphics.setColor(new Color(config.unchargedTableColor().getRed(), config.unchargedTableColor().getGreen(), config.unchargedTableColor().getBlue(), 255));
			graphics.draw(plugin.unchargedCellTable.getClickbox());
			graphics.setColor(config.unchargedTableColor());
			graphics.fill(plugin.unchargedCellTable.getClickbox());
		}
		return null;
	}
}
