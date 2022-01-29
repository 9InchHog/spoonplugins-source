package net.runelite.client.plugins.corpboost.spots;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum XferHealerSpots {
    XFER_HEALER(new WorldPoint(2995, 4381, 2));

    @Getter
    private static final List<WorldPoint> xferHealerSpots = new ArrayList<>();

    static
    {
        for (XferHealerSpots xferHealerSpot : values())
        {
            xferHealerSpots.addAll(Arrays.asList(xferHealerSpot.spots));
        }
    }

    private final WorldPoint[] spots;

    XferHealerSpots(WorldPoint... spots)
    {
        this.spots = spots;
    }
}
