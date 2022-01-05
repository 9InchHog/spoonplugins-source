package net.runelite.client.plugins.dnightmare;

public class MiscUtil {
    public static String to_mmss(int ticks) {
        int m = ticks / 100;
        int s = (ticks - m * 100) * 6 / 10;
        return m + ((s < 10) ? ":0" : ":") + s;
    }

    public static String to_mmss_precise(int ticks) {
        int min = ticks / 100;
        int tmp = (ticks - min * 100) * 6;
        int sec = tmp / 10;
        int sec_tenth = tmp - sec * 10;
        return min + ((sec < 10) ? ":0" : ":") +
                sec + "." + sec_tenth + "0";
    }
}
