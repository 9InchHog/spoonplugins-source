package net.runelite.client.plugins.spoonnightmare;

import net.runelite.api.Prayer;

public enum NightmareAttack {
    UNKNOWN(null, -1, -1),
    MAGIC_ATTACK(Prayer.PROTECT_FROM_MAGIC, 127, 17),
    RANGED_ATTACK(Prayer.PROTECT_FROM_MISSILES, 128, 18),
    MELEE_ATTACK(Prayer.PROTECT_FROM_MELEE, 129, 19),
    CURSED_MAGIC_ATTACK(Prayer.PROTECT_FROM_MELEE, 129, 19),
    CURSED_RANGED_ATTACK(Prayer.PROTECT_FROM_MAGIC, 127, 17),
    CURSED_MELEE_ATTACK(Prayer.PROTECT_FROM_MISSILES, 128, 18);

    private final Prayer prayer;

    private final int spriteId;

    private final int childId;

    public Prayer getPrayer() {
        return this.prayer;
    }

    public int getSpriteId() {
        return this.spriteId;
    }

    public int getChildId() {
        return this.childId;
    }

    NightmareAttack(Prayer prayer, int spriteId, int childId) {
        this.prayer = prayer;
        this.spriteId = spriteId;
        this.childId = childId;
    }
}
