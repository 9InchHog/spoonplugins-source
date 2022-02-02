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

    void RenderTile(Client client, WorldPoint point, int size, Color color, Graphics2D graphics) {
        LocalPoint nexLoc;
        Polygon nexPoly;
        if ((nexLoc = LocalPoint.fromWorld(client, point)) != null && (nexPoly = Perspective.getCanvasTileAreaPoly(
                client, nexLoc = new LocalPoint(nexLoc.getX() - 128, nexLoc.getY() - 128), size)) != null)
            drawPoly(graphics, nexPoly, color, 2, 255, 0);
    }

    public Dimension render(Graphics2D graphics) {
        if (plugin.nexBoss != null) {
            if (config.showTenTile())
                RenderTile(client, plugin.nexBoss.getWorldLocation().dx(2).dy(2), 23, Color.black, graphics);
            if (config.showTrueTile())
                RenderTile(client, plugin.nexBoss.getWorldLocation().dx(2).dy(2), 3, Color.red, graphics);
        }
        Font fontCache = graphics.getFont();
        graphics.setFont(new Font("Arial", Font.BOLD, config.getFontSize()));
        if (config.highlightShadows())
            plugin.getShadowObjects().forEach(obj -> drawTile(graphics, obj.getWorldLocation(), Color.CYAN, 2, 255, 32));
        if (config.showIceShardTimer() && plugin.getIceCageTimer() > 0)
            plugin.getIceObjects().forEach(obj -> {
                Color color = healthColorCode((int)(100.0D * plugin.getIceCageTimer() / 8.0D));
                LocalPoint lp3 = obj.getLocalLocation();
                if (lp3 != null) {
                    Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 3);
                    if (poly3 != null)
                        drawPoly(graphics, poly3, color, 2, 255, 32);
                    String txt3;
                    Point loc3;
                    if ((loc3 = Perspective.getCanvasTextLocation(client, graphics, lp3, txt3 = Integer.toString(plugin.getIceCageTimer()), 0)) != null)
                        OverlayUtil.renderTextLocation(graphics, loc3, txt3, Color.WHITE);
                }
            });
        LocalPoint lp2;
        Polygon poly;
        if (config.showBloodSacrificeRange() && plugin.getBloodSacrificeTimer() > 0 && plugin.getBloodSacrificeLocation() != null && (poly = Perspective.getCanvasTileAreaPoly(client, lp2 = LocalPoint.fromWorld(client, plugin.getBloodSacrificeLocation()), 13)) != null) {
            Color color = healthColorCode((int)(100.0D * plugin.getBloodSacrificeTimer() / 10.0D) - 20);
            drawPoly(graphics, poly, color, 2, 255, 32);
        }
        NPC nex;
        if ((nex = plugin.findNpc(NexConstant.NEX_IDS)) != null) {
            LocalPoint lp3 = nex.getLocalLocation();
            if (lp3 != null) {
                String txt2;
                Point loc2;
                if (config.showInvulnerability() && plugin.getNexInvulnerability() > 0 && (loc2 = Perspective.getCanvasTextLocation(
                        client, graphics, lp3, txt2 = Integer.toString(plugin.getNexInvulnerability()), nex.getLogicalHeight() / 2)) != null)
                    OverlayUtil.renderTextLocation(graphics, loc2, txt2, Color.WHITE);
                Polygon poly2;
                if (config.showIceShardRange() && plugin.getIceShardTimer() > 0 && (poly2 = Perspective.getCanvasTileAreaPoly(client, lp3, 5)) != null) {
                    Color color = healthColorCode((int)(100.0D * plugin.getIceShardTimer() / 6.0D));
                    drawPoly(graphics, poly2, color, 2, 255, 64);
                }
                if (config.showWrathRange() && plugin.getCurrentPhase() == NexPhase.ZAROS && nex.getId() == 11282
                        && (poly2 = Perspective.getCanvasTileAreaPoly(client, lp3, 7)) != null) {
                    Color color = healthColorCode((int)(100.0D * nex.getHealthRatio() / 30.0D));
                    drawPoly(graphics, poly2, color, 2, 255, 64);
                }
            }
            Actor target;
            if (config.showNexTarget() != NexConfig.NexTargetIndicator.DISABLED && (target = nex.getInteracting()) != null
                    && (!config.showTargetLocalPlayerOnly() || target == client.getLocalPlayer()))
                drawInteracting(graphics, nex, target, config.showNexTarget());
        }
        if (config.showPlayersWithVirus()) {
            NexConfig.VirusIndicator vi = config.getVirusIndicator();
            client.getPlayers().stream().filter(p -> plugin.getSickPlayers().containsKey(p.getName())).forEach(p -> {
                Shape poly2;
                if (vi.isHullVisible() && (poly2 = p.getConvexHull()) != null) {
                    Color color = new Color(255, 153, 153);
                    drawPoly(graphics, poly2, color, 2, 255, 64);
                }
                Polygon poly4;
                LocalPoint lp4;
                if (vi.isTileVisible() && (lp4 = p.getLocalLocation()) != null && (poly4 = Perspective.getCanvasTileAreaPoly(client, lp4, 3)) != null) {
                    Color color = new Color(255, 153, 153);
                    drawPoly(graphics, poly4, color, 2, 255, 32);
                }
            });
        }
        String txt;
        Point loc;
        Player p2;
        LocalPoint lp;
        if (config.showBloodSacrificeTimer() && plugin.getBloodSacrificeTimer() > 0 && (lp = (p2 = client.getLocalPlayer()).getLocalLocation()) != null
                && (loc = Perspective.getCanvasTextLocation(client, graphics, lp, txt = Integer.toString(
                        plugin.getBloodSacrificeTimer()), p2.getLogicalHeight() / 2)) != null)
            OverlayUtil.renderTextLocation(graphics, loc, txt, Color.RED.brighter());
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
