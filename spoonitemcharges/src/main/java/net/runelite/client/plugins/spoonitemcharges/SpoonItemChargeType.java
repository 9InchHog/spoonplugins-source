package net.runelite.client.plugins.spoonitemcharges;

import java.util.function.Predicate;

enum SpoonItemChargeType {
	ABYSSAL_BRACELET(SpoonItemChargeConfig::showAbyssalBraceletCharges),
	AMULET_OF_CHEMISTRY(SpoonItemChargeConfig::showAmuletOfChemistryCharges),
	AMULET_OF_BOUNTY(SpoonItemChargeConfig::showAmuletOfBountyCharges),
	BELLOWS(SpoonItemChargeConfig::showBellowCharges),
	BRACELET_OF_SLAUGHTER(SpoonItemChargeConfig::showBraceletOfSlaughterCharges),
	EXPEDITIOUS_BRACELET(SpoonItemChargeConfig::showExpeditiousBraceletCharges),
	FUNGICIDE_SPRAY(SpoonItemChargeConfig::showFungicideCharges),
	IMPBOX(SpoonItemChargeConfig::showImpCharges),
	TELEPORT(SpoonItemChargeConfig::showTeleportCharges),
	WATERCAN(SpoonItemChargeConfig::showWateringCanCharges),
	WATERSKIN(SpoonItemChargeConfig::showWaterskinCharges),
	DODGY_NECKLACE(SpoonItemChargeConfig::showDodgyCount),
	BINDING_NECKLACE(SpoonItemChargeConfig::showBindingNecklaceCharges),
	EXPLORER_RING(SpoonItemChargeConfig::showExplorerRingCharges),
	FRUIT_BASKET(SpoonItemChargeConfig::showBasketCharges),
	SACK(SpoonItemChargeConfig::showSackCharges),
	RING_OF_FORGING(SpoonItemChargeConfig::showRingOfForgingCount),
	POTION(SpoonItemChargeConfig::showPotionDoseCount),
	GUTHIX_REST(SpoonItemChargeConfig::showGuthixRestDoses),
	DIVINE_POTION(SpoonItemChargeConfig::showDivinePotionDoseCount),
	COX_POTION(SpoonItemChargeConfig::showCoxPotionDoseCount);

	SpoonItemChargeType(Predicate<SpoonItemChargeConfig> enabled) {
		this.enabled = enabled;
	}

	private final Predicate<SpoonItemChargeConfig> enabled;

	public Predicate<SpoonItemChargeConfig> getEnabled() {
		return this.enabled;
	}
}
