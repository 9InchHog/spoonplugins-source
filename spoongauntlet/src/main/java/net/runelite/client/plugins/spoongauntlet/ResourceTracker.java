package net.runelite.client.plugins.spoongauntlet;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class ResourceTracker extends Overlay {
	private final SpoonGauntletConfig config;
	private final SpoonGauntletPlugin plugin;
	private final Client client;

	@Inject
	private ItemManager itemManager;

	private static final int SEPERATOR = 1;

	@Inject
	private ResourceTracker(SpoonGauntletPlugin plugin, SpoonGauntletConfig config, Client client) {
		this.plugin = plugin;
		this.config = config;
		this.client = client;
		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		if (config.resourceTracker() && GauntletUtils.inRaid(client)) {
			Rectangle bounds = new Rectangle();

			// Determine bounds.
			int maxImageWidth = 0;
			for (GauntletResource resource : plugin.resourcesTracked) {
				BufferedImage image = getIcon(resource.id);
				int countLeft = 0;
				if(resource.id == 23866) {
                    countLeft = plugin.shardsNeeded - resource.count;
                }else if(resource.id == 23878) {
                    countLeft = plugin.barkNeeded - resource.count;
                }else if(resource.id == 23876) {
                    countLeft = plugin.woolNeeded - resource.count;
                }else if(resource.id == 23877) {
                    countLeft = plugin.oreNeeded - resource.count;
                }else if(resource.id == 23871) {
                    countLeft = plugin.framesNeeded - resource.count;
                }else if(resource.id == 23874) {
                    countLeft = plugin.fishNeeded - resource.count;
                }else if(resource.id == 23875) {
                    countLeft = plugin.herbsNeeded - resource.count;
                }

				if (image != null && countLeft > 0) {
					maxImageWidth = Math.max(maxImageWidth, image.getWidth());
					FontMetrics fontMetrics = graphics.getFontMetrics();
					String text = String.valueOf(countLeft);

					if (config.verticalResourceOverlay()) {
						bounds.height += SEPERATOR;
						bounds.height += image.getHeight();
						bounds.width = Math.max(bounds.width, SEPERATOR * 2 + image.getWidth() + fontMetrics.stringWidth(text));
					} else {
						bounds.width += SEPERATOR;
						bounds.width += image.getWidth();
						bounds.height = Math.max(bounds.height, image.getHeight() + fontMetrics.getHeight() + SEPERATOR * 2);
					}
				}
			}

			if (config.verticalResourceOverlay()) {
				bounds.height += SEPERATOR;
			} else {
				bounds.width += SEPERATOR;
			}

			// Draw the icons and text.
			int x = 0; int y = 0;
			for (GauntletResource resource : plugin.resourcesTracked) {
				BufferedImage image = getIcon(resource.id);
				int countLeft = 0;
				if(resource.id == 23866) {
					countLeft = plugin.shardsNeeded - resource.count;
				}else if(resource.id == 23878) {
					countLeft = plugin.barkNeeded - resource.count;
				}else if(resource.id == 23876) {
					countLeft = plugin.woolNeeded - resource.count;
				}else if(resource.id == 23877) {
					countLeft = plugin.oreNeeded - resource.count;
				}else if(resource.id == 23871) {
					countLeft = plugin.framesNeeded - resource.count;
				}else if(resource.id == 23874) {
					countLeft = plugin.fishNeeded - resource.count;
				}else if(resource.id == 23875) {
					countLeft = plugin.herbsNeeded - resource.count;
				}
				if (image != null && countLeft > 0) {
					FontMetrics fontMetrics = graphics.getFontMetrics();
					String text = String.valueOf(countLeft);

					if (config.verticalResourceOverlay()) {
						Point textPoint = new Point(bounds.width - 2 * SEPERATOR - maxImageWidth - fontMetrics.stringWidth(text), y + SEPERATOR + image.getHeight() / 2);
						Point imagePoint = new Point(bounds.width - SEPERATOR - image.getWidth(), y + SEPERATOR);
						y += SEPERATOR + image.getHeight();

						OverlayUtil.renderTextLocation(graphics, textPoint, text, Color.WHITE);
						OverlayUtil.renderImageLocation(graphics, imagePoint, image);
					} else {
						Point textPoint = new Point(x + SEPERATOR + image.getWidth() / 2 - fontMetrics.stringWidth(text) / 2, bounds.height - SEPERATOR);
						Point imagePoint = new Point(x + SEPERATOR, y + SEPERATOR);
						x += SEPERATOR + image.getWidth();

						OverlayUtil.renderTextLocation(graphics, textPoint, text, Color.WHITE);
						OverlayUtil.renderImageLocation(graphics, imagePoint, image);
					}
				}
			}
			return bounds.getSize();
		}
		return null;
	}

	private BufferedImage getIcon(int id) {
		//100 and false are to force the sprite of a stack of crystal shards.
		return itemManager.getImage(id,100,false);
	}
}
