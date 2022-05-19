package net.runelite.client.plugins.socket.plugins.socketping.packets;

import java.util.HashMap;
import net.runelite.client.plugins.socket.plugins.socketping.data.PacketKeys;
import net.runelite.client.plugins.socket.plugins.socketping.data.Ping;
import net.runelite.client.plugins.socket.plugins.socketping.data.PingPlayer;

public class SocketPingPlayerPacket extends SocketPingPacket {
    public SocketPingPlayerPacket(PingPlayer pingPlayer) {
        super((Ping)pingPlayer);
        HashMap<String, Integer> pingData = new HashMap<>();
        pingData.put(PacketKeys.PLAYER_ID.getKey(), pingPlayer.getIndex());
        put(PacketKeys.PING_DATA.getKey(), pingData);
    }
}
