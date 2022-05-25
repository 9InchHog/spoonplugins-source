package net.runelite.client.plugins.socket.plugins.socketping;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;

import java.awt.*;
import java.io.BufferedInputStream;
import java.util.Random;
import javax.inject.Inject;
import javax.sound.sampled.*;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.socket.SocketPlugin;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
import net.runelite.client.plugins.socket.plugins.socketping.data.Ping;
import net.runelite.client.plugins.socket.plugins.socketping.data.PingGameObject;
import net.runelite.client.plugins.socket.plugins.socketping.data.PingNPC;
import net.runelite.client.plugins.socket.plugins.socketping.data.PingPlayer;
import net.runelite.client.plugins.socket.plugins.socketping.data.PingTile;
import net.runelite.client.plugins.socket.plugins.socketping.packets.SocketPingGameObjectPacket;
import net.runelite.client.plugins.socket.plugins.socketping.packets.SocketPingNPCPacket;
import net.runelite.client.plugins.socket.plugins.socketping.packets.SocketPingPlayerPacket;
import net.runelite.client.plugins.socket.plugins.socketping.packets.SocketPingTilePacket;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.RaveUtils;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
@PluginDescriptor(
        name = "Socket - Ping",
        description = "Marks objects, NPCs, and tiles to players in socket",
        tags = {"socket", "server", "discord", "connection", "broadcast", "ping"},
        enabledByDefault = false
)
@PluginDependency(SocketPlugin.class)
public class SocketPing extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(SocketPing.class);

    @Inject
    private SocketPingConfig config;

    @Inject
    private KeyManager keyManager;

    @Inject
    private MouseManager mouseManager;

    @Inject
    private SocketPingKeyListener socketPingKeyListener;

    @Inject
    private SocketPingMouseListener socketPingMouseListener;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SocketPingOverlay socketPingOverlay;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Getter
    private MenuOptionClicked lastMenuOptionClicked;

    @Getter
    @Setter
    private Tile lastSelectedSceneTile;

    @Inject
    private EventBus eventBus;

    @Inject
    private RaveUtils raveUtils;

    @Provides
    SocketPingConfig getConfig(ConfigManager configManager) {
        return (SocketPingConfig)configManager.getConfig(SocketPingConfig.class);
    }

    private final ImmutableSet<Integer> ACCEPTED_TILE_GAMEOBJECT_MENU_OPTION_IDS = ImmutableSet.of(
            MenuAction.GAME_OBJECT_FIRST_OPTION.getId(),
            MenuAction.GAME_OBJECT_SECOND_OPTION.getId(),
            MenuAction.GAME_OBJECT_THIRD_OPTION.getId(),
            MenuAction.GAME_OBJECT_FOURTH_OPTION.getId(),
            MenuAction.GAME_OBJECT_FIFTH_OPTION.getId(),
            MenuAction.EXAMINE_OBJECT.getId());

    private final ImmutableSet<Integer> ACCEPTED_TILE_NPCS_MENU_OPTION_IDS = ImmutableSet.of(
            MenuAction.NPC_FIRST_OPTION.getId(),
            MenuAction.NPC_SECOND_OPTION.getId(),
            MenuAction.NPC_THIRD_OPTION.getId(),
            MenuAction.NPC_FOURTH_OPTION.getId(),
            MenuAction.NPC_FIFTH_OPTION.getId(),
            MenuAction.EXAMINE_NPC.getId());

    private final ImmutableSet<Integer> ACCEPTED_TILE_PLAYER_MENU_OPTION_IDS = ImmutableSet.of(
            MenuAction.PLAYER_FIRST_OPTION.getId(),
            MenuAction.PLAYER_SECOND_OPTION.getId(),
            MenuAction.PLAYER_THIRD_OPTION.getId(),
            MenuAction.PLAYER_FOURTH_OPTION.getId(),
            MenuAction.PLAYER_FIFTH_OPTION.getId(),
            MenuAction.PLAYER_SIXTH_OPTION.getId(),
            MenuAction.PLAYER_SEVENTH_OPTION.getId(),
            MenuAction.PLAYER_EIGTH_OPTION.getId());

    public int pingTicks = 0;
    public Color raveColor = Color.BLUE;
    private static Clip clip;

    protected void startUp() {
        keyManager.registerKeyListener(socketPingKeyListener);
        mouseManager.registerMouseListener(socketPingMouseListener);
        overlayManager.add(socketPingOverlay);
        pingTicks = 0;
    }

    protected void shutDown() {
        keyManager.unregisterKeyListener(socketPingKeyListener);
        mouseManager.unregisterMouseListener(socketPingMouseListener);
        overlayManager.remove(socketPingOverlay);
        pingTicks = 0;
    }

    @Subscribe
    protected void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("socketping") && event.getKey().equals("pingSoundVolume")) {
            if (clip != null) {
                FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                if (control != null) {
                    control.setValue((float) (config.pingSoundVolume() / 2 - 45));
                }
            }
        }
    }

    @Subscribe
    protected void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
        if (socketPingOverlay.isHotkeyPressed()) {
            lastMenuOptionClicked = menuOptionClicked;
            lastSelectedSceneTile = client.getSelectedSceneTile();
            menuOptionClicked.consume();
        }
    }

    public void ping(PingType pingType) {
        clientThread.invokeLater(() -> {
            Object object = getLastClickTarget();
            if (object != null) {
                if (object instanceof Integer) {
                    PingPlayer pingPlayer = new PingPlayer((Integer) object, pingType, client.getWorld());
                    SocketPingPlayerPacket packet = new SocketPingPlayerPacket(pingPlayer);
                    eventBus.post(new SocketBroadcastPacket(packet));
                }
                if (object instanceof NPC) {
                    PingNPC pingNPC = new PingNPC(((NPC)object).getIndex(), pingType, client.getWorld());
                    SocketPingNPCPacket packet = new SocketPingNPCPacket(pingNPC);
                    eventBus.post(new SocketBroadcastPacket(packet));
                }
                if (object instanceof GameObject) {
                    PingGameObject pingGameObject = new PingGameObject(((GameObject)object).getId(), ((GameObject)object).getWorldLocation(), pingType, client.getWorld());
                    SocketPingGameObjectPacket packet = new SocketPingGameObjectPacket(pingGameObject);
                    eventBus.post(new SocketBroadcastPacket(packet));
                }
            } else {
                WorldPoint worldPoint = getLastClickTileTarget();
                if (worldPoint != null) {
                    PingTile pingTile = new PingTile(worldPoint, pingType, client.getWorld());
                    SocketPingTilePacket packet = new SocketPingTilePacket(pingTile);
                    eventBus.post(new SocketBroadcastPacket(packet));
                }
            }

            if(config.pingSound()) {
                try {
                    AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(SocketPing.class.getResourceAsStream(pingType.type + ".wav")));
                    AudioFormat format = stream.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    clip = (Clip)AudioSystem.getLine(info);
                    clip.open(stream);
                    FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null)
                        control.setValue((float)(config.pingSoundVolume() / 2 - 45));
                    clip.setFramePosition(0);
                    clip.start();
                } catch (Exception var6) {
                    clip = null;
                }
            }
            pingTicks = 5;
        });
    }

    public WorldPoint getLastClickTileTarget() {
        if (lastMenuOptionClicked.getMenuAction().getId() == MenuAction.WALK.getId()) {
            Tile selectedSceneTile = lastSelectedSceneTile;
            if (selectedSceneTile == null) {
                selectedSceneTile = client.getSelectedSceneTile();
                if (selectedSceneTile == null)
                    return null;
            }
            boolean isOnCanvas = false;
            for (MenuEntry menuEntry : client.getMenuEntries()) {
                if (menuEntry != null)
                    if ("walk here".equalsIgnoreCase(menuEntry.getOption()))
                        isOnCanvas = true;
            }
            if (!isOnCanvas)
                return null;
            return selectedSceneTile.getWorldLocation();
        }
        return null;
    }

    public Object getLastClickTarget() {
        if (ACCEPTED_TILE_GAMEOBJECT_MENU_OPTION_IDS.contains(lastMenuOptionClicked.getMenuAction().getId())) {
            int x = lastMenuOptionClicked.getParam0();
            int y = lastMenuOptionClicked.getParam1();
            int z = client.getPlane();
            Tile tile = client.getScene().getTiles()[z][x][y];
            return findGameObject(tile, lastMenuOptionClicked.getId());
        }
        if (ACCEPTED_TILE_NPCS_MENU_OPTION_IDS.contains(lastMenuOptionClicked.getMenuAction().getId()))
            return client.getCachedNPCs()[lastMenuOptionClicked.getId()];
        if (ACCEPTED_TILE_PLAYER_MENU_OPTION_IDS.contains(lastMenuOptionClicked.getMenuAction().getId()))
            return lastMenuOptionClicked.getId();
        return null;
    }

    private GameObject findGameObject(Tile tile, int id) {
        if (tile == null)
            return null;
        GameObject[] tileGameObjects = tile.getGameObjects();
        for (GameObject object : tileGameObjects) {
            if (objectIdEquals(object, id))
                return object;
        }
        return null;
    }

    private boolean objectIdEquals(TileObject tileObject, int id) {
        if (tileObject == null)
            return false;
        if (tileObject.getId() == id)
            return true;
        ObjectComposition comp = client.getObjectDefinition(tileObject.getId());
        if (comp.getImpostorIds() != null)
            for (int impostorId : comp.getImpostorIds()) {
                if (impostorId == id)
                    return true;
            }
        return false;
    }

    public int decayTicks(PingType pingType) {
        switch (pingType) {
            case OMW:
                return config.pingDecayOmw();
            case WARN:
                return config.pingDecayWarn();
            case TARGET:
                return config.pingDecayTarget();
            case ASSIST_ME:
                return config.pingDecayAssistMe();
            case QUESTION_MARK:
                return config.pingDecayQuestionMark();
        }
        return 0;
    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event) {
        PingTile pingTile;
        PingNPC pingNPC;
        PingPlayer pingPlayer;
        PingGameObject pingGameObject;
        JSONObject object = event.getPayload();
        PingTargetType pingTargetType = Ping.tryParseType(object);
        if (pingTargetType == null)
            return;
        switch (pingTargetType) {
            case TILE:
                pingTile = PingTile.tryParse(object);
                if (pingTile != null)
                    (SocketPingOverlay.getHighlightedObjects().get(pingTile.getPingType())).put(pingTile.getWorldPoint(), decayTicks(pingTile.getPingType()));
                break;
            case NPC:
                pingNPC = PingNPC.tryParse(object);
                if (pingNPC != null && pingNPC.getWorld() == client.getWorld()) {
                    NPC npc = client.getCachedNPCs()[pingNPC.getIndex()];
                    if (npc != null)
                        (SocketPingOverlay.getHighlightedObjects().get(pingNPC.getPingType())).put(npc, decayTicks(pingNPC.getPingType()));
                }
                break;
            case PLAYER:
                pingPlayer = PingPlayer.tryParse(object);
                if (pingPlayer != null && pingPlayer.getWorld() == client.getWorld()) {
                    Player player = client.getCachedPlayers()[pingPlayer.getIndex()];
                    if (player != null)
                        (SocketPingOverlay.getHighlightedObjects().get(pingPlayer.getPingType())).put(player, decayTicks(pingPlayer.getPingType()));
                }
                break;
            case GAMEOBJECT:
                pingGameObject = PingGameObject.tryParse(object);
                if (pingGameObject != null && pingGameObject.getWorld() == client.getWorld())
                    if (pingGameObject.getWorldPoint().isInScene(client)) {
                        int x = pingGameObject.getWorldPoint().getX() - client.getBaseX();
                        int y = pingGameObject.getWorldPoint().getY() - client.getBaseY();
                        int z = pingGameObject.getWorldPoint().getPlane();
                        Tile tile = client.getScene().getTiles()[z][x][y];
                        clientThread.invokeLater(() -> {
                            GameObject gameObject = findGameObject(tile, pingGameObject.getId());
                            if (gameObject != null)
                                (SocketPingOverlay.getHighlightedObjects().get(pingGameObject.getPingType())).put(gameObject, decayTicks(pingGameObject.getPingType()));
                        });
                    }
                break;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (config.raveWheel() == SocketPingConfig.RaveMode.RAVE) {
            raveColor = Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F);
        }

        if (pingTicks > 0)
            pingTicks--;
    }
}
