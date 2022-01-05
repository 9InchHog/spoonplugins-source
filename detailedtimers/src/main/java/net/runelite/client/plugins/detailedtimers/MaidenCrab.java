package net.runelite.client.plugins.detailedtimers;

import net.runelite.api.NPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaidenCrab {
    private static final Logger log = LoggerFactory.getLogger(MaidenCrab.class);

    private NPC npc;

    public boolean scuffed;

    private String name;

    int HP;

    public MaidenCrab(String name, boolean scuffed, NPC npc) {
        this.HP = -1;
        this.npc = npc;
        this.name = name;
        this.scuffed = scuffed;
        log.info("Adding: " + name + ", Scuffed: " + scuffed + ", index: " + npc.getIndex() + ", HP: " + npc.getHealthRatio());
    }

    public void updateHP() {
        if (this.npc.getHealthRatio() != -1)
            this.HP = this.npc.getHealthRatio();
    }

    public String getName() {
        return this.name;
    }

    public int getHP() {
        return (this.HP == -1) ? 30 : this.HP;
    }

    public int getIndex() {
        return this.npc.getIndex();
    }
}
