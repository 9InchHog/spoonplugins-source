package net.runelite.client.plugins.spoongroundmarkers;

import java.awt.Color;
import lombok.Value;
import net.runelite.api.coords.WorldPoint;

/**
 * Used to denote marked tiles and their colors.
 * Note: This is not used for serialization of ground markers; see {@link BrushMarkerPoint}
 */
@Value
class ColorTileMarker
{
    private WorldPoint worldPoint;
    private Color color;
}