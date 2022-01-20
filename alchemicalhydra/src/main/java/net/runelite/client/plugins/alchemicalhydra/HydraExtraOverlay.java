package net.runelite.client.plugins.alchemicalhydra;

import com.google.common.base.Strings;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Collection;

@Singleton
public class HydraExtraOverlay extends Overlay {
    private final Client client;
    private final HydraPlugin plugin;
    private final HydraConfig config;
    private ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private HydraExtraOverlay(final Client client, final HydraPlugin plugin, final HydraConfig config, ModelOutlineRenderer modelOutlineRenderer) {
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
        if (config.hydraImmunityOutline() == HydraConfig.ImmunityMode.HULL && plugin.immuneActive && plugin.hydra.getNpc() != null) {
            Shape poly = plugin.hydra.getNpc().getConvexHull();
            if (poly != null) {
                Color color = config.hydraImmunityColor();
                int strokeWidth = this.config.hydraImmunityWidth();
                int outlineAlpha = 255;
                int fillAlpha = 0;
                graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
                graphics.setStroke(new BasicStroke(strokeWidth));
                graphics.draw(poly);
                graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
                graphics.fill(poly);
            }
        }else if (config.hydraImmunityOutline() == HydraConfig.ImmunityMode.OUTLINE && plugin.immuneActive && plugin.getHydra() != null){
            Color color = config.hydraImmunityColor();
            if(plugin.hydra.getNpc() != null) {
                this.modelOutlineRenderer.drawOutline(plugin.hydra.getNpc(), this.config.hydraImmunityWidth(), color, config.hydraImmunityGlow());
            }
        }

        if(config.lightningHighlight() != HydraConfig.LightningMode.OFF & ((Collection<?>) this.client.getGraphicsObjects().iterator()).size() > 0) {
            for (GraphicsObject obj : this.client.getGraphicsObjects()) {
                if (obj.getId() == 1666) {
                    int size = 1;
                    LocalPoint lp = obj.getLocation();
                    if (config.lightningHighlight() == HydraConfig.LightningMode.TRUE_LOCATION) {
                        if (lp != null) {
                            lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
                            Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
                            if (tilePoly != null) {
                                renderPoly(graphics, config.lightningColor(), tilePoly, config.lightningWidth());
                            }
                        }
                    }  else {
                        modelOutlineRenderer.drawOutline(obj,config.lightningWidth(), config.lightningColor(), config.lightningGlow());
                    }
                }
            }
        }

        if(config.ventTicks() && plugin.ventTicks > 0 && plugin.inHydraInstance && this.client.isInInstancedRegion()){
            String text = String.valueOf(plugin.ventTicks);
            Font oldFont = graphics.getFont();
            graphics.setFont(FontManager.getRunescapeBoldFont());
            Color color = Color.WHITE;
            if(plugin.redVent != null && (plugin.hydra.getNpc() == null || plugin.hydra.getNpc().getId() == 8615)){
                Point textLoc = plugin.redVent.getCanvasTextLocation(graphics, text, 25);
                Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
                OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
                OverlayUtil.renderTextLocation(graphics, textLoc, text, color);
            }else if(plugin.greenVent != null && (plugin.hydra.getNpc().getId() == 8619 || plugin.hydra.getNpc().getId() == 8616)){
                Point textLoc = plugin.greenVent.getCanvasTextLocation(graphics, text, 25);
                Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
                OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
                OverlayUtil.renderTextLocation(graphics, textLoc, text, color);
            }else if(plugin.blueVent != null && (plugin.hydra.getNpc().getId() == 8620 || plugin.hydra.getNpc().getId() == 8617 || plugin.hydra.getNpc().getId() == 8621 || plugin.hydra.getNpc().getId() == 8618)){
                Point textLoc = plugin.blueVent.getCanvasTextLocation(graphics, text, 25);
                Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
                OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
                OverlayUtil.renderTextLocation(graphics, textLoc, text, color);
            }
            graphics.setFont(oldFont);
        }

        if (config.showHpUntilPhaseChange() && plugin.hydra != null) {
            if (plugin.hydra.getNpc().getId() != 8621 && plugin.hydra.getNpc().getId() != 8616 && plugin.hydra.getNpc().getId() != 8617 && plugin.hydra.getNpc().getId() != 8618) {
                NPC npc = plugin.hydra.getNpc();
                int healthThreshold = 0;

                if (plugin.hydra.getNpc().getId() == 8615) {
                    healthThreshold = 825;
                } else if (plugin.hydra.getNpc().getId() == 8619) {
                    healthThreshold = 550;
                } else if (plugin.hydra.getNpc().getId() == 8620) {
                    healthThreshold = 275;
                }

                final int ratio = npc.getHealthRatio();
                final int health = npc.getHealthScale();

                if (ratio < 0 || health <= 0) {
                    return null;
                }

                int exactHealth = 0;
                if (ratio > 0) {
                    int minHealth = 1;
                    int maxHealth;

                    if (health > 1) {
                        if (ratio > 1) {
                            minHealth = (1100 * (ratio - 1) + health - 2) / (health - 1);
                        }
                        maxHealth = (1100 * ratio - 1) / (health - 1);
                        if (maxHealth > 1100) {
                            maxHealth = 1100;
                        }
                    } else {
                        maxHealth = 1100;
                    }
                    exactHealth = (minHealth + maxHealth + 1) / 2;
                }
                int hpLeft = exactHealth - healthThreshold;

                Color textColor = Color.WHITE;
                if (hpLeft <= 83) {
                    textColor = Color.RED;
                }
                System.out.println(hpLeft);
                String text = String.valueOf(hpLeft);
                net.runelite.api.Point textLoc = plugin.hydra.getNpc().getCanvasTextLocation(graphics, text, 75);
                if (textLoc != null) {
                    Font oldFont = graphics.getFont();
                    graphics.setFont(FontManager.getRunescapeBoldFont());
                    net.runelite.api.Point pointShadow = new net.runelite.api.Point(textLoc.getX() + 1, textLoc.getY() + 1);
                    OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
                    OverlayUtil.renderTextLocation(graphics, textLoc, text, textColor);
                    System.out.println("text done");
                    graphics.setFont(oldFont);
                }
            }
        }
        return null;
    }

    private void renderPoly(Graphics2D graphics, Color color, Shape polygon, int width) {
        if (polygon != null) {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(width));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
            graphics.fill(polygon);
        }
    }

    protected void renderTextLocation(Graphics2D graphics, @Nullable net.runelite.api.Point txtLoc, @Nullable String text, @Nonnull Color color) {
        if (txtLoc == null || Strings.isNullOrEmpty(text))
            return;
        int x = txtLoc.getX();
        int y = txtLoc.getY();
        graphics.setColor(Color.BLACK);
        {
            graphics.drawString(text, x, y + 1);
            graphics.drawString(text, x, y - 1);
            graphics.drawString(text, x + 1, y);
            graphics.drawString(text, x - 1, y);
        }
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
        graphics.drawString(text, x, y);
    }

    protected void renderImageLocation(Graphics2D graphics, @Nullable Point imgLoc, @Nullable BufferedImage image) {
        if (imgLoc == null || image == null)
            return;
        int x = imgLoc.getX();
        int y = imgLoc.getY();
        graphics.drawImage(image, x, y, (ImageObserver)null);
    }
}
