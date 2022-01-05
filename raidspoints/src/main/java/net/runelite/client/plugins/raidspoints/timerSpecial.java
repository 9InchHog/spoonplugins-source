package net.runelite.client.plugins.raidspoints;

public class timerSpecial {
    private long startTime = 0L;

    private long difference = 0L;

    private long pause = 0L;

    private long unpaused = 0L;

    private long tempTime = 0L;

    private boolean paused = false;

    public boolean started = false;

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.tempTime = this.startTime;
    }

    public void start(long offset) {
        this.startTime = System.currentTimeMillis() - offset * 1000L;
        this.tempTime = this.startTime;
        this.started = true;
    }

    public void pause() {
        this.pause = System.currentTimeMillis();
        this.tempTime = (System.currentTimeMillis() - this.startTime) / 1000L;
        this.paused = true;
    }

    public void unpause() {
        this.paused = false;
        this.unpaused = System.currentTimeMillis();
        this.difference = this.unpaused - this.pause;
        this.startTime += this.difference;
    }

    public void reset() {
        this.startTime = 0L;
        this.difference = 0L;
        this.pause = 0L;
        this.unpaused = 0L;
        this.tempTime = 0L;
        this.paused = false;
        this.startTime = System.currentTimeMillis();
        this.tempTime = this.startTime;
        this.started = false;
    }

    public long getElapsedTime() {
        return this.paused ? this.tempTime : ((System.currentTimeMillis() - this.startTime) / 1000L);
    }
}
