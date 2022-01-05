package net.runelite.client.plugins.spoonnightmare;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;

public class SpoonNightmareOverlay extends Overlay {
    private final Client client;

    private final SpoonNightmarePlugin plugin;

    private final SpoonNightmareConfig config;

    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private SpoonNightmareOverlay(Client client, SpoonNightmarePlugin plugin, SpoonNightmareConfig config, ModelOutlineRenderer modelOutlineRenderer) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        if (config.nightmareHands() != SpoonNightmareConfig.handsMode.OFF && plugin.handsLocation.size() > 0){
            for (int i=0; i<plugin.handsLocation.size(); i++) {
                if (client.getLocalPlayer() != null) {
                    GraphicsObject obj = plugin.handsLocation.get(i);
                    WorldPoint playerLp = WorldPoint.fromLocal(client, client.getLocalPlayer().getLocalLocation());
                    WorldPoint handLp = WorldPoint.fromLocal(client, obj.getLocation());
                    if (!config.handsDistance() || (playerLp.distanceTo(handLp) <= config.handsDistanceLimit() && config.handsDistance())) {
                        Color color = config.nightmareHandsColor();
                        if (config.raveHands()) {
                            color = plugin.raveHandsColors.get(i);
                        }

                        if (config.nightmareHands() == SpoonNightmareConfig.handsMode.TILE) {
                            Polygon poly = Perspective.getCanvasTilePoly(client, obj.getLocation());
                            if (poly != null) {
                                graphics.setStroke(new BasicStroke(config.handsWidth()));
                                Color outline = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
                                graphics.setColor(outline);
                                graphics.draw(poly);
                                Color colorFill = new Color(color.getRed(), color.getGreen(), color.getBlue(), config.handsOpacity());
                                graphics.setColor(colorFill);
                                graphics.fill(poly);
                            }
                        } else if (config.nightmareHands() == SpoonNightmareConfig.handsMode.OUTLINE) {
                            modelOutlineRenderer.drawOutline(obj, config.handsWidth(), color, config.handsGlow());
                        }

                        if(config.handsTicks()){
                            Color tickColor = Color.WHITE;
                            if(plugin.handsDelay == 1){
                                tickColor = Color.RED;
                            }
                            String text = Integer.toString(plugin.handsDelay);
                            Point point = Perspective.getCanvasTextLocation(client, graphics, obj.getLocation(), text, 0);
                            Font oldFont = graphics.getFont();
                            graphics.setFont(new Font("Arial", Font.BOLD, 12));
                            if(point != null) {
                                Point pointShadow = new Point(point.getX() + 1, point.getY() + 1);
                                OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
                                OverlayUtil.renderTextLocation(graphics, point, text, tickColor);
                            }
                            graphics.setFont(oldFont);
                        }
                    }
                }
            }
        }

