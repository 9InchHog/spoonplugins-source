package net.runelite.client.plugins.nex;

import lombok.Getter;

public class NexDialogue {
    @Getter
    private NexPhase phase;

    @Getter
    private String text;

    @Getter
    private final int invulnerability;

    public NexDialogue(NexPhase phase, String text, int invulnerability) {
        this.phase = phase;
        this.text = text;
        this.invulnerability = invulnerability;
    }

    public boolean matches(String text) {
        return this.text.equals(text);
    }
}
