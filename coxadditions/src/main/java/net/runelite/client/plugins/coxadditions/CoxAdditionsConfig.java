package net.runelite.client.plugins.coxadditions;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("CoxAdditions")
public interface CoxAdditionsConfig extends Config {
    @ConfigSection(
            name = "Olm",
            description = "Olm Plugins",
            position = 0,
            closedByDefault = true
    )
    String olmSection = "olm";

    @ConfigSection(
            name = "Rooms",
            description = "Cox Room Plugins",
            position = 1,
            closedByDefault = true
    )
    String roomSection = "rooms";

    @ConfigSection(
            name = "Prep",
            description = "Cox Prep Plugins",
            position = 2,
            closedByDefault = true
    )
    String prepSection = "prep";

    // Olm Section
    @ConfigItem(
            name = "Acid",
            keyName = "acidTickCounter",
            description = "Shows how long you have acid for (yourself only)",
            position = 1,
            section = olmSection)
    default boolean acidTickCounter() {
        return false;
    }

    @ConfigItem(
            name = "Burn",
            keyName = "burnTickCounter",
            description = "Shows how long you are burned for (yourself only)",
            position = 2,
            section = olmSection)
    default boolean burnTickCounter() {
        return false;
    }

    @ConfigItem(
            name = "Crystals",
            keyName = "crystalTickCounter",
            description = "Shows how long you have crystals for (yourself only)",
            position = 3,
            section = olmSection)
    default boolean crystalTickCounter() {
        return false;
    }

    @ConfigItem(
            keyName = "olmCrippleTimer",
            name = "Olm Cripple Timer",
            description = "Adds a timer over olms right hand when crippled",
            position = 4,
            section = olmSection)
    default boolean olmCrippleTimer() {
        return true;
    }

    @Range(min = 1, max = 32)
    @ConfigItem(
            name = "Olm Cripple Text Size",
            keyName = "olmCrippleTextSize",
            description = "Increase or decreases the size of the text for the Olm cripple timer timer",
            position = 5,
            section = olmSection)
    default int olmCrippleTextSize() {
        return 20;
    }

    @Alpha
    @ConfigItem(
            keyName = "olmCrippleText",
            name = "Olm Cripple Text",
            description = "Configures the color of the timer for olm hand cripple",
            position = 6,
            section = olmSection)
    default Color olmCrippleText() {
        return Color.YELLOW;
    }

    @ConfigItem(
            name = "Olm Healing Pool",
            keyName = "olmHealingPoolTimer",
            description = "Puts a timer on Olm healing pools",
            position = 7,
            section = olmSection)
    default healingPoolMode olmHealingPoolTimer() {
        return healingPoolMode.OFF;
    }

    @Alpha
    @ConfigItem(
            name = "Olm Pool Timer Color",
            keyName = "olmHealingPoolTimerColor",
            description = "Configures the color of the Olm healing pool timer",
            position = 8,
            section = olmSection)
    default Color olmHealingPoolTimerColor() {
        return Color.WHITE;
    }

    @ConfigItem(
            name = "Olm Phase Highlight",
            keyName = "olmPhaseHighlight",
            description = "Highlights Olm head the color of the phase (Red = Flame, Green = Acid, Purple = Crystal)",
            position = 9,
            section = olmSection)
    default boolean olmPhaseHighlight() {
        return false;
    }

    @ConfigItem(
            position = 10,
            keyName = "olmPhasePanel",
            name = "Show Phase Panel",
            description = "Displays Olm phase in an infobox",
            section = olmSection
    )
    default boolean olmPhasePanel() { return false; }

    @Alpha
    @ConfigItem(
            name = "Final Phase Color",
            keyName = "olmHighlightColor",
            description = "Configures the color of the Olm phase highlight",
            position = 11,
            section = olmSection)
    default Color olmHighlightColor() {
        return Color.CYAN;
    }

    @Range(min = 1, max = 5)
    @ConfigItem(
            name = "Olm Outline Width",
            keyName = "olmThiCC",
            description = "Outline width for Olm phase highlight",
            position = 12,
            section = olmSection)
    default double olmThiCC() {
        return 2;
    }

