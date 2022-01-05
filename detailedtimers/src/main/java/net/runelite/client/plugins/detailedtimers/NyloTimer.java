package net.runelite.client.plugins.detailedtimers;

import net.runelite.api.Client;

public class NyloTimer extends RoomTimer {
    private int waves;

    public NyloTimer(Client client) {
        super(client);
        this.waves = -1;
    }

    public int getTicksActive() {
        if (isActive())
            return this.client.getTickCount() - this.roomStart;
        return -1;
    }

    public void startBoss() {
        this.waves = this.client.getTickCount();
    }

    public int getWaves() {
        return this.waves - this.roomStart;
    }

    public int getBoss() {
        return this.roomEnd - this.waves;
    }

    public void reset() {
        this.waves = -1;
        super.reset();
    }
}
