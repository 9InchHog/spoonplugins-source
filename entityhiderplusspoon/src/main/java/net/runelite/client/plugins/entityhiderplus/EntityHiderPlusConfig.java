/*
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
 * Copyright (c) 2021, BickusDiggus <https://github.com/BickusDiggus>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.entityhiderplus;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("ehplus")
public interface EntityHiderPlusConfig extends Config
{
    //Config Sections
    @ConfigSection(
            name = "Alive NPCs",
            description = "Alive NPCs",
            position = 0,
            keyName = "npcsSection",
            closedByDefault = true
    )
    String npcsSection = "NPCs";

    @ConfigSection(
            name = "Dead NPCs",
            description = "Dead NPCs",
            position = 1,
            keyName = "deadNPCsSection",
            closedByDefault = true
    )
    String deadNPCsSection = "Dead NPCs";

    @ConfigSection(
            name = "Objects",
            description = "Objects",
            position = 2,
            keyName = "objectsSection",
            closedByDefault = true
    )
    String objectsSection = "Objects";

    // NPCs Section
    @ConfigItem(
            position = 0,
            keyName = "hideAliveName",
            name = "Hide Alive NPCs (Name)",
            description = "Configures which alive NPCs by Name are hidden",
            section = npcsSection
    )
    default String hideAliveName()
    {
        return "";
    }

    @ConfigItem(
            position = 1,
            keyName = "hideAliveId",
            name = "Hide Alive NPCs (ID)",
            description = "Configures which alive NPCs by ID are hidden",
            section = npcsSection
    )
    default String hideAliveId()
    {
        return "";
    }

    @ConfigItem(
            position = 2,
            keyName = "hideAnimation",
            name = "Hide NPCs On Animation (ID)",
            description = "Configures which NPCs by Animation ID to hide",
            section = npcsSection
    )
    default String hideAnimation()
    {
        return "8097,8078,8006,8000,7992";
    }

    // DeadNPCs Section
    @ConfigItem(
            position = 1,
            keyName = "hideDeadName",
            name = "Hide NPCs On Death (Name)",
            description = "Configures which NPCs by Name to hide when they die",
            section = deadNPCsSection
    )
    default String hideDeadName()
    {
        return "";
    }

    @ConfigItem(
            position = 2,
            keyName = "hideDeadID",
            name = "Hide NPCs On Death (ID)",
            description = "Configures which NPCs by ID to hide when they die",
            section = deadNPCsSection
    )
    default String hideDeadID()
    {
        return "";
    }

    @ConfigItem(
            position = 3,
            keyName = "ignoreNPCS",
            name = "Ignore NPCs (Name)",
            description = "Configures which NPCs by Name NOT to hide when they die",
            section = deadNPCsSection
    )
    default String ignoreNPCS()
    {
        return "ice demon,vet'ion,vet'ion reborn,dusk,dawn,vanguard,the nightmare,phosani's nightmare,kalphite queen";
    }

    @ConfigItem(
            position = 4,
            keyName = "ignoreNPCId",
            name = "Ignore NPCs (ID)",
            description = "Configures which NPCs by ID NOT to hide when they die",
            section = deadNPCsSection
    )
    default String ignoreNPCId()
    {
        return "";
    }

    // Objects Section
    @ConfigItem(
            position = 0,
            keyName = "hideGraphicsObjects",
            name = "Hide Graphics Objects (ID)",
            description = "Configures which Graphics Objects by ID to hide",
            section = objectsSection
    )
    default String hideGraphicsObjects()
    {
        return "1562,1563,1564";
    }

    // Hide Options Section
    @ConfigItem(
            position = 90,
            keyName = "hideDeadNPCs",
            name = "Hide ALL Dead NPCs",
            description = "Configures whether or not NPCs that just died are hidden"
    )
    default boolean hideDeadNPCs()
    {
        return false;
    }

    @ConfigItem(
            position = 91,
            keyName = "hideAttackDead",
            name = "Hide Attack Dead NPC's",
            description = "Hides attack option on any NPC that dies"
    )
    default boolean hideAttackDead() {
        return false;
    }

    @ConfigItem(
            position = 92,
            keyName = "highlightDead",
            name = "Highlight Dead NPC's",
            description = "Highlight any dead npcs"
    )
    default boolean higlightDead() {
        return false;
    }

    @Alpha
    @ConfigItem(
            position = 93,
            keyName = "highlightDeadColor",
            name = "Highlight Color",
            description = "Sets the color of highlight dead npcs"
    )
    default Color highlightDeadColor() {
        return Color.RED;
    }

    @Range(min = 1, max = 5)
    @ConfigItem(
            position = 94,
            keyName = "highlightDeadThiCC",
            name = "Highlight Width",
            description = "Sets the width of highlight dead npcs"
    )
    default int highlightDeadThiCC() { return 2; }

    @Range(min = 1, max = 4)
    @ConfigItem(
            position = 95,
            keyName = "highlightDeadFeather",
            name = "Highlight Feather",
            description = "Sets the feather of highlight dead npcs"
    )
    default int highlightDeadFeather() {
        return 2;
    }
}
