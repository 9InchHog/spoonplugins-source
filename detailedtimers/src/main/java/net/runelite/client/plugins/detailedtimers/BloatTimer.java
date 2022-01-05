package net.runelite.client.plugins.detailedtimers;

import net.runelite.api.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class BloatTimer extends RoomTimer {
    private static final Logger log = LoggerFactory.getLogger(BloatTimer.class);

    boolean walking;

    int walkStart;

    private ArrayList<Integer> walksList;

    private int lastStop;

    public BloatTimer(Client client) {
        super(client);
        this.walking = false;
        this.walkStart = -1;
        this.lastStop = -1;
        this.walksList = new ArrayList<>();
    }

    public ArrayList<Integer> getWalks() {
        return this.walksList;
    }

    public int walks() {
        return this.walksList.size();
    }

    public void walk() {
        if (!this.walking) {
            this.walkStart = this.client.getTickCount();
            this.walking = true;
            if (this.roomStart - this.client.getTickCount() != 0)
                log.info("Down time: " + DetailedTimersPlugin.time(this.client.getTickCount() - this.lastStop) + " (" + DetailedTimersPlugin.bloatInstStart + ")");
        }
    }

    public void stopWalk() {
        if (this.walking) {
            this.walksList.add(this.client.getTickCount() - this.walkStart);
            this.walkStart = -1;
            this.lastStop = this.client.getTickCount();
            this.walking = false;
            log.info("Walk: " + DetailedTimersPlugin.time(this.walksList.get(this.walksList.size() - 1)) + " (" + DetailedTimersPlugin.bloatInstStart + ")");
        }
    }

    public void kill() {
        super.kill();
        log.info("Down time: " + DetailedTimersPlugin.time(this.client.getTickCount() - this.lastStop) + " (" + DetailedTimersPlugin.bloatInstStart + ")");
    }

    public void reset() {
        this.walking = false;
        this.walkStart = -1;
        this.walksList.clear();
        super.reset();
    }
}
