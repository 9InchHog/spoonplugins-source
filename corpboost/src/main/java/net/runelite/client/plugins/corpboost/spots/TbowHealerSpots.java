package net.runelite.client.plugins.corpboost.spots;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum TbowHealerSpots {
    TBOW_HEALER_1(new WorldPoint(2989, 4382, 2));

    @Getter
    private static final List<WorldPoint> tbowHealerSpots = new ArrayList<>();

    static
    {
        for (TbowHealerSpots tbowHealerSpot : values())
        {
            tbowHealerSpots.addAll(Arrays.asList(tbowHealerSpot.spots));
        }
    }

    private final WorldPoint[] spots;

    TbowHealerSpots(WorldPoint... spots)
    {
        this.spots = spots;
    }
}
