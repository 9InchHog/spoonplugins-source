package net.runelite.client.plugins.spoonezswaps;

import net.runelite.client.config.*;
import net.runelite.client.plugins.spoonezswaps.config.*;

@ConfigGroup("spoonezswaps")
public interface SpoonEzSwapsConfig extends Config
{
	@ConfigSection(
			name = "Custom Swaps",
			description = "Configuration for custom Swaps",
			position = 0,
			closedByDefault = true
	)
	String customSwapsSection = "Custom Swaps";

	@ConfigSection(
			name = "Shift Swaps",
			description = "Configuration for Shift Swaps",
			position = 1,
			closedByDefault = true
	)
	String shiftCustomSwapsSection = "Shift Swaps";

	@ConfigSection(
			name = "Hotkey Swaps",
			description = "Configuration for Hotkey Custom Swaps",
			position = 2,
			closedByDefault = true
	)
	String keyCustomSwapsSection = "Hotkey Swaps";

	@ConfigSection(
			name = "Remove Options",
			description = "Configuration for removing swaps",
			position = 3,
			closedByDefault = true
	)
	String removeSwapsSection = "Remove Options";

	@ConfigSection(
			name = "Skilling",
			description = "",
			position = 4,
			keyName = "skillingSection",
			closedByDefault = true
	)
	String skillingSection = "Skilling";

	@ConfigSection(
			name = "Diary",
			description = "",
			position = 5,
			keyName = "diarySection",
			closedByDefault = true
	)
	String diarySection = "Diary";

	@ConfigSection(
			name = "Teleportation",
			description = "",
			position = 6,
			keyName = "teleportationSection",
			closedByDefault = true
	)
	String teleportationSection = "Teleportation";

	@ConfigSection(
			name = "Misc",
			description = "",
			position = 7,
			keyName = "miscSection",
			closedByDefault = true
	)
	String miscSection = "Misc";

	@ConfigSection(
			name = "PvM",
			description = "",
			position = 8,
			keyName = "pvmSection",
			closedByDefault = true
	)
	String pvmSection = "PvM";

	@ConfigSection(
			name = "Audio",
			description = "",
			position = 9,
			keyName = "audioSection",
			closedByDefault = true
	)
	String audioSection = "Audio";

	//------------------------------------------------------------//
	// Skilling
	//------------------------------------------------------------//

	@ConfigItem(
			keyName = "getEasyConstruction",
			name = "Easy Construction",
			description = "Makes 'Remove'/'Build' the default option for listed items.",
			position = 0,
			section = skillingSection
	)
	default boolean getEasyConstruction()
	{
		return true;
	}

	@ConfigItem(
			keyName = "getConstructionMode",
			name = "EZ Construction Type",
			description = "",
			position = 1,
			section = skillingSection,
			hidden = true,
			unhide = "getEasyConstruction"
	)
	default ConstructionMode getConstructionMode()
	{
		return ConstructionMode.LARDER;
	}

	@ConfigItem(
			keyName = "afkConstruction",
			name = "Afk Construction",
			description = "You click, it presses key. Basically one click. <br> Presses 1 for removing/repeat task/payment, presses 6 to build mahogany tables",
			position = 2,
			section = skillingSection,
			hidden = true,
			unhide = "getEasyConstruction"
	)
	default boolean afkConstruction()
	{
		return true;
	}

	@ConfigItem(
			keyName = "stringAmulet",
			name = "String Amulet Overlay",
			description = "Overlay indicating how many amulets in your invent are strung",
			position = 3,
			section = skillingSection
	)
	default boolean getStringAmulet() {
		return false;
	}

	@ConfigItem(
			keyName = "cookPie",
			name = "Bake Pie Overlay",
			description = "Overlay indicating how many summer pies in your inventory are baked",
			position = 4,
			section = skillingSection
	)
	default boolean getBakePie() {
		return false;
	}

	@ConfigItem(
			keyName = "swapPickpocket",
			name = "Pickpocket",
			description = "Swap Talk-to with Pickpocket on NPC<br>Example: Man, Woman",
			position = 5,
			section = skillingSection
	)
	default boolean swapPickpocket()
	{
		return false;
	}

