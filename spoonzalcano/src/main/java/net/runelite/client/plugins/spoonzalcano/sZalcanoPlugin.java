package net.runelite.client.plugins.spoonzalcano;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Zalcano",
        description = "All-in-one plugin for the Zalcano.",
        tags = {"Zalcano"},
        enabledByDefault = false
)
public class sZalcanoPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private sZalcanoOverlay overlay;

    @Inject
    private sZalcanoConfig config;

    @Inject
    private ChatMessageManager chatMessageManager;

    Map<GameObject, Tile> aqewsBeyblades = new HashMap<>();

    Map<GameObject, Tile> glowingRock = new HashMap<>();

    ArrayList<GraphicsObject> fallingRocks = new ArrayList<>();

    Map<Projectile, LocalPoint> golem = new HashMap<>();

    NPC golemNPC;

    protected int tickCounterForBeyblades;

    protected boolean safeOnRock = true;

    protected boolean correctRegion;

    protected ArrayList<Integer> regions = new ArrayList<>();

    protected final int CORRECT_REGION = 12126;

    private boolean mirrorMode;

    @Provides
    sZalcanoConfig getConfig(ConfigManager configManager) {
        return (sZalcanoConfig)configManager.getConfig(sZalcanoConfig.class);
    }

    protected void startUp() {
        reset();
        this.overlayManager.add(this.overlay);
    }

    protected void shutDown() {
        reset();
        this.overlayManager.remove(this.overlay);
    }

    private void reset() {
        this.safeOnRock = true;
        this.aqewsBeyblades.clear();
        this.glowingRock.clear();
        this.fallingRocks.clear();
        this.golem.clear();
        this.golemNPC = null;
        this.correctRegion = false;
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved event) {
        if (event.getProjectile().getId() == 1729) {
            this.golem.put(event.getProjectile(), event.getPosition());
            if (this.config.golem())
                this.chatMessageManager.queue(QueuedMessage.builder()
                        .type(ChatMessageType.FRIENDSCHATNOTIFICATION)
                        .runeLiteFormattedMessage((new ChatMessageBuilder())
                                .append(ChatColorType.HIGHLIGHT)
                                .append("~~~!!!GOLEM SPAWNING!!!~~~")
                                .build())
                        .build());
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState currentState = event.getGameState();
        if (currentState.equals(GameState.CONNECTION_LOST) || currentState.equals(GameState.HOPPING) || currentState.equals(GameState.LOGGING_IN))
            reset();
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        /*if (client.isMirrored() && !mirrorMode) {
            overlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(overlay);
            overlayManager.add(overlay);
            mirrorMode = true;
        }*/

        this.safeOnRock = true;
        this.regions.clear();
        this.fallingRocks.clear();
        for (int x : this.client.getMapRegions())
            this.regions.add(x);
        if (this.regions.contains(12126)) {
            this.correctRegion = true;
        } else {
            this.correctRegion = false;
            this.aqewsBeyblades.clear();
            this.glowingRock.clear();
        }
        for (NPC n : this.client.getNpcs()) {
            if (this.correctRegion &&
                    n.getName().toLowerCase().contains("golem"))
                this.golem.clear();
        }
        for (GraphicsObject obj : this.client.getGraphicsObjects()) {
            if (obj.getId() == 1727)
                this.fallingRocks.add(obj);
        }
        for (Projectile p : this.client.getProjectiles()) {
            if (p.getId() == 1728)
                this.safeOnRock = false;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        this.tickCounterForBeyblades--;
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        if (event.getGameObject().getId() == 36199 || event.getGameObject().getId() == 36200) {
            this.tickCounterForBeyblades = 24;
            this.aqewsBeyblades.put(event.getGameObject(), event.getTile());
        }
        if (event.getGameObject().getId() == 36192) {
            this.glowingRock.clear();
            this.glowingRock.put(event.getGameObject(), event.getTile());
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        if (event.getGameObject().getId() == 36199 || event.getGameObject().getId() == 36200)
            this.aqewsBeyblades.remove(event.getGameObject());
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        if (event.getNpc() != null &&
                this.correctRegion &&
                event.getNpc().getName().toLowerCase().contains("golem")) {
            this.golemNPC = event.getNpc();
            this.golem.clear();
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        if (this.correctRegion &&
                event.getNpc().getName().toLowerCase().contains("golem"))
            this.golemNPC = null;
    }
}