        if (config.p3Runway() != SpoonNightmareConfig.runwayMode.OFF && plugin.preparedForTakeoff) {
            WorldPoint leftTop;
            WorldPoint rightBottom;
            WorldPoint middle1;
            WorldPoint middle2;
            WorldPoint middle3;
            int angle = plugin.getNightmareNpc().getOrientation();
            int round = angle >>> 9;
            int up = angle & 0x100;
            if (up != 0)
                round++;
            int directionNum = round & 0x3;
            int extraTiles;
            Color color = Color.WHITE;

            if(config.p3Runway() == SpoonNightmareConfig.runwayMode.COLOR){
                color = config.p3RunwayColor();
            }else if(config.p3Runway() == SpoonNightmareConfig.runwayMode.RAVE){
                color = plugin.raveRunway.get(0);
            }

            int index = 0;
            if (directionNum == 0) {
                extraTiles = 15;
                for (int i = 0; i < extraTiles; i++) {
                    leftTop = new WorldPoint(plugin.bossLoc.getX(), plugin.bossLoc.getY() - 1 - i, client.getPlane());
                    middle1 = new WorldPoint(plugin.bossLoc.getX() + 1, plugin.bossLoc.getY() - 1 - i, client.getPlane());
                    middle2 = new WorldPoint(plugin.bossLoc.getX() + 2, plugin.bossLoc.getY() - 1 - i, client.getPlane());
                    middle3 = new WorldPoint(plugin.bossLoc.getX() + 3, plugin.bossLoc.getY() - 1 - i, client.getPlane());
                    rightBottom = new WorldPoint(plugin.bossLoc.getX() + 4, plugin.bossLoc.getY() - 1 - i, client.getPlane());
                    drawRunwayTiles(graphics, leftTop, middle1, middle2, middle3, rightBottom, index, color);
                    index += 5;
                }
            } else if (directionNum == 1) {
                extraTiles = 14;
                for (int i = 0; i < extraTiles; i++) {
                    leftTop = new WorldPoint(plugin.bossLoc.getX() - 1 - i, plugin.bossLoc.getY() + 4, client.getPlane());
                    middle1 = new WorldPoint(plugin.bossLoc.getX() - 1 - i, plugin.bossLoc.getY() + 3, client.getPlane());
                    middle2 = new WorldPoint(plugin.bossLoc.getX() - 1 - i, plugin.bossLoc.getY() + 2, client.getPlane());
                    middle3 = new WorldPoint(plugin.bossLoc.getX() - 1 - i, plugin.bossLoc.getY() + 1, client.getPlane());
                    rightBottom = new WorldPoint(plugin.bossLoc.getX() - 1 - i, plugin.bossLoc.getY(), client.getPlane());
                    drawRunwayTiles(graphics, leftTop, middle1, middle2, middle3, rightBottom, index, color);
                    index += 5;
                }
            } else if (directionNum == 2) {
                extraTiles = 15;
                for (int i = 0; i < extraTiles; i++) {
                    leftTop = new WorldPoint(plugin.bossLoc.getX(), plugin.bossLoc.getY() + 5 + i, client.getPlane());
                    middle1 = new WorldPoint(plugin.bossLoc.getX() + 1, plugin.bossLoc.getY() + 5 + i, client.getPlane());
                    middle2 = new WorldPoint(plugin.bossLoc.getX() + 2, plugin.bossLoc.getY() + 5 + i, client.getPlane());
                    middle3 = new WorldPoint(plugin.bossLoc.getX() + 3, plugin.bossLoc.getY() + 5 + i, client.getPlane());
                    rightBottom = new WorldPoint(plugin.bossLoc.getX() + 4, plugin.bossLoc.getY() + 5 + i, client.getPlane());
                    drawRunwayTiles(graphics, leftTop, middle1, middle2, middle3, rightBottom, index, color);
                    index += 5;
                }
            } else {
                extraTiles = 14;
                for (int i = 0; i < extraTiles; i++) {
                    leftTop = new WorldPoint(plugin.bossLoc.getX() + 5 + i, plugin.bossLoc.getY() + 4, client.getPlane());
                    middle1 = new WorldPoint(plugin.bossLoc.getX() + 5 + i, plugin.bossLoc.getY() + 3, client.getPlane());
                    middle2 = new WorldPoint(plugin.bossLoc.getX() + 5 + i, plugin.bossLoc.getY() + 2, client.getPlane());
                    middle3 = new WorldPoint(plugin.bossLoc.getX() + 5 + i, plugin.bossLoc.getY() + 1, client.getPlane());
                    rightBottom = new WorldPoint(plugin.bossLoc.getX() + 5 + i, plugin.bossLoc.getY(), client.getPlane());
                    drawRunwayTiles(graphics, leftTop, middle1, middle2, middle3, rightBottom, index, color);
                    index += 5;
                }
            }
        }

