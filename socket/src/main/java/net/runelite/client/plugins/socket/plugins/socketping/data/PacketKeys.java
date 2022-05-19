package net.runelite.client.plugins.socket.plugins.socketping.data;

public enum PacketKeys {
    PING_TYPE("ping-type"),
    PING_TARGET_TYPE("ping-target-type"),
    PING_DATA("ping-data"),
    REGION_X("region-x"),
    REGION_Y("region-y"),
    REGION_Z("region-z"),
    REGION_ID("region-id"),
    NPC_ID("npc-id"),
    PLAYER_ID("player-id"),
    GAMEOBJECT_ID("gameobject-id"),
    PING_WORLD("ping-world");

    String key;

    PacketKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
