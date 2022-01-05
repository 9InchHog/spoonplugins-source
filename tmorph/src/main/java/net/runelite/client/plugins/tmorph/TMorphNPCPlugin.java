package net.runelite.client.plugins.tmorph;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@Slf4j
@PluginDescriptor(
        name = "[S] Tmorph NPC",
        description = "Change your character model to an NPC model",
        enabledByDefault = false
)
public class TMorphNPCPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private TMorphNPCConfig config;
    @Inject
    private ConfigManager configManager;
    @Inject
    private ClientThread clientThread;

    public TMorphNPCPlugin() {
    }

    @Provides
    TMorphNPCConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TMorphNPCConfig.class);
    }

    @Override
    protected void startUp() {}

    @Override
    protected void shutDown() {
        Player localPlayer = client.getLocalPlayer();
        if (localPlayer != null && localPlayer.getPlayerComposition() != null)
        {
            localPlayer.getPlayerComposition().setTransformedNpcId(-1);
        }
    }

    @Subscribe
    protected void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("NPCTMorph")) {
            return;
        }

        if (event.getKey().equals("npcID")) {
            Player localPlayer = client.getLocalPlayer();
            if (localPlayer != null && localPlayer.getPlayerComposition() != null) {
                localPlayer.getPlayerComposition().setTransformedNpcId(config.npcID());
            }
        }
    }

    @Subscribe
    protected void onGameTick(GameTick gameTick) {
        if (client.getGameState() == GameState.LOGGED_IN && client.getLocalPlayer() != null && client.getLocalPlayer().getPlayerComposition() != null) {
            Player localPlayer = client.getLocalPlayer();
            if (localPlayer != null && localPlayer.getPlayerComposition() != null) {
                localPlayer.getPlayerComposition().setTransformedNpcId(config.npcID());
            }
        }
    }
}
