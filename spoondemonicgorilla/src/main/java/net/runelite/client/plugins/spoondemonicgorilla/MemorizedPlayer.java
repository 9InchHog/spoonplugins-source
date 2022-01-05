package net.runelite.client.plugins.spoondemonicgorilla;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Hitsplat;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldArea;

class MemorizedPlayer {
    private Player player;

    private WorldArea lastWorldArea;

    private List<Hitsplat> recentHitsplats;

    Player getPlayer() {
        return this.player;
    }

    WorldArea getLastWorldArea() {
        return this.lastWorldArea;
    }

    void setLastWorldArea(WorldArea lastWorldArea) {
        this.lastWorldArea = lastWorldArea;
    }

    List<Hitsplat> getRecentHitsplats() {
        return this.recentHitsplats;
    }

    MemorizedPlayer(Player player) {
        this.player = player;
        this.recentHitsplats = new ArrayList<>();
    }
}
