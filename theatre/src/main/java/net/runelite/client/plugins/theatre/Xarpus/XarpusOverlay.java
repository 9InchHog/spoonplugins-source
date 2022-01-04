/*
 * Copyright (c) 2021 BikkusLite
 * All rights reserved.
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

package net.runelite.client.plugins.theatre.Xarpus;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.BasicStroke;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.GroundObject;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.theatre.Direction;
import net.runelite.client.plugins.theatre.RoomOverlay;
import net.runelite.client.plugins.theatre.TheatreConfig;
import net.runelite.client.plugins.theatre.TheatrePlugin;
import net.runelite.client.ui.overlay.OverlayLayer;
import org.apache.commons.lang3.tuple.Pair;

public class XarpusOverlay extends RoomOverlay
{
	@Inject
	private Xarpus xarpus;

	@Inject
	protected XarpusOverlay(TheatreConfig config)
	{
		super(config);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	private static final Function<WorldPoint, Point[]> getNEBoxPoints = (p) -> new Point[]{new Point(p.getX(), p.getY()), new Point(p.getX(), p.getY() + 8), new Point(p.getX() + 8, p.getY() + 8), new Point(p.getX() + 8, p.getY())};
	private static final Function<WorldPoint, Point[]> getNWBoxPoints = (p) -> new Point[]{new Point(p.getX() - 8, p.getY()), new Point(p.getX() - 8, p.getY() + 8), new Point(p.getX(), p.getY() + 8), new Point(p.getX(), p.getY())};
	private static final Function<WorldPoint, Point[]> getSEBoxPoints = (p) -> new Point[]{new Point(p.getX(), p.getY() - 8), new Point(p.getX(), p.getY()), new Point(p.getX() + 8, p.getY()), new Point(p.getX() + 8, p.getY() - 8)};
	private static final Function<WorldPoint, Point[]> getSWBoxPoints = (p) -> new Point[]{new Point(p.getX() - 8, p.getY() - 8), new Point(p.getX() - 8, p.getY()), new Point(p.getX(), p.getY()), new Point(p.getX(), p.getY() - 8)};
	private static final Function<WorldPoint, Point[]> getNEMeleePoints = (p) -> new Point[]{new Point(p.getX() + 4, p.getY() + 4), new Point(p.getX(), p.getY() + 4), new Point(p.getX(), p.getY() + 3), new Point(p.getX() + 3, p.getY() + 3), new Point(p.getX() + 3, p.getY()), new Point(p.getX() + 4, p.getY())};
	private static final Function<WorldPoint, Point[]> getNWMeleePoints = (p) -> new Point[]{new Point(p.getX() - 4, p.getY() + 4), new Point(p.getX() - 4, p.getY()), new Point(p.getX() - 3, p.getY()), new Point(p.getX() - 3, p.getY() + 3), new Point(p.getX(), p.getY() + 3), new Point(p.getX(), p.getY() + 4)};
	private static final Function<WorldPoint, Point[]> getSEMeleePoints = (p) -> new Point[]{new Point(p.getX() + 4, p.getY() - 4), new Point(p.getX() + 4, p.getY()), new Point(p.getX() + 3, p.getY()), new Point(p.getX() + 3, p.getY() - 3), new Point(p.getX(), p.getY() - 3), new Point(p.getX(), p.getY() - 4)};
	private static final Function<WorldPoint, Point[]> getSWMeleePoints = (p) -> new Point[]{new Point(p.getX() - 4, p.getY() - 4), new Point(p.getX(), p.getY() - 4), new Point(p.getX(), p.getY() - 3), new Point(p.getX() - 3, p.getY() - 3), new Point(p.getX() - 3, p.getY()), new Point(p.getX() - 4, p.getY())};

	public Dimension render(Graphics2D graphics)
	{
		if (xarpus.isInstanceTimerRunning() && !xarpus.isExhumedSpawned() && xarpus.inRoomRegion(TheatrePlugin.XARPUS_REGION) && config.xarpusInstanceTimer())
		{
			Player player = client.getLocalPlayer();

			if (player != null)
			{
				Point point = player.getCanvasTextLocation(graphics, "#", player.getLogicalHeight() + 120);
				if (point != null)
				{
					renderTextLocation(graphics, String.valueOf(xarpus.getInstanceTimer()), Color.CYAN, point);
				}
			}
		}

		if (xarpus.isXarpusActive())
		{
			NPC boss = xarpus.getXarpusNPC();

			if ((config.xarpusTickP2() && (boss.getId() == NpcID.XARPUS_8340 || boss.getId() == 10768 || boss.getId() == 10772))
				|| (config.xarpusTickP3() && (boss.getId() == NpcID.XARPUS_8341 || boss.getId() == 10769 || boss.getId() == 10773)))
			{
				int tick = xarpus.getXarpusTicksUntilAttack();
				final String ticksLeftStr = String.valueOf(tick);
				Point canvasPoint = boss.getCanvasTextLocation(graphics, ticksLeftStr, 130);
				renderTextLocation(graphics, ticksLeftStr, Color.WHITE, canvasPoint);
			}

			if ((config.xarpusExhumed() || config.xarpusExhumedTick()) && (boss.getId() == NpcID.XARPUS_8339 || boss.getId() == 10767 || boss.getId() == 10771))
			{
				if (!xarpus.getXarpusExhumeds().isEmpty())
				{
					Collection<Pair<GroundObject, Integer>> exhumeds = xarpus.getXarpusExhumeds().values();
					exhumeds.forEach((p) -> {
						GroundObject o = p.getLeft();
						int ticks = p.getRight();

						if (config.xarpusExhumed())
						{
							Polygon poly = o.getCanvasTilePoly();
							if (poly != null)
							{
								graphics.setColor(new Color(0, 255, 0, 130));
								graphics.setStroke(new BasicStroke(1));
								graphics.draw(poly);
							}
						}
						if (config.xarpusExhumedTick())
						{
							String count = Integer.toString(ticks);
							LocalPoint lp = o.getLocalLocation();
							Point point = Perspective.getCanvasTextLocation(client, graphics, lp, count, 0);
							if (point != null)
							{
								renderTextLocation(graphics, count, Color.WHITE, point);
							}
						}
					});
				}
			}

			if (config.xarpusLineOfSight() != TheatreConfig.XARPUS_LINE_OF_SIGHT.OFF)
			{
				renderLineOfSightPolygon(graphics);
			}
		}
		return null;
	}

	/* created by the zhuri, the myth, the legend, the tob god. */
	private void renderLineOfSightPolygon(Graphics2D graphics)
	{
		NPC xarpusNpc = xarpus.getXarpusNPC();
		if (xarpusNpc != null && (xarpusNpc.getId() == NpcID.XARPUS_8340 || xarpusNpc.getId() == 10768 || xarpusNpc.getId() == 10772) && !xarpusNpc.isDead() && xarpus.isPostScreech())
		{
			WorldPoint xarpusWorldPoint = WorldPoint.fromLocal(client, xarpusNpc.getLocalLocation());
			Direction dir = Direction.getPreciseDirection(xarpusNpc.getOrientation());
			if (dir != null)
			{
				Point[] points;
				boolean markMeleeTiles = config.xarpusLineOfSight() == TheatreConfig.XARPUS_LINE_OF_SIGHT.MELEE_TILES;
				switch (dir)
				{
					case NORTHEAST:
						points = markMeleeTiles ? getNEMeleePoints.apply(xarpusWorldPoint) : getNEBoxPoints.apply(xarpusWorldPoint);
						break;
					case NORTHWEST:
						points = markMeleeTiles ? getNWMeleePoints.apply(xarpusWorldPoint) : getNWBoxPoints.apply(xarpusWorldPoint);
						break;
					case SOUTHEAST:
						points = markMeleeTiles ? getSEMeleePoints.apply(xarpusWorldPoint) : getSEBoxPoints.apply(xarpusWorldPoint);
						break;
					case SOUTHWEST:
						points = markMeleeTiles ? getSWMeleePoints.apply(xarpusWorldPoint) : getSWBoxPoints.apply(xarpusWorldPoint);
						break;
					default:
						return;
				}

				Polygon poly = new Polygon();
				Point[] dangerousPolygonPoints = points;
				int dangerousPolygonPointsLength = points.length;

				Arrays.stream(dangerousPolygonPoints, 0, dangerousPolygonPointsLength)
						.map(point -> localToCanvas(dir, point.getX(), point.getY()))
						.filter(Objects::nonNull)
						.forEach(p -> poly.addPoint(p.getX(), p.getY()));

				renderPoly(graphics, config.xarpusLineOfSightColor(), poly);
			}
		}
	}

	private Point localToCanvas(Direction dir, int px, int py)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, px, py);
		int x = lp.getX();
		int y = lp.getY();
		int s = 64;
		switch (dir)
		{
			case NORTHEAST:
				return Perspective.localToCanvas(client, new LocalPoint(x - s, y - s), client.getPlane());
			case NORTHWEST:
				return Perspective.localToCanvas(client, new LocalPoint(x + s, y - s), client.getPlane());
			case SOUTHEAST:
				return Perspective.localToCanvas(client, new LocalPoint(x - s, y + s), client.getPlane());
			case SOUTHWEST:
				return Perspective.localToCanvas(client, new LocalPoint(x + s, y + s), client.getPlane());
			default:
				return null;
		}
	}
}
