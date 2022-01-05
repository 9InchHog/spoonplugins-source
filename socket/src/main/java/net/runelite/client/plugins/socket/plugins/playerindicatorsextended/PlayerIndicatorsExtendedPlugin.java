package net.runelite.client.plugins.socket.plugins.playerindicatorsextended;

import com.google.inject.Provides;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.socket.SocketPlugin;
import net.runelite.client.plugins.socket.packet.SocketMembersUpdate;
import net.runelite.client.plugins.socket.packet.SocketPlayerJoin;
import net.runelite.client.plugins.socket.packet.SocketShutdown;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Objects;

@Extension
@PluginDescriptor(
        name = "Socket - Player Indicator",
        description = "Shows you players who are in your socket",
        tags = {"indicator, socket, player, highlight"}
)
@PluginDependency(SocketPlugin.class)
public class PlayerIndicatorsExtendedPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(PlayerIndicatorsExtendedPlugin.class);

    @Inject
    private PlayerIndicatorsExtendedConfig config;

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private PlayerIndicatorsExtendedOverlay overlay;

    @Inject
    private PlayerIndicatorsExtendedMinimapOverlay overlayMinimap;

    @Inject
    private ChatIconManager chatIconManager;

    private ArrayList<Actor> players;

    private ArrayList<String> names;

    @Provides
    PlayerIndicatorsExtendedConfig getConfig(ConfigManager configManager) {
        return (PlayerIndicatorsExtendedConfig)configManager.getConfig(PlayerIndicatorsExtendedConfig.class);
    }

    public ArrayList<Actor> getPlayers() {
        return players;
    }

    int activeTick = 0;

    boolean cleared = false;

    protected void startUp() {
        overlayManager.add(overlay);
        overlayManager.add(overlayMinimap);
        players = new ArrayList<>();
        names = new ArrayList<>();
    }

    protected void shutDown() {
        overlayManager.remove(overlay);
        overlayManager.remove(overlayMinimap);
    }

    @Subscribe
    public void onSocketPlayerJoin(SocketPlayerJoin event) {
        names.add(event.getPlayerName());
        if (event.getPlayerName().equals(Objects.requireNonNull(client.getLocalPlayer()).getName()))
            names.clear();
    }

    @Subscribe
    public void onSocketMembersUpdate(SocketMembersUpdate event)
    {
        names.clear();
        for(String s : event.getMembers())
        {
            if(!s.equals(client.getLocalPlayer().getName()))
            {
                names.add(s);
            }
        }
    }

    @Subscribe
    public void onSocketShutdown(SocketShutdown event)
    {
        names.clear();
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        players.clear();
        loop: for(Player p : client.getPlayers())
        {
            for(String name : names)
            {
                if(name.equals(p.getName()))
                {
                    players.add(p);
                    continue loop;
                }
            }
        }
    }
}
