package net.runelite.client.plugins.spoongauntlet;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.model.Jarvis;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GauntletUtils {
    public static final int[] MELEE_ANIMATIONS = new int[]{395, 401, 400, 401, 386, 390, 422, 423, 401, 428, 440};
    public static final int[] RANGE_ANIMATIONS = new int[]{426};
    public static final int[] MAGE_ANIMATIONS = new int[]{1167};
    public static final int[] PLAYER_ANIMATIONS;
    public static final int[] CRYSTAL_DEPOSIT;
    public static final int[] PHREN_ROOTS;
    public static final int[] FISHING_SPOTS;
    public static final int[] GRYM_ROOTS;
    public static final int[] LINUM_TIRINUM;
    public static final int[] RESOURCE_IDS;
    public static final int[] PROJECTILE_MAGIC;
    public static final int[] PROJECTILE_RANGE;
    public static final int[] PROJECTILE_PRAYER;
    public static final int VARP_BOSS_ROOM = 9177;
    public static final int VARP_RAID_ROOM = 9178;
    public static final int BOSS_ANIMATION_LIGHTNING = 8418;
    public static final int[] TORNADO_IDS;
    public static final int TORNADO_TICKS = 20;

    public static int[] concatIntArray(int[]... arrays) {
        int length = 0;
        int[][] var2 = arrays;
        int currentIndex = arrays.length;

        for(int var4 = 0; var4 < currentIndex; ++var4) {
            int[] array = var2[var4];
            length += array.length;
        }

        int[] returnArray = new int[length];
        currentIndex = 0;
        int[][] var13 = arrays;
        int var14 = arrays.length;

        for(int var6 = 0; var6 < var14; ++var6) {
            int[] array = var13[var6];
            int[] var8 = array;
            int var9 = array.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                int value = var8[var10];
                returnArray[currentIndex] = value;
                ++currentIndex;
            }
        }

        return returnArray;
    }

    public static boolean arrayContainsInteger(int[] intArray, int value) {
        int[] var2 = intArray;
        int var3 = intArray.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int i = var2[var4];
            if (i == value) {
                return true;
            }
        }

        return false;
    }

    public static boolean isTornado(NPC npc) {
        return npc != null && arrayContainsInteger(TORNADO_IDS, npc.getId());
    }

    public static boolean isBoss(NPC npc) {
        return npc != null && npc.getName() != null && npc.getName().matches("(Crystalline|Corrupted) Hunllef");
    }

    public static boolean inRaid(Client client) {
        try {
            return client.getVarbitValue(client.getVarps(), 9178) == 1;
        } catch (NullPointerException | IndexOutOfBoundsException var2) {
            return false;
        }
    }

    public static boolean inBoss(Client client) {
        try {
            return client.getVarbitValue(client.getVarps(), 9177) == 1;
        } catch (NullPointerException | IndexOutOfBoundsException var2) {
            return false;
        }
    }

    public static Shape boundProjectile(Client client, Projectile p) {
        if (p != null && p.getModel() != null) {
            Model model = p.getModel();
            LocalPoint point = new LocalPoint((int)p.getX(), (int)p.getY());
            int tileHeight = Perspective.getTileHeight(client, point, client.getPlane());
            double angle = Math.atan(p.getVelocityY() / p.getVelocityX());
            angle = Math.toDegrees(angle) + (double)(p.getVelocityX() < 0.0D ? 180 : 0);
            angle = angle < 0.0D ? angle + 360.0D : angle;
            angle = 360.0D - angle - 90.0D;
            double ori = angle * 5.688888888888889D;
            ori = ori < 0.0D ? ori + 2048.0D : ori;
            int orientation = (int)Math.round(ori);
            List<Vertex> vertices = new ArrayList();
            System.out.println(model.getVerticesCount());
            System.out.println(model.getVerticesX().length);
            System.out.println(model.getVerticesY().length);
            System.out.println(model.getVerticesZ().length);
            int[] var11 = model.getVerticesX();
            int var12 = var11.length;

            for(int var13 = 0; var13 < var12; ++var13) {
                int var10000 = var11[var13];
            }

            for(int i = 0; i < vertices.size(); ++i) {
                vertices.set(i, vertices.get(i).rotate(orientation));
            }

            List<Point> list = new ArrayList();
            Iterator var18 = vertices.iterator();

            while(var18.hasNext()) {
                Vertex vertex = (Vertex)var18.next();
                Point localToCanvas = Perspective.localToCanvas(client, point.getX() - vertex.getX(), point.getY() - vertex.getZ(), tileHeight + vertex.getY() + (int)p.getZ());
                if (localToCanvas != null) {
                    list.add(localToCanvas);
                }
            }

            List<Point> convexHull = Jarvis.convexHull(list);
            if (convexHull == null) {
                return null;
            } else {
                Polygon polygon = new Polygon();
                Iterator var22 = convexHull.iterator();

                while(var22.hasNext()) {
                    Point hullPoint = (Point)var22.next();
                    polygon.addPoint(hullPoint.getX(), hullPoint.getY());
                }

                return polygon;
            }
        } else {
            return null;
        }
    }

    static {
        PLAYER_ANIMATIONS = concatIntArray(MELEE_ANIMATIONS, RANGE_ANIMATIONS, MAGE_ANIMATIONS);
        CRYSTAL_DEPOSIT = new int[]{36064, 35967};
        PHREN_ROOTS = new int[]{36066, 35969};
        FISHING_SPOTS = new int[]{36068, 35971};
        GRYM_ROOTS = new int[]{36070, 35973};
        LINUM_TIRINUM = new int[]{36072, 35975};
        RESOURCE_IDS = concatIntArray(CRYSTAL_DEPOSIT, PHREN_ROOTS, FISHING_SPOTS, GRYM_ROOTS, LINUM_TIRINUM);
        PROJECTILE_MAGIC = new int[]{1707, 1708};
        PROJECTILE_RANGE = new int[]{1711, 1712};
        PROJECTILE_PRAYER = new int[]{1713, 1714};
        TORNADO_IDS = new int[]{9025, 9039};
    }
}