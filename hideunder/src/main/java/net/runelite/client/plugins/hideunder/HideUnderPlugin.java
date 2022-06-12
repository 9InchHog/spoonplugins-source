package net.runelite.client.plugins.hideunder;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
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
    @Inject
    private Hooks hooks;

    private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

    private boolean hideLocalPlayer;
    private boolean hideLocalPlayer2D;

    @Provides
    HideUnderConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(HideUnderConfig.class);
    }

    @Override
    protected void startUp()
    {
        updateConfig();
        hooks.registerRenderableDrawListener(drawListener);
    }

    @Override
    protected void shutDown()
    {
        updateConfig();
        hooks.unregisterRenderableDrawListener(drawListener);
        client.setRenderSelf(true);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals("hideunder"))
        {
            updateConfig();
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
            hideLocalPlayer = hide;
        }
    }

    @VisibleForTesting
    boolean shouldDraw(Renderable renderable, boolean drawingUI)
    {
        if (renderable instanceof Player)
        {
            Player player = (Player) renderable;
            Player local = client.getLocalPlayer();

            if (player.getName() == null)
            {
                // player.isFriend() and player.isFriendsChatMember() npe when the player has a null name
                return true;
            }

            // Allow hiding local self in pvp, which is an established meta.
            // It is more advantageous than renderself due to being able to still render local player 2d
            if (player == local)
            {
                return !(drawingUI ? hideLocalPlayer2D : hideLocalPlayer);
            }
        }
        return true;
    }

    private void updateConfig()
    {
        hideLocalPlayer2D = config.hideLocalPlayer2D();
    }
}
