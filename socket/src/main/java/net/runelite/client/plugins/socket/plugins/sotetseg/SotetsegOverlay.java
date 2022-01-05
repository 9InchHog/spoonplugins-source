/*
 * Copyright (c) 2020, Charles Xu <github.com/kthisiscvpv>
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

package net.runelite.client.plugins.socket.plugins.sotetseg;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public class SotetsegOverlay extends Overlay
{

	private final Client client;
	private final SotetsegPlugin plugin;
	private final SotetsegConfig config;

	private int flashTimeout;
	private int chosenTextTimeout;

	@Inject
	private SotetsegOverlay(Client client, SotetsegPlugin plugin, SotetsegConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}


	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.isSotetsegActive() || config.showTestOverlay())
		{
			Set<WorldPoint> tiles;
			if(config.showTestOverlay())
			{
				tiles = new HashSet<>();
				for(int i = 0; i < 5; i++)
				{
					try
					{
						WorldPoint base = client.getLocalPlayer().getWorldLocation();
						WorldPoint wp = new WorldPoint(base.getX(), base.getY() + i, base.getPlane());
						tiles.add(wp);
					}
					catch (Exception ignored)
					{

					}
				}
			}
			else
			{
				tiles = plugin.getMazePings();
			}

			for (final WorldPoint next : tiles)
			{
				final LocalPoint localPoint = LocalPoint.fromWorld(client, next);
				if (localPoint != null) {
					Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
					if (poly != null) {
						if (!config.streamerMode()) {
							int outlineAlpha;
							if (config.getTileOutlineSize() > 0) {
								outlineAlpha = 255;
							} else {
								outlineAlpha = 0;
							}

							if (config.antiAlias()) {
								graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
							} else {
								graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
							}

							Color color = new Color(config.getTileOutline().getRed(), config.getTileOutline().getGreen(), config.getTileOutline().getBlue(), outlineAlpha);
							graphics.setColor(color);

							Stroke originalStroke = graphics.getStroke();

							graphics.setStroke(new BasicStroke((float) config.getTileOutlineSize()));
							graphics.draw(poly);

							Color fill = config.getTileColor();
							int alpha = Math.min(Math.max(config.getTileTransparency(), 0), 255);
							Color realFill = new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), alpha);
							graphics.setColor(realFill);
							graphics.fill(poly);

							graphics.setStroke(originalStroke);
						}
					}
				}
			}
		}

		if (plugin.isSotetsegActive())
		{
			if (config.solveMaze())
			{
				if (plugin.isInUnderWorld()) {
					drawPoints(graphics, plugin.getSolvedRedTiles(), plugin.getMazeSolvedIndex());
					drawPoints(graphics, plugin.getMazeSolved(), plugin.getMazeSolvedIndex());
				} else {
					drawPoints(graphics, plugin.getMazeSolved(), plugin.getMazeSolvedIndex());
				}
			}

			if (plugin.isMazeActive() && config.showSotetsegInstanceTimer())
			{
				try
				{
					String text = String.valueOf(plugin.getInstanceTime());
					int width = graphics.getFontMetrics().stringWidth(text);
					Point base = Perspective.localToCanvas(client, plugin.getSotetsegNPC().getLocalLocation(), client.getPlane(), plugin.getSotetsegNPC().getLogicalHeight());
					Point actual = new Point(base.getX() - width / 2, base.getY() + 100);
					graphics.setFont(new Font("Arial", Font.BOLD, config.getFontSizeInstanceTimer()));
					OverlayUtil.renderTextLocation(graphics, actual, text, config.sotetsegInstanceTimerColor());
				}
				catch (NullPointerException ignored)
				{
				}
			}

			if (plugin.isMazeActive() && config.showSotetsegInstanceTimerPlayer())
			{
				try
				{
					String text = String.valueOf(plugin.getInstanceTime());
					Player player = client.getLocalPlayer();
					if (player != null)
					{
						Point point = player.getCanvasTextLocation(graphics, "#", player.getLogicalHeight() + 250);
						if (point != null)
						{
							graphics.setFont(new Font("Arial", Font.BOLD, config.getFontSizeInstanceTimer()));
							OverlayUtil.renderTextLocation(graphics, point, text, config.sotetsegInstanceTimerPlayerColor());
						}
					}
				}
				catch (NullPointerException ignored)
				{
				}
			}

			if (plugin.flashScreen && config.flashScreen())
			{
				Color originalColor = graphics.getColor();
				graphics.setColor(new Color(255, 0, 0, 70));
				graphics.fill(client.getCanvas().getBounds());
				graphics.setColor(originalColor);

				if (++flashTimeout >= 15)
				{
					flashTimeout = 0;
					plugin.flashScreen = false;
				}
			}

			if (plugin.chosenTextScreen && config.isChosenText() && config.hideScreenFlash())
			{
				String text = config.customChosenText();
				graphics.setFont(new Font("Arial", Font.BOLD, 20));
				int width = graphics.getFontMetrics().stringWidth(text);
				int drawX = client.getViewportWidth() / 2 - width / 2;
				int drawY = client.getViewportHeight() - client.getViewportHeight() / 2 - 12;
				OverlayUtil.renderTextLocation(graphics, new net.runelite.api.Point(drawX, drawY), text, Color.WHITE);
				if (++chosenTextTimeout >= (config.chosenTextDuration() * 50))
				{
					chosenTextTimeout = 0;
					plugin.chosenTextScreen = false;
				}
			}
		}

		return null;
	}

	private void drawPoints(Graphics2D graphics, ArrayList<Point> points, int index)
	{
		WorldPoint player = Objects.requireNonNull(client.getLocalPlayer()).getWorldLocation();

		IntStream.range(0, points.size()).forEach(i -> {
			Point p = points.get(i);
			WorldPoint wp = WorldPoint.fromRegion(player.getRegionID(), p.getX(), p.getY(), player.getPlane());

			if (plugin.isInUnderWorld()) {
				wp = WorldPoint.fromRegion(player.getRegionID(), p.getX() + 42 - 9, p.getY() + 31 - 22, player.getPlane());
			}

			LocalPoint lp = LocalPoint.fromWorld(client, wp);

			if (config.numbersOn() && index < i)
			{
				try
				{
					Point textPoint = Perspective.getCanvasTextLocation(client, graphics, lp, String.valueOf(i), 0);
					Point canvasCenterPoint = new Point(textPoint.getX(), (int) ((double) textPoint.getY() + Math.floor((double) config.getFontSize() / 2.0D)));
					OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, String.valueOf(i), Color.WHITE);
				}
				catch (NullPointerException ignored)
				{
				}
			}
			if (config.highlightTiles() && index < i)
			{
				try
				{
					Polygon poly = Perspective.getCanvasTilePoly(client, lp);
					if (poly != null)
					{
						Color colorTile = config.getHighlightTileOutline();
						int outlineAlpha;
						if (config.solvedTileWidth() > 0) {
							outlineAlpha = 255;
						} else {
							outlineAlpha = 0;
						}

						if (config.antiAlias()) {
							graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						} else {
							graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
						}

						graphics.setColor(new Color(colorTile.getRed(), colorTile.getGreen(), colorTile.getBlue(), outlineAlpha));
						Stroke originalStroke = graphics.getStroke();
						double strokeSize = config.solvedTileWidth();
						graphics.setStroke(new BasicStroke((float) strokeSize));

						if (i < points.size())
						{
							graphics.draw(poly);

							int alpha = Math.min(Math.max(config.solvedTileOpacity(), 0), 255);
							Color realFill = new Color(colorTile.getRed(), colorTile.getGreen(), colorTile.getBlue(), alpha);
							graphics.setColor(realFill);
							graphics.fill(poly);

							graphics.setStroke(originalStroke);
						}
					}
				}
				catch (NullPointerException ignored)
				{
				}
			}
		});

	}
}
