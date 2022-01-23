package net.runelite.client.plugins.spoontob;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("spoontob")
public interface SpoonTobConfig extends Config {
    @ConfigSection(
            name = "Maiden",
            description = "Maiden settings",
            position = 1,
            closedByDefault = true
    )
    String maiden = "maiden";
    @ConfigSection(
            name = "Bloat",
            description = "Bloat settings",
            position = 2,
            closedByDefault = true
    )
    String bloat = "bloat";
    @ConfigSection(
            name = "Nylocas",
            description = "Nylocas settings",
            position = 3,
            closedByDefault = true
    )
    String nylocas = "nylocas";
    @ConfigSection(
            name = "Sotetseg",
            description = "Sotetseg settings",
            position = 4,
            closedByDefault = true
    )
    String sotetseg = "sotetseg";
    @ConfigSection(
            name = "Xarpus",
            description = "Xarpus settings",
            position = 5,
            closedByDefault = true
    )
    String xarpus = "xarpus";
    @ConfigSection(
            name = "Verzik",
            description = "Verzik settings",
            position = 6,
            closedByDefault = true
    )
    String verzik = "verzik";
    @ConfigSection(
            name = "Misc",
            description = "Misc settings",
            position = 7,
            closedByDefault = true
    )
    String misc = "misc";

    //------------------------------------------------------------//
    // Maiden
    //------------------------------------------------------------//
    @ConfigItem(
            position = 0,
            keyName = "MaidenTickCounter",
            name = "Show Maiden Tick Counter",
            description = "Show a Tick timer on the boss indicating time until next attack",
            section = maiden
    )
    default boolean maidenTickCounter() {return false;}

