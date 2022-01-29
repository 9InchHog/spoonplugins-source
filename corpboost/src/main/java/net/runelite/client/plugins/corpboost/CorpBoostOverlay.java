package net.runelite.client.plugins.corpboost;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.kit.KitType;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.runelite.api.ItemID.*;

public class CorpBoostOverlay extends Overlay {
    private static final int MAX_DISTANCE = 2350;

    private final Client client;
    private final CorpBoostPlugin plugin;
    private final CorpBoostConfig config;
    private ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private ItemManager itemManager;

    @Inject
    private SpriteManager spriteManager;

    BufferedImage heal = ImageUtil.loadImageResource(CorpBoostOverlay.class, "Heal_Other.png");

    ArrayList<Integer> unchargedSerps = new ArrayList<>(Arrays.asList(SERPENTINE_HELM_UNCHARGED, TANZANITE_HELM_UNCHARGED, MAGMA_HELM_UNCHARGED));

    @Inject
    CorpBoostOverlay(Client client, CorpBoostPlugin plugin, CorpBoostConfig config, ModelOutlineRenderer modelOutlineRenderer) {
        setPosition(OverlayPosition.DYNAMIC);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        List<WorldPoint> stunSpearPoints = plugin.getStunSpearPoints();
        List<WorldPoint> xferSpearPoints = plugin.getXferSpearPoints();
        List<WorldPoint> stunHealerPoints = plugin.getStunHealerPoints();
        List<WorldPoint> xferHealerPoints = plugin.getXferHealerPoints();
        List<WorldPoint> stunDwhPoints = plugin.getStunDwhPoints();
        List<WorldPoint> xferDwhPoints = plugin.getXferDwhPoints();
        List<WorldPoint> customerPoints = plugin.getCustomerPoints();
        Player player = client.getLocalPlayer();
        LocalPoint localLocation = null;

        if(player != null){
            localLocation = LocalPoint.fromWorld(client, player.getWorldLocation());
        }

        if(localLocation != null) {
            if (config.spearAlt()) {
                if ((config.boosterRole() == CorpBoostConfig.boosterRoleMode.STUNNER || config.boosterRole() == CorpBoostConfig.boosterRoleMode.BOTH)
                        && stunSpearPoints.size() > 0) {
                    for (WorldPoint spot : stunSpearPoints) {
                        if (spot.getPlane() != client.getPlane()) {
                            continue;
                        }

                        LocalPoint spearAltPoint = LocalPoint.fromWorld(client, spot);

                        if (spearAltPoint != null && localLocation.distanceTo(spearAltPoint) <= MAX_DISTANCE && spearAltPoint.distanceTo(localLocation) > 0) {
                            renderSpot(graphics, client, spearAltPoint, itemManager.getImage(ZAMORAKIAN_SPEAR), config.stunTileColor());
                        }
                    }
                }

                if ((config.boosterRole() == CorpBoostConfig.boosterRoleMode.XFER || config.boosterRole() == CorpBoostConfig.boosterRoleMode.BOTH)
                        && xferSpearPoints.size() > 0) {
                    for (WorldPoint spot : xferSpearPoints) {
                        if (spot.getPlane() != client.getPlane()) {
                            continue;
                        }

                        LocalPoint spearAltPoint = LocalPoint.fromWorld(client, spot);

                        if (spearAltPoint != null && localLocation.distanceTo(spearAltPoint) <= MAX_DISTANCE && spearAltPoint.distanceTo(localLocation) > 0) {
                            renderSpot(graphics, client, spearAltPoint, itemManager.getImage(ZAMORAKIAN_SPEAR), config.xferTileColor());
                        }
                    }
                }
            }

            if (config.healer()) {
                if ((config.boosterRole() == CorpBoostConfig.boosterRoleMode.STUNNER || config.boosterRole() == CorpBoostConfig.boosterRoleMode.BOTH)
                        && stunHealerPoints.size() > 0) {
                    for (WorldPoint spot : stunHealerPoints) {
                        if (spot.getPlane() != client.getPlane()) {
                            continue;
                        }

                        LocalPoint stunHealerPoint = LocalPoint.fromWorld(client, spot);

                        if (stunHealerPoint != null && localLocation.distanceTo(stunHealerPoint) <= MAX_DISTANCE && stunHealerPoint.distanceTo(localLocation) > 0) {
                            renderSpot(graphics, client, stunHealerPoint, itemManager.getImage(TANZANITE_HELM), config.stunTileColor());
                        }
                    }
                }

                if ((config.boosterRole() == CorpBoostConfig.boosterRoleMode.XFER || config.boosterRole() == CorpBoostConfig.boosterRoleMode.BOTH)
                        && xferHealerPoints.size() > 0) {
                    for (WorldPoint spot : xferHealerPoints) {
                        if (spot.getPlane() != client.getPlane()) {
                            continue;
                        }

                        LocalPoint xferHealerPoint = LocalPoint.fromWorld(client, spot);

                        if (xferHealerPoint != null && localLocation.distanceTo(xferHealerPoint) <= MAX_DISTANCE && xferHealerPoint.distanceTo(localLocation) > 0) {
                            renderSpot(graphics, client, xferHealerPoint, heal, config.xferTileColor());
                        }
                    }
                }
            }

            if (config.dwh()) {
                if ((config.boosterRole() == CorpBoostConfig.boosterRoleMode.STUNNER || config.boosterRole() == CorpBoostConfig.boosterRoleMode.BOTH)
                        && stunDwhPoints.size() > 0) {
                    for (WorldPoint spot : stunDwhPoints) {
                        if (spot.getPlane() != client.getPlane()) {
                            continue;
                        }

                        LocalPoint stunDwhPoint = LocalPoint.fromWorld(client, spot);

                        if (stunDwhPoint != null && localLocation.distanceTo(stunDwhPoint) <= MAX_DISTANCE && stunDwhPoint.distanceTo(localLocation) > 0) {
                            renderSpot(graphics, client, stunDwhPoint, itemManager.getImage(DRAGON_WARHAMMER), config.stunTileColor());
                        }
                    }
                }

                if ((config.boosterRole() == CorpBoostConfig.boosterRoleMode.XFER || config.boosterRole() == CorpBoostConfig.boosterRoleMode.BOTH)
                        && xferDwhPoints.size() > 0) {
                    for (WorldPoint spot : xferDwhPoints) {
                        if (spot.getPlane() != client.getPlane()) {
                            continue;
                        }

                        LocalPoint xferDwhPoint = LocalPoint.fromWorld(client, spot);

                        if (xferDwhPoint != null && localLocation.distanceTo(xferDwhPoint) <= MAX_DISTANCE && xferDwhPoint.distanceTo(localLocation) > 0) {
                            renderSpot(graphics, client, xferDwhPoint, itemManager.getImage(DRAGON_WARHAMMER), config.xferTileColor());
                        }
                    }
                }
            }

            if (config.customer() && customerPoints.size() > 0) {
                for (WorldPoint spot : customerPoints) {
                    if (spot.getPlane() != client.getPlane()) {
                        continue;
                    }

                    LocalPoint customerPoint = LocalPoint.fromWorld(client, spot);

                    if (customerPoint != null && localLocation.distanceTo(customerPoint) <= MAX_DISTANCE && customerPoint.distanceTo(localLocation) > 0) {
                        renderSpot(graphics, client, customerPoint, itemManager.getImage(COIN_POUCH), config.customerTileColor());
                    }
                }
            }
        }

        if(config.coreHighlight() != CorpBoostConfig.CoreHighlightMode.OFF && plugin.core != null) {
            if (config.coreHighlight() == CorpBoostConfig.CoreHighlightMode.AREA) {
                renderAreaOverlay(graphics, plugin.core, config.coreHighlightColor());
            }

            if (this.config.coreHighlight() == CorpBoostConfig.CoreHighlightMode.TILE) {
                NPCComposition npcComp = plugin.core.getComposition();
                int size = npcComp.getSize();
                LocalPoint lp = plugin.core.getLocalLocation();
                Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
                renderPoly(graphics, config.coreHighlightColor(), tilePoly);
            }

            if (this.config.coreHighlight() == CorpBoostConfig.CoreHighlightMode.HULL) {
                Shape objectClickbox = plugin.core.getConvexHull();
                if (objectClickbox != null) {
                    graphics.setStroke(new BasicStroke(config.coreHighlightWidth()));
                    graphics.setColor(config.coreHighlightColor());
                    graphics.draw(objectClickbox);
                }
            }

            if (this.config.coreHighlight() == CorpBoostConfig.CoreHighlightMode.TRUE_LOCATION) {
                int size = 1;
                NPCComposition composition = plugin.core.getTransformedComposition();
                if (composition != null)
                    size = composition.getSize();
                LocalPoint lp = LocalPoint.fromWorld(this.client, plugin.core.getWorldLocation());
                if (lp != null) {
                    lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
                    Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
                    renderPoly(graphics, config.coreHighlightColor(), tilePoly);
                }
            }

            if (this.config.coreHighlight() == CorpBoostConfig.CoreHighlightMode.OUTLINE) {
                modelOutlineRenderer.drawOutline(plugin.core, config.coreHighlightWidth(), config.coreHighlightColor(), config.coreHighlightGlow());
            }
        }

        if (config.unchargedSerp() && client.getLocalPlayer() != null && client.getLocalPlayer().getWorldLocation().getRegionID() == 11844) {
            for (Player p : client.getPlayers()) {
                if (p.getPlayerComposition() != null && unchargedSerps.contains(p.getPlayerComposition().getEquipmentId(KitType.HEAD))) {
                    Polygon tilePoly = Perspective.getCanvasTilePoly(client, p.getLocalLocation(), 0);
                    if(tilePoly != null)
                        renderTile(graphics, tilePoly, config.serpColor(), config.serpWidth(), 0, 255);
                }
            }
        }
        return null;
    }

