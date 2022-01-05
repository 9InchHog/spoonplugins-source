package net.runelite.client.plugins.spoonezswaps;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.spoonezswaps.config.MinionHighlightMode;
import net.runelite.client.plugins.spoonezswaps.config.MinionSelector;
import net.runelite.client.plugins.spoonezswaps.util.MinionData;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MinionOverlay extends Overlay {
    private static final Set<Integer> GAP = ImmutableSet.of(34, 33, 26, 25, 18, 17, 10, 9, 2, 1);
    private final Client client;
    private final SpoonEzSwapsPlugin plugin;
    private final SpoonEzSwapsConfig config;
    private final ModelOutlineRenderer modelOutlineRenderer;

    private static final Color TEXT_COLOR = Color.WHITE;
    private static final NumberFormat TIME_LEFT_FORMATTER = DecimalFormat.getInstance(Locale.US);

    static {
        ((DecimalFormat)TIME_LEFT_FORMATTER).applyPattern("#0.0");
    }

    @Inject
    private MinionOverlay(final Client client, final SpoonEzSwapsPlugin plugin, final SpoonEzSwapsConfig config, ModelOutlineRenderer modelOutlineRenderer) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if(config.highlightMinions() != MinionHighlightMode.OFF) {
            for (NPC npc : this.client.getNpcs()) {
                if (!npc.isDead()) {
                    if ((plugin.gwdMinonListMelee.contains(npc.getId()) && (config.minionSelect() == MinionSelector.MELEE || config.minionSelect() == MinionSelector.ALL)) ||
                            (plugin.gwdMinonListMage.contains(npc.getId()) && (config.minionSelect() == MinionSelector.MAGE || config.minionSelect() == MinionSelector.ALL)) ||
                            (plugin.gwdMinonListRange.contains(npc.getId()) && (config.minionSelect() == MinionSelector.RANGE || config.minionSelect() == MinionSelector.ALL))) {
                        Color color = Color.WHITE;
                        if (plugin.gwdMinonListMelee.contains(npc.getId())) {
                            color = Color.RED;
                        } else if (plugin.gwdMinonListMage.contains(npc.getId())) {
                            color = Color.BLUE;
                        } else {
                            color = Color.GREEN;
                        }
                        if (config.highlightMinions() == MinionHighlightMode.AREA) {
                            renderAreaOverlay(graphics, npc, color);
                        } else if (this.config.highlightMinions() == MinionHighlightMode.TILE) {
                            NPCComposition npcComp = npc.getComposition();
                            int size = npcComp.getSize();
                            LocalPoint lp = npc.getLocalLocation();
                            Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
                            renderGwdPoly(graphics, color, tilePoly);
                        } else if (this.config.highlightMinions() == MinionHighlightMode.HULL) {
                            Shape objectClickbox = npc.getConvexHull();
                            if (objectClickbox != null) {
                                graphics.setStroke(new BasicStroke(this.config.gwdThicc()));
                                graphics.setColor(color);
                                graphics.draw(objectClickbox);
                            }
                        } else if (this.config.highlightMinions() == MinionHighlightMode.TL) {
                            int size = 1;
                            NPCComposition composition = npc.getTransformedComposition();
                            if (composition != null)
                                size = composition.getSize();
                            LocalPoint lp = LocalPoint.fromWorld(this.client, npc.getWorldLocation());
                            if (lp != null) {
                                lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
                                Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
                                renderGwdPoly(graphics, color, tilePoly);
                            }
                        } else if (this.config.highlightMinions() == MinionHighlightMode.OUTLINE) {
                            this.modelOutlineRenderer.drawOutline(npc, this.config.gwdThicc(), color, 2);
                        }
                    }
                }
            }
        }

        if (this.config.minionRespawn()){
            if (this.plugin.isInGodWars()) {
                List<MinionData> pendingRemove = new ArrayList<>();
                for (MinionData minion : this.plugin.getTrackedMinions()) {
                    if (minion.tickDied != -1 && this.plugin.getBoss() != null) {
                        if (!renderNpcRespawn(minion, graphics))
                            pendingRemove.add(minion);
                    }
                }

                for (MinionData minion : pendingRemove) {
                    this.plugin.getTrackedMinions().remove(minion);
                }
            }
        }
        return null;
    }

    private void renderAreaOverlay(Graphics2D graphics, NPC actor, Color color) {
        Shape objectClickbox = actor.getConvexHull();
        if (objectClickbox != null) {
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), this.config.gwdOpacity()));
            graphics.fill(actor.getConvexHull());
        }
    }

    private void renderGwdPoly(Graphics2D graphics, Color color, Shape polygon) {
        if (polygon != null)
        {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(this.config.gwdThicc()));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), this.config.gwdOpacity()));
            graphics.fill(polygon);
        }
    }

    private boolean renderNpcRespawn(MinionData npc, Graphics2D graphics) {
        Color color = npc.getColor();

        WorldPoint respawnLocation = npc.respawnLocation;

        LocalPoint lp = LocalPoint.fromWorld(this.client, respawnLocation.getX(), respawnLocation.getY());

        if (lp == null) {
            return false;
        }

        LocalPoint centerLp = new LocalPoint(lp.getX() + 128 * (npc.size - 1) / 2, lp.getY() + 128 * (npc.size - 1) / 2);

        Polygon poly = Perspective.getCanvasTileAreaPoly(this.client, centerLp, npc.size);

        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, color);
        }

        Instant now = Instant.now();
        double baseTick = (npc.tickDied + npc.respawnTicks - this.client.getTickCount()) * 0.6D;
        double sinceLast = (now.toEpochMilli() - this.plugin.getLastTickUpdate().toEpochMilli()) / 1000.0D;
        double timeLeft = Math.max(0.0D, baseTick - sinceLast);
        String timeLeftStr = TIME_LEFT_FORMATTER.format(timeLeft);

        int textWidth = graphics.getFontMetrics().stringWidth(timeLeftStr);
        int textHeight = graphics.getFontMetrics().getAscent();

        net.runelite.api.Point canvasPoint = Perspective.localToCanvas(this.client, centerLp, respawnLocation.getPlane());

        if (canvasPoint != null) {
            net.runelite.api.Point canvasCenterPoint = new Point(canvasPoint.getX() - textWidth / 2, canvasPoint.getY() + textHeight / 2);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, timeLeftStr, TEXT_COLOR);
        }

        return timeLeft != 0.0D;
    }

    private void renderPoly(Graphics2D graphics, Color color, Shape polygon, int alpha, boolean fill) {
        if (polygon != null) {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2.0F));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            if (fill)
                graphics.fill(polygon);
        }
    }
}
