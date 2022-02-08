/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Modified by farhan1666
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
package net.runelite.client.plugins.aoe;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("spoonaoe")
public interface AoeConfig extends Config {
	/*@AllArgsConstructor
	enum VorkathMode {
		BOMBS(VORKATH_BOMB),
		POOLS(VORKATH_POISON_POOL),
		SPAWN(VORKATH_SPAWN),
		FIRES(VORKATH_TICK_FIRE); // full auto ratatat

		private final AoeProjectileInfo info;
		static VorkathMode of(AoeProjectileInfo info) {
			for (VorkathMode m : values()) {
				if (m.info == info) {
					return m;
				}
			}
			throw new EnumConstantNotPresentException(AoeProjectileInfo.class, info.toString());
		}
	}*/

	@ConfigSection(
			name = "Display Settings",
			description = "Display settings",
			position = 0,
			closedByDefault = true
	)
	public static final String displaySection = "display";

	@ConfigSection(
			name = "Chambers of Xeric",
			description = "Chambers of Xeric settings",
			position = 1,
			closedByDefault = true
	)
	public static final String coxSection = "cox";

	@ConfigSection(
			name = "Theatre of Blood",
			description = "Theatre of Blood settings",
			position = 2,
			closedByDefault = true
	)
	public static final String tobSection = "tob";

	@ConfigSection(
			name = "Other Section",
			description = "Other pvm settings",
			position = 3,
			closedByDefault = true
	)
	public static final String otherSection = "other";

	//Display Section
	@ConfigItem(
			keyName = "Color",
			name = "AoE Color",
			description = "Color Picker",
			position = 2,
			section = displaySection
	)
	default Color AoEColor() { return Color.CYAN; }

	@Alpha
	@ConfigItem(
			keyName = "AoEFillColor",
			name = "AoE Fill Color",
			description = "Color Picker for Fill",
			position = 3,
			section = displaySection
	)
	default Color AoEFillColor() { return new Color(0, 255, 255, 20); }

	@Range(min = 0, max = 5)
	@ConfigItem(
			keyName = "aoeThiCC",
			name = "Outline Width",
			description = "Configures the width of AoE projectiles",
			position = 4,
			section = displaySection
	)
	default int aoeThiCC() { return 1; }

	@ConfigItem(
			keyName = "delay",
			name = "Fade delay",
			description = "Configures the amount of time in milliseconds that the warning lingers for after the projectile has touched the ground",
			position = 5,
			section = displaySection
	)
	default int delay() { return 300; }

	@ConfigItem(
			keyName = "fade",
			name = "Fade Warnings",
			description = "Configures whether or not AoE Projectile Warnings fade over time",
			position = 6,
			section = displaySection,
			disabledBy = "reverseFade"
	)
	default boolean isFadeEnabled() { return true; }

	@ConfigItem(
			keyName = "reverseFade",
			name = "Fade Warnings (Reverse)",
			description = "Reverses the fade effect",
			position = 7,
			section = displaySection,
			disabledBy = "fade"
	)
	default boolean isReverseFadeEnabled() { return false; }

	@ConfigItem(
			keyName = "aoeTicks",
			name = "AoE Ticks",
			description = "Ticks until the AoE lands",
			position = 8,
			section = displaySection
	)
	default boolean aoeTicks() { return true; }

	@ConfigItem(
			keyName = "fontType",
			name = "Runelite Font",
			description = "Changes the font type to Runelite's font",
			position = 8,
			section = displaySection
	)
	default boolean fontType() { return true; }

	@ConfigItem(
			keyName = "textSize",
			name = "Ticks Size",
			description = "Changes the size of the AoE ticks",
			position = 10,
			section = displaySection
	)
	default int textSize() { return 14; }

	@ConfigItem(
			keyName = "raveHighlights",
			name = "Rave",
			description = ":catJam:",
			position = 11,
			section = displaySection
	)
	default raveMode raveHighlights() { return raveMode.OFF; }

