package net.runelite.client.plugins.grotesqueguardians;

import net.runelite.api.NPC;
import org.apache.commons.lang3.ArrayUtils;

public class GGUtil {
    public static int REGION_ID = 6727;

    public static final int YELLOW_LIGHTNING = 1416;

    public static final int PURPLE_LIGHTNING = 1424;

    public static final int FALLING_ROCKS = 1436;

    public static final int STONE_ORB = 160;

    public static final int RANGED_PROJECTILE = 1444;

    public static final int STONE_RANGED_PROJECTILE = 1445;

    public static final int HEALING_ORB_PROJECTILE = 1437;

    public static final int[] DUSK_ATTACK_ANIMATIONS = new int[] { 7785, 7786, 7788, 7800, 7801 };

    public static final int[] DAWN_ATTACK_ANIMATIONS = new int[] { 7770, 7771, 7769 };

    public static final int DUSK_GLOWING_WING = 7802;

    public static final int DAWN_P3_END = 7776;

    public static final int DUSK_P4_END = 7803;

    public static boolean foundDawn(NPC npc) {
        return (npc != null && npc.getName() != null && npc.getName().matches("Dawn"));
    }

    public static boolean foundDusk(NPC npc) {
        return (npc != null && npc.getName() != null && npc.getName().matches("Dusk"));
    }

    public static boolean contains(int[] array, int value) {
        return ArrayUtils.contains(array, value);
    }
}
