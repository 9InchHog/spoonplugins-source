package net.runelite.client.plugins.socket.plugins.socketworldhopper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.http.api.worlds.WorldRegion;

@RequiredArgsConstructor
enum RegionFilterMode
{
    AUSTRALIA(WorldRegion.AUSTRALIA),
    GERMANY(WorldRegion.GERMANY),
    UNITED_KINGDOM(WorldRegion.UNITED_KINGDOM)
            {
                @Override
                public String toString()
                {
                    return "U.K.";
                }
            },
    UNITED_STATES(WorldRegion.UNITED_STATES_OF_AMERICA)
            {
                @Override
                public String toString()
                {
                    return "USA";
                }
            };

    @Getter
    private final WorldRegion region;

    static RegionFilterMode of(WorldRegion region)
    {
        switch (region)
        {
            case UNITED_STATES_OF_AMERICA:
                return UNITED_STATES;
            case UNITED_KINGDOM:
                return UNITED_KINGDOM;
            case AUSTRALIA:
                return AUSTRALIA;
            case GERMANY:
                return GERMANY;
            default:
                throw new IllegalStateException();
        }
    }
}