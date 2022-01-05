/*
 * Copyright (c) 2020, Brooklyn <https://github.com/Broooklyn>
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
package net.runelite.client.plugins.spoonannoyancemute;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("annoyancemute")
public interface SpoonAnnoyanceMuteConfig extends Config
{
    @ConfigSection(
            name = "Combat",
            description = "Combat sounds to mute",
            position = 0,
            closedByDefault = true
    )
    String combatSection = "Combat";

    @ConfigSection(
            name = "NPCs",
            description = "NPC sounds to mute",
            position = 1,
            closedByDefault = true
    )
    String npcSection = "NPCs";

    @ConfigSection(
            name = "Skilling",
            description = "Skilling sounds to mute",
            position = 2,
            closedByDefault = true
    )
    String skillingSection = "Skilling";

    @ConfigSection(
            name = "Prayers",
            description = "Pray activation/deactivation sounds to mute",
            position = 3,
            closedByDefault = true
    )
    String prayerSection = "Prayers";

    @ConfigSection(
            name = "Miscellaneous",
            description = "Miscellaneous sounds to mute",
            position = 4,
            closedByDefault = true
    )
    String miscSection = "Miscellaneous";

    //------------------------------------------------------------//
    // Combat
    //------------------------------------------------------------//
    @ConfigItem(
            keyName = "muteREEEE",
            name = "Armadyl Crossbow",
            description = "Mutes the REEEEE of the ACB spec",
            section = combatSection
    )
    default boolean muteREEEE()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteCannon",
            name = "Cannon spin",
            description = "Mutes the sounds of a cannon spinning",
            section = combatSection
    )
    default boolean muteCannon()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteIceSpells",
            name = "Ice Spells",
            description = "Mutes the sounds of Ice Barrage, Ice Blitz, etc.",
            section = combatSection
    )
    default boolean muteIceSpells()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteThrallSummon",
            name = "Thrall Summmon",
            description = "Mutes the sounds of summoning Thralls",
            section = combatSection,
            position = 99
    )
    default boolean muteThrallSummon()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteThrallAttacks",
            name = "Thrall Attacks",
            description = "Mutes the sounds of Thralls attacking",
            section = combatSection,
            position = 100
    )
    default boolean muteThrallAttacks()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteThrallDeath",
            name = "Thrall Death",
            description = "Mutes the sounds of Thralls returning to the grave",
            section = combatSection,
            position = 101
    )
    default boolean muteThrallDeath()
    {
        return true;
    }

    //------------------------------------------------------------//
    // NPC
    //------------------------------------------------------------//
    @ConfigItem(
            keyName = "muteCaveHorrors",
            name = "Cave Horrors",
            description = "Mutes the sound of Cave Horrors",
            section = npcSection
    )
    default boolean muteCaveHorrors()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteCows",
            name = "Cows",
            description = "Mutes the sounds of Cows' moomoo",
            section = npcSection
    )
    default boolean muteCows()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteDemons",
            name = "Demons",
            description = "Mutes the sounds of various Demons",
            section = npcSection
    )
    default boolean muteDemons()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteDustDevils",
            name = "Dust Devils",
            description = "Mutes the sounds of Dust Devils",
            section = npcSection
    )
    default boolean muteDustDevils()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteWyverns",
            name = "Fossil Island Wyverns",
            description = "Mutes the sounds of Fossil Island Wyverns",
            section = npcSection
    )
    default boolean muteWyverns()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteJellies",
            name = "Jellies",
            description = "Mutes the sounds of Jellies",
            section = npcSection
    )
    default boolean muteJellies()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteNailBeasts",
            name = "Nail Beasts",
            description = "Mutes the sounds of Nail Beasts",
            section = npcSection
    )
    default boolean muteNailBeasts()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteNechryael",
            name = "Nechryael",
            description = "Mutes the sounds of Nechryaels",
            section = npcSection
    )
    default boolean muteNechryael()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteNightmare",
            name = "Nightmare",
            description = "Mutes the sound of the Nightmare's parry",
            section = npcSection
    )
    default boolean muteNightmare()
    {
        return true;
    }

    @ConfigItem(
            keyName = "mutePetSounds",
            name = "Pets",
            description = "Mutes the sounds of noise-making pets",
            section = npcSection
    )
    default boolean mutePetSounds()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteRandoms",
            name = "Random Events",
            description = "Mutes the sounds produced by random events",
            section = npcSection
    )
    default boolean muteRandoms()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteScarabs",
            name = "Scarab Swarm",
            description = "Mutes the sound of the Scarab swarm in Pyramid Plunder",
            section = npcSection
    )
    default boolean muteScarabs()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteSire",
            name = "Sire Spawns",
            description = "Mutes the sounds of the Abyssal Sire's spawns",
            section = npcSection
    )
    default boolean muteSire()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteSpectres",
            name = "Spectres",
            description = "Mutes the sounds of Aberrant and Deviant Spectres",
            section = npcSection
    )
    default boolean muteSpectres()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteTekton",
            name = "Tekton meteors",
            description = "Mutes the sound of Tekton's meteor attack",
            section = npcSection
    )
    default boolean muteTekton()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteCrier",
            name = "Town Crier",
            description = "Mutes the sounds of the Town Crier",
            section = npcSection
    )
    default boolean muteTownCrierSounds()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteVerzikP2",
            name = "Verzik P2",
            description = "Mutes Verziks P2 sound effect",
            section = npcSection
    )
    default boolean muteVerzikP2()
    {
        return true;
    }

    //------------------------------------------------------------//
    // Skilling
    //------------------------------------------------------------//
    @ConfigItem(
            keyName = "muteAlchemy",
            name = "Alchemy",
            description = "Mutes the sounds of Low and High Alchemy",
            section = skillingSection
    )
    default boolean muteAlchemy()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteChargeOrb",
            name = "Charge Orb",
            description = "Mutes the sounds of Charge Orb spells",
            section = skillingSection
    )
    default boolean muteChargeOrb()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteChopChop",
            name = "Chop Chop!",
            description = "Mutes the sound of the Dragon axe special",
            section = skillingSection
    )
    default boolean muteChopChop()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteDenseEssence",
            name = "Dense Essence",
            description = "Mutes the sound of chiseling Dense Essence",
            section = skillingSection
    )
    default boolean muteDenseEssence()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteFiremaking",
            name = "Firemaking",
            description = "Mutes the sound of burning and lighting logs",
            section = skillingSection
    )
    default boolean muteFiremaking()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteFishing",
            name = "Fishing",
            description = "Mutes the sound of Fishing",
            section = skillingSection
    )
    default boolean muteFishing()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteFletching",
            name = "Fletching",
            description = "Mutes the sound of Fletching",
            section = skillingSection
    )
    default boolean muteFletching()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteAreaOfEffectSpells",
            name = "Humidify",
            description = "Mutes humidify spell sound",
            section = skillingSection
    )
    default boolean muteAOESounds()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteMining",
            name = "Mining",
            description = "Mutes the sounds of Mining",
            section = skillingSection
    )
    default boolean muteMining()
    {
        return false;
    }

    @ConfigItem(
            keyName = "mutePickpocket",
            name = "Pickpocket",
            description = "Mutes the sound of the pickpocket plop",
            section = skillingSection
    )
    default boolean mutePickpocket()
    {
        return false;
    }

    @ConfigItem(
            keyName = "mutePickpocketStun",
            name = "Pickpocket Stun",
            description = "Mutes the sound of the pickpocket stun",
            section = skillingSection
    )
    default boolean mutePickpocketStun()
    {
        return false;
    }

    @ConfigItem(
            keyName = "plankMake",
            name = "Plank Make",
            description = "Mutes the sound of Plank Make",
            section = skillingSection
    )
    default boolean mutePlankMake()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteStringJewellery",
            name = "String Jewellery",
            description = "Mutes the sound of the String Jewellery spell",
            section = skillingSection
    )
    default boolean muteStringJewellery()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteWoodcutting",
            name = "Woodcutting",
            description = "Mutes the sound of Woodcutting",
            section = skillingSection
    )
    default boolean muteWoodcutting()
    {
        return true;
    }

    //------------------------------------------------------------//
    // Prayers
    //------------------------------------------------------------//
    @ConfigItem(
            keyName = "muteThickSkin",
            name = "Thick Skin",
            description = "Mutes the activation sound of Thick Skin",
            section = prayerSection,
            position = 1
    )
    default boolean muteThickSkin()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteBurstOfStrength",
            name = "Burst of Strength",
            description = "Mutes the activation sound of Burst of Strength",
            section = prayerSection,
            position = 2
    )
    default boolean muteBurstofStrength()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteClarityOfThought",
            name = "Clarity of Thought",
            description = "Mutes the activation sound of Clarity of Thought",
            section = prayerSection,
            position = 3
    )
    default boolean muteClarityOfThought()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteRockSkin",
            name = "Rock Skin",
            description = "Mutes the activation sound of Rock Skin",
            section = prayerSection,
            position = 4
    )
    default boolean muteRockSkin()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteSuperhumanStrength",
            name = "Superhuman Strength",
            description = "Mutes the activation sound of Superhuman Strength",
            section = prayerSection,
            position = 5
    )
    default boolean muteSuperhumanStrength()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteImprovedReflexes",
            name = "Improved Reflexes",
            description = "Mutes the activation sound of Improved Reflexes",
            section = prayerSection,
            position = 6
    )
    default boolean muteImprovedReflexes()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteRapidHeal",
            name = "Rapid Heal",
            description = "Mutes the activation sound of Rapid Heal",
            section = prayerSection,
            position = 7
    )
    default boolean muteRapidHeal()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteProtectItem",
            name = "Protect Item",
            description = "Mutes the activation sound of Protect Item",
            section = prayerSection,
            position = 8
    )
    default boolean muteProtectItem()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteHawkEye",
            name = "Hawk Eye",
            description = "Mutes the activation sound of Hawk Eye",
            section = prayerSection,
            position = 9
    )
    default boolean muteHawkEye()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteMysticLore",
            name = "Mystic Lore",
            description = "Mutes the activation sound of Mystic Lore",
            section = prayerSection,
            position = 10
    )
    default boolean muteMysticLore()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteSteelSkin",
            name = "Steel Skin",
            description = "Mutes the activation sound of Steel Skin",
            section = prayerSection,
            position = 11
    )
    default boolean muteSteelSkin()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteUltimateStrength",
            name = "Ultimate Strength",
            description = "Mutes the activation sound of Ultimate Strength",
            section = prayerSection,
            position = 12
    )
    default boolean muteUltimateStrength()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteIncredibleReflexes",
            name = "Incredible Reflexes",
            description = "Mutes the activation sound of Incredible Reflexes",
            section = prayerSection,
            position = 13
    )
    default boolean muteIncredibleReflexes()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteProtectFromMagic",
            name = "Protect from Magic",
            description = "Mutes the activation sound of Protect from Magic",
            section = prayerSection,
            position = 14
    )
    default boolean muteProtectFromMagic()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteProtectFromRange",
            name = "Protect from Range",
            description = "Mutes the activation sound of Protect from Range",
            section = prayerSection,
            position = 15
    )
    default boolean muteProtectFromRange()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteProtectFromMelee",
            name = "Protect from Melee",
            description = "Mutes the activation sound of Protect from Melee",
            section = prayerSection,
            position = 16
    )
    default boolean muteProtectFromMelee()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteEagleEye",
            name = "Eagle Eye",
            description = "Mutes the activation sound of Eagle Eye",
            section = prayerSection,
            position = 17
    )
    default boolean muteEagleEye()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteMysticMight",
            name = "Mystic Might",
            description = "Mutes the activation sound of Mystic Might",
            section = prayerSection,
            position = 18
    )
    default boolean muteMysticMight()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteRetribution",
            name = "Retribution",
            description = "Mutes the activation sound of Retribution",
            section = prayerSection,
            position = 19
    )
    default boolean muteRetribution()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteRedemption",
            name = "Redemption",
            description = "Mutes the activation sound of Redemption",
            section = prayerSection,
            position = 20
    )
    default boolean muteRedemption()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteSmite",
            name = "Smite",
            description = "Mutes the activation sound of Smite",
            section = prayerSection,
            position = 21
    )
    default boolean muteSmite()
    {
        return false;
    }

    @ConfigItem(
            keyName = "mutePreserve",
            name = "Preserve",
            description = "Mutes the activation sound of Preserve and Rapid Restore",
            section = prayerSection,
            position = 22
    )
    default boolean mutePreserve()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteChivalry",
            name = "Chivalry",
            description = "Mutes the activation sound of Chivalry",
            section = prayerSection,
            position = 23
    )
    default boolean muteChivalry()
    {
        return false;
    }

    @ConfigItem(
            keyName = "mutePiety",
            name = "Piety",
            description = "Mutes the activation sound of Piety",
            section = prayerSection,
            position = 24
    )
    default boolean mutePiety()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteRigour",
            name = "Rigour",
            description = "Mutes the activation sound of Rigour and Sharp Eye",
            section = prayerSection,
            position = 25
    )
    default boolean muteRigour()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteAugury",
            name = "Augury",
            description = "Mutes the activation sound of Augury and Mystic Will",
            section = prayerSection,
            position = 26
    )
    default boolean muteAugury()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteDeactivatePrayer",
            name = "Deactivate Prayer",
            description = "Mutes the prayer deactivation sound",
            section = prayerSection,
            position = 27
    )
    default boolean muteDeactivatePrayer()
    {
        return false;
    }

    //------------------------------------------------------------//
    // Misc
    //------------------------------------------------------------//
    @ConfigItem(
            keyName = "muteFishingExplosive",
            name = "Fishing Explosive",
            description = "Mutes the sound of Fishing Explosives",
            section = miscSection
    )
    default boolean muteFishingExplosive()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteHealOther",
            name = "Heal Other",
            description = "Mutes the sound of Heal Other and Heal Group",
            section = miscSection
    )
    default boolean muteHealOther()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteItemDrop",
            name = "Item Dropping",
            description = "Mutes the sound that occurs when dropping an item",
            section = miscSection
    )
    default boolean muteItemDrop()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteLevelUp",
            name = "Level Up",
            description = "Mutes the sound of the Level-up fireworks",
            section = miscSection
    )
    default boolean muteLevelUp()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteNPCContact",
            name = "NPC Contact",
            description = "Mutes the sound of NPC Contact",
            section = miscSection
    )
    default boolean muteNPCContact()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteOthersAreaSounds",
            name = "Others' Area Sounds",
            description = "Mutes other players' area sounds",
            section = miscSection
    )
    default boolean muteOthersAreaSounds()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteSnowballs",
            name = "Snowballs",
            description = "Mutes the sounds of snowballs being thrown",
            section = miscSection
    )
    default boolean muteSnowballSounds()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteTeleother",
            name = "Teleother",
            description = "Mutes the sound of the Teleother spell",
            section = miscSection
    )
    default boolean muteTeleother()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteTeleport",
            name = "Teleport",
            description = "Mutes the teleport vwoop sound",
            section = miscSection
    )
    default boolean muteTeleport()
    {
        return false;
    }

    @ConfigItem(
            keyName = "muteTeleportOthers",
            name = "Teleport (only others)",
            description = "Mutes the sound of other players' teleports",
            section = miscSection
    )
    default boolean muteTeleportOthers() {
        return true;
    }

    @ConfigItem(
            keyName = "muteWhack",
            name = "Whack",
            description = "Mutes the Rubber chicken and Stale baguette whack sound",
            section = miscSection
    )
    default boolean muteRubberChickenSounds()
    {
        return true;
    }

    @ConfigItem(
            keyName = "muteObelisk",
            name = "Wilderness Obelisk",
            description = "Mutes the sounds of the Wilderness Obelisk",
            section = miscSection
    )
    default boolean muteObelisk()
    {
        return true;
    }

    //------------------------------------------------------------//
    // No section
    //------------------------------------------------------------//
    @ConfigItem(
            keyName = "soundsToMute",
            name = "Muted Sounds",
            description = "Enter IDs of the sounds you wish to mute",
            position = 28
    )
    default String soundsToMute()
    {
        return "";
    }

    @ConfigItem(
            keyName = "debug",
            name = "Debug Sounds",
            description = "Marks the source of the sound and displays the sound ID",
            position = 29
    )
    default boolean debug()
    {
        return false;
    }
}
