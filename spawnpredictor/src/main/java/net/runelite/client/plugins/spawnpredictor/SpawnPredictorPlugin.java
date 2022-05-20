package net.runelite.client.plugins.spawnpredictor;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.spawnpredictor.overlays.DebugOverlayPanel;
import net.runelite.client.plugins.spawnpredictor.overlays.DisplayModeOverlay;
import net.runelite.client.plugins.spawnpredictor.overlays.RotationOverlayPanel;
import net.runelite.client.plugins.spawnpredictor.util.FightCavesNpc;
import net.runelite.client.plugins.spawnpredictor.util.FightCavesNpcSpawn;
import net.runelite.client.plugins.spawnpredictor.util.StartLocations;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Extension
@PluginDescriptor(
        name = "[S] Spawn Predictor",
        tags = {"fc", "fight", "caves", "spawn", "predictor", "fight caves", "tzhaar"},
        description = "Shows the upcoming and/or current spawns for each wave of Fight Caves",
        enabledByDefault = false
)
public class SpawnPredictorPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(SpawnPredictorPlugin.class);

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private RotationOverlayPanel rotationOverlayPanel;

    @Inject
    private DisplayModeOverlay displayModeOverlay;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private DebugOverlayPanel debugOverlayPanel;

    @Inject
    private SpawnPredictorConfig config;

    private boolean mirrorMode;

    @Provides
    SpawnPredictorConfig providesConfig(ConfigManager configManager) {
        return configManager.getConfig(SpawnPredictorConfig.class);
    }

    @Getter
    private static List<List<FightCavesNpcSpawn>> waveData = new ArrayList<>();

    @Getter
    private int currentUTCTime;

    @Getter
    private int rotationCol;

    @Getter
    private static int currentWave = -1;

    @Getter
    private int currentRotation = -1;

    @Getter
    private static int rsVal = -1;

    private boolean active = false;

    private final Pattern WAVE_PATTERN = Pattern.compile(".*Wave: (\\d+).*");

    public boolean isFightCavesActive() {
        return (ArrayUtils.contains(client.getMapRegions(), 9551) && client.isInInstancedRegion());
    }

    public boolean isLocatedAtTzhaars() {
        return (ArrayUtils.contains(client.getMapRegions(), 9808) && !client.isInInstancedRegion());
    }

    protected void startUp() {
        overlayManager.add(rotationOverlayPanel);
        overlayManager.add(displayModeOverlay);
        overlayManager.add(debugOverlayPanel);
    }

    protected void shutDown() {
        overlayManager.remove(rotationOverlayPanel);
        overlayManager.remove(displayModeOverlay);
        overlayManager.remove(debugOverlayPanel);
        reset();
    }

    private void reset() {
        currentUTCTime = -1;
        rotationCol = -1;
        currentWave = -1;
        currentRotation = -1;
        rsVal = -1;
        active = false;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() != GameState.LOGGED_IN)
            return;
        if (isFightCavesActive() && !active) {
            currentRotation = StartLocations.translateRotation(rotationCol);
            rsVal = (Integer) ((Pair) StartLocations.getLookupMap().get(currentRotation)).getLeft();
            updateWaveData(rsVal);
            currentWave = 1;
            active = true;
        } else if (!isFightCavesActive()) {
            reset();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        Matcher waveMatcher = WAVE_PATTERN.matcher(event.getMessage());
        if (event.getType() != ChatMessageType.GAMEMESSAGE ||
                !isFightCavesActive() ||
                !waveMatcher.matches())
            return;
        currentWave = Integer.parseInt(waveMatcher.group(1));
        if (currentRotation == 7 && currentWave == 3) {
            rsVal = 11;
            updateWaveData(rsVal);
        }
    }

    private void updateWaveData(int rsVal) {
        waveData = calculateSpawns(rsVal);
    }

    private static List<List<FightCavesNpcSpawn>> calculateSpawns(int rsVal) {
        ArrayList<List<FightCavesNpcSpawn>> spawns = new ArrayList<>();
        int currentCycle = rsVal;
        for (FightCavesNpc npc : FightCavesNpc.values()) {
            if (npc != FightCavesNpc.JAD) {
                List<List<FightCavesNpcSpawn>> subSpawns = generateSubSpawns((currentCycle + 1) % 15, npc, spawns);
                ArrayList<FightCavesNpcSpawn> initialSpawn = new ArrayList<>();
                initialSpawn.add(new FightCavesNpcSpawn(npc, currentCycle));
                spawns.add(initialSpawn);
                currentCycle = (currentCycle + 1) % 15;
                spawns.addAll(subSpawns);
                currentCycle = (currentCycle + subSpawns.size()) % 15;
                ArrayList<FightCavesNpcSpawn> postSpawns = new ArrayList<>();
                postSpawns.add(new FightCavesNpcSpawn(npc, currentCycle));
                postSpawns.add(new FightCavesNpcSpawn(npc, (currentCycle + 1) % 15));
                spawns.add(postSpawns);
                currentCycle = (currentCycle + 1) % 15;
            }
        }
        ArrayList<FightCavesNpcSpawn> jadSpawn = new ArrayList<>();
        jadSpawn.add(new FightCavesNpcSpawn(FightCavesNpc.JAD, currentCycle));
        spawns.add(jadSpawn);
        return spawns;
    }

    private static List<List<FightCavesNpcSpawn>> generateSubSpawns(int currentCycle, FightCavesNpc npc, List<List<FightCavesNpcSpawn>> existing) {
        ArrayList<List<FightCavesNpcSpawn>> sub = new ArrayList<>();
        for (List<FightCavesNpcSpawn> existingWave : existing) {
            ArrayList<FightCavesNpcSpawn> newSpawn = new ArrayList<>();
            newSpawn.add(new FightCavesNpcSpawn(npc, currentCycle));
            for (int i = 0; i < existingWave.size(); i++) {
                FightCavesNpcSpawn existingSpawn = existingWave.get(i);
                newSpawn.add(new FightCavesNpcSpawn(existingSpawn.getNpc(), (currentCycle + i + 1) % 15));
            }
            sub.add(newSpawn);
            currentCycle = (currentCycle + 1) % 15;
        }
        return sub;
    }

    public final LocalTime getUTCTime() {
        return LocalTime.now(ZoneId.of("UTC"));
    }

    public final String getUTCFormatted() {
        return getUTCTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    @Schedule(period = 500L, unit = ChronoUnit.MILLIS)
    public void updateSchedule() {
        if (client.getGameState() != GameState.LOGGED_IN)
            return;
        if (!isLocatedAtTzhaars() || client.isInInstancedRegion())
            return;
        currentUTCTime = getUTCTime().getHour() * 60 + getUTCTime().getMinute();
        setRotationColVal();
    }

    private void setRotationColVal() {
        rotationCol = currentUTCTime % 16;
        int minute = getUTCTime().getMinute();
        if ((rotationCol == 15 && minute % 2 != 0) || (rotationCol == 0 && minute % 2 == 0)) {
            rotationCol = 1;
        } else {
            rotationCol++;
        }
    }

    /*@Subscribe
    private void onClientTick(ClientTick event) {
        if (client.isMirrored() && !mirrorMode) {
            rotationOverlayPanel.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(rotationOverlayPanel);
            overlayManager.add(rotationOverlayPanel);
            displayModeOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(displayModeOverlay);
            overlayManager.add(displayModeOverlay);
            debugOverlayPanel.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(debugOverlayPanel);
            overlayManager.add(debugOverlayPanel);
            mirrorMode = true;
        }
    }*/
}
