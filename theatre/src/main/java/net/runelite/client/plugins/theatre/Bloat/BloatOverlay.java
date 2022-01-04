/*
 * BikkusLite / UncleLite © 2020
 */

package net.runelite.client.plugins.theatre.Bloat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.theatre.Bloat.stomp.BloatSafespot;
import net.runelite.client.plugins.theatre.RoomOverlay;
import net.runelite.client.plugins.theatre.TheatreConfig;
import net.runelite.client.ui.overlay.OverlayLayer;

public class BloatOverlay extends RoomOverlay
{
	@Inject
	private Bloat bloat;

	@Inject
	protected BloatOverlay(TheatreConfig config)
	{
		super(config);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	public Dimension render(Graphics2D graphics)
	{
		if (bloat.isBloatActive())
		{
			if (config.bloatIndicator())
			{
				renderPoly(graphics, bloat.getBloatStateColor(), bloat.getBloatTilePoly(), 2);
			}

			if (config.bloatTickCounter())
			{
				NPC boss = bloat.getBloatNPC();

				int tick = bloat.getBloatTickCount();
				final String ticksCounted = String.valueOf(tick);
				Point canvasPoint = boss.getCanvasTextLocation(graphics, ticksCounted, 50);
				if ((bloat.getBloatState() > 1 && bloat.getBloatState() < 4) && config.BloatTickCountStyle() == TheatreConfig.BLOATTIMEDOWN.COUNTDOWN)
				{
					renderTextLocation(graphics, String.valueOf(33 - bloat.getBloatDownCount()), Color.WHITE, canvasPoint);
				}
				else
				{
					renderTextLocation(graphics, ticksCounted, Color.WHITE, canvasPoint);
				}
			}

			if (config.bloatHands())
			{
				for (WorldPoint point : bloat.getBloatHands().keySet())
				{
					drawTile(graphics, point, config.bloatHandsColor(), config.bloatHandsWidth(), 255, 10);

					if (config.bloatHandsTickCounter())
					{
						String ticks = Integer.toString(bloat.getBloatHands().get(point));
						LocalPoint lp = LocalPoint.fromWorld(client, point);

						if (lp != null)
						{
							Point point2 = Perspective.getCanvasTextLocation(client, graphics, lp, ticks, 0);
							renderTextLocation(graphics, ticks, Color.WHITE, point2);
						}
					}
				}
			}

			if (config.bloatStompSafespots() && bloat.getBloatDown() != null)
			{
				BloatSafespot safespot = bloat.getBloatDown().getBloatSafespot();
				safespot.getSafespotLines().forEach((line) -> drawLine(graphics, line, config.bloatStompSafespotsColor(), 1));
			}
		}
		return null;
	}
}
