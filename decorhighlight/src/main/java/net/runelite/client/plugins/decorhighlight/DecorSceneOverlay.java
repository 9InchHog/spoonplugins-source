package net.runelite.client.plugins.decorhighlight;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class DecorSceneOverlay extends Overlay {
    private Client client;

    private DecorHighlightPlugin plugin;

    private DecorHighlightConfig config;

    @Inject
    public DecorSceneOverlay(Client client, DecorHighlightPlugin plugin, DecorHighlightConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPosition(OverlayPosition.DYNAMIC);
    }

    public Dimension render(Graphics2D g) {
        if (config.antiAlias()) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        g.setColor(Color.RED);
        for (GraphicsObject go : this.client.getGraphicsObjects()) {
            boolean debug = (this.client.isKeyPressed(86) && this.client.isKeyPressed(81));
            if (!debug && !this.plugin.graphicsObjectWhitelist.contains(go.getId()))
                continue;
            String str = null;
            if (debug)
                str = Integer.toString(go.getId());
            LocalPoint lp = go.getLocation();
            int i = (lp.getX() - 64) / 128;
            int y = (lp.getY() - 64) / 128;
            tile(g, i, y, this.config.highlightColor(), str);
        }
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                Tile t = this.client.getScene().getTiles()[this.client.getPlane()][x][y];
                if (t != null) {
                    GroundObject decor = t.getGroundObject();
                    if (decor != null) {
                        boolean debug = (this.client.isKeyPressed(86) && this.client.isKeyPressed(81));
                        if (debug || this.plugin.groundDecorWhitelist.contains(decor.getId())) {
                            String str = null;
                            if (debug)
                                str = Integer.toString(decor.getId());
                            LocalPoint lp = decor.getLocalLocation();
                            int _x = (lp.getX() - 64) / 128;
                            int _y = (lp.getY() - 64) / 128;
                            tile(g, _x, _y, this.config.highlightColor(), str);
                        }
                    }
                }
            }
        }
        return null;
    }

    private void tile(Graphics g, int x, int y, Color c, String textLine1) {
        byte[][][] s = this.client.getTileSettings();
        int l = ((s[1][x][y] & 0x2) != 0) ? 1 : this.client.getPlane();
        int[][] h = this.client.getTileHeights()[l];
        g.setColor(c);
        line(g, x, y, x + 1, y, h);
        line(g, x, y, x, y + 1, h);
        line(g, x, y + 1, x + 1, y + 1, h);
        line(g, x + 1, y, x + 1, y + 1, h);
        int x0 = x, x1 = x + 1, y0 = y, y1 = y + 1;
        Point p0 = Perspective.localToCanvas(this.client, x0 << 7, y0 << 7, h[x0][y0]);
        Point p1 = Perspective.localToCanvas(this.client, x1 << 7, y0 << 7, h[x1][y0]);
        Point p3 = Perspective.localToCanvas(this.client, x0 << 7, y1 << 7, h[x0][y1]);
        Point p2 = Perspective.localToCanvas(this.client, x1 << 7, y1 << 7, h[x1][y1]);
        if (p0 != null && p1 != null && p3 != null && p2 != null) {
            int[] xPoints = { p0.getX(), p1.getX(), p2.getX(), p3.getX() };
            int[] yPoints = { p0.getY(), p1.getY(), p2.getY(), p3.getY() };
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 50));
            g.fillPolygon(xPoints, yPoints, 4);
        }
        if (textLine1 != null) {
            LocalPoint lp = new LocalPoint(x * 128 + 64, y * 128 + 64);
            Point p = Perspective.getCanvasTextLocation(this.client, (Graphics2D)g, lp,
                    textLine1, 0);
            if (p != null) {
                g.setColor(Color.BLACK);
                g.drawString(textLine1, p.getX() + 1, p.getY() + 1);
                g.setColor(Color.WHITE);
                g.drawString(textLine1, p.getX(), p.getY());
            }
        }
    }

    private void line(Graphics g, int x0, int y0, int x1, int y1, int[][] h) {
        Point p0 = Perspective.localToCanvas(this.client, x0 << 7, y0 << 7, h[x0][y0]);
        Point p1 = Perspective.localToCanvas(this.client, x1 << 7, y1 << 7, h[x1][y1]);
        if (p0 != null && p1 != null)
            g.drawLine(p0.getX(), p0.getY(), p1.getX(), p1.getY());
    }
}