	@ConfigItem(
			keyName = "swapHerblore",
			name = "Herblore",
			description = "Removes the `drink` option on herblore potions - for herblore training",
			position = 6,
			section = skillingSection
	)
	default boolean swapHerblore()
	{
		return false;
	}

	@ConfigItem(
			keyName = "karambwans",
			name = "Karambwans",
			description = "Swaps max cape left click teleports depending on location.",
			position = 7,
			section = skillingSection,
			disabledBy = "swapMaxCape"
	)
	default boolean karambwans()
	{
		return false;
	}

	@ConfigItem(
			keyName = "customDrop",
			name = "Left Click Drop",
			description = "Anything in the list will be dropped on left click",
			position = 8,
			section = skillingSection
	)
	default String customDrop()
	{
		return "iron ore, blisterwood log, redwood log, empty plant pot, leaping trout, leaping salmon, leaping sturgeon, sandstone (1kg), sandstone (2kg), sandstone (5kg), sandstone (10kg), granite (500g), granite (2kg), granite (5kg)";
	}

	//------------------------------------------------------------//
	// Diary
	//------------------------------------------------------------//

	@ConfigItem(
			keyName = "swapArdyCloak",
			name = "Ardy Cloak",
			description = "Swap ardy cloak options",
			section = diarySection
	)
	default ArdyCloakMode swapArdyCloak()
	{
		return ArdyCloakMode.OFF;
	}

	@ConfigItem(
			keyName = "swapDesertAmulet",
			name = "Desert Amulet",
			description = "Swap desert amulet options",
			section = diarySection
	)
	default DesertAmuletMode swapDesertAmulet()
	{
		return DesertAmuletMode.OFF;
	}

	@ConfigItem(
			keyName = "swapFremmyBoots",
			name = "Fremennik Boots",
			description = "Swap wear with teleport",
			section = diarySection
	)
	default boolean swapFremmyBoots() {
		return false;
	}

	@ConfigItem(
			keyName = "caHiltMode",
			name = "Ghommal's Hilt",
			description = "Swap the left click option with the desired teleport location on Ghommal's Hilt.",
			section = diarySection
	)
	default CaHiltMode getCaHiltMode() {return CaHiltMode.OFF;}

	@ConfigItem(
			keyName = "swapKandarinHelm",
			name = "Kandarin Helm",
			description = "Swap wear with teleport",
			section = diarySection
	)
	default boolean swapKandarinHelm() {
		return false;
	}

	@ConfigItem(
			keyName = "swapKaramjaGloves",
			name = "Karamja Gloves",
			description = "Swap wear with duradel",
			section = diarySection
	)
	default KaramjaGlovesMode swapKaramjaGloves() {
		return KaramjaGlovesMode.OFF;
	}

	@ConfigItem(
			keyName = "swapMory",
			name = "Morytania Legs",
			description = "Swap morytania legs options",
			section = diarySection
	)
	default MoryMode swapMory()
	{
		return MoryMode.OFF;
	}

	@ConfigItem(
			keyName = "swapRadasBlessing",
			name = "Rada's Blessing",
			description = "Swap wear with teleports",
			section = diarySection
	)
	default boolean swapRadasBlessing() {
		return false;
	}

	//------------------------------------------------------------//
	// Teleportation
	//------------------------------------------------------------//
	@ConfigItem(
			keyName = "gamesNecklaceMode",
			name = "Games Necklace",
			description = "Swap the left click 'remove' option with the desired teleport location on a worn Games Necklace.",
			//position = 1,
			section = teleportationSection
	)
	default GamesNecklaceMode getGamesNecklaceMode()
	{
		return GamesNecklaceMode.OFF;
	}

	@ConfigItem(
			keyName = "duelingRingMode",
			name = "Dueling Ring",
			description = "Swap the left click 'remove' option with the desired teleport location on a worn Ring of Dueling.",
			//position = 2,
			section = teleportationSection
	)
	default DuelingRingMode getDuelingRingMode()
	{
		return DuelingRingMode.OFF;
	}

	@ConfigItem(
			keyName = "gloryMode",
			name = "Glory",
			description = "Swap the left click 'remove' option with the desired teleport location on a worn Amulet of Glory / Amulet of Eternal Glory.",
			//position = 3,
			section = teleportationSection
	)
	default GloryMode getGloryMode()
	{
		return GloryMode.OFF;
	}

