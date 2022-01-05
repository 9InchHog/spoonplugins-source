package net.runelite.client.plugins.coxentrance;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(name = "[D] CoX Entrance")
public class CoxEntrancePlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private CoxEntranceOverlay overlay;

    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
    }

    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
    }
}
