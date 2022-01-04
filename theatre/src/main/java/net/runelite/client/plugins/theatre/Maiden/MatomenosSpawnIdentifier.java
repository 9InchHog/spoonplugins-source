package net.runelite.client.plugins.theatre.Maiden;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.google.common.collect.ArrayListMultimap;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public enum MatomenosSpawnIdentifier {
    N1("N1", 216689714834258L),
    N2("N2", 216689719029074L),
    N3("N3", 216689723223890L),
    N4_WALL("N4 (1)", 216689727418706L),
    N4("N4 (2)", 216689727385934L),
    S1("S1", 216689714473770L),
    S2("S2", 216689718668586L),
    S3("S3", 216689722863402L),
    S4_WALL("S4 (1)", 216689727058218L),
    S4("S4 (2)", 216689727090990L);

    private final String key;
    private final long hash;
    private static final ArrayListMultimap<String, Integer> identifierMultiMap = ArrayListMultimap.create();

    MatomenosSpawnIdentifier(String key, long hash) {
        this.key = key;
        this.hash = hash;
    }

    @Nullable
    public static Pair<String, Boolean> of(Client client, @Nullable NPC npc) {
        if (npc != null)
        {
            WorldPoint wp = WorldPoint.fromLocal(client, npc.getLocalLocation());
            int stack = (wp.getRegionX() & 63) << 7 | (wp.getRegionY() & 63) << 1;

            for (String key : identifierMultiMap.keys())
            {

                for (int mapStack : identifierMultiMap.get(key))
                {
                    if ((mapStack & 8190 ^ stack) == 0)
                    {
                        return new ImmutablePair<>(key, (mapStack & 1) != 0);
                    }
                }
            }

        }
        return null;
    }

    static {
        MatomenosSpawnIdentifier[] var0 = values();
        int var1 = var0.length;

        for (MatomenosSpawnIdentifier id : var0)
        {
            identifierMultiMap.putAll(id.key, List.of((int) (id.hash >> 13 & 8191L), (int) (id.hash & 8191L)));
        }

    }
}
