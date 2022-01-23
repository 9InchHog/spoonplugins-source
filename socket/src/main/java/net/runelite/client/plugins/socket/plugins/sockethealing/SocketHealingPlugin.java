package net.runelite.client.plugins.socket.plugins.sockethealing;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.socket.SocketPlugin;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
import net.runelite.client.plugins.socket.packet.SocketPlayerLeave;
import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

@Extension
@PluginDescriptor(
        name = "Socket - Healing",
        description = "Displays health overlays for socket party members. <br> Created by: A wild animal with a keyboard <br> Modified by: SpoonLite",
        enabledByDefault = false
)
@PluginDependency(SocketPlugin.class)
public class SocketHealingPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SocketHealingOverlay socketHealingOverlay;

    @Inject
    private SocketHealingConfig config;

    @Inject
    private SocketPlugin socketPlugin;

    @Inject
    private ClientThread clientThread;

    @Inject
    private EventBus eventBus;

    @Inject
    private ChatMessageManager chatMessageManager;

    private Map<String, SocketHealingPlayer> partyMembers = new TreeMap<>();

    private int lastRefresh;

    public ArrayList<String> playerNames = new ArrayList<String>();

    private boolean mirrorMode;

    @Provides
    SocketHealingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SocketHealingConfig.class);
    }

    protected void startUp() {
        overlayManager.add(socketHealingOverlay);
        lastRefresh = 0;
        synchronized (partyMembers) {
            partyMembers.clear();
        }
    }

    protected void shutDown() {
        overlayManager.remove(socketHealingOverlay);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged e) {
        if(e.getGroup().equals("sockethealing")) {
            if (!config.hpPlayerNames().equals("")) {
                playerNames.clear();
                byte b;
                int i;
                String[] arrayOfString;
                for (i = (arrayOfString = config.hpPlayerNames().split(",")).length, b = 0; b < i; ) {
                    String str = arrayOfString[b];
                    str = str.trim();
                    if (!"".equals(str))
                        playerNames.add(str.toLowerCase());
                    b++;
                }
            }

            if (e.getKey().equals("setHighestPriority")) {
                socketHealingOverlay.setLayer(config.setHighestPriority() ? OverlayLayer.ABOVE_WIDGETS : OverlayLayer.ABOVE_SCENE);
                ChatMessageBuilder message = (new ChatMessageBuilder()).append(Color.MAGENTA, "Re-load the plugin to change overlay layer!");
                this.chatMessageManager.queue(QueuedMessage.builder()
                        .type(ChatMessageType.ITEM_EXAMINE)
                        .runeLiteFormattedMessage(message.build())
                        .build());
            }
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING)
            synchronized (partyMembers) {
                partyMembers.clear();
            }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() == GameState.LOGGED_IN) {
            SocketHealingPlayer playerHealth;
            int currentHealth = client.getBoostedSkillLevel(Skill.HITPOINTS);
            String name = client.getLocalPlayer().getName();
            synchronized (partyMembers) {
                playerHealth = partyMembers.get(name);
                if (playerHealth == null) {
                    playerHealth = new SocketHealingPlayer(name, currentHealth);
                    partyMembers.put(name, playerHealth);
                } else {
                    playerHealth.setHealth(currentHealth);
                }
            }
            lastRefresh++;
            if (lastRefresh >= Math.max(1, config.refreshRate())) {
                JSONObject packet = new JSONObject();
                packet.put("name", name);
                packet.put("player-health", playerHealth.toJSON());
                eventBus.post(new SocketBroadcastPacket(packet));
                lastRefresh = 0;
            }
        }
    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event) {
        try {
            JSONObject payload = event.getPayload();
            String localName = client.getLocalPlayer().getName();
            if (payload.has("player-health")) {
                String targetName = payload.getString("name");
                if (targetName.equals(localName))
                    return;
                JSONObject statusJSON = payload.getJSONObject("player-health");
                synchronized (partyMembers) {
                    SocketHealingPlayer playerHealth = partyMembers.get(targetName);
                    if (playerHealth == null) {
                        playerHealth = SocketHealingPlayer.fromJSON(statusJSON);
                        partyMembers.put(targetName, playerHealth);
                    } else {
                        playerHealth.parseJSON(statusJSON);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Subscribe
    public void onSocketPlayerLeave(SocketPlayerLeave event) {
        String target = event.getPlayerName();
        synchronized (partyMembers) {
            if (partyMembers.containsKey(target))
                partyMembers.remove(target);
        }
    }

    public Map<String, SocketHealingPlayer> getPartyMembers() {
        return partyMembers;
    }

    /*@Subscribe
    private void onClientTick(ClientTick event) {
        if (client.isMirrored() && !mirrorMode) {
            socketHealingOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(socketHealingOverlay);
            overlayManager.add(socketHealingOverlay);
            mirrorMode = true;
        }
    }*/

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (config.hpMenu()) {
            int type = event.getType();
            if (type >= 2000)
                type -= 2000;
            Color color = Color.GREEN;
            String target = event.getTarget().replaceAll("[^A-Za-z0-9-()<>=]", " ");
            for (String playerName : getPartyMembers().keySet()) {
                if (Text.removeTags(target).toLowerCase().contains(playerName.toLowerCase() + "  (level-")) {
                    SocketHealingPlayer player = getPartyMembers().get(playerName);
                    MenuEntry[] menuEntries = client.getMenuEntries();
                    MenuEntry menuEntry = menuEntries[menuEntries.length - 1];

                    int playerHealth = player.getHealth();
                    if (playerHealth > config.greenZone())
                        color = config.greenZoneColor();
                    if (playerHealth <= config.greenZone() && playerHealth > config.orangeZone())
                        color = config.orangeZoneColor();
                    if (playerHealth <= config.orangeZone())
                        color = config.redZoneColor();
                    String hpAdded = ColorUtil.prependColorTag(" - " + playerHealth, color);
                    menuEntry.setTarget(event.getTarget() + hpAdded);
                    client.setMenuEntries(menuEntries);
                }
            }
        }
    }
}
