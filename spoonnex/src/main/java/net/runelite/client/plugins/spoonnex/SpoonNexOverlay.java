package net.runelite.client.plugins.spoonnex;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Singleton
class SpoonNexOverlay extends Overlay {
	private final SpoonNexPlugin plugin;
	private final Client client;
	private SpoonNexConfig config;

	@Inject
	SpoonNexOverlay(final SpoonNexPlugin plugin, final Client client, final SpoonNexConfig config) {
		this.config = config;
		this.plugin = plugin;
		this.client = client;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		if(plugin.nex != null) {
			Font oldFont = graphics.getFont();
			graphics.setFont(new Font("Arial", 1, config.textSize()));

			LocalPoint nexLp = plugin.nex.npc.getLocalLocation();
			if(!plugin.nex.npc.isDead()) {
				if(config.invulnerableTicks() && plugin.nex.invulnerableTicks > 0) {
					String text = String.valueOf(plugin.nex.invulnerableTicks);
					Point textLoc = plugin.nex.npc.getCanvasTextLocation(graphics, text, 25);
					if (textLoc != null) {
						Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
						OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
						OverlayUtil.renderTextLocation(graphics, textLoc, text, Color.WHITE);
					}
				} else if((config.attacksTilSpecial() != SpoonNexConfig.AttacksTilSpecialMode.OFF || (plugin.nex.specialTicksLeft > 0 && config.specialTicks())) && plugin.nex.phase != 5) {
					Color color;
					if(config.forWhy()) {
						color = plugin.forWhyColors.get(0);
					} else {
						color = (plugin.nex.attacksTilSpecial > 0 || config.attacksTilSpecial() == SpoonNexConfig.AttacksTilSpecialMode.ON) ? Color.WHITE : Color.RED;
					}
					String text = "";
					if(config.attacksTilSpecial() != SpoonNexConfig.AttacksTilSpecialMode.OFF) {
						text += Integer.toString(plugin.nex.attacksTilSpecial);
					}
					if(config.specialTicks() && plugin.nex.specialTicksLeft > 0) {
						if(text.equals("")) {
							text += plugin.nex.specialTicksLeft;
						} else {
							text += " : " + plugin.nex.specialTicksLeft;
						}
					}

					Point textLoc = plugin.nex.npc.getCanvasTextLocation(graphics, text, 25);
					if (textLoc != null) {
						Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
						OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
						OverlayUtil.renderTextLocation(graphics, textLoc, text, color);
					}
				}

				int index = 0;
				for(GameObject obj : plugin.gameObjects) {
					if (obj.getId() == 42942 && config.shadowSpots() != SpoonNexConfig.ShadowsMode.OFF && plugin.nex.currentSpecial.equals("shadows") && plugin.nex.phase == 2
							&& plugin.nex.specialTicksLeft > 0) {
						Color color;
						try {
							color = (config.shadowSpots() == SpoonNexConfig.ShadowsMode.RAVE || config.forWhy()) ? plugin.raveObjects.get(index) : Color.CYAN;
						} catch (IndexOutOfBoundsException ex) {
							color = Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F);
						}

						LocalPoint lp = obj.getLocalLocation();
						if(lp != null) {
							Polygon poly = Perspective.getCanvasTilePoly(client, lp);
							renderPoly(graphics, color, poly);

							String textOverlay = Integer.toString(plugin.nex.specialTicksLeft);
							Point textLoc = Perspective.getCanvasTextLocation(client, graphics, lp, textOverlay, 0);
							if (textLoc != null) {
								Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
								OverlayUtil.renderTextLocation(graphics, pointShadow, textOverlay, Color.BLACK);
								OverlayUtil.renderTextLocation(graphics, textLoc, textOverlay, color);
							}
						}
					} else if(obj.getId() == 42944){
						Color color = config.forWhy() ? plugin.raveObjects.get(index) : Color.RED;
						LocalPoint lp = obj.getLocalLocation();
						if(lp != null) {
							Polygon poly = Perspective.getCanvasTilePoly(client, lp);
							renderArea(graphics, color, poly);
						}
					}

					index++;
				}

				if (config.sacrifice() && plugin.nex.currentSpecial.equals("sacrifice") && plugin.nex.phase == 3 && plugin.nex.specialTicksLeft > 0 && plugin.sacrificeTarget) {
					if (nexLp != null) {
						Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, nexLp, 13);
						if (tilePoly != null) {
							Color color = config.forWhy() ? plugin.forWhyColors.get(2) : Color.ORANGE;
							this.renderPoly(graphics, color, tilePoly);
						}
					}
				}

				if (config.containThis() && plugin.nex.currentSpecial.equals("contain") && plugin.nex.phase == 4 && plugin.nex.specialTicksLeft > 0) {
					if (nexLp != null) {
						Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, nexLp, 5);
						if (tilePoly != null) {
							Color color = config.forWhy() ? plugin.forWhyColors.get(2) : Color.RED;
							this.renderPoly(graphics, color, tilePoly);
						}
					}
				}

