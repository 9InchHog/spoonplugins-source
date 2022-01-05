package net.runelite.client.plugins.socket.plugins.socketworldhopper.ping;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.UCHAR;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.USHORT;
import java.util.Arrays;
import java.util.List;

public class IcmpEchoReply extends Structure {
    private static final int IP_OPTION_INFO_SIZE;
    public static final int SIZE;
    public ULONG address;
    public ULONG status;
    public ULONG roundTripTime;
    public USHORT dataSize;
    public USHORT reserved;
    public PVOID data;
    public UCHAR ttl;
    public UCHAR tos;
    public UCHAR flags;
    public UCHAR optionsSize;
    public PVOID optionsData;

    IcmpEchoReply(Pointer p) {
        super(p);
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("address", "status", "roundTripTime", "dataSize", "reserved", "data", "ttl", "tos", "flags", "optionsSize", "optionsData");
    }

    static {
        IP_OPTION_INFO_SIZE = 4 + (Native.POINTER_SIZE == 8 ? 12 : 4);
        SIZE = 16 + Native.POINTER_SIZE + IP_OPTION_INFO_SIZE;
    }
}