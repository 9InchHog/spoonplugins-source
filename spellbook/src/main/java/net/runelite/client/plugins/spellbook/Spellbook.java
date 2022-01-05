package net.runelite.client.plugins.spellbook;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Spellbook
{
    STANDARD(0, "standard"),
    ANCIENT(1, "ancient"),
    LUNAR(2, "lunar"),
    ARCEUUS(3, "arceuus");

    @Getter(AccessLevel.PACKAGE)
    private final int id;

    @Getter(AccessLevel.PACKAGE)
    private final String configKey;

    private static final ImmutableMap<Integer, Spellbook> map;

    static
    {
        ImmutableMap.Builder<Integer, Spellbook> builder = new ImmutableMap.Builder<>();
        for (Spellbook s : values())
        {
            builder.put(s.id, s);
        }
        map = builder.build();
    }

    public static Spellbook getByID(int id)
    {
        return map.get(id);
    }
}