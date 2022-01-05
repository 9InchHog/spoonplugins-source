package net.runelite.client.plugins.spoonezswaps.config;

public enum MinionHighlightMode
{
    OFF("Off"),
    AREA("Area"),
    HULL("Hull"),
    TILE("Tile"),
    TL("True Tile"),
    OUTLINE("Outline");

    private final String name;

    MinionHighlightMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
