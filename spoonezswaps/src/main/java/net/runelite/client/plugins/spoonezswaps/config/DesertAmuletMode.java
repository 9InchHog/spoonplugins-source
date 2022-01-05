package net.runelite.client.plugins.spoonezswaps.config;

public enum DesertAmuletMode
{
    OFF("Off"),
    NARDAH("Nardah"),
    KALPHITE_CAVE("Kalphite");

    private final String name;

    DesertAmuletMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
