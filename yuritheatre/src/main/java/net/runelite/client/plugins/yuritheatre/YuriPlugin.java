package net.runelite.client.plugins.yuritheatre;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
@PluginDescriptor(
        name = "[Y] Theatre",
        description = "This is Yuri-chan's Theatre of Blood plugin.",
        tags = {"theatre", "theater", "yuri"},
        enabledByDefault = true
)
public class YuriPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(YuriPlugin.class);

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    private YuriOverlay overlay;

    @Inject
    private YuriPrayerOverlay prayerOverlay;

    @Inject
    private YuriConfig config;

    private int nylocasDelay;

    private int lastNylocasId;

    private NPC nylocasBoss;

    @Provides
    YuriConfig getConfig(ConfigManager configManager) {
        return (YuriConfig)configManager.getConfig(YuriConfig.class);
    }

    public int getNylocasDelay() {
        return this.nylocasDelay;
    }

    public NPC getNylocasBoss() {
        return this.nylocasBoss;
    }

    private int verzikAttacksLeft = 4;

    public int getVerzikAttacksLeft() {
        return this.verzikAttacksLeft;
    }

    public void setVerzikAttacksLeft(int verzikAttacksLeft) {
        this.verzikAttacksLeft = verzikAttacksLeft;
    }

    private Map<NPC, Integer> nylocasPillars = new HashMap<>();

    private NPC bloatBoss;

    public NPC getBloatBoss() {
        return this.bloatBoss;
    }

    private int[] nextBloatAttack = null;

    public int[] getNextBloatAttack() {
        return this.nextBloatAttack;
    }

    private List<int[]> bloatLocations = (List)new ArrayList<>();

    public List<int[]> getBloatLocations() {
        return this.bloatLocations;
    }

    private int nextBloatTicks = -1;

    public int getNextBloatTicks() {
        return this.nextBloatTicks;
    }

    private boolean mirrorMode;

    protected void startUp() {
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.prayerOverlay);
        this.nylocasDelay = -1;
        this.lastNylocasId = -1;
        this.nylocasBoss = null;
        this.verzikAttacksLeft = 4;
        this.verzikRangedAttacks.clear();
        this.sotetsegAttacksLeft = 10;
        this.sotetsegBomb = null;
        this.nylocasPillars.clear();
        this.sotetsegAttacks.clear();
        this.bloatBoss = null;
        this.nextBloatAttack = null;
        this.bloatLocations.clear();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j <= 10; j++) {
                if (i == 0) {
                    this.bloatLocations.add(new int[] { 26, 26 + j });
                } else if (i == 1) {
                    this.bloatLocations.add(new int[] { 26 + j, 37 });
                } else if (i == 2) {
                    this.bloatLocations.add(new int[] { 37, 37 - j });
                } else if (i == 3) {
                    this.bloatLocations.add(new int[] { 37 - j, 26 });
                }
            }
        }
    }

    protected void shutDown() {
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.prayerOverlay);
    }

    private int sotetsegAttacksLeft = 10;

    public int getSotetsegAttacksLeft() {
        return this.sotetsegAttacksLeft;
    }

    public void setSotetsegAttacksLeft(int sotetsegAttacksLeft) {
        this.sotetsegAttacksLeft = sotetsegAttacksLeft;
    }

    private Projectile sotetsegBomb = null;

    @Subscribe
    private void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        if (npc == null)
            return;
        switch (npc.getId()) {
            case 8355:
            case 8356:
            case 8357:
                this.nylocasBoss = npc;
                this.lastNylocasId = npc.getId();
                this.nylocasDelay = 10;
                this.sotetsegAttacksLeft = 10;
                break;
            case 8358:
                this.nylocasPillars.put(npc, 100);
                break;
            case 8359:
            case 10812:
            case 10813:
                this.bloatBoss = npc;
                this.nextBloatAttack = null;
                break;
        }
    }

    @Subscribe
    private void onNpcDespawned(NpcDespawned event) {
        NPC npc = event.getNpc();
        if (npc == null)
            return;
        switch (npc.getId()) {
            case 8355:
            case 8356:
            case 8357:
                this.nylocasBoss = null;
                this.lastNylocasId = -1;
                this.nylocasDelay = -1;
                break;
            case 8358:
                this.nylocasPillars.remove(npc);
                break;
            case 8359:
            case 10812:
            case 10813:
                this.bloatBoss = null;
                this.nextBloatAttack = null;
                break;
        }
    }

    private List<Projectile> verzikRangedAttacks = new ArrayList<>();

    public List<Projectile> getVerzikRangedAttacks() {
        return this.verzikRangedAttacks;
    }

    private List<Projectile> sotetsegAttacks = new ArrayList<>();

    public List<Projectile> getSotetsegAttacks() {
        return this.sotetsegAttacks;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState state = event.getGameState();
        if (state == GameState.LOGIN_SCREEN || state == GameState.HOPPING) {
            this.bloatBoss = null;
            this.nextBloatAttack = null;
            this.nylocasDelay = -1;
            this.lastNylocasId = -1;
            this.nylocasBoss = null;
            this.nylocasPillars.clear();
            this.sotetsegAttacks.clear();
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (this.bloatBoss != null) {
            Actor actor = event.getActor();
            if (actor instanceof Player) {
                Player p = (Player)actor;
                if (p == this.client.getLocalPlayer()) {
                    int id = p.getAnimation();
                    int delay = -1;
                    if (id == 8056) {
                        delay = 5;
                    } else if (id == 7642) {
                        delay = 6;
                    }
                    if (delay != -1) {
                        this.nextBloatTicks = delay + 1;
                        LocalPoint lp = this.bloatBoss.getLocalLocation();
                        if (lp == null)
                            return;
                        WorldPoint wp = WorldPoint.fromLocalInstance(this.client, lp);
                        if (wp == null)
                            return;
                        int rX = wp.getRegionX();
                        int rY = wp.getRegionY();
                        int dir = this.bloatBoss.getOrientation();
                        int currentIndex = -1;
                        int pathLength = this.bloatLocations.size();
                        for (int i = 0; i < pathLength; i++) {
                            int[] j = this.bloatLocations.get(i);
                            if (j[0] == rX && j[1] == rY) {
                                currentIndex = i;
                                break;
                            }
                        }
                        if (currentIndex == -1)
                            return;
                        int direction = 1;
                        if ((rX == 26 && dir == 0) || (rY == 37 && dir == 512) || (rX == 37 && dir == 1024) || (rY == 26 && dir == 1536))
                            direction = -1;
                        int nextIndex = currentIndex + direction * delay;
                        if (nextIndex < 0)
                            nextIndex = nextIndex % pathLength + pathLength;
                        if (nextIndex >= pathLength)
                            nextIndex %= pathLength;
                        this.nextBloatAttack = this.bloatLocations.get(nextIndex);
                    }
                }
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (this.nextBloatTicks > 0)
            this.nextBloatTicks--;
        if (this.nylocasBoss != null) {
            this.nylocasDelay--;
            if (this.nylocasBoss.getId() != this.lastNylocasId) {
                this.lastNylocasId = this.nylocasBoss.getId();
                this.nylocasDelay = 10;
            }
        }
        boolean foundPillar = false;
        for (NPC npc : this.client.getNpcs()) {
            if (npc.getId() == 8358) {
                foundPillar = true;
                break;
            }
        }
        NPC minNPC = null;
        int minHealth = 100;
        if (foundPillar) {
            for (NPC npc : this.nylocasPillars.keySet()) {
                int health = (npc.getHealthRatio() > -1) ? npc.getHealthRatio() : this.nylocasPillars.get(npc);
                this.nylocasPillars.replace(npc, health);
                if (health < minHealth) {
                    minHealth = health;
                    minNPC = npc;
                }
            }
            if (minNPC != null && this.config.showNylocasPillar())
                this.client.setHintArrow(minNPC);
        } else {
            this.nylocasPillars.clear();
        }
        boolean newAttack = false;
        boolean newCycle = false;
        for (Projectile projectile : this.client.getProjectiles()) {
            if (projectile.getRemainingCycles() <= 0)
                continue;
            if (projectile.getId() == 1583) {
                if (!this.verzikRangedAttacks.contains(projectile)) {
                    this.verzikRangedAttacks.add(projectile);
                    newAttack = true;
                }
                continue;
            }
            if (projectile.getId() == 1585) {
                if (!this.verzikRangedAttacks.contains(projectile)) {
                    this.verzikRangedAttacks.add(projectile);
                    newCycle = true;
                }
                continue;
            }
            if (projectile.getId() == 1604 && this.sotetsegBomb == null) {
                this.sotetsegBomb = projectile;
                this.sotetsegAttacksLeft = 10;
                continue;
            }
            if (projectile.getId() == 1606 || projectile.getId() == 1607)
                if (!this.sotetsegAttacks.contains(projectile)) {
                    this.sotetsegAttacks.add(projectile);
                    NPC sotetseg = null;
                    for (NPC npc : this.client.getNpcs()) {
                        String name = npc.getName();
                        if (name == null)
                            continue;
                        if (name.equals("Sotetseg")) {
                            sotetseg = npc;
                            break;
                        }
                    }
                    if (sotetseg != null) {
                        LocalPoint sl = sotetseg.getLocalLocation();
                        if (sl.getX() == projectile.getX1() && sl.getY() == projectile.getY1())
                            this.sotetsegAttacksLeft--;
                    }
                }
        }
        if (newCycle) {
            this.verzikAttacksLeft = 4;
        } else if (newAttack) {
            this.verzikAttacksLeft--;
        }
        for (Projectile projectile : this.verzikRangedAttacks) {
            if (projectile.getRemainingCycles() <= 0)
                this.verzikRangedAttacks.remove(projectile);
        }
        for (Projectile projectile : this.sotetsegAttacks) {
            if (projectile.getRemainingCycles() <= 0)
                this.sotetsegAttacks.remove(projectile);
        }
        if (this.sotetsegBomb != null)
            if (this.sotetsegBomb.getRemainingCycles() <= 0)
                this.sotetsegBomb = null;
    }

    /*@Subscribe
    private void onClientTick(ClientTick event) {
        if (client.isMirrored() && !mirrorMode) {
            overlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(overlay);
            overlayManager.add(overlay);
            prayerOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(prayerOverlay);
            overlayManager.add(prayerOverlay);
            mirrorMode = true;
        }
    }*/
}
