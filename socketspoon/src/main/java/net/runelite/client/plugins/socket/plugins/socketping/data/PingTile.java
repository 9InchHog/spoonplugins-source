package net.runelite.client.plugins.socket.plugins.socketping.data;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.plugins.socketping.PingTargetType;
import net.runelite.client.plugins.socket.plugins.socketping.PingType;

import java.util.Objects;

public final class PingTile extends Ping {
    private final WorldPoint worldPoint;

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PingTile))
            return false;
        PingTile other = (PingTile)o;
        if (!other.canEqual(this))
            return false;
        if (!super.equals(o))
            return false;
        Object this$worldPoint = getWorldPoint(), other$worldPoint = other.getWorldPoint();
        return Objects.equals(this$worldPoint, other$worldPoint);
    }

    public boolean canEqual(Object other) {
        return other instanceof PingTile;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        Object $worldPoint = getWorldPoint();
        return result * 59 + (($worldPoint == null) ? 43 : $worldPoint.hashCode());
    }

    public String toString() {
        return "PingTile(worldPoint=" + getWorldPoint() + ")";
    }

    public WorldPoint getWorldPoint() {
        return this.worldPoint;
    }

    public PingTile(WorldPoint worldPoint, PingType pingType, int world) {
        super(PingTargetType.TILE, pingType, world);
        this.worldPoint = worldPoint;
    }

    public static PingTile tryParse(JSONObject object) {
        if (!object.has(PacketKeys.PING_DATA.getKey()))
            return null;
        Ping ping = tryParsePing(object);
        if (ping == null)
            return null;
        Object res = object.get(PacketKeys.PING_DATA.getKey());
        if (!(res instanceof JSONObject))
            return null;
        JSONObject data = (JSONObject)res;
        int x = data.getInt(PacketKeys.REGION_X.getKey());
        int y = data.getInt(PacketKeys.REGION_Y.getKey());
        int z = data.getInt(PacketKeys.REGION_Z.getKey());
        int id = data.getInt(PacketKeys.REGION_ID.getKey());
        return new PingTile(WorldPoint.fromRegion(id, x, y, z), ping.getPingType(), ping.getWorld());
    }
}
