package net.runelite.client.plugins.coxadditions.overlays;

import com.google.common.base.Strings;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;
import net.runelite.client.plugins.coxadditions.utils.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;

@Singleton
public class CoxAdditionsOverlay extends Overlay {
	private final Client client;
	private final CoxAdditionsPlugin plugin;
	private final CoxAdditionsConfig config;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	private CoxAdditionsOverlay(final Client client, final CoxAdditionsPlugin plugin, final CoxAdditionsConfig config, ModelOutlineRenderer modelOutlineRenderer) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.modelOutlineRenderer = modelOutlineRenderer;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		if(this.client.getVar(Varbits.IN_RAID) == 1) {
			if (config.olmCrippleTimer() && plugin.handCripple && plugin.meleeHand != null) {
				NPC olmHand = plugin.meleeHand;
				String textOverlay = Integer.toString(plugin.crippleTimer);
				Point textLoc = olmHand.getCanvasTextLocation(graphics, textOverlay, 50);
				if (textLoc != null) {
					Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
					Font oldFont = graphics.getFont();
					graphics.setFont(new Font("Arial", Font.BOLD, this.config.olmCrippleTextSize()));
					OverlayUtil.renderTextLocation(graphics, pointShadow, textOverlay, Color.BLACK);
					OverlayUtil.renderTextLocation(graphics, textLoc, textOverlay, config.olmCrippleText());
					graphics.setFont(oldFont);
				}
			}

			if (config.shamanSlam() && plugin.shamanInfoList.size() > 0) {
				for (ShamanInfo sInfo : this.plugin.shamanInfoList) {
					if (sInfo.jumping && sInfo.interactingLoc != null) {
						Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, sInfo.interactingLoc, 3);
						renderArea(graphics, config.shamanSlamColor(), tilePoly);
					}
				}
			}

