package net.runelite.client.plugins.socket.plugins.socketping.packets;

import java.util.HashMap;
import net.runelite.client.plugins.socket.plugins.socketping.data.PacketKeys;
import net.runelite.client.plugins.socket.plugins.socketping.data.Ping;
import net.runelite.client.plugins.socket.plugins.socketping.data.PingTile;

public class SocketPingTilePacket extends SocketPingPacket {
    public int hashCode() {
        return super.hashCode();
    }

    protected boolean canEqual(Object other) {
        return other instanceof SocketPingTilePacket;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SocketPingTilePacket))
            return false;
        SocketPingTilePacket other = (SocketPingTilePacket)o;
        return other.canEqual(this) && (super.equals(o));
    }

    public SocketPingTilePacket(PingTile pingTile) {
        super((Ping)pingTile);
        HashMap<String, Integer> pingData = new HashMap<>();
        pingData.put(PacketKeys.REGION_X.getKey(), pingTile.getWorldPoint().getRegionX());
        pingData.put(PacketKeys.REGION_Y.getKey(), pingTile.getWorldPoint().getRegionY());
        pingData.put(PacketKeys.REGION_Z.getKey(), pingTile.getWorldPoint().getPlane());
        pingData.put(PacketKeys.REGION_ID.getKey(), pingTile.getWorldPoint().getRegionID());
        put(PacketKeys.PING_DATA.getKey(), pingData);
    }
}
