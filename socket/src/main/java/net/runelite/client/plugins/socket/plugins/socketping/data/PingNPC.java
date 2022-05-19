package net.runelite.client.plugins.socket.plugins.socketping.data;

import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.plugins.socketping.PingTargetType;
import net.runelite.client.plugins.socket.plugins.socketping.PingType;

public final class PingNPC extends Ping {
    private final int index;

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PingNPC))
            return false;
        PingNPC other = (PingNPC)o;
        return other.canEqual(this) && (super.equals(o) && (getIndex() == other.getIndex()));
    }

    public boolean canEqual(Object other) {
        return other instanceof PingNPC;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        return result * 59 + getIndex();
    }

    public String toString() {
        return "PingNPC(index=" + getIndex() + ")";
    }

    public int getIndex() {
        return this.index;
    }

    public PingNPC(int index, PingType pingType, int world) {
        super(PingTargetType.NPC, pingType, world);
        this.index = index;
        this.pingType = pingType;
    }

    public static PingNPC tryParse(JSONObject object) {
        if (!object.has(PacketKeys.PING_DATA.getKey()))
            return null;
        Ping ping = tryParsePing(object);
        if (ping == null)
            return null;
        Object res = object.get(PacketKeys.PING_DATA.getKey());
        if (!(res instanceof JSONObject))
            return null;
        JSONObject data = (JSONObject)res;
        int id = data.getInt(PacketKeys.NPC_ID.getKey());
        return new PingNPC(id, ping.getPingType(), ping.getWorld());
    }
}