	@ConfigItem(
			keyName = "skillsnecklacemode",
			name = "Skills Necklace",
			description = "Swap the left click 'remove' option with the desired teleport location on a worn Skills Necklace.",
			//position = 4,
			section = teleportationSection
	)
	default SkillsNecklaceMode getSkillsNecklaceMode()
	{
		return SkillsNecklaceMode.OFF;
	}

	@ConfigItem(
			keyName = "necklaceofpassagemode",
			name = "Necklace of Passage",
			description = "Swap the left click 'remove' option with the desired teleport location on a worn Necklace of Passage.",
			//position = 5,
			section = teleportationSection
	)
	default NecklaceOfPassageMode getNecklaceofPassageMode()
	{
		return NecklaceOfPassageMode.OFF;
	}

	@ConfigItem(
			keyName = "digsitependantmode",
			name = "Digsite Pendant",
			description = "Swap the left click 'remove' option with the desired teleport location on a worn Digsite Pendant.",
			//position = 6,
			section = teleportationSection
	)
	default DigsitePendantMode getDigsitePendantMode()
	{
		return DigsitePendantMode.OFF;
	}

	@ConfigItem(
			keyName = "combatbraceletmode",
			name = "Combat Bracelet",
			description = "Swap the left click 'remove' option with the desired teleport location on a worn Combat Bracelet.",
			//position = 7,
			section = teleportationSection
	)
	default CombatBraceletMode getCombatBraceletMode()
	{
		return CombatBraceletMode.OFF;
	}

	@ConfigItem(
			keyName = "burningamuletmode",
			name = "Burning Amulet",
			description = "Swap the left click 'remove' option with the desired teleport location on a worn Burning Amulet.",
			//position = 8,
			section = teleportationSection
	)
	default BurningAmuletMode getBurningAmuletMode()
	{
		return BurningAmuletMode.OFF;
	}

	@ConfigItem(
			keyName = "xericstalismanmode",
			name = "Xeric's Talisman",
			description = "Swap the left click 'remove' option with the desired teleport location on a worn Xeric's Talisman.",
			//position = 9,
			section = teleportationSection
	)
	default XericsTalismanMode getXericsTalismanMode()
	{
		return XericsTalismanMode.OFF;
	}

	@ConfigItem(
			keyName = "ringofwealthmode",
			name = "Ring of Wealth",
			description = "Swap the left click 'remove' option with the desired teleport location on a worn Ring of Wealth.",
			//position = 10,
			section = teleportationSection
	)
	default RingOfWealthMode getRingofWealthMode()
	{
		return RingOfWealthMode.OFF;
	}

	@ConfigItem(
			keyName = "drakanmode",
			name = "Drakan's Medallion",
			description = "Swap the left click option with the desired teleport location on a Drakan's Medallion.",
			//position = 22,
			section = teleportationSection
	)
	default DrakanMode getDrakanMode() {return DrakanMode.OFF;}

	@ConfigItem(
			keyName = "skullSceptre",
			name = "Skull Sceptre",
			description = "Swap the left click option with `teleport` on the Skull Sceptre",
			//position = 22,
			section = teleportationSection
	)
	default boolean skullSceptre() {return false;}

	@ConfigItem(
			keyName = "teleportCrystal",
			name = "Teleport Crystal",
			description = "Swap the left click teleport option to Prifddinas",
			//position = 22,
			section = teleportationSection
	)
	default boolean teleportCrystal() {return false;}

	//------------------------------------------------------------//
	// Cape Swaps
	//------------------------------------------------------------//
	@ConfigItem(
			keyName = "swapConstructionCape",
			name = "Construction Cape",
			description = "Swap the left click option with 'Tele to POH' on a Construction Cape.",
			position = 90,
			section = teleportationSection
	)
	default ConstructionCapeMode getConstructionCapeMode()
	{
		return ConstructionCapeMode.OFF;
	}

	@ConfigItem(
			keyName = "swapCraftingCape",
			name = "Crafting Cape",
			description = "Swap the left click option with 'teleport' on a Crafting Cape.",
			position = 91,
			section = teleportationSection
	)
	default CraftingCapeMode getCraftingCapeMode()
	{
		return CraftingCapeMode.OFF;
	}

