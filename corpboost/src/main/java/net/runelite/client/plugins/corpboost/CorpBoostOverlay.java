package net.runelite.client.plugins.corpboost;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
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
        List<WorldPoint> spearAltPoints = plugin.getSpearAltPoints();
        List<WorldPoint> spearHealerPoints = plugin.getSpearHealerPoints();
        List<WorldPoint> tbowHealerPoints = plugin.getTbowHealerPoints();
        List<WorldPoint> dwhAltPoints = plugin.getDwhAltPoints();
        List<WorldPoint> dwhAltPoints2 = plugin.getDwhAltPoints2();
        List<WorldPoint> stunnerPoints = plugin.getStunnerPoints();
        List<WorldPoint> customerPoints = plugin.getCustomerPoints();
        Player player = this.client.getLocalPlayer();
        LocalPoint localLocation = null;

        if(player != null){
            localLocation = LocalPoint.fromWorld(client, player.getWorldLocation());
        }

        if(localLocation != null) {
            if (config.spearAlt() && spearAltPoints.size() > 0) {
                for (WorldPoint spot : spearAltPoints) {
                    if (spot.getPlane() != client.getPlane()) {
                        continue;
                    }

                    LocalPoint spearAltPoint = LocalPoint.fromWorld(client, spot);

                    if (spearAltPoint != null && localLocation.distanceTo(spearAltPoint) <= MAX_DISTANCE && spearAltPoint.distanceTo(localLocation) > 0) {
                        renderSpot(graphics, client, spearAltPoint, itemManager.getImage(ZAMORAKIAN_SPEAR), Color.BLACK);
                    }
                }
            }

            if (config.spearHealer() && spearHealerPoints.size() > 0) {
                for (WorldPoint spot : spearHealerPoints) {
                    if (spot.getPlane() != client.getPlane()) {
                        continue;
                    }

                    LocalPoint spearHealerPoint = LocalPoint.fromWorld(client, spot);

                    if (spearHealerPoint != null && localLocation.distanceTo(spearHealerPoint) <= MAX_DISTANCE && spearHealerPoint.distanceTo(localLocation) > 0) {
                        renderSpot(graphics, client, spearHealerPoint, heal, Color.GREEN);
                    }
                }
            }

            if (config.bowHealer() && tbowHealerPoints.size() > 0) {
                for (WorldPoint spot : tbowHealerPoints) {
                    if (spot.getPlane() != client.getPlane()) {
                        continue;
                    }

                    LocalPoint tbowHealerPoint = LocalPoint.fromWorld(client, spot);

                    if (tbowHealerPoint != null && localLocation.distanceTo(tbowHealerPoint) <= MAX_DISTANCE && tbowHealerPoint.distanceTo(localLocation) > 0) {
                        renderSpot(graphics, client, tbowHealerPoint, itemManager.getImage(TWISTED_BOW), Color.BLACK);
                    }
                }
            }

            if (this.config.dwh() && dwhAltPoints.size() > 0) {
                for (WorldPoint spot : dwhAltPoints) {
                    if (spot.getPlane() != client.getPlane()) {
                        continue;
                    }

                    LocalPoint dwhAltPoint = LocalPoint.fromWorld(client, spot);

                    if (dwhAltPoint != null && localLocation.distanceTo(dwhAltPoint) <= MAX_DISTANCE && dwhAltPoint.distanceTo(localLocation) > 0) {
                        renderSpot(graphics, client, dwhAltPoint, itemManager.getImage(DRAGON_WARHAMMER), Color.BLACK);
                    }
                }
            }

            if (this.config.dwh2() && dwhAltPoints2.size() > 0) {
                for (WorldPoint spot : dwhAltPoints2) {
                    if (spot.getPlane() != client.getPlane()) {
                        continue;
                    }

                    LocalPoint dwhAltPoint2 = LocalPoint.fromWorld(client, spot);

                    if (dwhAltPoint2 != null && localLocation.distanceTo(dwhAltPoint2) <= MAX_DISTANCE && dwhAltPoint2.distanceTo(localLocation) > 0) {
                        renderSpot(graphics, client, dwhAltPoint2, itemManager.getImage(DRAGON_WARHAMMER), Color.RED);
                    }
                }
            }

            if (this.config.stunner() && stunnerPoints.size() > 0) {
                for (WorldPoint spot : stunnerPoints) {
                    if (spot.getPlane() != client.getPlane()) {
                        continue;
                    }

                    LocalPoint stunnerPoint = LocalPoint.fromWorld(client, spot);

                    if (stunnerPoint != null && localLocation.distanceTo(stunnerPoint) <= MAX_DISTANCE && stunnerPoint.distanceTo(localLocation) > 0) {
                        renderSpot(graphics, client, stunnerPoint, itemManager.getImage(TANZANITE_HELM), Color.CYAN);
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
                        renderSpot(graphics, client, customerPoint, itemManager.getImage(COIN_POUCH), Color.BLUE);
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
                this.modelOutlineRenderer.drawOutline(plugin.core, this.config.coreHighlightWidth(), config.coreHighlightColor(), config.coreHighlightGlow());
            }
        }
        return null;
    }

    private void renderSpot(Graphics2D graphics, Client client, LocalPoint point, BufferedImage image, Color color) {
        //Render tile
        Polygon poly = Perspective.getCanvasTilePoly(client, point);

        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, color);
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
}
