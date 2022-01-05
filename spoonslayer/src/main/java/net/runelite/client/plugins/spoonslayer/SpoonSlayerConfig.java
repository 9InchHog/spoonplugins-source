/*
 * Copyright (c) 2017, Seth <Sethtroll3@gmail.com>
 * Copyright (c) 2018, Shaun Dreclin <shaundreclin@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.spoonslayer;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup(SpoonSlayerConfig.GROUP_NAME)
public interface SpoonSlayerConfig extends Config
{
    String GROUP_NAME = "slayer";

    // Key names for stored task values
    String TASK_NAME_KEY = "taskName";
    String AMOUNT_KEY = "amount";
    String INIT_AMOUNT_KEY = "initialAmount";
    String TASK_LOC_KEY = "taskLocation";
    String STREAK_KEY = "streak";
    String POINTS_KEY = "points";

    @ConfigSection(
            name = "Task Highlights",
            description = "Highlight settings",
            position = 0
    )
    String overlay = "overlay";

    @ConfigItem(
            position = 0,
            keyName = "highlightTargets",
            name = "Highlight Targets",
            description = "Highlight monsters you can kill for your current slayer assignment",
            section = overlay
    )
    default TileMode highlightTargets() {return TileMode.OFF;}

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "targetColor",
            name = "Target Color",
            description = "Color of the highlighted targets",
            section = overlay
    )
    default Color getTargetColor() {return Color.RED;}

    @ConfigItem(
            position = 2,
            keyName = "opacity",
            name = "Opacity",
            description = "Configures the opacity of the highlighted style",
            section = overlay
    )
    default int opacity() {return 20;}

    @ConfigItem(
            position = 3,
            keyName = "targetThiCC",
            name = "Target Width",
            description = "Configures the width of the tiles",
            section = overlay
    )
    @Range(min = 0, max = 5)
    default int targetThiCC() {return 2;}

    @ConfigItem(
            position = 4,
            keyName = "outlineFeather",
            name = "Outline feather",
            description = "Specify between 0-4 how much of the model outline should be faded",
            section = overlay
    )
    @Range(min = 0, max = 4)
    default int outlineFeather() {return 0;}

    @ConfigItem(
            position = 5,
            keyName = "antiAlias",
            name = "Anti-Aliasing",
            description = "Turns on anti-aliasing for the tiles. Makes them smoother.",
            section = overlay
    )
    default boolean antiAlias() {return false;}

    //Other Section
    @ConfigItem(
            position = 1,
            keyName = "infobox",
            name = "Task InfoBox",
            description = "Display task information in an InfoBox"
    )
    default boolean showInfobox()
    {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "itemoverlay",
            name = "Count on Items",
            description = "Display task count remaining on slayer items"
    )
    default boolean showItemOverlay()
    {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "superiornotification",
            name = "Superior foe notification",
            description = "Toggles notifications on superior foe encounters"
    )
    default boolean showSuperiorNotification()
    {
        return true;
    }

    @ConfigItem(
            position = 4,
            keyName = "statTimeout",
            name = "InfoBox Expiry",
            description = "Set the time until the InfoBox expires"
    )
    @Units(Units.MINUTES)
    default int statTimeout()
    {
        return 5;
    }

    @ConfigItem(
            position = 5,
            keyName = "weaknessPrompt",
            name = "Show Monster Weakness",
            description = "Show an overlay on a monster when it is weak enough to finish off (Only Lizards, Gargoyles & Rockslugs)"
    )
    default boolean weaknessPrompt()
    {
        return true;
    }

    @ConfigItem(
            position = 6,
            keyName = "taskCommand",
            name = "Task Command",
            description = "Configures whether the slayer task command is enabled<br> !task"
    )
    default boolean taskCommand()
    {
        return true;
    }

    public enum TileMode {
        OFF, AREA, HULL, TILE, TRUE_LOCATIONS, SOUTH_WEST_TILE, OUTLINE;
    }
}
