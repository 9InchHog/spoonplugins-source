package net.runelite.client.plugins.tickdebug;

import com.google.inject.Provides;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
        name = "[D] Tick Debug",
        enabledByDefault = false,
        description = "Displays server cycle time in top-right corner"
)
public class TickDebugPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private TickDebugOverlay overlay;

    @Inject
    private TickDebugConfig config;

    private long last_tick_ns;

    public int last_tick_dur_ms;

    @Provides
    TickDebugConfig provideConfig(ConfigManager configManager){
        return configManager.getConfig(TickDebugConfig.class);
    }

    protected void startUp() throws Exception {
        overlayManager.add(overlay);
    }

    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onGameTick(GameTick e) {
        long time = System.nanoTime();
        last_tick_dur_ms = (int)((time - last_tick_ns) / 1000000L);
        last_tick_ns = time;
    }
}
