package net.runelite.client.plugins.socket.plugins.sockethealing;

import net.runelite.client.plugins.socket.org.json.JSONObject;

public class SocketHealingPlayer {
    private int health;

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    private SocketHealingPlayer() {}

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("health", this.health);
        return json;
    }

    public void parseJSON(JSONObject json) {
        this.health = json.getInt("health");
    }

    public static SocketHealingPlayer fromJSON(JSONObject json) {
        SocketHealingPlayer shp = new SocketHealingPlayer();
        shp.parseJSON(json);
        return shp;
    }

    public SocketHealingPlayer(String name, int health) {
        this.health = health;
    }
}
