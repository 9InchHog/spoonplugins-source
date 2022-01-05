package net.runelite.client.plugins.spoondemonicgorilla;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Demonic Gorillas",
        description = "Count demonic gorilla attacks and display their next possible attack styles",
        tags = {"combat", "overlay", "pve", "pvm"}
)
@Singleton
public class DemonicGorillaPlugin extends Plugin {
    private static final Set<Integer> DEMONIC_PROJECTILES = (Set<Integer>)ImmutableSet.of(Integer.valueOf(1302), Integer.valueOf(1304), Integer.valueOf(856));

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private DemonicGorillaOverlay overlay;

    @Inject
    private ClientThread clientThread;

    private Map<NPC, DemonicGorilla> gorillas;

    private List<WorldPoint> recentBoulders;

    private List<PendingGorillaAttack> pendingAttacks;

    private Map<Player, MemorizedPlayer> memorizedPlayers;

    Map<NPC, DemonicGorilla> getGorillas() {
        return this.gorillas;
    }

    private boolean mirrorMode;

    protected void startUp() {
        this.overlayManager.add(this.overlay);
        this.gorillas = new HashMap<>();
        this.recentBoulders = new ArrayList<>();
        this.pendingAttacks = new ArrayList<>();
        this.memorizedPlayers = new HashMap<>();
        this.clientThread.invoke(this::reset);
    }

    protected void shutDown() {
        this.overlayManager.remove(this.overlay);
        this.gorillas = null;
        this.recentBoulders = null;
        this.pendingAttacks = null;
        this.memorizedPlayers = null;
    }

    private void clear() {
        this.recentBoulders.clear();
        this.pendingAttacks.clear();
        this.memorizedPlayers.clear();
        this.gorillas.clear();
    }

    private void reset() {
        this.recentBoulders.clear();
        this.pendingAttacks.clear();
        resetGorillas();
        resetPlayers();
    }

    private void resetGorillas() {
        this.gorillas.clear();
        for (NPC npc : this.client.getNpcs()) {
            if (isNpcGorilla(npc.getId()))
                this.gorillas.put(npc, new DemonicGorilla(npc));
        }
    }

    private void resetPlayers() {
        this.memorizedPlayers.clear();
        for (Player player : this.client.getPlayers())
            this.memorizedPlayers.put(player, new MemorizedPlayer(player));
    }

    private static boolean isNpcGorilla(int npcId) {
        return (npcId == 7144 || npcId == 7145 || npcId == 7146 || npcId == 7147 || npcId == 7148 || npcId == 7149);
    }

    private void checkGorillaAttackStyleSwitch(DemonicGorilla gorilla, DemonicGorilla.AttackStyle... protectedStyles) {
        if (gorilla.getAttacksUntilSwitch() <= 0 || gorilla
                .getNextPosibleAttackStyles().isEmpty()) {
            gorilla.setNextPosibleAttackStyles(
                    (List<DemonicGorilla.AttackStyle>)Arrays.<DemonicGorilla.AttackStyle>stream(DemonicGorilla.ALL_REGULAR_ATTACK_STYLES)
                            .filter(x -> Arrays.<DemonicGorilla.AttackStyle>stream(protectedStyles).noneMatch(y -> x == y))
                            .collect(Collectors.toList()));
            gorilla.setAttacksUntilSwitch(3);
            gorilla.setChangedAttackStyleThisTick(true);
        }
    }

    private DemonicGorilla.AttackStyle getProtectedStyle(Player player) {
        HeadIcon headIcon = player.getOverheadIcon();
        if (headIcon == null)
            return null;
        switch (headIcon) {
            case MELEE:
                return DemonicGorilla.AttackStyle.MELEE;
            case RANGED:
                return DemonicGorilla.AttackStyle.RANGED;
            case MAGIC:
                return DemonicGorilla.AttackStyle.MAGIC;
        }
        return null;
    }

