package net.runelite.client.plugins.corpboost.spots;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SpearHealerSpots {
    SPEAR_HEALER_1(new WorldPoint(2995, 4381, 2));

    @Getter
    private static final List<WorldPoint> spearHealerSpots = new ArrayList<>();

    static
    {
        for (SpearHealerSpots spearHealerSpot : values())
        {
            spearHealerSpots.addAll(Arrays.asList(spearHealerSpot.spots));
        }
    }

    private final WorldPoint[] spots;

    SpearHealerSpots(WorldPoint... spots)
    {
        this.spots = spots;
    }
}
