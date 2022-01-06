package net.runelite.client.plugins.nex;

import lombok.Getter;

import java.util.Set;

public class NexTargetChange {
    @Getter
    private Set<Integer> ids;

    @Getter
    private NexDialogue[] dialogues;

    public NexTargetChange(Set<Integer> ids, NexDialogue[] dialogues) {
        this.ids = ids;
        this.dialogues = dialogues;
    }
}
