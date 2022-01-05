package net.runelite.client.plugins.corpboost.spots;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum StunnerSpot {
    STUNNER(new WorldPoint(2992, 4386, 2));

    @Getter
    private static final List<WorldPoint> stunnerSpots = new ArrayList<>();

    static
    {
        for (StunnerSpot stunnerspot : values())
        {
            stunnerSpots.addAll(Arrays.asList(stunnerspot.spots));
        }
    }

    private final WorldPoint[] spots;

    StunnerSpot(WorldPoint... spots)
    {
        this.spots = spots;
    }
}
