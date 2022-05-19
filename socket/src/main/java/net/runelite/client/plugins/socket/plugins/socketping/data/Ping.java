package net.runelite.client.plugins.socket.plugins.socketping.data;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.plugins.socketping.PingTargetType;
import net.runelite.client.plugins.socket.plugins.socketping.PingType;

import java.util.Objects;

public class Ping {
    @Getter
    @Setter
    PingTargetType pingTargetType;

    @Getter
    @Setter
    PingType pingType;

    @Getter
    @Setter
    int world;

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Ping))
            return false;
        Ping other = (Ping)o;
        if (!other.canEqual(this))
            return false;
        Object this$pingTargetType = getPingTargetType(), other$pingTargetType = other.getPingTargetType();
        if (!Objects.equals(this$pingTargetType, other$pingTargetType))
            return false;
        Object this$pingType = getPingType(), other$pingType = other.getPingType();
        return (Objects.equals(this$pingType, other$pingType)) && (getWorld() == other.getWorld());
    }

    protected boolean canEqual(Object other) {
        return other instanceof Ping;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $pingTargetType = getPingTargetType();
        result = result * 59 + (($pingTargetType == null) ? 43 : $pingTargetType.hashCode());
        Object $pingType = getPingType();
        result = result * 59 + (($pingType == null) ? 43 : $pingType.hashCode());
        return result * 59 + getWorld();
    }

    public String toString() {
        return "Ping(pingTargetType=" + getPingTargetType() + ", pingType=" + getPingType() + ", world=" + getWorld() + ")";
    }

    public Ping(PingTargetType pingTargetType, PingType pingType, int world) {
        this.pingTargetType = pingTargetType;
        this.pingType = pingType;
        this.world = world;
    }

    public static Ping tryParsePing(JSONObject object) {
        if (!object.has(PacketKeys.PING_TARGET_TYPE.getKey()) ||
                !object.has(PacketKeys.PING_TYPE.getKey()) ||
                !object.has(PacketKeys.PING_WORLD.getKey()))
            return null;
        PingTargetType pingTargetType = tryParseType(object);
        PingType pingType = tryParsePingType(object);
        if (pingTargetType == null || pingType == null)
            return null;
        int world = object.getInt(PacketKeys.PING_WORLD.getKey());
        return new Ping(pingTargetType, pingType, world);
    }

    public static PingTargetType tryParseType(JSONObject object) {
        if (!object.has(PacketKeys.PING_TARGET_TYPE.getKey()))
            return null;
        String res = object.getString(PacketKeys.PING_TARGET_TYPE.getKey());
        return PingTargetType.mapping.get(res.toLowerCase());
    }

    public static PingType tryParsePingType(JSONObject object) {
        if (!object.has(PacketKeys.PING_TYPE.getKey()))
            return null;
        String res = object.getString(PacketKeys.PING_TYPE.getKey());
        return PingType.mapping.get(res.toLowerCase());
    }
}