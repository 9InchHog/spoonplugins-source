package net.runelite.client.plugins.bonylo;

import com.google.common.collect.ArrayListMultimap;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.*;

class BoNyloOverlay extends Overlay {
    private static final Logger log = LoggerFactory.getLogger(BoNyloOverlay.class);

    private final Client client;

    private final BoNyloConfig config;

    private final BoNyloPlugin plugin;

    private final BoNyloTileOverlay tileOverlay;

    private final ModelOutlineRenderer modelOutlineRenderer;

    private Graphics2D graphics;

    private String assign;

    private Color textColor = Color.WHITE;

    @Getter
    public ArrayListMultimap<WorldPoint, NPC> guardsGrouped = ArrayListMultimap.create();

    @Getter
    public ArrayListMultimap<WorldPoint, BoNyloPlugin.NyloContainer> nyloGrouped = ArrayListMultimap.create();

    @Inject
    private BoNyloOverlay(Client client, BoNyloPlugin plugin, BoNyloConfig config, BoNyloTileOverlay tileOverlay, ModelOutlineRenderer modelOutlineRenderer) {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.tileOverlay = tileOverlay;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        guardsGrouped.clear();
        nyloGrouped.clear();
        int style = 0;
        if (config.enableBold())
            style = 1;
        if (!plugin.getTest2().isEmpty() && config.enableDebug()) {
            ArrayListMultimap<WorldPoint, NPC> guardsGrouped = ArrayListMultimap.create();
            for (NPC npc : plugin.getTest2()) {
                String guard = npc.getName();
                if (!npc.isDead() && guard != null)
                    guardsGrouped.put(npc.getWorldLocation(), npc);
            }
            if (!guardsGrouped.isEmpty()) {
                if (config.textAA()) {
                    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                } else {
                    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                }
                graphics.setFont(new Font("Arial", style, config.textSize()));
                for (WorldPoint worldPoint : guardsGrouped.keys()) {
                    int offset = 0;
                    for (NPC npc : guardsGrouped.get(worldPoint)) {
                        drawDebugOverlay(graphics, npc, offset);
                        offset += graphics.getFontMetrics().getHeight();
                    }
                }
            }
        }
        if (plugin.getBigNylos().isEmpty())
            return null;
        ArrayListMultimap<WorldPoint, BoNyloPlugin.NyloContainer> nyloGrouped = ArrayListMultimap.create();
        for (BoNyloPlugin.NyloContainer nylo : plugin.getBigNylos()) {
            String nyloName = nylo.getNpc().getName();
            if (nylo.isAlive() && nyloName != null)
                nyloGrouped.put(nylo.getNpc().getWorldLocation(), nylo);
        }
        if (!nyloGrouped.isEmpty()) {
            if (config.textAA()) {
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            } else {
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            }
            graphics.setFont(new Font("Arial", style, config.textSize()));
            for (WorldPoint worldPoint : nyloGrouped.keys()) {
                int offset = 0;
                for (BoNyloPlugin.NyloContainer nylo : nyloGrouped.get(worldPoint)) {
                    drawNyloOverlay(graphics, nylo, offset);
                    offset += graphics.getFontMetrics().getHeight();
                }
            }
        }
        return null;
    }

    private void drawNyloOverlay(Graphics2D graphics, BoNyloPlugin.NyloContainer nylo, int offset) {
        Point textLocation = nylo.getNpc().getCanvasTextLocation(graphics, Integer.toString(nylo.getTicksAlive()), config.zOffText());
        if (nylo.isAlive() && textLocation != null) {
            int x = textLocation.getX();
            int y = textLocation.getY() + offset;
            if (nylo.getTicksAlive() <= config.BoxLowThreshold() && (nylo.getSize().equals("small") || config.enableBoxBigs())
                    && plugin.getWave() >= config.enableAfterWave() && ((nylo.getCurrentColor().equals(Color.WHITE) && config.enableBoxMelee())
                    || (nylo.getCurrentColor().equals(Color.GREEN) && config.enableBoxRange())
                    || (nylo.getCurrentColor().equals(Color.CYAN) && config.enableBoxMage()))) {
                if (config.enableTile())
                    tileOverlay.renderTile(graphics, nylo.getNpc(), nylo.getCurrentColor());
                if (config.enablePixelOutline())
                    modelOutlineRenderer.drawOutline(nylo.getNpc(), config.pixelWidth(), nylo.getCurrentColor(), 4);
                if (config.enableHull()) {
                    Shape objectClickbox = nylo.getNpc().getConvexHull();
                    renderPoly(graphics, nylo.getCurrentColor(), objectClickbox);
                }
                if (config.enableBox()) {
                    graphics.setColor(nylo.getCurrentColor());
                    graphics.setStroke(new BasicStroke(config.boxStrokeWidth()));
                    int rX = graphics.getFontMetrics().getHeight() + config.boxSize();
                    int rY = graphics.getFontMetrics().getHeight() - 1 + config.boxSize();
                    Point rOrigin = new Point(x - 2, y - rY - 2);
                    Rectangle r = new Rectangle(rOrigin.getX() - config.boxSize() / 2, rOrigin.getY() + config.boxSize() / 2, rX, rY);
                    graphics.draw(r);
                    graphics.setColor(new Color(nylo.getCurrentColor().getRed(), nylo.getCurrentColor().getGreen(), nylo.getCurrentColor().getBlue(), (int)Math.round(config.boxFillAlpha() * 2.55D)));
                    graphics.fill(r);
                }
            }
            graphics.setColor(nylo.getT26color());
            if (config.enableMonochrome())
                graphics.setColor(Color.BLACK);
            graphics.drawString(Integer.toString(nylo.getTicksAlive()), x + 1, y);
            graphics.drawString(Integer.toString(nylo.getTicksAlive()), x - 1, y);
            graphics.drawString(Integer.toString(nylo.getTicksAlive()), x, y + 1);
            graphics.drawString(Integer.toString(nylo.getTicksAlive()), x, y - 1);
            graphics.drawString(Integer.toString(nylo.getTicksAlive()), x + 1, y + 1);
            graphics.drawString(Integer.toString(nylo.getTicksAlive()), x + 1, y - 1);
            graphics.drawString(Integer.toString(nylo.getTicksAlive()), x - 1, y + 1);
            graphics.drawString(Integer.toString(nylo.getTicksAlive()), x - 1, y - 1);
            graphics.setColor(nylo.getCurrentColor());
            if (config.enableMonochrome())
                graphics.setColor(Color.WHITE);
            graphics.drawString(Integer.toString(nylo.getTicksAlive()), x, y);
        }
    }

