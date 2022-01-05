/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 *
 * Modified by farhan1666
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.aoe;

import net.runelite.api.Point;
import net.runelite.api.ProjectileID;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static net.runelite.client.plugins.aoe.ColorUtil.setAlphaComponent;

@Singleton
public class AoeOverlay extends Overlay {
	private static final int FILL_START_ALPHA = 25;
	private static final int OUTLINE_START_ALPHA = 255;

	private final Client client;
	private final AoePlugin plugin;
	private final AoeConfig config;

	@Inject
	public AoeOverlay(final Client client, final AoePlugin plugin, final AoeConfig config) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.UNDER_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		AtomicInteger index = new AtomicInteger();
		if(this.client.getLocalPlayer() != null) {
			WorldPoint lp = client.getLocalPlayer().getWorldLocation();

			if (client.getVar(Varbits.IN_RAID) == 1) {
				plugin.getLightningTrail().forEach(o -> {
					if (config.raveHighlights() != AoeConfig.raveMode.OFF) {
						if (config.raveHighlights() == AoeConfig.raveMode.RAVE) {
							OverlayUtil.drawTiles(graphics, client, o, lp, plugin.raveColors.get(index.intValue()), 2, 150, 50);
						} else {
							OverlayUtil.drawTiles(graphics, client, o, lp, plugin.flowColor, 2, 150, 50);
						}
					} else {
						OverlayUtil.drawTiles(graphics, client, o, lp, new Color(0, 150, 200), 2, 150, 50);
					}
					index.getAndIncrement();
				});

				if (config.olmAcid()) {
					plugin.getAcidTrail().forEach(o -> {
						if (config.raveHighlights() != AoeConfig.raveMode.OFF) {
							if (config.raveHighlights() == AoeConfig.raveMode.RAVE) {
								OverlayUtil.drawTiles(graphics, client, o.getWorldLocation(), lp, plugin.raveColors.get(index.intValue()), 2, 150, 50);
							} else {
								OverlayUtil.drawTiles(graphics, client, o.getWorldLocation(), lp, plugin.flowColor, 2, 150, 50);
							}
						} else {
							OverlayUtil.drawTiles(graphics, client, o.getWorldLocation(), lp, new Color(69, 241, 44), 2, 150, 50);
						}
						index.getAndIncrement();
					});
				}

				if (config.olmCrystal()) {
					plugin.getCrystalSpike().forEach(o -> {
						if (config.raveHighlights() != AoeConfig.raveMode.OFF) {
							if (config.raveHighlights() == AoeConfig.raveMode.RAVE) {
								OverlayUtil.drawTiles(graphics, client, o.getWorldLocation(), lp, plugin.raveColors.get(index.intValue()), 2, 150, 50);
							} else {
								OverlayUtil.drawTiles(graphics, client, o.getWorldLocation(), lp, plugin.flowColor, 2, 150, 50);
							}
						} else {
							OverlayUtil.drawTiles(graphics, client, o.getWorldLocation(), lp, new Color(255, 0, 84), 2, 150, 50);
						}
						index.getAndIncrement();
					});
				}
			}

			if (config.isWintertodtEnabled()) {
				plugin.getWintertodtSnowFall().forEach(o -> {
					if (config.raveHighlights() != AoeConfig.raveMode.OFF) {
						if (config.raveHighlights() == AoeConfig.raveMode.RAVE) {
							OverlayUtil.drawTiles(graphics, client, o.getWorldLocation(), lp, plugin.raveColors.get(index.intValue()), 2, 150, 50);
						} else {
							OverlayUtil.drawTiles(graphics, client, o.getWorldLocation(), lp, plugin.flowColor, 2, 150, 50);
						}
					} else {
						OverlayUtil.drawTiles(graphics, client, o.getWorldLocation(), lp, new Color(255, 0, 84), 2, 150, 50);
					}
					index.getAndIncrement();
				});
			}
		}

