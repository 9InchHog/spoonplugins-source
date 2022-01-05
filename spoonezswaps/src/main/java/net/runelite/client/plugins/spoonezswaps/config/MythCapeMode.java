package net.runelite.client.plugins.spoonezswaps.config;

public enum MythCapeMode
{
    OFF("Off"),
    INVENTORY("Inventory"),
    EQUIPPED("Worn"),
    ALWAYS("Both");

    private final String name;

    MythCapeMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
