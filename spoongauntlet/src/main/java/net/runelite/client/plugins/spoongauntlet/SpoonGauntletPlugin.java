package net.runelite.client.plugins.spoongauntlet;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.util.*;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Gauntlet",
        description = "All-in-one plugin for the Gauntlet.",
        tags = {"Gauntlet"},
        enabledByDefault = false
)
public class SpoonGauntletPlugin extends Plugin {
    public BufferedImage imageCrystalDeposit;
    public BufferedImage imagePhrenRoots;
    public BufferedImage imageFishingSpot;
    public BufferedImage imageGrymRoot;
    public BufferedImage imageLinumTirinum;
    GameObject singingBowl;
    GameObject bossDoor;
    private boolean newInstance = false;
    @Inject
    private ChatMessageManager chatMessageManager;
    public BufferedImage imageAttackRange;
    public BufferedImage imageAttackMage;
    public BufferedImage imageAttackPrayer;
    public ArrayList<GameObject> resources = new ArrayList<GameObject>();
    public ArrayList<GameObject> utilities = new ArrayList<GameObject>();
    public Set<Projectile> projectiles = new HashSet();
    public int bossCounter = 0;
    public SpoonGauntletPlugin.BossAttackPhase currentPhase;
    public int playerCounter;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private SpoonGauntletOverlay overlay;
    @Inject
    private GauntletSpriteOverlay spriteOverlay;
    @Inject
    private PrayerWidgetOverlay prayerOverlay;
    @Inject
    private SkillIconManager iconManager;
    @Inject
    private SpoonGauntletConfig config;
    @Inject
    private GauntletTimer timer;
    @Inject
    private ResourceTracker resourceTracker;
    private boolean timerVisible;
    private boolean leaguesFlag;
    public boolean tornadoesActive;
    public int tornadoTicks;
    public boolean completeStartup;
    private ArrayList<Integer> projectileHashes;
    int[] wrong_style;
    private String previousClick;
    private int weaponID;
    private boolean hunllefStomped = false;
    private static Clip clip;
    public int bossTicks = 0;
    public ArrayList<GauntletResource> resourcesTracked = new ArrayList<GauntletResource>();
    public int fishNeeded;
    public int herbsNeeded;
    public int shardsNeeded;
    public int barkNeeded;
    public int woolNeeded;
    public int oreNeeded;
    public int framesNeeded;
    public int lastFish;
    public int lastHerbs;
    public int lastShards;
    public int lastBark;
    public int lastWool;
    public int lastOre;
    public int lastFrames;
    public boolean corrupted = false;
    public boolean gauntletStarted = false;
    public boolean hasRawFish = false;

    private boolean mirrorMode;

    public SpoonGauntletPlugin() {
        currentPhase = SpoonGauntletPlugin.BossAttackPhase.UNKNOWN;
        playerCounter = 6;
        timerVisible = true;
        leaguesFlag = true;
        tornadoesActive = false;
        tornadoTicks = 20;
        completeStartup = false;
        projectileHashes = new ArrayList();
        wrong_style = new int[0];
        previousClick = "";
        weaponID = 0;
        clip = null;
        hunllefStomped = false;
    }

