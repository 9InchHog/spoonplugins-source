package net.runelite.client.plugins.socket.plugins.socketping.packets;

import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.plugins.socketping.data.PacketKeys;
import net.runelite.client.plugins.socket.plugins.socketping.data.Ping;

public abstract class SocketPingPacket extends JSONObject {
    protected SocketPingPacket(Ping ping) {
        put(PacketKeys.PING_TARGET_TYPE.getKey(), ping.getPingTargetType().getType());
        put(PacketKeys.PING_TYPE.getKey(), ping.getPingType().getType());
        put(PacketKeys.PING_WORLD.getKey(), ping.getWorld());
    }
}