	@ConfigItem(
			keyName = "swapFarmingCape",
			name = "Farming Cape",
			description = "Swap the left click option with 'teleport' on a Farming Cape.",
			position = 92,
			section = teleportationSection
	)
	default FarmingCapeMode getFarmingCapeMode()
	{
		return FarmingCapeMode.OFF;
	}

	@ConfigItem(
			keyName = "magicCapeMode",
			name = "Magic Cape",
			description = "Swap the left click option with 'spellbook' on a Magic Cape.",
			position = 93,
			section = teleportationSection
	)
	default MagicCapeMode getMagicCapeMode()
	{
		return MagicCapeMode.OFF;
	}

	@ConfigItem(
			keyName = "swapMaxCape",
			name = "Max Cape",
			description = "Swap the left click 'remove' option with another on a worn Max Cape.",
			position = 94,
			section = teleportationSection,
			disabledBy = "karambwans"
	)
	default boolean swapMaxCape()
	{
		return false;
	}

	@ConfigItem(
			keyName = "swapMaxCapeEquipped",
			name = "Mode",
			description = "",
			position = 94,
			section = teleportationSection,
			hidden = true,
			unhide = "swapMaxCape"
	)
	default MaxCapeEquippedMode getMaxCapeEquippedMode()
	{
		return MaxCapeEquippedMode.OFF;
	}

	@ConfigItem(
			keyName = "musicCapeMode",
			name = "Music Cape",
			description = "Swap the left click option with 'teleport' on a Music Cape.",
			position = 95,
			section = teleportationSection
	)
	default MusicCapeMode getMusicCapeMode()
	{
		return MusicCapeMode.OFF;
	}

	@ConfigItem(
			keyName = "mythCapeMode",
			name = "Myth Cape",
			description = "Swap the left click option with 'teleport' on a Mythical Cape.",
			position = 96,
			section = teleportationSection
	)
	default MythCapeMode getMythCapeMode()
	{
		return MythCapeMode.OFF;
	}

	@ConfigItem(
			keyName = "questCapeMode",
			name = "Quest Point Cape",
			description = "Swap the left click option with 'teleport' on a Quest Point Cape.",
			position = 97,
			section = teleportationSection
	)
	default QuestCapeMode getQuestCapeMode()
	{
		return QuestCapeMode.OFF;
	}


	//------------------------------------------------------------//
	// Misc
	//------------------------------------------------------------//

