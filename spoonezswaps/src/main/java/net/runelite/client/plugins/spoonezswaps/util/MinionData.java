package net.runelite.client.plugins.spoonezswaps.util;

import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

import java.awt.*;

public class MinionData {
    NPC npc;
    String name;
    public int tickDied;
    public int respawnTicks;
    public WorldPoint respawnLocation;
    public int npcIdx;
    public int size;
    MinionType minionType;

    enum MinionType { MELEE, RANGED, MAGIC; }

    public static boolean isMinion(NPC npc) {
        int npcID = npc.getId();
        return (npcID == 3132 || npcID == 3131 || npcID == 3130 || npcID == 3163 || npcID == 3164 || npcID == 3165 || npcID == 2217 || npcID == 2218 || npcID == 2216 || npcID == 2207 || npcID == 2208 || npcID == 2206);
    }

    public static boolean isBoss(NPC npc) {
        int npcID = npc.getId();
        return (npcID == 3129 || npcID == 2215 || npcID == 2205 || npcID == 3162);
    }

    public static MinionType getMinionType(NPC npc) {
        switch (npc.getId()) {
            case 2207:
            case 2217:
            case 3132:
            case 3163:
                return MinionType.MAGIC;
            case 2208:
            case 2218:
            case 3131:
            case 3164:
                return MinionType.RANGED;
            case 2206:
            case 2216:
            case 3130:
            case 3165:
                return MinionType.MELEE;
        }
        return MinionType.MELEE;
    }

    WorldPoint getMinionRespawnLocation(NPC npc) {
        int regionID = npc.getWorldLocation().getRegionID();
        switch (npc.getId()) {
            case 3163:
                return WorldPoint.fromRegion(regionID, 24, 55, 2);
            case 3164:
                return WorldPoint.fromRegion(regionID, 12, 51, 2);
            case 3165:
                return WorldPoint.fromRegion(regionID, 17, 49, 2);

            case 3132:
                return WorldPoint.fromRegion(regionID, 41, 7, 2);
            case 3131:
                return WorldPoint.fromRegion(regionID, 39, 15, 2);
            case 3130:
                return WorldPoint.fromRegion(regionID, 52, 16, 2);

            case 2207:
                return WorldPoint.fromRegion(regionID, 16, 16, 0);
            case 2208:
                return WorldPoint.fromRegion(regionID, 22, 26, 0);
            case 2206:
                return WorldPoint.fromRegion(regionID, 23, 13, 0);

            case 2217:
                return WorldPoint.fromRegion(regionID, 56, 40, 2);
            case 2218:
                return WorldPoint.fromRegion(regionID, 52, 50, 2);
            case 2216:
                return WorldPoint.fromRegion(regionID, 50, 46, 2);
        }

        return WorldPoint.fromRegion(regionID, 0, 0, 2);
    }

    public Color getColor() {
        switch (this.name) {
            case "Wingman Skree":
            case "Balfrug Kreeyath":
            case "Sergeant Steelwill":
            case "Growler":
                return Color.blue;
            case "Flockleader Geerin":
            case "Zakl'n Gritch":
            case "Sergeant Grimspike":
            case "Bree":
                return Color.green;
            case "Flight Kilisa":
            case "Tstanon Karlak":
            case "Sergeant Strongstack":
            case "Starlight":
                return Color.red;
        }
        return Color.white;
    }

    public MinionData(NPC _npc) {
        this.npc = _npc;
        this.name = this.npc.getName();
        WorldPoint wp = this.npc.getWorldLocation();
        this.respawnLocation = getMinionRespawnLocation(this.npc);

        switch (this.name) {
            case "Wingman Skree":
            case "Balfrug Kreeyath":
            case "Sergeant Steelwill":
            case "Growler":
                this.respawnTicks = 53;
                break;
            case "Flockleader Geerin":
            case "Zakl'n Gritch":
            case "Sergeant Grimspike":
            case "Bree":
                this.respawnTicks = 53;
                break;
            case "Flight Kilisa":
            case "Tstanon Karlak":
            case "Sergeant Strongstack":
            case "Starlight":
                this.respawnTicks = 53;
                break;
            default:
                this.respawnTicks = 10;
                break;
        }

        System.out.println("Respawn ticks : " + this.respawnTicks + " : " + this.name);

        this.npcIdx = this.npc.getIndex();
        this.tickDied = -1;
        this.minionType = getMinionType(this.npc);
        if (this.npc.getComposition() != null) {

            this.size = this.npc.getComposition().getSize();
        } else {
            this.size = 1;
        }
    }
}
