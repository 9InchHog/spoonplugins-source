package net.runelite.client.plugins.spoonezswaps.config;

public enum ArdyCloakMode
{
    OFF("Off"),
    KANDARIN_MONASTERY("Monastery"),
    ARDOUGNE_FARM("Farm");

    private final String name;

    ArdyCloakMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