    @ConfigItem(
            position = 1,
            keyName = "maidenBlood",
            name = "Show Maiden Blood Toss",
            description = "Displays the tile location where tossed blood will land.",
            section = maiden
    )
    default maidenBloodSplatMode maidenBlood() {
        return maidenBloodSplatMode.COLOR;
    }

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "bloodTossColour",
            name = "Blood Toss Color",
            description = "Colors the tile where blood will land",
            section = maiden
    )
    default Color bloodTossColour() {
        return new Color(0, 255, 255, 150);
    }

    @Range(min = 0, max = 255)
    @ConfigItem(
            position = 3,
            keyName = "bloodTossFill",
            name = "Blood Toss Opacity",
            description = "Changes the opacity of the blood toss highlight",
            section = maiden
    )
    default int bloodTossFill() {
        return 10;
    }

    @ConfigItem(
            position = 4,
            keyName = "bloodTossTicks",
            name = "Blood Toss Ticks",
            description = "Show the ticks until the blood splat lands",
            section = maiden
    )
    default boolean bloodTossTicks() {
        return false;
    }

    @ConfigItem(
            position = 5,
            keyName = "maidenSpawns",
            name = "Show Blood Spawns True Tile",
            description = "Show the tiles that blood spawns will travel to.",
            section = maiden
    )
    default boolean maidenSpawns() {
        return false;
    }

    @ConfigItem(
            position = 6,
            keyName = "maidenSpawnsTrail",
            name = "Show Blood Spawns Trailing Tile",
            description = "Shows the trailing tile of the blood spawns location.",
            section = maiden,
            hidden = true,
            unhide = "maidenSpawns"
    )
    default boolean maidenSpawnsTrail() {
        return false;
    }

    @ConfigItem(
            position = 7,
            keyName = "bloodSpawnsColor",
            name = "Blood Spawns Color",
            description = "Color of the tiles that blood spawns will travel to.",
            section = maiden,
            hidden = true,
            unhide = "maidenSpawns"
    )
    default Color bloodSpawnsColor() {
        return new Color(0, 150, 200);
    }

    @ConfigItem(
            position = 8,
            keyName = "MaidenRecolourNylos",
            name = "Recolor HP Nylo Menu",
            description = "Recolor nylos in right click menu based on their HP and adds the HP % next to the name.",
            section = maiden
    )
    default boolean maidenRecolourNylos() {
        return false;
    }

    @ConfigItem(
            position = 9,
            keyName = "maidenFreezeTimer",
            name = "Nylo Freeze Timers",
            description = "Displays how long each Nylocas Matomenos is frozen for",
            section = maiden
    )
    default maidenFreezeTimerMode maidenFreezeTimer() { return maidenFreezeTimerMode.OFF; }

    @ConfigItem(
            position = 10,
            keyName = "showMaidenCrabsDistance",
            name = "Show Crabs Distance",
            description = "You really need me to explain this?",
            section = maiden
    )
    default boolean showMaidenCrabsDistance() { return false; }

    @ConfigItem(
            position = 11,
            keyName = "singleLineDistance",
            name = "Single Line Crabs Distance",
            description = "Makes the crabs distance and hp % a single line",
            section = maiden,
            hidden = true,
            unhide = "showMaidenCrabsDistance"
    )
    default boolean singleLineDistance() { return false; }

    @ConfigItem(
            position = 12,
            keyName = "showFrozenDistance",
            name = "Show Distance When Frozen",
            description = "Shows the distance overlay on the crabs when they are frozen",
            section = maiden,
            hidden = true,
            unhide = "showMaidenCrabsDistance"
    )
    default boolean showFrozenDistance() { return false; }

    @ConfigItem(
            position = 13,
            keyName = "distanceColor",
            name = "Crabs Distance Color",
            description = "You really need me to explain this?",
            section = maiden,
            hidden = true,
            unhide = "showMaidenCrabsDistance"
    )
    default Color distanceColor() { return Color.WHITE; }

    @ConfigItem(
            position = 14,
            keyName = "showMaidenCrabHp",
            name = "Show Crabs HP",
            description = "Crab HP show do",
            section = maiden
    )
    default boolean showMaidenCrabHp() { return false; }

    @ConfigItem(
            position = 15,
            keyName = "removeMaidenBloods",
            name = "Remove Blood Spawns",
            description = "Yup... cast, attack, both",
            section = maiden
    )
    default maidenBloodsMode removeMaidenBloods() { return maidenBloodsMode.BOTH; }

    @ConfigItem(
            position = 16,
            keyName = "leakedMessage",
            name = "Show Leaked Message",
            description = "For all those shit freezers out there",
            section = maiden
    )
    default boolean leakedMessage() { return false; }

    @ConfigItem(
            position = 17,
            keyName = "bloodSpawnFreezeTimer",
            name = "Blood Spawn Freeze Timer",
            description = "y freeze?",
            section = maiden
    )
    default boolean bloodSpawnFreezeTimer() { return false; }

    @ConfigItem(
            position = 18,
            keyName = "maidenScuffedCrab",
            name = "Scuffed Crab Outline",
            description = "Spawn right you little shit",
            section = maiden
    )
    default boolean maidenScuffedCrab() { return false; }

    @ConfigItem(
            position = 19,
            keyName = "maidenScuffedCrabColor",
            name = "Scuffed Crab Color",
            description = "Sets the color for Scuffed Crab Outline",
            section = maiden,
            hidden = true,
            unhide = "maidenScuffedCrab"
    )
    default Color maidenScuffedCrabColor() {
        return Color.WHITE;
    }

    @ConfigItem(
            position = 20,
            keyName = "maidenCrabHpPriority",
            name = "Prioritize Highest Hp Crab",
            description = "Swaps menu entries so the highest HP crab is left click",
            section = maiden
    )
    default boolean maidenCrabHpPriority() {return false;}

    @ConfigItem(
            position = 21,
            keyName = "maidenProcThreshold",
            name = "Maiden Proc Threshold",
            description = "Shows a rough estimate on the damage needed to proc next phase",
            section = maiden
    )
    default boolean maidenProcThreshold()
    {
        return true;
    }

    @ConfigItem(
            position = 22,
            name = "Maiden Max Hit (Tooltip)",
            keyName = "maidenMaxHit",
            description = "When hovering over Maiden's clickbox it will display her max hits for:<br>No Prayer<br>Prayer<br>Elysian Spirit Shield",
            section = maiden
    )
    default boolean maidenMaxHit() {
        return false;
    }

    @ConfigItem(
            position = 23,
            name = "Maiden Max Hit (Overlay)",
            keyName = "maidenMaxHitOverlay",
            description = "Overlay that will display her max hits for:<br>No Prayer<br>Prayer<br>Elysian Spirit Shield",
            section = maiden
    )
    default MaidenMaxHit maidenMaxHitOverlay() {
        return MaidenMaxHit.OFF;
    }

    @ConfigItem(
            position = 24,
            name = "Reds Freeze Warning",
            keyName = "redsFreezeWarning",
            description = "Highlights the N3 and S3 crabs when the north mager cannot freeze them in time <br> Must be on Ancient spellbook and be 4 or 5 man scale",
            section = maiden
    )
    default boolean redsFreezeWarning() {
        return false;
    }

    @ConfigItem(
            position = 25,
            name = "Reds Freeze Warning Color",
            keyName = "redsFreezeWarningColor",
            description = "Color of the N3 crabs when the north mager cannot freeze them in time",
            section = maiden,
            hidden = true,
            unhide = "redsFreezeWarning"
    )
    default Color redsFreezeWarningColor() {
        return Color.RED;
    }

    //------------------------------------------------------------//
    // Bloat
    //------------------------------------------------------------//
    @ConfigItem(
            position = 1,
            keyName = "bloatIndicator",
            name = "Bloat Status",
            description = "Display Bloat's status (asleep, awake, enrage) using color codes.",
            section = bloat
    )
    default BloatIndicatorMode bloatIndicator() {
        return BloatIndicatorMode.TILE;
    }

    @ConfigItem(
            position = 2,
            keyName = "showBloatHands",
            name = "Show Bloat Hands",
            description = "Highlights the falling hands inside Bloat.",
            section = bloat
    )
    default bloatHandsMode showBloatHands() {
        return bloatHandsMode.COLOR;
    }

    @ConfigItem(
            position = 3,
            keyName = "bloatHandsTicks",
            name = "Bloat Hands Ticks",
            description = "Shows the ticks till the hands hit the ground",
            section = bloat
    )
    default boolean bloatHandsTicks() {
        return false;
    }

    @Alpha
    @ConfigItem(
            position = 4,
            keyName = "bloatColor",
            name = "Hands Color",
            description = "Bloat Hands Color",
            section = bloat
    )
    default Color bloatHandColor() {
        return new Color(106, 61, 255, 255);
    }

    @Range(min = 0, max = 255)
    @ConfigItem(
            position = 5,
            keyName = "bloatColorFill",
            name = "Hands Color Opacity",
            description = "Changes the opacity of the bloat hands highlight",
            section = bloat
    )
    default int bloatColorFill() {
        return 10;
    }

    @ConfigItem(
            position = 6,
            keyName = "bloatUpTimer",
            name = "Bloat Timer",
            description = "Show the estimated time when Bloat will stop moving.",
            section = bloat
    )
    default boolean bloatUpTimer() {
        return false;
    }

    @ConfigItem(
            position = 7,
            keyName = "bloatEntryTimer",
            name = "Bloat Entry Timer",
            description = "Shows the ticks since entering the Bloat region. Disappears once you start Bloat",
            section = bloat
    )
    default boolean bloatEntryTimer() {
        return false;
    }

    @ConfigItem(
            position = 8,
            keyName = "hideAnnoyingAssObjects",
            name = "Hide Objects",
            description = "Hides annoying objects in the bloat room",
            section = bloat
    )
    default annoyingObjectHideMode hideAnnoyingAssObjects() {
        return annoyingObjectHideMode.CHAINS;
    }

    @ConfigItem(
            position = 9,
            keyName = "bloatStompMode",
            name = "Stomp Safespots",
            description = "Shows lines for where you should go to flinch bloat stomps",
            section = bloat
    )
    default bloatStompMode bloatStompMode() {
        return bloatStompMode.COLOR;
    }

    @ConfigItem(
            position = 10,
            keyName = "bloatStompColor",
            name = "Stomp Color",
            description = "Color of the stomp lines",
            section = bloat
    )
    @Alpha
    default Color bloatStompColor() {
        return new Color(0, 255, 0, 100);
    }

    @ConfigItem(
            position = 11,
            keyName = "bloatStompWidth",
            name = "Stomp Width",
            description = "Girth",
            section = bloat
    )
    @Range(max = 3, min = 1)
    default int bloatStompWidth() {
        return 1;
    }
	
	@ConfigItem(
            position = 12,
            keyName = "bloatReverseNotifier",
            name = "Bloat Turn",
            description = "Plays a sound to let you know when bloat changes direction",
            section = bloat
    )
    default bloatTurnMode bloatReverseNotifier() {
        return bloatTurnMode.OFF;
    }

    @Range(max = 100)
    @ConfigItem(
            position = 13,
            keyName = "reverseVolume",
            name = "Turn Volume",
            description = "Cha cha real smooth",
            section = bloat
    )
    default int reverseVolume() {
        return 50;
    }

    //------------------------------------------------------------//
    // Nylo
    //------------------------------------------------------------//
    @ConfigItem(
            position = 0,
            keyName = "wheelchairWaves",
            name = "Wheelchair Nylos",
            description = "Removes attack options on wrong style nylos when weapons are equipped",
            section = nylocas
    )
    default wheelchairMode wheelchairNylo() {
        return wheelchairMode.BOTH;
    }

    @ConfigItem(
            position = 1,
            keyName = "ignoreChins",
            name = "Wheelchair - Ignore Chins",
            description = "Ignores wheelchair settings if you equip chins (aka lets you attack the wrong styles with chins)",
            section = nylocas
    )
    default boolean ignoreChins() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "showPhaseChange",
            name = "Show Boss Phase Change",
            description = "Shows how long until the boss changes phases. Both includes demiboss",
            section = nylocas
    )
    default nyloBossPhaseChange showPhaseChange() {
        return nyloBossPhaseChange.BOSS;
    }

    @ConfigItem(
            position = 3,
            keyName = "nyloPillars",
            name = "Show Nylocas Pillar Health",
            description = "Show the health bars of the Nylocas pillars.",
            section = nylocas
    )
    default boolean nyloPillars() {
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = "showLowestPillar",
            name = "Show Lowest Pillar Health",
            description = "Puts a hint arrow on the Nylocas pillar with the lowest health.",
            section = nylocas
    )
    default boolean showLowestPillar() {
        return true;
    }

    @Range(max = 52)
    @ConfigItem(
            position = 5,
            keyName = "nyloExplosionDisplayTicks",
            name = "Display Last Ticks",
            description = "Displays the last 'x' amount of ticks for a Nylocas. (ex: to see the last 10 ticks, you set it to 10).",
            section = nylocas
    )
    default int nyloExplosionDisplayTicks()
    {
        return 52;
    }

    @ConfigItem(
            position = 6,
            keyName = "nyloTimeAliveCountStyle",
            name = "Nylocas Tick Time Alive Style",
            description = "Count up or Count down options on the tick time alive.",
            section = nylocas
    )
    default nylotimealive nyloTimeAliveCountStyle() { return nylotimealive.COUNTUP;}

    @ConfigItem(
            position = 7,
            keyName = "showNylocasExplosions",
            name = "Explosion Warning",
            description = "Displays ticks until explosion, a yellow tile, or both.",
            section = nylocas
    )
    default ExplosionWarning showNylocasExplosions() {
        return ExplosionWarning.OFF;
    }

    @ConfigItem(
            position = 8,
            keyName = "nyloExplosionType",
            name = "Explosion Mode",
            description = "Display nylo explosion as either tile or explosion radius",
            section = nylocas
    )
    default nyloExplosionType nyloExplosionType() {
        return nyloExplosionType.TILE;
    }

    @ConfigItem(
            position = 9,
            keyName = "nyloRecolorMenu",
            name = "Nylocas Recolor Menu",
            description = "Recolors the right click menu to the color of the nylos. Bigs are darker.",
            section = nylocas
    )
    default boolean nyloRecolorMenu() {
        return false;
    }

    @ConfigItem(
            position = 10,
            keyName = "nyloTicksMenu",
            name = "Time Alive Right Click Menu",
            description = "Displays how many ticks the Nylos have left/been alive for in the right click menu <br> Must have Nylocas Recolour Menu on",
            section = nylocas,
            hidden = true,
            unhide = "nyloRecolorMenu"
    )
    default boolean nyloTicksMenu() { return false; }

    @ConfigItem(
            position = 11,
            keyName = "nyloOverlay",
            name = "Nylocas Role Overlay",
            description = "Display the interactive overlay allowing you to choose which nylocas to highlight",
            section = nylocas
    )
    default boolean nyloOverlay() {
        return false;
    }

    @Range(min = 0, max = 3)
    @ConfigItem(
            position = 12,
            keyName = "nyloTileWidth",
            name = "Nylocas Tile Width",
            description = "girth",
            section = nylocas,
            hidden = true,
            unhide = "nyloOverlay"
    )
    default double nyloTileWidth()
    {
        return 1;
    }

    @ConfigItem(
            position = 13,
            keyName = "nyloAliveCounter",
            name = "Nylocas Alive Display",
            description = "Show how many nylocas are alive in the room.",
            section = nylocas
    )
    default boolean nyloAlivePanel() {
        return false;
    }

    @ConfigItem(
            position = 14,
            keyName = "nyloAggressiveOverlay",
            name = "Highlight Aggressive Nylocas",
            description = "Highlight nylocas that are aggressive.",
            section = nylocas
    )
    default boolean nyloAggressiveOverlay() {
        return false;
    }

    @ConfigItem(
            position = 15,
            keyName = "nyloStallMessage",
            name = "Nylo Stall Chat Message",
            description = "Display a message in chatbox when a wave stalls.",
            section = nylocas
    )
    default boolean nyloStallMessage() {
        return false;
    }

    @ConfigItem(
            position = 16,
            keyName = "showBigSplits",
            name = "Show Big Splits",
            description = "Marks where a big nylo died and how long until littles spawn",
            section = nylocas
    )
    default boolean showBigSplits() {
        return true;
    }

    @ConfigItem(
            position = 17,
            keyName = "bigsColor",
            name = "Big Splits Color",
            description = "Big Splits Color",
            section = nylocas,
            hidden = true,
            unhide = "showBigSplits"
    )
    default Color bigsColor() {
        return Color.GRAY;
    }

    @ConfigItem(
            position = 18,
            keyName = "nyloSplitsMsg",
            name = "Nylo Splits Message",
            description = "Shows how many of each boss phase and/or how many small splits you got",
            section = nylocas
    )
    default nyloSplitsMessage nyloSplitsMsg() {
        return nyloSplitsMessage.BOSS;
    }

    @ConfigItem(
            position = 19,
            keyName = "splitMsgTiming",
            name = "Waves Message Timing",
            description = "Shows when to display how many small nylos you got from splits",
            section = nylocas
    )
    default splitsMsgTiming splitMsgTiming() {
        return splitsMsgTiming.FINISHED;
    }

    @ConfigItem(
            position = 20,
            keyName = "smallSplitsType",
            name = "Small Splits Type",
            description = "Caps = Pre  + Post cap splits, Total is just the total splits throughout the waves, both you don't need an explanation",
            section = nylocas
    )
    default smallSplitsMode smallSplitsType() {
        return smallSplitsMode.TOTAL;
    }

    @ConfigItem(
            position = 21,
            keyName = "hidePillars",
            name = "Hide Pillars",
            description = "Removes the pillars in Nylo and the walls as well if set to clean",
            section = nylocas
    )
    default hidePillarsMode hidePillars() {
        return hidePillarsMode.OFF;
    }

    @ConfigItem(
            position = 22,
            keyName = "hideEggs",
            name = "Hide Eggs",
            description = "You're an idiot. Nobody's allergic to eggs",
            section = nylocas
    )
    default boolean hideEggs() {
        return false;
    }

    @ConfigItem(
            position = 23,
            keyName = "waveSpawnTimer",
            name = "Wave Spawn Timer",
            description = "Timer for when them niglets finna pull up",
            section = nylocas
    )
    default waveSpawnTimerMode waveSpawnTimer() {
        return waveSpawnTimerMode.OFF;
    }

    @ConfigItem(
            position = 24,
            keyName = "waveSpawnTimerColor",
            name = "Wave Timer Color",
            description = "Sets color of Wave Spawn Timer overlay",
            section = nylocas
    )
    default Color waveSpawnTimerColor() {
        return Color.WHITE;
    }

    //------------------------------------------------------------//
    // Sote
    //------------------------------------------------------------//
    @ConfigItem(
            position = 1,
            keyName = "sotetsegMaze1",
            name = "Sotetseg Maze",
            description = "Display tiles indicating the correct path of the sotetseg maze.",
            section = sotetseg
    )
    default boolean sotetsegMaze() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "showSotetsegAttackTicks",
            name = "Show Sotetseg Attack Ticks",
            description = "Ticks until Sotetseg attacks again.",
            section = sotetseg
    )
    default boolean showSotetsegAttackTicks() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "SotetsegAttacks",
            name = "Show Sotetseg Orb",
            description = "Highlight the attacks that Sotetseg throws at you.",
            section = sotetseg
    )
    default soteOrbMode sotetsegShowOrbs() {
        return soteOrbMode.OFF;
    }

    @Range(min = 1, max = 50)
    @ConfigItem(
            position = 4,
            keyName = "soteHatSize",
            name = "Sotetseg Orb Hat Size",
            description = "Changes the size of the hats",
            section = sotetseg
    )
    default int soteHatSize() {
        return 16;
    }

    @ConfigItem(
            position = 5,
            keyName = "SotetsegAttacks1",
            name = "Show Sotetseg Nuke",
            description = "Highlight the big AoE dragonball-z deathball mfkin thing.",
            section = sotetseg
    )
    default soteDeathballOverlayMode sotetsegShowNuke() {
        return soteDeathballOverlayMode.TICKS;
    }

    @ConfigItem(
            position = 6,
            keyName = "SotetsegAttacksSounds",
            name = "Sotetseg Nuke Sound",
            description = "Ear rape.",
            section = sotetseg
    )
    default boolean sotetsegAttacksSound() {
        return false;
    }

    @Range(max = 100)
    @ConfigItem(
            position = 7,
            keyName = "SotetsegAttacksSoundsVolume",
            name = "Nuke Volume",
            description = "Set this to 100 or you're a pussy.",
            section = sotetseg,
            hidden = true,
            unhide = "SotetsegAttacksSounds"
    )
    default int sotetsegAttacksSoundVolume() {
        return 80;
    }

    @ConfigItem(
            position = 8,
            keyName = "displayDeathBall",
            name = "Show Death Ball Target",
            description = "Shows who has the death ball",
            section = sotetseg
    )
    default boolean displayDeathBall() {
        return false;
    }

    @ConfigItem(
            position = 9,
            keyName = "displayDeathBallColor",
            name = "Death Ball Target Color",
            description = "Sets color of the death ball target tile",
            section = sotetseg,
            hidden = true,
            unhide = "displayDeathBall"
    )
    default Color displayDeathBallColor()
    {
        return new Color(188, 74, 74);
    }

    @ConfigItem(
            position = 10,
            keyName = "deathballInfobox",
            name = "Attacks Until Death Ball",
            description = "Shows an infobox with the attacks left until death ball",
            section = sotetseg
    )
    default soteDeathballMode deathballInfobox() {
        return soteDeathballMode.OFF;
    }

    @ConfigItem(
            position = 11,
            keyName = "deathballSingleLine",
            name = "Single Line Text",
            description = "Makes the attacks until deathball and ticks until attack a single line",
            section = sotetseg
    )
    default boolean deathballSingleLine() {
        return false;
    }

    //------------------------------------------------------------//
    // Xarpus
    //------------------------------------------------------------//
    @ConfigItem(
            position = 1,
            keyName = "xarpusExhumed",
            name = "Xarpus Exhumed",
            description = "Highlight the exhumed tiles that spawn on the ground.",
            section = xarpus
    )
    default exhumedMode xarpusExhumed() {
        return exhumedMode.BOTH;
    }

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "exhumedStepOffWarning",
            name = "Exhumed Step Off Warning",
            description = "Changes the color of exhumed ticks and/or highlights when they are not active.",
            section = xarpus
    )
    default stepOffMode exhumedStepOffWarning() {
        return stepOffMode.OFF;
    }

    @ConfigItem(
            position = 3,
            keyName = "xarpusExhumedInfo",
            name = "Show Xarpus Exhumed Panel",
            description = "Show a small info panel indicating how many exhumes remaining and total healed.",
            section = xarpus
    )
    default boolean xarpusExhumedInfo() {
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = "exhumedOnXarpus",
            name = "Show Exhumed Count on Xarpus",
            description = "Displays the number of exhumeds left on Xarpus",
            section = xarpus
    )
    default boolean exhumedOnXarpus() {
        return false;
    }

    @ConfigItem(
            position = 5,
            keyName = "exhumedIB",
            name = "Show Exhumed Count Infobox",
            description = "Displays the number of exhumeds left in an infobox",
            section = xarpus
    )
    default boolean exhumedIB() {
        return false;
    }

    @ConfigItem(
            position = 6,
            keyName = "xarpusHealingCount",
            name = "Show Healing Count Infobox",
            description = "Displays the healing done from exhumeds",
            section = xarpus
    )
    default boolean xarpusHealingCount()
    {
        return true;
    }

    @ConfigItem(
            position = 7,
            keyName = "xarpusTicks",
            name = "Xarpus Ticks",
            description = "Count down the ticks until xarpus attacks next",
            section = xarpus
    )
    default boolean xarpusTicks() {
        return false;
    }

    @ConfigItem(
            position = 8,
            keyName = "xarpusLos",
            name = "Xarpus Line of Sight",
            description = "No attack here",
            section = xarpus
    )
    default losMode xarpusLos() {
        return losMode.OFF;
    }

    @Alpha
    @ConfigItem(
            position = 9,
            keyName = "xarpusLosColor",
            name = "Line of Sight Color",
            description = "What sorta fuckin description u need u moron",
            section = xarpus
    )
    default Color xarpusLosColor() {
        return new Color(255, 0, 0, 255);
    }

    @Range(min = 0, max = 255)
    @ConfigItem(
            position = 10,
            keyName = "xarpusLosFill",
            name = "Line of Sight Opacity",
            description = "Changes the opacity of the Xarpus Line of Sight highlight",
            section = xarpus
    )
    default int xarpusLosFill() {
        return 20;
    }

    @ConfigItem(
            position = 11,
            keyName = "muteXarpusHmEarrape",
            name = "Mute HM Earrape",
            description = "Fuck that noise",
            section = xarpus
    )
    default boolean muteXarpusHmEarrape() {
        return false;
    }

    @ConfigItem(
            position = 12,
            keyName = "sheesh",
            name = "Sheeeesh",
            description = "Why not?",
            section = xarpus
    )
    default boolean sheesh() {
        return false;
    }

	@Range(max = 100)
    @ConfigItem(
            position = 13,
            keyName = "sheeshVolume",
            name = "Sheesh Volume",
            description = "Muted hard mode earrape.... then I added this",
            section = xarpus,
            hidden = true,
            unhide = "sheesh"
    )
    default int sheeshVolume() {
        return 50;
    }

    //------------------------------------------------------------//
    // Verzik
    //------------------------------------------------------------//
    @ConfigItem(
            position = 1,
            keyName = "showVerzikTicks",
            name = "Show Verzik Ticks",
            description = "Count down the ticks until Verzik attacks.",
            section = verzik
    )
    default boolean showVerzikTicks() {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "showVerzikAttacks",
            name = "Show Attack Counter",
            description = "Counts how many attacks Verzik has done",
            section = verzik
    )
    default verzikAttacksMode showVerzikAttacks() {
        return verzikAttacksMode.REDS;
    }

    @ConfigItem(
            position = 3,
            keyName = "showVerzikTotalTicks",
            name = "Show Total Tick Counter",
            description = "Display the total tick counter on Verzik.",
            section = verzik
    )
    default boolean showVerzikTotalTicks() {
        return true;
    }

    @ConfigItem(
            position = 4,
            keyName = "VerzikMeleeLocation",
            name = "P3 True Location",
            description = "Draws a true location tile around verzik during p3 <br> Purple until tornadoes spawn. Orange after tornadoes spawn. Tank changes color if YOU are the tank.",
            section = verzik
    )
    default meleeTileMode verzikMelee() {
        return meleeTileMode.NORMAL;
    }

    @Alpha
    @ConfigItem(
            position = 5,
            keyName = "verzikMeleeColor",
            name = "P3 Tile Color",
            description = "Sets color of P3 True Location",
            section = verzik
    )
    default Color verzikMeleeColor() {
        return new Color(106, 61, 255);
    }

	@Alpha
    @ConfigItem(
            position = 6,
            keyName = "p3AggroColor",
            name = "P3 Tile Aggro Color",
            description = "Changes the color the tile will be if you are chosen as the tank <br> Must have 'Tank Notifier' selected",
            section = verzik
    )
    default Color p3AggroColor() {
        return Color.RED;
    }
	
	@ConfigItem(
            position = 7,
            keyName = "verzikTankTarget",
            name = "Verzik Tank Target",
            description = "Highlight the tile of the player who is tanking. Color set by aggro color",
            section = verzik
    )
    default boolean verzikTankTarget() {
        return false;
    }

    @ConfigItem(
            position = 8,
            keyName = "VerzikNyloAggro",
            name = "Show Crab Targets",
            description = "Show a text overlay on crab spawns that are aggressive to you.",
            section = verzik
    )
    default boolean verzikNyloAggroWarning() {
        return true;
    }

    @ConfigItem(
            position = 9,
            keyName = "VerzikNyloExplode",
            name = "Crab Tile",
            description = "Show crab explosion range with a tile indicator.",
            section = verzik
    )
    default VerzikNyloSetting verzikNyloExplodeRange() {
        return VerzikNyloSetting.MY_CRABS;
    }

    @ConfigItem(
            position = 10,
            keyName = "VerzikNyloExplodeColour",
            name = "Crab Tile Color",
            description = "Color of the tile for the exploding range.",
            section = verzik
    )
    default Color verzikNyloExplodeTileColor() {
        return Color.RED;
    }

    @ConfigItem(
            position = 11,
            keyName = "redsHp",
            name = "Show Red Crab Hp",
            description = "Shows the hp % of red crabs during P2 verzik",
            section = verzik
    )
    default boolean redsHp() {
        return false;
    }

    @ConfigItem(
            position = 12,
            keyName = "showVerzikYellows",
            name = "Show Yellows Tick",
            description = "<u>Count down the ticks until Verzik yellow's damage tick.</u>"
                    + "<br> Thank you to Caps Lock13 for contributing to the 'groups' option",
            section = verzik
    )
    default verzikYellowsMode showVerzikYellows() {
        return verzikYellowsMode.OFF;
    }

    @ConfigItem(
            position = 13,
            keyName = "yellowTicksOnPlayer",
            name = "Yellows Ticks on Player",
            description = "Displays the yellows ticks on the local player instead of on the yellows",
            section = verzik
    )
    default boolean yellowTicksOnPlayer() {
        return false;
    }

    @ConfigItem(
            position = 14,
            keyName = "hideAttackYellows",
            name = "Hide Attack Yellows",
            description = "Hides attack option on Verzik during yellows",
            section = verzik
    )
    default boolean hideAttackYellows() {
        return false;
    }

    @ConfigItem(
            position = 15,
            keyName = "purpleAoe",
            name = "Show Purple AoE",
            description = "Where the purple is gonna land",
            section = verzik
    )
    default boolean purpleAoe() { return false; }

    @ConfigItem(
            position = 16,
            keyName = "hidePurple",
            name = "Hide Attack Purple",
            description = "Removes clickbox on purple crab spawn at Verzik when not wearing poison weapons/serps",
            section = verzik
    )
    default boolean hidePurple() {
        return false;
    }

    @ConfigItem(
            position = 17,
            keyName = "displayGreenBall",
            name = "Show Green Ball",
            description = "Highlights whoever the green ball is on",
            section = verzik
    )
    default greenBallMode displayGreenBall() { return greenBallMode.OFF; }

    @ConfigItem(
            position = 18,
            keyName = "displayGreenBallTicks",
            name = "Show Green Ball Ticks",
            description = "Shows ticks on the person who has green ball on them",
            section = verzik
    )
    default boolean displayGreenBallTicks() { return false; }

    @ConfigItem(
            position = 19,
            keyName = "greenBouncePanel",
            name = "Green Bounce/Dmg Counter",
            description = "Infobox to display how many times you have bounced the green ball",
            section = verzik
    )
    default greenBouncePanelMode greenBouncePanel() { return greenBouncePanelMode.OFF; }

    @ConfigItem(
            position = 20,
            keyName = "showVerzikNados",
            name = "Show Tornadoes",
            description = "Highlights all or only your personal tornado",
            section = verzik
    )
    default nadoMode showVerzikNados() {
        return nadoMode.OFF;
    }
	
	@ConfigItem(
            position = 21,
            keyName = "showVerzikNadoStyle",
            name = "Tornado Style",
            description = "Sets the type of highlight for Show Tornadoes",
            section = verzik
    )
    default nadoStyle showVerzikNadoStyle() {
        return nadoStyle.TRUE_LOCATION;
    }

    @Alpha
    @ConfigItem(
            position = 22,
            keyName = "showVerzikNadoColor",
            name = "Verzik Nado Color",
            description = "Color for the tornadoes",
            section = verzik
    )
    default Color showVerzikNadoColor() { return Color.RED; }

    @Alpha
    @ConfigItem(
            position = 23,
            keyName = "verzikNadoOpacity",
            name = "Verzik Nado Opacity",
            description = "opacity for the tornadoes",
            section = verzik
    )
    default int verzikNadoOpacity() { return 0; }

    @ConfigItem(
            position = 24,
            keyName = "hideOtherNados",
            name = "Hide Other Tornadoes",
            description = "Hides any tornado not following you",
            section = verzik
    )
    default boolean hideOtherNados() { return false; }

    @ConfigItem(
            position = 25,
            keyName = "showVerzikRangeAttacks",
            name = "Show Verzik Range Attacks",
            description = "Shows the tile in which a ranged attack on P2 will land.",
            section = verzik
    )
    default boolean showVerzikRangeAttack() { return false; }

    @Alpha
    @ConfigItem(
            position = 26,
            keyName = "verzikRangeAttacksColor",
            name = "Verzik Range Attacks Color",
            description = "Color for the garlic balls",
            section = verzik,
            hidden = true,
            unhide = "showVerzikRangeAttacks"
    )
    default Color verzikRangeAttacksColor() { return new Color(106, 61, 255, 255); }

    @Range(min = 0, max = 255)
    @ConfigItem(
            position = 27,
            keyName = "verzikRangeAttacksFill",
            name = "Verzik Range Attacks Opacity",
            description = "Changes the opacity of the Xarpus Line of Sight highlight",
            section = verzik,
            hidden = true,
            unhide = "showVerzikRangeAttacks"
    )
    default int verzikRangeAttacksFill() {
        return 20;
    }
	
	@ConfigItem(
            position = 28,
            keyName = "showVerzikRocks",
            name = "Show HM Verzik Rocks",
            description = "Shows the tile the rocks will land on in P1 of Hard mode",
            section = verzik
    )
    default boolean showVerzikRocks() { return false; }

    @Alpha
    @ConfigItem(
            position = 29,
            keyName = "showVerzikRocksColor",
            name = "Verzik Rock Color",
            description = "Color for the rocks in P1",
            section = verzik,
            hidden = true,
            unhide = "showVerzikRocks"
    )
    default Color showVerzikRocksColor() { return new Color(106, 61, 255); }

    @ConfigItem(
            position = 30,
            keyName = "showVerzikAcid",
            name = "Show HM Acid",
            description = "Shows the tile the acid from hard mode Verzik range attacks is on",
            section = verzik
    )
    default boolean showVerzikAcid() { return false; }

    @Range(min = 1)
    @ConfigItem(
            position = 31,
            keyName = "showVerzikAcidDistance",
            name = "Acid Render Distance",
            description = "Only highlights acid within a certain distance",
            section = verzik,
            hidden = true,
            unhide = "showVerzikAcid"
    )
    default int showVerzikAcidDistance() { return 5; }

    @Alpha
    @ConfigItem(
            position = 32,
            keyName = "showVerzikAcidColor",
            name = "Verzik Acid Color",
            description = "Color for the acid from range attacks in P2",
            section = verzik,
            hidden = true,
            unhide = "showVerzikAcid"
    )
    default Color showVerzikAcidColor() { return Color.GREEN; }

    @ConfigItem(
            position = 33,
            keyName = "lightningInfobox",
            name = "Attacks Until Lightning",
            description = "Shows the attacks left until lightning",
            section = verzik
    )
    default lightningMode lightningInfobox() {
        return lightningMode.OFF;
    }

    @ConfigItem(
            position = 34,
            keyName = "lightningAttackTick",
            name = "Lightning Attack Tick",
            description = "Displays the number of ticks before a lightning ball hits you.",
            section = verzik
    )
    default boolean lightningAttackTick() { return false; }

    @ConfigItem(
            position = 35,
            keyName = "purpleCrabInfobox",
            name = "Attacks Until Purple Crab",
            description = "Shows an infobox with the attacks left until purple crab can spawn",
            section = verzik
    )
    default boolean purpleCrabInfobox() {
        return false;
    }

    @ConfigItem(
            position = 36,
            keyName = "muteVerzikSounds",
            name = "Mute Verzik Sounds",
            description = "Woooooo.... more sounds to mute",
            section = verzik
    )
    default boolean muteVerzikSounds() {
        return false;
    }

    //------------------------------------------------------------//
    // Misc
    //------------------------------------------------------------//
    @ConfigItem(
            position = 0,
            keyName = "removeCastToB",
            name = "Remove Cast ToB",
            description = "Removes cast on players and thralls in Theatre of Blood",
            section = misc
    )
    default boolean removeCastToB() { return false; }

    @ConfigItem(
            position = 1,
            keyName = "entryInstanceTimer",
            name = "Tick Entry Timer",
            description = "Show the instance timer indicating when you should enter the Nylo and Xarpus rooms for perfect spawn.",
            section = misc
    )
    default instancerTimerMode entryInstanceTimer() {
        return instancerTimerMode.OVERHEAD;
    }

    @ConfigItem(
            position = 2,
            keyName = "removeFRCFlag",
            name = "Left Click Bank Loot",
            description = "Removes the 'Force Right Click' flag from the [Bank-all] option inside the Monumental Chest in the Loot Room",
            section = misc
    )
    default boolean removeFRCFlag() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "fontStyle",
            name = "Runelite Font",
            description = "Replaces the default font with whatever you have the dynamic font set to",
            section = misc
    )
    default boolean fontStyle() {
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = "redsTL",
            name = "Red Crabs True Tile",
            description = "Shows the true tile for red crabs",
            section = misc
    )
    default redsTlMode redsTL() {return redsTlMode.OFF;}

    @ConfigItem(
            position = 5,
            keyName = "redsTLColor",
            name = "Red Crabs True Tile Color",
            description = "Color for the reds true tile",
            section = misc
    )
    default Color redsTLColor() {return new Color(207, 138, 253, 255);}

    @ConfigItem(
            position = 6,
            keyName = "recolorBarriers",
            name = "Recolor Barriers",
            description = "Recolors all the barriers inside the raid",
            section = misc
    )
    default barrierMode recolorBarriers() {
        return barrierMode.COLOR;
    }

    @ConfigItem(
            position = 7,
            keyName = "barriersColor",
            name = "Barriers Color",
            description = "Sets the color of barriers",
            section = misc
    )
    @Alpha
    default Color barriersColor() {
        return new Color(106, 61, 255, 255);
    }

    @ConfigItem(
            position = 8,
            keyName = "swapTobBuys", 
            name = "Swap value with buy 1", 
            description = "Swap value and buy 1 on tob chest items",
            section = misc
    )
    default boolean swapTobBuys() {
        return false;
    }

    @ConfigItem(
            position = 9,
            keyName = "situationalTicks",
            name = "Situational Ticks",
            description = "Displays ticks till next attack on players with certain weapons <br> " +
                    "Local player in Bloat and all players in Xarpus",
            section = misc
    )
    default boolean situationalTicks() {return false;}

    @ConfigItem(
            position = 10,
            keyName = "staminaRequirement",
            name = "Xarpus - Stamina Requirement",
            description = "Doesn't let you go to Xarpus if you don't have a stamina potion",
            section = misc
    )
    default boolean staminaRequirement() {
        return false;
    }

    @ConfigItem(
            position = 11,
            keyName = "oldHpThreshold",
            name = "Old HP Colors",
            description = "Changes HP overlays from a gradual change to set colors <br>" +
                    "Works for maiden reds, verzik reds, and nylo pillars",
            section = misc
    )
    default boolean oldHpThreshold() {return false;}

    @ConfigItem(
            position = 66,
            keyName = "verzikTeleportCrystalHelper",
            name = "Remove Use Teleport Crystal",
            description = "Removes use option for verzik's teleport crystals on anything other than players",
            section = misc
    )
    default boolean verzikTeleportCrystalHelper() {return false;}

    @ConfigItem(
            position = 67,
            keyName = "lootReminder",
            name = "Loot Reminder",
            description = "Dont be a chest victim",
            section = misc
    )
    default lootReminderMode lootReminder() {
        return lootReminderMode.OFF;
    }

    @Alpha
    @ConfigItem(
            position = 68,
            keyName = "lootReminderColor",
            name = "Reminder Color",
            description = "Sets color of the chest highlight from loot reminder",
            section = misc
    )
    default Color lootReminderColor() {return new Color(106, 61, 255, 100);}

    @ConfigItem(
            position = 90,
            keyName = "fuckbluelite",
            name = "Fuck Bluelite",
            description = "Fuck Bluelite",
            section = misc
    )
    default boolean fuckBluelite() {
        return false;
    }
	
	@ConfigItem(
            keyName = "raveNylo",
            name = "Rave Nylos",
            description = "Fucking crab rave",
            section = misc,
            position = 99
    )
    default boolean raveNylo() {
        return false;
    }
	
	@ConfigItem(
            keyName = "raveNados",
            name = "Rave Nados",
            description = "Just incase you cant fucking see it",
            section = misc,
            position = 99
    )
    default raveNadoMode raveNados() {
        return raveNadoMode.OFF;
    }

    @ConfigItem(
            position = 99,
            keyName = "raveHats",
            name = "Rave Hats",
            description = "Hats = $400<br>Rave Hats = my fucking sanity",
            section = misc
    )
    default raveHatsMode raveHats() {
        return raveHatsMode.OFF;
    }

    @ConfigItem(
            position = 99,
            keyName = "raveLos",
            name = "Rave Xarpus Line of Sight",
            description = "No attack here... rave",
            section = misc
    )
    default raveLosMode raveLos() {
        return raveLosMode.OFF;
    }

    @ConfigItem(
            keyName = "highlightMelee",
            name = "",
            description = "",
            hidden = true
    )
    default boolean getHighlightMeleeNylo() {
        return false;
    }

    @ConfigItem(
            keyName = "highlightMelee",
            name = "",
            description = "",
            hidden = true
    )
    void setHighlightMeleeNylo(boolean var1);

    @ConfigItem(
            keyName = "highlightMage",
            name = "",
            description = "",
            hidden = true
    )
    default boolean getHighlightMageNylo() {
        return false;
    }

    @ConfigItem(
            keyName = "highlightMage",
            name = "",
            description = "",
            hidden = true
    )
    void setHighlightMageNylo(boolean var1);

    @ConfigItem(
            keyName = "highlightRange",
            name = "",
            description = "",
            hidden = true
    )
    default boolean getHighlightRangeNylo() {
        return false;
    }

    @ConfigItem(
            keyName = "highlightRange",
            name = "",
            description = "",
            hidden = true
    )
    void setHighlightRangeNylo(boolean var1);

    //------------------------------------------------------------//
    // Maiden enums
    //------------------------------------------------------------//
    enum maidenBloodSplatMode {
        OFF, COLOR, FLOW, RAVE
    }

    enum maidenFreezeTimerMode {
        OFF, TICKS, TILE
    }

    enum maidenBloodsMode{
        OFF, CAST, ATTACK, BOTH
    }

    public enum MaidenMaxHit {
        OFF("Off"),
        REGULAR("Regular"),
        ELY("Elysian"),
        BOTH("Both");

        private final String name;

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        private MaidenMaxHit(String name) {
            this.name = name;
        }
    }

    //------------------------------------------------------------//
    // Bloat enums
    //------------------------------------------------------------//
    enum BloatIndicatorMode {
        OFF, TILE, TRUE_LOCATION
    }

    enum bloatHandsMode{
        OFF, COLOR, FLOW, RAVE, RAVEST
    }

    enum bloatStompMode {
        OFF, COLOR, FLOW, RAVE
    }

    enum bloatTurnMode {
        OFF, SOUND, CHA_CHA
    }

    enum annoyingObjectHideMode{
        OFF, CHAINS, TANK, BOTH
    }

    //------------------------------------------------------------//
    // Nylo enums
    //------------------------------------------------------------//
    enum wheelchairMode {
        OFF, WAVES, BOSS, BOTH
    }

    enum nyloExplosionType {
        TILE, EXPLOSION
    }

    enum ExplosionWarning {
        OFF, TILE, TICKS, BOTH
    }

    enum nylotimealive {
        COUNTUP, COUNTDOWN
    }

    enum nyloBossPhaseChange {
        OFF, BOSS, BOTH
    }

    enum nyloSplitsMessage {
        OFF, WAVES, BOSS, BOTH
    }

    enum splitsMsgTiming {
        CLEANUP, FINISHED
    }

    enum smallSplitsMode {
        CAP, TOTAL, BOTH
    }

    enum waveSpawnTimerMode {
        OFF, INFOBOX, OVERLAY, BOTH
    }

    enum hidePillarsMode {
        OFF, PILLARS, CLEAN
    }

    //------------------------------------------------------------//
    // Sote enums
    //------------------------------------------------------------//
    enum soteOrbMode {
        OFF, TICKS, HATS, BOTH
    }

    enum soteDeathballMode {
        OFF, INFOBOX, OVERLAY, BOTH
    }

    enum soteDeathballOverlayMode {
        OFF, TICKS, NUKE, BOTH
    }

    enum raveHatsMode {
        OFF, RAVE, EPILEPSY, TURBO
    }

    //------------------------------------------------------------//
    // Xarpus enums
    //------------------------------------------------------------//
    enum exhumedMode {
        OFF, TILE, TICKS, BOTH
    }

    enum stepOffMode {
        OFF, TILE, TICKS, BOTH
    }

    enum meleeTileMode {
        OFF, NORMAL, TANK_NOTIFIER
    }

    enum losMode {
        OFF, MELEE, QUADRANT
    }

    //------------------------------------------------------------//
    // Verzik enums
    //------------------------------------------------------------//
    enum verzikAttacksMode {
        OFF, REDS, P2, ALL
    }

    enum VerzikNyloSetting {
        OFF, MY_CRABS, ALL_CRABS
    }

    enum nadoMode {
        OFF, ALL, PERSONAL
    }

    enum nadoStyle {
        TILE, TRUE_LOCATION
    }

    enum verzikYellowsMode {
        OFF, YELLOW, GROUPS
    }

    enum greenBouncePanelMode {
        OFF, BOUNCES, DAMAGE, BOTH
    }

    enum greenBallMode{
        OFF, TILE, AREA
    }

    enum lightningMode {
        OFF, INFOBOX, OVERLAY, BOTH
    }

    //------------------------------------------------------------//
    // Misc enums
    //------------------------------------------------------------//
    enum redsTlMode {
        OFF, MAIDEN, VERZIK, BOTH
    }

    enum barrierMode {
        OFF, COLOR, RAVE
    }

    enum instancerTimerMode {
        OFF, OVERHEAD, OVERLAY
    }

    enum lootReminderMode {
        OFF, DUMB, DUMBER, DUMBEST, DUMBEREST
    }

    enum raveLosMode {
        OFF, FlOW, RAVE
    }

    enum raveNadoMode {
        OFF, FlOW, RAVE
    }
}