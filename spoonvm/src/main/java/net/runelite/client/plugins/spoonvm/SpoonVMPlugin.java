package net.runelite.client.plugins.spoonvm;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.spoonvm.overlays.PlatformOverlay;
import net.runelite.client.plugins.spoonvm.overlays.RockRespawnOverlay;
import net.runelite.client.plugins.spoonvm.overlays.SwimOverlay;
import net.runelite.client.plugins.spoonvm.overlays.VMPrayerOverlay;
import net.runelite.client.plugins.spoonvm.utils.Constants;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Volcanic Mine",
        description = "Useful plugins for Volcanic Mine.",
        tags = {"volcanic", "mine", "vm", "mining", "timer", "warning","stability","swim","swimmer","fish"}
)
@Slf4j
public class SpoonVMPlugin extends Plugin {
    // Chat messages
    private static final String CHAT_VM_START = "The volcano awakens! You can now access the area below...";
    private static final String PLATFORM_WARNING_MESSAGE = "The platform beneath you will disappear soon!";
    private static final String BOULDER_WARNING_MESSAGE = "The current boulder stage is complete.";
    // Constants
    private static final int PLATFORM_STAGE_1_ID = 30998;
    private static final int PLATFORM_STAGE_2_ID = 30999;
    private static final int PLATFORM_STAGE_3_ID = 31000;
    private static final int BOULDER_BREAK_STAGE_1_ID = 7807;
    private static final int BOULDER_BREAK_STAGE_2_ID = 7809;
    private static final int BOULDER_BREAK_STAGE_3_ID = 7811;
    private static final int BOULDER_BREAK_STAGE_4_ID = 7813;
    private static final int BOULDER_BREAK_STAGE_5_ID = 7815;
    private static final int ROCK_EMPTY = 31046;
    private static final int ROCK_ACTIVE = 31045;
    private static final int VM_REGION_NORTH = 15263;
    private static final int VM_REGION_SOUTH = 15262;
    private static final Duration VM_FULL_TIME = Duration.ofMinutes(10);
    private static final Duration VM_HALF_TIME = Duration.ofMinutes(5);

    //start of Vm Tracker Plugin int
    private static final int VARBIT_STABILITY = 5938;
    private static final int VARBIT_GAME_STATE = 5941;
    private static final int PROC_VOLCANIC_MINE_SET_OTHERINFO = 2022;

    private static final int HUD_COMPONENT = 611;
    private static final int HUD_STABILITY_COMPONENT = 13;

    private static final int GAME_STATE_IN_LOBBY = 1;
    private static final int GAME_STATE_IN_GAME = 2;

    private int lastMineStability = 50;
    private ArrayList<Integer> stabilityChanged = new ArrayList<>();

    public ArrayList<SpoonVMObjects> platforms = new ArrayList<>();
    public ArrayList<SpoonVMObjects> rocks = new ArrayList<>();
    private Set<Integer> gameObjects = null;
    //End of Vm Tracker Plugin int

    private boolean mirrorMode;

    @Inject
    private Notifier notifier;

    @Inject
    private Client client;

    @Inject
    private SpoonVMConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ClientThread clientThread;

    @Inject
    private PlatformOverlay platformOverlay;

    @Inject
    private SwimOverlay swimOverlay;

    @Inject
    private VMPrayerOverlay prayerOverlay;

    @Inject
    private RockRespawnOverlay rockRespawnOverlay;

    @Inject
    private Constants constants;

