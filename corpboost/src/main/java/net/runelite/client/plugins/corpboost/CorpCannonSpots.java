package net.runelite.client.plugins.corpboost;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

enum CorpCannonSpots {
    CANNON_1(new WorldPoint(2994, 4379, 2)),
    CANNON_2(new WorldPoint(2996, 4381, 2)),
    CANNON_3(new WorldPoint(2992, 4379, 2)),
    CANNON_4(new WorldPoint(2990, 4381, 2)),
    CANNON_5(new WorldPoint(2987, 4383, 2)),
    CANNON_6(new WorldPoint(2992, 4385, 2));

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
