package net.runelite.client.plugins.detailedtimers;

import net.runelite.api.Client;

public class SotetsegTimer extends RoomTimer {
    private int p66;

    private int p33;

    private int maze1;

    private int maze2;

    public SotetsegRoomState state;

    public SotetsegTimer(Client client) {
        super(client);
        this.state = SotetsegRoomState.NOT_STARTED;
    }

    public int getTicksSinceStart() {
        return this.client.getTickCount() - this.roomStart;
    }

    public int getTicksSinceLastMaze() {
        if (this.state == SotetsegRoomState.PHASE_2)
            return this.client.getTickCount() - this.maze1;
        if (this.state == SotetsegRoomState.PHASE_3)
            return this.client.getTickCount() - this.maze2;
        return -1;
    }

    public void start() {
        this.state = SotetsegRoomState.PHASE_1;
        super.start();
    }

    public void end() {
        this.state = SotetsegRoomState.FINISHED;
        super.end();
    }

    public void procFirstMaze() {
        this.p66 = this.client.getTickCount();
        this.state = SotetsegRoomState.MAZE_1;
    }

    public void procSecondMaze() {
        this.p33 = this.client.getTickCount();
        this.state = SotetsegRoomState.MAZE_2;
    }

    public void endFirstMaze() {
        this.maze1 = this.client.getTickCount();
        this.state = SotetsegRoomState.PHASE_2;
    }

    public void endSecondMaze() {
        this.maze2 = this.client.getTickCount();
        this.state = SotetsegRoomState.PHASE_3;
    }

    public int splitMaze1() {
        return this.maze1 - this.p66;
    }

    public int splitMaze2() {
        return this.maze2 - this.p33;
    }

    public int split66s() {
        return this.p66 - this.roomStart;
    }

    public int split33s() {
        return this.p66 - this.p33;
    }

    enum SotetsegRoomState {
        NOT_STARTED, PHASE_1, MAZE_1, PHASE_2, MAZE_2, PHASE_3, FINISHED;
    }

    public int to66s() {
        return this.p66 - this.roomStart;
    }

    public int to33s() {
        return this.p33 - this.roomStart;
    }
}