        if (plugin.totemsActive && (config.totemHighlight() != SpoonNightmareConfig.totemHighlightMode.OFF || config.totemHP())){
            for (int i=0; i<plugin.totemList.size(); i++) {
                TotemInfo ti = plugin.totemList.get(i);

                if(config.totemHighlight() != SpoonNightmareConfig.totemHighlightMode.OFF){
                    Color totemColor = config.totemHighlightColor();
                    if(config.totemColorMode() == SpoonNightmareConfig.totemColorMode.RAVE){
                        totemColor = plugin.raveTotemColors.get(0);
                    }else if(config.totemColorMode() == SpoonNightmareConfig.totemColorMode.RAVEST){
                        totemColor = plugin.raveTotemColors.get(i);
                    }

                    if (config.totemHighlight() == SpoonNightmareConfig.totemHighlightMode.AREA) {
                        Shape poly = ti.getNpc().getConvexHull();
                        if(poly != null) {
                            Color fillColor = new Color(totemColor.getRed(), totemColor.getGreen(), totemColor.getBlue(), 50);
                            graphics.setColor(fillColor);
                            graphics.fill(poly);
                        }
                    }else {
                        modelOutlineRenderer.drawOutline(ti.getNpc(), config.totemWidth(), totemColor, config.totemGlow());
                    }
                }

                if (config.totemHP()) {
                    LocalPoint lp = ti.getNpc().getLocalLocation();
                    int scale = client.getPlayers().size();
                    int totemHealth;
                    int id = plugin.getNightmareNpc().getId();
                    if((id >= 9416 && id <= 9424) || (id >= 11152 && id <= 11155)){
                        totemHealth = 200;
                    }else {
                        if (scale >= 6) {
                            totemHealth = scale * 30;
                        } else {
                            totemHealth = 300;
                        }
                    }

                    int healthRatio = ti.getNpc().getHealthRatio();
                    double healthRatioDec;
                    if (healthRatio > 0) {
                        healthRatioDec = healthRatio / 100.0D;
                        ti.setRatio(ti.getNpc().getHealthRatio());
                    }else {
                        if(ti.getRatio() > 0) {
                            healthRatioDec = ti.getRatio() / 100.0D;
                        }else {
                            healthRatioDec = 0.0D;
                        }
                    }

                    double currentHealth = totemHealth - totemHealth * healthRatioDec;
                    Color textColor = Color.GREEN;
                    if (currentHealth <= 98.0D) {
                        textColor = Color.RED;
                    } else if (currentHealth < totemHealth) {
                        textColor = Color.ORANGE;
                    }
                    String healthStr = Integer.toString(Double.valueOf(currentHealth).intValue());
                    Point point = Perspective.getCanvasTextLocation(client, graphics, lp, healthStr, 0);
                    Font oldFont = graphics.getFont();
                    graphics.setFont(new Font("Arial", Font.BOLD, config.totemHPSize()));
                    if(point != null) {
                        Point pointShadow = new Point(point.getX() + 1, point.getY() + 1);
                        OverlayUtil.renderTextLocation(graphics, pointShadow, healthStr, Color.BLACK);
                        OverlayUtil.renderTextLocation(graphics, point, healthStr, textColor);
                    }
                    graphics.setFont(oldFont);
                }
            }
        }

        if(plugin.shrooms.size() > 0 && (config.highlightSpores() || config.sporesTickCounter())){
            renderShrooms(graphics, plugin.shrooms);
        }

        if (config.huskHighlight() && plugin.husks.size() > 0) {
            highlightHusks(graphics);
        }

        if (config.huskTarget() && plugin.isActiveFight()){
            for (Projectile p : client.getProjectiles()){
                if(p.getId() == 1781 && p.getInteracting() != null & p.getInteracting() instanceof Player) {
                    Polygon poly = Perspective.getCanvasTilePoly(client, p.getInteracting().getLocalLocation());
                    renderPoly(graphics, config.huskTargetColor(), poly, config.huskWidth(), config.huskOpacity());
                }
            }
        }

