package net.runelite.client.plugins.spoonvorkath;

import net.runelite.api.NPC;

public class VorkathUtils {
    public static final int REGION = 9023;

    public static final int VORKATH_WAKE_UP = 7950;

    public static final int VORKATH_DEATH = 7949;

    public static final int VORKATH_SLASH_ATTACK = 7951;

    public static final int VORKATH_ATTACK = 7952;

    public static final int VORKATH_FIRE_BOMB = 7960;

    public static final int VORKATH_ACID_ATTACK = 7957;

    public static final int VORKATH_POISON_POOL_AOE = 1483;

    public static final int VORKATH_TICK_FIRE_AOE = 1482;

    public static final int VORKATH_ICE = 395;

    public enum VorkathPhase {
        ZOMBIFIED_SPAWN, ACID, UNKNOWN;
    }

    public static boolean foundVorkath(NPC npc) {
        return (npc != null && npc.getName() != null && npc.getName().matches("Vorkath"));
    }

    public static boolean foundZombifiedSpawn(NPC npc) {
        return (npc != null && npc.getName() != null && npc.getName().matches("Zombified Spawn"));
    }
}
