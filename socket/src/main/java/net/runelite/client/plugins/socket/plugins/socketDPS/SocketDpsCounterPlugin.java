package net.runelite.client.plugins.socket.plugins.socketDPS;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.socket.SocketPlugin;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static net.runelite.api.NpcID.*;

@Extension
@PluginDescriptor(
        name = "Socket - Damage Counter",
        description = "Counts damage by a party",
        enabledByDefault = false
)
@PluginDependency(SocketPlugin.class)
public class SocketDpsCounterPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(SocketDpsCounterPlugin.class);

    private static final ArrayList<Integer> BOSSES = new ArrayList<>();

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SocketDpsOverlay socketDpsOverlay;

    @Inject
    private SocketDpsDifferenceOverlay differenceOverlay;

    @Inject
    private SocketDpsConfig socketDpsConfig;

    @Inject
    private EventBus eventBus;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private SocketPlugin socketPlugin;

    @Inject
    private ClientThread clientThread;

    @Getter
    private Map<String, Integer> members = new ConcurrentHashMap<>();

    private List<String> highlights = new ArrayList<>();

    @Getter
    private List<String> danger = new ArrayList<>();

    private boolean mirrorMode;

    @Provides
    SocketDpsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SocketDpsConfig.class);
    }

    protected void startUp() {
        members.clear();
        overlayManager.add(socketDpsOverlay);
        overlayManager.add(differenceOverlay);
        clientThread.invoke(this::rebuildAllPlayers);
        BOSSES.addAll(Arrays.asList(
                ABYSSAL_SIRE, ABYSSAL_SIRE_5887, ABYSSAL_SIRE_5888, ABYSSAL_SIRE_5889, ABYSSAL_SIRE_5890, ABYSSAL_SIRE_5891, ABYSSAL_SIRE_5908,
                CALLISTO, CALLISTO_6609, CERBERUS, CERBERUS_5863, CERBERUS_5866, CHAOS_ELEMENTAL, CHAOS_ELEMENTAL_6505, CORPOREAL_BEAST,
                GENERAL_GRAARDOR, GENERAL_GRAARDOR_6494, GIANT_MOLE, GIANT_MOLE_6499, KALPHITE_QUEEN, KALPHITE_QUEEN_963, KALPHITE_QUEEN_965,
                KALPHITE_QUEEN_4303, KALPHITE_QUEEN_4304, KALPHITE_QUEEN_6500, KALPHITE_QUEEN_6501, KING_BLACK_DRAGON, KING_BLACK_DRAGON_2642,
                ICE_TROLL_650, KRIL_TSUTSAROTH, KRIL_TSUTSAROTH_6495, SARACHNIS, VENENATIS, VENENATIS_6610, VETION, VETION_REBORN, SCORPIA, MAN_3106, MAN_3108,
                KREEARRA, COMMANDER_ZILYANA, DAGANNOTH_SUPREME, DAGANNOTH_PRIME, DAGANNOTH_REX, GUARD_11204,
                //Cox IDs
                TEKTON, TEKTON_7541, TEKTON_7542, TEKTON_ENRAGED, TEKTON_ENRAGED_7544, TEKTON_7545, VESPULA, VESPULA_7531, VESPULA_7532, ABYSSAL_PORTAL,
                VANGUARD, VANGUARD_7526, VANGUARD_7527, VANGUARD_7528, VANGUARD_7529, GREAT_OLM, GREAT_OLM_LEFT_CLAW, GREAT_OLM_RIGHT_CLAW_7553,
                GREAT_OLM_7554, GREAT_OLM_LEFT_CLAW_7555, DEATHLY_RANGER, DEATHLY_MAGE, MUTTADILE, MUTTADILE_7562, MUTTADILE_7563,
                VASA_NISTIRIO, VASA_NISTIRIO_7567, GUARDIAN, GUARDIAN_7570, GUARDIAN_7571, GUARDIAN_7572, LIZARDMAN_SHAMAN_7573, LIZARDMAN_SHAMAN_7574,
                ICE_DEMON, ICE_DEMON_7585, SKELETAL_MYSTIC, SKELETAL_MYSTIC_7605, SKELETAL_MYSTIC_7606,
                //Nightmare IDs
                THE_NIGHTMARE_9425, THE_NIGHTMARE_9426, THE_NIGHTMARE_9427, THE_NIGHTMARE_9428, THE_NIGHTMARE_9429, THE_NIGHTMARE_9430,
                THE_NIGHTMARE_9431, THE_NIGHTMARE_9432, THE_NIGHTMARE_9433, PHOSANIS_NIGHTMARE_9416, PHOSANIS_NIGHTMARE_9417, PHOSANIS_NIGHTMARE_9418,
                PHOSANIS_NIGHTMARE_9419, PHOSANIS_NIGHTMARE_9420, PHOSANIS_NIGHTMARE_9421, PHOSANIS_NIGHTMARE_9422, PHOSANIS_NIGHTMARE_9423, PHOSANIS_NIGHTMARE_9424,
                PHOSANIS_NIGHTMARE_11153, PHOSANIS_NIGHTMARE_11154, PHOSANIS_NIGHTMARE_11155,
                //Regular Tob IDs
                THE_MAIDEN_OF_SUGADINTI, THE_MAIDEN_OF_SUGADINTI_8361, THE_MAIDEN_OF_SUGADINTI_8362, THE_MAIDEN_OF_SUGADINTI_8363, THE_MAIDEN_OF_SUGADINTI_8364,
                THE_MAIDEN_OF_SUGADINTI_8365, PESTILENT_BLOAT, NYLOCAS_VASILIAS, NYLOCAS_VASILIAS_8355, NYLOCAS_VASILIAS_8356, NYLOCAS_VASILIAS_8357,
                SOTETSEG, SOTETSEG_8388, XARPUS, XARPUS_8339, XARPUS_8340, XARPUS_8341, VERZIK_VITUR_8369, VERZIK_VITUR_8370, VERZIK_VITUR_8371,
                VERZIK_VITUR_8372, VERZIK_VITUR_8373, VERZIK_VITUR_8374, VERZIK_VITUR_8375,
                //Story Mode Tob IDs
                THE_MAIDEN_OF_SUGADINTI_10814, THE_MAIDEN_OF_SUGADINTI_10815, THE_MAIDEN_OF_SUGADINTI_10816, THE_MAIDEN_OF_SUGADINTI_10817, THE_MAIDEN_OF_SUGADINTI_10818,
                THE_MAIDEN_OF_SUGADINTI_10819, PESTILENT_BLOAT_10812, NYLOCAS_VASILIAS_10786, NYLOCAS_VASILIAS_10787, NYLOCAS_VASILIAS_10788, NYLOCAS_VASILIAS_10789,
                SOTETSEG_10864, SOTETSEG_10865, XARPUS_10766, XARPUS_10767, XARPUS_10768, XARPUS_10769,VERZIK_VITUR_10830, VERZIK_VITUR_10831, VERZIK_VITUR_10832,
                VERZIK_VITUR_10833, VERZIK_VITUR_10834, VERZIK_VITUR_10835, VERZIK_VITUR_10836,
                //Hard Mode Tob IDs
                THE_MAIDEN_OF_SUGADINTI_10822, THE_MAIDEN_OF_SUGADINTI_10823, THE_MAIDEN_OF_SUGADINTI_10824, THE_MAIDEN_OF_SUGADINTI_10825,
                THE_MAIDEN_OF_SUGADINTI_10826, THE_MAIDEN_OF_SUGADINTI_10827, PESTILENT_BLOAT_10813, NYLOCAS_VASILIAS_10807, NYLOCAS_VASILIAS_10808,
                NYLOCAS_VASILIAS_10809, NYLOCAS_VASILIAS_10810,  SOTETSEG_10867, SOTETSEG_10868, XARPUS_10770, XARPUS_10771, XARPUS_10772, XARPUS_10773,
                VERZIK_VITUR_10847, VERZIK_VITUR_10848, VERZIK_VITUR_10849, VERZIK_VITUR_10850, VERZIK_VITUR_10851, VERZIK_VITUR_10852, VERZIK_VITUR_10853,
                //Nex IDs
                NEX, NEX_11279, NEX_11280, NEX_11281, NEX_11282));
    }

    protected void shutDown() {
        overlayManager.remove(socketDpsOverlay);
        overlayManager.remove(differenceOverlay);
        members.clear();
    }

    @Subscribe
    void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals("socketdpscounter"))
            clientThread.invoke(this::rebuildAllPlayers);
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING)
            members.clear();
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
        Actor target = hitsplatApplied.getActor();
        Hitsplat hitsplat = hitsplatApplied.getHitsplat();
        if (client.getLocalPlayer() != null && hitsplat.isMine() && target != client.getLocalPlayer() && target instanceof NPC && hitsplat.getAmount() > 0) {
            NPC npc = (NPC)target;
            int interactingId = npc.getId();
            if (!socketDpsConfig.onlyBossDps() || BOSSES.contains(interactingId)) {
                int hit = hitsplat.getAmount();
                String pName = client.getLocalPlayer().getName();
                members.put(pName, members.getOrDefault(pName, 0) + hit);
                members.put("Total", members.getOrDefault("Total", 0) + hit);
                JSONObject data = new JSONObject();
                data.put("player", pName);
                data.put("target", interactingId);
                data.put("hit", hit);
                data.put("world", client.getWorld());
                JSONObject payload = new JSONObject();
                payload.put("dps-counter", data);
                eventBus.post(new SocketBroadcastPacket(payload));
                members = sortByValue(members);
            }
        }
    }

    @Subscribe
    public void onOverlayMenuClicked(OverlayMenuClicked event) {
        if (event.getEntry() == SocketDpsOverlay.RESET_ENTRY)
            members.clear();
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        if (npc.isDead() && BOSSES.contains(npc.getId())) {
            log.debug("Boss has died!");
            if (socketDpsConfig.autoclear())
                members.clear();
            if (socketDpsConfig.clearAnyBossKill()) {
                JSONObject data = new JSONObject();
                data.put("boss", npc.getId());
                data.put("world", client.getWorld());
                JSONObject payload = new JSONObject();
                payload.put("dps-clear", data);
                eventBus.post(new SocketBroadcastPacket(payload));
            }
        }
    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event) {
        try {
            if (client.getGameState() == GameState.LOGGED_IN && client.getLocalPlayer() != null){
                JSONObject payload = event.getPayload();
                if (payload.has("dps-clear")){
                    if (!socketDpsConfig.onlySameWorld() || payload.getJSONObject("dps-clear").getInt("world") == client.getWorld()) {
                        members.clear();
                    }
                }else if (payload.has("dps-counter")){
                    JSONObject data = payload.getJSONObject("dps-counter");
                    if (!socketDpsConfig.onlySameWorld() || client.getWorld() == data.getInt("world")){
                        if (!data.getString("player").equals(client.getLocalPlayer().getName())){
                            clientThread.invoke(() -> {
                                String attacker = data.getString("player");
                                int targetId = data.getInt("target");
                                updateDpsMember(attacker, targetId, data.getInt("hit"));
                            });
                        }
                    }
                }
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }
    }

    private void updateDpsMember(String attacker, int targetId, int hit) {
        if (BOSSES.contains(targetId) || !socketDpsConfig.onlyBossDps()) {
            members.put(attacker, members.getOrDefault(attacker, 0) + hit);
            members.put("Total", (members.getOrDefault("Total", 0)) + hit);
            members = sortByValue(members);
            updateDanger();
        }
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return (Map<K, V>)map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, java.util.LinkedHashMap::new));
    }

    public List<String> getHighlights() {
        String configplayers = socketDpsConfig.getPlayerToHighlight().toLowerCase();
        return configplayers.isEmpty() ? Collections.<String>emptyList() : SocketText.fromCSV(configplayers);
    }

    void rebuildAllPlayers() {
        highlights = getHighlights();
    }

    void updateDanger() {
        danger.clear();
        for (String mem1 : members.keySet()) {
            if (highlights.contains(mem1))
                for (String mem2 : members.keySet()) {
                    if (!mem2.equalsIgnoreCase(mem1))
                        if (members.get(mem2) - members.get(mem1) <= 50)
                            danger.add(mem2);
                }
        }
    }

    /*@Subscribe
    private void onClientTick(ClientTick event)
    {
        if (client.isMirrored() && !mirrorMode) {
            socketDpsOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(socketDpsOverlay);
            overlayManager.add(socketDpsOverlay);
            mirrorMode = true;
        }
    }*/

}
