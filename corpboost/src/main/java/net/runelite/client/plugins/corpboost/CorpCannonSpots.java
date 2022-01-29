package net.runelite.client.plugins.corpboost;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

enum CorpCannonSpots {
    CANNON_1(new WorldPoint(2992, 4379, 2));

    @Getter
    private static final List<WorldPoint> corpCannonSpots = new ArrayList<>();

    static
    {
        for (CorpCannonSpots corpCannonSpot : values())
        {
            corpCannonSpots.addAll(Arrays.asList(corpCannonSpot.spots));
        }
    }

    private final WorldPoint[] spots;

    CorpCannonSpots(WorldPoint... spots)
    {
        this.spots = spots;
    }
}
