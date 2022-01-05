package net.runelite.client.plugins.spoonezswaps.config;

public enum MoryMode
{
    OFF("Off"),
    ECTO("Ecto"),
    BURGH("Burgh");

    private final String name;

    MoryMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
