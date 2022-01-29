package net.runelite.client.plugins.corpboost.spots;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum StunDwhSpots {
    STUN_DWH_1(new WorldPoint(2997, 4387, 2)),
    STUN_DWH_2(new WorldPoint(2995, 4387, 2)),
    STUN_DWH_3(new WorldPoint(2993, 4387, 2));

    @Getter
    private static final List<WorldPoint> stunDwhSpots = new ArrayList<>();

    static
    {
        for (StunDwhSpots stunDwhSpot : values())
        {
            stunDwhSpots.addAll(Arrays.asList(stunDwhSpot.spots));
        }
    }

    private final WorldPoint[] spots;

    StunDwhSpots(WorldPoint... spots)
    {
        this.spots = spots;
    }
}
