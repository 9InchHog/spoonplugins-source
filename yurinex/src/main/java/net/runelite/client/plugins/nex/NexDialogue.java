package net.runelite.client.plugins.nex;

import lombok.Getter;

public class NexDialogue {
    @Getter
    private NexPhase phase;

    @Getter
    private String text;

    @Getter
    private String chat;

    @Getter
    private final int invulnerability;

    public NexDialogue(NexPhase phase, String text, String chat, int invulnerability) {
        this.phase = phase;
        this.text = text;
        this.chat = chat;
        this.invulnerability = invulnerability;
    }

    public boolean matches(String text) {
        return this.text.equals(text);
    }

    public boolean chats(String text) {
        return this.chat.equals(text);
    }
}
