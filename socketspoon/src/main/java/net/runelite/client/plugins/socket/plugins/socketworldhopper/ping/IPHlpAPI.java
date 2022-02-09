package net.runelite.client.plugins.socket.plugins.socketworldhopper.ping;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

interface IPHlpAPI extends Library {
    IPHlpAPI INSTANCE = (IPHlpAPI)Native.loadLibrary("IPHlpAPI", IPHlpAPI.class);

    Pointer IcmpCreateFile();

    boolean IcmpCloseHandle(Pointer var1);

    int IcmpSendEcho(Pointer var1, int var2, Pointer var3, short var4, Pointer var5, IcmpEchoReply var6, int var7, int var8);
}