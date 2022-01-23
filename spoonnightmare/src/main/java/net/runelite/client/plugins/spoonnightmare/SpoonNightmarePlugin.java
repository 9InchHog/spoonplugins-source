package net.runelite.client.plugins.spoonnightmare;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.sound.sampled.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.util.List;
import java.util.*;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Nightmare",
        description = "Nightmare Plugin",
        tags = {"nightmare, ashihama, ross stinks, spoon"},
        enabledByDefault = false
)
public class SpoonNightmarePlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SpoonNightmareOverlay overlay;

    @Inject
    private PrayerOverlay prayOverlay;

    @Inject
    private SanfewOverlay sanfewOverlay;

    @Inject
    private TickOverlay ticksOverlay;

    @Inject
    private NightmarePrayerOverlay prayerOverlay;

    @Inject
    private YawnOverlay yawnOverlay;

    @Inject
    private SpoonNightmareConfig config;

    @Inject
    private ClientThread clientThread;

    public String correctPray = "";

    public Boolean cursePhase;

    Boolean impregnated;

    WorldPoint bossLoc;

    int parasiteTicks;

    boolean preggers;

    ArrayList<NPC> parasiteList = new ArrayList<>();

    boolean preparedForTakeoff = false;

    int flightTime = 5;

    public ArrayList<Color> raveRunway = new ArrayList<Color>();

    boolean totemsActive = false;

    ArrayList<TotemInfo> totemList = new ArrayList<>();

    public ArrayList<GameObject> shrooms = new ArrayList<>();

    public int mushroomTicks = 31;

    public boolean mushroomActive = false;

    public ArrayList<NPC> husks = new ArrayList<>();

    @Getter
    private int ticksUntilAttack = -1;

    @Getter
    private int eventTicks = -1;

    @Getter
    private NightmareAttack attack = NightmareAttack.UNKNOWN;

    @Getter
    private NPC nightmareNpc;

    @Getter
    private boolean activeFight = false;

    private static Clip clip;

    public int handsDelay = 5;
    public boolean handsOut = false;
	public ArrayList<GraphicsObject> handsLocation = new ArrayList<>();
    public ArrayList<Color> raveHandsColors = new ArrayList<>();

    private int sleepwalkerAlive = 0;

    public boolean yawning = false;
    public int yawnTicks = 26;

    public boolean flowersOut = false;

    public ArrayList<Color> raveTotemColors = new ArrayList<>();

    private int sleepwalkerCount = 0;
    private int huskCount = 0;
    private int parasiteCount = 0;
    private int nightmareHealingCount = 0;
    private int totemHealingCount = 0;
    private int hellPhaseSleepwalkerCount = 0;

    Point originalMagePosition = null;
    Point originalMeleePosition = null;
    Point originalRangePosition = null;
    boolean reorderActive = false;

    private boolean mirrorMode;

    @Getter
    private Set<LocalPoint> flowerTiles = new HashSet<>();
    public int flowerTickCount = 0;
    boolean flowersActive = false;
    boolean flowersActive2 = false;

    @Provides
    SpoonNightmareConfig getConfig(ConfigManager configManager) {
        return (SpoonNightmareConfig)configManager.getConfig(SpoonNightmareConfig.class);
    }

    protected void startUp() {
        setOriginalPositions();
        reset();
        overlayManager.add(overlay);
        overlayManager.add(sanfewOverlay);
        overlayManager.add(prayOverlay);
        overlayManager.add(ticksOverlay);
        overlayManager.add(prayerOverlay);
        overlayManager.add(yawnOverlay);
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(SpoonNightmarePlugin.class.getResourceAsStream("a10Strafe.wav")));
            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip)AudioSystem.getLine(info);
            clip.open(stream);
            FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (control != null) {
                control.setValue(config.a10StrafeVolume() / 2 - 45);
            }
        } catch (Exception var6) {
            clip = null;
        }
    }

    protected void shutDown() {
        reset();
        overlayManager.remove(sanfewOverlay);
        overlayManager.remove(overlay);
        overlayManager.remove(prayOverlay);
        overlayManager.remove(ticksOverlay);
        overlayManager.remove(prayerOverlay);
        overlayManager.remove(yawnOverlay);
    }

    protected void reset() {
        deActivateShuffle();
        reorderActive = false;
        correctPray = "";
        cursePhase = false;
        impregnated = false;
        preparedForTakeoff = false;
        flightTime = 5;
        raveRunway.clear();
        totemsActive = false;
        totemList.clear();
        shrooms.clear();
        mushroomTicks = 31;
        mushroomActive = false;
        husks.clear();
        ticksUntilAttack = -1;
        eventTicks = -1;
        attack = NightmareAttack.UNKNOWN;
        activeFight = false;
        preggers = false;
        nightmareNpc = null;
        parasiteList.clear();
        handsDelay = 5;
        handsOut = false;
		handsLocation.clear();
        raveHandsColors.clear();
        sleepwalkerAlive = 0;
        yawning = false;
        yawnTicks = 26;
        flowersOut = false;
        raveTotemColors.clear();
        sleepwalkerCount = 0;
        huskCount = 0;
        parasiteCount = 0;
        nightmareHealingCount = 0;
        totemHealingCount = 0;
        hellPhaseSleepwalkerCount = 0;
        flowerTickCount = 0;
        flowerTiles.clear();
        flowersActive = false;
        flowersActive2 = false;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState gameState = event.getGameState();
        if ((gameState == GameState.LOADING || gameState == GameState.CONNECTION_LOST) && (activeFight || nightmareNpc != null)) {
            reset();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if(event.getGroup().equals("Nightmare")) {
            if (event.getKey().equalsIgnoreCase("hidePrayers")) {
                Widget x = client.getWidget(35454980);
                if (x != null) {
                    if (config.hidePrayer()) {
                        if (nightmareNpc != null) {
                            for (Widget y : x.getStaticChildren()) {
                                if (!y.getName().toLowerCase().contains("piety") && !y.getName().toLowerCase().contains("augury") && !y.getName().toLowerCase().contains("preserve") &&
                                        !y.getName().toLowerCase().contains("melee") && !y.getName().toLowerCase().contains("magic") && !y.getName().toLowerCase().contains("missiles") &&
                                        !y.getName().toLowerCase().contains("redemption") && !y.getName().toLowerCase().contains("rapid heal")) {
                                    y.setHidden(true);
                                }
                            }
                        }
                    } else {
                        for (Widget y : x.getStaticChildren()) {
                            if (!y.getName().toLowerCase().contains("piety") && !y.getName().toLowerCase().contains("augury") && !y.getName().toLowerCase().contains("preserve") &&
                                    !y.getName().toLowerCase().contains("melee") && !y.getName().toLowerCase().contains("magic") && !y.getName().toLowerCase().contains("missiles") &&
                                    !y.getName().toLowerCase().contains("redemption") && !y.getName().toLowerCase().contains("rapid heal"))
                                y.setHidden(false);
                        }
                    }
                }
            } else if (event.getKey().equalsIgnoreCase("a10StrafeVoulme")) {
                if (clip != null) {
                    FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null) {
                        control.setValue((float) (config.a10StrafeVolume() / 2 - 45));
                    }
                }
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        if (event.getNpc() != null && event.getNpc().getName() != null){
            NPC npc = event.getNpc();
            int id = event.getNpc().getId();
            String name = event.getNpc().getName();

            if (name.equalsIgnoreCase("the nightmare") || name.equalsIgnoreCase("phosani's nightmare")) {
                nightmareNpc = npc;
                preggers = false;

                Widget x = client.getWidget(35454980);
                if (config.hidePrayer() && x != null) {
                    for (Widget y : x.getStaticChildren()) {
                        if (!y.getName().toLowerCase().contains("piety") && !y.getName().toLowerCase().contains("augury") && !y.getName().toLowerCase().contains("preserve") &&
                                !y.getName().toLowerCase().contains("melee") && !y.getName().toLowerCase().contains("magic") && !y.getName().toLowerCase().contains("missiles") &&
                                !y.getName().toLowerCase().contains("redemption") && !y.getName().toLowerCase().contains("rapid heal")) {
                            y.setHidden(true);
                        }
                    }
                }
            }else if (name.equalsIgnoreCase("parasite")){
                preggers = false;
                parasiteList.add(npc);
                parasiteCount++;
            }else if (id == 9455 || id == 9454 || id == 9466 || id == 9467) {
                husks.add(npc);
                huskCount++;
            }else if (id == 9435 || id == 9438 || id == 9441 || id == 9444){
                totemList.add(new TotemInfo(npc, -1));
                if(!totemsActive){
                    totemsActive = true;
                }
            }else if(name.equalsIgnoreCase("sleepwalker")){
                sleepwalkerAlive++;
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        if (event.getNpc() != null && event.getNpc().getName() != null){
            NPC npc = event.getNpc();
            int id = event.getNpc().getId();
            String name = event.getNpc().getName();

            if (name.equalsIgnoreCase("the nightmare") || name.equalsIgnoreCase("phosani's nightmare")) {
                reset();

                Widget x = client.getWidget(35454980);
                if(x != null) {
                    for (Widget y : x.getStaticChildren()) {
                        if (!y.getName().toLowerCase().contains("piety") && !y.getName().toLowerCase().contains("augury") && !y.getName().toLowerCase().contains("preserve") &&
                                !y.getName().toLowerCase().contains("melee") && !y.getName().toLowerCase().contains("magic") && !y.getName().toLowerCase().contains("missiles") &&
                                !y.getName().toLowerCase().contains("redemption") && !y.getName().toLowerCase().contains("rapid heal")) {
                            y.setHidden(false);
                        }
                    }
                }
            }else if (id == 9435 || id == 9438 || id == 9441 || id == 9444){
                for(TotemInfo ti : totemList){
                    if (ti.getNpc() == npc) {
                        totemList.remove(ti);
                        if (totemList.size() == 0)
                            totemsActive = false;
                    }
                }
            }else if (id == 9455 || id == 9454 || id == 9466 || id == 9467) {
                husks.remove(npc);
            }else if(name.equalsIgnoreCase("sleepwalker")){
                sleepwalkerAlive--;
            }
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        NPC npc = event.getNpc();
        int id = npc.getId();
        String name = npc.getName();

        if(id == 9435 || id == 9438 || id == 9441 || id == 9444){
            totemList.add(new TotemInfo(npc, -1));
            if(!totemsActive){
                totemsActive = true;
            }
        }else if(id == 9436 || id == 9439 || id == 9442 || id == 9445){
            for(int i=totemList.size()-1; i>=0; i--){
                if (totemList.get(i).getNpc() == npc) {
                    totemList.remove(i);
                    if (totemList.size() == 0)
                        totemsActive = false;
                }
            }
        }else if(name != null && (name.equalsIgnoreCase("the nightmare") || name.equalsIgnoreCase("phosani's nightmare")) && (npc.getId() == 9433  || npc.getId() == 9424)){
            if(config.displayStatsMsg()){
                if(config.huskStats()){
                    client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Husks - <col=ff0000>" + (huskCount / 2), null);
                }

                if(config.parasiteStats()){
                    client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Parasites - <col=ff0000>" + parasiteCount, null);
                }

                if(config.sleepwalkerStats()){
                    if(name.equalsIgnoreCase("phosani's nightmare")){
                        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Sleepwalkers - <col=ff0000>" + sleepwalkerCount + "</col>     Hell Phase - <col=ff0000>" + hellPhaseSleepwalkerCount, null);
                    }else {
                        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Sleepwalkers - <col=ff0000>" + sleepwalkerCount, null);
                    }
                }

                if(config.healingStats() != SpoonNightmareConfig.healingStatsMode.OFF){
                    String msg = "";
                    if(config.healingStats() == SpoonNightmareConfig.healingStatsMode.BOTH){
                        msg = "Boss Healed - <col=ff0000>" + nightmareHealingCount + "</col>     Totems Healed - <col=ff0000>" + totemHealingCount;
                    }else if(config.healingStats() == SpoonNightmareConfig.healingStatsMode.BOSS){
                        msg = "Boss Healed - <col=ff0000>" + nightmareHealingCount;
                    }else {
                        msg = "Totems Healed - <col=ff0000>" + totemHealingCount;
                    }
                    client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg, null);
                }
                sleepwalkerCount = 0;
                huskCount = 0;
                parasiteCount = 0;
                nightmareHealingCount = 0;
                totemHealingCount = 0;
                hellPhaseSleepwalkerCount = 0;
            }
        }
    }

    @Subscribe
    public void onActorDeath(ActorDeath event) {
        if(event.getActor() instanceof NPC && event.getActor().getName() != null){
            NPC npc = (NPC) event.getActor();
            String name = npc.getName();
            if(name != null ) {
                if (name.equalsIgnoreCase("parasite")) {
                    parasiteList.remove(npc);
                } else if (name.equalsIgnoreCase("husk")) {
                    husks.remove(npc);
                }
            }
        }

        if(event.getActor() instanceof Player && event.getActor().getName() != null){
            Player player = (Player) event.getActor();
            String name = player.getName();
            if(name != null && player == client.getLocalPlayer()) {
                deActivateShuffle();
            }
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (event.getActor() instanceof NPC){
            NPC npc = (NPC)event.getActor();
            int npcId = npc.getId();
            int npcAnimId = npc.getAnimation();
            if (isNightmareId(npcId)) {
                nightmareNpc = npc;
                activeFight = true;
                switch (npcAnimId) {
                    case 8594:
                        ticksUntilAttack = 7;
                        correctPray = "melee";
                        break;
                    case 8595:
                        ticksUntilAttack = 7;
                        correctPray = "magic";
                        break;
                    case 8596:
                        ticksUntilAttack = 7;
                        correctPray = "missiles";
                        break;
                    case 8598:
                        ticksUntilAttack = 10;
                        break;
                    case 8599:
                        ticksUntilAttack = 7;
                        cursePhase = true;
                        eventTicks = 36;
                        break;
                    case 8600:
                    case 8605:
                        ticksUntilAttack = 7;
                        break;
                    case 8601:
                        eventTicks = 31;
                        break;
                    case 8606:
                        eventTicks = 27;
                        ticksUntilAttack = 7;
                        if (!preggers) {
                            preggers = true;
                            parasiteTicks = 27;
                        } 
                        break;
                    case 8607:
                        ticksUntilAttack = 3;
                        break;
                    case 8609:
                        if (npcId == 9427 || npcId == 9430) {
                            ticksUntilAttack = 10;
                            eventTicks = 5;
                            preparedForTakeoff = true;

                            if(config.a10Strafe()){
                                clip.setFramePosition(0);
                                clip.start();
                            }
                        }else if((npcId >= 9416 && npcId <= 9424) || (npcId >= 11152 && npcId <= 11155)){
                            ticksUntilAttack = 10;
                            eventTicks = 5;

                            if(nightmareNpc.getWorldLocation().getRegionX() != 46 || nightmareNpc.getWorldLocation().getRegionY() != 45){
                                preparedForTakeoff = true;
                                if(config.a10Strafe()){
                                    clip.setFramePosition(0);
                                    clip.start();
                                }
                            }
                        }else {
                            ticksUntilAttack = 14;
                        }
                        break;
                    case 8610:
                        ticksUntilAttack = 26;
                        break;
                    case 8611:
                        ticksUntilAttack = 11;
                        break;
                    case 9102:
                        ticksUntilAttack = 32;
                        break;
                }
            }else if(npc.getName() != null && npc.getName().equalsIgnoreCase("sleepwalker") && nightmareNpc != null){
                if(npcAnimId == 8571 && !npc.isDead()){
                    if(nightmareNpc.getId() == 11154) {
                        hellPhaseSleepwalkerCount++;
                    }else {
                        sleepwalkerCount++;
                    }
                }
            }
        }
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        /*if (client.isMirrored() && !mirrorMode) {
            overlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(overlay);
            overlayManager.add(overlay);
            prayerOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(prayerOverlay);
            overlayManager.add(prayerOverlay);
            prayOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(prayOverlay);
            overlayManager.add(prayOverlay);
            sanfewOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(sanfewOverlay);
            overlayManager.add(sanfewOverlay);
            ticksOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(ticksOverlay);
            overlayManager.add(ticksOverlay);
            yawnOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(yawnOverlay);
            overlayManager.add(yawnOverlay);
            mirrorMode = true;
        }*/

        List<NPC> npcs = client.getNpcs();
        for (NPC n : npcs) {
            if (n != null && n.getName() != null && (n.getName().equalsIgnoreCase("the nightmare") || n.getName().equalsIgnoreCase("phosani's nightmare")) && client.isInInstancedRegion())
                bossLoc = n.getWorldLocation();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        String msg = event.getMessage().toLowerCase();
        if(event.getType() == ChatMessageType.GAMEMESSAGE) {
            if (msg.contains("the nightmare has impregnated you with a deadly parasite")) {
                impregnated = true;
            } else if (msg.contains("the parasite within you has been weakened") || msg.contains("the parasite bursts out of you, fully grown")) {
                impregnated = false;
            } else if (msg.contains("shuffling your prayers")) {
                cursePhase = true;
                activateShuffle();
            } else if (msg.contains("feel the effects of the nightmare's curse wear off")) {
                cursePhase = false;
                deActivateShuffle();
            } else if (msg.contains("the nightmare's spores have infected you, making you feel drowsy!")) {
                yawning = true;
                yawnTicks = 26;
            } else if (msg.contains("the nightmare's infection has worn off.")) {
                yawning = false;
                yawnTicks = 26;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!activeFight && nightmareNpc == null) {
            if (config.eventTickCounter() && eventTicks > 0) {
                eventTicks--;
            }
            return;
        }

        raveHandsColors.clear();
        for(GraphicsObject obj : client.getGraphicsObjects()){
            if(obj.getId() == 1767){
                raveHandsColors.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
            }
        }

        raveTotemColors.clear();
        for(int i=0; i<4; i++){
            raveTotemColors.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
        }

        raveRunway.clear();
        for(int i=0; i<80; i++){
            raveRunway.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
        }

        ticksUntilAttack--;
        if (eventTicks > 0)
            eventTicks--;

        if (preggers)
            parasiteTicks--;

        if (preparedForTakeoff) {
            flightTime--;
            if (flightTime == 0) {
                preparedForTakeoff = false;
                flightTime = 5;
            }
        }

        if (mushroomActive) {
            mushroomTicks--;
            if (mushroomTicks == 0) {
                mushroomActive = false;
                mushroomTicks = 31;
            }
        }

        if(handsOut){
            handsDelay--;
            if(handsDelay <= 0){
                handsDelay = 5;
                handsOut = false;
                handsLocation.clear();
            }
        }

        if(yawning){
            yawnTicks--;
            if(yawnTicks <= 0){
                yawnTicks = 26;
                yawning = false;
            }
        }

        if (flowersActive || flowersActive2) {
            Scene scene = client.getScene();
            Tile[][][] tiles = scene.getTiles();
            int z = client.getPlane();
            boolean flag = false;
            for (int x = 0; x < 104; x++) {
                for (int y = 0; y < 104; y++) {
                    Tile tile = tiles[z][x][y];
                    if (tile != null) {
                        Player player = client.getLocalPlayer();
                        if (player != null) {
                            GameObject[] gameObjects = tile.getGameObjects();
                            if (gameObjects != null) {
                                for (GameObject gameObject : gameObjects) {
                                    if (gameObject != null) {
                                        if (player.getLocalLocation().distanceTo(gameObject.getLocalLocation()) <= 2400) {
                                            if (config.lowFps()) {
                                                if (gameObject.getId() == 37744 || gameObject.getId() == 37745)
                                                    scene.removeGameObject(gameObject);
                                                if (gameObject.getId() == 37741 || gameObject.getId() == 37742 || gameObject.getId() == 29733 || gameObject.getId() == 37743 || gameObject.getId() == 37740) {
                                                    flag = true;
                                                    scene.removeGameObject(gameObject);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (flowersActive)
                flowerTickCount++;
            if (flowerTickCount >= 25) {
                flowersActive = false;
                flowerTickCount = 0;
                flowerTiles.clear();
            }
            if (flag)
                flowersActive2 = false;
        }
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObj = event.getGameObject();
        int id = gameObj.getId();
        if (id == 37739 || id == 37738) {
            shrooms.add(gameObj);
            mushroomActive = true;
        }else if(id == 37741 || id == 37744){
            flowersOut = true;
        }

        if (id == 37744 || id == 37745) {
            if (!flowersActive) {
                flowersActive = true;
                flowerTickCount = 0;
            }
            flowerTiles.add(gameObj.getLocalLocation());
        } else if (id == 29733 || id == 37741 || id == 37742 || id == 37743 || id == 37740) {
            flowersActive2 = true;
        }
    }

    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned event) {
        GameObject obj = event.getGameObject();
        int id = obj.getId();
        if (id == 37739) {
            shrooms.remove(obj);
        } else if (id == 37738) {
            shrooms.remove(obj);
        }else if (id == 37745 || id == 37742){
            flowersOut = false;
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        String target = Text.removeTags(event.getTarget()).toLowerCase();
        String option = Text.removeTags(event.getOption()).toLowerCase();
        if (nightmareNpc != null) {
            if (config.hideAttack() != SpoonNightmareConfig.hideAttackMode.OFF && (target.contains("the nightmare") || target.contains("phosani's nightmare"))
                    && event.getType() == MenuAction.NPC_SECOND_OPTION.getId()) {
                if ((((totemsActive && (config.hideAttack() == SpoonNightmareConfig.hideAttackMode.TOTEM || config.hideAttack() == SpoonNightmareConfig.hideAttackMode.ALL))
                        || (parasiteList.size() > 0 && (config.hideAttack() == SpoonNightmareConfig.hideAttackMode.PARASITE || config.hideAttack() == SpoonNightmareConfig.hideAttackMode.ALL))
                        || (husks.size() > 0 && (config.hideAttack() == SpoonNightmareConfig.hideAttackMode.HUSK || config.hideAttack() == SpoonNightmareConfig.hideAttackMode.ALL)))
                        && (shrooms.size() == 0 || (config.hideAttackIgnore() != SpoonNightmareConfig.hideAttackIgnoreMode.BOTH && config.hideAttackIgnore() != SpoonNightmareConfig.hideAttackIgnoreMode.SPORES))
                        && (!flowersOut || (config.hideAttackIgnore() != SpoonNightmareConfig.hideAttackIgnoreMode.BOTH && config.hideAttackIgnore() != SpoonNightmareConfig.hideAttackIgnoreMode.FLOWERS)))
                        || (sleepwalkerAlive > 0 && nightmareNpc.getId() != 11154)) {
                    client.setMenuOptionCount(client.getMenuOptionCount() - 1);
                }
            } else if (target.contains("sleepwalker") && event.getType() == MenuAction.NPC_SECOND_OPTION.getId() && nightmareNpc.getId() == 11154
                    && config.hideAttackSleepwalkers()) {
                client.setMenuOptionCount(client.getMenuOptionCount() - 1);
            }
        }
    }

    @Subscribe
    public void onSoundEffectPlayed(SoundEffectPlayed event) {
        if(config.muteHands() && (event.getSoundId() == 4307 || event.getSoundId() == 4274 || event.getSoundId() == 4228 || event.getSoundId() == 4322)){
            event.consume();
        }
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        if(event.getGraphicsObject().getId() == 1767){
            handsLocation.add(event.getGraphicsObject());
            if(!handsOut){
                handsOut = true;
                handsDelay = 5;
            }
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        if(event.getActor() != null && event.getActor() instanceof NPC){
            String name = event.getActor().getName();
            int id = ((NPC) event.getActor()).getId();
            Hitsplat.HitsplatType splatType = event.getHitsplat().getHitsplatType();

            if(name != null && splatType == Hitsplat.HitsplatType.HEAL
                    && (name.equalsIgnoreCase("the nightmare") || (name.equalsIgnoreCase("phosani's nightmare") && event.getHitsplat().getAmount() != 149))){
                nightmareHealingCount += event.getHitsplat().getAmount();
            }else if((id == 9435 || id == 9438 || id == 9441 || id == 9444) && splatType == Hitsplat.HitsplatType.DAMAGE_OTHER_WHITE){
                totemHealingCount += event.getHitsplat().getAmount();
            }
        }
    }

    private boolean isNightmareId(int npcId) {
        return ((npcId >= 9425 && npcId <= 9433) || (npcId >= 9416 && npcId <= 9424) || (npcId >= 11152 && npcId <= 11155));
    }

    @Subscribe(priority = -1)
    public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
        if (widgetLoaded.getGroupId() == WidgetID.PRAYER_GROUP_ID) {
            setOriginalPositions();
        }
    }

    private void activateShuffle() {
        if (!config.swapPrayers() || reorderActive) {
            return;
        }
        reorderActive = setPrayerPositions();
        if (reorderActive) {
            setPrayerIcons();
        }
    }

    private void deActivateShuffle() {
        if (!reorderActive) {
            return;
        }
        reorderActive = !resetPrayer();
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired scriptPostFired) {
        if (!reorderActive) {
            return;
        }
        if (scriptPostFired.getScriptId() == 461 || scriptPostFired.getScriptId() == 462) {
            boolean result = setPrayerPositions();
            if (result) {
                setPrayerIcons();
            }
        }
    }

    protected void setOriginalPositions() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (reorderActive) {
            if (originalRangePosition != null && originalMeleePosition != null && originalMagePosition != null) {
                return;
            }
        }

        Widget widgetMage = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
        Widget widgetRange = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES);
        Widget widgetMelee = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE);

        if (widgetMage == null || widgetRange == null || widgetMelee == null) {
            return;
        }

        originalMagePosition = new Point(widgetMage.getOriginalX(), widgetMage.getOriginalY());
        originalRangePosition = new Point(widgetRange.getOriginalX(), widgetRange.getOriginalY());
        originalMeleePosition = new Point(widgetMelee.getOriginalX(), widgetMelee.getOriginalY());
    }

    protected boolean setPrayerPositions() {
        Widget widgetMage = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
        Widget widgetRange = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES);
        Widget widgetMelee = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE);

        if (widgetMage == null || widgetRange == null || widgetMelee == null || originalMagePosition == null || originalRangePosition == null || originalMeleePosition == null) {
            return false;
        }

        // mage -> range
        setWidgetPosition(widgetMage, originalRangePosition.getX(), originalRangePosition.getY());
        // range -> melee
        setWidgetPosition(widgetRange, originalMeleePosition.getX(), originalMeleePosition.getY());
        // melee -> mage
        setWidgetPosition(widgetMelee, originalMagePosition.getX(), originalMagePosition.getY());

        return true;
    }

    protected boolean setPrayerIcons() {
        Widget widgetMage = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
        Widget widgetRange = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES);
        Widget widgetMelee = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE);

        if (widgetMage == null || widgetRange == null || widgetMelee == null) {
            return false;
        }

        Widget widgetMageChild = getPrayerIconWidgetChild(widgetMage);
        Widget widgetRangeChild = getPrayerIconWidgetChild(widgetRange);
        Widget widgetMeleeChild = getPrayerIconWidgetChild(widgetMelee);

        if (widgetMageChild == null || widgetRangeChild == null || widgetMeleeChild == null) {
            return false;
        }

        setWidgetIcon(widgetMageChild, SpriteID.PRAYER_PROTECT_FROM_MISSILES);
        setWidgetIcon(widgetRangeChild, SpriteID.PRAYER_PROTECT_FROM_MELEE);
        setWidgetIcon(widgetMeleeChild, SpriteID.PRAYER_PROTECT_FROM_MAGIC);

        return true;
    }

    protected boolean resetPrayer() {
        Widget widgetMage = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
        Widget widgetRange = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES);
        Widget widgetMelee = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE);

        if (widgetMage == null || widgetRange == null || widgetMelee == null) {
            return false;
        }

        Widget widgetMageChild = getPrayerIconWidgetChild(widgetMage);
        Widget widgetRangeChild = getPrayerIconWidgetChild(widgetRange);
        Widget widgetMeleeChild = getPrayerIconWidgetChild(widgetMelee);

        if (widgetMageChild == null || widgetRangeChild == null || widgetMeleeChild == null) {
            return false;
        }

        // range -> mage
        setWidgetPosition(widgetMage, originalMagePosition.getX(), originalMagePosition.getY());
        // melee - > range
        setWidgetPosition(widgetRange, originalRangePosition.getX(), originalRangePosition.getY());
        // mage - > melee
        setWidgetPosition(widgetMelee, originalMeleePosition.getX(), originalMeleePosition.getY());

        setWidgetIcon(widgetMageChild, SpriteID.PRAYER_PROTECT_FROM_MAGIC);
        setWidgetIcon(widgetRangeChild, SpriteID.PRAYER_PROTECT_FROM_MISSILES);
        setWidgetIcon(widgetMeleeChild, SpriteID.PRAYER_PROTECT_FROM_MELEE);

        return true;
    }


    private void setWidgetPosition(final Widget widget, int x, int y) {
        final Runnable r = () -> {
            widget.setXPositionMode(WidgetPositionMode.ABSOLUTE_LEFT);
            widget.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
            widget.setOriginalX(x);
            widget.setOriginalY(y);
            widget.revalidate();
        };
        if (client.isClientThread()) {
            r.run();
        } else {
            clientThread.invoke(r);
        }
    }

    private void setWidgetIcon(final Widget widget, int iconId) {
        final Runnable r = () -> {
            widget.setSpriteId(iconId);
            widget.revalidate();
        };
        if (client.isClientThread()) {
            r.run();
        } else {
            clientThread.invoke(r);
        }
    }

    private static Widget getPrayerIconWidgetChild(Widget widget) {
        Widget[] children = widget.getDynamicChildren();
        if (children != null && children.length > 1) {
            return children[1];
        }
        return null;
    }
}
