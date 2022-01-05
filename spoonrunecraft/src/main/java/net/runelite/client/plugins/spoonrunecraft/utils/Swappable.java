package net.runelite.client.plugins.spoonrunecraft.utils;

import net.runelite.client.util.Text;

public class Swappable {
    private String target;

    private String optionOne;

    private String optionTwo;

    private int indexOne;

    private int indexTwo;

    public String getTarget() {
        return this.target;
    }

    public String getOptionOne() {
        return this.optionOne;
    }

    public String getOptionTwo() {
        return this.optionTwo;
    }

    public int getIndexOne() {
        return this.indexOne;
    }

    public void setIndexOne(int indexOne) {
        this.indexOne = indexOne;
    }

    public int getIndexTwo() {
        return this.indexTwo;
    }

    public void setIndexTwo(int indexTwo) {
        this.indexTwo = indexTwo;
    }

    public Swappable(String target, String optionOne, String optionTwo) {
        this.target = Text.removeTags(target.toLowerCase());
        this.optionOne = Text.removeTags(optionOne);
        this.optionTwo = Text.removeTags(optionTwo);
        this.indexOne = -1;
        this.indexTwo = -1;
    }

    public boolean isReady() {
        return (this.indexOne != -1 && this.indexTwo != -1);
    }
}
