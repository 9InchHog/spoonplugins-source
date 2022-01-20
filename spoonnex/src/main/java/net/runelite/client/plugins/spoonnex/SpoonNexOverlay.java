package net.runelite.client.plugins.spoonnex;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
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
	private final SpoonNexConfig config;

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
			graphics.setFont(new Font("Arial", Font.BOLD, config.textSize()));

			LocalPoint nexLp = plugin.nex.npc.getLocalLocation();
			if(!plugin.nex.npc.isDead()) {
				if(config.invulnerableTicks() && plugin.nex.invulnerableTicks > 0) {
					String text = String.valueOf(plugin.nex.invulnerableTicks);
					Point textLoc = plugin.nex.npc.getCanvasTextLocation(graphics, text, 25);
					if (textLoc != null) {
						Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
						OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
						OverlayUtil.renderTextLocation(graphics, textLoc, text, config.forWhy() ? plugin.forWhyColors.get(0) : Color.WHITE);
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
						Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, nexLp, 17);
						if (tilePoly != null) {
							Color color = config.forWhy() ? plugin.forWhyColors.get(1) : Color.ORANGE;
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

				if(plugin.activeMage != null && (config.mageHighlight() != SpoonNexConfig.MageHighlightMode.ARROW || config.mageHighlight() != SpoonNexConfig.MageHighlightMode.OFF)) {
						Color color = config.forWhy() ? plugin.forWhyColors.get(3) : config.mageHighlightColor();
						LocalPoint lp = plugin.activeMage.getLocalLocation();
						if (lp != null) {
							Polygon poly = Perspective.getCanvasTilePoly(client, lp);
							renderPoly(graphics, color, poly);
						}
				}

				if(plugin.covidList.size() > 0 && config.virus() != SpoonNexConfig.VirusMode.OFF) {
					for(Player p : client.getPlayers()) {
						if(plugin.covidList.containsKey(p.getName())) {
							Color color = config.forWhy() ? plugin.forWhyColors.get(4) : new Color(100, 255, 0);
							LocalPoint lp = p.getLocalLocation();
							if (lp != null) {
								Polygon poly = config.virus() == SpoonNexConfig.VirusMode.TILE ? Perspective.getCanvasTilePoly(client, lp) : Perspective.getCanvasTileAreaPoly(client, lp ,3);
								renderPoly(graphics, color, poly);
							}
						}
					}
				}

				if(config.tankHighlight() && plugin.nex.npc.getInteracting() != null) {
					Color color = config.forWhy() ? plugin.forWhyColors.get(5) : config.tankHighlightColor();
					LocalPoint lp = plugin.nex.npc.getInteracting().getLocalLocation();
					if (lp != null) {
						Polygon poly = Perspective.getCanvasTilePoly(client, lp);
						renderPoly(graphics, color, poly);
					}
				}

				if (config.noEscapeRunway() != SpoonNexConfig.NoEscapeRunwayMode.OFF && plugin.nex != null && plugin.nex.currentSpecial.equals("no escape")
						&& (plugin.nex.specialTicksLeft > 0 && plugin.nex.specialTicksLeft <= 4)) {
					WorldPoint leftTop;
					WorldPoint rightBottom;
					WorldPoint middle;
					int angle = plugin.nex.npc.getOrientation();
					int round = angle >>> 9;
					int up = angle & 0x100;
					if (up != 0)
						round++;
					int directionNum = round & 0x3;
					Color color = Color.WHITE;

					if(config.noEscapeRunway() == SpoonNexConfig.NoEscapeRunwayMode.COLOR){
						color = config.noEscapeRunwayColor();
					}else if(config.noEscapeRunway() == SpoonNexConfig.NoEscapeRunwayMode.RAVE){
						color = plugin.raveRunway.get(0);
					}

					WorldPoint bossLoc = plugin.nex.npc.getWorldLocation();
					int raveIndex = 0;
					for (int i = 0; i < 10; i++) {
						//0 = South
						//1 = West
						//2 = North
						//3 = East
						if (directionNum == 0) {
							leftTop = new WorldPoint(bossLoc.getX(), bossLoc.getY() - 1 - i, client.getPlane());
							middle = new WorldPoint(bossLoc.getX() + 1, bossLoc.getY() - 1 - i, client.getPlane());
							rightBottom = new WorldPoint(bossLoc.getX() + 2, bossLoc.getY() - 1 - i, client.getPlane());
							drawRunwayTiles(graphics, leftTop, middle, rightBottom, raveIndex, color);
							raveIndex += 3;
						} else if (directionNum == 1) {
							leftTop = new WorldPoint(bossLoc.getX() - 1 - i, bossLoc.getY() + 2, client.getPlane());
							middle = new WorldPoint(bossLoc.getX() - 1 - i, bossLoc.getY() + 1, client.getPlane());
							rightBottom = new WorldPoint(bossLoc.getX() - 1 - i, bossLoc.getY(), client.getPlane());
							drawRunwayTiles(graphics, leftTop, middle, rightBottom, raveIndex, color);
							raveIndex += 3;
						} else if (directionNum == 2) {
							leftTop = new WorldPoint(bossLoc.getX(), bossLoc.getY() + 3 + i, client.getPlane());
							middle = new WorldPoint(bossLoc.getX() + 1, bossLoc.getY() + 3 + i, client.getPlane());
							rightBottom = new WorldPoint(bossLoc.getX() + 2, bossLoc.getY() + 3 + i, client.getPlane());
							drawRunwayTiles(graphics, leftTop, middle, rightBottom, raveIndex, color);
							raveIndex += 3;
						} else {
							leftTop = new WorldPoint(bossLoc.getX() + 3 + i, bossLoc.getY() + 2, client.getPlane());
							middle = new WorldPoint(bossLoc.getX() + 3 + i, bossLoc.getY() + 1, client.getPlane());
							rightBottom = new WorldPoint(bossLoc.getX() + 3 + i, bossLoc.getY(), client.getPlane());
							drawRunwayTiles(graphics, leftTop, middle, rightBottom, raveIndex, color);
							raveIndex += 3;
						}
					}
				}
			}

			if(plugin.nex != null && plugin.nex.currentSpecial.equals("wrath") && config.wrathWarning()) {
				if (nexLp != null) {
					Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, nexLp, 7);
					if (tilePoly != null) {
						Color color = config.forWhy() ? plugin.forWhyColors.get(6) : Color.RED;
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

	private void drawRunwayTiles(Graphics2D graphics, WorldPoint leftTop, WorldPoint middle, WorldPoint rightBottom, int index, Color color) {
        if(config.noEscapeRunway() == SpoonNexConfig.NoEscapeRunwayMode.RAVEST){
            color = plugin.raveRunway.get(index);
            drawTile(graphics, leftTop, color);
            index++;
            color = plugin.raveRunway.get(index);
            drawTile(graphics, middle, color);
            index++;
            color = plugin.raveRunway.get(index);
            drawTile(graphics, rightBottom, color);
        }else {
            drawTile(graphics, leftTop, color);
            drawTile(graphics, middle, color);
            drawTile(graphics, rightBottom, color);
        }
    }

	protected void drawTile(Graphics2D graphics, WorldPoint point, Color color) {
		if(client.getLocalPlayer() != null) {
			WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
			if (point.distanceTo(playerLocation) < 32) {
				LocalPoint lp = LocalPoint.fromWorld(client, point);
				if (lp != null) {
					Polygon poly = Perspective.getCanvasTilePoly(client, lp);
					if (poly != null) {
						graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
						graphics.setStroke(new BasicStroke(0));
						graphics.draw(poly);
						graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 120));
						graphics.fill(poly);
					}
				}
			}
		}
	}
}