			if (config.shamanSpawn()) {
				for (NPC npc : this.client.getNpcs()) {
					if (npc.getId() == 6768) {
						LocalPoint lp = npc.getLocalLocation();
						Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, 5);
						renderSpawnAoE(graphics, config.shamanSpawnColor(), tilePoly, 1);
					}
				}
			}

			if (config.coxHerbTimer() != CoxAdditionsConfig.CoXHerbTimerMode.OFF && (plugin.coxHerb1 != null || plugin.coxHerb2 != null)) {
				if (config.coxHerbTimer() == CoxAdditionsConfig.CoXHerbTimerMode.TEXT) {
					if (plugin.coxHerb1 != null) {
						GameObject herb = plugin.coxHerb1;
						String text = Integer.toString(plugin.coxHerbTimer1);
						Point textLoc = herb.getCanvasTextLocation(graphics, text, 50);
						Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
						Font oldFont = graphics.getFont();
						graphics.setFont(new Font("Arial", Font.BOLD, config.coxHerbTimerSize()));
						OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
						OverlayUtil.renderTextLocation(graphics, textLoc, text, config.coxHerbTimerColor());
						graphics.setFont(oldFont);
					}

					if (plugin.coxHerb2 != null) {
						GameObject herb = plugin.coxHerb2;
						String text = Integer.toString(plugin.coxHerbTimer2);
						Point textLoc = herb.getCanvasTextLocation(graphics, text, 50);
						Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
						Font oldFont = graphics.getFont();
						graphics.setFont(new Font("Arial", Font.BOLD, config.coxHerbTimerSize()));
						OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
						OverlayUtil.renderTextLocation(graphics, textLoc, text, config.coxHerbTimerColor());
						graphics.setFont(oldFont);
					}
				} else if (config.coxHerbTimer() == CoxAdditionsConfig.CoXHerbTimerMode.PIE) {
					if (plugin.coxHerb1 != null) {
						final Point position = plugin.coxHerb1.getCanvasLocation(100);
						final ProgressPieComponent progressPie = new ProgressPieComponent();
						progressPie.setDiameter(config.coxHerbTimerSize());
						Color colorFill = new Color(config.coxHerbTimerColor().getRed(), config.coxHerbTimerColor().getGreen(), config.coxHerbTimerColor().getBlue(), 100);
						progressPie.setFill(colorFill);
						progressPie.setBorderColor(config.coxHerbTimerColor());
						progressPie.setPosition(position);
						int ticks = 16 - plugin.coxHerbTimer1;
						double progress = 1 - (ticks / 16.0);
						progressPie.setProgress(progress);
						progressPie.render(graphics);
					}

					if (plugin.coxHerb2 != null) {
						final Point position = plugin.coxHerb2.getCanvasLocation(100);
						final ProgressPieComponent progressPie = new ProgressPieComponent();
						progressPie.setDiameter(config.coxHerbTimerSize());
						Color colorFill = new Color(config.coxHerbTimerColor().getRed(), config.coxHerbTimerColor().getGreen(), config.coxHerbTimerColor().getBlue(), 100);
						progressPie.setFill(colorFill);
						progressPie.setBorderColor(config.coxHerbTimerColor());
						progressPie.setPosition(position);
						int ticks = 16 - plugin.coxHerbTimer2;
						double progress = 1 - (ticks / 16.0);
						progressPie.setProgress(progress);
						progressPie.render(graphics);
					}
				}
			}

			if (config.olmHealingPoolTimer() != CoxAdditionsConfig.healingPoolMode.OFF && plugin.olmHealingPools.size() > 0) {
				for (HealingPoolInfo poolInfo : plugin.olmHealingPools) {
					if (poolInfo.lp != null) {
						if (config.olmHealingPoolTimer() == CoxAdditionsConfig.healingPoolMode.TIMER || config.olmHealingPoolTimer() == CoxAdditionsConfig.healingPoolMode.BOTH) {
							String text = String.valueOf(poolInfo.ticks);
							Point point = Perspective.getCanvasTextLocation(client, graphics, poolInfo.lp, text, 0);
							Point pointShadow = new Point(point.getX() + 1, point.getY() + 1);
							Font oldFont = graphics.getFont();
							graphics.setFont(new Font("Arial", Font.BOLD, 12));
							OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
							OverlayUtil.renderTextLocation(graphics, point, text, config.olmHealingPoolTimerColor());
							graphics.setFont(oldFont);
						}

						if (config.olmHealingPoolTimer() == CoxAdditionsConfig.healingPoolMode.OVERLAY || config.olmHealingPoolTimer() == CoxAdditionsConfig.healingPoolMode.BOTH) {
							Polygon poly = Perspective.getCanvasTilePoly(this.client, poolInfo.lp);
							if (poly != null) {
								graphics.setColor(new Color(0, 255, 255, 255));
								graphics.setStroke(new BasicStroke(1.0F));
								graphics.draw(poly);
								graphics.setColor(new Color(0, 255, 255, 10));
								graphics.fill(poly);
							}
						}
					}
				}
			}

			if (config.vasaCrystalTimer() != CoxAdditionsConfig.crystalTimerMode.OFF && plugin.vasaCrystalTicks > 0) {
				for (NPC npc : this.client.getNpcs()) {
					if (npc.getId() == 7568) {
						if (config.vasaCrystalTimer() == CoxAdditionsConfig.crystalTimerMode.BOLD) {
							graphics.setFont(FontManager.getRunescapeBoldFont());
						} else if (config.vasaCrystalTimer() == CoxAdditionsConfig.crystalTimerMode.REGULAR) {
							graphics.setFont(FontManager.getRunescapeFont());
						} else if (config.vasaCrystalTimer() == CoxAdditionsConfig.crystalTimerMode.SMALL) {
							graphics.setFont(FontManager.getRunescapeSmallFont());
						} else if (config.vasaCrystalTimer() == CoxAdditionsConfig.crystalTimerMode.CUSTOM) {
							graphics.setFont(new Font("Arial", Font.BOLD, this.config.vasaCrystalTextSize()));
						}
						String str = this.plugin.vasaAtCrystal ? ("*" + this.plugin.vasaCrystalTicks) : Integer.toString(this.plugin.vasaCrystalTicks);
						Point p = npc.getCanvasTextLocation(graphics, str, npc.getLogicalHeight() / 2);
						if (p != null) {
							Point pointShadow = new Point(p.getX() + 1, p.getY() + 1);
							OverlayUtil.renderTextLocation(graphics, pointShadow, str, Color.BLACK);
							OverlayUtil.renderTextLocation(graphics, new Point(p.getX(), p.getY()), str, config.vasaCrystalTimerColor());
						}
					}
				}
			}

			if (this.config.chinRope() != CoxAdditionsConfig.chinRopeMode.OFF && plugin.ropeNpcs.size() > 0) {
				drawRopeChin(graphics);
			}

			if (config.smallMuttaHp() && (plugin.meatTreeAlive && plugin.smallMuttaAlive)) {
				NPC smallMutta = plugin.smallMutta;
				Color textColor = Color.WHITE;
				String text = "";

				if (smallMutta.getHealthRatio() > 0 || (plugin.lastRatio != 0 && plugin.lastHealthScale != 0)) {
					if (smallMutta.getHealthRatio() > 0) {
						plugin.lastRatio = smallMutta.getHealthRatio();
						plugin.lastHealthScale = smallMutta.getHealthScale();
					}
					float floatRatio = ((float) plugin.lastRatio / (float) plugin.lastHealthScale) * 100;
					if (floatRatio > 75) {
						textColor = Color.GREEN;
					} else if (floatRatio > 50) {
						textColor = Color.YELLOW;
					} else {
						textColor = Color.RED;
					}
					text = Float.toString(floatRatio).substring(0, 4);
					Point textLoc = plugin.smallMutta.getCanvasTextLocation(graphics, text, 50);
					if (textLoc != null) {
						Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
						Font oldFont = graphics.getFont();
						graphics.setFont(new Font("Arial", Font.BOLD, 15));
						OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
						OverlayUtil.renderTextLocation(graphics, textLoc, text, textColor);
						graphics.setFont(oldFont);
					}
				}
			}

			if (config.meatTreeChopCycle() == CoxAdditionsConfig.meatTreeChopCycleMode.OVERLAY && plugin.startedChopping && plugin.meatTreeAlive && plugin.meatTree != null) {
				String text = String.valueOf(plugin.ticksToChop);
				Point textLoc = plugin.meatTree.getCanvasTextLocation(graphics, text, 0);
				if (textLoc != null) {
					Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
					Font oldFont = graphics.getFont();
					graphics.setFont(new Font("Arial", Font.BOLD, 15));
					OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
					OverlayUtil.renderTextLocation(graphics, textLoc, text, Color.WHITE);
					graphics.setFont(oldFont);
				}
			}

			if (config.instanceTimer() == CoxAdditionsConfig.instanceTimerMode.OVERHEAD && plugin.isInstanceTimerRunning) {
				Player player = this.client.getLocalPlayer();
				if (player != null) {
					Point point = player.getCanvasTextLocation(graphics, "#", player.getLogicalHeight() + 60);
					if (point != null) {
						OverlayUtil.renderTextLocation(graphics, point, String.valueOf(plugin.instanceTimer), Color.CYAN);
					}
				}
			}

			if (config.olmPhaseHighlight() && plugin.olmSpawned && plugin.olmHead != null) {
				NPCComposition comp = plugin.olmHead.getComposition();
				int size = comp.getSize();
				LocalPoint lp = plugin.olmHead.getLocalLocation();
				if (lp != null) {
					Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
					if (tilePoly != null) {
						Color color = config.olmHighlightColor();
						switch (plugin.olmPhase) {
							case "Crystal":
								color = Color.MAGENTA;
								break;
							case "Acid":
								color = Color.GREEN;
								break;
							case "Flame":
								color = Color.RED;
								break;
						}
						renderPoly(graphics, color, tilePoly, config.olmThiCC());
					}
				}
			}

			if (!config.tlList().equals("")) {
				for (NPC npc : this.client.getNpcs()) {
					if (npc.getName() != null && npc.getId() != 8203) {
						String bossName = "";

						if (npc.getName().toLowerCase().contains("tekton")) {
							bossName = "tekton";
						} else if (npc.getName().toLowerCase().contains("jewelled crab")) {
							bossName = "jewelled crab";
						} else {
							bossName = npc.getName().toLowerCase();
						}

						if (plugin.tlList.contains(bossName)) {
							NPCComposition comp = npc.getComposition();
							int size = comp.getSize();
							LocalPoint lp = LocalPoint.fromWorld(this.client, npc.getWorldLocation());
							if (lp != null) {
								lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
								Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
								renderPoly(graphics, config.tlColor(), tilePoly, config.tlThiCC());
							}
						}
					}
				}
			}

			if (config.olmTp()) {
				for (GraphicsObject obj : this.client.getGraphicsObjects()) {
					if (obj.getId() == 1359) {
						Shape poly = Perspective.getCanvasTilePoly(client, obj.getLocation());
						renderTp(graphics, Color.ORANGE, poly);
						break;
					}
				}
			}

			if (this.config.olmHandsHealth() == CoxAdditionsConfig.olmHandsHealthMode.OVERLAY && (plugin.mageHand != null || plugin.meleeHand != null)) {
				NPC mageHand = plugin.mageHand;
				NPC meleeHand = plugin.meleeHand;
				Font oldFont = graphics.getFont();
				graphics.setFont(FontManager.getRunescapeBoldFont());
				if (this.client.getVarbitValue(5424) == 1) {
					if (mageHand != null && plugin.mageHandHp >= 0) {
						String mageText = String.valueOf(plugin.mageHandHp);
						Color mageColor = Color.WHITE;
						if (plugin.mageHandHp < 100) {
							mageColor = Color.RED;
						}
						Point mageTextLoc = mageHand.getCanvasTextLocation(graphics, mageText, -75);
						if (mageTextLoc != null) {
							Point magePointShadow = new Point(mageTextLoc.getX() + 1, mageTextLoc.getY() + 1);
							OverlayUtil.renderTextLocation(graphics, magePointShadow, mageText, Color.BLACK);
							OverlayUtil.renderTextLocation(graphics, mageTextLoc, mageText, mageColor);
						}
					}

					if (meleeHand != null && plugin.meleeHandHp >= 0) {
						String meleeText = String.valueOf(plugin.meleeHandHp);
						Color meleeColor = Color.WHITE;
						if (plugin.meleeHandHp < 100) {
							meleeColor = Color.RED;
						}
						Point meleeTextLoc = meleeHand.getCanvasTextLocation(graphics, meleeText, -75);
						if (meleeTextLoc != null) {
							Point meleePointShadow = new Point(meleeTextLoc.getX() + 1, meleeTextLoc.getY() + 1);
							OverlayUtil.renderTextLocation(graphics, meleePointShadow, meleeText, Color.BLACK);
							OverlayUtil.renderTextLocation(graphics, meleeTextLoc, meleeText, meleeColor);
						}
					}
				} else {
					if (mageHand != null) {
						String mageText = "";
						Color mageColor = Color.CYAN;
						if (mageHand.getHealthRatio() > 0 || (plugin.mageHandLastRatio != 0 && plugin.mageHandLastHealthScale != 0)) {
							if (mageHand.getHealthRatio() > 0) {
								System.out.println("Set mage hand ratio/scale");
								plugin.mageHandLastRatio = mageHand.getHealthRatio();
								plugin.mageHandLastHealthScale = mageHand.getHealthScale();
							}

							float floatRatioMage = ((float) plugin.mageHandLastRatio / (float) plugin.mageHandLastHealthScale * 100);
							if (floatRatioMage <= 15) {
								mageColor = Color.RED;
							}
							mageText = Float.toString(floatRatioMage);
							mageText = mageText.substring(0, mageText.indexOf("."));
						}
						Point mageTextLoc = mageHand.getCanvasTextLocation(graphics, mageText, 0);
						if (mageTextLoc != null) {
							Point magePointShadow = new Point(mageTextLoc.getX() + 1, mageTextLoc.getY() + 1);
							OverlayUtil.renderTextLocation(graphics, magePointShadow, mageText + "%", Color.BLACK);
							OverlayUtil.renderTextLocation(graphics, mageTextLoc, mageText + "%", mageColor);
						}
					}

					if (meleeHand != null) {
						Color meleeColor = Color.WHITE;
						String meleeText = "";
						if (meleeHand.getHealthRatio() > 0 || (plugin.meleeHandLastRatio != 0 && plugin.meleeHandLastHealthScale != 0)) {
							if (plugin.meleeHand.getHealthRatio() > 0) {
								plugin.meleeHandLastRatio = meleeHand.getHealthRatio();
								plugin.meleeHandLastHealthScale = meleeHand.getHealthScale();
							}


							float floatRatioMelee = ((float) plugin.meleeHandLastRatio / (float) plugin.meleeHandLastHealthScale * 100);
							if (floatRatioMelee <= 15) {
								meleeColor = Color.RED;
							}
							meleeText = Float.toString(floatRatioMelee);
							meleeText = meleeText.substring(0, meleeText.indexOf("."));
						}
						Point meleeTextLoc = meleeHand.getCanvasTextLocation(graphics, meleeText, 0);
						if (meleeTextLoc != null) {
							Point meleePointShadow = new Point(meleeTextLoc.getX() + 1, meleeTextLoc.getY() + 1);
							OverlayUtil.renderTextLocation(graphics, meleePointShadow, meleeText + "%", Color.BLACK);
							OverlayUtil.renderTextLocation(graphics, meleeTextLoc, meleeText + "%", meleeColor);
						}
					}
				}
				graphics.setFont(oldFont);
			}

			if (config.ropeCross() != CoxAdditionsConfig.ropeCrossMode.OFF && plugin.rapidHealActive) {
				Color ropeColor;
				if (plugin.ticksSinceHPRegen > 48 || plugin.ticksSinceHPRegen < 41) {
					ropeColor = Color.RED;
				} else if (plugin.ticksSinceHPRegen <= 43) {
					ropeColor = Color.GREEN;
				} else {
					ropeColor = Color.ORANGE;
				}

				if (config.ropeCross() == CoxAdditionsConfig.ropeCrossMode.HIGHLIGHT || config.ropeCross() == CoxAdditionsConfig.ropeCrossMode.BOTH) {
					if (plugin.rope != null) {
						for (GroundObject obj : plugin.rope) {
							Player player = client.getLocalPlayer();
							if (player != null) {
								if (player.getLocalLocation().distanceTo(obj.getLocalLocation()) <= 2400) {
									Shape hull = obj.getConvexHull();
									if (hull != null) {
										graphics.setColor(ropeColor);
										graphics.setStroke(new BasicStroke(1));
										graphics.draw(hull);
									}
								}
							}
						}
					}
				}

				if ((config.ropeCross() == CoxAdditionsConfig.ropeCrossMode.TICKS || config.ropeCross() == CoxAdditionsConfig.ropeCrossMode.BOTH) && this.client.getLocalPlayer() != null) {
					String text;
					if(config.ropeTicksDown()) {
						text = String.valueOf(50 - plugin.ticksSinceHPRegen);
					}else {
						text = String.valueOf(plugin.ticksSinceHPRegen);
					}
					if(config.ropeCrossTicks() == CoxAdditionsConfig.ropeCrossTicksMode.PLAYER || config.ropeCrossTicks() == CoxAdditionsConfig.ropeCrossTicksMode.BOTH) {
						Point p = this.client.getLocalPlayer().getCanvasTextLocation(graphics, text, 0);
						if (p != null) {
							Point pShadow = new Point(p.getX() + 1, p.getY() + 1);
							OverlayUtil.renderTextLocation(graphics, pShadow, text, Color.BLACK);
							OverlayUtil.renderTextLocation(graphics, p, text, ropeColor);
						}
					}
					if(config.ropeCrossTicks() == CoxAdditionsConfig.ropeCrossTicksMode.ROPE || config.ropeCrossTicks() == CoxAdditionsConfig.ropeCrossTicksMode.BOTH && plugin.rope != null) {
						for(GroundObject obj : plugin.rope) {
							Player player = client.getLocalPlayer();
							if (player != null) {
								if (player.getLocalLocation().distanceTo(obj.getLocalLocation()) <= 2400) {
									Point p = obj.getCanvasTextLocation(graphics, text, 0);
									if (p != null) {
										Point pShadow = new Point(p.getX() + 1, p.getY() + 1);
										OverlayUtil.renderTextLocation(graphics, pShadow, text, Color.BLACK);
										OverlayUtil.renderTextLocation(graphics, p, text, ropeColor);
									}
								}
							}
						}
					}
				}
			}

			if(config.teleportTarget() && !plugin.portalBuddy.equals("")) {
				System.out.println("Buddy: " + plugin.portalBuddy);
				for(Player p : this.client.getPlayers()) {
					System.out.println("player: " + p.getName().toLowerCase() + "       " + p.getName().equalsIgnoreCase(plugin.portalBuddy));
					if(p.getName() != null && p.getName().equalsIgnoreCase(plugin.portalBuddy)) {
						LocalPoint lp = p.getLocalLocation();
						if (lp != null) {
							Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, 1);
							renderPoly(graphics, config.teleportColor(), tilePoly, 2);
						}
					}
				}
			}

			if(config.teleportTargetTicks() && plugin.portalTicks > 0 && this.client.getLocalPlayer() != null) {
				String text = String.valueOf(plugin.portalTicks);
				Point p = this.client.getLocalPlayer().getCanvasTextLocation(graphics, text, 0);
				if (p != null) {
					Point pShadow = new Point(p.getX() + 1, p.getY() + 1);
					OverlayUtil.renderTextLocation(graphics, pShadow, text, Color.BLACK);
					OverlayUtil.renderTextLocation(graphics, p, text, Color.WHITE);
				}
			}
		}
		return null;
	}

	private void renderPoly(Graphics2D graphics, Color color, Shape polygon, double width) {
		if (polygon != null) {
			graphics.setColor(color);
			graphics.setStroke(new BasicStroke((float) width));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
			graphics.fill(polygon);
		}
	}

	private void renderPoly(Graphics2D graphics, Color color, Shape polygon) {
		if (polygon != null) {
			graphics.setColor(color);
			graphics.setStroke(new BasicStroke(2));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
			graphics.fill(polygon);
		}
	}

	private void renderSpawnAoE(Graphics2D graphics, Color color, Shape polygon, int width) {
		if (polygon != null) {
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 255));
			graphics.setStroke(new BasicStroke(1));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
			graphics.fill(polygon);
		}
	}

	private void renderTp(Graphics2D graphics, Color color, Shape polygon) {
		if (polygon != null) {
			graphics.setColor(color);
			graphics.setStroke(new BasicStroke(2));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
			graphics.fill(polygon);
		}
	}

	private void renderArea(Graphics2D graphics, Color color, Shape polygon) {
		if (polygon != null) {
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
			graphics.fill(polygon);
		}
	}

	public void drawRopeChin(Graphics2D graphics) {
		boolean highlight = false;
		for (NPC npc : plugin.ropeNpcs) {
			WorldPoint wp = npc.getWorldLocation();
			for (NPC target : plugin.ropeNpcs) {
				if (target != npc){
					WorldPoint tWp = target.getWorldLocation();
					int x_dist = Math.abs(tWp.getX() - wp.getX());
					int y_dist = Math.abs(tWp.getY() - wp.getY());
					if (x_dist <= 1 && y_dist <= 1){
						highlight = true;
					}
				}
			}
			
			if (highlight) {
				if(config.chinRope() == CoxAdditionsConfig.chinRopeMode.HULL) {
					Shape poly = npc.getConvexHull();
					if (poly != null) {
						graphics.setColor(new Color(config.chinRopeColor().getRed(), config.chinRopeColor().getGreen(), config.chinRopeColor().getBlue(), 255));
						graphics.setStroke(new BasicStroke(config.chinRopeThiCC()));
						graphics.draw(poly);
						graphics.setColor(new Color(config.chinRopeColor().getRed(), config.chinRopeColor().getGreen(), config.chinRopeColor().getBlue(), 0));
						graphics.fill(poly);
					}
				}else if(config.chinRope() == CoxAdditionsConfig.chinRopeMode.OUTLINE) {
					modelOutlineRenderer.drawOutline(npc, config.chinRopeThiCC(), config.chinRopeColor(), 2);
				}
			}
		}
	}

	protected void renderTextLocation(Graphics2D graphics, @Nullable Point txtLoc, @Nullable String text, @Nonnull Color color) {
		if (txtLoc == null || Strings.isNullOrEmpty(text))
			return;
		int x = txtLoc.getX();
		int y = txtLoc.getY();
		graphics.setColor(Color.BLACK);{
			graphics.drawString(text, x, y + 1);
			graphics.drawString(text, x, y - 1);
			graphics.drawString(text, x + 1, y);
			graphics.drawString(text, x - 1, y);
		}
		graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
		graphics.drawString(text, x, y);
	}

	protected void renderImageLocation(Graphics2D graphics, @Nullable Point imgLoc, @Nullable BufferedImage image) {
		if (imgLoc == null || image == null)
			return;
		int x = imgLoc.getX();
		int y = imgLoc.getY();
		graphics.drawImage(image, x, y, null);
	}
}
