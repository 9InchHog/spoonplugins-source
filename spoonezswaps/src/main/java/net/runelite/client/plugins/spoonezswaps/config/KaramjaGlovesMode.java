package net.runelite.client.plugins.spoonezswaps.config;

public enum KaramjaGlovesMode
{
    OFF("Off"),
    DURADEL("Duradel"),
    GEM_MINE("Gem Mine");

    private final String name;

    KaramjaGlovesMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
