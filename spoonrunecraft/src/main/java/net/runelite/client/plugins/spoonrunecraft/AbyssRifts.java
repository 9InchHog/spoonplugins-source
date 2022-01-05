package net.runelite.client.plugins.spoonrunecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public enum AbyssRifts {
	AIR_RIFT(25378, 556),
	BLOOD_RIFT(25380, 565),
	BODY_RIFT(24973, 559),
	CHAOS_RIFT(24976, 562),
	COSMIC_RIFT(24974, 564),
	DEATH_RIFT(25035, 560),
	EARTH_RIFT(24972, 557),
	FIRE_RIFT(24971, 554),
	LAW_RIFT(25034, 563),
	MIND_RIFT(25379, 558),
	NATURE_RIFT(24975, 561),
	SOUL_RIFT(25377, 566),
	WATER_RIFT(25376, 555);

	private final int objectId;

	private final int itemId;

	private static final Map<Integer, AbyssRifts> rifts;

	public int getObjectId() {
		return this.objectId;
	}

	public int getItemId() {
		return this.itemId;
	}

	static {
		ImmutableMap.Builder<Integer, AbyssRifts> builder = new ImmutableMap.Builder();
		for (AbyssRifts s : values())
			builder.put(Integer.valueOf(s.getObjectId()), s);
		rifts = (Map<Integer, AbyssRifts>)builder.build();
	}

	AbyssRifts(int objectId, int itemId) {
		this.objectId = objectId;
		this.itemId = itemId;
	}

	public static AbyssRifts getRift(int id) {
		return rifts.get(Integer.valueOf(id));
	}
}
