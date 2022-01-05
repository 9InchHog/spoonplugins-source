package net.runelite.client.plugins.spoonslayer;

import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class TargetMinimapOverlay extends Overlay {
    private final SpoonSlayerConfig config;

    private final SpoonSlayerPlugin plugin;

    @Inject
    TargetMinimapOverlay(SpoonSlayerConfig config, SpoonSlayerPlugin plugin) {
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    public Dimension render(Graphics2D graphics) {
        if (this.config.highlightTargets() == SpoonSlayerConfig.TileMode.OFF)
            return null;
        List<NPC> targets = this.plugin.getTargets();
        for (NPC target : targets)
            renderTargetOverlay(graphics, target, this.config.getTargetColor());
        return null;
    }

    private void renderTargetOverlay(Graphics2D graphics, NPC actor, Color color) {
        Point minimapLocation = actor.getMinimapLocation();
        if (minimapLocation != null)
            OverlayUtil.renderMinimapLocation(graphics, minimapLocation, color);
    }
}
