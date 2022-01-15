package net.runelite.client.plugins.nex;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class NexOverlay extends Overlay {
    private final Client client;

    private final NexPlugin plugin;

    private final NexConfig config;

    @Inject
    public NexOverlay(Client client, NexPlugin plugin, NexConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }

    public Dimension render(Graphics2D graphics) {
        Font fontCache = graphics.getFont();
        graphics.setFont(new Font("Arial", 1, config.getFontSize()));
        if (config.highlightShadows())
            plugin.getShadowObjects()
                    .forEach(obj -> drawTile(graphics, obj.getWorldLocation(), Color.CYAN, 2, 255, 64));
        if (config.showIceShardTimer() && plugin.getIceCageTimer() > 0)
            plugin.getIceObjects().forEach(obj -> {
                Color color = healthColorCode((int)(100.0D * plugin.getIceCageTimer() / 8.0D));
                LocalPoint lp = obj.getLocalLocation();
                if (lp != null) {
                    Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 3);
                    if (poly != null)
                        drawPoly(graphics, poly, color, 2, 255, 64);
                    String txt = Integer.toString(plugin.getIceCageTimer());
                    Point loc = Perspective.getCanvasTextLocation(client, graphics, lp, txt, 0);
                    if (loc != null)
                        OverlayUtil.renderTextLocation(graphics, loc, txt, Color.WHITE);
                }
            });
        if (config.showBloodSacrificeRange() && plugin.getBloodSacrificeTimer() > 0 && plugin.getBloodSacrificeLocation() != null) {
            LocalPoint lp = LocalPoint.fromWorld(client, plugin.getBloodSacrificeLocation());
            Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 13);
            if (poly != null) {
                Color color = healthColorCode((int)(100.0D * plugin.getBloodSacrificeTimer() / 10.0D) - 20);
                drawPoly(graphics, poly, color, 2, 255, 64);
            }
        }
        NPC nex = plugin.findNpc(NexConstant.NEX_IDS);
        if (nex != null) {
            LocalPoint lp = nex.getLocalLocation();
            if (lp != null) {
                if (config.showInvulnerability() && plugin.getNexInvulnerability() > 0) {
                    String txt = Integer.toString(plugin.getNexInvulnerability());
                    Point loc = Perspective.getCanvasTextLocation(client, graphics, lp, txt, nex.getLogicalHeight() / 2);
                    if (loc != null)
                        OverlayUtil.renderTextLocation(graphics, loc, txt, Color.WHITE);
                }
                if (config.showIceShardRange() && plugin.getIceShardTimer() > 0) {
                    Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 5);
                    if (poly != null) {
                        Color color = healthColorCode((int)(100.0D * plugin.getIceShardTimer() / 6.0D));
                        drawPoly(graphics, poly, color, 2, 255, 64);
                    }
                }
                if (config.showWrathRange() && plugin.getCurrentPhase() == NexPhase.ZAROS && nex.getId() == 11282) {
                    Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 7);
                    if (poly != null) {
                        Color color = healthColorCode((int)(100.0D * nex.getHealthRatio() / 30.0D));
                        drawPoly(graphics, poly, color, 2, 255, 64);
                    }
                }
            }
            if (config.showNexTarget() != NexConfig.NexTargetIndicator.DISABLED) {
                Actor target = nex.getInteracting();
                if (target != null)
                    if (!config.showTargetLocalPlayerOnly() || target == client.getLocalPlayer())
                        drawInteracting(graphics, (Actor)nex, target, config.showNexTarget());
            }
        }
        if (config.showPlayersWithVirus()) {
            NexConfig.VirusIndicator vi = config.getVirusIndicator();
            client.getPlayers().stream().filter(p -> plugin.getSickPlayers().containsKey(p.getName())).forEach(p -> {
                if (vi.isHullVisible()) {
                    Shape poly = p.getConvexHull();
                    if (poly != null) {
                        Color color = new Color(255, 153, 153);
                        drawPoly(graphics, poly, color, 2, 255, 64);
                    }
                }
                if (vi.isTileVisible()) {
                    LocalPoint lp = p.getLocalLocation();
                    if (lp != null) {
                        Shape poly = Perspective.getCanvasTileAreaPoly(client, lp, 3);
                        if (poly != null) {
                            Color color = new Color(255, 153, 153);
                            drawPoly(graphics, poly, color, 2, 255, 64);
                        }
                    }
                }
            });
        }
        if (config.showBloodSacrificeTimer() && plugin.getBloodSacrificeTimer() > 0) {
            Player p = client.getLocalPlayer();
            LocalPoint lp = p.getLocalLocation();
            if (lp != null) {
                String txt = Integer.toString(plugin.getBloodSacrificeTimer());
                Point loc = Perspective.getCanvasTextLocation(client, graphics, lp, txt, p.getLogicalHeight() / 2);
                if (loc != null)
                    OverlayUtil.renderTextLocation(graphics, loc, txt, Color.RED.brighter());
            }
        }
        graphics.setFont(fontCache);
        return null;
    }

    private Color healthColorCode(int health) {
        health = Math.max(health, 0);
        health = Math.min(health, 100);
        double rMod = 130.0D * health / 100.0D;
        double gMod = 255.0D * health / 100.0D;
        double bMod = 125.0D * health / 100.0D;
        Color c = new Color((int)(255.0D - rMod), (int)(0.0D + gMod), (int)(0.0D + bMod));
        return c;
    }

    private void drawPoly(Graphics2D graphics, Shape poly, Color color, int strokeWidth, int outlineAlpha, int fillAlpha) {
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
        graphics.setStroke(new BasicStroke(strokeWidth));
        graphics.draw(poly);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
        graphics.fill(poly);
    }

    private void drawTile(Graphics2D graphics, WorldPoint point, Color color, int strokeWidth, int outlineAlpha, int fillAlpha) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        if (point.distanceTo(playerLocation) >= 32)
            return;
        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null)
            return;
        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null)
            return;
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
        graphics.setStroke(new BasicStroke(strokeWidth));
        graphics.draw(poly);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
        graphics.fill(poly);
    }

    private void drawInteracting(Graphics2D graphics, Actor source, Actor target, NexConfig.NexTargetIndicator indicator) {
        if (indicator.isLineVisible()) {
            LocalPoint sourcePosition = source.getLocalLocation();
            LocalPoint targetPosition = target.getLocalLocation();
            if (sourcePosition != null && targetPosition != null) {
                Point sourcePoint = Perspective.localToCanvas(client, sourcePosition, client.getPlane(), 0);
                Point targetPoint = Perspective.localToCanvas(client, targetPosition, client.getPlane(), 0);
                if (sourcePoint != null && targetPoint != null) {
                    graphics.setColor(Color.CYAN);
                    graphics.setStroke(new BasicStroke(1.0F));
                    graphics.drawLine(sourcePoint.getX(), sourcePoint.getY(), targetPoint.getX(), targetPoint.getY());
                }
            }
        }
        if (indicator.isHullVisible()) {
            Shape targetShape = target.getConvexHull();
            if (targetShape != null)
                drawPoly(graphics, targetShape, Color.CYAN, 2, 255, 30);
        }
    }
}
