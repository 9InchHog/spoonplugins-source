package net.runelite.client.plugins.spoonezswaps.config;

public enum BuyMode
{
    VALUE("Value"),
    BUY_1("Buy 1"),
    BUY_5("Buy 5"),
    BUY_10("Buy 10"),
    BUY_50("Buy 50");

    private final String name;

    BuyMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
