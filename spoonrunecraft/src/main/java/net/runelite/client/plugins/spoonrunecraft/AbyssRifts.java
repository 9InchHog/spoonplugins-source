package net.runelite.client.plugins.spoonrunecraft;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;

import java.util.Map;
import java.util.function.Predicate;

import static net.runelite.api.ItemID.*;

@AllArgsConstructor
enum AbyssRifts
{
	AIR_RIFT(ObjectID.AIR_RIFT, AIR_RUNE, sRunecraftConfig::showAir),
	BLOOD_RIFT(NullObjectID.NULL_43848, BLOOD_RUNE, sRunecraftConfig::showBlood),
	BODY_RIFT(ObjectID.BODY_RIFT, BODY_RUNE, sRunecraftConfig::showBody),
	CHAOS_RIFT(ObjectID.CHAOS_RIFT, CHAOS_RUNE, sRunecraftConfig::showChaos),
	COSMIC_RIFT(ObjectID.COSMIC_RIFT, COSMIC_RUNE, sRunecraftConfig::showCosmic),
	DEATH_RIFT(ObjectID.DEATH_RIFT, DEATH_RUNE, sRunecraftConfig::showDeath),
	EARTH_RIFT(ObjectID.EARTH_RIFT, EARTH_RUNE, sRunecraftConfig::showEarth),
	FIRE_RIFT(ObjectID.FIRE_RIFT, FIRE_RUNE, sRunecraftConfig::showFire),
	LAW_RIFT(ObjectID.LAW_RIFT, LAW_RUNE, sRunecraftConfig::showLaw),
	MIND_RIFT(ObjectID.MIND_RIFT, MIND_RUNE, sRunecraftConfig::showMind),
	NATURE_RIFT(ObjectID.NATURE_RIFT, NATURE_RUNE, sRunecraftConfig::showNature),
	SOUL_RIFT(ObjectID.SOUL_RIFT, SOUL_RUNE, sRunecraftConfig::showSoul),
	WATER_RIFT(ObjectID.WATER_RIFT, WATER_RUNE, sRunecraftConfig::showWater);

	@Getter
	private final int objectId;

	@Getter
	private final int itemId;

	@Getter
	private final Predicate<sRunecraftConfig> configEnabled;

	private static final Map<Integer, AbyssRifts> rifts;

	static
	{
		ImmutableMap.Builder<Integer, AbyssRifts> builder = new ImmutableMap.Builder<>();

		for (AbyssRifts s : values())
		{
			builder.put(s.getObjectId(), s);
		}

		rifts = builder.build();
	}

	static AbyssRifts getRift(int id)
	{
		return rifts.get(id);
	}
}
