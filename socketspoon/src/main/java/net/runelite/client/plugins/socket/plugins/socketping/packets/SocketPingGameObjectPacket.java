package net.runelite.client.plugins.socket.plugins.socketping.packets;

import java.util.HashMap;
import net.runelite.client.plugins.socket.plugins.socketping.data.PacketKeys;
import net.runelite.client.plugins.socket.plugins.socketping.data.Ping;
import net.runelite.client.plugins.socket.plugins.socketping.data.PingGameObject;

public class SocketPingGameObjectPacket extends SocketPingPacket {
    public SocketPingGameObjectPacket(PingGameObject pingGameObject) {
        super((Ping)pingGameObject);
        HashMap<String, Integer> pingData = new HashMap<>();
        pingData.put(PacketKeys.GAMEOBJECT_ID.getKey(), pingGameObject.getId());
        pingData.put(PacketKeys.REGION_X.getKey(), pingGameObject.getWorldPoint().getRegionX());
        pingData.put(PacketKeys.REGION_Y.getKey(), pingGameObject.getWorldPoint().getRegionY());
        pingData.put(PacketKeys.REGION_Z.getKey(), pingGameObject.getWorldPoint().getPlane());
        pingData.put(PacketKeys.REGION_ID.getKey(), pingGameObject.getWorldPoint().getRegionID());
        put(PacketKeys.PING_DATA.getKey(), pingData);
    }
}