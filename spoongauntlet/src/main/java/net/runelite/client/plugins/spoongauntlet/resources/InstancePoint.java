package net.runelite.client.plugins.spoongauntlet.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;

@EqualsAndHashCode
@AllArgsConstructor
@Data
public class InstancePoint
{
    private int x;
    private int y;
    private int regionID;
    private int plane;

    public Point getPoint()
    {
        return new Point(x, y);
    }

    int distanceToWorldPoint(Client client, WorldPoint other)
    {
        WorldPoint thisWp = WorldPoint.fromRegion(regionID, x, y, plane);
        return Math.max(Math.abs(thisWp.getX() - other.getX()), Math.abs(thisWp.getY() - other.getY()));
    }
}
