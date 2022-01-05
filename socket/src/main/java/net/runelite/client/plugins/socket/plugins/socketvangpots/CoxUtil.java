package net.runelite.client.plugins.socket.plugins.socketvangpots;

public class CoxUtil {

    // p=plane, y=y_8, x=x_8, r=rot pp_xxxxxxxxxx_yyyyyyyyyy_rr0
    private static final int COX_ROOM_MASK = 0b11_1111111100_11111111100_00_0;

    private static final int FL_END1 = 0 << 24 | 102 << 16 | 160 << 5;

    private static final int FL_END2 = 0 << 24 | 102 << 16 | 161 << 5;
    private static final int FL_END3 = 0 << 24 | 103 << 16 | 161 << 5;

    private static final int LOBBY_CCW = 0 << 24 | 102 << 16 | 162 << 5;
    private static final int LOBBY_THRU = 0 << 24 | 103 << 16 | 162 << 5;
    private static final int LOBBY_CW = 0 << 24 | 104 << 16 | 162 << 5;

    private static final int SCAVS_SM_CCW = 0 << 24 | 102 << 16 | 163 << 5;
    private static final int SCAVS_SM_THRU = 0 << 24 | 103 << 16 | 163 << 5;
    private static final int SCAVS_SM_CW = 0 << 24 | 104 << 16 | 163 << 5;

    private static final int SHAMANS_CCW = 0 << 24 | 102 << 16 | 164 << 5;
    private static final int SHAMANS_THRU = 0 << 24 | 103 << 16 | 164 << 5;
    private static final int SHAMANS_CW = 0 << 24 | 104 << 16 | 164 << 5;

    private static final int VASA_CCW = 0 << 24 | 102 << 16 | 165 << 5;
    private static final int VASA_THRU = 0 << 24 | 103 << 16 | 165 << 5;
    private static final int VASA_CW = 0 << 24 | 104 << 16 | 165 << 5;

    private static final int VANGUARDS_CCW = 0 << 24 | 102 << 16 | 166 << 5;
    private static final int VANGUARDS_THRU = 0 << 24 | 103 << 16 | 166 << 5;
    private static final int VANGUARDS_CW = 0 << 24 | 104 << 16 | 166 << 5;

    private static final int ICE_DEMON_CCW = 0 << 24 | 102 << 16 | 167 << 5;
    private static final int ICE_DEMON_THRU = 0 << 24 | 103 << 16 | 167 << 5;
    private static final int ICE_DEMON_CW = 0 << 24 | 104 << 16 | 167 << 5;

    private static final int THIEVING_CCW = 0 << 24 | 102 << 16 | 168 << 5;
    private static final int THIEVING_THRU = 0 << 24 | 103 << 16 | 168 << 5;
    private static final int THIEVING_CW = 0 << 24 | 104 << 16 | 168 << 5;

    private static final int FARM_FISH_CCW = 0 << 24 | 102 << 16 | 170 << 5;
    private static final int FARM_FISH_THRU = 0 << 24 | 103 << 16 | 170 << 5;
    private static final int FARM_FISH_CW = 0 << 24 | 104 << 16 | 170 << 5;

    private static final int FL_START1_CCW = 0 << 24 | 102 << 16 | 178 << 5;
    private static final int FL_START1_THRU = 0 << 24 | 103 << 16 | 178 << 5;
    private static final int FL_START1_CW = 0 << 24 | 104 << 16 | 178 << 5;

    private static final int FL_START2_CCW = 0 << 24 | 102 << 16 | 179 << 5;
    private static final int FL_START2_THRU = 0 << 24 | 103 << 16 | 179 << 5;
    private static final int FL_START2_CW = 0 << 24 | 104 << 16 | 179 << 5;

    private static final int SCAVS_LG_CCW = 1 << 24 | 102 << 16 | 163 << 5;
    private static final int SCAVS_LG_THRU = 1 << 24 | 103 << 16 | 163 << 5;
    private static final int SCAVS_LG_CW = 1 << 24 | 104 << 16 | 163 << 5;

    private static final int MYSTICS_CCW = 1 << 24 | 102 << 16 | 164 << 5;
    private static final int MYSTICS_THRU = 1 << 24 | 103 << 16 | 164 << 5;
    private static final int MYSTICS_CW = 1 << 24 | 104 << 16 | 164 << 5;

    private static final int TEKTON_CCW = 1 << 24 | 102 << 16 | 165 << 5;
    private static final int TEKTON_THRU = 1 << 24 | 103 << 16 | 165 << 5;
    private static final int TEKTON_CW = 1 << 24 | 104 << 16 | 165 << 5;

    private static final int MUTTADILES_CCW = 1 << 24 | 102 << 16 | 166 << 5;
    private static final int MUTTADILES_THRU = 1 << 24 | 103 << 16 | 166 << 5;
    private static final int MUTTADILES_CW = 1 << 24 | 104 << 16 | 166 << 5;

    private static final int TIGHTROPE_CCW = 1 << 24 | 102 << 16 | 167 << 5;
    private static final int TIGHTROPE_THRU = 1 << 24 | 103 << 16 | 167 << 5;
    private static final int TIGHTROPE_CW = 1 << 24 | 104 << 16 | 167 << 5;

