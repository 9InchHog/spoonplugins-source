package net.runelite.client.plugins.bingo;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Bingo",
        description = "Steroids made this- not me. Bingo code overlay.",
        tags = {"bingo"},
        enabledByDefault = false
)
public class BingoPlugin extends Plugin {
    @Inject
    private BingoOverlay overlay;

    @Inject
    private Client client;

    @Inject
    private BingoConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Provides
    BingoConfig provideConfig(ConfigManager configManager) {
        return (BingoConfig)configManager.getConfig(BingoConfig.class);
    }

    protected void startUp() {
        overlayManager.add(overlay);
    }

    protected void shutDown() {
        overlayManager.remove(overlay);
    }
}
