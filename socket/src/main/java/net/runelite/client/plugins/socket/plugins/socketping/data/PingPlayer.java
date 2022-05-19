package net.runelite.client.plugins.socket.plugins.socketping.data;

import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.plugins.socketping.PingTargetType;
import net.runelite.client.plugins.socket.plugins.socketping.PingType;

public final class PingPlayer extends Ping {
    private final int index;

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PingPlayer))
            return false;
        PingPlayer other = (PingPlayer)o;
        return other.canEqual(this) && (super.equals(o) && (getIndex() == other.getIndex()));
    }

    public boolean canEqual(Object other) {
        return other instanceof PingPlayer;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        return result * 59 + getIndex();
    }

    public String toString() {
        return "PingPlayer(index=" + getIndex() + ")";
    }

    public int getIndex() {
        return this.index;
    }

    public PingPlayer(int id, PingType pingType, int world) {
        super(PingTargetType.PLAYER, pingType, world);
        this.index = id;
    }

    public static PingPlayer tryParse(JSONObject object) {
        if (!object.has(PacketKeys.PING_DATA.getKey()))
            return null;
        Ping ping = tryParsePing(object);
        if (ping == null)
            return null;
        Object res = object.get(PacketKeys.PING_DATA.getKey());
        if (!(res instanceof JSONObject))
            return null;
        JSONObject data = (JSONObject)res;
        int id = data.getInt(PacketKeys.PLAYER_ID.getKey());
        return new PingPlayer(id, ping.getPingType(), ping.getWorld());
    }
}