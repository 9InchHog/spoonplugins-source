package net.runelite.client.plugins.corpboost.spots;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum XferDwhSpots {
    XFER_DWH_1(new WorldPoint(2998,4385,2)),
    XFER_DWH_2(new WorldPoint(2998,4383,2)),
    XFER_DWH_3(new WorldPoint(2997, 4381, 2));


    @Getter
    private static final List<WorldPoint> xferDwhSpots = new ArrayList<>();

    static
    {
        for (XferDwhSpots xferDwhSpot : values())
        {
            xferDwhSpots.addAll(Arrays.asList(xferDwhSpot.spots));
        }
    }

    private final WorldPoint[] spots;

    XferDwhSpots(WorldPoint... spots)
    {
        this.spots = spots;
    }
}
