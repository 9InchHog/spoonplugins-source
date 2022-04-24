package net.runelite.client.plugins.coxadditions;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import com.openosrs.client.util.WeaponStyle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.api.util.Text;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.coxadditions.overlays.*;
import net.runelite.client.plugins.coxadditions.utils.HealingPoolInfo;
import net.runelite.client.plugins.coxadditions.utils.ShamanInfo;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.sound.sampled.Clip;
import java.util.*;
import java.util.function.Predicate;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Cox Additions",
        description = "Additional plugins for the Chambers of Xeric",
        tags = {"xeric", "olm", "chambers", "cox", "spoon"},
        enabledByDefault = false
)
@Slf4j
public class CoxAdditionsPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private CoxAdditionsConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private OlmOverlay olmOverlay;

    @Inject
    private CoxAdditionsOverlay overlay;

    @Inject
    private VespEnhanceOverlay vespEnahnceOverlay;

    @Inject
    private VanguardCycleOverlay vanguardCycleOverlay;

    @Inject
    private ShortcutOverlay shortcutOverlay;

    @Inject
    private OlmOrbOverlay orbOverlay;

    @Inject
    private OrbPrayerTabOverlay orbTabOverlay;

	@Inject
    private OlmHpPanelOverlay olmHpPanelOverlay;

    @Inject
    private OlmSideOverlay olmSideOverlay;

    @Inject
    private CoxItemOverlay itemOverlay;

    @Inject
    private InstanceTimerOverlay instanceTimerOverlay;

    @Inject
    private MeatTreeCycleOverlay meatTreeCycleOverlay;

    @Inject
    private OlmPhasePanel olmPhasePanel;

    @Inject
    private EventBus eventBus;

    private final ArrayListMultimap<String, Integer> optionIndexes = ArrayListMultimap.create();

    public boolean handCripple;
    public int crippleTimer = 45;
    public NPC meleeHand;
	public NPC mageHand;
	public int meleeHandHp = -1;
    public int mageHandHp = -1;
	public int mageHandLastRatio = 0;
	public int mageHandLastHealthScale = 0;
	public int meleeHandLastRatio = 0;
	public int meleeHandLastHealthScale = 0;

    public ArrayList<String> playerEntry = new ArrayList<String>();
    public ArrayList<String> playerNames = new ArrayList<String>();
    public ArrayList<String> customTexts = new ArrayList<String>();
    public ArrayList<NPC> intNPC = new ArrayList<NPC>();
    public boolean namedTarget;

    public boolean coxSpade;
    public boolean coxDibbler;
    public boolean coxRake;

    private static Clip clip;

    public int acidTicks;
    public int burningTicks;
    public int crystalsTicks;
    public boolean acidActive;
    public boolean burningActive;
    public boolean crystalsActive;

    public ArrayList<ShamanInfo> shamanInfoList = new ArrayList<>();

    public GameObject coxHerb1;
    public int coxHerbTimer1;
    public GameObject coxHerb2;
    public int coxHerbTimer2;

    public ArrayList<Integer> offHandId = new ArrayList<Integer>(Arrays.asList(
            8850, 24142, 12954, 24143, 19722, 23230, 22322, 24186, 3842, 12610, 12608, 12612, 6889, 12817, 12821, 12825, 11283, 20714, 20716, 3844, 3840, 21633, 21000, 22002, 11926,
            12807, 11924, 12806, 25818));
    private int weaponId;

    public ArrayList<HealingPoolInfo> olmHealingPools = new ArrayList<>();

    private NPC vasa;
    public int vasaCrystalTicks;
    public boolean vasaAtCrystal;

    public boolean vespAlive = false;
    public boolean prayerEnhanceActive = false;
    public int prayerEnhanceTicks = 7;
    public boolean vespDied = false;

    public boolean meatTreeAlive = false;
    public NPC meatTree = null;
    public boolean smallMuttaAlive = false;
    public NPC smallMutta = null;
	public int lastRatio = 0;
	public int lastHealthScale = 0;
	public boolean startedChopping = false;
	public int ticksToChop = 5;

    public int instanceTimer = 3;
    public boolean isInstanceTimerRunning = false;

    public String olmPhase = "";
    public NPC olmHead = null;
    public boolean olmSpawned = false;

    public List<String> tlList = new ArrayList<>();
    public  List<String> bossList = Arrays.asList("tekton", "jewelled crab", "scavenger beast", "ice demon", "lizardman shaman", "vanguard", "vespula", "deathly ranger", "deathly mage",
            "vasa nistirio", "skeletal mystic", "muttadile");

    public boolean vangsActive = false;
    public int vangsTicks = 1;
    public int vangs4Ticks = 1;
    public boolean vangsAlive = false;

    @Getter
    private final List<TileObject> shortcut = new ArrayList<>();
    @Getter
    private boolean highlightShortcuts;

    public String orbStyle = "";
    public int orbTicks = 0;

    @Getter
    private LocalPoint olmTile = null;

    public ArrayList<Integer> chestHighlightIdList = new ArrayList<>();
    public ArrayList<Integer> chestHighlightIdList2 = new ArrayList<>();
	
	public ArrayList<NPC> ropeNpcs = new ArrayList<>();

    public int ticksSinceHPRegen;
    public boolean rapidHealActive;

    public ArrayList<GroundObject> rope = new ArrayList<>();
    public int ropeSpawnDelay = 0;

    public String portalBuddy = "";
    public int portalTicks = 0;

    private static final Set<String> BATS = ImmutableSet.of(
            "guanic bat",
            "prael bat",
            "giral bat",
            "phluxia bat",
            "kryket bat",
            "murng bat",
            "psykk bat");

    private boolean mirrorMode;

    @Provides
    CoxAdditionsConfig provideConfig(ConfigManager configManager) {
        return (CoxAdditionsConfig) configManager.getConfig(CoxAdditionsConfig.class);
    }

    private void reset() {
        meleeHand = null;
		mageHand = null;
        crippleTimer = 45;
        handCripple = false;
        meleeHandHp = -1;
        mageHandHp = -1;
		mageHandLastRatio = 0;
		mageHandLastHealthScale = 0;
		meleeHandLastRatio = 0;
		meleeHandLastHealthScale = 0;

        playerEntry.clear();
        playerNames.clear();
        customTexts.clear();
        intNPC.clear();
        namedTarget = false;

        coxSpade = false;
        coxDibbler = false;
        coxRake = false;

        clip = null;

        acidTicks = 23;
        acidActive = false;
        burningTicks = 41;
        burningActive = false;
        crystalsTicks = 32;
        crystalsActive = false;

		shamanInfoList.clear();

        coxHerb1 = null;
        coxHerbTimer1 = 16;
        coxHerb2 = null;
        coxHerbTimer2 = 16;

        weaponId = 0;

        olmHealingPools.clear();

        vasa = null;
        vasaCrystalTicks = 0;
        vasaAtCrystal = false;

        vespAlive = false;
        prayerEnhanceActive = false;
        prayerEnhanceTicks = 7;
        vespDied = false;

        meatTreeAlive = false;
        meatTree = null;
        smallMuttaAlive = false;
        smallMutta = null;
		lastRatio = 0;
		lastHealthScale = 0;
		startedChopping = false;
		ticksToChop = 5;

        olmPhase = "";
        olmSpawned = false;
        olmHead = null;

        vangsActive = false;
        vangsAlive = false;
        vangsTicks = 1;
        vangs4Ticks = 1;

        orbStyle = "";
        orbTicks = 0;

        olmTile = null;
		
		ropeNpcs.clear();

		shortcut.clear();

        rope.clear();
        ropeSpawnDelay = 0;

        portalBuddy = "";
        portalTicks = 0;
    }

    @Override
    protected void startUp(){
        reset();

        tlList.clear();
        for (String str : config.tlList().split(",")) {
            if (!str.trim().equals(""))
                tlList.add(str.trim().toLowerCase());
        }

        chestHighlightIdList.clear();
        for (String str : config.highlightChestItems().split(",")) {
            if (!str.trim().equals("")) {
                try {
                    chestHighlightIdList.add(Integer.valueOf(str.trim()));
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        chestHighlightIdList2.clear();
        for (String str : config.highlightChestItems2().split(",")) {
            if (!str.trim().equals("")) {
                try {
                    chestHighlightIdList2.add(Integer.valueOf(str.trim()));
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        highlightShortcuts = config.highlightShortcuts();
        overlayManager.add(overlay);
        overlayManager.add(olmOverlay);
        overlayManager.add(vespEnahnceOverlay);
        overlayManager.add(vanguardCycleOverlay);
        overlayManager.add(shortcutOverlay);
        overlayManager.add(orbOverlay);
        overlayManager.add(orbTabOverlay);
		overlayManager.add(olmHpPanelOverlay);
        overlayManager.add(olmSideOverlay);
        overlayManager.add(itemOverlay);
        overlayManager.add(instanceTimerOverlay);
        overlayManager.add(meatTreeCycleOverlay);
        overlayManager.add(olmPhasePanel);
    }

    @Override
    protected void shutDown(){
        reset();
        eventBus.unregister(this);
        overlayManager.remove(overlay);
        overlayManager.remove(olmOverlay);
        overlayManager.remove(vespEnahnceOverlay);
        overlayManager.remove(vanguardCycleOverlay);
        overlayManager.remove(shortcutOverlay);
        overlayManager.remove(orbOverlay);
        overlayManager.remove(orbTabOverlay);
		overlayManager.remove(olmHpPanelOverlay);
        overlayManager.remove(olmSideOverlay);
        overlayManager.remove(itemOverlay);
        overlayManager.remove(instanceTimerOverlay);
        overlayManager.remove(meatTreeCycleOverlay);
        overlayManager.remove(olmPhasePanel);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged e) {
        if (e.getGroup().equals("CoxAdditions")) {
            switch (e.getKey()) {
                case "tlList":
                    tlList.clear();
                    for (String str : config.tlList().split(",")) {
                        if (!str.trim().equals(""))
                            tlList.add(str.trim().toLowerCase());
                    }
                    break;
                case "highlightChestItems":
                    chestHighlightIdList.clear();
                    for (String str : config.highlightChestItems().split(",")) {
                        if (!str.trim().equals("")) {
                            try {
                                chestHighlightIdList.add(Integer.valueOf(str.trim()));
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                    }
                    break;
                case "highlightChestItems2":
                    chestHighlightIdList2.clear();
                    for (String str : config.highlightChestItems2().split(",")) {
                        if (!str.trim().equals("")) {
                            try {
                                chestHighlightIdList2.add(Integer.valueOf(str.trim()));
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                    }
                    break;
                case "vangsCycle":
                    if (config.vangsCycle() != CoxAdditionsConfig.VangsTicksMode.OFF) {
                        for (NPC npc : client.getNpcs()) {
                            if (npc.getId() == 7527 || npc.getId() == 7528 || npc.getId() == 7529) {
                                vangsAlive = true;
                                vangsActive = true;
                            } else if (npc.getId() == 7526) {
                                vangsAlive = true;
                                vangsActive = false;
                            }
                        }
                    } else {
                        vangsAlive = false;
                        vangsActive = false;
                        vangsTicks = 1;
                        vangs4Ticks = 1;
                    }
                    break;
                case "highlightShortcuts":
                    highlightShortcuts = config.highlightShortcuts();
                    break;
                case "olmTp":
                    if (config.olmTp()) {
                        for (GraphicsObject go : client.getGraphicsObjects()) {
                            if (go.getId() == 1359) {
                                if (client.hasHintArrow()) {
                                    client.setHintArrow(WorldPoint.fromLocal(client, go.getLocation()));
                                }
                            }
                        }
                    } else {
                        client.clearHintArrow();
                    }
                    break;
            }
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        String msg = Text.standardize(event.getMessageNode().getValue());
        if (client.getVarbitValue(Varbits.IN_RAID) == 1) {
            if (msg.equalsIgnoreCase("the great olm's left claw clenches to protect itself temporarily.")){
                handCripple = true;
            } else if (msg.equalsIgnoreCase("the great olm infects you with a burning overwhelming power.")){
                burningTicks = 41;
                burningActive = true;
            } else if (msg.equalsIgnoreCase("the great olm has smothered you in acid. it starts to drip off slowly.")){
                acidTicks = 23;
                acidActive = true;
            } else if (msg.equalsIgnoreCase("the great olm has chosen you as its target - watch out!")){
                crystalsTicks = 23;
                crystalsActive = true;
            } else if (msg.equalsIgnoreCase("the great olm rises with the power of crystal.")){
                olmPhase = "Crystal";
            } else if (msg.equalsIgnoreCase("the great olm rises with the power of acid.")){
                olmPhase = "Acid";
            } else if (msg.equalsIgnoreCase("the great olm rises with the power of flame.")){
                olmPhase = "Flame";
            } else if (msg.equalsIgnoreCase("you drink some of your strong prayer enhance potion.")){
                prayerEnhanceTicks = 7;
                prayerEnhanceActive = true;
            } else if (msg.equalsIgnoreCase("your prayer enhance effect has worn off.")){
                prayerEnhanceTicks = 7;
                prayerEnhanceActive = false;
            } else if (msg.equalsIgnoreCase("the great olm fires a sphere of aggression your way. your prayers have been sapped.")){
                orbStyle = "melee";
                orbTicks = 8;
            } else if (msg.equalsIgnoreCase("the great olm fires a sphere of accuracy and dexterity your way. your prayers have been sapped.")){
                orbStyle = "range";
                orbTicks = 8;
            } else if (msg.equalsIgnoreCase("the great olm fires a sphere of magical power your way. your prayers have been sapped.")){
                orbStyle = "mage";
                orbTicks = 8;
            } else if (msg.equalsIgnoreCase("the great olm is giving its all. this is its final stand.")){
                mageHand = null;
				meleeHand = null;
            } else if (msg.equalsIgnoreCase("you swing your axe...") && meatTreeAlive && meatTree != null) {
                startedChopping = true;
            } else if (msg.equalsIgnoreCase("you hack away some of the meat.") && meatTreeAlive && meatTree != null) {
                ticksToChop = 6;
            } else if (msg.contains("! the magical power will enact soon...")) {
                if (msg.contains("you have been paired with ")) {
                    portalBuddy = msg.substring(26, msg.indexOf("! the magical power")).trim();
                }
                portalTicks = 10;
            } else if (msg.equalsIgnoreCase("the teleport attack has no effect!") || (msg.contains("yourself and ") && msg.contains(" have swapped places!"))) {
                portalBuddy = "";
                portalTicks = 0;
            }
        }

        if (msg.equalsIgnoreCase("you have been kicked from the channel.") || msg.contains("decided to start the raid without you. sorry.") 
            || msg.equalsIgnoreCase("you are no longer eligible to lead the party.") || msg.equalsIgnoreCase("the raid has begun!")){
            instanceTimer = 5;
            isInstanceTimerRunning = false;
        } else if (msg.equalsIgnoreCase("inviting party...") || msg.equalsIgnoreCase("your party has entered the dungeons! come and join them now.")){
            instanceTimer = 5;
            isInstanceTimerRunning = true;
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
		if (client.getVarbitValue(Varbits.IN_RAID) == 1){
			if (handCripple) {
				crippleTimer--;
				if (crippleTimer <= 0) {
					handCripple = false;
					crippleTimer = 45;
				}
			}

			if (acidActive) {
				acidTicks--;
				if (acidTicks <= 0) {
					acidActive = false;
					acidTicks = 23;
				}
			}

			if (burningActive) {
				burningTicks--;
				if (burningTicks <= 0) {
					burningActive = false;
					burningTicks = 41;
				}
			}

			if (crystalsActive) {
				crystalsTicks--;
				if (crystalsTicks <= 0) {
					crystalsActive = false;
					crystalsTicks = 23;
				}
			}

			if (coxHerb1 != null || coxHerb2 != null){
				if (coxHerb1 != null){
					if (coxHerbTimer1 != 0){
						coxHerbTimer1--;
					} else{
						coxHerb1 = null;
					}
				}

				if (coxHerb2 != null){
					if (coxHerbTimer2 != 0){
						coxHerbTimer2--;
					} else{
						coxHerb2 = null;
					}
				}
			}
			
			if (olmHealingPools.size() > 0) {
				for (int i = olmHealingPools.size() - 1; i >= 0; i--) {
					olmHealingPools.get(i).ticks--;
					if (olmHealingPools.get(i).ticks == 0){
						olmHealingPools.remove(i);
					}
				}
			}

			if (vasa != null) {
				if (vasa.getId() == 7567) {
					if (vasaCrystalTicks == 0) {
						vasaCrystalTicks = 67;
					} else {
						vasaCrystalTicks--;
					}
				} else if (vasa.getId() == 7566 && vasa.getAnimation() == 7409) {
					vasaCrystalTicks = 67;
					vasaAtCrystal = false;
				}
			}

			if (prayerEnhanceActive){
				prayerEnhanceTicks--;
				if (prayerEnhanceTicks <= 0){
					prayerEnhanceTicks = 6;
				}
			}

			if (vangsActive) {
				vangsTicks++;
				vangs4Ticks++;
				if (vangs4Ticks > 4){
					vangs4Ticks = 1;
				}
			}

			shortcut.removeIf(object -> (object.getCanvasLocation() == null));

			if (!orbStyle.equals("")){
				orbTicks--;
				if (orbTicks <= 0){
					orbTicks = 0;
					orbStyle = "";
				}
			}

			if (startedChopping){
			    ticksToChop--;
			    if (ticksToChop <= 0){
			        ticksToChop = 5;
                }
            }

			if (portalTicks > 0) {
			    portalTicks--;
			    if (portalTicks == 0) {
			        portalBuddy = "";
                }
            }

            if (ropeSpawnDelay > 0) {
                ropeSpawnDelay--;
            }
		}
		
		if (isInstanceTimerRunning){
			instanceTimer--;
			if (instanceTimer < 0){
				instanceTimer = 3;
			}
		}

        ticksSinceHPRegen++;
        if ((ticksSinceHPRegen == 50 && rapidHealActive) || ticksSinceHPRegen == 100) {
            ticksSinceHPRegen = 0;
        }
    }

    @Subscribe
    private void onProjectileMoved(ProjectileMoved event) {
        if (client.getVarbitValue(Varbits.IN_RAID) == 1) {
            if (event.getProjectile().getId() == 1355) {
                olmHealingPools.add(new HealingPoolInfo(event.getPosition(), 10));
            }
        }
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject obj = event.getGameObject();
        if (client.getVarbitValue(Varbits.IN_RAID) == 1) {
            if (obj.getId() >= 29997 && obj.getId() <= 29999) {
                if (coxHerb1 == null) {
                    coxHerb1 = obj;
                    coxHerbTimer1 = 16;
                } else {
                    coxHerb2 = obj;
                    coxHerbTimer2 = 16;
                }
            } else if (obj.getId() >= 30000 && obj.getId() <= 30008) {
                if (coxHerb1 == null) {
                    coxHerb1 = obj;
                    coxHerbTimer1 = 16;
                } else {
                    coxHerb2 = obj;
                    coxHerbTimer2 = 16;
                }
            }

            WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, event.getGameObject().getLocalLocation());
            if (worldPoint != null){
                if (event.getGameObject().getId() == 29740 || event.getGameObject().getId() == 29736 || event.getGameObject().getId() == 29738){
                    shortcut.add(event.getGameObject());
                }
            }
        }
    }

    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned event) {
        GameObject obj = event.getGameObject();
        if (client.getVarbitValue(Varbits.IN_RAID) == 1) {
            if (coxHerb1 != null || coxHerb2 != null) {
                if (obj.getId() >= 29997 && obj.getId() <= 30008) {
                    if (coxHerb1 != null) {
                        if (obj.getId() == coxHerb1.getId()) {
                            coxHerb1 = null;
                        } else {
                            coxHerb2 = null;
                        }
                    } else {
                        coxHerb2 = null;
                    }
                }
            }
            shortcut.remove(event.getGameObject());
        }
    }

    @Subscribe
    private void onGroundObjectSpawned(GroundObjectSpawned event) {
        GroundObject obj = event.getGroundObject();
        if (client.getVarbitValue(Varbits.IN_RAID) == 1 && obj.getId() == 29750) {
            if ((rope.size() > 0 && ropeSpawnDelay == 0) || rope.size() >= 2) {
                rope.clear();
            }
            rope.add(obj);

            if (ropeSpawnDelay == 0) {
                ropeSpawnDelay = 2;
            }
        }
    }

    @Subscribe
    private void onGraphicsObjectCreated(GraphicsObjectCreated event){
        if (event.getGraphicsObject().getId() == 1359) {
            if (client.hasHintArrow()) {
                client.clearHintArrow();
            } else {
                if (config.olmTp()) {
                    client.setHintArrow(WorldPoint.fromLocal(client, event.getGraphicsObject().getLocation()));
                }
            }
        }
    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event) {
        if (client.getVarbitValue(Varbits.IN_RAID) == 1 && event.getNpc() != null) {
            NPC npc = event.getNpc();
            int id = npc.getId();
            String name = npc.getName();

			if (id == 7552 || id == 7555) {
				meleeHand = npc;
            } else if (id == 7550 || id == 7553){
				mageHand = npc;
			} else if (id == 7565 || id == 7566 || id == 7567) {
                vasa = npc;
                vasaAtCrystal = (id == 7567);
            } else if (id == 7530 || id == 7531 || id == 7532 || id == 7533) {
                vespAlive = true;
            } else if (id == 7564) {
                meatTreeAlive = true;
                meatTree = npc;
            } else if (id == 7562) {
                smallMuttaAlive = true;
                smallMutta = npc;
            } else if (id == 7528 || id == 7527 || id == 7529) {
                vangsAlive = true;
                vangsActive = true;
            } else if (id == 7526 || id == 7525){
                vangsAlive = true;
            } else {
                if (name != null){
                    if (name.equalsIgnoreCase("great olm")){
                        olmHead = npc;
                        olmSpawned = true;
                        if (id == 7551){
                            olmTile = npc.getLocalLocation();
                        } else if (id == 7554){
                            olmTile = null;
                        }
                    } else if (name.equalsIgnoreCase("lizardman shaman")) {
                        if (npc.getInteracting() != null){
                            shamanInfoList.add(new ShamanInfo(npc, npc.getInteracting().getLocalLocation(), false));
                        } else {
                            shamanInfoList.add(new ShamanInfo(npc, null, false));
                        }
                    } else if (name.equalsIgnoreCase("deathly mage") || name.equalsIgnoreCase("deathly ranger")){
				        ropeNpcs.add(npc);
			        }
                }
            }
        }
    }

    @Subscribe
    private void onNpcDespawned(NpcDespawned event){
        if (client.getVarbitValue(Varbits.IN_RAID) == 1) {
            NPC npc = event.getNpc();
            int id = npc.getId();
            String name = npc.getName();

            if (id == 7552 || id == 7555) {
                meleeHand = null;
                if (npc.isDead()) {
                    if (mageHand == null){
                        olmPhase = "";
                    }
                    handCripple = false;
                    crippleTimer = 45;
                    meleeHandLastHealthScale = 0;
                    meleeHandLastRatio = 0;
                }
            } else if (id == 7550 || id == 7553) {
                mageHand = null;
                if (npc.isDead()) {
                    if (meleeHand == null){
                        olmPhase = "";
                    }
                    handCripple = false;
                    crippleTimer = 45;
                    mageHandLastHealthScale = 0;
                    mageHandLastRatio = 0;
                }
            } else if (id == 7530 || id == 7531 || id == 7532 || id == 7533) {
                vespAlive = false;
            } else if (id == 7564) {
                meatTreeAlive = false;
                meatTree = null;
                smallMutta = null;
                lastHealthScale = 0;
                lastRatio = 0;
                startedChopping = false;
                ticksToChop = 5;
            } else if (id == 7562) {
                smallMuttaAlive = false;
                smallMutta = null;
                lastHealthScale = 0;
                lastRatio = 0;
            } else if (id == 7528 || id == 7527 || id == 7529 || id == 7526 || id == 7525) {
                boolean alive = false;
                for (NPC n : client.getNpcs()) {
                    if (n.getId() == 7527 || n.getId() == 7528 || n.getId() == 7529 || n.getId() == 7526 || n.getId() == 7525) {
                        alive = true;
                        break;
                    }
                }

                if (!alive) {
                    vangsAlive = false;
                    vangsActive = false;
                    vangsTicks = 1;
                    vangs4Ticks = 1;
                }
            } else {
                if (name != null){
                    if (name.equalsIgnoreCase("great olm")) {
                        olmHead = null;
                        olmSpawned = false;
                        if (id == 7551) {
                            olmTile = null;
                        }

                        if (npc.isDead()) {
                            olmPhase = "";
                        }
                    } else if (name.equalsIgnoreCase("lizardman shaman")) {
                        for(int i=shamanInfoList.size()-1; i>=0; i--){
					        if (shamanInfoList.get(i).shaman == npc){
						        shamanInfoList.remove(i);
					        }
				        }
                    } else if (name.equalsIgnoreCase("deathly mage") || name.equalsIgnoreCase("deathly ranger")){
                        ropeNpcs.remove(npc);
			        }
                }
            }
        }
    }

    @Subscribe
    private void onNpcChanged(NpcChanged event){
        if (client.getVarbitValue(Varbits.IN_RAID) == 1) {
            NPC npc = event.getNpc();
            int id = npc.getId();

            if (id == 7526) {
                if (vangsActive) {
                    vangsActive = false;
                }
            } else if (id == 7527 || id == 7528 || id == 7529) {
                vangsActive = true;
                vangsTicks = 1;
                vangs4Ticks = 1;
            } else if (id == 7554){
                olmTile = null;
            }
        }
    }

    @Subscribe
    private void onItemContainerChanged(ItemContainerChanged event) {
        if (client.getVarbitValue(Varbits.IN_RAID) == 1) {
            if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
                coxSpade = false;
                coxDibbler = false;
                coxRake = false;

                if (client.getItemContainer(InventoryID.INVENTORY).count(952) > 0) {
                    coxSpade = true;
                }
                if (client.getItemContainer(InventoryID.INVENTORY).count(5343) > 0) {
                    coxDibbler = true;
                }
                if (client.getItemContainer(InventoryID.INVENTORY).count(5341) > 0) {
                    coxRake = true;
                }
            }
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (client.getVarbitValue(Varbits.IN_RAID) == 1 && event.getActor() instanceof NPC) {
			NPC npc = (NPC) event.getActor();
            if (npc != null && npc.getName() != null && npc.getName().equalsIgnoreCase("lizardman shaman")) {
                for (ShamanInfo shamanInfo : shamanInfoList) {
                    if (shamanInfo.shaman == npc) {
                        if (npc.getAnimation() == 7152) {
                            shamanInfo.jumping = true;
                        } else if (npc.getAnimation() == 6946) {
                            shamanInfo.jumping = false;
                        }

                        if (npc.getInteracting() != null) {
                            shamanInfo.interactingLoc = npc.getInteracting().getLocalLocation();
                        }
                        break;
                    }
                }
            }
        }
    }

    @Subscribe
    private void onActorDeath(ActorDeath event){
        if (client.getVarbitValue(Varbits.IN_RAID) == 1 && event.getActor() instanceof NPC) {
			NPC npc = (NPC) event.getActor();
            if (npc.getName() != null) {
                if (npc.getName().toLowerCase().contains("great olm (left claw)")) {
                    meleeHand = null;
                    meleeHandHp = -1;
                } else if (npc.getName().toLowerCase().contains("great olm (right claw)")) {
                    mageHand = null;
                    mageHandHp = -1;
                } else if (npc.getId() == 7533) {
                    vespDied = true;
                } else if (npc.getName().toLowerCase().contains("vasa nistirio")) {
                    vasa = null;
					vasaCrystalTicks = 0;
					vasaAtCrystal = false;
                }
            }
        }
    }

    private final Predicate<MenuEntry> filterMenuEntries = entry -> {
        if (client.getVarbitValue(Varbits.IN_RAID) == 1) {
        int id = entry.getIdentifier();
        String option = Text.standardize(entry.getOption(), true).toLowerCase();
        String target = Text.standardize(entry.getTarget(), true).toLowerCase();

            if (config.hideAttackHead() && (meleeHand != null || mageHand != null) && target.contains("great olm") && !target.contains("(left claw)")
                    && !target.contains("(right claw)")) {
                return false;
            }

            if (config.hideVesp() && target.equals("vespula")) {
                return false;
            }

            if (config.swapBats() && option.contains("catch") && BATS.contains(target)) {
                return false;
            }

            if (config.removeChop() && target.equals("sapling") && option.equals("chop")) {
                ArrayList<Integer> axeList = new ArrayList<>(Arrays.asList(1349, 1351, 1353, 1355, 1357, 1359, 1361, 6739, 13241, 13242, 25110, 23673, 25066, 25371, 25378));
                int weapon = Objects.requireNonNull(client.getLocalPlayer()).getPlayerComposition().getEquipmentId(KitType.WEAPON);
                boolean axeFound = false;
                for (int axe : axeList) {
                    if (client.getItemContainer(InventoryID.INVENTORY).count(axe) > 0 || weapon == axe) {
                        axeFound = true;
                        break;
                    }
                }

                if (!axeFound) {
                    return false;
                }
            }

            if (config.removeUseSeed() && option.equals("use") && target.contains(" seed -> ")) {
                String[] seeds = {"buchu seed", "golpar seed", "noxifer seed"};
                for(String seed : seeds) {
                    if (target.startsWith(seed + " ->") && entry.getType().getId() != 8 && (!target.contains("herb patch") || target.contains("(level-"))) {
                        return false;
                    }
                }
            }

            if (config.removeUseVial() && option.equals("use") && target.contains("empty gourd vial -> ") && entry.getType().getId() != 8 && (!target.contains("geyser")
                    && !target.contains("xeric's aid ") && !target.contains("revitalisation ") && !target.contains("prayer enhance ") && !target.contains("overload "))) {
                return false;
            }

            if (config.removeFeed() && target.equals("lux grub")) {
                if (client.getItemContainer(InventoryID.INVENTORY).count(20892) == 0) {
                    return false;
                }
            }

            if (config.removePickRoot() && target.equals("medivaemia root") && vespDied && option.equals("pick")) {
                return false;
            }

            if (config.removePickSpec() && (target.equals("special attack") || option.equals("use special attack"))) {
                ArrayList<Integer> pickList = new ArrayList<>(Arrays.asList(11920, 12797, 23677, 25376, 13243, 13244, 25063, 25369, 23680, 23682, 23863, 20014, 23276, 23822));
                int weapon = Objects.requireNonNull(client.getLocalPlayer()).getPlayerComposition().getEquipmentId(KitType.WEAPON);
                if (pickList.contains(weapon)) {
                    return false;
                }
            }

            if (config.removeCastCoX() && option.equals("cast") && entry.getType().getId() == MenuAction.WIDGET_TARGET_ON_PLAYER.getId()) {
                String[] spells = {"ice barrage", "ice burst", "ice blitz", "ice rush", "entangle", "snare", "bind", "blood barrage", "blood burst", "blood rush",
                        "blood blitz", "fire surge", "fire wave"};
                for (String spell : spells) {
                    if (client.getSelectedSpellName().toLowerCase().contains(spell)) {
                        return false;
                    }
                }
            }
        }
        return true;
    };

	/*@Subscribe
    public void onMenuEntryAdded(MenuEntryAdded e) {
        if (client.getVarbitValue(Varbits.IN_RAID) == 1) {
            int type = e.getType();
            int id = e.getIdentifier();
            String target = Text.standardize(e.getTarget(), true).toLowerCase();
            String option = Text.standardize(e.getOption()).toLowerCase();

            if (config.hideAttackHead()) {
                try {
                    if (type >= 7 && type <= 13 && type != 8){
                        NPC npc = client.getCachedNPCs()[id];
                        if (npc != null && npc.getName() != null) {
                            String name = npc.getName().toLowerCase();
                            if (name.contains("great olm") && !name.contains("(left claw)") && !name.contains("(right claw)") && (meleeHand != null || mageHand != null)) {
                                client.setMenuOptionCount(client.getMenuOptionCount() - 1);
                            }
                        }
                    }
                }catch (ArrayIndexOutOfBoundsException ex){
                    System.out.println(ex.getMessage());
                }
            }

            if (config.hideVesp()) {
                try {
                    if (type >= 7 && type <= 13 && type != 8){
                        NPC npc = client.getCachedNPCs()[id];
                        if (npc != null && npc.getName() != null) {
                            String name = npc.getName().toLowerCase();
                            if (name.contains("vespula")) {
                                client.setMenuOptionCount(client.getMenuOptionCount() - 1);
                            }
                        }
                    }
                }catch (ArrayIndexOutOfBoundsException ex){
                    System.out.println(ex.getMessage());
                }
            }

            if (config.swapBats() && option.contains("catch") && (target.contains("guanic bat") || target.contains("prael bat") || target.contains("giral bat") || target.contains("phluxia bat") ||
                    target.contains("kryket bat") || target.contains("murng bat") || target.contains("psykk bat"))) {
                client.setMenuOptionCount(client.getMenuOptionCount() - 1);
            }

            if (config.removeChop() && target.equals("sapling") && option.equals("chop")) {
                ArrayList<Integer> axeList = new ArrayList<>(Arrays.asList(1349, 1351, 1353, 1355, 1357, 1359, 1361, 6739, 13241, 13242, 25110, 23673, 25066, 25371, 25378));
                int weapon = Objects.requireNonNull(client.getLocalPlayer()).getPlayerComposition().getEquipmentId(KitType.WEAPON);
                boolean axeFound = false;
                for (int axe : axeList) {
                    if (client.getItemContainer(InventoryID.INVENTORY).count(axe) > 0 || weapon == axe) {
                        axeFound = true;
                        break;
                    }
                }

                if (!axeFound) {
                    client.setMenuOptionCount(client.getMenuOptionCount() - 1);
                }
            }

            if (config.removeUseSeed() && option.equals("use") && target.contains(" seed -> ")) {
                String[] seeds = {"buchu seed", "golpar seed", "noxifer seed"};
                MenuEntry[] entries = client.getMenuEntries();
                MenuEntry[] newEntries = client.getMenuEntries();
                for (String seed : seeds) {
                    if (target.startsWith(seed + " ->") && type != 8 && (!target.contains("herb patch") || target.contains("(level-"))) {
                        System.out.println("Removed entry: " + target);
                        newEntries = (MenuEntry[]) ArrayUtils.remove((Object[]) entries, entries.length-1);
                        break;
                    }
                }
                client.setMenuEntries(newEntries);
            }

            if (config.removeUseVial() && option.equals("use") && target.contains("empty gourd vial -> ") && type != 8 && (!target.contains("geyser")
                    && !target.contains("xeric's aid ") && !target.contains("revitalisation ") && !target.contains("prayer enhance ") && !target.contains("overload "))) {
                MenuEntry[] entries = client.getMenuEntries();
                MenuEntry[] newEntries = (MenuEntry[]) ArrayUtils.remove((Object[]) entries, entries.length-1);
                client.setMenuEntries(newEntries);
            }

            if (config.removeFeed() && target.equals("lux grub") && option.equals("feed")) {
                if (client.getItemContainer(InventoryID.INVENTORY).count(20892) > 0) {
                    client.setMenuOptionCount(client.getMenuOptionCount() - 1);
                }
            }

            if (config.removePickRoot() && target.equals("medivaemia root") && vespDied && option.equals("pick")) {
                client.setMenuOptionCount(client.getMenuOptionCount() - 1);
            }

            if (config.removePickSpec() && (target.equals("special attack") || option.equals("use special attack"))) {
                ArrayList<Integer> pickList = new ArrayList<>(Arrays.asList(11920, 12797, 23677, 25376, 13243, 13244, 25063, 25369, 23680, 23682, 23863, 20014, 23276, 23822));
                int weapon = Objects.requireNonNull(client.getLocalPlayer()).getPlayerComposition().getEquipmentId(KitType.WEAPON);
                if (pickList.contains(weapon)) {
                    client.setMenuOptionCount(0);
                }
            }

            if (config.removeCastCoX() && option.equals("cast") && type == MenuAction.SPELL_CAST_ON_PLAYER.getId()) {
                String[] spells = {"ice barrage", "ice burst", "ice blitz", "ice rush", "entangle", "snare", "bind", "blood barrage", "blood burst", "blood rush",
                        "blood blitz", "fire surge", "fire wave"};
                for (String spell : spells) {
                    if (client.getSelectedSpellName().toLowerCase().contains(spell)) {
                        client.setMenuOptionCount(client.getMenuOptionCount() - 1);
                    }
                }
            }
        }
    }*/
	
    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (event.getMenuOption().equalsIgnoreCase("hold") || event.getMenuOption().equalsIgnoreCase("equip") || event.getMenuOption().equalsIgnoreCase("wield")){
            if (!offHandId.contains(event.getItemId())) {
                weaponId = event.getItemId();
            }
        }
    }

    private void swapMenuEntry(int index, MenuEntry menuEntry) {
        int eventId = menuEntry.getIdentifier();
        int type = menuEntry.getType().getId();
        String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
        String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();

        if (client.getVarbitValue(Varbits.IN_RAID) == 1) {
            if (config.swapCoXKeystone() && target.equals("keystone crystal") && option.equals("use")) {
                swap("drop", option, target, index, false);
            }

            if (config.swapCoXTools() && target.equals("old tools")) {
                if (!coxDibbler){
                    swap("take seed dibber", option, target, index);
                } else if (!coxSpade) {
                    swap("take spade", option, target, index);
                } else {
                    swap("take rake", option, target, index);
                }
            }

            //If it doesnt work, their off hand might not be in offHandId list
            if (config.leftClickSmash() && target.contains("jewelled crab") && option.contains("attack")) {
                if (weaponId == 0) {
                    weaponId = Objects.requireNonNull(client.getLocalPlayer()).getPlayerComposition().getEquipmentId(KitType.WEAPON);
                }

                if (weaponId == 13576) {
                    swap("smash", option, target, index);
                }
            }
        }
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick) {
        /*if (client.isMirrored() && !mirrorMode) {
            overlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(overlay);
            overlayManager.add(overlay);
            olmOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(olmOverlay);
            overlayManager.add(olmOverlay);
            vespEnahnceOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(vespEnahnceOverlay);
            overlayManager.add(vespEnahnceOverlay);
            vanguardCycleOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(vanguardCycleOverlay);
            overlayManager.add(vanguardCycleOverlay);
            shortcutOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(shortcutOverlay);
            overlayManager.add(shortcutOverlay);
            orbOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(orbOverlay);
            overlayManager.add(orbOverlay);
            orbTabOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(orbTabOverlay);
            overlayManager.add(orbTabOverlay);
            olmHpPanelOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(olmHpPanelOverlay);
            overlayManager.add(olmHpPanelOverlay);
            olmSideOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(olmSideOverlay);
            overlayManager.add(olmSideOverlay);
            instanceTimerOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(instanceTimerOverlay);
            overlayManager.add(instanceTimerOverlay);
            itemOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(itemOverlay);
            overlayManager.add(itemOverlay);
            meatTreeCycleOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(meatTreeCycleOverlay);
            overlayManager.add(meatTreeCycleOverlay);
            mirrorMode = true;
        }*/

        if (client.getGameState() == GameState.LOGGED_IN && !client.isMenuOpen()) {
            MenuEntry[] menuEntries = client.getMenuEntries();
            int idx = 0;
            optionIndexes.clear();
            for (MenuEntry entry : menuEntries) {
                String option = Text.removeTags(entry.getOption()).toLowerCase();
                optionIndexes.put(option, idx++);
            }

            idx = 0;
            for (MenuEntry entry : menuEntries) {
                swapMenuEntry(idx++, entry);
            }
        }
        client.setMenuEntries(updateMenuEntries(client.getMenuEntries()));
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (client.getVarbitValue(Varbits.IN_RAID) != 1) {
            meleeHand = null;
            mageHand = null;
            crippleTimer = 45;
            handCripple = false;
            meleeHandHp = -1;
            mageHandHp = -1;
            mageHandLastRatio = 0;
            mageHandLastHealthScale = 0;
            meleeHandLastRatio = 0;
            meleeHandLastHealthScale = 0;

            playerEntry.clear();
            playerNames.clear();
            customTexts.clear();
            intNPC.clear();
            namedTarget = false;

            coxSpade = false;
            coxDibbler = false;
            coxRake = false;

            clip = null;

            acidTicks = 23;
            acidActive = false;
            burningTicks = 41;
            burningActive = false;
            crystalsTicks = 32;
            crystalsActive = false;

            shamanInfoList.clear();

            coxHerb1 = null;
            coxHerbTimer1 = 16;
            coxHerb2 = null;
            coxHerbTimer2 = 16;

            weaponId = 0;

            olmHealingPools.clear();

            vasa = null;
            vasaCrystalTicks = 0;
            vasaAtCrystal = false;

            vespAlive = false;
            prayerEnhanceActive = false;
            prayerEnhanceTicks = 7;
            vespDied = false;

            meatTreeAlive = false;
            meatTree = null;
            smallMuttaAlive = false;
            lastRatio = 0;
            lastHealthScale = 0;
            smallMutta = null;
            startedChopping = false;
            ticksToChop = 5;

            vangsAlive = false;
            vangsTicks = 1;
            vangsActive = false;

            orbStyle = "";
            orbTicks = 0;

			shortcut.clear();

            rope.clear();

            portalBuddy = "";
            portalTicks = 0;
        } else {
            if (client.getVar(VarPlayer.HP_HUD_NPC_ID) == 7555){
                meleeHandHp = client.getVarbitValue(6099);
            } else if (client.getVar(VarPlayer.HP_HUD_NPC_ID) == 7553){
                mageHandHp = client.getVarbitValue(6099);
            }

            if (rapidHealActive != client.isPrayerActive(Prayer.RAPID_HEAL)) {
                ticksSinceHPRegen = 0;
            }
            rapidHealActive = client.isPrayerActive(Prayer.RAPID_HEAL);
        }
    }

    private void swap(String optionA, String optionB, String target, int index) {
        swap(optionA, optionB, target, index, true);
    }

    private void swapContains(String optionA, String optionB, String target, int index) {
        swap(optionA, optionB, target, index, false);
    }

    private void swap(String optionA, String optionB, String target, int index, boolean strict) {
        MenuEntry[] menuEntries = client.getMenuEntries();
        int thisIndex = findIndex(menuEntries, index, optionB, target, strict);
        int optionIdx = 0;

        if (target.contains("*")) {
            optionIdx = findIndex(menuEntries, thisIndex, optionA, target.replace("*", ""), strict);
        } else {
            optionIdx = findIndex(menuEntries, thisIndex, optionA, target, strict);
        }

        if (thisIndex >= 0 && optionIdx >= 0)
            swap(optionIndexes, menuEntries, optionIdx, thisIndex);
    }

    private int findIndex(MenuEntry[] entries, int limit, String option, String target, boolean strict) {
        if (strict) {
            List<Integer> indexes = optionIndexes.get(option);
            for (int i = indexes.size() - 1; i >= 0; i--) {
                int idx = indexes.get(i);
                MenuEntry entry = entries[idx];
                String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
                if (idx <= limit && entryTarget.equals(target))
                    return idx;
            }
        } else {
            for (int i = limit; i >= 0; i--) {
                MenuEntry entry = entries[i];
                String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
                String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
                if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target))
                    return i;
            }
        }
        return -1;
    }

    private void swap(ArrayListMultimap<String, Integer> optionIndexes, MenuEntry[] entries, int index1, int index2) {
        MenuEntry entry1 = entries[index1],
                entry2 = entries[index2];
        entries[index1] = entry2;
        entries[index2] = entry1;
        if (entry1.isItemOp() && entry1.getType() == MenuAction.CC_OP_LOW_PRIORITY)
        {
            entry1.setType(MenuAction.CC_OP);
        }
        if (entry2.isItemOp() && entry2.getType() == MenuAction.CC_OP_LOW_PRIORITY)
        {
            entry2.setType(MenuAction.CC_OP);
        }

        client.setMenuEntries(entries);
        optionIndexes.clear();
        int idx = 0;
        for (MenuEntry menuEntry : entries) {
            String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
            optionIndexes.put(option, idx++);
        }
    }

    private MenuEntry[] updateMenuEntries(MenuEntry[] menuEntries)
    {
        return Arrays.stream(menuEntries)
                .filter(filterMenuEntries).sorted((o1, o2) -> 0)
                .toArray(MenuEntry[]::new);
    }
}
