package net.runelite.client.plugins.spoonezswaps.config;

public enum CaHiltMode
{
    OFF("Off"),
    GWD("Trollheim"),
    ZUK("Mor Ul Rek");

    private final String name;

    CaHiltMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
