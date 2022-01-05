package net.runelite.client.plugins.corpboost.spots;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.coords.WorldPoint;

public enum DwhAltSpots {
    DWH_ALT_1(new WorldPoint(2997, 4381, 2)),
    DWH_ALT_2(new WorldPoint(2998, 4385, 2)),
    DWH_ALT_3(new WorldPoint(2995, 4387, 2));

    @Getter
    private static final List<WorldPoint> dwhAltSpots = new ArrayList<>();

    static
    {
        for (DwhAltSpots dwhAltSpot : values())
        {
            dwhAltSpots.addAll(Arrays.asList(dwhAltSpot.spots));
        }
    }

    private final WorldPoint[] spots;

    DwhAltSpots(WorldPoint... spots)
    {
        this.spots = spots;
    }
}
