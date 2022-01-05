package net.runelite.client.plugins.spoonvorkath;

import java.awt.*;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class VorkathOverlay extends Overlay {
    private final Client client;

    private final VorkathConfig config;

    @Inject
    private VorkathPlugin plugin;

    @Inject
    private VorkathOverlay(Client client, VorkathPlugin plugin, VorkathConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        if (!this.plugin.isVorkathInstance())
            return null;
        if (this.plugin.getVorkathNpc() != null)
            if (this.plugin.getVorkathNpc().getId() == 8061 && this.plugin.getAttacksLeft() >= 0) {
                if (this.config.phaseAttackCounter()) {
                    String specialAttackCounter = (this.plugin.getAttacksLeft() > 0) ? Integer.toString(this.plugin.getAttacksLeft()) : "NOW!";
                    Color specialAttackColor = Color.WHITE;
                    if (this.plugin.getAttacksLeft() <= 0)
                        specialAttackColor = Color.RED;
                    if (this.config.nextPhaseName()) {
                        VorkathUtils.VorkathPhase vorkathPhase = this.plugin.getNextVorkathPhase();
                        switch (vorkathPhase) {
                            case ZOMBIFIED_SPAWN:
                                specialAttackCounter = (this.plugin.getAttacksLeft() > 0) ? ("Zombified Spawn in: " + this.plugin.getAttacksLeft()) : "Zombified Spawn NOW!";
                                break;
                            case ACID:
                                specialAttackCounter = (this.plugin.getAttacksLeft() > 0) ? ("Acid Pool in: " + this.plugin.getAttacksLeft()) : "Acid Pool NOW!";
                                break;
                            case UNKNOWN:
                                specialAttackCounter = (this.plugin.getAttacksLeft() > 0) ? ("Unknown Phase in: " + this.plugin.getAttacksLeft()) : "Unknown Phase NOW!";
                                break;
                        }
                    }
                    Point canvasPoint = this.plugin.getVorkathNpc().getCanvasTextLocation(graphics, specialAttackCounter, 60);

                    renderTextLocation(graphics, canvasPoint, specialAttackCounter, specialAttackColor);
                }
                if (this.plugin.getZombifiedSpawn() != null && this.config.zombifiedSpawn() != TileMode.OFF) {
                    NPC npc = this.plugin.getZombifiedSpawn();
                    renderNpcOverlay(graphics, npc, this.config.zombifiedSpawnColor(), 1, 150, 50);
                }
            }
        return null;
    }

    private static void renderTextLocation(Graphics2D graphics, Point canvasPoint, String text, Color fontColor) {
        if (canvasPoint != null) {
            int x = canvasPoint.getX();
            int y = canvasPoint.getY();
            graphics.setColor(Color.BLACK);
            graphics.drawString(text, x + 1, y + 1);
            graphics.setColor(fontColor);
            graphics.drawString(text, x, y);
        }
    }

    private void renderNpcOverlay(Graphics2D graphics, NPC actor, Color color, int outlineWidth, int outlineAlpha, int fillAlpha) {
        int size = 1;
        NPCComposition composition = actor.getTransformedComposition();
        if (composition != null) {
            size = composition.getSize();
        }
        LocalPoint lp = actor.getLocalLocation();

        if(this.config.zombifiedSpawn() == TileMode.AREA) {
            Shape poly = actor.getConvexHull();
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
            graphics.fill(poly);
        }else if(this.config.zombifiedSpawn() == TileMode.TILE){
            Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
            if (tilePoly != null) {
                graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
                graphics.setStroke(new BasicStroke(outlineWidth));
                graphics.draw(tilePoly);
                graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
                graphics.fill(tilePoly);
            }
        }
    }
}
