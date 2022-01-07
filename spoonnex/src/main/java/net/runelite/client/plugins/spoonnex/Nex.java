package net.runelite.client.plugins.spoonnex;

import net.runelite.api.NPC;

public class Nex {
	public Nex(NPC npc) {
		this.npc = npc;
		this.phase = 1;
		this.attacksTilSpecial = 5;
		this.specialTicksLeft = 0;
		this.currentSpecial = "virus";
		this.nextSpecial = "no escape";
		this.invulnerableTicks = 27;

	}
	public NPC npc;
	public int phase;
	public int attacksTilSpecial;
	public int specialTicksLeft;
	public String currentSpecial;
	public String nextSpecial;
	public int invulnerableTicks;
}
