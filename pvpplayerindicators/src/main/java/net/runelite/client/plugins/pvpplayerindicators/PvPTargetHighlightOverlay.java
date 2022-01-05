/*
 * Copyright (c) 2018, Kamiel <https://github.com/Kamielvf>
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

package net.runelite.client.plugins.pvpplayerindicators;

import java.awt.*;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

@Singleton
public class PvPTargetHighlightOverlay extends Overlay {
    private final PvPPlayerIndicatorsTargetService playerIndicatorsTargetService;
    private final PvPPlayerIndicatorsPlugin playerIndicatorsPlugin;
    private final PvPPlayerIndicatorsConfig config;

    @Inject
    private Client client;

    @Inject
    private PvPTargetHighlightOverlay(PvPPlayerIndicatorsConfig config, PvPPlayerIndicatorsTargetService playerIndicatorsTargetService, PvPPlayerIndicatorsPlugin plugin) {
        this.config = config;
        this.playerIndicatorsTargetService = playerIndicatorsTargetService;
        this.playerIndicatorsPlugin = plugin;
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.MED);
    }

    public Dimension render(Graphics2D graphics) {
        if (config.highlightTargets() != PvPPlayerIndicatorsConfig.TargetHighlightMode.OFF) {
            this.playerIndicatorsTargetService.forEachPlayer((player, color) -> {
                if (config.highlightTargets() == PvPPlayerIndicatorsConfig.TargetHighlightMode.TILE) {
                    renderPolygon(graphics, player.getCanvasTilePoly(), color);
                } else if (config.highlightTargets() == PvPPlayerIndicatorsConfig.TargetHighlightMode.HULL) {
                    renderPolygon(graphics, player.getConvexHull(), config.getTargetColor());
                } else if (config.highlightTargets() == PvPPlayerIndicatorsConfig.TargetHighlightMode.TRUE_LOCATION) {
                    final WorldPoint playerPos = player.getWorldLocation();
                    if (playerPos != null) {
                        final LocalPoint playerPosLocal = LocalPoint.fromWorld(client, playerPos);
                        if (playerPosLocal != null) {
                            renderTile(graphics, playerPosLocal, color);
                        }
                    }
                }
            });
        }
        return null;
    }

    private void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color)
    {
        if (dest == null)
        {
            return;
        }

        final Polygon poly = Perspective.getCanvasTilePoly(client, dest);

        if (poly == null)
        {
            return;
        }

        renderPolygon(graphics, poly, color);
    }

    public static void renderPolygon(Graphics2D graphics, Shape poly, Color color)
    {
        graphics.setColor(color);
        final Stroke originalStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(2));
        graphics.draw(poly);
        graphics.setColor(new Color(0, 0, 0, 0));
        graphics.fill(poly);
        graphics.setStroke(originalStroke);
    }
}
