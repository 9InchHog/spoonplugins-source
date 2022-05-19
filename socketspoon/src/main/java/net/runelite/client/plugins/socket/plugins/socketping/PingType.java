package net.runelite.client.plugins.socket.plugins.socketping;

import java.util.HashMap;

public enum PingType {
    TARGET("Target"),
    WARN("Warn"),
    OMW("OMW"),
    ASSIST_ME("Assist Me"),
    QUESTION_MARK("Question Mark");

    String type;

    public static final HashMap<String, PingType> mapping;

    public String getType() {
        return this.type;
    }

    PingType(String type) {
        this.type = type;
    }

    static {
        mapping = new HashMap<>();
        for (PingType value : values())
            mapping.put(value.type.toLowerCase(), value);
    }
}
