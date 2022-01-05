package net.runelite.client.plugins.spoonezswaps.config;

public enum FarmingCapeMode
{
    OFF("Off"),
    INVENTORY("Inventory"),
    EQUIPPED("Worn"),
    ALWAYS("Both");

    private final String name;

    FarmingCapeMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
