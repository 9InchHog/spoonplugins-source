/*
 * Copyright (c) 2019, Lucas <https://github.com/lucwousin>
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
package net.runelite.client.plugins.alchemicalhydra;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("betterHydra")
public interface HydraConfig extends Config
{
	@ConfigSection(
			name = "Hydra",
			description = "Configuration for Hydra options",
			position = 0,
			closedByDefault = true
	)
	String hydraSection = "Hydra";

	@ConfigSection(
			name = "Fountain",
			description = "Configuration for fountain options",
			position = 1,
			closedByDefault = true
	)
	String fountainSection = "Fountain";

	@ConfigSection(
			name = "Lightning",
			description = "Configuration for lightning options",
			position = 2,
			closedByDefault = true
	)
	String lightningSection = "Lightning";

	@ConfigItem(
		keyName = "counting",
		name = "Prayer helper",
		description = "Basically everything this plugin is known for. Also has attacks between specs and poison overlay. Shouldn't NOT use this tbh",
		position = 1,
		section = hydraSection
	)
	default boolean counting()
	{
		return true;
	}

	@ConfigItem(
		keyName = "fountain",
		name = "Fountain helper",
		description = "Indicates if hydra is on a fountain",
		position = 2,
		section = fountainSection
	)
	default boolean fountain()
	{
		return true;
	}

	@ConfigItem(
		keyName = "stun",
		name = "Stun timer",
		description = "Shows when you can walk in fire phase",
		position = 3,
		section = hydraSection
	)
	default boolean stun()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		keyName = "safeCol",
		name = "Safe colour",
		description = "Colour overlay will be when there's >2 attacks left",
		position = 4,
		section = hydraSection
	)
	default Color safeCol()
	{
		return new Color(0, 156, 0, 156);
	}

	@Alpha
	@ConfigItem(
		keyName = "medCol",
		name = "Medium colour",
		description = "Colour overlay will be when a input is coming up",
		position = 5,
		section = hydraSection
	)
	default Color medCol()
	{
		return new Color(200, 156, 0, 156);
	}

	@Alpha
	@ConfigItem(
		keyName = "badCol",
		name = "Bad colour",
		description = "Colour overlay will be when you have to do something NOW",
		position = 6,
		section = hydraSection
	)
	default Color badCol()
	{
		return new Color(156, 0, 0, 156);
	}

	@Alpha
	@ConfigItem(
		keyName = "poisonBorderCol",
		name = "Poison border colour",
		description = "Colour the edges of the area highlighted by poison special will be",
		position = 7,
		section = hydraSection
	)
	default Color poisonBorderCol()
	{
		return new Color(255, 0, 0, 100);
	}

	@Alpha
	@ConfigItem(
		keyName = "poisonCol",
		name = "Poison colour",
		description = "Colour the fill of the area highlighted by poison special will be",
		position = 8,
		section = hydraSection
	)
	default Color poisonCol()
	{
		return new Color(255, 0, 0, 50);
	}

	@Alpha
	@ConfigItem(
		keyName = "fountainColA",
		name = "Fountain colour (not on top)",
		description = "Fountain colour (not on top)",
		position = 9,
		section = fountainSection
	)
	default Color fountainColA()
	{
		return new Color(255, 0, 0, 100);
	}

	@Alpha
	@ConfigItem(
		keyName = "fountainColB",
		name = "Fountain colour (on top)",
		description = "Fountain colour (on top)",
		position = 10,
		section = fountainSection
	)
	default Color fountainColB()
	{
		return new Color(0, 255, 0, 100);
	}

	//SpoonLite
	@ConfigItem(
			keyName = "hydraImmunityOutline",
			name = "Hydra immunity outline",
			description = "Highlights hydra when it is not weakened",
			position = 11,
			section = hydraSection
	)
	default ImmunityMode hydraImmunityOutline()
	{
		return ImmunityMode.OFF;
	}

	@Alpha
	@ConfigItem(
			keyName = "hydraImmunityColor",
			name = "Hydra immunity color",
			description = "Adjusts the color of the hydra outline when its immune",
			position = 12,
			section = hydraSection
	)
	default Color hydraImmunityColor() { return Color.WHITE; }

	@Range(min = 1, max = 10)
	@ConfigItem(
			keyName = "hydraImmunityWidth",
			name = "Hydra immunity width",
			description = "Adjust the width of the hydra outline when its immune",
			position = 13,
			section = hydraSection
	)
	default int hydraImmunityWidth()
	{
		return 2;
	}

	@Range(min = 0, max = 4)
	@ConfigItem(
			keyName = "hydraImmunityGlow",
			name = "Hydra immunity glow",
			description = "Specify between 0-4 how much of the model outline should be faded",
			position = 14,
			section = hydraSection
	)
	default int hydraImmunityGlow()
	{
		return 2;
	}

	@ConfigItem(
			keyName = "lightningHighlight",
			name = "Lightning highlight",
			description = "Highlights the lightning during blue/electric phase",
			position = 15,
			section = lightningSection
	)
	default LightningMode lightningHighlight()
	{
		return LightningMode.OFF;
	}

	@Alpha
	@ConfigItem(
			keyName = "lightningColor",
			name = "Lightning color",
			description = "Adjusts the color of the lightning highlight",
			position = 16,
			section = lightningSection
	)
	default Color lightningColor() { return Color.WHITE; }

	@Range(min = 1, max = 10)
	@ConfigItem(
			keyName = "lightningWidth",
			name = "Lightning width",
			description = "Adjust the width of the lightning",
			position = 17,
			section = lightningSection
	)
	default int lightningWidth()
	{
		return 2;
	}

	@Range(min = 0, max = 4)
	@ConfigItem(
			keyName = "lightningGlow",
			name = "Lightning glow",
			description = "Adjust the glow of the lightning when outline is selected",
			position = 18,
			section = lightningSection
	)
	default int lightningGlow()
	{
		return 4;
	}

	@ConfigItem(
			keyName = "showHpUntilPhaseChange",
			name = "Show HP until phase change",
			description = "Overlay hydra with hp remaining until next phase change.",
			position = 19,
			section = hydraSection
	)
	default boolean showHpUntilPhaseChange()
	{
		return false;
	}

	@ConfigItem(
			keyName = "attackChange",
			name = "Attack change sound notification",
			description = "Plays a sound when hydra switches its attack style.",
			position = 20,
			section = hydraSection
	)
	default boolean attackChange()
	{
		return false;
	}

	@Range(min = 1, max = 100)
	@ConfigItem(
			keyName = "attackChangeVolume",
			name = "Attack change sound volume",
			description = "Ding ding",
			position = 21,
			section = hydraSection
	)
	default int attackChangeVolume()
	{
		return 20;
	}

	@ConfigItem(
			keyName = "ventTicks",
			name = "Vent Ticks",
			description = "Display ticks until vent shoots up again",
			position = 22,
			section = fountainSection
	)
	default boolean ventTicks()
	{
		return false;
	}

	public enum ImmunityMode {
		OFF,
		HULL,
		OUTLINE
	}

	public enum LightningMode {
		OFF,
		OUTLINE,
		TRUE_LOCATION
	}
}
