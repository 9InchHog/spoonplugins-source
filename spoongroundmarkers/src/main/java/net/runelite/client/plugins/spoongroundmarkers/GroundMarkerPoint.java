package net.runelite.client.plugins.spoongroundmarkers;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(exclude = {"group"})
class GroundMarkerPoint {
	private int regionId;
	private int regionX;
	private int regionY;
	private int z;
	private int group;
	private String label;
}