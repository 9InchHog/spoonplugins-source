package net.runelite.client.plugins.socket.plugins.socketworldhopper.ping;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

public class Timeval extends Structure {
    public long tv_sec;
    public long tv_usec;

    protected List<String> getFieldOrder() {
        return Arrays.asList("tv_sec", "tv_usec");
    }
}