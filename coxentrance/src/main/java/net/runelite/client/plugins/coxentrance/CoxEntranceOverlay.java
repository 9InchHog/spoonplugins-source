package net.runelite.client.plugins.coxentrance;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class CoxEntranceOverlay extends Overlay {
    private Client client;

    @Inject
    public CoxEntranceOverlay(Client client) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGHEST);
        this.client = client;
    }

    public Dimension render(Graphics2D g) {
        int level = 0, x = 1232, y = 3573;
        int sceneX = 1232 - this.client.getBaseX(), sceneY = 3573 - this.client.getBaseY();
        if (sceneX >= 0 && sceneY >= 0 && sceneX < 104 && sceneY < 104) {
            Tile t = this.client.getScene().getTiles()[0][sceneX][sceneY];
            if (t.getGameObjects()[0] != null) {
                GameObject go = t.getGameObjects()[0];
                if (go.getId() == 29777) {
                    Color c = null;
                    if (this.client.getFriendsChatManager() == null) {
                        c = new Color(255, 0, 0, 50);
                    } else if (this.client.getVarpValue(1427) == -1) {
                        c = new Color(200, 200, 0, 50);
                    }
                    Shape s = go.getClickbox();
                    if (c != null && s != null) {
                        Point mousePos = this.client.getMouseCanvasPosition();
                        if (mousePos != null && s.contains(mousePos.getX(), mousePos.getY()))
                            c = c.darker();
                        g.setColor(c);
                        g.fill(s);
                        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue()));
                        g.draw(s);
                    }
                }
            }
        }
        return null;
    }
}