	//Cox Section
	@ConfigItem(
			keyName = "olmAcid",
			name = "Olm Acid Trail",
			description = "Shows the acid tiles at Olm",
			section = coxSection
	)
	default boolean olmAcid() { return false; }

	@ConfigItem(
			keyName = "olmBurning",
			name = "Olm Burning",
			description = "Shows which mfer is about to get burnt",
			section = coxSection
	)
	default boolean olmBurning() { return false; }

	@ConfigItem(
			keyName = "olmCrystal",
			name = "Olm Crystal Trail",
			description = "Shows the crystal spikes and the crystal trail",
			section = coxSection
	)
	default boolean olmCrystal() { return false; }

	@ConfigItem(
			keyName = "olmFallingCrystal",
			name = "Olm Falling Crystals",
			description = "Shows the AoE of the falling crystals between phases",
			section = coxSection
	)
	default boolean olmFallingCrystal() { return false; }

	@ConfigItem(
			keyName = "crystalSize",
			name = "Olm Falling Crystals Tile",
			description = "Makes the falling crystal single tile instead of a 3x3",
			section = coxSection
	)
	default boolean crystalSize() { return false; }

	@ConfigItem(
			keyName = "olmFlameWall",
			name = "Olm Flame Wall",
			description = "Highlights the first row of the flame walls",
			section = coxSection
	)
	default boolean olmFlameWall() { return false; }

	@ConfigItem(
			keyName = "bombDisplay",
			name = "Olm Bombs",
			description = "Display a timer and colour-coded AoE for Olm's crystal-phase bombs.",
			section = coxSection
	)
	default bombMode bombDisplay() { return bombMode.TICKS; }

	@ConfigItem(
			keyName = "lightning",
			name = "Olm Lightning Trails",
			description = "Show Lightning Trails",
			section = coxSection
	)
	default boolean LightningTrail() { return true; }

	@ConfigItem(
			keyName = "lizardmanaoe",
			name = "Lizardman Shamans",
			description = "Configures whether or not AoE Projectile Warnings for Lizardman Shamans is displayed",
			section = coxSection
	)
	default boolean isShamansEnabled() { return true; }

	@ConfigItem(
			keyName = "icedemon",
			name = "Ice Demon",
			description = "Configures whether or not AoE Projectile Warnings for Ice Demon is displayed",
			section = coxSection
	)
	default boolean isIceDemonEnabled() { return true; }

	@ConfigItem(
			keyName = "vasa",
			name = "Vasa",
			description = "Configures whether or not AoE Projectile Warnings for Vasa is displayed",
			section = coxSection
	)
	default boolean isVasaEnabled() { return true; }

	@ConfigItem(
			keyName = "tekton",
			name = "Tekton",
			description = "Configures whether or not AoE Projectile Warnings for Tekton is displayed",
			section = coxSection
	)
	default boolean isTektonEnabled() { return true; }

	//Tob section
	@ConfigItem(
			keyName = "isXarpusEnabled",
			name = "Xarpus",
			description = "Configures whether or not AOE Projectile Warnings for Xarpus are displayed",
			section = tobSection
	)
	default boolean isXarpusEnabled() { return true; }

	@ConfigItem(
			keyName = "verzikPurple",
			name = "Verzik Purple Crab",
			description = "Configures if Verzik purple crab tile markers are displayed",
			section = tobSection
	)
	default boolean isVerzikPurpleEnabled() { return false; }

	@ConfigItem(
			keyName = "verzikRange",
			name = "Verzik P2 Range Attack",
			description = "Configures if Verzik's p2 range attack tile markers are displayed",
			section = tobSection
	)
	default boolean isVerzikRangeEnabled() { return false; }

	@ConfigItem(
			keyName = "verzikHM",
			name = "Verzik HM Rocks",
			description = "Configures if HM Verzik's p1 falling rock attack tile markers are displayed",
			section = tobSection
	)
	default boolean isVerzikHMEnabled() { return false; }

	//Other section
	@ConfigItem(
			keyName = "addyDrags",
			name = "Addy Drags",
			description = "Show Bad Areas",
			section = otherSection
	)
	default boolean addyDrags() { return true; }

