package net.runelite.client.plugins.detailedtimers;

import net.runelite.api.Client;

public class MaidenTimer extends RoomTimer {
    private int p70;

    private int p50;

    private int p30;

    public MaidenTimer(Client client) {
        super(client);
    }

    public void proc70() {
        this.p70 = this.client.getTickCount();
    }

    public void proc50() {
        this.p50 = this.client.getTickCount();
    }

    public void proc30() {
        this.p30 = this.client.getTickCount();
    }

    public int split70s() {
        return this.p70 - this.roomStart;
    }

    public int split50s() {
        return this.p50 - this.p70;
    }

    public int split30s() {
        return this.p30 - this.p50;
    }

    public int splitSkip() {
        return this.roomEnd - this.p30;
    }

    public int to30s() {
        return this.p30 - this.roomStart;
    }

    public int to50s() {
        return this.p50 - this.roomStart;
    }

    public int to70s() {
        return this.p70 - this.roomStart;
    }
}
