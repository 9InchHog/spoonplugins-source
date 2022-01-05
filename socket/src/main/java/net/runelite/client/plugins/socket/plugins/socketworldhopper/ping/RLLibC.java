package net.runelite.client.plugins.socket.plugins.socketworldhopper.ping;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.LibC;

interface RLLibC extends LibC {
    RLLibC INSTANCE = (RLLibC)Native.loadLibrary("c", RLLibC.class);
    int AF_INET = 2;
    int SOCK_DGRAM = 2;
    int SOL_SOCKET = 1;
    int IPPROTO_ICMP = 1;
    int SO_RCVTIMEO = 20;

    int socket(int var1, int var2, int var3);

    int sendto(int var1, byte[] var2, int var3, int var4, byte[] var5, int var6);

    int recvfrom(int var1, Pointer var2, int var3, int var4, Pointer var5, Pointer var6);

    int setsockopt(int var1, int var2, int var3, Pointer var4, int var5);
}