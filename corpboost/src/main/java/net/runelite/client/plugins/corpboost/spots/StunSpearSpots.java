package net.runelite.client.plugins.corpboost.spots;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum StunSpearSpots {
    STUN_SPEAR_1(new WorldPoint(2996,4387,2)),
    STUN_SPEAR_2(new WorldPoint(2994,4387,2));

    @Getter
    private static final List<WorldPoint> stunSpearSpots = new ArrayList<>();

    static
    {
        for (StunSpearSpots stunSpearSpot : values())
        {
            stunSpearSpots.addAll(Arrays.asList(stunSpearSpot.spots));
        }
    }

    private final WorldPoint[] spots;

    StunSpearSpots(WorldPoint... spots)
    {
        this.spots = spots;
    }
}
