package net.runelite.client.plugins.spoontobstats;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InfoboxText
{
    DAMAGE_PERCENT("Damage Percent"),
    TIME("Room Time"),
    NONE("None");

    private final String type;

    @Override
    public String toString()
    {
        return type;
    }
}
