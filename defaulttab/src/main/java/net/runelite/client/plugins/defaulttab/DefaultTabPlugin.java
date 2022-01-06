package net.runelite.client.plugins.defaulttab;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
        name = "[S] Default Tab",
        description = "Set a default tab for post hopping",
        tags = {"default", "tab", "hop", "hopping", "big mfkn tyler"},
        enabledByDefault = false
)
public class DefaultTabPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private DefaultTabConfig config;

    private static final int TAB_SWITCH_SCRIPT = 915;

    @Provides
    DefaultTabConfig provideConfig(ConfigManager configManager) {
        return (DefaultTabConfig)configManager.getConfig(DefaultTabConfig.class);
    }

    private boolean pushTab = false;

    protected void shutDown() {
        pushTab = false;
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() == GameState.HOPPING)
            pushTab = true;
    }

    @Subscribe
    private void onGameTick(GameTick e) {
        if (!pushTab || client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
            return;
        client.runScript(915, config.getDefaultTab().getIndex());
        pushTab = false;
    }
}
