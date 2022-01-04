/*
 * BikkusLite / UncleLite © 2020
 */

package net.runelite.client.plugins.theatre.Sotetseg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.Projectile;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.theatre.RoomOverlay;
import net.runelite.client.plugins.theatre.TheatreConfig;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.game.SkillIconManager;
import net.runelite.api.Skill;
import java.awt.image.BufferedImage;

public class SotetsegOverlay extends RoomOverlay
{
	@Inject
	private Sotetseg sotetseg;

	@Inject
	private SkillIconManager iconManager;

	@Inject
	protected SotetsegOverlay(TheatreConfig config)
	{
		super(config);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.MED);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (sotetseg.isSotetsegActive())
		{
			if (config.sotetsegAutoAttacksTicks())
			{
				int tick = sotetseg.getSotetsegTickCount();
				if (tick >= 0)
				{
					NPC boss = sotetseg.getSotetsegNPC();
					final String ticksCounted = String.valueOf(tick);
					Point canvasPoint = boss.getCanvasTextLocation(graphics, ticksCounted, 50);
					renderTextLocation(graphics, ticksCounted, Color.WHITE, canvasPoint);
				}
			}

			if (config.sotetsegAttackCounter())
			{
				int attack = sotetseg.getAttacksLeft();
				if (attack >= 0)
				{
					NPC boss = sotetseg.getSotetsegNPC();
					final String attacksCounted = String.valueOf(sotetseg.getAttacksLeft());
					Point canvasPoint = boss.getCanvasTextLocation(graphics, attacksCounted, 250);
					renderTextLocation(graphics, attacksCounted, Color.YELLOW, canvasPoint);
				}
			}

			if (config.sotetsegOrbAttacksTicks() || config.sotetsegBigOrbTicks() || config.sotetsegOrbIcons() != TheatreConfig.SOTETSEGORBICONS.OFF)
			{
				for (Projectile p : client.getProjectiles())
				{
					int id = p.getId();

					Point point = Perspective.localToCanvas(client, new LocalPoint((int)p.getX(), (int)p.getY()), 0, Perspective.getTileHeight(client, new LocalPoint((int)p.getX(), (int)p.getY()), p.getFloor()) - (int)p.getZ());

					if (point == null)
					{
						continue;
					}

					if (config.sotetsegOrbIcons() != TheatreConfig.SOTETSEGORBICONS.OFF)
					{
						BufferedImage icon;
						if (id == Sotetseg.SOTETSEG_MAGE_ORB)
						{
							icon = iconManager.getSkillImage(Skill.MAGIC);
							Point iconlocation = new Point(point.getX() - icon.getWidth() / 2, point.getY() - 30);

							if (config.sotetsegOrbIcons() == TheatreConfig.SOTETSEGORBICONS.ALL)
							{
								OverlayUtil.renderImageLocation(graphics, iconlocation, icon);
							}

							if (p.getInteracting() == client.getLocalPlayer() && config.sotetsegOrbIcons() == TheatreConfig.SOTETSEGORBICONS.YOURS)
							{
								OverlayUtil.renderImageLocation(graphics, iconlocation, icon);
							}
						}

						if (id == Sotetseg.SOTETSEG_RANGE_ORB)
						{
							icon = iconManager.getSkillImage(Skill.RANGED);
							Point iconlocation = new Point(point.getX() - icon.getWidth() / 2, point.getY() - 30);

							if (config.sotetsegOrbIcons() == TheatreConfig.SOTETSEGORBICONS.ALL)
							{
								OverlayUtil.renderImageLocation(graphics, iconlocation, icon);
							}

							if (p.getInteracting() == client.getLocalPlayer() && config.sotetsegOrbIcons() == TheatreConfig.SOTETSEGORBICONS.YOURS)
							{
								OverlayUtil.renderImageLocation(graphics, iconlocation, icon);
							}
						}
					}

					if ((p.getInteracting() == client.getLocalPlayer()) && (id == Sotetseg.SOTETSEG_MAGE_ORB || id == Sotetseg.SOTETSEG_RANGE_ORB) && config.sotetsegOrbAttacksTicks())
					{
						renderTextLocation(graphics, String.valueOf(p.getRemainingCycles() / 30), (id == Sotetseg.SOTETSEG_MAGE_ORB ? Color.CYAN : Color.GREEN), point);
					}

					if (id == Sotetseg.SOTETSEG_BIG_AOE_ORB && config.sotetsegBigOrbTicks())
					{
						Color color = (p.getRemainingCycles() / 30) > 0 ? config.sotetsegBigOrbTickColor() : Color.ORANGE;
						renderTextLocation(graphics, String.valueOf(p.getRemainingCycles() / 30), color, point);
						renderPoly(graphics, config.sotetsegBigOrbTileColor(), p.getInteracting().getCanvasTilePoly());
						Point imagelocation = new Point(point.getX() - Sotetseg.TACTICAL_NUKE_OVERHEAD.getWidth() / 2, point.getY() - 60);
						OverlayUtil.renderImageLocation(graphics, imagelocation, Sotetseg.TACTICAL_NUKE_OVERHEAD);
					}
				}
			}

			if (config.sotetsegMaze())
			{
				int counter = 1;
				for (Point p : sotetseg.getRedTiles())
				{
					WorldPoint wp = sotetseg.worldPointFromMazePoint(p);
					drawTile(graphics, wp, Color.WHITE, 1, 255, 0);
					LocalPoint lp = LocalPoint.fromWorld(client, wp);
					if (lp != null && !sotetseg.isWasInUnderWorld())
					{
						Point textPoint = Perspective.getCanvasTextLocation(client, graphics, lp, String.valueOf(counter), 0);
						if (textPoint != null)
						{
							renderTextLocation(graphics, String.valueOf(counter), Color.WHITE, textPoint);
						}
					}
					counter++;
				}

				for (Point p : sotetseg.getGreenTiles())
				{
					WorldPoint wp = sotetseg.worldPointFromMazePoint(p);
					drawTile(graphics, wp, Color.GREEN, 1, 255, 0);
				}
			}
		}
		return null;
	}
}