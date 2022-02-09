package net.runelite.client.plugins.socket.plugins.socketicedemon.util;

public class Raids1Util {
    public static final int RAIDS1_ROOM_MASK = 67059680;

    public static final int FL_END1 = 6689792;

    public static final int FL_END2 = 6689824;

    public static final int FL_END3 = 6755360;

    public static final int LOBBY_CCW = 6689856;

    public static final int LOBBY_THRU = 6755392;

    public static final int LOBBY_CW = 6820928;

    public static final int SCAVS1_CCW = 6689888;

    public static final int SCAVS1_THRU = 6755424;

    public static final int SCAVS1_CW = 6820960;

    public static final int SHAMANS_CCW = 6689920;

    public static final int SHAMANS_THRU = 6755456;

    public static final int SHAMANS_CW = 6820992;

    public static final int VASA_CCW = 6689952;

    public static final int VASA_THRU = 6755488;

    public static final int VASA_CW = 6821024;

    public static final int VANGUARDS_CCW = 6689984;

    public static final int VANGUARDS_THRU = 6755520;

    public static final int VANGUARDS_CW = 6821056;

    public static final int ICE_DEMON_CCW = 6690016;

    public static final int ICE_DEMON_THRU = 6755552;

    public static final int ICE_DEMON_CW = 6821088;

    public static final int THIEVING_CCW = 6690048;

    public static final int THIEVING_THRU = 6755584;

    public static final int THIEVING_CW = 6821120;

    public static final int FARMING1_CCW = 6690112;

    public static final int FARMING1_THRU = 6755648;

    public static final int FARMING1_CW = 6821184;

    public static final int FL_START1_CCW = 6690368;

    public static final int FL_START1_THRU = 6755904;

    public static final int FL_START1_CW = 6821440;

    public static final int FL_START2_CCW = 6690400;

    public static final int FL_START2_THRU = 6755936;

    public static final int FL_START2_CW = 6821472;

    public static final int SCAVS2_CCW = 23467104;

    public static final int SCAVS2_THRU = 23532640;

    public static final int SCAVS2_CW = 23598176;

    public static final int MYSTICS_CCW = 23467136;

    public static final int MYSTICS_THRU = 23532672;

    public static final int MYSTICS_CW = 23598208;

    public static final int TEKTON_CCW = 23467168;

    public static final int TEKTON_THRU = 23532704;

    public static final int TEKTON_CW = 23598240;

    public static final int MUTTADILES_CCW = 23467200;

    public static final int MUTTADILES_THRU = 23532736;

    public static final int MUTTADILES_CW = 23598272;

    public static final int TIGHTROPE_CCW = 23467232;

    public static final int TIGHTROPE_THRU = 23532768;

    public static final int TIGHTROPE_CW = 23598304;

    public static final int FARMING2_CCW = 23467328;

    public static final int FARMING2_THRU = 23532864;

    public static final int FARMING2_CW = 23598400;

    public static final int GUARDIANS_CCW = 40244352;

    public static final int GUARDIANS_THRU = 40309888;

    public static final int GUARDIANS_CW = 40375424;

    public static final int VESPULA_CCW = 40244384;

    public static final int VESPULA_THRU = 40309920;

    public static final int VESPULA_CW = 40375456;

    public static final int CRABS_CCW = 40244448;

    public static final int CRABS_THRU = 40309984;

    public static final int CRABS_CW = 40375520;

    public static final int FLOOR_START = 0;

    public static final int FLOOR_END = 1;

    public static final int SCAVENGERS = 2;

    public static final int FARMING = 3;

    public static final int SHAMANS = 4;

    public static final int VASA = 5;

    public static final int VANGUARDS = 6;

    public static final int MYSTICS = 7;

    public static final int TEKTON = 8;

    public static final int MUTTADILES = 9;

    public static final int GUARDIANS = 10;

    public static final int VESPULA = 11;

    public static final int ICE_DEMON = 12;

    public static final int THIEVING = 13;

    public static final int TIGHTROPE = 14;

    public static final int CRABS = 15;

    public static final int UNKNOWN = 16;

    private static final char[] SORTS = new char[] {
            '*',
            '*',
            'S',
            'F',
            'C',
            'C',
            'C',
            'C',
            'C',
            'C',
            'C',
            'C',
            'P',
            'P',
            'P',
            'P' };

    private static final String[] NAMES = new String[] {
            "Floor start",
            "Floor end",
            "Scavengers",
            "Farming",
            "Shamans",
            "Vasa",
            "Vanguards",
            "Mystics",
            "Tekton",
            "Muttadiles",
            "Guardians",
            "Vespula",
            "Ice demon",
            "Thieving",
            "Tightrope",
            "Crabs" };

    public static int getroom_type(int zonecode) {
        switch (zonecode & 0x3FF3FE0) {
            case 6689856:
            case 6690368:
            case 6690400:
            case 6755392:
            case 6755904:
            case 6755936:
            case 6820928:
            case 6821440:
            case 6821472:
                return 0;
            case 6689792:
            case 6689824:
            case 6755360:
                return 1;
            case 6689888:
            case 6755424:
            case 6820960:
            case 23467104:
            case 23532640:
            case 23598176:
                return 2;
            case 6690112:
            case 6755648:
            case 6821184:
            case 23467328:
            case 23532864:
            case 23598400:
                return 3;
            case 6689920:
            case 6755456:
            case 6820992:
                return 4;
            case 6689952:
            case 6755488:
            case 6821024:
                return 5;
            case 6689984:
            case 6755520:
            case 6821056:
                return 6;
            case 23467136:
            case 23532672:
            case 23598208:
                return 7;
            case 23467168:
            case 23532704:
            case 23598240:
                return 8;
            case 23467200:
            case 23532736:
            case 23598272:
                return 9;
            case 40244352:
            case 40309888:
            case 40375424:
                return 10;
            case 40244384:
            case 40309920:
            case 40375456:
                return 11;
            case 6690016:
            case 6755552:
            case 6821088:
                return 12;
            case 6690048:
            case 6755584:
            case 6821120:
                return 13;
            case 23467232:
            case 23532768:
            case 23598304:
                return 14;
            case 40244448:
            case 40309984:
            case 40375520:
                return 15;
        }
        return 16;
    }

    public static char getroom_sort(int roomtype) {
        if (roomtype >= 0 && roomtype < 16)
            return SORTS[roomtype];
        return '?';
    }

    public static String getroom_name(int roomtype) {
        if (roomtype >= 0 && roomtype < 16)
            return NAMES[roomtype];
        return "Unknown";
    }

    public static int getroom_winding(int zonecode) {
        return (zonecode >> 16 & 0xFF) - 103 & 0x3;
    }

    public static int getroom_rot(int zonecode) {
        return zonecode >> 1 & 0x3;
    }

    public static int getroom_exitside(int zonecode) {
        return getroom_winding(zonecode) + getroom_rot(zonecode) & 0x3;
    }
}
