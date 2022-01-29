package net.runelite.client.plugins.corpboost.spots;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum StunHealerSpots {
    STUN_HEALER(new WorldPoint(2992, 4384, 2));

    @Getter
    private static final List<WorldPoint> stunHealerSpots = new ArrayList<>();

    static
    {
        for (StunHealerSpots stunHealerSpot : values())
        {
            stunHealerSpots.addAll(Arrays.asList(stunHealerSpot.spots));
        }
    }

    private final WorldPoint[] spots;

    StunHealerSpots(WorldPoint... spots)
    {
        this.spots = spots;
    }
}
