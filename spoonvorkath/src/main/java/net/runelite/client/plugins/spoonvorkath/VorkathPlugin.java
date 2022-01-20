package net.runelite.client.plugins.spoonvorkath;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Projectile;
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

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Vorkath",
        description = "",
        tags = {"vorkath", "vork"},
        enabledByDefault = false
)
public class VorkathPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(VorkathPlugin.class);

    @Inject
    private Client client;

    @Inject
    OverlayManager overlayManager;

    @Inject
    VorkathOverlay vorkathOverlay;

    @Inject
    private VorkathConfig config;

    private NPC vorkathNpc;

    private NPC zombifiedSpawn;

    @Provides
    VorkathConfig getConfig(ConfigManager configManager) {
        return (VorkathConfig)configManager.getConfig(VorkathConfig.class);
    }

    public NPC getVorkathNpc() {
        return this.vorkathNpc;
    }

    public NPC getZombifiedSpawn() {
        return this.zombifiedSpawn;
    }

    private int attacksLeft = 6;

    public int getAttacksLeft() {
        return this.attacksLeft;
    }

    public void setAttacksLeft(int attacksLeft) {
        this.attacksLeft = attacksLeft;
    }

    private boolean vorkathInstance = false;

    private VorkathUtils.VorkathPhase currentVorkathPhase;

    private VorkathUtils.VorkathPhase nextVorkathPhase;

    public boolean isVorkathInstance() {
        return this.vorkathInstance;
    }

    public VorkathUtils.VorkathPhase getCurrentVorkathPhase() {
        return this.currentVorkathPhase;
    }

    public VorkathUtils.VorkathPhase getNextVorkathPhase() {
        return this.nextVorkathPhase;
    }

    private boolean notFireBombAttack = false;

    private boolean mirrorMode;

    protected void startUp() {
        this.overlayManager.add(this.vorkathOverlay);
    }

    protected void shutDown() {
        this.overlayManager.remove(this.vorkathOverlay);
        reset();
    }

    public void reset() {
        this.currentVorkathPhase = VorkathUtils.VorkathPhase.UNKNOWN;
        this.nextVorkathPhase = VorkathUtils.VorkathPhase.UNKNOWN;
        this.attacksLeft = 6;
        this.notFireBombAttack = false;
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        int[] loadedRegions = this.client.getMapRegions();
        for (int region : loadedRegions) {
            if (region == 9023)
                this.vorkathInstance = true;
        }
        if (!this.vorkathInstance)
            return;
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved e) {
        Projectile projectile = e.getProjectile();
        if (this.client.getGameCycle() >= projectile.getStartCycle())
            return;
        int projectileId = projectile.getId();
        if (this.vorkathNpc != null)
            if (projectileId == 395) {
                this.attacksLeft = 6;
                this.notFireBombAttack = true;
                this.currentVorkathPhase = VorkathUtils.VorkathPhase.ZOMBIFIED_SPAWN;
                this.nextVorkathPhase = VorkathUtils.VorkathPhase.ACID;
                log.debug("[Vorkath] Projectile: {}, Attacks Left: {}, Current Phase: {}, Next Phase: {}", projectileId, this.attacksLeft, this.currentVorkathPhase, this.nextVorkathPhase);
            }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged e) {
        if (e.getActor() == this.vorkathNpc) {
            int animation = this.vorkathNpc.getAnimation();
            switch (animation) {
                case 7949:
                case 7950:
                    reset();
                    log.debug("[Vorkath] Animation ID: {}, Attacks Left: {}, Current Phase: {}, Next Phase: {}", animation, this.attacksLeft, this.currentVorkathPhase, this.nextVorkathPhase);
                    break;
                case 7952:
                case 7960:
                    if (this.notFireBombAttack) {
                        this.notFireBombAttack = false;
                        break;
                    }
                    setAttacksLeft(getAttacksLeft() - 1);
                    log.debug("[Vorkath] Animation ID: {}, Attacks Left: {}, Current Phase: {}, Next Phase: {}", animation, Integer.valueOf(this.attacksLeft), this.currentVorkathPhase, this.nextVorkathPhase);
                    break;
                case 7951:
                    setAttacksLeft(getAttacksLeft() - 1);
                    log.debug("[Vorkath] Animation ID: {}, Attacks Left: {}, Current Phase: {}, Next Phase: {}", new Object[] { Integer.valueOf(animation), Integer.valueOf(this.attacksLeft), this.currentVorkathPhase, this.nextVorkathPhase });
                    break;
                case 7957:
                    this.attacksLeft = 6;
                    this.currentVorkathPhase = VorkathUtils.VorkathPhase.ACID;
                    this.nextVorkathPhase = VorkathUtils.VorkathPhase.ZOMBIFIED_SPAWN;
                    log.debug("[Vorkath] AnimationID: {}, Attacks Left: {}, Current Phase: {}, Next Phase: {}", new Object[] { Integer.valueOf(animation), Integer.valueOf(this.attacksLeft), this.currentVorkathPhase, this.nextVorkathPhase });
                    break;
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned e) {
        NPC npc = e.getNpc();
        if (VorkathUtils.foundVorkath(npc)) {
            this.vorkathNpc = npc;
        } else if (VorkathUtils.foundZombifiedSpawn(npc)) {
            this.zombifiedSpawn = npc;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned e) {
        NPC npc = e.getNpc();
        if (VorkathUtils.foundVorkath(npc)) {
            this.vorkathNpc = null;
            reset();
        } else if (VorkathUtils.foundZombifiedSpawn(npc)) {
            this.zombifiedSpawn = null;
        }
    }

    /*@Subscribe
    private void onClientTick(ClientTick event) {
        if (client.isMirrored() && !mirrorMode) {
            vorkathOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(vorkathOverlay);
            overlayManager.add(vorkathOverlay);
            mirrorMode = true;
        }
    }*/
}