    @ConfigItem(
            name = "Olm Orbs",
            keyName = "olmOrbs",
            description = "Puts an infobox and prayer tab marker whenever an orb is directed at you",
            position = 13,
            section = olmSection)
    default boolean olmOrbs() { return false; }

    @Range(min = 1, max = 3)
    @ConfigItem(
            name = "Orbs Prayer Tab Width",
            keyName = "prayerStrokeSize",
            description = "Adjusts the width of the prayer tab marker when Infobox is selected for Olm Orbs",
            position = 14,
            section = olmSection)
    default int prayerStrokeSize() { return 2; }

    @ConfigItem(
            keyName = "hideAttackHead",
            name = "Hide Attack Head",
            description = "Removes the attack option on Olms Head before head phase",
            position = 15,
            section = olmSection)
    default boolean hideAttackHead() {
        return true;
    }

    @ConfigItem(
            name = "Olm Hands Health",
            keyName = "olmHandsHealth",
            description = "Puts an overlay on Olm's hands showing their current HP",
            position = 16,
            section = olmSection)
    default olmHandsHealthMode olmHandsHealth() { return olmHandsHealthMode.OFF; }

    @ConfigItem(
            position = 17,
            keyName = "olmSide",
            name = "Olm Side Highlight",
            description = "Highlights a tile indicating which side olm will spawn on - disappears when he pops up",
            section = olmSection
    )
    default boolean olmSide() { return false; }

    @Alpha
    @ConfigItem(
            keyName = "olmSideColor",
            name = "Olm Side Color",
            description = "Configures the color of the Olm side highlight",
            position = 18,
            section = olmSection)
    default Color olmSideColor() {
        return Color.RED;
    }

    @ConfigItem(
            position = 19,
            keyName = "olmTp",
            name = "Teleport Portals Arrow",
            description = "Puts a retard-proof arrow on the teleports in solo",
            section = olmSection
    )
    default boolean olmTp() { return false; }

    @ConfigItem(
            position = 20,
            keyName = "teleportTarget",
            name = "Teleport Target",
            description = "Highlights the player paired with for portals that you're about to yeet cross map",
            section = olmSection
    )
    default boolean teleportTarget() { return false; }

    @ConfigItem(
            position = 21,
            keyName = "teleportTargetTicks",
            name = "Teleport Target Ticks",
            description = "Shows ticks until teleport attack activates",
            section = olmSection
    )
    default boolean teleportTargetTicks() { return false; }

    @ConfigItem(
            position = 22,
            keyName = "teleportColor",
            name = "Teleport Target Color",
            description = "Changes color for Teleport Target and Teleport Target Ticks",
            section = olmSection
    )
    default Color teleportColor() {return new Color(193, 255, 245);}

    //Room Section
    @ConfigItem(
            keyName = "swapCoXKeystone",
            name = "CoX Keystone",
            description = "swaps use with drop for the keystone crystal at tightrope",
            position = 1,
            section = roomSection)
    default boolean swapCoXKeystone() {
        return false;
    }

    @ConfigItem(
            keyName = "removeCastCoX",
            name = "Remove Cast CoX",
            description = "Removes cast on players in Chambers of Xeric",
            position = 2,
            section = roomSection)
    default boolean removeCastCoX() {
        return false;
    }

    @ConfigItem(
            name = "Instance Timer",
            keyName = "instanceTimer",
            description = "Instance timer for starting a raid.",
            position = 3,
            section = roomSection)
    default instanceTimerMode instanceTimer() { return instanceTimerMode.OVERHEAD;}

    @ConfigItem(
            name = "Left Click Smash",
            keyName = "leftClickSmash",
            description = "Toggles left click smashing crabs with dwh",
            position = 4,
            section = roomSection)
    default boolean leftClickSmash() { return false; }

    @ConfigItem(
            name = "Remove Chop",
            keyName = "removeChop",
            description = "Removes chop option on trees at Ice Demon when no axe in inventory",
            position = 5,
            section = roomSection)
    default boolean removeChop() { return false; }

