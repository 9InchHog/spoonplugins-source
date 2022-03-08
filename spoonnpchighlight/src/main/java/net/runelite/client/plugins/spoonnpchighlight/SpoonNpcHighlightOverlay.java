package net.runelite.client.plugins.spoonnpchighlight;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

public class SpoonNpcHighlightOverlay extends Overlay {
    private final Client client;

    private final SpoonNpcHighlightPlugin plugin;

    private final SpoonNpcHighlightConfig config;

    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private SpoonNpcHighlightOverlay(Client client, SpoonNpcHighlightPlugin plugin, SpoonNpcHighlightConfig config, ModelOutlineRenderer modelOutlineRenderer) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        for(NPC npc : this.client.getNpcs()) {
            boolean showWhileDead = !config.ignoreDeadNpcs() || plugin.checkSpecificList(plugin.ignoreDeadExclusionList, new ArrayList<>(), npc);

            if (npc.getHealthRatio() != 0 || showWhileDead) {
                boolean foundNpc = false;
                Color outlineColor = config.highlightColor();
                Color fillColor = config.fillColor();
                if (npc.getInteracting() != null && this.client.getLocalPlayer() != null && npc.getInteracting().equals(this.client.getLocalPlayer()) && config.interactingHighlight() != SpoonNpcHighlightConfig.interactingHighlightMode.OFF) {
                    outlineColor = config.interactingColor();
                    fillColor = new Color(config.interactingColor().getRed(), config.interactingColor().getGreen(), config.interactingColor().getBlue(), config.fillColor().getAlpha());
                }

                NPCComposition npcComposition = npc.getTransformedComposition();
                if (npcComposition != null) {
                    if (config.tileHighlight() && plugin.checkSpecificList(plugin.tileNames, plugin.tileIds, npc)) {
                        int size = npcComposition.getSize();
                        LocalPoint lp = npc.getLocalLocation();
                        if (lp != null) {
                            Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
                            if (tilePoly != null) {
                                this.renderPoly(graphics, outlineColor, fillColor, tilePoly, this.config.highlightThiCC());
                                foundNpc = true;
                            }
                        }
                    }

                    if (config.trueTileHighlight() && plugin.checkSpecificList(plugin.trueTileNames, plugin.trueTileIds, npc)) {
                        int size = npcComposition.getSize();
                        LocalPoint lp = LocalPoint.fromWorld(this.client, npc.getWorldLocation());
                        if (lp != null) {
                            lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
                            Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
                            if (tilePoly != null) {
                                this.renderPoly(graphics, outlineColor, fillColor, tilePoly, this.config.highlightThiCC());
                                foundNpc = true;
                            }
                        }
                    }

                    if (config.swTileHighlight() && plugin.checkSpecificList(plugin.swTileNames, plugin.swTileIds, npc)) {
                        int size = npcComposition.getSize();
                        LocalPoint lp = npc.getLocalLocation();
                        if (lp != null) {
                            int x = lp.getX() - (size - 1) * 128 / 2;
                            int y = lp.getY() - (size - 1) * 128 / 2;
                            Polygon tilePoly = Perspective.getCanvasTilePoly(this.client, new LocalPoint(x, y));
                            if (tilePoly != null) {
                                this.renderPoly(graphics, outlineColor, fillColor, tilePoly, this.config.highlightThiCC());
                                foundNpc = true;
                            }
                        }
                    }

                    if (config.hullHighlight() && plugin.checkSpecificList(plugin.hullNames, plugin.hullIds, npc)) {
                        Shape objectClickbox = npc.getConvexHull();
                        if (objectClickbox != null) {
                            this.renderPoly(graphics, outlineColor, fillColor, objectClickbox, this.config.highlightThiCC());
                            foundNpc = true;
                        }
                    }

                    if (config.areaHighlight() && plugin.checkSpecificList(plugin.areaNames, plugin.areaIds, npc)) {
                        graphics.setColor(fillColor);
                        graphics.fill(npc.getConvexHull());
                        foundNpc = true;
                    }

                    if (config.outlineHighlight() && plugin.checkSpecificList(plugin.outlineNames, plugin.outlineIds, npc)) {
                        this.modelOutlineRenderer.drawOutline(npc, this.config.outlineThiCC(), outlineColor, config.outlineFeather());
                        foundNpc = true;
                    }

                    if (config.turboHighlight() && plugin.checkSpecificList(plugin.turboNames, plugin.turboIds, npc)) {
                        foundNpc = true;
                        Color raveColor = plugin.turboIds.contains(npc.getId()) ? plugin.turboColors.get(plugin.turboIds.indexOf(npc.getId()) + plugin.turboNames.size()) : Color.WHITE;
                        Color oColor = new Color(raveColor.getRed(), raveColor.getGreen(), raveColor.getBlue(), new Random().nextInt(254) + 1);
                        Color fColor = new Color(raveColor.getRed(), raveColor.getGreen(), raveColor.getBlue(), new Random().nextInt(254) + 1);
                        if(raveColor == Color.WHITE){
                            if(npc.getName() != null){
                                String name = npc.getName().toLowerCase();
                                int index = 0;
                                for(String str : plugin.turboNames){
                                    if(str.equalsIgnoreCase(name) || (str.contains("*")
                                            && ((str.startsWith("*") && str.endsWith("*") && name.contains(str.replace("*", "")))
                                            || (str.startsWith("*") && name.endsWith(str.replace("*", ""))) || name.startsWith(str.replace("*", ""))))){
                                        raveColor = plugin.turboColors.get(index);
                                        oColor = new Color(raveColor.getRed(), raveColor.getGreen(), raveColor.getBlue(), new Random().nextInt(254) + 1);
                                        fColor = new Color(raveColor.getRed(), raveColor.getGreen(), raveColor.getBlue(), new Random().nextInt(254) + 1);
                                        break;
                                    }
                                    index++;
                                }
                            }
                        }

                        if(plugin.turboModeStyle == 0){
                            int size = npcComposition.getSize();
                            LocalPoint lp = npc.getLocalLocation();
                            if (lp != null) {
                                Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
                                if (tilePoly != null) {
                                    this.renderPoly(graphics, oColor, fColor, tilePoly, plugin.turboTileWidth);
                                }
                            }
                        }else if(plugin.turboModeStyle == 1){
                            int size = npcComposition.getSize();
                            LocalPoint lp = LocalPoint.fromWorld(this.client, npc.getWorldLocation());
                            if (lp != null) {
                                lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
                                Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
                                if (tilePoly != null) {
                                    this.renderPoly(graphics, oColor, fColor, tilePoly, plugin.turboTileWidth);
                                }
                            }
                        }else if(plugin.turboModeStyle == 2){
                            int size = npcComposition.getSize();
                            LocalPoint lp = npc.getLocalLocation();
                            if (lp != null) {
                                int x = lp.getX() - (size - 1) * 128 / 2;
                                int y = lp.getY() - (size - 1) * 128 / 2;
                                Polygon tilePoly = Perspective.getCanvasTilePoly(this.client, new LocalPoint(x, y));
                                if (tilePoly != null) {
                                    this.renderPoly(graphics, oColor, fColor, tilePoly, plugin.turboTileWidth);
                                }
                            }
                        }else if(plugin.turboModeStyle == 3){
                            Shape objectClickbox = npc.getConvexHull();
                            if (objectClickbox != null) {
                                this.renderPoly(graphics, oColor, fColor, objectClickbox, plugin.turboTileWidth);
                            }
                        }else if(plugin.turboModeStyle == 4){
                            graphics.setColor(fColor);
                            graphics.fill(npc.getConvexHull());
                        }else {
                            this.modelOutlineRenderer.drawOutline(npc, plugin.turboTileWidth, oColor, plugin.turboOutlineFeather);
                        }
                    }

                    if (foundNpc && config.interactingHighlight() == SpoonNpcHighlightConfig.interactingHighlightMode.BOTH && npc.getInteracting() != null
                            && this.client.getLocalPlayer() != null && npc.getInteracting().getName() != null && npc.getInteracting().getName().equals(this.client.getLocalPlayer().getName())) {
                        String text = npc.getInteracting().getName();
                        Point textLoc = npc.getCanvasTextLocation(graphics, text, npc.getLogicalHeight() + 20);
                        if (textLoc != null) {
                            Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
                            OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
                            OverlayUtil.renderTextLocation(graphics, textLoc, text, config.interactingColor());
                        }
                    }else if(plugin.namesToDisplay.size() > 0 && npc.getName() != null && plugin.namesToDisplay.contains(npc.getName().toLowerCase())) {
                        String text = npc.getName();
                        Point textLoc = npc.getCanvasTextLocation(graphics, text, npc.getLogicalHeight() + 20);
                        if (textLoc != null) {
                            Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
                            OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
                            OverlayUtil.renderTextLocation(graphics, textLoc, text, config.highlightColor());
                        }
                    }
                }
            }
        }