    private void onGorillaAttack(DemonicGorilla gorilla, DemonicGorilla.AttackStyle attackStyle) {
        gorilla.setInitiatedCombat(true);
        Player target = (Player)gorilla.getNpc().getInteracting();
        DemonicGorilla.AttackStyle protectedStyle = null;
        if (target != null)
            protectedStyle = getProtectedStyle(target);
        boolean correctPrayer = (target == null || (attackStyle != null && attackStyle.equals(protectedStyle)));
        if (attackStyle == DemonicGorilla.AttackStyle.BOULDER) {
            gorilla.setNextPosibleAttackStyles((List<DemonicGorilla.AttackStyle>)gorilla
                    .getNextPosibleAttackStyles()
                    .stream()
                    .filter(x -> (x != DemonicGorilla.AttackStyle.MELEE))
                    .collect(Collectors.toList()));
        } else {
            if (correctPrayer) {
                gorilla.setAttacksUntilSwitch(gorilla.getAttacksUntilSwitch() - 1);
            } else {
                int damagesOnTick = this.client.getTickCount();
                if (attackStyle == DemonicGorilla.AttackStyle.MAGIC) {
                    MemorizedPlayer mp = this.memorizedPlayers.get(target);
                    WorldArea lastPlayerArea = mp.getLastWorldArea();
                    if (lastPlayerArea != null) {
                        int dist = gorilla.getNpc().getWorldArea().distanceTo(lastPlayerArea);
                        damagesOnTick += (dist + 12) / 8;
                    }
                } else if (attackStyle == DemonicGorilla.AttackStyle.RANGED) {
                    MemorizedPlayer mp = this.memorizedPlayers.get(target);
                    WorldArea lastPlayerArea = mp.getLastWorldArea();
                    if (lastPlayerArea != null) {
                        int dist = gorilla.getNpc().getWorldArea().distanceTo(lastPlayerArea);
                        damagesOnTick += (dist + 9) / 6;
                    }
                }
                this.pendingAttacks.add(new PendingGorillaAttack(gorilla, attackStyle, target, damagesOnTick));
            }
            gorilla.setNextPosibleAttackStyles((List<DemonicGorilla.AttackStyle>)gorilla
                    .getNextPosibleAttackStyles()
                    .stream()
                    .filter(x -> (x == attackStyle))
                    .collect(Collectors.toList()));
            if (gorilla.getNextPosibleAttackStyles().isEmpty()) {
                gorilla.setNextPosibleAttackStyles(
                        (List<DemonicGorilla.AttackStyle>)Arrays.<DemonicGorilla.AttackStyle>stream(DemonicGorilla.ALL_REGULAR_ATTACK_STYLES)
                                .filter(x -> (x == attackStyle))
                                .collect(Collectors.toList()));
                gorilla.setAttacksUntilSwitch(3 - (correctPrayer ? 1 : 0));
            }
        }
        checkGorillaAttackStyleSwitch(gorilla, new DemonicGorilla.AttackStyle[] { protectedStyle });
        int tickCounter = this.client.getTickCount();
        gorilla.setNextAttackTick(tickCounter + 5);
    }