        if (plugin.flowersActive && config.lowFps()) {
            for (LocalPoint lp : plugin.getFlowerTiles()) {
                drawTile(graphics, WorldPoint.fromLocal(client, lp), Color.GREEN, 0, 0, 50);
            }
        }
        return null;
    }

    protected void drawTile(Graphics2D graphics, WorldPoint point, Color color, int strokeWidth, int outlineAlpha, int fillAlpha) {
        if(client.getLocalPlayer() != null) {
            WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
            if (point.distanceTo(playerLocation) < 32) {
                LocalPoint lp = LocalPoint.fromWorld(client, point);
                if (lp != null) {
                    Polygon poly = Perspective.getCanvasTilePoly(client, lp);
                    if (poly != null) {
                        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
                        graphics.setStroke(new BasicStroke(strokeWidth));
                        graphics.draw(poly);
                        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
                        graphics.fill(poly);
                    }
                }
            }
        }
    }

    private void renderShrooms(Graphics2D graphics, ArrayList<GameObject> shrooms) {
        for (GameObject obj : shrooms) {
            if(config.highlightSpores()) {
                Polygon poly = Perspective.getCanvasTileAreaPoly(client, obj.getLocalLocation(), 3);
                if(poly != null) {
                    graphics.setStroke(new BasicStroke(config.sporeWidth()));
                    graphics.setColor(config.sporeBorderColor());
                    graphics.draw(poly);
                    Color colorFill = new Color(config.sporeBorderColor().getRed(), config.sporeBorderColor().getGreen(), config.sporeBorderColor().getBlue(), config.sporeOpacity());
                    graphics.setColor(colorFill);
                    graphics.fill(poly);
                }
            }

            if(config.sporesTickCounter()){
                String ticks = Integer.toString(plugin.mushroomTicks);
                Point point = Perspective.getCanvasTextLocation(client, graphics, obj.getLocalLocation(), ticks, 0);
                Font oldFont = graphics.getFont();
                graphics.setFont(new Font("Arial", Font.BOLD, 12));
                Point pointShadow = new Point(point.getX() + 1, point.getY() + 1);
                OverlayUtil.renderTextLocation(graphics, pointShadow, ticks, Color.BLACK);
                OverlayUtil.renderTextLocation(graphics, point, ticks, config.sporesTickColor());
                graphics.setFont(oldFont);
            }
        }
    }

    private void highlightHusks(Graphics2D graphics) {
        for (NPC npc : plugin.husks) {
            int id = npc.getId();
            Color color;
            if (id == 9455 || id == 9467) {
                color = Color.GREEN;
            } else {
                color = Color.BLUE;
            }
            Polygon poly = Perspective.getCanvasTilePoly(client, npc.getLocalLocation());
            renderPoly(graphics, color, poly, config.huskWidth(), config.huskOpacity());
        }
    }

    private static void renderPoly(Graphics2D graphics, Color color, Shape polygon, int stroke, int opacity) {
        if (polygon != null) {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(stroke));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity));
            graphics.fill(polygon);
        }
    }

    private void drawRunwayTiles(Graphics2D graphics, WorldPoint leftTop, WorldPoint middle1, WorldPoint middle2, WorldPoint middle3, WorldPoint rightBottom, int index, Color color) {
        if(config.p3Runway() == SpoonNightmareConfig.runwayMode.RAVEST){
            color = plugin.raveRunway.get(index);
            drawTile(graphics, leftTop, color, 0, 0, 100);
            index++;
            color = plugin.raveRunway.get(index);
            drawTile(graphics, middle1, color, 0, 0, 100);
            index++;
            color = plugin.raveRunway.get(index);
            drawTile(graphics, middle2, color, 0, 0, 100);
            index++;
            color = plugin.raveRunway.get(index);
            drawTile(graphics, middle3, color, 0, 0, 100);
            index++;
            color = plugin.raveRunway.get(index);
            drawTile(graphics, rightBottom, color, 0, 0, 100);
        }else {
            drawTile(graphics, leftTop, color, 0, 0, 100);
            drawTile(graphics, middle1, color, 0, 0, 100);
            drawTile(graphics, middle2, color, 0, 0, 100);
            drawTile(graphics, middle3, color, 0, 0, 100);
            drawTile(graphics, rightBottom, color, 0, 0, 100);
        }
    }
}
