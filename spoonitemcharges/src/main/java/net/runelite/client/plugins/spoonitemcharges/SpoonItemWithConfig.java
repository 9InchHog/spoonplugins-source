package net.runelite.client.plugins.spoonitemcharges;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.util.Map;
import javax.annotation.Nullable;

enum SpoonItemWithConfig {
	DODGY_NECKLACE(21143, "dodgyNecklace", SpoonItemChargeType.DODGY_NECKLACE),
	BINDING_NECKLACE(5521, "bindingNecklace", SpoonItemChargeType.BINDING_NECKLACE),
	EXPLORERS_RING_1(13125, "explorerRing", SpoonItemChargeType.EXPLORER_RING),
	EXPLORERS_RING_2(13126, "explorerRing", SpoonItemChargeType.EXPLORER_RING),
	EXPLORERS_RING_3(13127, "explorerRing", SpoonItemChargeType.EXPLORER_RING),
	EXPLORERS_RING_4(13128, "explorerRing", SpoonItemChargeType.EXPLORER_RING),
	RING_OF_FORGING(2568, "ringOfForging", SpoonItemChargeType.RING_OF_FORGING),
	AMULET_OF_CHEMISTRY(21163, "amuletOfChemistry", SpoonItemChargeType.AMULET_OF_CHEMISTRY),
	AMULET_OF_BOUNTY(21160, "amuletOfBounty", SpoonItemChargeType.AMULET_OF_BOUNTY),
	BRACELET_OF_SLAUGHTER(21183, "braceletOfSlaughter", SpoonItemChargeType.BRACELET_OF_SLAUGHTER),
	EXPEDITIOUS_BRACELET(21177, "expeditiousBracelet", SpoonItemChargeType.EXPEDITIOUS_BRACELET),
	CHRONICLE(13660, "chronicle", SpoonItemChargeType.TELEPORT);

	SpoonItemWithConfig(int itemId, String configKey, SpoonItemChargeType type) {
		this.itemId = itemId;
		this.configKey = configKey;
		this.type = type;
	}

	@Getter
	private final int itemId;

	@Getter
	private final String configKey;

	@Getter
	private final SpoonItemChargeType type;

	private static final Map<Integer, SpoonItemWithConfig> ID_MAP;

	static {
		ImmutableMap.Builder<Integer, SpoonItemWithConfig> builder = new ImmutableMap.Builder();
		for (SpoonItemWithConfig item : values())
			builder.put(item.getItemId(), item);
		ID_MAP = (Map<Integer, SpoonItemWithConfig>)builder.build();
	}

	@Nullable
	static SpoonItemWithConfig findItem(int itemId) {
		return ID_MAP.get(itemId);
	}
}
