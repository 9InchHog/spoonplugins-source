package net.runelite.client.plugins.spoondemonicgorilla;

import java.util.Arrays;
import java.util.List;
import net.runelite.api.Actor;
import net.runelite.api.HeadIcon;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.coords.WorldArea;

class DemonicGorilla {
    static final int MAX_ATTACK_RANGE = 10;

    static final int ATTACK_RATE = 5;

    static final int ATTACKS_PER_SWITCH = 3;

    static final int PROJECTILE_MAGIC_SPEED = 8;

    static final int PROJECTILE_RANGED_SPEED = 6;

    static final int PROJECTILE_MAGIC_DELAY = 12;

    static final int PROJECTILE_RANGED_DELAY = 9;

    static final AttackStyle[] ALL_REGULAR_ATTACK_STYLES = new AttackStyle[] { AttackStyle.MELEE, AttackStyle.RANGED, AttackStyle.MAGIC };

    private NPC npc;

    private List<AttackStyle> nextPosibleAttackStyles;

    private int attacksUntilSwitch;

    private int nextAttackTick;

    private int lastTickAnimation;

    private WorldArea lastWorldArea;

    private boolean initiatedCombat;

    private Actor lastTickInteracting;

    private boolean takenDamageRecently;

    private int recentProjectileId;

    private boolean changedPrayerThisTick;

    private boolean changedAttackStyleThisTick;

    private boolean changedAttackStyleLastTick;

    private HeadIcon lastTickOverheadIcon;

    private int disabledMeleeMovementForTicks;

    enum AttackStyle {
        MAGIC, RANGED, MELEE, BOULDER;
    }

    NPC getNpc() {
        return this.npc;
    }

    List<AttackStyle> getNextPosibleAttackStyles() {
        return this.nextPosibleAttackStyles;
    }

    void setNextPosibleAttackStyles(List<AttackStyle> nextPosibleAttackStyles) {
        this.nextPosibleAttackStyles = nextPosibleAttackStyles;
    }

    int getAttacksUntilSwitch() {
        return this.attacksUntilSwitch;
    }

    void setAttacksUntilSwitch(int attacksUntilSwitch) {
        this.attacksUntilSwitch = attacksUntilSwitch;
    }

    int getNextAttackTick() {
        return this.nextAttackTick;
    }

    void setNextAttackTick(int nextAttackTick) {
        this.nextAttackTick = nextAttackTick;
    }

    int getLastTickAnimation() {
        return this.lastTickAnimation;
    }

    void setLastTickAnimation(int lastTickAnimation) {
        this.lastTickAnimation = lastTickAnimation;
    }

    WorldArea getLastWorldArea() {
        return this.lastWorldArea;
    }

    void setLastWorldArea(WorldArea lastWorldArea) {
        this.lastWorldArea = lastWorldArea;
    }

    boolean isInitiatedCombat() {
        return this.initiatedCombat;
    }

    void setInitiatedCombat(boolean initiatedCombat) {
        this.initiatedCombat = initiatedCombat;
    }

    Actor getLastTickInteracting() {
        return this.lastTickInteracting;
    }

    void setLastTickInteracting(Actor lastTickInteracting) {
        this.lastTickInteracting = lastTickInteracting;
    }

    boolean isTakenDamageRecently() {
        return this.takenDamageRecently;
    }

    void setTakenDamageRecently(boolean takenDamageRecently) {
        this.takenDamageRecently = takenDamageRecently;
    }

    int getRecentProjectileId() {
        return this.recentProjectileId;
    }

    void setRecentProjectileId(int recentProjectileId) {
        this.recentProjectileId = recentProjectileId;
    }

    boolean isChangedPrayerThisTick() {
        return this.changedPrayerThisTick;
    }

    void setChangedPrayerThisTick(boolean changedPrayerThisTick) {
        this.changedPrayerThisTick = changedPrayerThisTick;
    }

    boolean isChangedAttackStyleThisTick() {
        return this.changedAttackStyleThisTick;
    }

    void setChangedAttackStyleThisTick(boolean changedAttackStyleThisTick) {
        this.changedAttackStyleThisTick = changedAttackStyleThisTick;
    }

    boolean isChangedAttackStyleLastTick() {
        return this.changedAttackStyleLastTick;
    }

    void setChangedAttackStyleLastTick(boolean changedAttackStyleLastTick) {
        this.changedAttackStyleLastTick = changedAttackStyleLastTick;
    }

    HeadIcon getLastTickOverheadIcon() {
        return this.lastTickOverheadIcon;
    }

    void setLastTickOverheadIcon(HeadIcon lastTickOverheadIcon) {
        this.lastTickOverheadIcon = lastTickOverheadIcon;
    }

    int getDisabledMeleeMovementForTicks() {
        return this.disabledMeleeMovementForTicks;
    }

    void setDisabledMeleeMovementForTicks(int disabledMeleeMovementForTicks) {
        this.disabledMeleeMovementForTicks = disabledMeleeMovementForTicks;
    }

    DemonicGorilla(NPC npc) {
        this.npc = npc;
        this.nextPosibleAttackStyles = Arrays.asList(ALL_REGULAR_ATTACK_STYLES);
        this.nextAttackTick = -100;
        this.attacksUntilSwitch = 3;
        this.recentProjectileId = -1;
    }

    HeadIcon getOverheadIcon() {
        NPCComposition composition = this.npc.getComposition();
        if (composition != null)
            return composition.getOverheadIcon();
        return null;
    }
}