	@ConfigItem(
			keyName = "hideTradeWith",
			name = "Hide 'Trade With'",
			description = "Hides the 'Trade with' option from the right click menu.",
			position = 0,
			section = miscSection
	)
	default boolean hideTradeWith()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hideEmpty",
			name = "Hide 'Empty'",
			description = "Hides the 'Empty' option from the right click menu for potions.",
			position = 1,
			section = miscSection
	)
	default boolean hideEmpty()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hideExamine",
			name = "Hide 'Examine'",
			description = "Hides the 'Examine' option from the right click menu.",
			position = 2,
			section = miscSection
	)
	default boolean hideExamine()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hideReport",
			name = "Hide 'Report'",
			description = "Hides the 'Report' option from the right click menu.",
			position = 3,
			section = miscSection
	)
	default boolean hideReport()
	{
		return false;
	}

	/*@ConfigItem(
			keyName = "hideCancel",
			name = "Hide 'Cancel'",
			description = "Hides the 'Cancel' option from the right click menu.",
			position = 4,
			section = miscSection
	)
	default boolean hideCancel()
	{
		return false;
	}*/

	@ConfigItem(
			keyName = "hideDestroy",
			name = "Hide 'Destroy' Rune Pouch",
			description = "Hides the 'Destroy' option from rune pouch.",
			position = 5,
			section = miscSection
	)
	default boolean hideDestroy()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hideLootImpJars",
			name = "Hide 'Loot' Impling Jars",
			description = "Hides the 'Loot' option from impling jars if you have the type of clue.",
			position = 6,
			section = miscSection
	)
	default boolean hideLootImpJars()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hideRestoreMutagen",
			name = "Hide 'Restore' Mutagens",
			description = "Hides the 'restore' option on mutagen helms.",
			position = 7,
			section = miscSection
	)
	default boolean hideRestoreMutagen()
	{
		return false;
	}

	@ConfigItem(
			keyName = "swapBloom",
			name = "Bloom",
			description = "Change left click option to bloom",
			position = 8,
			section = miscSection
	)
	default boolean swapBloom() {
		return false;
	}

	@ConfigItem(
			keyName = "swapMetamorphosis",
			name = "Metamorphosis",
			description = "Change left click option on pets so you can spam metamorphosis like a fucking degenerate",
			position = 9,
			section = miscSection
	)
	default boolean swapMetamorphosis() {
		return false;
	}

	@ConfigItem(
			keyName = "swapShapBuy",
			name = "Shop Buy",
			description = "Swap value with buy options",
			position = 10,
			section = miscSection
	)
	default BuyMode swapShapBuy() { return BuyMode.VALUE; }

	@ConfigItem(
			keyName = "swapShap",
			name = "Shop Sell",
			description = "Swap value with sell options",
			position = 11,
			section = miscSection
	)
	default SellMode swapShapSell() { return SellMode.VALUE; }

	//------------------------------------------------------------//
	// PVM
	//------------------------------------------------------------//

	@ConfigItem(
			keyName = "hideCastRaids",
			name = "Hide Cast On Players In Raids",
			description = "Hides the cast option for players while in raids.",
			position = 1,
			section = pvmSection
	)
	default boolean hideCastRaids()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hideCastIgnoredSpells",
			name = "Ignored Spells",
			description = "Spells that should not be hidden from being cast, separated by a comma",
			position = 2,
			section = pvmSection,
			hidden = true,
			unhide = "hideCastRaids"
	)
	default String hideCastIgnoredSpells()
	{
		return "cure other, energy transfer, heal other, vengeance other";
	}

	@ConfigItem(
			keyName = "hideCastThralls",
			name = "Hide Cast On Thralls",
			description = "Hides the cast option on thralls.",
			position = 3,
			section = pvmSection
	)
	default boolean hideCastThralls()
	{
		return true;
	}

	@ConfigItem(
			keyName = "swapDustDevils",
			name = "Deprio Attack Dust Devils",
			description = "Deprioritizes attack when a magic weapon is equipped",
			position = 4,
			section = pvmSection
	)
	default boolean swapDustDevils() { return false; }

	@ConfigItem(
			keyName = "swapSmokeDevil",
			name = "Deprio Attack Smoke Devils",
			description = "Deprioritizes attack on small smoke devils <br> When thermo is alive or when you equip a mage weapon",
			position = 5,
			section = pvmSection
	)
	default boolean swapSmokeDevil() { return false; }

	@ConfigItem(
			keyName = "swapNechs",
			name = "Deprio Attack Nechryaels",
			description = "Deprioritizes attack when a magic weapon is equipped",
			position = 6,
			section = pvmSection
	)
	default boolean swapNechs() { return false; }

	@ConfigItem(
			keyName = "removeSireSpawns",
			name = "Deprio Attack Sire Minions",
			description = "Deprioritizes attack option on spawns and scions at Sire",
			position = 7,
			section = pvmSection
	)
	default boolean removeSireSpawns() { return true; }

	@ConfigItem(
			keyName = "deprioVetion",
			name = "Deprio Attack Vet'ion",
			description = "Deprioritizes attack while skeleton hellhounds are alive",
			position = 8,
			section = pvmSection
	)
	default boolean deprioVetion() { return false; }

	@ConfigItem(
			keyName = "vengDeezNuts",
			name = "Vengeance Message",
			description = "Replaces 'Taste Vengeance' with a custom message",
			position = 9,
			section = pvmSection
	)
	default boolean vengDeezNuts() { return false; }

	@ConfigItem(
			keyName = "vengMessage",
			name = "Vengeance Message Text",
			description = "Sets the overhead text to the custom message when vengeance is popped",
			position = 10,
			section = pvmSection,
			hidden = true,
			unhide = "vengDeezNuts"
	)
	default String vengMessage() { return "Taste Deez Nuts"; }

	@ConfigItem(
			keyName = "hideAttackBandos",
			name = "Hide Attack Graardor",
			description = "Hides attack on Graardor while minions are alive",
			position = 11,
			section = pvmSection
	)
	default boolean hideAttackBandos() { return false; }

	@ConfigItem(
			keyName = "hideAttackBandosMinions",
			name = "Hide Attack Bandos Minions",
			description = "Hides attack on minions while Graardor is alive",
			position = 12,
			section = pvmSection
	)
	default boolean hideAttackBandosMinions() { return false; }

	@ConfigItem(
			keyName = "hideAttackSara",
			name = "Hide Attack Zilyana",
			description = "Hides attack on Zilyana when any minion is alive",
			position = 13,
			section = pvmSection
	)
	default boolean hideAttackSara() { return false; }

	@ConfigItem(
			keyName = "hideAttackSaraMinions",
			name = "Hide Attack Sara Minions",
			description = "Hides attack on Starlight while Zilyana is alive",
			position = 14,
			section = pvmSection
	)
	default boolean hideAttackSaraMinions() { return true; }

	@ConfigItem(
			keyName = "hideAttackZammy",
			name = "Hide Attack K'ril",
			description = "Hides attack on K'ril when any minion is alive",
			position = 15,
			section = pvmSection
	)
	default boolean hideAttackZammy() { return false; }

	@ConfigItem(
			keyName = "hideAttackZammyMinions",
			name = "Hide Attack Zammy Minions",
			description = "Hides attack on minions when Kril is alive",
			position = 16,
			section = pvmSection
	)
	default boolean hideAttackZammyMinions() { return false; }

	@ConfigItem(
			keyName = "hideAttackKree",
			name = "Deprio Attack Kree",
			description = "Deprioritizes attack on Kree when any minion is alive",
			position = 17,
			section = pvmSection
	)
	default boolean hideAttackKree() { return false; }

	@ConfigItem(
			keyName = "hideAttackArmaMinions",
			name = "Deprio Attack Arma Minions",
			description = "Deprioritizes attack on minions while Kree is alive",
			position = 18,
			section = pvmSection
	)
	default boolean hideAttackArmaMinions() { return false; }

	@ConfigItem(
			keyName = "minionSelect",
			name = "Gwd Minion Selector",
			description = "Selects which minions to highlight",
			position = 95,
			section = pvmSection
	)
	default MinionSelector minionSelect() { return MinionSelector.ALL;}

	@ConfigItem(
			keyName = "highlightMinions",
			name = "Highlight Gwd Minions",
			description = "Highlights the Gwd minions with the color of their attack style",
			position = 96,
			section = pvmSection
	)
	default MinionHighlightMode highlightMinions() { return MinionHighlightMode.OFF;}

	@Range(min = 1, max = 5)
	@ConfigItem(
			keyName = "gwdThicc",
			name = "Minion Width",
			description = "Adjusts the width of the minion highlights",
			position = 97,
			section = pvmSection
	)
	default int gwdThicc() { return 2; }

	@Range(min = 0, max = 255)
	@ConfigItem(
			keyName = "gwdOpacity",
			name = "Minion Opacity",
			description = "Adjusts the opacity of the minion highlights",
			position = 98,
			section = pvmSection
	)
	default int gwdOpacity() { return 20; }

	@ConfigItem(
			keyName = "minionRespawn",
			name = "Minion Respawn Timers",
			description = "Shows the area, time and attack style of the minion that is respawning - only works while the boss is alive",
			position = 99,
			section = pvmSection
	)
	default boolean minionRespawn() {
		return true;
	}

	//------------------------------------------------------------//
	// Custom swaps
	//------------------------------------------------------------//

	@ConfigItem(
			name = "Custom Swaps Toggle",
			keyName = "customSwapsToggle",
			description = "Toggles the use of the Custom Swaps",
			section = customSwapsSection,
			position = 3
	)
	default boolean customSwapsToggle()
	{
		return false;
	}

	@ConfigItem(
			name = "Custom Swaps",
			keyName = "customSwapsStr",
			description = "",
			section = customSwapsSection,
			position = 4
	)
	default String customSwapsString()
	{
		return "";
	}

	@ConfigItem(
			name = "Bank Swaps",
			keyName = "bankCustomSwapsStr",
			description = "",
			section = customSwapsSection,
			position = 5
	)
	default String bankCustomSwapsString()
	{
		return "";
	}

	@ConfigItem(
			name = "Shift Swaps Toggle",
			keyName = "shiftCustomSwapsToggle",
			description = "Toggles the use of the Shift Swaps",
			section = shiftCustomSwapsSection,
			position = 6
	)
	default boolean shiftCustomSwapsToggle()
	{
		return false;
	}

	@ConfigItem(
			name = "Shift Swaps",
			keyName = "shiftCustomSwapsStr",
			description = "",
			section = shiftCustomSwapsSection,
			position = 7
	)
	default String shiftCustomSwapsString()
	{
		return "";
	}

	@ConfigItem(
			name = "Shift Bank Swaps",
			keyName = "bankShiftCustomSwapsStr",
			description = "",
			section = shiftCustomSwapsSection,
			position = 8
	)
	default String bankShiftCustomSwapsString()
	{
		return "";
	}

	@ConfigItem(
			name = "Hotkey Swaps Toggle",
			keyName = "keyCustomSwapsToggle",
			description = "Toggles the use of the Hotkey Swaps",
			section = keyCustomSwapsSection,
			position = 1
	)
	default boolean keyCustomSwapsToggle()
	{
		return false;
	}

	@ConfigItem(
			keyName = "hotkey",
			name = "Set Hotkey",
			description = "Binds the key to hold to enable this section",
			section = keyCustomSwapsSection,
			position = 2
	)
	default Keybind hotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			name = "Hotkey Swaps",
			keyName = "keyCustomSwapsStr",
			description = "",
			section = keyCustomSwapsSection,
			position = 3
	)
	default String keyCustomSwapsString()
	{
		return "";
	}

	@ConfigItem(
			name = "Hotkey Bank Swaps",
			keyName = "bankKeyCustomSwapsStr",
			description = "",
			section = keyCustomSwapsSection,
			position = 4
	)
	default String bankKeyCustomSwapsString()
	{
		return "";
	}

	@ConfigItem(
			name = "Remove Options Toggle",
			keyName = "removeOptionsToggle",
			description = "Toggles the use of the removing options",
			section = removeSwapsSection,
			position = 9
	)
	default boolean removeOptionsToggle()
	{
		return false;
	}

	@ConfigItem(
			name = "Remove Options",
			keyName = "removeOptionsStr",
			description = "",
			section = removeSwapsSection,
			position = 10
	)
	default String removeOptionsString()
	{
		return "";
	}

	//------------------------------------------------------------//
	// Audio
	//------------------------------------------------------------//

	@ConfigItem(
			keyName = "muteThralls",
			position = 0,
			name = "Mute Thralls",
			description = "Stop making annoying sounds please",
			section = audioSection)
	default boolean muteThralls() { return false; }

	@ConfigItem(
			keyName = "cannonPing",
			position = 1,
			name = "Empty Cannon Warning",
			description = "I couldn't use any other sound, it had to be this",
			section = audioSection)
	default boolean cannonPing() { return false; }

	@Range(min = 1, max = 100)
	@ConfigItem(
			keyName = "cannonPingVolume",
			position = 2,
			name = "Cannon Warning Volume",
			description = "Sets volume of empty cannon warning",
			section = audioSection)
	default int cannonPingVolume() { return 50; }

	@ConfigItem(
			keyName = "soundFilePath",
			position = 3,
			name = "Sound File Path",
			description = "Path to sound files for plugins to play from",
			section = audioSection)
	default String soundFilePath() { return ""; }

	@ConfigItem(
			keyName = "deathSounds",
			position = 4,
			name = "Death Sounds",
			description = "Plays sounds on death",
			section = audioSection)
	default boolean deathSounds() { return false; }

	@ConfigItem(
			keyName = "deathSoundsNames",
			position = 5,
			name = "Death Sounds Names",
			description = "Enter the name of players you also want to play death sounds. Separate each name with a ,",
			section = audioSection)
	default String deathSoundsNames() { return ""; }

	@Range(min = 0, max = 100)
	@ConfigItem(
			keyName = "deathSoundsVolume",
			name = "Death Sounds Volume",
			position = 6,
			description = "Control volume of death sounds",
			section = audioSection)
	default int deathSoundsVolume() { return 0; }
}