				if(!plugin.activeMage.equals("") && (config.mageHighlight() != SpoonNexConfig.MageHighlightMode.ARROW || config.mageHighlight() != SpoonNexConfig.MageHighlightMode.OFF)) {
					for (NPC npc : this.client.getNpcs()) {
						if(npc.getName() != null && npc.getName().equals(plugin.activeMage)) {
							Color color = config.forWhy() ? plugin.forWhyColors.get(4) : config.mageHighlightColor();
							LocalPoint lp = npc.getLocalLocation();
							if (lp != null) {
								Polygon poly = Perspective.getCanvasTilePoly(client, lp);
								renderPoly(graphics, color, poly);
							}
						}
					}
				}

				if(plugin.covidList.size() > 0 && config.virus() != SpoonNexConfig.VirusMode.OFF) {
					for(Player p : client.getPlayers()) {
						if(plugin.covidList.containsKey(p.getName())) {
							Color color = config.forWhy() ? plugin.forWhyColors.get(5) : new Color(100, 255, 0);
							LocalPoint lp = p.getLocalLocation();
							if (lp != null) {
								Polygon poly = config.virus() == SpoonNexConfig.VirusMode.TILE ? Perspective.getCanvasTilePoly(client, lp) : Perspective.getCanvasTileAreaPoly(client, lp ,3);
								renderPoly(graphics, color, poly);
							}
						}
					}
				}

				if(config.tankHighlight()) {
					Color color = config.forWhy() ? plugin.forWhyColors.get(6) : config.tankHighlightColor();
					LocalPoint lp = plugin.nex.npc.getInteracting().getLocalLocation();
					if (lp != null) {
						Polygon poly = Perspective.getCanvasTilePoly(client, lp);
						renderPoly(graphics, color, poly);
					}
				}
			}else if(plugin.nex.npc.isDead() && config.wrathWarning()) {
				if (nexLp != null) {
					Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, nexLp, 7);
					if (tilePoly != null) {
						Color color = config.forWhy() ? plugin.forWhyColors.get(3) : Color.RED;
						this.renderPoly(graphics, color, tilePoly);
					}
				}
			}

			if(config.forWhy() && plugin.ratJamTicks > 0) {
				BufferedImage icon = ImageUtil.loadImageResource(SpoonNexPlugin.class, plugin.ratJamFrame + ".png");
				if (icon != null) {
					graphics.drawImage(icon, plugin.ratJamPoint.getX(), plugin.ratJamPoint.getY(), 25, 25, null);
				}
			}
			graphics.setFont(oldFont);
		}
		return null;
	}

	protected void renderPoly(Graphics2D graphics, Color color, Polygon polygon) {
		if (polygon != null) {
			graphics.setColor(color);
			graphics.setStroke(new BasicStroke(2));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
			graphics.fill(polygon);
		}
	}

	protected void renderArea(Graphics2D graphics, Color color, Polygon polygon) {
		if (polygon != null) {
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 64));
			graphics.fill(polygon);
		}
	}
}
