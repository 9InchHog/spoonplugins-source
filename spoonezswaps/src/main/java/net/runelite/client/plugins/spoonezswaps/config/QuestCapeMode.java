package net.runelite.client.plugins.spoonezswaps.config;

public enum QuestCapeMode
{
    OFF("Off"),
    INVENTORY("Inventory"),
    EQUIPPED("Worn"),
    ALWAYS("Both");

    private final String name;

    QuestCapeMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