    private static final int FARM_BATS_CCW = 1 << 24 | 102 << 16 | 170 << 5;
    private static final int FARM_BATS_THRU = 1 << 24 | 103 << 16 | 170 << 5;
    private static final int FARM_BATS_CW = 1 << 24 | 104 << 16 | 170 << 5;

    private static final int GUARDIANS_CCW = 2 << 24 | 102 << 16 | 164 << 5;
    private static final int GUARDIANS_THRU = 2 << 24 | 103 << 16 | 164 << 5;
    private static final int GUARDIANS_CW = 2 << 24 | 104 << 16 | 164 << 5;

    private static final int VESPULA_CCW = 2 << 24 | 102 << 16 | 165 << 5;
    private static final int VESPULA_THRU = 2 << 24 | 103 << 16 | 165 << 5;
    private static final int VESPULA_CW = 2 << 24 | 104 << 16 | 165 << 5;

    private static final int CRABS_CCW = 2 << 24 | 102 << 16 | 167 << 5;
    private static final int CRABS_THRU = 2 << 24 | 103 << 16 | 167 << 5;
    private static final int CRABS_CW = 2 << 24 | 104 << 16 | 167 << 5;

    private static final int OLM_ROOM_MASK = 0b11_1111111000_11111111000_00_0;

    private static final int OLM_ = 0 << 24 | 50 << 17 | 89 << 6;

    public static final int FL_START = 0;
    public static final int FL_END = 1;
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
    public static final int OLM = 16;

    public static final int UNKNOWN = 17;

    private static final char[] ROOM_SORTS = new char[] { '*', '*', 'S', 'F', 'C',
            'C', 'C', 'C', 'C', 'C', 'C', 'C', 'P', 'P', 'P', 'P', 'O' };

    private static final String[] ROOM_NAMES = new String[] { "Floor start",
            "Floor end", "Scavengers", "Farming", "Shamans", "Vasa", "Vanguards",
            "Mystics", "Tekton", "Muttadiles", "Guardians", "Vespula", "Ice demon",
            "Thieving", "Tightrope", "Crabs", "Olm" };

    public static int getroom_type(int zonecode) {
        switch (zonecode & COX_ROOM_MASK) {
            case LOBBY_CCW:
            case LOBBY_THRU:
            case LOBBY_CW:
            case FL_START1_CCW:
            case FL_START1_THRU:
            case FL_START1_CW:
            case FL_START2_CCW:
            case FL_START2_THRU:
            case FL_START2_CW:
                return FL_START;
            case FL_END1:
            case FL_END2:
            case FL_END3:
                return FL_END;
            case SCAVS_SM_CCW:
            case SCAVS_SM_THRU:
            case SCAVS_SM_CW:
            case SCAVS_LG_CCW:
            case SCAVS_LG_THRU:
            case SCAVS_LG_CW:
                return SCAVENGERS;
            case FARM_FISH_CCW:
            case FARM_FISH_THRU:
            case FARM_FISH_CW:
            case FARM_BATS_CCW:
            case FARM_BATS_THRU:
            case FARM_BATS_CW:
                return FARMING;
            case SHAMANS_CCW:
            case SHAMANS_THRU:
            case SHAMANS_CW:
                return SHAMANS;
            case VASA_CCW:
            case VASA_THRU:
            case VASA_CW:
                return VASA;
            case VANGUARDS_CCW:
            case VANGUARDS_THRU:
            case VANGUARDS_CW:
                return VANGUARDS;
            case MYSTICS_CCW:
            case MYSTICS_THRU:
            case MYSTICS_CW:
                return MYSTICS;
            case TEKTON_CCW:
            case TEKTON_THRU:
            case TEKTON_CW:
                return TEKTON;
            case MUTTADILES_CCW:
            case MUTTADILES_THRU:
            case MUTTADILES_CW:
                return MUTTADILES;
            case GUARDIANS_CCW:
            case GUARDIANS_THRU:
            case GUARDIANS_CW:
                return GUARDIANS;
            case VESPULA_CCW:
            case VESPULA_THRU:
            case VESPULA_CW:
                return VESPULA;
            case ICE_DEMON_CCW:
            case ICE_DEMON_THRU:
            case ICE_DEMON_CW:
                return ICE_DEMON;
            case THIEVING_CCW:
            case THIEVING_THRU:
            case THIEVING_CW:
                return THIEVING;
            case TIGHTROPE_CCW:
            case TIGHTROPE_THRU:
            case TIGHTROPE_CW:
                return TIGHTROPE;
            case CRABS_CCW:
            case CRABS_THRU:
            case CRABS_CW:
                return CRABS;
        }

        if ((zonecode & OLM_ROOM_MASK) == OLM_)
            return OLM;

        return UNKNOWN;
    }

    public static char getroom_sort(int roomtype) {
        if (roomtype >= 0 && roomtype < UNKNOWN)
            return ROOM_SORTS[roomtype];

        return '?';
    }

    public static String getroom_name(int roomtype) {
        if (roomtype >= 0 && roomtype < UNKNOWN)
            return ROOM_NAMES[roomtype];

        return "Unknown";
    }

    public static int room_winding(int zonecode) {
        return (((zonecode >> 16) & 0xff) - 103) & 0x3;
    }

    public static int room_rot(int zonecode) {
        return zonecode >> 1 & 0x3;
    }

    public static int room_exitside(int zonecode) {
        return (room_winding(zonecode) + room_rot(zonecode)) & 0x3;
    }

}
