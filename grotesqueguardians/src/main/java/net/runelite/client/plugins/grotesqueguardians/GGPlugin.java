package net.runelite.client.plugins.grotesqueguardians;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

@Extension
@PluginDescriptor(
        name = "[S] Grotesque Guardians",
        description = "Show various helpful utilities during the Grotesque Guardians fight",
        tags = {"grotesque", "guardian", "gg", "pvm", "overlay", "boss", "gargs"},
        enabledByDefault = false
)
public class GGPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(GGPlugin.class);

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GGOverlay overlay;

    @Inject
    private GGPrayerOverlay prayerOverlay;

    @Inject
    private GGConfig config;

    private NPC duskNPC;

    public enum DuskAttackStyles {
        MELEE, RANGE, UNKNOWN;
    }

    public enum DawnStates {
        ATTACK, CANT_ATTACK, TRANSITION, UNKNOWN;
    }

    public enum DuskStates {
        ATTACK, CANT_ATTACK, TRANSITION, UNKNOWN;
    }

    @Provides
    GGConfig getConfig(ConfigManager configManager) {
        return (GGConfig)configManager.getConfig(GGConfig.class);
    }

    public NPC getDuskNPC() {
        return duskNPC;
    }

    private int duskTicks = -1;

    private NPC dawnNPC;

    public int getDuskTicks() {
        return duskTicks;
    }

    public NPC getDawnNPC() {
        return dawnNPC;
    }

    private int dawnTicks = -1;

    private DawnStates dawnState;

    private DuskStates duskState;

    private DuskAttackStyles currentAttackStyle;

    public int getDawnTicks() {
        return dawnTicks;
    }

    public DawnStates getDawnState() {
        return dawnState;
    }

    public DuskStates getDuskState() {
        return duskState;
    }

    public DuskAttackStyles getCurrentAttackStyle() {
        return currentAttackStyle;
    }

    private final Map<GameObject, Integer> dawnHealingOrbs = new HashMap<>();

    public Map<GameObject, Integer> getDawnHealingOrbs() {
        return dawnHealingOrbs;
    }

    public boolean inGarg = false;

    public boolean stepBack;

    private long[] startTime;

    public Set<Projectile> projectiles = new HashSet<>();

    private boolean mirrorMode;

    protected void startUp() {
        overlayManager.add(overlay);
        overlayManager.add(prayerOverlay);
        startTime = new long[] { 0L, 0L, 0L, 0L };
    }

    protected void shutDown() {
        overlayManager.remove(overlay);
        overlayManager.remove(prayerOverlay);
        duskNPC = null;
        duskTicks = -1;
        currentAttackStyle = DuskAttackStyles.UNKNOWN;
        dawnState = DawnStates.UNKNOWN;
        duskState = DuskStates.UNKNOWN;
        cleanup();
    }

    private void cleanup() {
        dawnNPC = null;
        dawnTicks = -1;
        stepBack = false;
        currentAttackStyle = DuskAttackStyles.UNKNOWN;
        dawnHealingOrbs.clear();
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (event.getActor() == duskNPC) {
            int animation = duskNPC.getAnimation();
            if (GGUtil.contains(GGUtil.DUSK_ATTACK_ANIMATIONS, animation)) {
                duskTicks = 7;
            } else if (animation == 7802) {
                duskTicks = 11;
                duskState = DuskStates.CANT_ATTACK;
                stepBack = true;
            } else if (animation == 7803) {
                log.debug("Finished Phase 4");
                phaseTimeClientMessage("Dusk", 2, System.currentTimeMillis() - startTime[3], System.currentTimeMillis() - startTime[0]);
            }
        }
        if (event.getActor() == dawnNPC) {
            int animation = dawnNPC.getAnimation();
            if (animation == 7776) {
                log.debug("Finished Phase 3");
                phaseTimeClientMessage("Dawn", 2, System.currentTimeMillis() - startTime[2], System.currentTimeMillis() - startTime[0]);
            }
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        int[] loadedRegions = client.getMapRegions();
        for (int region : loadedRegions) {
            if (region == GGUtil.REGION_ID) {
                inGarg = true;
                break;
            }
        }
        if (!inGarg)
            return;
        if (duskNPC != null) {
            duskTicks--;
            if (duskNPC.getAnimation() == 7802 && duskTicks <= 7) {
                duskState = DuskStates.ATTACK;
                stepBack = false;
            }
            if (duskNPC.getId() == 7888)
                switch (duskNPC.getAnimation()) {
                    case 7800:
                        currentAttackStyle = DuskAttackStyles.MELEE;
                        break;
                    case 7801:
                        currentAttackStyle = DuskAttackStyles.RANGE;
                        break;
                }
        }
        Set<Projectile> dawnProjectiles = new HashSet<>();
        Iterator<Projectile> iterator = client.getProjectiles().iterator();
        if (dawnNPC != null) {
            dawnTicks--;
            Iterator<GameObject> orbsIterator = dawnHealingOrbs.keySet().iterator();
            while (orbsIterator.hasNext()) {
                GameObject key = orbsIterator.next();
                dawnHealingOrbs.replace(key, dawnHealingOrbs.get(key) - 1);
                if (dawnHealingOrbs.get(key) < 0)
                    orbsIterator.remove();
            }
            while (iterator.hasNext()) {
                Projectile projectile = iterator.next();
                dawnProjectiles.add(projectile);
                if (!projectiles.contains(projectile)) {
                    int projectileId = projectile.getId();
                    if (GGUtil.RANGED_PROJECTILE == projectileId || GGUtil.STONE_RANGED_PROJECTILE == projectileId || GGUtil.HEALING_ORB_PROJECTILE == projectileId
                            || (dawnTicks <= 0 && dawnNPC.getInteracting() == client.getLocalPlayer()))
                        dawnTicks = 6;
                }
            }
        }
        projectiles.clear();
        projectiles = dawnProjectiles;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        int npcId = npc.getId();
        if (GGUtil.foundDawn(npc)) {
            dawnTicks = 0;
            dawnNPC = npc;
            dawnHealingOrbs.clear();
            projectiles.clear();
        }
        if (GGUtil.foundDusk(npc))
            duskNPC = npc;
        if (npcId == 7851 || npcId == 7852) {
            log.debug("Starting Phase 1");
            startTime[0] = System.currentTimeMillis();
            dawnState = DawnStates.ATTACK;
            duskState = DuskStates.CANT_ATTACK;
        }
        if (npcId == 7853) {
            dawnTicks = 17;
            dawnState = DawnStates.TRANSITION;
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        NPC npc = event.getNpc();
        switch (npc.getId()) {
            case 7853:
                log.debug("Finished Phase 1");
                phaseTimeClientMessage("Dawn", 1, System.currentTimeMillis() - startTime[0], System.currentTimeMillis() - startTime[0]);
                dawnState = DawnStates.TRANSITION;
                break;
            case 7882:
                log.debug("Starting Phase 2");
                startTime[1] = System.currentTimeMillis();
                duskState = DuskStates.ATTACK;
                break;
            case 7883:
                duskState = DuskStates.CANT_ATTACK;
                break;
            case 7855:
                log.debug("Finished Phase 2");
                log.debug("Dusk NPC ID:" + npc.getId() + " Animation ID: " + npc.getAnimation() + " DuskState: " + duskState);
                phaseTimeClientMessage("Dusk", 1, System.currentTimeMillis() - startTime[1], System.currentTimeMillis() - startTime[0]);
                duskTicks = 0;
                duskState = DuskStates.TRANSITION;
                break;
            case 7884:
                log.debug("Starting Phase 3");
                startTime[2] = System.currentTimeMillis();
                dawnState = DawnStates.ATTACK;
                break;
            case 7885:
                dawnState = DawnStates.TRANSITION;
                break;
            case 7886:
                duskTicks = 27;
                duskState = DuskStates.TRANSITION;
                break;
            case 7888:
                log.debug("Starting Phase 4");
                startTime[3] = System.currentTimeMillis();
                duskState = DuskStates.ATTACK;
                break;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        NPC npc = event.getNpc();
        if (GGUtil.foundDawn(npc)) {
            projectiles.clear();
            cleanup();
        }
        if (GGUtil.foundDusk(npc)) {
            duskNPC = null;
            duskTicks = -1;
            cleanup();
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        if (!inGarg)
            return;
        if (dawnNPC != null) {
            GameObject gameObject = event.getGameObject();
            switch (gameObject.getId()) {
                case 31686:
                case 31687:
                case 31688:
                    dawnHealingOrbs.put(gameObject, 30);
                    break;
            }
        }
    }

    protected void phaseTimeMessageBuilder(String bossName, int phaseNumber, long fightTime, long totalTime) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder = stringBuilder
                .append("'")
                .append(bossName).append(" Phase ")
                .append(phaseNumber).append("'")
                .append(" completed! Duration: <col=FF0000>")
                .append(millisToText(fightTime))
                .append(" </col>Total: <col=FF0000>")
                .append(millisToText(totalTime));
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", stringBuilder.toString(), "");
    }

    protected void phaseTimeClientMessage(String bossName, int phaseNumber, long fightTime, long totalTime) {
        if (config.splitsTimer())
            phaseTimeMessageBuilder(bossName, phaseNumber, fightTime, totalTime);
    }

    private String millisToText(long deltaTime) {
        long seconds = deltaTime / 1000L;
        long minutes = seconds / 60L;
        seconds %= 60L;
        return String.format("%2d:%2d", minutes, seconds).replaceAll(" ", "0");
    }

    /*@Subscribe
    private void onClientTick(ClientTick event)
    {
        if (client.isMirrored() && !mirrorMode) {
            overlay.setLayer(OverlayLayer.AFTER_MIRROR);
            prayerOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            mirrorMode = true;
        }
    }*/

}
