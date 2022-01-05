package net.runelite.client.plugins.spoonbarrows;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;

@RequiredArgsConstructor
@Getter
public enum sBarrowsBrothers {
	AHRIM("Ahrim", new WorldPoint(3566, 3289, 0), Varbits.BARROWS_KILLED_AHRIM),
	DHAROK("Dharok", new WorldPoint(3575, 3298, 0), Varbits.BARROWS_KILLED_DHAROK),
	GUTHAN("Guthan", new WorldPoint(3577, 3283, 0), Varbits.BARROWS_KILLED_GUTHAN),
	KARIL("Karil", new WorldPoint(3566, 3275, 0), Varbits.BARROWS_KILLED_KARIL),
	TORAG("Torag", new WorldPoint(3553, 3283, 0), Varbits.BARROWS_KILLED_TORAG),
	VERAC("Verac", new WorldPoint(3557, 3298, 0), Varbits.BARROWS_KILLED_VERAC);

	private final String name;
	private final WorldPoint location;
	private final Varbits killedVarbit;
}
