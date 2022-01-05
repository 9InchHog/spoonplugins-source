package net.runelite.client.plugins.corpboost.spots;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum CustomerSpot {
    CUSTOMER(new WorldPoint(2993, 4381, 2));

    @Getter
    private static final List<WorldPoint> customerSpots = new ArrayList<>();

    static
    {
        for (CustomerSpot customerSpot : values())
        {
            customerSpots.addAll(Arrays.asList(customerSpot.spots));
        }
    }

    private final WorldPoint[] spots;

    CustomerSpot(WorldPoint... spots)
    {
        this.spots = spots;
    }
}
