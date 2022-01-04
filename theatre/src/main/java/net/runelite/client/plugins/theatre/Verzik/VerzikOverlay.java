/*
 * THIS PLUGIN WAS WRITTEN BY A KEYBOARD-WIELDING MONKEY BOI BUT SHUFFLED BY A KANGAROO WITH THUMBS.
 * The plugin and it's refactoring was intended for xKylee's Externals but I'm sure if you're reading this, you're probably planning to yoink..
 * or you're just genuinely curious. If you're trying to yoink, it doesn't surprise me.. just don't claim it as your own. Cheers.
 */

package net.runelite.client.plugins.theatre.Verzik;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import javax.inject.Inject;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.theatre.RoomOverlay;
import net.runelite.client.plugins.theatre.TheatreConfig;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayUtil;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class VerzikOverlay extends RoomOverlay
{
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.0");

	@Inject
	private Verzik verzik;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	protected VerzikOverlay(TheatreConfig config, SpriteManager spriteManager)
	{
		super(config);
		this.spriteManager = spriteManager;
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (verzik.isVerzikActive())
		{
			if (config.verzikTileOverlay() || (config.verzikTileOverlayP3() && !config.verzikTileOverlay() && verzik.getVerzikPhase() == Verzik.Phase.PHASE3))
			{
				int size = 1;
				final NPCComposition composition = verzik.getVerzikNPC().getTransformedComposition();
				if (composition != null)
				{
					size = composition.getSize();
				}
				LocalPoint lp = LocalPoint.fromWorld(client, verzik.getVerzikNPC().getWorldLocation());
				if (lp != null)
				{
					Polygon tilePoly = getCanvasTileAreaPoly(client, lp, size, false);
					if (tilePoly != null)
					{
						if (verzik.isVerzikEnraged())
						{
							renderPoly(graphics, new Color(255, 110, 90), tilePoly);
						}
						else
						{
							renderPoly(graphics, new Color(255, 110, 230), tilePoly);
						}
					}
				}
			}

			String tick_text = "";
			if (config.verzikAttackCounter() && verzik.getVerzikSpecial() != Verzik.SpecialAttack.WEBS)
			{
				tick_text += verzik.getVerzikAttackCount();
				if (config.verzikAutosTick() || config.verzikTotalTickCounter())
				{
					tick_text += " : ";
				}
			}
			if (config.verzikAutosTick() && verzik.getVerzikSpecial() != Verzik.SpecialAttack.WEBS)
			{
				tick_text += verzik.getVerzikTicksUntilAttack();
				if (config.verzikTotalTickCounter())
				{
					tick_text += " : ";
				}
			}
			if (config.verzikTotalTickCounter())
			{
				tick_text += "(" + verzik.getVerzikTotalTicksUntilAttack() + ")";
			}
			Point canvasPoint = verzik.getVerzikNPC().getCanvasTextLocation(graphics, tick_text, 60);

			if (canvasPoint != null)
			{
				Color col = verzik.verzikSpecialWarningColor();
				renderTextLocation(graphics, tick_text, col, canvasPoint);
			}

			if (verzik.getVerzikPhase() == Verzik.Phase.PHASE2)
			{
				if (config.verzikProjectiles())
				{
					for (Projectile p : verzik.getVerzikRangeProjectiles().keySet())
					{
						WorldPoint wp = verzik.getVerzikRangeProjectiles().get(p);
						drawTile(graphics, wp, config.verzikProjectilesColor(), 1, 255, 20);
						if (config.verzikProjectilesTicks())
						{
							LocalPoint lp = LocalPoint.fromWorld(client, wp);
							if (lp != null)
							{
								int ticks = p.getRemainingCycles() / 30;
								Point point = Perspective.localToCanvas(client, lp, client.getPlane());
								Color color = ticks > 0 ? Color.WHITE : Color.RED;
								renderTextLocation(graphics, Integer.toString(ticks), color, point);
							}
						}
					}
				}

				if (config.verzikReds())
				{
					verzik.getVerzikReds().forEach((k, v) ->
					{
						int v_health = v.getValue();
						int v_healthRation = v.getKey();
						if (k.getName() != null && k.getHealthScale() > 0)
						{
							v_health = k.getHealthScale();
							v_healthRation = Math.min(v_healthRation, k.getHealthRatio());
						}
						float percentage = ((float) v_healthRation / (float) v_health) * 100f;
						Point textLocation = k.getCanvasTextLocation(graphics, String.valueOf(DECIMAL_FORMAT.format(percentage)), 80);

						if (textLocation != null)
						{
							renderTextLocation(graphics, String.valueOf(DECIMAL_FORMAT.format(percentage)), Color.WHITE, textLocation);
						}
					});

					NPC[] reds = verzik.getVerzikReds().keySet().toArray(new NPC[0]);
					for (NPC npc : reds)
					{
						if (npc.getName() != null && npc.getHealthScale() > 0 && npc.getHealthRatio() < 100)
						{
							Pair<Integer, Integer> newVal = new MutablePair<>(npc.getHealthRatio(), npc.getHealthScale());
							if (verzik.getVerzikReds().containsKey(npc))
							{
								verzik.getVerzikReds().put(npc, newVal);
							}
						}
					}
				}

				if (verzik.getVerzikPhase() == Verzik.Phase.PHASE2 && verzik.getVerzikNPC() != null && config.lightningAttackHelper())
				{
					Point imageLocation;
					if (verzik.getVerzikLightningAttacks() == 0)
					{
						BufferedImage lightningIcon = spriteManager.getSprite(558, 0);
						imageLocation = verzik.getVerzikNPC().getCanvasImageLocation(lightningIcon, 200);
						if (imageLocation != null)
						{
							OverlayUtil.renderImageLocation(graphics, imageLocation, lightningIcon);
						}
					}
					else
					{
						String attacksLeft = Integer.toString(verzik.getVerzikLightningAttacks());
						imageLocation = Perspective.getCanvasTextLocation(client, graphics, verzik.getVerzikNPC().getLocalLocation(), attacksLeft, 200);
						renderTextLocation(graphics, attacksLeft, Color.WHITE, imageLocation);
					}
				}

				if (config.lightningAttackTick())
				{
					for (Projectile p : verzik.getVerzikLightningProjectiles().keySet())
					{
						Player localPlayer = client.getLocalPlayer();
						if (localPlayer != null && p.getInteracting() == localPlayer)
						{
							int ticks = verzik.getVerzikLightningProjectiles().get(p);
							String tickstring = String.valueOf(ticks);
							Point point = Perspective.getCanvasTextLocation(client, graphics, localPlayer.getLocalLocation(), tickstring, 0);
							if (point != null)
							{
								renderTextLocation(graphics, tickstring, (ticks > 0 ? Color.WHITE : Color.ORANGE), point);
							}
						}
					}
				}
			}

			if (verzik.getVerzikPhase() == Verzik.Phase.PHASE3)
			{
				if (config.verzikDisplayTank())
				{
					if (verzik.getVerzikNPC().getInteracting() != null)
					{
						Polygon tilePoly = verzik.getVerzikNPC().getInteracting().getCanvasTilePoly();
						if (tilePoly != null)
						{
							renderPoly(graphics, Color.LIGHT_GRAY, tilePoly);
						}
					}
				}

				if (config.verzikTornado() && (!config.verzikPersonalTornadoOnly() || (config.verzikPersonalTornadoOnly() && verzik.getVerzikLocalTornado() != null)))
				{
					verzik.getVerzikTornadoes().forEach(k ->
					{
						if (k.getCurrentPosition() != null)
						{
							drawTile(graphics, k.getCurrentPosition(), config.verzikTornadoColor(), 1, 120, 10);
						}

						if (k.getLastPosition() != null)
						{
							drawTile(graphics, k.getLastPosition(), config.verzikTornadoColor(), 2, 180, 20);
						}
					});
				}

				if (config.verzikYellows())
				{
					for (GraphicsObject object : client.getGraphicsObjects())
					{
						if (object.getId() == 1595)
						{
							drawTile(graphics, WorldPoint.fromLocal(client, object.getLocation()), Color.YELLOW, 1, 255, 0);
						}
					}

					if (verzik.getVerzikYellowProjectiles().size() > 0)
					{
						Player localPlayer = client.getLocalPlayer();
						if (localPlayer != null)
						{
							Projectile p = verzik.getVerzikYellowProjectiles().keySet().iterator().next();
							int ticks = verzik.getVerzikYellowProjectiles().get(p);
							String tickstring = String.valueOf(ticks);

							Point point = Perspective.getCanvasTextLocation(client, graphics, localPlayer.getLocalLocation(), "#", 0);
							if (point != null)
							{
								renderTextLocation(graphics, tickstring, Color.WHITE, point);
							}
						}
					}
				}

				if (config.verzikGreenBall() || config.verzikGreenBallTick())
				{
					for (Projectile p : verzik.getVerzikGreenBallProjectiles().keySet())
					{
						if (config.verzikGreenBallTick())
						{
							int ticks = verzik.getVerzikGreenBallProjectiles().get(p);
							String tickstring = String.valueOf(ticks);
							Point point = getProjectilePoint(p);
							if (point != null)
							{
								Point textLocation = new Point(point.getX(), point.getY());
								renderTextLocation(graphics, Integer.toString(ticks), Color.GREEN, textLocation);
							}

							Player localPlayer = client.getLocalPlayer();
							if (localPlayer != null && p.getInteracting() == localPlayer)
							{
								Point textLocationPlayer = Perspective.getCanvasTextLocation(client, graphics, localPlayer.getLocalLocation(), "#", 0);
								if (textLocationPlayer != null)
								{
									renderTextLocation(graphics, tickstring, Color.GREEN, textLocationPlayer);
								}
							}
						}
						if (config.verzikGreenBall())
						{
							Polygon tilePoly;
							if (config.verzikGreenBallMarker() == TheatreConfig.VERZIKBALLTILE.TILE)
							{
								tilePoly = p.getInteracting().getCanvasTilePoly();
							}
							else
							{
								tilePoly = getCanvasTileAreaPoly(client, p.getInteracting().getLocalLocation(), 3, true);
							}

							if (tilePoly != null)
							{
								renderPoly(graphics, config.verzikGreenBallColor(), tilePoly);
							}
						}
					}
				}
			}

			if (verzik.getVerzikPhase() == Verzik.Phase.PHASE2 || verzik.getVerzikPhase() == Verzik.Phase.PHASE3)
			{
				if (config.verzikNyloPersonalWarning() || config.verzikNyloOtherWarning())
				{
					verzik.getVerzikAggros().forEach(k ->
					{
						if (k.getInteracting() != null && !k.isDead())
						{
							if ((config.verzikNyloPersonalWarning() && (k.getInteracting() == client.getLocalPlayer() || (!config.verzikNyloOtherWarning() && k.getInteracting() != client.getLocalPlayer())))
								|| (config.verzikNyloOtherWarning() && k.getInteracting() != client.getLocalPlayer()))
							{
								Color color = Color.GREEN;
								if (k.getInteracting() == client.getLocalPlayer())
								{
									color = Color.RED;
								}

								Point textLocation = k.getCanvasTextLocation(graphics, k.getInteracting().getName(), 80);
								if (textLocation != null)
								{
									OverlayUtil.renderTextLocation(graphics, textLocation, k.getInteracting().getName(), color);
								}

								if (config.verzikNyloExplodeAOE() && ((config.verzikNyloPersonalWarning() && k.getInteracting() == client.getLocalPlayer())
								|| (config.verzikNyloOtherWarning() && k.getInteracting() != client.getLocalPlayer())))
								{
									int size = 1;
									int thick_size = 1;
									final NPCComposition composition = k.getComposition();

									if (composition != null)
									{
										size = composition.getSize() + 2 * thick_size;
									}

									LocalPoint lp = LocalPoint.fromWorld(client, k.getWorldLocation());
									if (lp != null)
									{
										lp = new LocalPoint(lp.getX() - thick_size * Perspective.LOCAL_TILE_SIZE, lp.getY() - thick_size * Perspective.LOCAL_TILE_SIZE);

										Polygon tilePoly = getCanvasTileAreaPoly(client, lp, size, false);
										if (tilePoly != null)
										{
											renderPoly(graphics, color, tilePoly);
										}
									}
								}
							}
						}
					});
				}
			}

		}
		return null;
	}

	private Point getProjectilePoint(Projectile p)
	{
		int x = (int) p.getX();
		int y = (int) p.getY();
		int z = (int) p.getZ();
		return Perspective.localToCanvas(client, new LocalPoint(x, y), 0, Perspective.getTileHeight(client, new LocalPoint(x, y), p.getFloor()) - z);
	}
}
