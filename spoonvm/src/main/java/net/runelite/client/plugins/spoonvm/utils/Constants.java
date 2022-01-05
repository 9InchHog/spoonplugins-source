package net.runelite.client.plugins.spoonvm.utils;

import java.time.Duration;

public class Constants {
    private static final String CHAT_VM_START = "The volcano awakens! You can now access the area below...";
    private static final String PLATFORM_WARNING_MESSAGE = "The platform beneath you will disappear soon!";
    private static final String BOULDER_WARNING_MESSAGE = "The current boulder stage is complete.";
    private static final int PLATFORM_STAGE_1_ID = 30998;
    private static final int PLATFORM_STAGE_2_ID = 30999;
    private static final int PLATFORM_STAGE_3_ID = 31000;
    private static final int BOULDER_BREAK_STAGE_1_ID = 7807;
    private static final int BOULDER_BREAK_STAGE_2_ID = 7809;
    private static final int BOULDER_BREAK_STAGE_3_ID = 7811;
    private static final int BOULDER_BREAK_STAGE_4_ID = 7813;
    private static final int BOULDER_BREAK_STAGE_5_ID = 7815;
    private static final int ROCK_EMPTY = 31046;
    private static final int ROCK_ACTIVE = 31045;
    private static final int EAST_GAS = 31050;
    private static final int EAST_GAS2 = 31051;
    private static final int VM_REGION_NORTH = 15263;
    private static final int VM_REGION_SOUTH = 15262;
    private static final Duration VM_FULL_TIME = Duration.ofMinutes(10);
    private static final Duration VM_HALF_TIME = Duration.ofMinutes(5);
    private static final int VARBIT_STABILITY = 5938;
    private static final int VARBIT_GAME_STATE = 5941;
    private static final int PROC_VOLCANIC_MINE_SET_OTHERINFO = 2022;

    private static final int HUD_COMPONENT = 611;
    private static final int HUD_STABILITY_COMPONENT = 13;

    private static final int GAME_STATE_IN_LOBBY = 1;
    private static final int GAME_STATE_IN_GAME = 2;

    private static final int STARTING_STABILITY = 50;

    private static final int STABILITY_CHANGE_HISTORY_SIZE = 2;

    //WidgetID Volcanic Mine
    static final int GENERAL_INFOBOX_GROUP_ID = 4;
    static final int TIME_LEFT = 8;
    static final int POINTS = 10;
    static final int STABILITY = 12;
    static final int PLAYER_COUNT = 14;
    static final int VENTS_INFOBOX_GROUP_ID = 15;
    static final int VENT_A_PERCENTAGE = 19;
    static final int VENT_B_PERCENTAGE = 20;
    static final int VENT_C_PERCENTAGE = 21;
    static final int VENT_A_STATUS = 23;
    static final int VENT_B_STATUS = 24;
    static final int VENT_C_STATUS = 25;
}