    @ConfigItem(
            name = "Remove Pick Spec",
            keyName = "removePickSpec",
            description = "Removes spec option for dragon pickaxe when in a raid",
            position = 6,
            section = roomSection)
    default boolean removePickSpec() { return false; }

    @ConfigItem(
            name = "Small Muttadile HP",
            keyName = "smallMuttaHp",
            description = "Displays the health percentage of small Muttadile while meat tree is alive",
            position = 7,
            section = roomSection)
    default boolean smallMuttaHp() {
        return true;
    }

    @ConfigItem(
            name = "Mutta Chop Cycle",
            keyName = "meatTreeChopCycle",
            description = "Displays a timer till next chop cycle when cutting the Meat Tree<br>-----------------------------------------------------------<br>Created By: Kitsch",
            position = 8,
            section = roomSection)
    default meatTreeChopCycleMode meatTreeChopCycle() {
        return meatTreeChopCycleMode.OFF;
    }

    @ConfigItem(
            name = "Vanguard Tick Cycle",
            keyName = "vangsCycle",
            description = "Shows the ticks that the Vanguards have been up for. Resets everytime they go down",
            position = 9,
            section = roomSection)
    default VangsTicksMode vangsCycle() {
        return VangsTicksMode.OFF;
    }

    @ConfigItem(
            name = "Remove Feed",
            keyName = "removeFeed",
            description = "Removes feed option on lux grubs in vesp when no herbs in inventory",
            position = 10,
            section = roomSection)
    default boolean removeFeed() { return false; }

    @ConfigItem(
            name = "Remove Pick Root",
            keyName = "removePickRoot",
            description = "Removes pick on roots in Vespula after it dies",
            position = 11,
            section = roomSection)
    default boolean removePickRoot() { return false; }

    @ConfigItem(
            name = "Hide Attack Vespula",
            keyName = "hideVesp",
            description = "Hides attack option on Vespula",
            position = 12,
            section = roomSection)
    default boolean hideVesp() {
        return true;
    }

