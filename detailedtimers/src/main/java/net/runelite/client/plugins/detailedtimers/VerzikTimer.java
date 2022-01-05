package net.runelite.client.plugins.detailedtimers;

import net.runelite.api.Client;

public class VerzikTimer extends RoomTimer {
    private int p1;

    private int p2;

    private int p3;

    private int p1Death;

    private int p2Death;

    public VerzikTimer(Client client) {
        super(client);
        this.p1 = -1;
        this.p2 = -1;
        this.p3 = -1;
        this.p1Death = -1;
        this.p2Death = -1;
    }

    public void endP1() {
        this.p1 = this.client.getTickCount();
        this.dead = false;
    }

    public void endP2() {
        this.p2 = this.client.getTickCount();
        this.dead = false;
    }

    public void killP1() {
        this.p1Death = this.client.getTickCount();
        this.dead = true;
    }

    public void killP2() {
        this.p2Death = this.client.getTickCount();
        this.dead = true;
    }

    public int p1DeathLength() {
        return this.p1 - this.p1Death;
    }

    public int p2DeathLength() {
        return this.p2 - this.p2Death;
    }

    public int p3DeathLength() {
        return this.roomEnd - this.roomKilled;
    }

    public void endP3() {
        this.p3 = this.client.getTickCount();
    }

    public int splitP1() {
        return this.p1 - this.roomStart;
    }

    public int splitP2() {
        return this.p2 - this.p1;
    }

    public int splitP3() {
        return this.p3 - this.p2;
    }

    public void reset() {
        super.reset();
        this.p1 = -1;
        this.p2 = -1;
        this.p3 = -1;
        this.p1Death = -1;
        this.p2Death = -1;
    }
}
