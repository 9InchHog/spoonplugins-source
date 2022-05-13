package net.runelite.client.plugins.hideunder;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
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
        name = "Hide Under",
        enabledByDefault = false,
        description = "Hide local player when under targeted players",
        tags = {"hide", "local", "player", "under"}
)
public class HideUnderPlugin extends Plugin
{
    @Inject
    private Client client;
    @Inject HideUnderConfig config;

    @Provides
    HideUnderConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(HideUnderConfig.class);
    }


    @Override
    protected void shutDown()
    {
        client.setLocalPlayerHidden(false);
        client.setRenderSelf(true);
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOGGED_IN)
        {
            client.setIsHidingEntities(true);
        }
    }

    @Subscribe
    private void onGameTick(GameTick event)
    {
        if (client.getLocalPlayer() == null)
        {
            return;
        }

        final WorldPoint localPlayerWp = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
        final WorldPoint lp = client.getLocalPlayer().getWorldLocation();
        boolean hide = false;
        for (Player player : client.getPlayers())
        {
            if (player == client.getLocalPlayer())
            {
                continue;
            }
            if (client.getVarbitValue(5314) == 1)
            {
                final WorldPoint playerWp = WorldPoint.fromLocalInstance(client, player.getLocalLocation());
                if (localPlayerWp != null && localPlayerWp.distanceTo(playerWp) == 0)
                {
                    hide = true;
                }
                continue;
            }

            if (lp != null && player.getWorldLocation().distanceTo(lp) == 0)
            {
                hide = true;
            }
        }
        if (config.renderMethod() == HideUnderConfig.hideUnderEnum.RENDER_SELF)
        {
            client.setRenderSelf(!hide);
        }
        else if (config.renderMethod() == HideUnderConfig.hideUnderEnum.ENTITY_HIDER)
        {
            client.setLocalPlayerHidden(hide);
        }
    }
}
