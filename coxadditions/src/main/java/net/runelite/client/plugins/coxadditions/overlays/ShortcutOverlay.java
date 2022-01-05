package net.runelite.client.plugins.coxadditions.overlays;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.overlay.*;

@Singleton
public class ShortcutOverlay extends Overlay {
    private final Client client;

    private final CoxAdditionsConfig config;

    private final CoxAdditionsPlugin plugin;

    private final BufferedImage treeIcon;

    private final BufferedImage strengthIcon;

    private final BufferedImage miningIcon;

    @Inject
    ShortcutOverlay(Client client, CoxAdditionsPlugin plugin, CoxAdditionsConfig config, SkillIconManager iconManager) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.treeIcon = iconManager.getSkillImage(Skill.WOODCUTTING);
        this.strengthIcon = iconManager.getSkillImage(Skill.STRENGTH);
        this.miningIcon = iconManager.getSkillImage(Skill.MINING);
    }

    public Dimension render(Graphics2D graphics) {
        if(this.client.getVar(Varbits.IN_RAID) == 1) {
            for (TileObject shortcut : this.plugin.getShortcut()) {
                if (shortcut.getPlane() == this.client.getPlane()) {
                    Shape poly;
                    if (shortcut instanceof GameObject) {
                        poly = ((GameObject)shortcut).getConvexHull();
                    } else {
                        poly = shortcut.getCanvasTilePoly();
                    }
                    if (poly != null) {
                        String name;
                        switch (shortcut.getId()) {
                            case 29736:
                                name = "Tree";
                                break;
                            case 29738:
                                name = "Rocks";
                                break;
                            case 29740:
                                name = "Boulder";
                                break;
                            default:
                                name = "null";
                                break;
                        }
                        if (this.plugin.isHighlightShortcuts()) {
                            if (name.equals("Tree")) {
                                Point canvasLoc = Perspective.getCanvasImageLocation(this.client, shortcut.getLocalLocation(), this.treeIcon, 150);
                                if (canvasLoc != null)
                                    graphics.drawImage(this.treeIcon, canvasLoc.getX(), canvasLoc.getY(), null);
                                Shape clickbox = shortcut.getClickbox();
                                if (clickbox != null) {
                                    Color fillColor = new Color(config.shortcutColor().getRed(), config.shortcutColor().getGreen(), config.shortcutColor().getBlue(), 20);
                                    OverlayUtil.renderHoverableArea(graphics, shortcut.getClickbox(), client.getMouseCanvasPosition(), fillColor, config.shortcutColor(), config.shortcutColor().darker());
                                }
                            }
                            if (name.equals("Rocks")) {
                                Point canvasLoc = Perspective.getCanvasImageLocation(this.client, shortcut.getLocalLocation(), this.miningIcon, 150);
                                if (canvasLoc != null)
                                    graphics.drawImage(this.miningIcon, canvasLoc.getX(), canvasLoc.getY(), null);
                                Shape clickbox = shortcut.getClickbox();
                                if (clickbox != null) {
                                    Color fillColor = new Color(config.shortcutColor().getRed(), config.shortcutColor().getGreen(), config.shortcutColor().getBlue(), 20);
                                    OverlayUtil.renderHoverableArea(graphics, shortcut.getClickbox(), client.getMouseCanvasPosition(), fillColor, config.shortcutColor(), config.shortcutColor().darker());
                                }
                            }
                            if (name.equals("Boulder")) {
                                Point canvasLoc = Perspective.getCanvasImageLocation(this.client, shortcut.getLocalLocation(), this.strengthIcon, 150);
                                if (canvasLoc != null)
                                    graphics.drawImage(this.strengthIcon, canvasLoc.getX(), canvasLoc.getY(), null);
                                Shape clickbox = shortcut.getClickbox();
                                if (clickbox != null) {
                                    Color fillColor = new Color(config.shortcutColor().getRed(), config.shortcutColor().getGreen(), config.shortcutColor().getBlue(), 20);
                                    OverlayUtil.renderHoverableArea(graphics, shortcut.getClickbox(), client.getMouseCanvasPosition(), fillColor, config.shortcutColor(), config.shortcutColor().darker());
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