    private void checkGorillaAttacks() {
        int tickCounter = this.client.getTickCount();
        for (Iterator<DemonicGorilla> iterator = this.gorillas.values().iterator(); iterator.hasNext(); ) {
            DemonicGorilla gorilla = iterator.next();
            Player interacting = (Player)gorilla.getNpc().getInteracting();
            MemorizedPlayer mp = this.memorizedPlayers.get(interacting);
            if (gorilla.getLastTickInteracting() != null && interacting == null) {
                gorilla.setInitiatedCombat(false);
            } else if (mp != null && mp.getLastWorldArea() != null &&
                    !gorilla.isInitiatedCombat() && tickCounter < gorilla
                    .getNextAttackTick() && gorilla
                    .getNpc().getWorldArea().isInMeleeDistance(mp.getLastWorldArea())) {
                gorilla.setInitiatedCombat(true);
                gorilla.setNextAttackTick(tickCounter + 1);
            }
            int animationId = gorilla.getNpc().getAnimation();
            if (gorilla.isTakenDamageRecently() && tickCounter >= gorilla
                    .getNextAttackTick() + 4) {
                gorilla.setNextAttackTick(tickCounter + 2);
                gorilla.setInitiatedCombat(true);
                if (mp != null && mp.getLastWorldArea() != null &&
                        !gorilla.getNpc().getWorldArea().isInMeleeDistance(mp.getLastWorldArea()) &&
                        !gorilla.getNpc().getWorldArea().intersectsWith(mp.getLastWorldArea())) {
                    gorilla.setNextPosibleAttackStyles((List<DemonicGorilla.AttackStyle>)gorilla
                            .getNextPosibleAttackStyles()
                            .stream()
                            .filter(x -> (x != DemonicGorilla.AttackStyle.MELEE))
                            .collect(Collectors.toList()));
                    if (interacting != null)
                        checkGorillaAttackStyleSwitch(gorilla, new DemonicGorilla.AttackStyle[] { DemonicGorilla.AttackStyle.MELEE,
                                getProtectedStyle(interacting) });
                }
            } else if (animationId != gorilla.getLastTickAnimation()) {
                if (animationId == 7226) {
                    onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.MELEE);
                } else if (animationId == 7225) {
                    onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.MAGIC);
                } else if (animationId == 7227) {
                    onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.RANGED);
                } else if (animationId == 7228 && interacting != null) {
                    if (gorilla.getOverheadIcon() == gorilla.getLastTickOverheadIcon()) {
                        onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.BOULDER);
                    } else {
                        if (tickCounter >= gorilla.getNextAttackTick()) {
                            gorilla.setChangedPrayerThisTick(true);
                            int projectileId = gorilla.getRecentProjectileId();
                            if (projectileId == 1304) {
                                onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.MAGIC);
                            } else if (projectileId == 1302) {
                                onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.RANGED);
                            } else if (mp != null) {
                                WorldArea lastPlayerArea = mp.getLastWorldArea();
                                if (lastPlayerArea != null && this.recentBoulders.stream()
                                        .anyMatch(x -> (x.distanceTo(lastPlayerArea) == 0))) {
                                    onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.BOULDER);
                                } else if (!mp.getRecentHitsplats().isEmpty()) {
                                    onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.MELEE);
                                }
                            }
                        }
                        gorilla.setNextAttackTick(tickCounter + 5);
                        gorilla.setChangedPrayerThisTick(true);
                    }
                }
            }
            if (gorilla.getDisabledMeleeMovementForTicks() > 0) {
                gorilla.setDisabledMeleeMovementForTicks(gorilla.getDisabledMeleeMovementForTicks() - 1);
            } else if (gorilla.isInitiatedCombat() && gorilla
                    .getNpc().getInteracting() != null &&
                    !gorilla.isChangedAttackStyleThisTick() && gorilla
                    .getNextPosibleAttackStyles().size() >= 2 && gorilla
                    .getNextPosibleAttackStyles().stream()
                    .anyMatch(x -> (x == DemonicGorilla.AttackStyle.MELEE))) {
                if (mp != null && mp.getLastWorldArea() != null && gorilla.getLastWorldArea() != null) {
                    WorldArea predictedNewArea = gorilla.getLastWorldArea().calculateNextTravellingPoint(
                            client, mp.getLastWorldArea(), true, x ->
                            {
                                // Gorillas can't normally walk through other gorillas
                                // or other players
                                final WorldArea area1 = new WorldArea(x, 1, 1);
                                return gorillas.values().stream().noneMatch(y ->
                                {
                                    if (y == gorilla)
                                    {
                                        return false;
                                    }
                                    final WorldArea area2 =
                                            y.getNpc().getIndex() < gorilla.getNpc().getIndex() ?
                                                    y.getNpc().getWorldArea() : y.getLastWorldArea();
                                    return area2 != null && area1.intersectsWith(area2);
                                }) && memorizedPlayers.values().stream().noneMatch(y ->
                                {
                                    final WorldArea area2 = y.getLastWorldArea();
                                    return area2 != null && area1.intersectsWith(area2);
                                });

                                // There is a special case where if a player walked through
                                // a gorilla, or a player walked through another player,
                                // the tiles that were walked through becomes
                                // walkable, but I didn't feel like it's necessary to handle
                                // that special case as it should rarely happen.
                            });
                    if (predictedNewArea != null) {
                        int distance = gorilla.getNpc().getWorldArea().distanceTo(mp.getLastWorldArea());
                        WorldPoint predictedMovement = predictedNewArea.toWorldPoint();
                        if (distance <= 10 && mp.getLastWorldArea().hasLineOfSightTo(this.client, gorilla.getLastWorldArea()))
                            if (predictedMovement.distanceTo(gorilla.getLastWorldArea().toWorldPoint()) != 0) {
                                if (predictedMovement.distanceTo(gorilla.getNpc().getWorldLocation()) == 0) {
                                    gorilla.setNextPosibleAttackStyles((List<DemonicGorilla.AttackStyle>)gorilla
                                            .getNextPosibleAttackStyles()
                                            .stream()
                                            .filter(x -> (x == DemonicGorilla.AttackStyle.MELEE))
                                            .collect(Collectors.toList()));
                                } else {
                                    gorilla.setNextPosibleAttackStyles((List<DemonicGorilla.AttackStyle>)gorilla
                                            .getNextPosibleAttackStyles()
                                            .stream()
                                            .filter(x -> (x != DemonicGorilla.AttackStyle.MELEE))
                                            .collect(Collectors.toList()));
                                }
                            } else if (tickCounter >= gorilla.getNextAttackTick() && gorilla
                                    .getRecentProjectileId() == -1 && this.recentBoulders
                                    .stream().noneMatch(x -> (x.distanceTo(mp.getLastWorldArea()) == 0))) {
                                gorilla.setNextPosibleAttackStyles((List<DemonicGorilla.AttackStyle>)gorilla
                                        .getNextPosibleAttackStyles()
                                        .stream()
                                        .filter(x -> (x == DemonicGorilla.AttackStyle.MELEE))
                                        .collect(Collectors.toList()));
                            }
                    }
                }
            }
            if (gorilla.isTakenDamageRecently())
                gorilla.setInitiatedCombat(true);
            if (gorilla.getOverheadIcon() != gorilla.getLastTickOverheadIcon())
                if (gorilla.isChangedAttackStyleLastTick() || gorilla
                        .isChangedAttackStyleThisTick()) {
                    gorilla.setDisabledMeleeMovementForTicks(2);
                } else {
                    gorilla.setDisabledMeleeMovementForTicks(1);
                }
            gorilla.setLastTickAnimation(gorilla.getNpc().getAnimation());
            gorilla.setLastWorldArea(gorilla.getNpc().getWorldArea());
            gorilla.setLastTickInteracting(gorilla.getNpc().getInteracting());
            gorilla.setTakenDamageRecently(false);
            gorilla.setChangedPrayerThisTick(false);
            gorilla.setChangedAttackStyleLastTick(gorilla.isChangedAttackStyleThisTick());
            gorilla.setChangedAttackStyleThisTick(false);
            gorilla.setLastTickOverheadIcon(gorilla.getOverheadIcon());
            gorilla.setRecentProjectileId(-1);
        }
    }

    @Subscribe
    private void onProjectileMoved(ProjectileMoved event) {
        Projectile projectile = event.getProjectile();
        int projectileId = projectile.getId();
        if (!DEMONIC_PROJECTILES.contains(Integer.valueOf(projectileId)))
            return;
        WorldPoint loc = WorldPoint.fromLocal(this.client, projectile.getX1(), projectile.getY1(), this.client.getPlane());
        if (projectileId == 856) {
            this.recentBoulders.add(loc);
        } else {
            for (DemonicGorilla gorilla : this.gorillas.values()) {
                if (gorilla.getNpc().getWorldLocation().distanceTo(loc) == 0)
                    gorilla.setRecentProjectileId(projectile.getId());
            }
        }
    }

    private void checkPendingAttacks() {
        Iterator<PendingGorillaAttack> it = this.pendingAttacks.iterator();
        int tickCounter = this.client.getTickCount();
        while (it.hasNext()) {
            PendingGorillaAttack attack = it.next();
            if (tickCounter >= attack.getFinishesOnTick()) {
                boolean shouldDecreaseCounter = false;
                DemonicGorilla gorilla = attack.getAttacker();
                MemorizedPlayer target = this.memorizedPlayers.get(attack.getTarget());
                if (target == null) {
                    shouldDecreaseCounter = true;
                } else if (target.getRecentHitsplats().isEmpty()) {
                    shouldDecreaseCounter = true;
                } else if (target.getRecentHitsplats().stream()
                        .anyMatch(x -> (x.getHitsplatType() == Hitsplat.HitsplatType.BLOCK_ME))) {
                    shouldDecreaseCounter = true;
                }
                if (shouldDecreaseCounter) {
                    gorilla.setAttacksUntilSwitch(gorilla.getAttacksUntilSwitch() - 1);
                    checkGorillaAttackStyleSwitch(gorilla, new DemonicGorilla.AttackStyle[0]);
                }
                it.remove();
            }
        }
    }

    private void updatePlayers() {
        for (MemorizedPlayer mp : this.memorizedPlayers.values()) {
            mp.setLastWorldArea(mp.getPlayer().getWorldArea());
            mp.getRecentHitsplats().clear();
        }
    }

    @Subscribe
    private void onHitsplatApplied(HitsplatApplied event) {
        if (this.gorillas.isEmpty())
            return;
        if (event.getActor() instanceof Player) {
            Player player = (Player)event.getActor();
            MemorizedPlayer mp = this.memorizedPlayers.get(player);
            if (mp != null)
                mp.getRecentHitsplats().add(event.getHitsplat());
        } else if (event.getActor() instanceof NPC) {
            DemonicGorilla gorilla = this.gorillas.get(event.getActor());
            Hitsplat.HitsplatType hitsplatType = event.getHitsplat().getHitsplatType();
            if (gorilla != null && (hitsplatType == Hitsplat.HitsplatType.BLOCK_ME || hitsplatType == Hitsplat.HitsplatType.DAMAGE_ME))
                gorilla.setTakenDamageRecently(true);
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        GameState gs = event.getGameState();
        if (gs == GameState.LOGGING_IN || gs == GameState.CONNECTION_LOST || gs == GameState.HOPPING)
            reset();
    }

    @Subscribe
    private void onPlayerSpawned(PlayerSpawned event) {
        if (this.gorillas.isEmpty())
            return;
        Player player = event.getPlayer();
        this.memorizedPlayers.put(player, new MemorizedPlayer(player));
    }

    @Subscribe
    private void onPlayerDespawned(PlayerDespawned event) {
        if (this.gorillas.isEmpty())
            return;
        this.memorizedPlayers.remove(event.getPlayer());
    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        if (isNpcGorilla(npc.getId())) {
            if (this.gorillas.isEmpty())
                resetPlayers();
            this.gorillas.put(npc, new DemonicGorilla(npc));
        }
    }

    @Subscribe
    private void onNpcDespawned(NpcDespawned event) {
        if (this.gorillas.remove(event.getNpc()) != null && this.gorillas.isEmpty())
            clear();
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        checkGorillaAttacks();
        checkPendingAttacks();
        updatePlayers();
        this.recentBoulders.clear();
    }

    /*@Subscribe
    private void onClientTick(ClientTick event)
    {
        if (client.isMirrored() && !mirrorMode) {
            overlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(overlay);
            overlayManager.add(overlay);
            mirrorMode = true;
        }
    }*/
}
