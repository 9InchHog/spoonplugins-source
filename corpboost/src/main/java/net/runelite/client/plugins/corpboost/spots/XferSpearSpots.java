package net.runelite.client.plugins.corpboost.spots;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum XferSpearSpots {
    XFER_SPEAR_1(new WorldPoint(2998,4384,2)),
    XFER_SPEAR_2(new WorldPoint(2998,4382,2));

    @Getter
    private static final List<WorldPoint> xferSpearSpots = new ArrayList<>();

    static
    {
        for (XferSpearSpots xferSpearSpot : values())
        {
            xferSpearSpots.addAll(Arrays.asList(xferSpearSpot.spots));
        }
    }

    private final WorldPoint[] spots;

    XferSpearSpots(WorldPoint... spots)
    {
        this.spots = spots;
    }
}
