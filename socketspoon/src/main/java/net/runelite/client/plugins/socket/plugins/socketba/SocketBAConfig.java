package net.runelite.client.plugins.socket.plugins.socketba;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("socketBa")
public interface SocketBAConfig extends Config {
  @ConfigItem(
            keyName = "roleInfobox",
            name = "Role Infobox",
            description = "Displays an infobox with your role and the correct call.",
            position = 1
    )
    default boolean roleInfobox() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightEggs",
            name = "Highlight Eggs",
            description = "Highlights the correct eggs tiles.",
            position = 2
    )
    default boolean highlightEggs() {
        return false;
    }
	
	@ConfigItem(
            keyName = "leftClickEggs",
            name = "Remove Wrong Eggs",
            description = "Removes pick up option on incorrect eggs.",
            position = 3
    )
    default boolean leftClickEggs() {
        return true;
    }
	
	@ConfigItem(
            keyName = "leftClickHorn",
            name = "Left Click Horn",
            description = "Sets left click option on horn to the correct call.",
            position = 4
    )
    default boolean leftClickHorn() {
        return true;
    }

    @ConfigItem(
            keyName = "hideAttack",
            name = "Hide Attack",
            description = "Hides attack on rangers and fighters if you are not attacker or using incorrect arrow/spell/attack style",
            position = 5
    )
    default boolean hideAttack() {
        return false;
    }

    @ConfigItem(
            keyName = "meleeStyleHighlight",
            name = "Melee Style Highlight",
            description = "Outlines the correct attack option in the combat tab",
            position = 6
    )
    default boolean meleeStyleHighlight() {
        return false;
    }

    @Alpha
    @ConfigItem(
            keyName = "meleeStyleHighlightColor",
            name = "Melee Highlight Color",
            description = "Sets the color of melee style highlight",
            position = 7
    )
    default Color meleeStyleHighlightColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "meleeSpecHighlight",
            name = "Melee Spec Highlight",
            description = "Highlights melee spec weapons depending on current call.<br>Accurate = Dihns    Others = Crystal Halberd",
            position = 8
    )
    default boolean meleeSpecHighlight() {
        return false;
    }

    @ConfigItem(
            keyName = "correctItemHighlight",
            name = "Correct Item Highlight",
            description = "Highlight the correct items in your inventory for your role",
            position = 9
    )
    default correctItemHighlightMode correctItemHighlight() {
        return correctItemHighlightMode.OFF;
    }

    @Alpha
    @ConfigItem(
            keyName = "correctItemColor",
            name = "Correct Item Color",
            description = "Sets the color for Correct Item Highlight",
            position = 10
    )
    default Color correctItemColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "hideHpOverlay",
            name = "Hide HP Overlay",
            description = "Hides that big ass HP overlay",
            position = 11
    )
    default boolean hideHpOverlay() {
        return false;
    }

    @ConfigItem(
            keyName = "highlightRoleNpcs",
            name = "Highlight Role NPCs",
            description = "Highlights the tile of the NPCs for your role",
            position = 12
    )
    default boolean highlightRoleNpcs() {
        return false;
    }

    @Alpha
    @ConfigItem(
            keyName = "highlightRoleNpcsColor",
            name = "Role NPC Color",
            description = "Sets the color for Highlight Role NPCs",
            position = 13
    )
    default Color highlightRoleNpcsColor() {return new Color(0, 255, 255, 20);}

    @ConfigItem(
            keyName = "removeUseFood",
            name = "Remove Use Food",
            description = "Removes the use food option on anything other than healers",
            position = 14
    )
    default boolean removeUseFood() {
        return false;
    }

    @ConfigItem(
          keyName = "highlightVendingMachine",
          name = "Highlight Vending Machine",
          description = "Highlights correct vending machine and removes click for incorrect ones",
          position = 15
    )
    default boolean highlightVendingMachine() {
        return false;
    }

    @ConfigItem(
          keyName = "vendingMachineColor",
          name = "Vending Machine Color",
          description = "Sets the color for Highlight Vending Machine",
          position = 16
    )
    default Color vendingMachineColor() {return new Color(0, 255, 255);}

    @Range(min = 0, max = 255)
    @ConfigItem(
          keyName = "vendingMachineOpacity",
          name = "Vending Machine Opacity",
          description = "Sets the fill opacity for Highlight Vending Machine",
          position = 16
    )
    default int vendingMachineOpacity() {return 0;}


    @ConfigItem(
          keyName = "cannonHelper",
          name = "Cannon Helper",
          description = "Helps see cannon clickbox better",
          position = 17
    )
    default boolean cannonHelper() {
        return false;
    }

    @ConfigItem(
          keyName = "discoQueen",
          name = "Disco Queen",
          description = "Had to do it to em",
          position = 18
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
