package net.runelite.client.plugins.socket.plugins.socketworldhopper.ping;

import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import net.runelite.client.util.OSType;
import net.runelite.http.api.worlds.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Ping {
    private static final Logger log = LoggerFactory.getLogger(Ping.class);
    private static final byte[] RUNELITE_PING;
    private static final int TIMEOUT = 2000;
    private static final int PORT = 43594;
    private static short seq;

    public static int ping(World world) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(world.getAddress());
        } catch (UnknownHostException var5) {
            log.warn("error resolving host for world ping", var5);
            return -1;
        }

        try {
            switch(OSType.getOSType()) {
                case Windows:
                    return windowsPing(inetAddress);
                case Linux:
                    try {
                        return linuxPing(inetAddress);
                    } catch (Exception var3) {
                        return tcpPing(inetAddress);
                    }
                default:
                    return tcpPing(inetAddress);
            }
        } catch (IOException var4) {
            log.warn("error pinging", var4);
            return -1;
        }
    }

    private static int windowsPing(InetAddress inetAddress) {
        IPHlpAPI ipHlpAPI = IPHlpAPI.INSTANCE;
        Pointer ptr = ipHlpAPI.IcmpCreateFile();

        int var8;
        try {
            byte[] address = inetAddress.getAddress();
            Memory data = new Memory((long)RUNELITE_PING.length);
            data.write(0L, RUNELITE_PING, 0, RUNELITE_PING.length);
            IcmpEchoReply icmpEchoReply = new IcmpEchoReply(new Memory((long)IcmpEchoReply.SIZE + data.size()));

            assert icmpEchoReply.size() == IcmpEchoReply.SIZE;

            int packed = address[0] & 255 | (address[1] & 255) << 8 | (address[2] & 255) << 16 | (address[3] & 255) << 24;
            int ret = ipHlpAPI.IcmpSendEcho(ptr, packed, data, (short)((int)data.size()), Pointer.NULL, icmpEchoReply, IcmpEchoReply.SIZE + (int)data.size(), 2000);
            if (ret != 1) {
                byte var12 = -1;
                return var12;
            }

            var8 = Math.toIntExact(icmpEchoReply.roundTripTime.longValue());
        } finally {
            ipHlpAPI.IcmpCloseHandle(ptr);
        }

        return var8;
    }

    private static int linuxPing(InetAddress inetAddress) throws IOException {
        RLLibC libc = RLLibC.INSTANCE;
        byte[] address = inetAddress.getAddress();
        int sock = libc.socket(2, 2, 1);
        if (sock < 0) {
            throw new IOException("failed to open ICMP socket");
        } else {
            byte var15;
            try {
                Timeval tv = new Timeval();
                tv.tv_sec = 2L;
                if (libc.setsockopt(sock, 1, 20, tv.getPointer(), tv.size()) < 0) {
                    throw new IOException("failed to set SO_RCVTIMEO");
                }

                short var10000 = Ping.seq;
                Ping.seq = (short)(var10000 + 1);
                short seqno = var10000;
                byte[] request = new byte[]{8, 0, 0, 0, 0, 0, (byte)(seqno >> 8 & 255), (byte)(seqno & 255)};
                request = Bytes.concat(new byte[][]{request, RUNELITE_PING});
                byte[] addr = new byte[]{2, 0, 0, 0, address[0], address[1], address[2], address[3], 0, 0, 0, 0, 0, 0, 0, 0};
                long start = System.nanoTime();
                if (libc.sendto(sock, request, request.length, 0, addr, addr.length) != request.length) {
                    byte var19 = -1;
                    return var19;
                }

                int size = 8 + RUNELITE_PING.length;
                Memory response = new Memory((long)size);
                if (libc.recvfrom(sock, response, size, 0, (Pointer)null, (Pointer)null) != size) {
                    byte var20 = -1;
                    return var20;
                }

                long end = System.nanoTime();
                short seq = (short)((response.getByte(6L) & 255) << 8 | response.getByte(7L) & 255);
                if (seqno == seq) {
                    int var21 = (int)((end - start) / 1000000L);
                    return var21;
                }

                log.warn("sequence number mismatch ({} != {})", seqno, seq);
                var15 = -1;
            } finally {
                libc.close(sock);
            }

            return var15;
        }
    }

    private static int tcpPing(InetAddress inetAddress) throws IOException {
        Socket socket = new Socket();

        int var6;
        try {
            socket.setSoTimeout(2000);
            long start = System.nanoTime();
            socket.connect(new InetSocketAddress(inetAddress, 43594));
            long end = System.nanoTime();
            var6 = (int)((end - start) / 1000000L);
        } catch (Throwable var8) {
            try {
                socket.close();
            } catch (Throwable var7) {
                var8.addSuppressed(var7);
            }

            throw var8;
        }

        socket.close();
        return var6;
    }

    static {
        RUNELITE_PING = "RuneLitePing".getBytes(Charsets.UTF_8);
    }
}