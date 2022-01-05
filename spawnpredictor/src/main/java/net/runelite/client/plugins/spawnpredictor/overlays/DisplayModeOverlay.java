package net.runelite.client.plugins.spawnpredictor.overlays;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.spawnpredictor.SpawnPredictorConfig;
import net.runelite.client.plugins.spawnpredictor.SpawnPredictorPlugin;
import net.runelite.client.plugins.spawnpredictor.util.FightCavesNpc;
import net.runelite.client.plugins.spawnpredictor.util.FightCavesNpcSpawn;
import net.runelite.client.plugins.spawnpredictor.util.SpawnLocations;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisplayModeOverlay extends Overlay {
    @Inject
    private DisplayModeOverlay(Client client, SpawnPredictorPlugin plugin, SpawnPredictorConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        if (!this.plugin.isFightCavesActive() || SpawnPredictorPlugin.getCurrentWave() <= 0)
            return null;
        if (this.config.displayMode() == SpawnPredictorConfig.DisplayMode.OFF)
            return null;
        int currentWave = SpawnPredictorPlugin.getCurrentWave() - 1;
        List<FightCavesNpcSpawn> currentWaveContents = SpawnPredictorPlugin.getWaveData().get(currentWave);
        List<FightCavesNpcSpawn> nextWaveContents = SpawnPredictorPlugin.getWaveData().get(currentWave + 1);
        switch (this.config.displayMode()) {
            case CURRENT_WAVE:
                displayWaveContents(graphics, currentWaveContents, this.config.currentWaveColor());
                return null;
            case NEXT_WAVE:
                if (currentWave == 0)
                    displayWaveContents(graphics, currentWaveContents, this.config.currentWaveColor());
                if (currentWave != 63)
                    displayWaveContents(graphics, nextWaveContents, this.config.nextWaveColor());
                return null;
            case BOTH:
                displayWaveContents(graphics, currentWaveContents, this.config.currentWaveColor());
                if (currentWave == 0)
                    displayWaveContents(graphics, currentWaveContents, this.config.currentWaveColor());
                if (currentWave != 63)
                    displayWaveContents(graphics, nextWaveContents, this.config.nextWaveColor());
                return null;
        }
        throw new IllegalStateException("Illegal 'Display Mode' config state... How did this happen? Who knows");
    }

    private void displayWaveContents(Graphics2D graphics, List<FightCavesNpcSpawn> waveContents, Color color) {
        if (!this.plugin.isFightCavesActive())
            return;
        for (FightCavesNpcSpawn fcNpc : waveContents) {
            FightCavesNpc npc = fcNpc.getNpc();
            int sVal = fcNpc.getSpawnLocation();
            int[] spawnLocation = ((SpawnLocations)Objects.<SpawnLocations>requireNonNull(SpawnLocations.lookup(sVal))).getRegionXY();
            LocalPoint localPoint = getLocalPointFromRegionCords(spawnLocation);
            renderOverlays(graphics, localPoint, npc.getSize(), color, npc.getName());
        }
    }

    private void renderOverlays(Graphics2D graphics, LocalPoint localPoint, int size, Color color, String name) {
        if (localPoint == null)
            return;
        LocalPoint localLocation = getLocalPointFromSWTile(localPoint, size);
        Polygon tileAreaPoly = Perspective.getCanvasTileAreaPoly(this.client, localLocation, size);
        renderPolygon(graphics, tileAreaPoly, this.config.overlayStrokeSize(), color);
        Point textPoint = Perspective.getCanvasTextLocation(this.client, graphics, localLocation, name, 0);
        if (textPoint != null)
            OverlayUtil.renderTextLocation(graphics, textPoint, name, this.config.multicolorNames() ? color : Color.WHITE);
    }

    private LocalPoint getLocalPointFromRegionCords(int... regionXY) {
        Player p = this.client.getLocalPlayer();
        if (p == null)
            return null;
        WorldPoint pwp = p.getWorldLocation();
        WorldPoint wp = WorldPoint.fromRegion(pwp.getRegionID(), regionXY[0], regionXY[1], this.client.getPlane());
        return LocalPoint.fromWorld(this.client, wp);
    }

    private LocalPoint getLocalPointFromSWTile(LocalPoint localPoint, int size) {
        int x = localPoint.getX();
        int y = localPoint.getY();
        int newSize = size - 1;
        int tileSize = 64;
        return new LocalPoint(x + newSize * 64, y + newSize * 64);
    }

    private static void renderPolygon(Graphics2D graphics, Shape poly, int stroke, Color color) {
        if (poly == null)
            return;
        graphics.setColor(color);
        Stroke originalStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(stroke));
        graphics.draw(poly);
        graphics.setColor(new Color(0, 0, 0, 50));
        graphics.fill(poly);
        graphics.setStroke(originalStroke);
    }

    private static final Logger log = LoggerFactory.getLogger(DisplayModeOverlay.class);

    private final Client client;

    private final SpawnPredictorPlugin plugin;

    private final SpawnPredictorConfig config;
}
