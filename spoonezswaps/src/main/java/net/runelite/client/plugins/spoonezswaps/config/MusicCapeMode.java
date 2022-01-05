package net.runelite.client.plugins.spoonezswaps.config;

public enum MusicCapeMode
{
    OFF("Off"),
    INVENTORY("Inventory"),
    EQUIPPED("Worn"),
    ALWAYS("Both");

    private final String name;

    MusicCapeMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
