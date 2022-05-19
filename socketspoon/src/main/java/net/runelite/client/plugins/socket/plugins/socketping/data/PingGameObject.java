package net.runelite.client.plugins.socket.plugins.socketping.data;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.plugins.socketping.PingTargetType;
import net.runelite.client.plugins.socket.plugins.socketping.PingType;

import java.util.Objects;

public final class PingGameObject extends Ping {
    private final int id;

    private final WorldPoint worldPoint;

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PingGameObject))
            return false;
        PingGameObject other = (PingGameObject)o;
        if (!other.canEqual(this))
            return false;
        if (!super.equals(o))
            return false;
        if (getId() != other.getId())
            return false;
        Object this$worldPoint = getWorldPoint(), other$worldPoint = other.getWorldPoint();
        return Objects.equals(this$worldPoint, other$worldPoint);
    }

    public boolean canEqual(Object other) {
        return other instanceof PingGameObject;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        result = result * 59 + getId();
        Object $worldPoint = getWorldPoint();
        return result * 59 + (($worldPoint == null) ? 43 : $worldPoint.hashCode());
    }

    public String toString() {
        return "PingGameObject(id=" + getId() + ", worldPoint=" + getWorldPoint() + ")";
    }

    public int getId() {
        return this.id;
    }

    public WorldPoint getWorldPoint() {
        return this.worldPoint;
    }

    public PingGameObject(int id, WorldPoint worldPoint, PingType pingType, int world) {
        super(PingTargetType.GAMEOBJECT, pingType, world);
        this.id = id;
        this.worldPoint = worldPoint;
    }

    public static PingGameObject tryParse(JSONObject object) {
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
        int regionId = data.getInt(PacketKeys.REGION_ID.getKey());
        int id = data.getInt(PacketKeys.GAMEOBJECT_ID.getKey());
        return new PingGameObject(id, WorldPoint.fromRegion(regionId, x, y, z), ping.getPingType(), ping.getWorld());
    }
}
