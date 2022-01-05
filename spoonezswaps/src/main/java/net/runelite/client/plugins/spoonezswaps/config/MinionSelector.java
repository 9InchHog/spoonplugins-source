package net.runelite.client.plugins.spoonezswaps.config;

public enum MinionSelector {
    ALL("All"),
    MAGE("Mage"),
    RANGE("Range"),
    MELEE("Melee");

    private final String name;

    MinionSelector(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