		Instant now = Instant.now();
		Set<ProjectileContainer> projectiles = plugin.getProjectiles();
		ArrayList<LocalPoint> previousPoints = new ArrayList<>();
		projectiles.forEach(proj -> {
			if (proj.getTargetPoint() != null) {
				Color color;
				if (now.isAfter(proj.getStartTime().plus(Duration.ofMillis(proj.getLifetime())))) {
					return;
				}

				if (proj.getProjectile().getId() == ProjectileID.ICE_DEMON_ICE_BARRAGE_AOE || proj.getProjectile().getId() == ProjectileID.TEKTON_METEOR_AOE) {
					if (client.getVar(Varbits.IN_RAID) == 0) {
						return;
					}
				}

				Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, proj.getTargetPoint(), proj.getAoeProjectileInfo().getAoeSize());

				if (config.crystalSize() && proj.getProjectile().getId() == ProjectileID.OLM_FALLING_CRYSTAL) {
					tilePoly = Perspective.getCanvasTileAreaPoly(client, proj.getTargetPoint(), 1);
				}

				if (tilePoly == null) {
					return;
				}

				final double progress = (System.currentTimeMillis() - proj.getStartTime().toEpochMilli()) / (double) proj.getLifetime();

				final int tickProgress = proj.getFinalTick() - client.getTickCount();

				int fillAlpha, outlineAlpha;

				if (config.isFadeEnabled()) {
					fillAlpha = (int) ((1 - progress) * FILL_START_ALPHA);
					outlineAlpha = (int) ((1 - progress) * OUTLINE_START_ALPHA);
				} else if (config.isReverseFadeEnabled()) {
					fillAlpha = (int) ((1 + progress) * FILL_START_ALPHA);
					outlineAlpha = (int) ((1 + progress) * OUTLINE_START_ALPHA);
				} else {
					fillAlpha = config.AoEFillColor().getAlpha();
					outlineAlpha = OUTLINE_START_ALPHA;
				}

				if (tickProgress == 0) {
					color = Color.RED;
				} else {
					color = Color.WHITE;
				}

				if (fillAlpha < 0) {
					fillAlpha = 0;
				}
				if (outlineAlpha < 0) {
					outlineAlpha = 0;
				}
				if (fillAlpha > 255) {
					fillAlpha = 255;
				}
				if (outlineAlpha > 255) {
					outlineAlpha = 255;
				}

				if (this.config.aoeThiCC() >= 1) {
					if(config.raveHighlights() != AoeConfig.raveMode.OFF) {
						if (config.raveHighlights() == AoeConfig.raveMode.RAVE) {
							Color raveColor = plugin.raveColors.get(index.intValue());
							graphics.setColor(new Color(raveColor.getRed(), raveColor.getGreen(), raveColor.getBlue(), outlineAlpha));
						} else {
							graphics.setColor(new Color(plugin.flowColor.getRed(), plugin.flowColor.getGreen(), plugin.flowColor.getBlue(), outlineAlpha));
						}
					} else {
						graphics.setColor(new Color(setAlphaComponent(config.AoEColor().getRGB(), outlineAlpha), true));
					}
					graphics.drawPolygon(tilePoly);
				}

				if (config.aoeTicks() && tickProgress >= 0) {
					int offset = 0;
					Point point = Perspective.getCanvasTextLocation(client, graphics, proj.getTargetPoint(), Integer.toString(tickProgress), 0);
					for (LocalPoint plp : previousPoints) {
						if (plp.getX() == proj.getTargetPoint().getX() && plp.getY() == proj.getTargetPoint().getY()) {
							offset += 15;
						}
					}
					point = new Point(point.getX(), point.getY() - offset);
					if (config.fontType()) {
						renderTextLocation(graphics, Integer.toString(tickProgress), color, point);
					} else {
						renderCustomTextLocation(graphics, Integer.toString(tickProgress), config.textSize(), 1, color, point);
					}
					previousPoints.add(proj.getTargetPoint());
				}

				if(config.raveHighlights() != AoeConfig.raveMode.OFF) {
					if(config.raveHighlights() == AoeConfig.raveMode.RAVE) {
						Color raveColor = plugin.raveColors.get(index.intValue());
						graphics.setColor(new Color(raveColor.getRed(), raveColor.getGreen(), raveColor.getBlue(), fillAlpha));
					} else {
						graphics.setColor(new Color(plugin.flowColor.getRed(), plugin.flowColor.getGreen(), plugin.flowColor.getBlue(), fillAlpha));
					}
				} else {
					graphics.setColor(new Color(setAlphaComponent(config.AoEFillColor().getRGB(), fillAlpha), true));
				}
				graphics.fillPolygon(tilePoly);
			}
		});
		projectiles.removeIf(proj -> now.isAfter(proj.getStartTime().plus(Duration.ofMillis(proj.getLifetime()))));
		return null;
	}

	private Point centerPoint(Rectangle rect) {
		int x = (int) (rect.getX() + rect.getWidth() / 2);
		int y = (int) (rect.getY() + rect.getHeight() / 2);
		return new Point(x, y);
	}

	protected void renderTextLocation(Graphics2D graphics, String txtString, Color fontColor, Point canvasPoint) {
		if (canvasPoint != null) {
			Point canvasCenterPoint = new Point(canvasPoint.getX(), canvasPoint.getY());
			Point canvasCenterPoint_shadow = new Point(canvasPoint.getX() + 1, canvasPoint.getY() + 1);
			OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
			OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
		}
	}

	protected void renderCustomTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint) {
		graphics.setFont(new Font("Arial", fontStyle, fontSize));
		if (canvasPoint != null) {
			Point canvasCenterPoint = new Point(canvasPoint.getX(), canvasPoint.getY());
			Point canvasCenterPointShadow = new Point(canvasPoint.getX() + 1, canvasPoint.getY() + 1);
			OverlayUtil.renderTextLocation(graphics, canvasCenterPointShadow, txtString, Color.BLACK);
			OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
		}
	}
}