    @ConfigItem(
            name = "Vespula Prayer Enhance",
            keyName = "vespPrayerEnhance",
            description = "Displays the ticks left until prayer enhance regens while in Vespula",
            position = 13,
            section = roomSection)
    default boolean vespPrayerEnhance() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightShortcuts",
            name = "Highlight shortcuts",
            description = "Displays which shortcut it is",
            position = 14,
            section = roomSection
    )
    default boolean highlightShortcuts() {
        return true;
    }

    @ConfigItem(
            name = "Shortcut Color",
            keyName = "shortcutColor",
            description = "Highlight color for shortcuts",
            position = 15,
            section = roomSection)
    default Color shortcutColor() {
        return Color.YELLOW;
    }

    @ConfigItem(
            name = "True Location List",
            keyName = "tlList",
            description = "NPC's in this list will be highlighted with true location. ONLY works with Cox bosses",
            position = 16,
            section = roomSection
    )
    default String tlList() {
        return "";
    }

    @Range(min = 1, max = 5)
    @ConfigItem(
            name = "True Location Width",
            keyName = "tlThiCC",
            description = "Outline width for true location highlight",
            position = 17,
            section = roomSection)
    default double tlThiCC() {
        return 2;
    }

    @ConfigItem(
            name = "True Location Color",
            keyName = "tlColor",
            description = "Highlight color for true location",
            position = 18,
            section = roomSection)
    default Color tlColor() {
        return Color.decode("2555817");
    }

    @ConfigItem(
            name = "Rope Tag Helper",
            keyName = "chinRope",
            description = "Highlights rangers/magers when multiple are next to each other",
            position = 19,
            section = roomSection)
    default chinRopeMode chinRope() {
        return chinRopeMode.OFF;
    }

    @Range(min = 1, max = 5)
    @ConfigItem(
            name = "Rope NPC Width",
            keyName = "chinRopeThiCC",
            description = "Width for the Rope NPC highlights",
            position = 20,
            section = roomSection)
    default int chinRopeThiCC() {
        return 2;
    }

    @ConfigItem(
            name = "Rope Tag Helper Color",
            keyName = "chinRopeColor",
            description = "Highlight color for rangers/magers chin helper at rope",
            position = 21,
            section = roomSection)
    default Color chinRopeColor() {
        return Color.MAGENTA;
    }

    @ConfigItem(
            name = "Rope Cross",
            keyName = "ropeCross",
            description = "Highlights the rope green during safe ticks, orange during questionable, and red during certain death",
            position = 22,
            section = roomSection)
    default ropeCrossMode ropeCross() {
        return ropeCrossMode.OFF;
    }

    @ConfigItem(
            name = "Rope Cross Ticks",
            keyName = "ropeCrossTicks",
            description = "Puts the ticks since activating rapid heal on the local player, on the rope, or both",
            position = 22,
            section = roomSection)
    default ropeCrossTicksMode ropeCrossTicks() {
        return ropeCrossTicksMode.ROPE;
    }

    @ConfigItem(
            name = "Rope Cross Ticks Countdown",
            keyName = "ropeTicksDown",
            description = "Counts down the ticks instead of up",
            position = 22,
            section = roomSection)
    default boolean ropeTicksDown() {
        return true;
    }

    @ConfigItem(
            name = "Shaman Slam",
            keyName = "shamanSlam",
            description = "Predicts where the Lizardman Shaman will rain down from the sky.",
            position = 22,
            section = roomSection)
    default boolean shamanSlam() {
        return false;
    }

    @Alpha
    @ConfigItem(
            name = "Shaman Slam Color",
            keyName = "shamanSlamColor",
            description = "Configures the color of the Shaman slam overlay",
            position = 23,
            section = roomSection)
    default Color shamanSlamColor() {
        return Color.RED;
    }

    @ConfigItem(
            name = "Shaman Spawn AoE",
            keyName = "shamanSpawn",
            description = "Shows the explosion radius of the little purple bastards",
            position = 24,
            section = roomSection)
    default boolean shamanSpawn() {
        return false;
    }

    @Alpha
    @ConfigItem(
            name = "Shaman Spawn Color",
            keyName = "shamanSpawnColor",
            description = "Configures the color of the barney explosion overlay",
            position = 25,
            section = roomSection)
    default Color shamanSpawnColor() {
        return new Color(255, 0, 0, 25);
    }

    @ConfigItem(
            name = "Vasa Crystal Timer",
            keyName = "vasaCrystalTimer",
            description = "Puts a timer on active crystal until Vasa teleports again",
            position = 26,
            section = roomSection)
    default crystalTimerMode vasaCrystalTimer() { return crystalTimerMode.BOLD; }

    @Range(min = 1, max = 32)
    @ConfigItem(
            name = "Vasa Crystal Timer Text",
            keyName = "vasaCrystalTextSize",
            description = "Increase or decreases the size of the text for the Vasa crystal timer",
            position = 27,
            section = roomSection)
    default int vasaCrystalTextSize() {
        return 20;
    }

    @ConfigItem(
            name = "Vasa Crystal Timer Color",
            keyName = "vasaCrystalTimerColor",
            description = "Color picker for vasa crystal timer",
            position = 28,
            section = roomSection)
    default Color vasaCrystalTimerColor() {
        return Color.WHITE;
    }

    @ConfigItem(
            name = "Left Click Leave CC",
            keyName = "leftClickLeave",
            description = "Fuck this shit im out",
            position = 29,
            section = roomSection)
    default boolean leftClickLeave() {
        return false;
    }

    //Prep Section
    @ConfigItem(
            keyName = "swapBats",
            name = "CoX Bats",
            description = "Fuck em",
            position = 1,
            section = prepSection)
    default boolean swapBats() {
        return true;
    }

    @ConfigItem(
            keyName = "swapCoXTools",
            name = "CoX Tools",
            description = "Swaps left click for tools you need in CoX",
            position = 2,
            section = prepSection)
    default boolean swapCoXTools() {
        return true;
    }

    @ConfigItem(
            name = "CoX Herb Timer",
            keyName = "coxHerbTimer",
            description = "Displays a timer for herb growth",
            position = 3,
            section = prepSection)
    default CoXHerbTimerMode coxHerbTimer() {
        return CoXHerbTimerMode.OFF;
    }

    @Alpha
    @ConfigItem(
            name = "CoX Herb Timer Color",
            keyName = "coxHerbTimerColor",
            description = "Sets color of CoX herb timer",
            position = 4,
            section = prepSection)
    default Color coxHerbTimerColor() {
        return Color.YELLOW;
    }

    @Range(min = 10, max = 30)
    @ConfigItem(
            name = "CoX Herb Timer Size",
            keyName = "coxHerbTimerSize",
            description = "Sets the size of the CoX herb timer",
            position = 5,
            section = prepSection)
    default int coxHerbTimerSize() {
        return 20;
    }

    @ConfigItem(
            name = "Remove Use Seed",
            keyName = "removeUseSeed",
            description = "Removes use option on other players for seeds",
            position = 6,
            section = prepSection)
    default boolean removeUseSeed() { return false; }

    @ConfigItem(
            name = "Remove Use Vial",
            keyName = "removeUseVial",
            description = "Removes use option on other players for vials",
            position = 7,
            section = prepSection)
    default boolean removeUseVial() { return false; }

    @ConfigItem(
            name = "Highlight Chest Mode",
            keyName = "highlightChest",
            description = "Highlight items in your private chest based off the lists",
            position = 8,
            section = prepSection)
    default HighlightChestMode highlightChest() {
        return HighlightChestMode.OFF;
    }

    @ConfigItem(
            name = "Highlight Private Chest Items 1",
            keyName = "highlightChestItems",
            description = "Highlights items in the list in the storage chest. Must be ids.",
            position = 9,
            section = prepSection)
    default String highlightChestItems() { return ""; }

    @Alpha
    @ConfigItem(
            name = "Chest Items Color 1",
            keyName = "highlightChestItemsColor",
            description = "Sets color of highlight chest items",
            position = 10,
            section = prepSection)
    default Color highlightChestItemsColor() { return Color.WHITE; }

    @ConfigItem(
            name = "Highlight Private Chest Items 2",
            keyName = "highlightChestItems2",
            description = "Highlights items in the list in the storage chest. Must be ids.",
            position = 11,
            section = prepSection)
    default String highlightChestItems2() { return ""; }

    @Alpha
    @ConfigItem(
            name = "Chest Items Color 2",
            keyName = "highlightChestItemsColor2",
            description = "Sets color of highlight chest items",
            position = 12,
            section = prepSection)
    default Color highlightChestItemsColor2() { return Color.WHITE; }

    public enum CoXHerbTimerMode {
        OFF,
        TEXT,
        PIE
    }

    public enum VangsTicksMode {
        OFF,
        TOTAL_TICKS,
        FOUR_TICK_CYCLE,
        BOTH
    }

    public enum chinRopeMode {
        OFF,
        HULL,
        OUTLINE
    }

    public enum olmHandsHealthMode {
        OFF,
        INFOBOX,
        OVERLAY
    }

    public enum crystalTimerMode {
        OFF,
        BOLD,
        REGULAR,
        SMALL,
        CUSTOM
    }

    public enum HighlightChestMode {
        OFF,
        UNDERLINE,
        OUTLINE
    }

    public enum instanceTimerMode {
        OFF,
        OVERHEAD,
        INFOBOX
    }

    public enum meatTreeChopCycleMode {
        OFF,
        OVERLAY,
        INFOBOX
    }

    public enum healingPoolMode {
        OFF, TIMER, OVERLAY, BOTH
    }

    public enum olmCrystalMode {
        OFF, TILE, AREA
    }

    public enum ropeCrossMode {
        OFF, TICKS, HIGHLIGHT, BOTH
    }

    public enum ropeCrossTicksMode {
        ROPE, PLAYER, BOTH
    }
}
