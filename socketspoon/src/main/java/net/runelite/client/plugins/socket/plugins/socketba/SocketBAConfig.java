package net.runelite.client.plugins.socket.plugins.socketba;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("socketBa")
public interface SocketBAConfig extends Config {
    @ConfigSection(
			name = "Attacker",
			description = "Attacker settings",
			position = 0,
			closedByDefault = true
	)
	String attSection = "attSection";

    @ConfigSection(
			name = "Collector",
			description = "Collector settings",
			position = 1,
			closedByDefault = true
	)
	String collSection = "collSection";

    @ConfigSection(
			name = "Defender",
			description = "Defender settings",
			position = 2,
			closedByDefault = true
	)
	String defSection = "defSection";

    @ConfigSection(
			name = "Healer",
			description = "Healer settings",
			position = 3,
			closedByDefault = true
	)
	String healSection = "healSection";

    //Attacker
    @ConfigItem(
            keyName = "hideAttack",
            name = "Hide Attack",
            description = "Hides attack on rangers and fighters if you are not attacker or using incorrect arrow/spell/attack style",
            position = 0,
            section = attSection
    )
    default boolean hideAttack() {
        return false;
    }

    @ConfigItem(
            keyName = "meleeStyleHighlight",
            name = "Melee Style Highlight",
            description = "Outlines the correct attack option in the combat tab",
            position = 1,
            section = attSection
    )
    default boolean meleeStyleHighlight() {
        return false;
    }

    @Alpha
    @ConfigItem(
            keyName = "meleeStyleHighlightColor",
            name = "Melee Highlight Color",
            description = "Sets the color of melee style highlight",
            position = 2,
            section = attSection
    )
    default Color meleeStyleHighlightColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "meleeSpecHighlight",
            name = "Melee Spec Highlight",
            description = "Highlights melee spec weapons depending on current call.<br>Accurate = Dihns    Others = Crystal Halberd",
            position = 3,
            section = attSection
    )
    default boolean meleeSpecHighlight() {
        return false;
    }

    @ConfigItem(
            keyName = "prioritizeNewestNpc",
            name = "Prioritize Newest NPC (BETA)",
            description = "Makes left click attack on the newest NPC in the stack (In testing still)",
            position = 4,
            section = attSection
    )
    default boolean prioritizeNewestNpc() {
        return false;
    }

    //Collector
    @ConfigItem(
            keyName = "highlightEggs",
            name = "Highlight Eggs",
            description = "Highlights the correct eggs tiles.",
            position = 0,
            section = collSection
    )
    default boolean highlightEggs() {
        return false;
    }
	
	@ConfigItem(
            keyName = "leftClickEggs",
            name = "Remove Wrong Eggs",
            description = "Removes pick up option on incorrect eggs.",
            position = 1,
            section = collSection
    )
    default boolean leftClickEggs() {
        return true;
    }

    //Defender
    @ConfigItem(
            keyName = "deprioPickupFood",
            name = "Deprio Pickup Food",
            description = "Makes it so you can only right click to pickup dropped food",
            position = 0,
            section = defSection
    )
    default boolean deprioPickupFood() {
        return false;
    }

    //Healer
    @ConfigItem(
            keyName = "hideHpOverlay",
            name = "Hide HP Overlay",
            description = "Hides that big ass HP overlay",
            position = 0,
            section = healSection
    )
    default boolean hideHpOverlay() {
        return false;
    }

    @ConfigItem(
            keyName = "removeUseFood",
            name = "Remove Use Food",
            description = "Removes the use food option on anything other than healers",
            position = 1,
            section = healSection
    )
    default boolean removeUseFood() {
        return false;
    }

    //General
    @ConfigItem(
            keyName = "roleInfobox",
            name = "Role Infobox",
            description = "Displays an infobox with your role and the correct call.",
            position = 4
    )
    default boolean roleInfobox() {
        return true;
    }

	@ConfigItem(
            keyName = "leftClickHorn",
            name = "Left Click Horn",
            description = "Sets left click option on horn to the correct call.",
            position = 5
    )
    default boolean leftClickHorn() {
        return true;
    }

    @ConfigItem(
            keyName = "correctItemHighlight",
            name = "Correct Item Highlight",
            description = "Highlight the correct items in your inventory for your role",
            position = 6
    )
    default correctItemHighlightMode correctItemHighlight() {
        return correctItemHighlightMode.OFF;
    }

    @Alpha
    @ConfigItem(
            keyName = "correctItemColor",
            name = "Correct Item Color",
            description = "Sets the color for Correct Item Highlight",
            position = 7
    )
    default Color correctItemColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "highlightRoleNpcs",
            name = "Highlight Role NPCs",
            description = "Highlights the tile of the NPCs for your role",
            position = 8
    )
    default boolean highlightRoleNpcs() {
        return false;
    }

    @Alpha
    @ConfigItem(
            keyName = "highlightRoleNpcsColor",
            name = "Role NPC Color",
            description = "Sets the color for Highlight Role NPCs",
            position = 9
    )
    default Color highlightRoleNpcsColor() {return new Color(0, 255, 255, 20);}

    @ConfigItem(
          keyName = "highlightVendingMachine",
          name = "Highlight Vending Machine",
          description = "Highlights correct vending machine and removes click for incorrect ones",
          position = 10
    )
    default boolean highlightVendingMachine() {
        return false;
    }

    @ConfigItem(
          keyName = "vendingMachineColor",
          name = "Vending Color",
          description = "Sets the color for Highlight Vending Machine",
          position = 11
    )
    default Color vendingMachineColor() {return new Color(0, 255, 255);}

    @Range(min = 0, max = 255)
    @ConfigItem(
          keyName = "vendingMachineOpacity",
          name = "Vending Opacity",
          description = "Sets the fill opacity for Highlight Vending Machine",
          position = 12
    )
    default int vendingMachineOpacity() {return 0;}

    @ConfigItem(
            keyName = "overstockHotkey",
            name = "Overstock Hotkey",
            description = "Makes the left click option on vending machine the current call overstock",
            position = 13
    )
    default Keybind overstockHotkey() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
          keyName = "cannonHelper",
          name = "Cannon Helper",
          description = "Helps see cannon clickbox better",
          position = 97
    )
    default boolean cannonHelper() {
        return false;
    }

    @ConfigItem(
          keyName = "discoQueen",
          name = "Disco Queen",
          description = "Had to do it to em",
          position = 98
    )
    default boolean discoQueen() {
        return false;
    }

    @ConfigItem(
            keyName = "bmMessages",
            name = "Messages",
            description = "Letting you know whats up.... definitely not toxic...",
            position = 99
    )
    default boolean bmMessages() {return false;}

    public enum correctItemHighlightMode {
        OFF, BOX, OUTLINE, UNDERLINE
    }
}
