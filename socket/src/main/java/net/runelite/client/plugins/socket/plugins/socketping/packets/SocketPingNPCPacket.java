package net.runelite.client.plugins.socket.plugins.socketping.packets;

import java.util.HashMap;
import net.runelite.client.plugins.socket.plugins.socketping.data.PacketKeys;
import net.runelite.client.plugins.socket.plugins.socketping.data.Ping;
import net.runelite.client.plugins.socket.plugins.socketping.data.PingNPC;

public class SocketPingNPCPacket extends SocketPingPacket {
    public SocketPingNPCPacket(PingNPC pingNPC) {
        super((Ping)pingNPC);
        HashMap<String, Integer> pingData = new HashMap<>();
        pingData.put(PacketKeys.NPC_ID.getKey(), pingNPC.getIndex());
        put(PacketKeys.PING_DATA.getKey(), pingData);
    }
}