    private void drawDebugOverlay(Graphics2D graphics, NPC npc, int offset) {
        int debugNumber = plugin.getDebugCounter();
        Point textLocation = npc.getCanvasTextLocation(graphics, Integer.toString(client.getTickCount()), config.zOffText());
        if (!npc.isDead() && textLocation != null) {
            int x = textLocation.getX();
            int y = textLocation.getY() - offset;
            int id2 = npc.getId();
            switch (id2) {
                case 3269:
                    textColor = Color.WHITE;
                    break;
                case 3271:
                    textColor = Color.GREEN;
                    break;
                case 3272:
                    textColor = Color.CYAN;
                    break;
                default:
                    textColor = Color.BLACK;
                    break;
            }
            graphics.setColor(textColor);
            if (config.enableTile())
                tileOverlay.renderTile(graphics, npc, textColor);
            if (config.enablePixelOutline())
                modelOutlineRenderer.drawOutline(npc, config.pixelWidth(), textColor, 4);
            if (config.enableHull()) {
                Shape objectClickbox = npc.getConvexHull();
                renderPoly(graphics, textColor, objectClickbox);
            }
            if (config.enableBox()) {
                graphics.setStroke(new BasicStroke(config.boxStrokeWidth()));
                int rX = graphics.getFontMetrics().getHeight() + config.boxSize();
                int rY = graphics.getFontMetrics().getHeight() - 1 + config.boxSize();
                Point rOrigin = new Point(x - 2, y - rY - 2);
                Rectangle r = new Rectangle(rOrigin.getX() - config.boxSize() / 2, rOrigin.getY() + config.boxSize() / 2, rX, rY);
                graphics.draw(r);
                graphics.setColor(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), (int)Math.round(config.boxFillAlpha() * 2.55D)));
                graphics.fill(r);
            }
            Color oldColor = new Color(graphics.getColor().getRGB());
            graphics.setColor(plugin.getTestShadowColor());
            graphics.drawString(Integer.toString(debugNumber), x + 1, y);
            graphics.drawString(Integer.toString(debugNumber), x - 1, y);
            graphics.drawString(Integer.toString(debugNumber), x, y + 1);
            graphics.drawString(Integer.toString(debugNumber), x, y - 1);
            graphics.drawString(Integer.toString(debugNumber), x + 1, y + 1);
            graphics.drawString(Integer.toString(debugNumber), x + 1, y - 1);
            graphics.drawString(Integer.toString(debugNumber), x - 1, y + 1);
            graphics.drawString(Integer.toString(debugNumber), x - 1, y - 1);
            if (config.enableMonochrome())
                graphics.setColor(Color.BLACK);
            if (config.debugRHmsgs() && debugNumber == 10) {
                log.info(String.valueOf(graphics.getRenderingHints()));
                log.info(String.valueOf(graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS)));
                log.info(String.valueOf(graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING)));
                log.info(String.valueOf(graphics.getRenderingHint(RenderingHints.KEY_RENDERING)));
                log.info(String.valueOf(graphics.getRenderingHint(RenderingHints.KEY_INTERPOLATION)));
                log.info(String.valueOf(graphics.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL)));
                log.info(String.valueOf(graphics.getRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST)));
                log.info(String.valueOf(graphics.getRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION)));
                log.info(String.valueOf(graphics.getRenderingHint(RenderingHints.KEY_DITHERING)));
                log.info(String.valueOf(graphics.getRenderingHint(RenderingHints.KEY_COLOR_RENDERING)));
            }
            graphics.setColor(oldColor);
            if (config.enableMonochrome())
                graphics.setColor(Color.WHITE);
            graphics.drawString(Integer.toString(debugNumber), x, y);
        }
    }

    private void renderPoly(Graphics2D graphics, Color color, Shape polygon) {
        if (polygon != null) {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(config.hullStrokeWidth()));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)Math.round(config.hullFillAlpha() * 2.55D)));
            graphics.fill(polygon);
        }
    }
}
