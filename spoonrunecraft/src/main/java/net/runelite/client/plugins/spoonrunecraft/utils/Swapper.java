package net.runelite.client.plugins.spoonrunecraft.utils;

import java.util.HashSet;
import java.util.Set;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

public class Swapper {
    public MenuEntry[] getEntries() {
        return this.entries;
    }

    public void setEntries(MenuEntry[] entries) {
        this.entries = entries;
    }

    private Set<Swappable> swapping = new HashSet<>();

    private MenuEntry[] entries;

    public void deprioritizeWalk() {
        MenuEntry menuEntry = this.entries[this.entries.length - 1];
        menuEntry.setType(MenuAction.of(MenuAction.WALK.getId() + 2000));
    }

    public void removeIndex(int index) {
        this.entries = (MenuEntry[])ArrayUtils.remove((Object[])this.entries, index);
    }

    public void markForSwap(String optionA, String optionB, String target) {
        if (optionA.equalsIgnoreCase(optionB))
            return;
        this.swapping.add(new Swappable(target, optionA, optionB));
    }

    public void startSwap() {
        int index = 0;
        for (MenuEntry entry : this.entries) {
            String target = Text.removeTags(entry.getTarget()).toLowerCase();
            String option = Text.removeTags(entry.getOption()).toLowerCase();
            for (Swappable swap : this.swapping) {
                if (swap.getTarget().equalsIgnoreCase(target)) {
                    if (option.equalsIgnoreCase(swap.getOptionOne())) {
                        swap.setIndexOne(index);
                        continue;
                    }
                    if (option.equalsIgnoreCase(swap.getOptionTwo()))
                        swap.setIndexTwo(index);
                }
            }
            index++;
        }
        for (Swappable swap : this.swapping) {
            if (swap.isReady()) {
                MenuEntry entry = this.entries[swap.getIndexOne()];
                this.entries[swap.getIndexOne()] = this.entries[swap.getIndexTwo()];
                this.entries[swap.getIndexTwo()] = entry;
            }
        }
        this.swapping.clear();
    }
}
