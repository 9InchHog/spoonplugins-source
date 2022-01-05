package net.runelite.client.plugins.spoonezswaps.config;

public enum SellMode
{
    VALUE("Value"),
    SELL_1("Sell 1"),
    SELL_5("Sell 5"),
    SELL_10("Sell 10"),
    SELL_50("Sell 50");

    private final String name;

    SellMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}