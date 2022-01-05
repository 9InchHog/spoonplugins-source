package net.runelite.client.plugins.grotesqueguardians;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;
import java.util.Iterator;

public class GGOverlay extends Overlay {
    private final Client client;

    private final GGConfig config;

    @Inject
    private GGPlugin plugin;

    @Inject
    private GGOverlay(Client client, GGPlugin plugin, GGConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        if (!client.isInInstancedRegion() || client.getMapRegions()[0] != GGUtil.REGION_ID)
            return null;
        if (plugin.getDuskNPC() != null) {
            if (config.duskTickCounter() && plugin.getDuskTicks() > 0) {
                String text = Integer.toString(plugin.getDuskTicks());
                Point canvasPoint = plugin.getDuskNPC().getCanvasTextLocation(graphics, text, 60);

                renderTextLocation(graphics, canvasPoint, text, Color.WHITE);
            }
            if (plugin.stepBack && config.stepBackWarning() && plugin.getDuskTicks() > 0) {
                String warning = "STEP BACK!";
                Point canvasPoint = plugin.getDuskNPC().getCanvasTextLocation(graphics, warning, 200);

                renderTextLocation(graphics, canvasPoint, warning, Color.RED);
            }
            if (config.duskOverlay()) {
                NPC npc = plugin.getDuskNPC();
                switch (plugin.getDuskState()) {
                    case ATTACK:
                        renderNpcOverlay(graphics, npc, Color.GREEN, config.fightWidth(), 150, config.fightOpacity());
                        break;
                    case CANT_ATTACK:
                        renderNpcOverlay(graphics, npc, Color.RED, config.fightWidth(), 150, config.fightOpacity());
                        break;
                    case TRANSITION:
                        renderNpcOverlay(graphics, npc, Color.WHITE, config.fightWidth(), 150, config.fightOpacity());
                        break;
                }
            }
        }
        if (plugin.getDawnNPC() != null) {
            if (config.dawnTickCounter() && plugin.getDawnTicks() > 0) {
                String text = Integer.toString(plugin.getDawnTicks());
                Point canvasPoint = plugin.getDawnNPC().getCanvasTextLocation(graphics, text, 60);

                renderTextLocation(graphics, canvasPoint, text, Color.WHITE);
            }
            if (config.dawnOverlay()) {
                NPC npc = plugin.getDawnNPC();
                switch (plugin.getDawnState()) {
                    case ATTACK:
                        renderNpcOverlay(graphics, npc, Color.GREEN, config.fightWidth(), 150, config.fightOpacity());
                        break;
                    case CANT_ATTACK:
                        renderNpcOverlay(graphics, npc, Color.RED, config.fightWidth(), 150, config.fightOpacity());
                        break;
                    case TRANSITION:
                        renderNpcOverlay(graphics, npc, Color.WHITE, config.fightWidth(), 150, config.fightOpacity());
                        break;
                }
            }
            if (config.healingOrbCounter() && plugin.getDawnNPC().getId() == 7884) {
                Iterator<GameObject> healingOrbs = plugin.getDawnHealingOrbs().keySet().iterator();
                while (healingOrbs.hasNext()) {
                    GameObject gameObject = healingOrbs.next();
                    Polygon poly = gameObject.getCanvasTilePoly();
                    if (poly != null) {
                        graphics.setColor(Color.GRAY);
                        graphics.setStroke(new BasicStroke(1.0F));
                        graphics.draw(poly);
                        String ticksLeft = Integer.toString(plugin.getDawnHealingOrbs().get(gameObject) + 1);
                        LocalPoint lp = gameObject.getLocalLocation();
                        Point point = Perspective.getCanvasTextLocation(client, graphics, lp, ticksLeft, 0);
                        if (point != null) {

                            renderTextLocation(graphics, point, ticksLeft, Color.WHITE);
                        }
                    }
                }
            }
        }
        for (GraphicsObject graphicsObject : client.getGraphicsObjects()) {
            LocalPoint lp = graphicsObject.getLocation();
            Polygon poly = Perspective.getCanvasTilePoly(client, lp);
            Polygon polyArea = Perspective.getCanvasTileAreaPoly(client, lp, 3);
            Color color = Color.WHITE;
            if (config.highlightLightning() && (graphicsObject.getId() == GGUtil.PURPLE_LIGHTNING || graphicsObject.getId() == GGUtil.YELLOW_LIGHTNING)) {
                color = config.lightningColor();
            } else if (config.highlightFallingRocks() && graphicsObject.getId() == GGUtil.FALLING_ROCKS) {
                color = config.fallingRocksColor();
            } else if (config.highlightStoneOrb() && graphicsObject.getId() == GGUtil.STONE_ORB) {
                color = config.stoneOrbColor();
            } else {
                continue;
            }
            if (poly != null) {

                renderPolygon(graphics, config.highlightAOE() ? polyArea : poly, color, config.width());
            }
        }
        return null;
    }

    public void renderPolygon(Graphics2D graphics, Shape poly, Color color, int stroke) {
        Color outlineColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 150);
        graphics.setColor(outlineColor);
        Stroke originalStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(stroke));
        graphics.draw(poly);
        Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), config.opacity());
        graphics.setColor(fillColor);
        graphics.fill(poly);
        graphics.setStroke(originalStroke);
    }

    private void renderNpcOverlay(Graphics2D graphics, NPC actor, Color color, int outlineWidth, int outlineAlpha, int fillAlpha) {
        int size = 1;
        NPCComposition composition = actor.getTransformedComposition();
        if (composition != null)
            size = composition.getSize();
        LocalPoint lp = actor.getLocalLocation();
        Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
        if (tilePoly != null) {
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
            graphics.setStroke(new BasicStroke(outlineWidth));
            graphics.draw(tilePoly);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
            graphics.fill(tilePoly);
        }
    }

    public static void renderTextLocation(Graphics2D graphics, Point canvasPoint, String text, Color fontColor) {
        if (canvasPoint != null) {
            int x = canvasPoint.getX();
            int y = canvasPoint.getY();
            graphics.setColor(Color.BLACK);
            graphics.drawString(text, x + 1, y + 1);
            graphics.setColor(fontColor);
            graphics.drawString(text, x, y);
        }
    }
}
