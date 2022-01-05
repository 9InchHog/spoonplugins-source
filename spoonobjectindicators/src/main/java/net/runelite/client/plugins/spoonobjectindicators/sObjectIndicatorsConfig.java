/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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

package net.runelite.client.plugins.spoonobjectindicators;

import java.awt.Color;

import net.runelite.client.config.*;

@ConfigGroup("objectindicators")
public interface sObjectIndicatorsConfig extends Config
{
	@ConfigItem(
			keyName = "objectMarkerRenderStyle",
			name = "Highlight Style",
			description = "Highlight setting",
			position = 0
	)
	default RenderStyle objectMarkerRenderStyle()
	{
		return RenderStyle.AREA;
	}

	@Alpha
	@ConfigItem(
		keyName = "markerColor",
		name = "Marker color",
		description = "Configures the color of object marker",
		position = 1
	)
	default Color markerColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
		keyName = "rememberObjectColors",
		name = "Remember color per object",
		description = "Color objects using the color from time of marking",
		position = 2
	)
	default boolean rememberObjectColors()
	{
		return false;
	}

	@Range(
			min = 0,
			max = 255
	)
	@ConfigItem(
			keyName = "opacity",
			name = "Opacity",
			description = "The opacity of ground markers from 0 to 255 (0 being black and 255 being transparent)",
			position = 3
	)
	default int opacity()
	{
		return 50;
	}

	@ConfigItem(
			position = 4,
			keyName = "outlineWidth",
			name = "Outline width",
			description = "Configures the amount of pixels to outline the object with",
			section = "renderStyleSection"
	)
	default int outlineWidth() { return 2; }

	@ConfigItem(
			position = 5,
			keyName = "outlineFeather",
			name = "Outline feather",
			description = "Specify between 0-4 how much of the model outline should be faded"
	)
	@Range(min = 0, max = 4)
	default int outlineFeather()
	{
		return 0;
	}
}
