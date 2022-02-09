package net.runelite.client.plugins.socket.plugins.socketthieving;

import net.runelite.client.plugins.socket.org.json.JSONObject;

import java.net.Socket;

public class SocketThievingPlayerName {

    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) { this.name = name; }

    private SocketThievingPlayerName() {}

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        return json;
    }

    public void parseJSON(JSONObject json) {
        this.name = json.getString("name");
    }

    public static SocketThievingPlayerName fromJSON(JSONObject json) {
        SocketThievingPlayerName shp = new SocketThievingPlayerName();
        shp.parseJSON(json);
        return shp;
    }

    public SocketThievingPlayerName(String name) {
        this.name = name;
    }
}
