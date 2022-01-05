package net.runelite.client.plugins.cclumphelper;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class cClumpHelperOverlay extends Overlay {
    private final cClumpHelperPlugin plugin;

    private final cClumpHelperConfig config;

    @Inject
    private cClumpHelperOverlay(cClumpHelperPlugin plugin, cClumpHelperConfig config) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.config = config;
    }

    public Dimension render(Graphics2D graphics) {
        return null;
    }
}
