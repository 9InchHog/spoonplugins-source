/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
package net.runelite.client.plugins.spoonobjectindicators;

import net.runelite.api.*;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

class sObjectIndicatorsOverlay extends Overlay
{
	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	private static final Color FILL = new Color(0, 0, 0, 0);
	private final Client client;
	private final sObjectIndicatorsConfig config;
	private final sObjectIndicatorsPlugin plugin;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	private sObjectIndicatorsOverlay(Client client, sObjectIndicatorsConfig config, sObjectIndicatorsPlugin plugin, ModelOutlineRenderer modelOutlineRenderer)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		this.modelOutlineRenderer = modelOutlineRenderer;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.LOW);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		for (ColorTileObject colorTileObject : plugin.getObjects())
		{
			TileObject object = colorTileObject.getTileObject();
			Color color = colorTileObject.getColor();

			if (object.getPlane() != client.getPlane())
			{
				continue;
			}

			if (color == null || !config.rememberObjectColors())
			{
				// Fallback to the current config if the object is marked before the addition of multiple colors
				color = config.markerColor();
			}

			switch (config.objectMarkerRenderStyle())
			{
				case HULL:
					final Shape polygon;
					Shape polygon2 = null;

					if (object instanceof GameObject)
					{
						polygon = ((GameObject) object).getConvexHull();
					}
					else if (object instanceof WallObject)
					{
						polygon = ((WallObject) object).getConvexHull();
						polygon2 = ((WallObject) object).getConvexHull2();
					}
					else if (object instanceof DecorativeObject)
					{
						polygon = ((DecorativeObject) object).getConvexHull();
						polygon2 = ((DecorativeObject) object).getConvexHull2();
					}
					else
					{
						polygon = object.getCanvasTilePoly();
					}

					if (polygon != null)
					{
						OverlayUtil.renderPolygon(graphics, polygon, color);
					}

					if (polygon2 != null)
					{
						OverlayUtil.renderPolygon(graphics, polygon2, color);
					}
					break;
				case CLICKBOX:
					Shape clickbox = object.getClickbox();
					if (clickbox != null)
					{
						Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), config.opacity());
						OverlayUtil.renderHoverableArea(graphics, object.getClickbox(), client.getMouseCanvasPosition(), fillColor, color, color.darker());
					}
					break;
				case AREA:
					final Shape polygon3;
					Shape polygon4 = null;

					if (object instanceof GameObject)
					{
						polygon3 = ((GameObject) object).getConvexHull();
					}
					else if (object instanceof WallObject)
					{
						polygon3 = ((WallObject) object).getConvexHull();
						polygon4 = ((WallObject) object).getConvexHull2();
					}
					else if (object instanceof DecorativeObject)
					{
						polygon3 = ((DecorativeObject) object).getConvexHull();
						polygon4 = ((DecorativeObject) object).getConvexHull2();
					}
					else
					{
						polygon3 = object.getCanvasTilePoly();
					}

					if (polygon3 != null)
					{
						int cRed = color.getRed();
						int cGreen = color.getGreen();
						int cBlue = color.getBlue();
						Color fillColor = new Color(cRed, cGreen, cBlue, config.opacity());
						graphics.setColor(fillColor);
						graphics.fill(polygon3);
					}

					if (polygon4 != null)
					{
						int cRed = color.getRed();
						int cGreen = color.getGreen();
						int cBlue = color.getBlue();
						Color fillColor = new Color(cRed, cGreen, cBlue, config.opacity());
						graphics.setColor(fillColor);
						graphics.fill(polygon4);
					}
					break;
				case OUTLINE:
					this.modelOutlineRenderer.drawOutline(object, this.config.outlineWidth(), color, config.outlineFeather());
					break;
			}
		}
		return null;
	}
}