    private void renderSpot(Graphics2D graphics, Client client, LocalPoint point, BufferedImage image, Color color) {
        //Render tile
        Polygon poly = Perspective.getCanvasTilePoly(client, point);

        if (poly != null) {
            renderTile(graphics, poly, color, config.tileWidth(), config.tileFillOpacity(), color.getAlpha());
        }

        //Render icon
        Point imageLoc = Perspective.getCanvasImageLocation(client, point, image, 0);

        if (imageLoc != null) {
            OverlayUtil.renderImageLocation(graphics, imageLoc, image);
        }
    }

    private void renderAreaOverlay(Graphics2D graphics, NPC actor, Color color) {
        Shape objectClickbox = actor.getConvexHull();
        if (objectClickbox != null) {
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
            graphics.fill(actor.getConvexHull());
        }
    }

    private void renderPoly(Graphics2D graphics, Color color, Shape polygon) {
        if (polygon != null) {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(config.coreHighlightWidth()));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), config.coreHighlightOpacity()));
            graphics.fill(polygon);
        }
    }

    private void renderTile(Graphics2D graphics, Shape polygon, Color color, final double borderWidth, int opacity, int outlineAlpha) {
        if (polygon == null)
            return;
        if (borderWidth == 0) {
            outlineAlpha = 0;
        }
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
        graphics.setStroke(new BasicStroke((float) borderWidth));
        graphics.draw(polygon);
        graphics.setColor(new Color(0, 0, 0, opacity));
        graphics.fill(polygon);
    }
}
