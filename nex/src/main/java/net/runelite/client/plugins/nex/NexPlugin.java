package net.runelite.client.plugins.nex;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

import lombok.Getter;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
@PluginDescriptor(
        name = "[Y] Nex",
        description = "Shows additional figures in the ancient chamber.",
        tags = {"nex", "ancient", "god", "wars", "happy birthday rusher"}
)
public class NexPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(NexPlugin.class);

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private NexConfig config;

    @Inject
    private NexOverlay overlay;

    @Getter
    private NexPhase currentPhase;

    @Getter
    private Set<Integer> targets;

    @Getter
    private Map<String, Integer> sickPlayers;

    @Getter
    private WorldPoint bloodSacrificeLocation;

    @Getter
    private int bloodSacrificeTimer;

    @Getter
    private int iceShardTimer;

    @Getter
    private int iceCageTimer;

    @Getter
    private int nexInvulnerability;

    @Getter
    private Set<GameObject> shadowObjects;

    @Getter
    private Set<GameObject> iceObjects;

    @Provides
    protected NexConfig getConfig(ConfigManager configManager) {
        return (NexConfig)configManager.getConfig(NexConfig.class);
    }

    protected void startUp() throws Exception {
        currentPhase = NexPhase.UNKNOWN;
        targets = new HashSet<>();
        sickPlayers = new HashMap<>();
        bloodSacrificeTimer = 0;
        iceShardTimer = 0;
        nexInvulnerability = 0;
        shadowObjects = new HashSet<>();
        iceObjects = new HashSet<>();
        overlayManager.add(overlay);
    }

    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (bloodSacrificeTimer > 0)
            bloodSacrificeTimer--;
        if (nexInvulnerability > 0)
            nexInvulnerability--;
        if (iceShardTimer > 0)
            iceShardTimer--;
        if (iceCageTimer > 0)
            iceCageTimer--;
        if (config.showTargetableEntity()) {
            NPC target = findNpc(targets);
            if (target != null)
                if (target.isDead() || target.getHealthRatio() == 0) {
                    if (client.hasHintArrow())
                        client.clearHintArrow();
                } else {
                    client.setHintArrow(target);
                }
        }
        List<String> curedPlayers = new ArrayList<>();
        for (String name : sickPlayers.keySet()) {
            int timer = sickPlayers.get(name) - 1;
            sickPlayers.put(name, timer);
            if (timer <= 0)
                curedPlayers.add(name);
        }
        curedPlayers.forEach(name -> sickPlayers.remove(name));
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        ChatMessageType cmt = event.getType();
        String msg = event.getMessage();
        if (cmt == ChatMessageType.GAMEMESSAGE && event
                .getName().equals("") && event
                .getSender() == null) {
            if ("<col=e00a19>Nex has marked you for a blood sacrifice! RUN!</col>".equals(msg)) {
                bloodSacrificeLocation = client.getLocalPlayer().getWorldLocation();
                bloodSacrificeTimer = 9;
            }
            parseNexVoiceLine(msg, true);
        }
    }

    private void parseNexVoiceLine(String txt, boolean isChatBox) {
        Arrays.stream(NexConstant.NEX_ALL_DIALOGUES).filter(x -> isChatBox ? x.chats(txt) : x.matches(txt)).forEach(x -> {
            currentPhase = x.getPhase();
            int invulnerability = x.getInvulnerability();
            if (invulnerability >= 0)
                nexInvulnerability = invulnerability;
        });
        calculateTargetableEntity(txt);
        if (isChatBox ? NexConstant.NEX_LINE_ICE_SHARD.chats(txt) : NexConstant.NEX_LINE_ICE_SHARD.matches(txt))
            iceShardTimer = 6;
    }

    @Subscribe
    public void onOverheadTextChanged(OverheadTextChanged event) {
        Actor actor = event.getActor();
        String text = event.getOverheadText();
        if (actor instanceof NPC && "Nex".equals(actor.getName())) {
            NPC npc = (NPC)actor;
            parseNexVoiceLine(text, false);
        }
        if (actor instanceof net.runelite.api.Player && "*Cough*".equals(text))
            sickPlayers.put(actor.getName(), 5);
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject obj = event.getGameObject();
        if (NexConstant.SHADOW_GAME_OBJECT_IDS.contains(obj.getId()))
            if (!shadowObjects.contains(obj))
                shadowObjects.add(obj);
        if (NexConstant.ICE_CAGE_PLACEHOLDER.contains(obj.getId()))
            if (!iceObjects.contains(obj)) {
                iceObjects.add(obj);
                if (iceObjects.size() == 1 && iceCageTimer == 0) {
                    iceCageTimer = 9;
                } else if (iceCageTimer > 7) {
                    iceCageTimer = 0;
                }
            }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        GameObject obj = event.getGameObject();
        if (NexConstant.SHADOW_GAME_OBJECT_IDS.contains(obj.getId()))
            if (shadowObjects.contains(obj))
                shadowObjects.remove(obj);
        if (NexConstant.ICE_CAGE_PLACEHOLDER.contains(obj.getId()))
            if (iceObjects.contains(obj))
                iceObjects.remove(obj);
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        NPC npc = event.getNpc();
        if (NexConstant.NEX_IDS.contains(npc.getId()))
            targets = NexConstant.NEX_IDS;
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        NPC npc = event.getNpc();
        if (NexConstant.NEX_IDS.contains(npc.getId()))
            targets = new HashSet<>();
    }

    public NPC findNpc(Set<Integer> ids) {
        return client.getNpcs().stream().filter(npc -> ids.contains(npc.getId())).findAny().orElse(null);
    }

    private void calculateTargetableEntity(String text) {
        for (NexTargetChange ntc : NexConstant.NEX_TARGETS) {
            for (NexDialogue nd : ntc.getDialogues()) {
                if (nd.matches(text))
                    targets = ntc.getIds();
            }
        }
    }
}
