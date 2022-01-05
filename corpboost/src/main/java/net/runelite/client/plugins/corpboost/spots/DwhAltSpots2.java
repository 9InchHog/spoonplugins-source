package net.runelite.client.plugins.corpboost.spots;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DwhAltSpots2 {
    DWH_ALT_1(new WorldPoint(2997,4387,2)),
    DWH_ALT_2(new WorldPoint(2998,4383,2)),
    DWH_ALT_3(new WorldPoint(2993, 4387, 2));


    @Getter
    private static final List<WorldPoint> dwhAltSpots2 = new ArrayList<>();

    static
    {
        for (DwhAltSpots2 dwhAltSpot2 : values())
        {
            dwhAltSpots2.addAll(Arrays.asList(dwhAltSpot2.spots));
        }
    }

    private final WorldPoint[] spots;

    DwhAltSpots2(WorldPoint... spots)
    {
        this.spots = spots;
    }
}
