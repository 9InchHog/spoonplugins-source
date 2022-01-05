package net.runelite.client.plugins.pvpplayerindicators;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PvPPlayerNameLocation
{

	DISABLED("Disabled"),
	ABOVE_HEAD("Above head"),
	MODEL_CENTER("Center of model"),
	MODEL_RIGHT("Right of model");

	private final String name;

	@Override
	public String toString()
	{
		return name;
	}
}