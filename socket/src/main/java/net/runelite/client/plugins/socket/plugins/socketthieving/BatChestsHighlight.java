package net.runelite.client.plugins.socket.plugins.socketthieving;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Tile;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class BatChestsHighlight extends Overlay {
    private Client client;

    private SocketThievingPlugin plugin;

    private SocketThievingConfig config;

    @Inject
    public BatChestsHighlight(Client client, SocketThievingPlugin plugin, SocketThievingConfig config) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D g) {
        if (!this.config.highlightBatChests())
            return null;
        byte[][] solns = BatData.CHEST_SOLNS[this.plugin.wind][this.plugin.rot];
        byte[][] locs = BatData.CHEST_LOCS[this.plugin.wind];
        float[] cols = new float[3];
        int[] colors = new int[10];
        if (this.config.gumdropFactor() != 0) {
            float factor = this.config.gumdropFactor() / 20.0F;
            Color.RGBtoHSB(255, 255, 0, cols);
            cols[2] = 0.9F;
            colors[0] = Color.HSBtoRGB(cols[0] + factor * -1.0F, cols[1], cols[2]);
            colors[1] = Color.HSBtoRGB(cols[0] + factor * -0.8F, cols[1], cols[2]);
            colors[2] = Color.HSBtoRGB(cols[0] + factor * -0.6F, cols[1], cols[2]);
            colors[3] = Color.HSBtoRGB(cols[0] + factor * -0.4F, cols[1], cols[2]);
            colors[4] = Color.HSBtoRGB(cols[0] + factor * -0.2F, cols[1], cols[2]);
            colors[5] = 16777215;
            colors[6] = Color.HSBtoRGB(cols[0] + factor * 0.2F, cols[1], cols[2]);
            colors[7] = Color.HSBtoRGB(cols[0] + factor * 0.4F, cols[1], cols[2]);
            colors[8] = Color.HSBtoRGB(cols[0] + factor * 0.6F, cols[1], cols[2]);
            colors[9] = Color.HSBtoRGB(cols[0] + factor * 0.8F, cols[1], cols[2]);
        } else {
            Arrays.fill(colors, 16776960);
        }
        if (this.plugin.soln == -1) {
            byte n;
            for (n = 0; n < solns.length; n = (byte)(n + 1)) {
                if (!this.plugin.not_solns.contains(Byte.valueOf(n))) {
                    Color col = new Color(colors[n] & 0xFFFFFF | 0x50000000, true);
                    for (int i = 0; i < 4; i++) {
                        Tile t = findChest(locs[solns[n][i] - 1], this.plugin.rot);
                        if (t != null)
                            highlightChest(t, col, g);
                    }
                }
            }
        } else {
            byte[] b = solns[this.plugin.soln];
            for (int j = 0; j < b.length; j++) {
                Tile t2 = findChest(locs[b[j] - 1], this.plugin.rot);
                if (t2 != null)
                    highlightChest(t2, new Color(0, 255, 0, 50), g);
            }
        }
        return null;
    }

    private void highlightChest(Tile t, Color c, Graphics2D g) {
        GameObject chest = t.getGameObjects()[0];
        if (chest == null)
            return;
        if (chest.getId() == 29742 || chest.getId() == 29743) {
            g.setColor(c);
            if (chest.getCanvasLocation() != null)
                g.fill(chest.getConvexHull());
        }
    }

    private Tile findChest(byte[] coords, int rot) {
        int rx = coords[0];
        int ry = coords[1];
        int chestX = this.plugin.room_base_x;
        int chestY = this.plugin.room_base_y;
        if (rot == 0) {
            chestX += rx;
            chestY += ry;
        } else if (rot == 1) {
            chestX += ry;
            chestY -= rx;
        } else if (rot == 2) {
            chestX -= rx;
            chestY -= ry;
        } else {
            chestX -= ry;
            chestY += rx;
        }
        if (chestX < 0 || chestY < 0 || chestX >= 104 || chestY >= 104)
            return null;
        return this.client.getScene().getTiles()[this.client.getPlane()][chestX][chestY];
    }
}