    @Provides
    SpoonGauntletConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SpoonGauntletConfig.class);
    }

    protected void startUp() {
        resourcesTracked = new ArrayList<>(Arrays.asList(new GauntletResource(23866,0), new GauntletResource(23878,0), new GauntletResource(23876,0),
                new GauntletResource(23877,0), new GauntletResource(23871,0), new GauntletResource(23874,0), new GauntletResource(23875,0)));
        loadImages(config.iconSize());
        resetCounters();
        timerVisible = config.displayTimerWidget();
        timer.resetStates();
        if (timerVisible) {
            overlayManager.add(timer);
        }

        wrong_style = new int[0];
        projectileHashes.clear();
        previousClick = "";
        weaponID = 0;
        overlayManager.add(overlay);
        overlayManager.add(spriteOverlay);
        overlayManager.add(prayerOverlay);
        overlayManager.add(resourceTracker);
        leaguesFlag = true;
        clip = null;
        hunllefStomped = false;
        if (client.getGameState() != GameState.STARTING && client.getGameState() != GameState.UNKNOWN) {
            completeStartup = false;
            clientThread.invoke(() -> {
                timer.initStates();
                completeStartup = true;
            });
        } else {
            completeStartup = true;
        }
        corrupted = false;
        gauntletStarted = false;
        hasRawFish = false;
    }

    protected void shutDown() {
        corrupted = false;
        gauntletStarted = false;
        fishNeeded = 0;
        herbsNeeded = 0;
        shardsNeeded = 0;
        barkNeeded = 0;
        woolNeeded = 0;
        oreNeeded = 0;
        framesNeeded = 0;
        lastFish = 0;
        lastHerbs = 0;
        lastShards = 0;
        lastBark = 0;
        lastWool = 0;
        lastOre = 0;
        lastFrames = 0;
        resetCounters();
        timer.resetStates();
        wrong_style = new int[0];
        projectileHashes.clear();
        previousClick = "";
        weaponID = 0;
        leaguesFlag = true;
        clip = null;
        hunllefStomped = false;
        hasRawFish = false;
        if (timerVisible) {
            overlayManager.remove(timer);
            timerVisible = false;
        }

        overlayManager.remove(overlay);
        overlayManager.remove(prayerOverlay);
        overlayManager.remove(spriteOverlay);
        overlayManager.remove(resourceTracker);
    }

    private void loadImages(int imageSize) {
        imageCrystalDeposit = ImageUtil.resizeImage(iconManager.getSkillImage(Skill.MINING, true), imageSize, imageSize);
        imagePhrenRoots = ImageUtil.resizeImage(iconManager.getSkillImage(Skill.WOODCUTTING, true), imageSize, imageSize);
        imageFishingSpot = ImageUtil.resizeImage(iconManager.getSkillImage(Skill.FISHING, true), imageSize, imageSize);
        imageGrymRoot = ImageUtil.resizeImage(iconManager.getSkillImage(Skill.HERBLORE, true), imageSize, imageSize);
        imageLinumTirinum = ImageUtil.resizeImage(iconManager.getSkillImage(Skill.FARMING, true), imageSize, imageSize);
        imageAttackMage = ImageUtil.resizeImage(iconManager.getSkillImage(Skill.MAGIC, true), imageSize, imageSize);
        imageAttackRange = ImageUtil.resizeImage(iconManager.getSkillImage(Skill.RANGED, true), imageSize, imageSize);
        imageAttackPrayer = ImageUtil.resizeImage(iconManager.getSkillImage(Skill.PRAYER, true), imageSize, imageSize);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup() != null && event.getKey() != null && event.getGroup().equals("SpoonGauntlet")) {
            if (event.getKey().equals("displayTimerWidget")) {
                if (config.displayTimerWidget() && !timerVisible) {
                    overlayManager.add(timer);
                    timerVisible = true;
                } else if (!config.displayTimerWidget() && timerVisible) {
                    overlayManager.remove(timer);
                    timerVisible = false;
                }
            } else if (event.getKey().equals("iconSize")) {
                loadImages(config.iconSize());
            }else if(event.getKey().equals("gauntletPrayerNotifierVolume")){
                if(clip != null) {
                    FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null) {
                        control.setValue((float)(config.gauntletPrayerNotifierVolume() / 2 - 45));
                    }
                }
            }else if(event.getKey().equals("gauntletStompNotifierVolume")){
                if(clip != null) {
                    FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null) {
                        control.setValue((float)(config.gauntletStompNotifierVolume() / 2 - 45));
                    }
                }
            }else if((event.getKey().equals("fishCount") || event.getKey().equals("potionCount") || event.getKey().equals("armorTier") || event.getKey().equals("halberdTier")
                    || event.getKey().equals("bowTier") || event.getKey().equals("staffTier") || event.getKey().equals("extraShards") || event.getKey().equals("teleportCrystalCount"))
                    && !corrupted){
                framesNeeded = 0;
                fishNeeded = config.fishCount();
                herbsNeeded = config.potionCount();
                shardsNeeded = (config.potionCount() * 20) + config.extraShards();
                if(config.armorTier() == SpoonGauntletConfig.GauntletTier.TIER1){
                    barkNeeded = 3;
                    woolNeeded = 3;
                    oreNeeded = 3;
                    shardsNeeded += 120;
                }else if(config.armorTier() == SpoonGauntletConfig.GauntletTier.TIER2){
                    barkNeeded = 7;
                    woolNeeded = 7;
                    oreNeeded = 7;
                    shardsNeeded += 300;
                }else if(config.armorTier() == SpoonGauntletConfig.GauntletTier.TIER3){
                    barkNeeded = 13;
                    woolNeeded = 13;
                    oreNeeded = 13;
                    shardsNeeded += 540;
                }else {
                    barkNeeded = 0;
                    woolNeeded = 0;
                    oreNeeded = 0;
                }
                if(config.halberdTier() == SpoonGauntletConfig.GauntletTier.TIER1){
                    framesNeeded++;
                    shardsNeeded += 20;
                }else if(config.halberdTier() == SpoonGauntletConfig.GauntletTier.TIER2 || config.halberdTier() == SpoonGauntletConfig.GauntletTier.TIER3){
                    framesNeeded++;
                    shardsNeeded += 80;
                }
                if(config.bowTier() == SpoonGauntletConfig.GauntletTier.TIER1){
                    framesNeeded++;
                    shardsNeeded += 20;
                }else if(config.bowTier() == SpoonGauntletConfig.GauntletTier.TIER2 || config.bowTier() == SpoonGauntletConfig.GauntletTier.TIER3){
                    framesNeeded++;
                    shardsNeeded += 80;
                }
                if(config.staffTier() == SpoonGauntletConfig.GauntletTier.TIER1){
                    framesNeeded++;
                    shardsNeeded += 20;
                }else if(config.staffTier() == SpoonGauntletConfig.GauntletTier.TIER2 || config.staffTier() == SpoonGauntletConfig.GauntletTier.TIER3){
                    framesNeeded++;
                    shardsNeeded += 80;
                }
                shardsNeeded += config.teleportCrystalCount() * 40;
            }else if((event.getKey().equals("corruptedFishCount") || event.getKey().equals("corruptedPotionCount") || event.getKey().equals("corruptedArmorTier")
                    || event.getKey().equals("corruptedHalberdTier") || event.getKey().equals("corruptedBowTier") || event.getKey().equals("corruptedStaffTier")
                    || event.getKey().equals("corruptedExtraShards") || event.getKey().equals("corruptedTeleportCrystalCount")) && corrupted){
                framesNeeded = 0;
                fishNeeded = config.corruptedFishCount();
                herbsNeeded = config.corruptedPotionCount();
                shardsNeeded = (config.corruptedPotionCount() * 20) + config.corruptedExtraShards();
                if(config.corruptedArmorTier() == SpoonGauntletConfig.GauntletTier.TIER1){
                    barkNeeded = 3;
                    woolNeeded = 3;
                    oreNeeded = 3;
                    shardsNeeded += 120;
                }else if(config.corruptedArmorTier() == SpoonGauntletConfig.GauntletTier.TIER2){
                    barkNeeded = 7;
                    woolNeeded = 7;
                    oreNeeded = 7;
                    shardsNeeded += 300;
                }else if(config.corruptedArmorTier() == SpoonGauntletConfig.GauntletTier.TIER3){
                    barkNeeded = 13;
                    woolNeeded = 13;
                    oreNeeded = 13;
                    shardsNeeded += 540;
                }else {
                    barkNeeded = 0;
                    woolNeeded = 0;
                    oreNeeded = 0;
                }
                if(config.corruptedHalberdTier() == SpoonGauntletConfig.GauntletTier.TIER1){
                    framesNeeded++;
                    shardsNeeded += 20;
                }else if(config.corruptedHalberdTier() == SpoonGauntletConfig.GauntletTier.TIER2 || config.corruptedHalberdTier() == SpoonGauntletConfig.GauntletTier.TIER3){
                    framesNeeded++;
                    shardsNeeded += 80;
                }
                if(config.corruptedBowTier() == SpoonGauntletConfig.GauntletTier.TIER1){
                    framesNeeded++;
                    shardsNeeded += 20;
                }else if(config.corruptedBowTier() == SpoonGauntletConfig.GauntletTier.TIER2 || config.corruptedBowTier() == SpoonGauntletConfig.GauntletTier.TIER3){
                    framesNeeded++;
                    shardsNeeded += 80;
                }
                if(config.corruptedStaffTier() == SpoonGauntletConfig.GauntletTier.TIER1){
                    framesNeeded++;
                    shardsNeeded += 20;
                }else if(config.corruptedStaffTier() == SpoonGauntletConfig.GauntletTier.TIER2 || config.corruptedStaffTier() == SpoonGauntletConfig.GauntletTier.TIER3){
                    framesNeeded++;
                    shardsNeeded += 80;
                }
                shardsNeeded += config.corruptedTeleportCrystalCount() * 40;
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (completeStartup) {
            timer.checkStates(true);
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        if (GauntletUtils.isBoss(npc)) {
            resetCounters();
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        NPC npc = event.getNpc();
        if (GauntletUtils.isBoss(npc)) {
            resetCounters();
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        if (GauntletUtils.inBoss(client)) {
            NPCComposition oldComp = event.getOld();
            NPCComposition newComp = event.getNpc().getComposition();
            if (oldComp != null && newComp != null && oldComp.getOverheadIcon() != null && newComp.getOverheadIcon() != null && oldComp.getOverheadIcon() != newComp.getOverheadIcon()) {
                playerCounter = 6;
            }
        }
    }

    private void resetCounters() {
        bossCounter = 0;
        currentPhase = SpoonGauntletPlugin.BossAttackPhase.UNKNOWN;
        playerCounter = 6;
        tornadoesActive = false;
        tornadoTicks = 20;
        projectiles.clear();
    }

    public void doAttack(SpoonGauntletPlugin.BossAttack style) {
        if (style == SpoonGauntletPlugin.BossAttack.PRAYER) {
            if (config.uniquePrayerAudio()) {
                client.playSoundEffect(227);
            }

            style = SpoonGauntletPlugin.BossAttack.MAGIC;
        }

        if (style == SpoonGauntletPlugin.BossAttack.LIGHTNING) {
            --bossCounter;
        } else if (style == SpoonGauntletPlugin.BossAttack.RANGE) {
            if (currentPhase != SpoonGauntletPlugin.BossAttackPhase.RANGE) {
                currentPhase = SpoonGauntletPlugin.BossAttackPhase.RANGE;
                bossCounter = 3;
            } else {
                --bossCounter;
            }
        } else if (style == SpoonGauntletPlugin.BossAttack.MAGIC) {
            if (currentPhase != SpoonGauntletPlugin.BossAttackPhase.MAGIC) {
                currentPhase = SpoonGauntletPlugin.BossAttackPhase.MAGIC;
                bossCounter = 3;
            } else {
                --bossCounter;
            }
        }

        if (bossCounter <= 0) {
            SpoonGauntletPlugin.BossAttackPhase nextPhase;
            switch (currentPhase) {
                case MAGIC:
                    bossCounter = 4;
                    nextPhase = SpoonGauntletPlugin.BossAttackPhase.RANGE;
                    if (config.gauntletPrayerNotifier() == SpoonGauntletConfig.gauntletPrayerNotifierMode.LOUD) {
                        try {
                            AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(SpoonGauntletPlugin.class.getResourceAsStream("range.wav")));
                            AudioFormat format = stream.getFormat();
                            DataLine.Info info = new DataLine.Info(Clip.class, format);
                            clip = (Clip) AudioSystem.getLine(info);
                            clip.open(stream);
                            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                            if (control != null) {
                                control.setValue((float) (config.gauntletPrayerNotifierVolume() / 2 - 45));
                            }
                            clip.setFramePosition(0);
                            clip.start();
                        } catch (Exception var6) {
                            clip = null;
                        }
                    } else if (config.gauntletPrayerNotifier() == SpoonGauntletConfig.gauntletPrayerNotifierMode.DING) {
                        client.playSoundEffect(1623, config.gauntletPrayerNotifierVolume() / 2);
                    }
                    break;
                case RANGE:
                    bossCounter = 4;
                    nextPhase = SpoonGauntletPlugin.BossAttackPhase.MAGIC;
                    if (config.gauntletPrayerNotifier() == SpoonGauntletConfig.gauntletPrayerNotifierMode.LOUD) {
                        try {
                            AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(SpoonGauntletPlugin.class.getResourceAsStream("mage.wav")));
                            AudioFormat format = stream.getFormat();
                            DataLine.Info info = new DataLine.Info(Clip.class, format);
                            clip = (Clip) AudioSystem.getLine(info);
                            clip.open(stream);
                            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                            if (control != null) {
                                control.setValue((float) (config.gauntletPrayerNotifierVolume() / 2 - 45));
                            }
                            clip.setFramePosition(0);
                            clip.start();
                        } catch (Exception var6) {
                            clip = null;
                        }
                    } else if (config.gauntletPrayerNotifier() == SpoonGauntletConfig.gauntletPrayerNotifierMode.DING) {
                        client.playSoundEffect(1623, config.gauntletPrayerNotifierVolume() / 2);
                    }
                    break;
                default:
                    bossCounter = 0;
                    nextPhase = SpoonGauntletPlugin.BossAttackPhase.UNKNOWN;
            }

            currentPhase = nextPhase;
        }
        bossTicks = 6;
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (GauntletUtils.inBoss(client)) {
            String s = event.getMenuTarget().toLowerCase();
            if (s.contains("wield")) {
                weaponID = event.getId();
            }
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if(config.hideEnterBoss() && hasRawFish && gauntletStarted && !client.isMenuOpen() && (event.getOption().equals("Pass") || event.getOption().equals("Quick-pass")) &&
                event.getTarget().contains("Barrier")) {
            MenuEntry[] entries = client.getMenuEntries();
            MenuEntry[] newEntries = new MenuEntry[entries.length - 1];
            System.arraycopy(entries, 0, newEntries, 0, newEntries.length);
            client.setMenuEntries(newEntries);
        }
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        if (!GauntletUtils.inBoss(client)) {
            projectileHashes.clear();
        } else {
            Player p = client.getLocalPlayer();
            assert p != null;
            weaponID = p.getPlayerComposition().getEquipmentId(KitType.WEAPON);
        }

        /*if (client.isMirrored() && !mirrorMode) {
            overlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(overlay);
            overlayManager.add(overlay);
            spriteOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(spriteOverlay);
            overlayManager.add(spriteOverlay);
            prayerOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(prayerOverlay);
            overlayManager.add(prayerOverlay);
            mirrorMode = true;
        }*/
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Actor actor = event.getActor();
        int id;
        if (actor instanceof Player && GauntletUtils.inBoss(client)) {
            Player p = (Player)actor;
            if (p.getName().equals(client.getLocalPlayer().getName())) {
                id = p.getAnimation();
                if (id != -1) {
                    wrong_style = new int[0];
                    Iterator var5 = client.getNpcs().iterator();

                    label82:
                    while(var5.hasNext()) {
                        NPC npc = (NPC)var5.next();
                        if (GauntletUtils.isBoss(npc)) {
                            NPCComposition comp = npc.getComposition();
                            if (comp != null) {
                                HeadIcon prayer = comp.getOverheadIcon();
                                if (prayer != null) {
                                    switch(prayer) {
                                        case MELEE:
                                            wrong_style = GauntletUtils.MELEE_ANIMATIONS;
                                            break label82;
                                        case RANGED:
                                            wrong_style = GauntletUtils.RANGE_ANIMATIONS;
                                            break label82;
                                        case MAGIC:
                                            wrong_style = GauntletUtils.MAGE_ANIMATIONS;
                                            break label82;
                                        default:
                                            wrong_style = new int[0];
                                            break label82;
                                    }
                                }
                            }
                        }
                    }

                    if (GauntletUtils.arrayContainsInteger(GauntletUtils.PLAYER_ANIMATIONS, id) && !GauntletUtils.arrayContainsInteger(wrong_style, id)) {
                        --playerCounter;
                    }
                }
            }

            if (playerCounter <= 0) {
                playerCounter = 6;
            }
        }

        if (actor instanceof NPC) {
            NPC npc = (NPC)actor;
            if (GauntletUtils.isBoss(npc)) {
                id = npc.getAnimation();
                if (id == 8418) {
                    doAttack(SpoonGauntletPlugin.BossAttack.LIGHTNING);
                }else if(config.stompNotifier() && event.getActor().getAnimation() == 8420){
                    hunllefStomped = true;
                }
            }
        }
    }

    @Subscribe
    private void onHitsplatApplied(HitsplatApplied event){
        if(event.getActor() instanceof Player && event.getActor() != null && event.getActor() == client.getLocalPlayer()){
            if(event.getHitsplat().isMine() && hunllefStomped){
                hunllefStomped = false;
                if(event.getHitsplat().getHitsplatType() == Hitsplat.HitsplatType.DAMAGE_ME) {
                    if (event.getHitsplat().getAmount() >= 40) {
                        try {
                            AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(SpoonGauntletPlugin.class.getResourceAsStream("JesusChrist.wav")));
                            AudioFormat format = stream.getFormat();
                            DataLine.Info info = new DataLine.Info(Clip.class, format);
                            clip = (Clip) AudioSystem.getLine(info);
                            clip.open(stream);
                            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                            if (control != null) {
                                control.setValue((float) (config.gauntletStompNotifierVolume() / 2 - 45));
                            }
                            clip.setFramePosition(0);
                            clip.start();
                        } catch (Exception var6) {
                            clip = null;
                        }
                    } else {
                        try {
                            AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(SpoonGauntletPlugin.class.getResourceAsStream("afka.wav")));
                            AudioFormat format = stream.getFormat();
                            DataLine.Info info = new DataLine.Info(Clip.class, format);
                            clip = (Clip) AudioSystem.getLine(info);
                            clip.open(stream);
                            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                            if (control != null) {
                                control.setValue((float) (config.gauntletPrayerNotifierVolume() / 2 - 45));
                            }
                            clip.setFramePosition(0);
                            clip.start();
                        } catch (Exception var6) {
                            clip = null;
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (completeStartup) {
            timer.checkStates(false);
        }

        Set<Projectile> newProjectiles = new HashSet();
        Iterator var3 = client.getProjectiles().iterator();

        while(var3.hasNext()) {
            Projectile projectile = (Projectile)var3.next();
            newProjectiles.add(projectile);
            if (!projectiles.contains(projectile)) {
                int id = projectile.getId();
                if (GauntletUtils.arrayContainsInteger(GauntletUtils.PROJECTILE_MAGIC, id)) {
                    doAttack(SpoonGauntletPlugin.BossAttack.MAGIC);
                } else if (GauntletUtils.arrayContainsInteger(GauntletUtils.PROJECTILE_PRAYER, id)) {
                    doAttack(SpoonGauntletPlugin.BossAttack.PRAYER);
                } else if (GauntletUtils.arrayContainsInteger(GauntletUtils.PROJECTILE_RANGE, id)) {
                    doAttack(SpoonGauntletPlugin.BossAttack.RANGE);
                }
            }
        }

        projectiles.clear();
        projectiles = newProjectiles;
        if (!tornadoesActive) {
            var3 = client.getNpcs().iterator();

            while(var3.hasNext()) {
                NPC npc = (NPC)var3.next();
                if (GauntletUtils.isTornado(npc)) {
                    tornadoesActive = true;
                    tornadoTicks = 20;
                    break;
                }
            }
        } else {
            --tornadoTicks;
            if (tornadoTicks <= 0) {
                tornadoesActive = false;
                tornadoTicks = 20;
            }
        }

        if (GauntletUtils.inBoss(client) && bossTicks > 0) {
            --bossTicks;
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        final GameObject gameObject = event.getGameObject();
        final int id = gameObject.getId();

        if (RESOURCE_IDS.contains(id)) {
            resources.add(gameObject);
        } else if (UTILITY_IDS.contains(id)) {
            utilities.add(gameObject);
        }
    }

    @Subscribe
    public void onGameObjectChanged(GameObjectChanged event) {
        final GameObject gameObject = event.getGameObject();
        final int id = gameObject.getId();

        if (RESOURCE_IDS.contains(gameObject.getId())) {
            resources.remove(gameObject);
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        final GameObject gameObject = event.getGameObject();
        final int id = gameObject.getId();

        if (RESOURCE_IDS.contains(gameObject.getId())) {
            resources.remove(gameObject);
        } else if (UTILITY_IDS.contains(id)) {
            utilities.remove(gameObject);
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            resources.clear();
            utilities.clear();
        }

        if(event.getGameState() == GameState.LOGGED_IN) {
            if (client.isInInstancedRegion() && !gauntletStarted && client.getLocalPlayer() != null) {
                corrupted = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == 7768;
                if (corrupted) {
                    framesNeeded = 0;
                    fishNeeded = config.corruptedFishCount();
                    herbsNeeded = config.corruptedPotionCount();
                    shardsNeeded = (config.corruptedPotionCount() * 20) + config.corruptedExtraShards();
                    if (config.corruptedArmorTier() == SpoonGauntletConfig.GauntletTier.TIER1) {
                        barkNeeded = 3;
                        woolNeeded = 3;
                        oreNeeded = 3;
                        shardsNeeded += 120;
                    } else if (config.corruptedArmorTier() == SpoonGauntletConfig.GauntletTier.TIER2) {
                        barkNeeded = 7;
                        woolNeeded = 7;
                        oreNeeded = 7;
                        shardsNeeded += 300;
                    } else if (config.corruptedArmorTier() == SpoonGauntletConfig.GauntletTier.TIER3) {
                        barkNeeded = 13;
                        woolNeeded = 13;
                        oreNeeded = 13;
                        shardsNeeded += 540;
                    } else {
                        barkNeeded = 0;
                        woolNeeded = 0;
                        oreNeeded = 0;
                    }
                    if (config.corruptedHalberdTier() == SpoonGauntletConfig.GauntletTier.TIER1) {
                        framesNeeded++;
                        shardsNeeded += 20;
                    } else if (config.corruptedHalberdTier() == SpoonGauntletConfig.GauntletTier.TIER2 || config.corruptedHalberdTier() == SpoonGauntletConfig.GauntletTier.TIER3) {
                        framesNeeded++;
                        shardsNeeded += 80;
                    }
                    if (config.corruptedBowTier() == SpoonGauntletConfig.GauntletTier.TIER1) {
                        framesNeeded++;
                        shardsNeeded += 20;
                    } else if (config.corruptedBowTier() == SpoonGauntletConfig.GauntletTier.TIER2 || config.corruptedBowTier() == SpoonGauntletConfig.GauntletTier.TIER3) {
                        framesNeeded++;
                        shardsNeeded += 80;
                    }
                    if (config.corruptedStaffTier() == SpoonGauntletConfig.GauntletTier.TIER1) {
                        framesNeeded++;
                        shardsNeeded += 20;
                    } else if (config.corruptedStaffTier() == SpoonGauntletConfig.GauntletTier.TIER2 || config.corruptedStaffTier() == SpoonGauntletConfig.GauntletTier.TIER3) {
                        framesNeeded++;
                        shardsNeeded += 80;
                    }
                    shardsNeeded += config.corruptedTeleportCrystalCount() * 40;
                } else {
                    framesNeeded = 0;
                    fishNeeded = config.fishCount();
                    herbsNeeded = config.potionCount();
                    shardsNeeded = (config.potionCount() * 20) + config.extraShards();
                    if (config.armorTier() == SpoonGauntletConfig.GauntletTier.TIER1) {
                        barkNeeded = 3;
                        woolNeeded = 3;
                        oreNeeded = 3;
                        shardsNeeded += 120;
                    } else if (config.armorTier() == SpoonGauntletConfig.GauntletTier.TIER2) {
                        barkNeeded = 7;
                        woolNeeded = 7;
                        oreNeeded = 7;
                        shardsNeeded += 300;
                    } else if (config.armorTier() == SpoonGauntletConfig.GauntletTier.TIER3) {
                        barkNeeded = 13;
                        woolNeeded = 13;
                        oreNeeded = 13;
                        shardsNeeded += 540;
                    } else {
                        barkNeeded = 0;
                        woolNeeded = 0;
                        oreNeeded = 0;
                    }
                    if (config.halberdTier() == SpoonGauntletConfig.GauntletTier.TIER1) {
                        framesNeeded++;
                        shardsNeeded += 20;
                    } else if (config.halberdTier() == SpoonGauntletConfig.GauntletTier.TIER2 || config.halberdTier() == SpoonGauntletConfig.GauntletTier.TIER3) {
                        framesNeeded++;
                        shardsNeeded += 80;
                    }
                    if (config.bowTier() == SpoonGauntletConfig.GauntletTier.TIER1) {
                        framesNeeded++;
                        shardsNeeded += 20;
                    } else if (config.bowTier() == SpoonGauntletConfig.GauntletTier.TIER2 || config.bowTier() == SpoonGauntletConfig.GauntletTier.TIER3) {
                        framesNeeded++;
                        shardsNeeded += 80;
                    }
                    if (config.staffTier() == SpoonGauntletConfig.GauntletTier.TIER1) {
                        framesNeeded++;
                        shardsNeeded += 20;
                    } else if (config.staffTier() == SpoonGauntletConfig.GauntletTier.TIER2 || config.staffTier() == SpoonGauntletConfig.GauntletTier.TIER3) {
                        framesNeeded++;
                        shardsNeeded += 80;
                    }
                    shardsNeeded += config.teleportCrystalCount() * 40;
                }
                lastFish = 0;
                lastHerbs = 0;
                lastShards = 0;
                lastBark = 0;
                lastWool = 0;
                lastOre = 0;
                lastFrames = 0;
                gauntletStarted = true;
                resourcesTracked = new ArrayList<>(Arrays.asList(new GauntletResource(23866, 0), new GauntletResource(23878, 0), new GauntletResource(23876, 0),
                        new GauntletResource(23877, 0), new GauntletResource(23871, 0), new GauntletResource(23874, 0), new GauntletResource(23875, 0)));
            } else if (!GauntletUtils.inRaid(client) && gauntletStarted) {
                lastFish = 0;
                lastHerbs = 0;
                lastShards = 0;
                lastBark = 0;
                lastWool = 0;
                lastOre = 0;
                lastFrames = 0;
                gauntletStarted = false;
                corrupted = false;
                resourcesTracked = new ArrayList<>(Arrays.asList(new GauntletResource(23866, 0), new GauntletResource(23878, 0), new GauntletResource(23876, 0),
                        new GauntletResource(23877, 0), new GauntletResource(23871, 0), new GauntletResource(23874, 0), new GauntletResource(23875, 0)));
            }
        }
    }

    @Subscribe
    protected void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getItemContainer() == client.getItemContainer(InventoryID.INVENTORY) && GauntletUtils.inRaid(client)){
            hasRawFish = client.getItemContainer(InventoryID.INVENTORY).count(23872) > 0 && client.getItemContainer(InventoryID.INVENTORY).count(23874) == 0;

            int shardCount = client.getItemContainer(InventoryID.INVENTORY).count(23824) + client.getItemContainer(InventoryID.INVENTORY).count(23866);
            int barkCount = client.getItemContainer(InventoryID.INVENTORY).count(23838) + client.getItemContainer(InventoryID.INVENTORY).count(23878);
            int woolCount = client.getItemContainer(InventoryID.INVENTORY).count(23836) + client.getItemContainer(InventoryID.INVENTORY).count(23876);
            int oreCount = client.getItemContainer(InventoryID.INVENTORY).count(23837) + client.getItemContainer(InventoryID.INVENTORY).count(23877);
            int framesCount = client.getItemContainer(InventoryID.INVENTORY).count(23834) + client.getItemContainer(InventoryID.INVENTORY).count(23871);
            int fishCount = client.getItemContainer(InventoryID.INVENTORY).count(23872) + client.getItemContainer(InventoryID.INVENTORY).count(23874);
            int herbCount = client.getItemContainer(InventoryID.INVENTORY).count(23835) + client.getItemContainer(InventoryID.INVENTORY).count(23875);

            for(GauntletResource resource : resourcesTracked){
                if(resource.id == 23866 && shardCount != resource.count) {
                    if(shardCount > lastShards) {
                        resource.count += shardCount - lastShards;
                    }
                    lastShards = shardCount;
                }else if(resource.id == 23878 && barkCount != resource.count) {
                    if(barkCount > lastBark) {
                        resource.count += barkCount - lastBark;
                    }
                    lastBark = barkCount;
                }else if(resource.id == 23876 && woolCount != resource.count) {
                    if(woolCount > lastWool) {
                        resource.count += woolCount - lastWool;
                    }
                    lastWool = woolCount;
                }else if(resource.id == 23877 && oreCount != resource.count) {
                    if(oreCount > lastOre) {
                        resource.count += oreCount - lastOre;
                    }
                    lastOre = oreCount;
                }else if(resource.id == 23871 && framesCount != resource.count) {
                    if(framesCount > lastFrames) {
                        resource.count += framesCount - lastFrames;
                    }
                    lastFrames = framesCount;
                }else if(resource.id == 23874 && fishCount != resource.count) {
                    if(fishCount > lastFish) {
                        resource.count += fishCount - lastFish;
                    }
                    lastFish = fishCount;
                }else if(resource.id == 23875 && herbCount != resource.count) {
                    if(herbCount > lastHerbs) {
                        resource.count += herbCount - lastHerbs;
                    }
                    lastHerbs = herbCount;
                }
            }
        }
    }

    public OverlayManager getOverlayManager() {
        return overlayManager;
    }

    public static enum BossAttack {
        MAGIC,
        RANGE,
        PRAYER,
        LIGHTNING;
    }

    public static enum BossAttackPhase {
        MAGIC,
        RANGE,
        UNKNOWN;
    }

    private static final Set<Integer> RESOURCE_IDS = Set.of(
            ObjectID.CRYSTAL_DEPOSIT, ObjectID.CORRUPT_DEPOSIT,
            ObjectID.PHREN_ROOTS, ObjectID.PHREN_ROOTS_36066,
            ObjectID.FISHING_SPOT_36068, ObjectID.FISHING_SPOT_35971,
            ObjectID.GRYM_ROOT, ObjectID.GRYM_ROOT_36070,
            ObjectID.LINUM_TIRINUM, ObjectID.LINUM_TIRINUM_36072
    );

    private static final Set<Integer> UTILITY_IDS = Set.of(
            ObjectID.SINGING_BOWL_35966, ObjectID.SINGING_BOWL_36063,
            ObjectID.RANGE_35980, ObjectID.RANGE_36077,
            ObjectID.WATER_PUMP_35981, ObjectID.WATER_PUMP_36078
    );
}