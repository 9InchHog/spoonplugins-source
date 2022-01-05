package net.runelite.client.plugins.spoonnightmare;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;

@Getter(AccessLevel.PACKAGE)
public class TotemInfo {
	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private NPC npc;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private int ratio;

	TotemInfo(NPC npc, int ratio){
		this.npc = npc;
		this.ratio = ratio;
	}
}
