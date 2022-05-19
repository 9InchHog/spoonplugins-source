package net.runelite.client.plugins.socket.plugins.socketping;

import java.util.HashMap;

public enum PingTargetType {
    TILE("Tile"),
    NPC("NPC"),
    PLAYER("Player"),
    GAMEOBJECT("GameObject");

    String type;

    public static final HashMap<String, PingTargetType> mapping;

    public String getType() {
        return this.type;
    }

    PingTargetType(String type) {
        this.type = type;
    }

    static {
        mapping = new HashMap<>();
        for (PingTargetType value : values())
            mapping.put(value.type.toLowerCase(), value);
    }
}
