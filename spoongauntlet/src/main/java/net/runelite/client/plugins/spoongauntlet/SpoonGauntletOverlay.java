package net.runelite.client.plugins.spoongauntlet;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SpoonGauntletOverlay extends Overlay {
    private final Client client;

    private final SpoonGauntletPlugin plugin;

    private final SpoonGauntletConfig config;

    private final SpriteManager x;

    private final ModelOutlineRenderer modelOutlineRenderer;

    private static final int MAX_DISTANCE = 2350;

    @Inject
    private SpoonGauntletOverlay(Client client, SpoonGauntletPlugin plugin, SpoonGauntletConfig config, SpriteManager x, ModelOutlineRenderer modelOutlineRenderer) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.x = x;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        if (GauntletUtils.inRaid(this.client)){
            if(config.showTrueTile() == SpoonGauntletConfig.showTrueTileMode.BOTH && this.client.getLocalPlayer() != null) {
                LocalPoint lp = LocalPoint.fromWorld(this.client, this.client.getLocalPlayer().getWorldLocation());
                if (lp != null)
                    renderTLTile(graphics, lp);
            }

            if (GauntletUtils.inBoss(this.client)) {
                if(config.showTrueTile() == SpoonGauntletConfig.showTrueTileMode.BOSS && this.client.getLocalPlayer() != null) {
                    LocalPoint lp = LocalPoint.fromWorld(this.client, this.client.getLocalPlayer().getWorldLocation());
                    if (lp != null)
                        renderTLTile(graphics, lp);
                }

                for (NPC npc : this.client.getNpcs()) {
                    if (this.plugin.tornadoesActive && GauntletUtils.isTornado(npc) && this.config.overlayTornadoes() != SpoonGauntletConfig.tornadoMode.OFF) {
                        if (npc.getId() == 9025 || npc.getId() == 9039) {
                            if (this.config.overlayTornadoes() == SpoonGauntletConfig.tornadoMode.BOTH || this.config.overlayTornadoes() == SpoonGauntletConfig.tornadoMode.TICKS) {
                                String textOverlay = Integer.toString(this.plugin.tornadoTicks);
                                Point textLoc = Perspective.getCanvasTextLocation(this.client, graphics, npc.getLocalLocation(), textOverlay, 0);
                                if (textLoc != null) {
                                    Font oldFont = graphics.getFont();
                                    switch (config.fontStyle()) {
                                        case SMALL:
                                            graphics.setFont(FontManager.getRunescapeSmallFont());
                                            break;
                                        case REGULAR:
                                            graphics.setFont(FontManager.getRunescapeFont());
                                            break;
                                        case BOLD:
                                            graphics.setFont(FontManager.getRunescapeBoldFont());
                                            break;
                                        case CUSTOM:
                                            graphics.setFont(new Font("Arial", Font.BOLD, config.fontSize()));
                                    }
                                    Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
                                    OverlayUtil.renderTextLocation(graphics, pointShadow, textOverlay, Color.BLACK);
                                    OverlayUtil.renderTextLocation(graphics, textLoc, textOverlay, Color.YELLOW);
                                    graphics.setFont(oldFont);
                                }
                            }

                            if (this.config.overlayTornadoes() == SpoonGauntletConfig.tornadoMode.BOTH || this.config.overlayTornadoes() == SpoonGauntletConfig.tornadoMode.TILE) {
                                NPCComposition npcComposition = npc.getTransformedComposition();
                                if (npcComposition != null) {
                                    int size = npcComposition.getSize();
                                    LocalPoint lp = LocalPoint.fromWorld(this.client, npc.getWorldLocation());
                                    if (lp != null) {
                                        lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
                                        Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
                                        renderPoly(graphics, config.tornadoColor(), tilePoly);
                                    }
                                }
                            }
                        }
                    }

                    if (GauntletUtils.isBoss(npc)) {
                        LocalPoint point = npc.getLocalLocation();
                        if(this.config.overlayBoss() != SpoonGauntletConfig.bossOverlayMode.OFF){
                            Color color;
                            if (this.plugin.playerCounter >1) {
                                if (this.plugin.currentPhase == SpoonGauntletPlugin.BossAttackPhase.MAGIC) {
                                    color = Color.CYAN;
                                } else {
                                    color = Color.GREEN;
                                }
                            } else {
                                color = Color.ORANGE;
                            }

                            if (this.config.overlayBoss() == SpoonGauntletConfig.bossOverlayMode.HULL) {
                                Shape polygon = npc.getConvexHull();
                                if (polygon != null) {
                                    graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 255));
                                    graphics.draw(polygon);
                                    graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
                                    graphics.fill(polygon);
                                }
                            } else if (this.config.overlayBoss() == SpoonGauntletConfig.bossOverlayMode.OUTLINE) {
                                Shape polygon = npc.getConvexHull();
                                if (polygon != null) {
                                    modelOutlineRenderer.drawOutline(npc, 2, color, 4);
                                }
                            } else if (this.config.overlayBoss() == SpoonGauntletConfig.bossOverlayMode.TRUE_LOCATION) {
                                int size = 1;
                                NPCComposition composition = npc.getTransformedComposition();
                                if (composition != null)
                                    size = composition.getSize();
                                LocalPoint lp = LocalPoint.fromWorld(this.client, npc.getWorldLocation());
                                if (lp != null) {
                                    lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
                                    Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
                                    renderPoly(graphics, color, tilePoly);
                                }
                            } else  {
                                NPCComposition npcComp = npc.getComposition();
                                int size = npcComp.getSize();
                                LocalPoint lp = npc.getLocalLocation();
                                Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
                                renderPoly(graphics, color, tilePoly);
                            }
                        }

                        if (this.config.overlayBossPrayer()) {
                            BufferedImage attackIcon = null;
                            switch (this.plugin.currentPhase) {
                                case MAGIC:
                                    attackIcon = this.plugin.imageAttackMage;
                                    break;
                                case RANGE:
                                    attackIcon = this.plugin.imageAttackRange;
                                    break;
                            }
                            if (attackIcon != null) {
                                Point imageLoc = Perspective.getCanvasImageLocation(this.client, point, attackIcon, npc.getLogicalHeight() / 2);
                                if (imageLoc != null) {
                                    graphics.drawImage(attackIcon, imageLoc.getX(), imageLoc.getY(), null);
                                }
                            }
                        }

                        if (config.attackTextStyle() == SpoonGauntletConfig.attackTextStyleMode.STEROIDS) {
                            String textOverlay = "";
                            if (this.config.countBossAttacks()) {
                                if (config.attackCounterType() == SpoonGauntletConfig.attackCounterTypeMode.DETAILED) {
                                    textOverlay += "Boss: ";
                                }
                                if(config.attackCounterUp()) {
                                    textOverlay += String.valueOf(4 - this.plugin.bossCounter);
                                }else {
                                    textOverlay += this.plugin.bossCounter;
                                }
                            }
                            if (this.config.countPlayerAttacks()) {
                                if (textOverlay.length() > 0) {
                                    textOverlay += " | ";
                                    if (config.attackCounterType() == SpoonGauntletConfig.attackCounterTypeMode.DETAILED) {
                                        textOverlay += "You: ";
                                    }
                                }
                                if(config.attackCounterUp()) {
                                    textOverlay += String.valueOf(6 - this.plugin.playerCounter);
                                }else {
                                    textOverlay += this.plugin.playerCounter;
                                }
                            }

                            if (textOverlay.length() > 0) {
                                Point textLoc = Perspective.getCanvasTextLocation(this.client, graphics, point, textOverlay, npc.getLogicalHeight() / 2);
                                if (textLoc != null) {
                                    textLoc = new Point(textLoc.getX(), textLoc.getY() + 35);
                                    Font oldFont = graphics.getFont();
                                    switch (config.fontStyle()) {
                                        case SMALL:
                                            graphics.setFont(FontManager.getRunescapeSmallFont());
                                            break;
                                        case REGULAR:
                                            graphics.setFont(FontManager.getRunescapeFont());
                                            break;
                                        case BOLD:
                                            graphics.setFont(FontManager.getRunescapeBoldFont());
                                            break;
                                        case CUSTOM:
                                            graphics.setFont(new Font("Arial", Font.BOLD, config.fontSize()));
                                    }
                                    Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
                                    OverlayUtil.renderTextLocation(graphics, pointShadow, textOverlay, Color.BLACK);
                                    if (config.textColor()) {
                                        if (plugin.currentPhase == SpoonGauntletPlugin.BossAttackPhase.MAGIC) {
                                            OverlayUtil.renderTextLocation(graphics, textLoc, textOverlay, Color.CYAN);
                                        } else {
                                            OverlayUtil.renderTextLocation(graphics, textLoc, textOverlay, Color.GREEN);
                                        }
                                    } else {
                                        OverlayUtil.renderTextLocation(graphics, textLoc, textOverlay, Color.WHITE);
                                    }
                                    graphics.setFont(oldFont);
                                }
                            }
                        }else {
                            String line1 = "";
				            String line2 = "";
                            if (this.config.countBossAttacks()) {
                                if(config.attackCounterUp()) {
                                    line1 += "Boss: " + (4 - this.plugin.bossCounter);
                                }else {
                                    line1 += "Boss: " + this.plugin.bossCounter;
                                }
                            }
                            if (this.config.countPlayerAttacks()) {
                                if(config.attackCounterUp()) {
                                    line2 += "You: " + (6 - this.plugin.playerCounter);
                                }else {
                                    line2 += "You: " + this.plugin.playerCounter;
                                }
                            }

				            Point canvasPointCenter = npc.getCanvasTextLocation(graphics, "", 120);
				            if (canvasPointCenter != null) {
					            FontMetrics metrics = graphics.getFontMetrics();
                                Font oldFont = graphics.getFont();
                                switch (config.fontStyle()) {
                                    case SMALL:
                                        graphics.setFont(FontManager.getRunescapeSmallFont());
                                        break;
                                    case REGULAR:
                                        graphics.setFont(FontManager.getRunescapeFont());
                                        break;
                                    case BOLD:
                                        graphics.setFont(FontManager.getRunescapeBoldFont());
                                        break;
                                    case CUSTOM:
                                        graphics.setFont(new Font("Arial", Font.BOLD, config.fontSize()));
                                }
					            int width1 = metrics.stringWidth(line1);
					            int width2 = metrics.stringWidth(line2);
					            Point line1Point = new Point(canvasPointCenter.getX() - width1 / 2, canvasPointCenter.getY());
					            Point line2Point = new Point(canvasPointCenter.getX() - width2 / 2, config.countBossAttacks() ? canvasPointCenter.getY() + metrics.getHeight() : canvasPointCenter.getY());
                                Color bossColor;
                                if (config.textColor()) {
                                    if (plugin.currentPhase == SpoonGauntletPlugin.BossAttackPhase.MAGIC) {
                                        bossColor = Color.CYAN;
                                    } else {
                                        bossColor = Color.GREEN;
                                    }
                                } else {
                                    bossColor = Color.WHITE;
                                }

                                Color playerColor;
                                if (this.plugin.playerCounter >1) {
                                    if (this.plugin.currentPhase == SpoonGauntletPlugin.BossAttackPhase.MAGIC) {
                                        playerColor = Color.CYAN;
                                    } else {
                                        playerColor = Color.GREEN;
                                    }
                                } else {
                                    playerColor = Color.ORANGE;
                                }
					            OverlayUtil.renderTextLocation(graphics, line1Point, line1, bossColor);
					            OverlayUtil.renderTextLocation(graphics, line2Point, line2, playerColor);
                                graphics.setFont(oldFont);
				            }
                        }

                        if(config.wrongStyleOutline() && this.client.getLocalPlayer() != null){
                            if (this.plugin.currentPhase == SpoonGauntletPlugin.BossAttackPhase.MAGIC && this.client.getLocalPlayer().getOverheadIcon() != HeadIcon.MAGIC) {
                                this.modelOutlineRenderer.drawOutline(npc, this.config.wrongStyleOutlineThiCC(), Color.BLUE, 2);
                            } else if(this.plugin.currentPhase == SpoonGauntletPlugin.BossAttackPhase.RANGE && this.client.getLocalPlayer().getOverheadIcon() != HeadIcon.RANGED){
                                this.modelOutlineRenderer.drawOutline(npc, this.config.wrongStyleOutlineThiCC(), Color.GREEN, 2);
                            }
                        }
                    }
                }
            } else {
                if(this.client.getLocalPlayer() != null && this.plugin.resources.size() > 0){
                    for (GameObject object : this.plugin.resources) {
                        Color color = SystemColor.YELLOW;
                        int id = object.getId();
                        int size = 1;
                        int imgOffset = 0;
                        BufferedImage icon = null;
                        if (GauntletUtils.arrayContainsInteger(GauntletUtils.CRYSTAL_DEPOSIT, id)) {
                            color = config.rockResourceColor();
                            icon = this.plugin.imageCrystalDeposit;
                            imgOffset = 50;
                        } else if (GauntletUtils.arrayContainsInteger(GauntletUtils.PHREN_ROOTS, id)) {
                            color = config.treeResourceColor();
                            icon = this.plugin.imagePhrenRoots;
                            imgOffset = 100;
                        } else if (GauntletUtils.arrayContainsInteger(GauntletUtils.FISHING_SPOTS, id)) {
                            color = config.fishResourceColor();
                            size = 2;
                            icon = this.plugin.imageFishingSpot;
                        } else if (GauntletUtils.arrayContainsInteger(GauntletUtils.GRYM_ROOTS, id)) {
                            color = config.plantResourceColor();
                            icon = this.plugin.imageGrymRoot;
                        } else if (GauntletUtils.arrayContainsInteger(GauntletUtils.LINUM_TIRINUM, id)) {
                            color = config.linumResourceColor();
                            icon = this.plugin.imageLinumTirinum;
                            imgOffset = 100;
                        }

                        if(this.config.resourceMode() != SpoonGauntletConfig.resourceMode.OFF) {
                            if (color != SystemColor.YELLOW && (!this.config.lowFps() || object.getLocalLocation().distanceTo(this.client.getLocalPlayer().getLocalLocation()) < 2350)) {
                                if (this.config.resourceMode() == SpoonGauntletConfig.resourceMode.TILE || config.lowFps()) {
                                    Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, object.getLocalLocation(), size);
                                    renderPoly(graphics, color, tilePoly);
                                } else {
                                    graphics.setColor(color);
                                    if (object.getConvexHull() != null) {
                                        graphics.draw(object.getConvexHull());
                                        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
                                    }
                                }
                            }
                        }

                        if (this.config.highlightResourcesIcons() && icon != null) {
                            if (!this.config.lowFps() || object.getLocalLocation().distanceTo(this.client.getLocalPlayer().getLocalLocation()) < 2350) {
                                LocalPoint lp = object.getLocalLocation();
                                OverlayUtil.renderImageLocation(client, graphics, lp, icon, imgOffset);
                            }
                        }
                    }
                }
            }
            if (config.utilitiesOutline() && !plugin.utilities.isEmpty()) {
                for (final GameObject gameObject : plugin.utilities) {
                    final Shape shape = gameObject.getConvexHull();

                    if (shape != null) {
                        modelOutlineRenderer.drawOutline(gameObject, config.utilitiesOutlineWidth(), config.utilitiesOutlineColor(), 4);
                    }
                }
            }
        }
        return null;
    }

    private void renderPoly(Graphics2D graphics, Color color, Shape polygon) {
        if (polygon != null) {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            graphics.fill(polygon);
        }
    }

    private void renderTLTile(Graphics2D graphics, LocalPoint lp) {
        Polygon poly = Perspective.getCanvasTilePoly(this.client, lp);
        if (poly != null) {
            Stroke originalStroke = graphics.getStroke();
            graphics.setStroke(new BasicStroke((float)config.showTrueTileWidth()));

            if(config.showTrueTileWidth() == 0) {
                graphics.setColor(new Color(config.showTrueTileColor().getRed(), config.showTrueTileColor().getGreen(), config.showTrueTileColor().getBlue(), 0));
            } else {
                graphics.setColor(config.showTrueTileColor());
            }
            graphics.draw(poly);
            Color fillColor = new Color(config.showTrueTileColor().getRed(), config.showTrueTileColor().getGreen(), config.showTrueTileColor().getBlue(), config.showTrueTileOpacity());
            graphics.setColor(fillColor);
            graphics.fill(poly);

            graphics.setStroke(originalStroke);
        }
    }
}