    @Provides
    SpoonVMConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SpoonVMConfig.class);
    }

    // Timer variables
    private Duration timeUntilVentWarning;
    private Duration timeUntilEruptionWarning;
    private Instant VMTimer;
    // Event warning latches
    private boolean hasWarnedVent = false;
    private boolean hasWarnedEruption = false;

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(platformOverlay);
        overlayManager.add(swimOverlay);
        overlayManager.add(prayerOverlay);
        overlayManager.add(rockRespawnOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(platformOverlay);
        overlayManager.remove(swimOverlay);
        overlayManager.remove(prayerOverlay);
        overlayManager.remove(rockRespawnOverlay);
        reset();
    }

    private void reset() {
        VMTimer = null;
        hasWarnedVent = false;
        hasWarnedEruption = false;
        platforms.clear();
        rocks.clear();
        lastMineStability = 50;
        stabilityChanged.clear();
    }

    private void calcVentWarningTime() {
        if (config.showVentWarning()) {
            timeUntilVentWarning = VM_HALF_TIME.minusSeconds(config.ventWarningTime());
        }
    }

    private void calcEruptionWarningTime() {
        if (config.showEruptionWarning()) {
            timeUntilEruptionWarning = VM_FULL_TIME.minusSeconds(config.eruptionWarningTime());
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        // Calculate new vent warning times if enabled
        calcVentWarningTime();
        calcEruptionWarningTime();

        if (event.getGroup().equalsIgnoreCase("spoonvm")) {
            if(event.getKey().equals("eastGas")){
                refreshScene();
                if (config.eastGas()) {
                    gameObjects = ImmutableSet.of(31050, 31051);
                    removeGameObjectsFromScene(gameObjects, 1);
                }
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        // Fetch times upon client loading
        if (event.getGameState() == GameState.LOGIN_SCREEN) {
            timeUntilVentWarning = VM_HALF_TIME.minusSeconds(config.ventWarningTime());
            timeUntilEruptionWarning = VM_FULL_TIME.minusSeconds(config.eruptionWarningTime());
        }

        if (event.getGameState() == GameState.LOGGED_IN && isInVM()) {
            if (this.config.eastGas()) {
                gameObjects = ImmutableSet.of(31050,31051);
                removeGameObjectsFromScene(gameObjects, 1);
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() == ChatMessageType.GAMEMESSAGE || event.getType() == ChatMessageType.SPAM) {
            if (Text.removeTags(event.getMessage()).equals(CHAT_VM_START)) {
                VMTimer = Instant.now();
                lastMineStability = 50;
                stabilityChanged.clear();
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (VMTimer != null) {
            if (!isInVM()) {
                reset();
            }else {
                Duration timeSinceStart = Duration.between(VMTimer, Instant.now());

                if(timeSinceStart.compareTo(timeUntilVentWarning) >= 0 && !hasWarnedVent && config.showVentWarning()){
                    if (config.warningStyle() == SpoonVMConfig.WarningStyle.NOTIFIER) {
                        notifier.notify("The vents will shift in " + config.ventWarningTime() + " seconds!");
                        hasWarnedVent = true;
                    }else {
                        config.ventWarningTime();
                        client.playSoundEffect(3522, 20);
                        hasWarnedVent = true;
                    }
                }

                if(timeSinceStart.compareTo(timeUntilEruptionWarning) >= 0 && !hasWarnedEruption && config.showEruptionWarning()){
                    if (config.warningStyle() == SpoonVMConfig.WarningStyle.NOTIFIER) {
                        notifier.notify("The volcano will erupt in " + config.eruptionWarningTime() + " seconds!");
                        hasWarnedEruption = true;
                    }else  {
                        config.eruptionWarningTime();
                        client.playSoundEffect(3522, 20);
                        hasWarnedEruption = true;
                    }
                }

                for (int i=platforms.size()-1; i>=0; i--) {
                    SpoonVMObjects vmObj = platforms.get(i);
                    vmObj.ticks--;
                    if(vmObj.ticks == 0){
                        platforms.remove(vmObj);
                    }else {
                        platforms.set(i, vmObj);
                    }
                }

                for (int i=rocks.size()-1; i>=0; i--) {
                    SpoonVMObjects vmObj = rocks.get(i);
                    vmObj.ticks--;
                    if(vmObj.ticks == 0){
                        rocks.remove(vmObj);
                    }else {
                        rocks.set(i, vmObj);
                    }
                }
            }
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        if (isInVM()) {
            if(event.getGameObject().getId() == PLATFORM_STAGE_3_ID && this.client.getLocalPlayer() != null){
                WorldPoint playerWp = client.getLocalPlayer().getWorldLocation();
                WorldPoint objWp = event.getGameObject().getWorldLocation();
                if(config.showPlatformWarning() && playerWp.getX() == objWp.getX() && playerWp.getY() == objWp.getY()){
                    if (config.warningStyle() == SpoonVMConfig.WarningStyle.NOTIFIER) {
                        notifier.notify(PLATFORM_WARNING_MESSAGE);
                    }else {
                        client.playSoundEffect(3522, 20);
                    }
                }
                platforms.add(new SpoonVMObjects(event.getGameObject(), 11));
            }

            if (event.getGameObject().getId() == ROCK_EMPTY) {
                rocks.add(new SpoonVMObjects(event.getGameObject(), 25));
            }
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        if (isInVM()) {
            if (event.getGameObject().getId() == PLATFORM_STAGE_3_ID) {

                platforms.remove(event.getGameObject());
            }else if (event.getGameObject().getId() == ROCK_EMPTY) {
                rocks.remove(event.getGameObject());
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        if (isInVM() && config.showBoulderWarning()) {
            switch(npcSpawned.getNpc().getId()) {
                case BOULDER_BREAK_STAGE_1_ID:
                case BOULDER_BREAK_STAGE_2_ID:
                case BOULDER_BREAK_STAGE_3_ID:
                case BOULDER_BREAK_STAGE_4_ID:
                case BOULDER_BREAK_STAGE_5_ID:
                    if (config.warningStyle() == SpoonVMConfig.WarningStyle.NOTIFIER) {
                        notifier.notify(BOULDER_WARNING_MESSAGE);
                    } else {
                        client.playSoundEffect(3522, 20);
                    }
            }
        }
    }

    public boolean isInVM() {
        if (client.getLocalPlayer() != null && client.getLocalPlayer().getLocalLocation() != null) {
            return WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == VM_REGION_NORTH ||
                    WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == VM_REGION_SOUTH;
        }
        return false;
    }

    //start of Vm Tracker Plugin
    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        if (event.getScriptId() == PROC_VOLCANIC_MINE_SET_OTHERINFO) {
            Widget stabilityWidget = this.client.getWidget(40042507);
            if(stabilityWidget != null) {
                int stability = Integer.parseInt(Text.removeTags(stabilityWidget.getText().replace("%", "")));
                int delta = stability - lastMineStability;
                lastMineStability = stability;

                if (delta != 0) {
                    stabilityChanged.add(delta);
                }


                if(stabilityChanged.size() >= 2) {
                    String color1;
                    if(stabilityChanged.get(stabilityChanged.size()-1) > 0){
                        color1 = "<col=00ff00>";
                    }else if(stabilityChanged.get(stabilityChanged.size()-1) < 0){
                        color1 = "<col=ff0000>";
                    }else {
                        color1 = "<col=ffffff>";
                    }

                    String color2;
                    if(stabilityChanged.get(stabilityChanged.size()-2) > 0){
                        color2 = "<col=00ff00>";
                    }else if(stabilityChanged.get(stabilityChanged.size()-2) < 0){
                        color2 = "<col=ff0000>";
                    }else {
                        color2 = "<col=ffffff>";
                    }
                    stabilityWidget.setText(stabilityWidget.getText() + " (" + color1 + stabilityChanged.get(stabilityChanged.size()-1) + "</col>, " + color2 + stabilityChanged.get(stabilityChanged.size()-2) + "</col>)");
                }else if(stabilityChanged.size() == 1){
                    String color1;
                    if(stabilityChanged.get(0) > 0){
                        color1 = "<col=00ff00>";
                    }else if(stabilityChanged.get(0) < 0){
                        color1 = "<col=ff0000>";
                    }else {
                        color1 = "<col=ffffff>";
                    }
                    stabilityWidget.setText(stabilityWidget.getText() + " (" + color1 + stabilityChanged.get(0) + "</col>)");
                }
            }
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded e) {
        if(isInVM()) {
            int type = e.getType();
            int id = e.getIdentifier();

            if (this.config.hideLavaBeast()) {
                try {
                    if(type >= 7 && type <= 13 && type != 8){
                        NPC npc = this.client.getCachedNPCs()[id];
                        if (npc != null && npc.getName() != null) {
                            String name = npc.getName().toLowerCase();
                            if (name.contains("lava beast")) {
                                MenuEntry[] entries = this.client.getMenuEntries();
                                MenuEntry[] newEntries = new MenuEntry[entries.length - 1];
                                System.arraycopy(entries, 0, newEntries, 0, newEntries.length);
                                this.client.setMenuEntries(newEntries);
                            }
                        }
                    }
                }catch (ArrayIndexOutOfBoundsException ex){
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    /*@Subscribe
    public void onClientTick(ClientTick clientTick) {
        if (client.isMirrored() && !mirrorMode) {
            platformOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(platformOverlay);
            overlayManager.add(platformOverlay);
            prayerOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(prayerOverlay);
            overlayManager.add(prayerOverlay);
            rockRespawnOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(rockRespawnOverlay);
            overlayManager.add(rockRespawnOverlay);
            swimOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(swimOverlay);
            overlayManager.add(swimOverlay);
            mirrorMode = true;
        }
    }*/

    public void refreshScene() {
        this.clientThread.invokeLater(() -> this.client.setGameState(GameState.LOADING));
    }

    public void removeGameObjectsFromScene(Set<Integer> objectIDs, int plane) {
        Scene scene = this.client.getScene();
        Tile[][] tiles = scene.getTiles()[plane];
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                Tile tile = tiles[x][y];
                if (tile != null) {
                    Objects.requireNonNull(scene);
                    Arrays.<GameObject>stream(tile.getGameObjects()).filter(obj -> (obj != null && objectIDs.contains(obj.getId()))).findFirst().ifPresent(scene::removeGameObject);
                }
            }
        }
    }
}
