package net.runelite.client.plugins.corpboost.spots;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SpearAltSpots {
    SPEAR_ALT_1(new WorldPoint(2992,4384,2)),
    SPEAR_ALT_2(new WorldPoint(2992,4382,2));

    @Getter
    private static final List<WorldPoint> spearAltSpots = new ArrayList<>();

    static
    {
        for (SpearAltSpots spearAltSpot : values())
        {
            spearAltSpots.addAll(Arrays.asList(spearAltSpot.spots));
        }
    }

    private final WorldPoint[] spots;

    SpearAltSpots(WorldPoint... spots)
    {
        this.spots = spots;
    }
}
