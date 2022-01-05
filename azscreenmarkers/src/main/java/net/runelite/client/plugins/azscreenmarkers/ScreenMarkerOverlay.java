package net.runelite.client.plugins.azscreenmarkers;

import lombok.NonNull;
import net.runelite.api.*;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import java.awt.*;

public class ScreenMarkerOverlay extends Overlay {
    private azScreenMarkerPlugin client;

    private final ScreenMarker marker;

    private final ScreenMarkerRenderable screenMarkerRenderable;

    public void setClient(azScreenMarkerPlugin client) {
        this.client = client;
    }

    public ScreenMarker getMarker() {
        return this.marker;
    }

    ScreenMarkerOverlay(@NonNull ScreenMarker marker) {
        if (marker == null)
            throw new NullPointerException("marker is marked non-null but is null");
        this.marker = marker;
        this.screenMarkerRenderable = new ScreenMarkerRenderable();
        setPosition(OverlayPosition.DETACHED);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGH);
        setResizable(true);
        setMinimumSize(16);
        setResettable(false);
    }

    public String getName() {
        return "marker" + this.marker.getId();
    }

    public Dimension render(Graphics2D graphics) {
        if (!this.marker.isVisible())
            return null;
        if (this.client != null) {
            Player p = this.client.getClient().getLocalPlayer();
            if (p == null)
                return null;
            if (this.marker.getName().toLowerCase().startsWith("cox bank") && !containsChestObject("")) {
                return null;
            }
            if (this.marker.getName().toLowerCase().startsWith("cox board") && !containsBoardObject("")) {
                return null;
            }
        }
        Dimension preferredSize = getPreferredSize();
        if (preferredSize == null)
            return null;
        this.screenMarkerRenderable.setBorderThickness(this.marker.getBorderThickness());
        this.screenMarkerRenderable.setColor(this.marker.getColor());
        this.screenMarkerRenderable.setFill(this.marker.getFill());
        this.screenMarkerRenderable.setStroke(new BasicStroke(this.marker.getBorderThickness()));
        this.screenMarkerRenderable.setPreferredSize(preferredSize);
        return this.screenMarkerRenderable.render(graphics);
    }

    public boolean containsChestObject(String n) {
        Scene scene = this.client.getClient().getScene();
        Tile[][][] tiles = scene.getTiles();
        int z = this.client.getClient().getPlane();
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                Tile tile = tiles[z][x][y];
                if (tile != null) {
                    Player player = this.client.getClient().getLocalPlayer();
                    if (player != null) {
                        GameObject[] gameObjects = tile.getGameObjects();
                        DecorativeObject decorativeObjects = tile.getDecorativeObject();
                        if (gameObjects != null) {
                            for (GameObject gameObject : gameObjects) {
                                if (gameObject != null && gameObject.getWorldLocation().distanceTo(this.client.getClient().getLocalPlayer().getWorldLocation()) < this.client.getConfig().chestDist()) {
                                    if (this.client.getCoxIds().contains(gameObject.getId())) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean containsBoardObject(String n) {
        Scene scene = this.client.getClient().getScene();
        Tile[][][] tiles = scene.getTiles();
        int z = this.client.getClient().getPlane();
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                Tile tile = tiles[z][x][y];
                if (tile != null) {
                    Player player = this.client.getClient().getLocalPlayer();
                    if (player != null) {
                        DecorativeObject decorativeObjects = tile.getDecorativeObject();
                        if (decorativeObjects != null && decorativeObjects.getWorldLocation().distanceTo(this.client.getClient().getLocalPlayer().getWorldLocation()) < this.client.getConfig().boardDist()) {
                            if (this.client.getCoxBoardIds().contains(decorativeObjects.getId())) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
