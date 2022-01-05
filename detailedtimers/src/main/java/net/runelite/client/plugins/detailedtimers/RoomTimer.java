package net.runelite.client.plugins.detailedtimers;

import java.util.Arrays;
import java.util.Objects;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomTimer {
    private static final Logger log = LoggerFactory.getLogger(RoomTimer.class);

    protected int instanceStart;

    protected int roomStart;

    protected int roomEnd;

    protected int roomKilled;

    private boolean finished = false;

    private boolean active = false;

    protected boolean dead = false;

    protected Client client;

    public RoomTimer(Client client) {
        this.client = client;
        this.instanceStart = -1;
        this.roomStart = -1;
        this.roomKilled = -1;
        this.roomEnd = -1;
        this.dead = false;
        this.active = false;
        this.finished = false;
    }

    public boolean isDead() {
        return this.dead;
    }

    public void start() {
        try {
            DetailedTimersPlugin.partySize = (int)Arrays.<Widget>asList(this.client.getWidget(28, 10).getStaticChildren()).stream().filter(w ->
                    w.getDynamicChildren() != null && Arrays.stream(w.getDynamicChildren()).anyMatch((r) -> !Objects.equals(r.getText(), ""))).count();
        } catch (NullPointerException nullPointerException) {}
        this.roomStart = this.client.getTickCount();
        log.info("Room start tick: " + this.roomStart);
        this.active = true;
    }

    public void kill() {
        this.dead = true;
        this.roomKilled = this.client.getTickCount();
    }

    public boolean isActive() {
        return this.active;
    }

    public void end() {
        this.roomEnd = this.client.getTickCount();
        this.active = false;
        this.finished = true;
    }

    public int duration() {
        return this.roomEnd - this.roomStart;
    }

    public int deathLength() {
        log.info("Room end: " + this.roomEnd + ", Room Killed: " + this.roomKilled);
        return this.roomEnd - this.roomKilled;
    }

    public int getActiveTicks() {
        if (isActive())
            return this.client.getTickCount() - this.roomStart;
        return -1;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void reset() {
        this.instanceStart = -1;
        this.roomStart = -1;
        this.roomKilled = -1;
        this.roomEnd = -1;
        this.dead = false;
        this.finished = false;
        this.active = false;
    }
}