	@ConfigItem(
			keyName = "cerbFire",
			name = "Cerberus Fire",
			description = "Configures if Cerberus fire tile markers are displayed",
			section = otherSection
	)
	default boolean isCerbFireEnabled() { return true; }

	@ConfigItem(
			keyName = "chaosfanatic",
			name = "Chaos Fanatic",
			description = "Configures whether or not AoE Projectile Warnings for Chaos Fanatic are displayed",
			section = otherSection
	)
	default boolean isChaosFanaticEnabled() { return true; }

	@ConfigItem(
			keyName = "corp",
			name = "Corporeal Beast",
			description = "Configures whether or not AoE Projectile Warnings for the Corporeal Beast are displayed",
			section = otherSection
	)
	default boolean isCorpEnabled() { return true; }

	@ConfigItem(
			keyName = "darkCore",
			name = "Corporeal Beast - Dark Core",
			description = "Configures whether or not AoE Projectile Warnings for the Corporeal Beast's dark core is displayed",
			section = otherSection
	)
	default boolean isDarkCoreEnabled() { return true; }

	@ConfigItem(
			keyName = "archaeologistaoe",
			name = "Crazy Archaeologist",
			description = "Configures whether or not AoE Projectile Warnings for Archaeologist is displayed",
			section = otherSection
	)
	default boolean isArchaeologistEnabled() { return true; }

	@ConfigItem(
			keyName = "demonicGorilla",
			name = "Demonic Gorilla",
			description = "Configures if Demonic Gorilla boulder tile markers are displayed",
			section = otherSection
	)
	default boolean isDemonicGorillaEnabled() {return true;}

	@ConfigItem(
			keyName = "drake",
			name = "Drakes Breath",
			description = "Configures if Drakes Breath tile markers are displayed",
			section = otherSection
	)
	default boolean isDrakeEnabled() { return true; }

	@ConfigItem(
			keyName = "galvek",
			name = "Galvek",
			description = "Configures whether or not AoE Projectile Warnings for Galvek are displayed",
			section = otherSection
	)
	default boolean isGalvekEnabled() { return true; }

	@ConfigItem(
			keyName = "gargboss",
			name = "Gargoyle Boss",
			description = "Configs whether or not AoE Projectile Warnings for Dawn/Dusk are displayed",
			section = otherSection
	)
	default boolean isGargBossEnabled() { return true; }

	@ConfigItem(
			keyName = "vetion",
			name = "Vet'ion",
			description = "Configures whether or not AoE Projectile Warnings for Vet'ion are displayed",
			section = otherSection
	)
	default boolean isVetionEnabled() { return true; }

	@ConfigItem(
			keyName = "wintertodt",
			name = "Wintertodt Snow Fall",
			description = "Configures whether or not AOE Projectile Warnings for the Wintertodt snow fall are displayed",
			section = otherSection
	)
	default boolean isWintertodtEnabled() { return true; }

	@ConfigItem(
			keyName = "vorkathBombs",
			name = "Vorkath Bombs",
			description = "Shows the AoE for the bombs that always happen when you look away",
			section = otherSection
	)
	default boolean vorkathBombs() { return true; }

	@ConfigItem(
			keyName = "vorkathPools",
			name = "Vorkath Pools",
			description = "Shows the AoE for the acid pools",
			section = otherSection
	)
	default boolean vorkathPools() { return true; }

	@ConfigItem(
			keyName = "vorkathSpawn",
			name = "Vorkath Spawn",
			description = "Shows the AoE for where the spawn is going to land",
			section = otherSection
	)
	default boolean vorkathSpawn() { return true; }

	@ConfigItem(
			keyName = "vorkathRapid",
			name = "Vorkath Rapid Fire",
			description = "Shows the AoE for where the full auto ratatat",
			section = otherSection
	)
	default boolean vorkathRapid() { return true; }

	enum bombMode {
		OFF, SECONDS, TICKS
	}

	enum raveMode {
		OFF, FLOW, RAVE
	}
}