        if(config.respawnTimer() != SpoonNpcHighlightConfig.respawnTimerMode.OFF) {
            for (NpcSpawn n : plugin.npcSpawns) {
                if (n.spawnPoint != null && n.respawnTime != -1 && n.dead) {
                    final LocalPoint lp = LocalPoint.fromWorld(client, n.spawnPoint.getX(), n.spawnPoint.getY());

                    if (lp != null) {
                    final LocalPoint centerLp = new LocalPoint(
                            lp.getX() + Perspective.LOCAL_TILE_SIZE * (n.size - 1) / 2,
                            lp.getY() + Perspective.LOCAL_TILE_SIZE * (n.size - 1) / 2);
                        Color raveColor = plugin.turboIds.contains(n.id) ? plugin.turboColors.get(plugin.turboIds.indexOf(n.id) + plugin.turboNames.size()) : Color.WHITE;
                        Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, centerLp, n.size);
                        if (tilePoly != null) {
                            Color oColor = raveColor != Color.WHITE ? new Color(raveColor.getRed(), raveColor.getGreen(), raveColor.getBlue(), new Random().nextInt(254) + 1) : config.highlightColor();
                            Color fColor = raveColor != Color.WHITE ? new Color(raveColor.getRed(), raveColor.getGreen(), raveColor.getBlue(), new Random().nextInt(254) + 1) : config.fillColor();
                            if(raveColor == Color.WHITE){
                                int index = 0;
                                for(String str : plugin.turboNames){
                                    if(str.equalsIgnoreCase(n.name) || (str.contains("*")
                                            && ((str.startsWith("*") && str.endsWith("*") && n.name.contains(str.replace("*", "")))
                                            || (str.startsWith("*") && n.name.endsWith(str.replace("*", ""))) || n.name.startsWith(str.replace("*", ""))))){
                                        raveColor = plugin.turboColors.get(index);
                                        oColor = new Color(raveColor.getRed(), raveColor.getGreen(), raveColor.getBlue(), new Random().nextInt(254) + 1);
                                        fColor = new Color(raveColor.getRed(), raveColor.getGreen(), raveColor.getBlue(), new Random().nextInt(254) + 1);
                                        break;
                                    }
                                    index++;
                                }
                            }
                            this.renderPoly(graphics, oColor, fColor, tilePoly, raveColor != Color.WHITE ? plugin.turboTileWidth : config.highlightThiCC());
                        }

                        String text;
                        if(config.respawnTimer() == SpoonNpcHighlightConfig.respawnTimerMode.SECONDS){
                            final Instant now = Instant.now();
                            final double baseTick = (n.respawnTime - (client.getTickCount() - n.diedOnTick)) * (Constants.GAME_TICK_LENGTH / 1000.0);
                            final double sinceLast = (now.toEpochMilli() - plugin.lastTickUpdate.toEpochMilli()) / 1000.0;
                            final double timeLeft = Math.max(0.0, baseTick - sinceLast);
                            text = String.valueOf(timeLeft);
                            if(text.contains(".")){
                                text = text.substring(0, text.indexOf(".") + 2);
                            }
                        }else {
                            text = String.valueOf((n.respawnTime - (client.getTickCount() - n.diedOnTick)));
                        }

                        Point textLoc = Perspective.getCanvasTextLocation(client, graphics, lp, text, 0);
                        if (textLoc != null) {
                            Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
                            OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
                            if(raveColor != Color.WHITE){
                                OverlayUtil.renderTextLocation(graphics, textLoc, text, new Color(raveColor.getRed(), raveColor.getGreen(), raveColor.getBlue(), new Random().nextInt(205) + 50));
                            }else {
                                OverlayUtil.renderTextLocation(graphics, textLoc, text, config.respawnTimerColor());
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void renderPoly(Graphics2D graphics, Color outlineColor, Color fillColor, Shape polygon, double width) {
        if (polygon != null) {
            if (this.config.antiAlias()) {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            } else {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            }
            graphics.setColor(outlineColor);
            graphics.setStroke(new BasicStroke((float) width));
            graphics.draw(polygon);
            graphics.setColor(fillColor);
            graphics.fill(polygon);
        }
    }

    public static String to_mmss(int ticks) {
        int m = ticks / 100;
        int s = (ticks - m * 100) * 6 / 10;
        String timeStr = m + (s < 10 ? ":0" : ":") + s;
        return String.valueOf((ticks - (ticks / 100) * 100) * 6 / 10);
    }
}
