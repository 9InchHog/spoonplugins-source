package net.runelite.client.plugins.spoonannoyancemute;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class SpoonAnnoyanceMuteOverlay extends Overlay {
    private Client client;

    private SpoonAnnoyanceMuteConfig config;

    private SpoonAnnoyanceMutePlugin plugin;

    @Inject
    public SpoonAnnoyanceMuteOverlay(Client client, SpoonAnnoyanceMuteConfig config, SpoonAnnoyanceMutePlugin plugin) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGHEST);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }

    public Dimension render(Graphics2D graphics) {
        if (config.debug() && plugin.debugSoundList.size() > 0) {
            for (DebugSoundEffect dse : plugin.debugSoundList) {
                if(dse.source != null) {
                    Polygon poly = dse.source.getCanvasTilePoly();
                    Point textLoc = dse.source.getCanvasTextLocation(graphics, String.valueOf(dse.id), 60);
                    if (poly != null && textLoc != null) {
                        Color color;
                        if (dse.type.equals("Area")) {
                            color = Color.ORANGE;
                        } else {
                            color = Color.MAGENTA;
                        }
                        renderPoly(graphics, color, poly, 2, 0);
                        OverlayUtil.renderTextLocation(graphics, textLoc, String.valueOf(dse.id), color);
                    }
                }
            }
        }
        return null;
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
}
