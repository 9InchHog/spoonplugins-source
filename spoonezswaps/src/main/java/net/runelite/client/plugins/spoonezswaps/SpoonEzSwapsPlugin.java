package net.runelite.client.plugins.spoonezswaps;

import com.google.common.collect.*;
import com.google.inject.Provides;
import com.openosrs.client.util.WeaponMap;
import com.openosrs.client.util.WeaponStyle;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.menuentryswapper.Swap;
import net.runelite.client.plugins.spoonezswaps.config.*;
import net.runelite.client.plugins.spoonezswaps.util.AbstractComparableEntry;
import net.runelite.client.plugins.spoonezswaps.util.CustomSwaps;
import net.runelite.client.plugins.spoonezswaps.util.DelayUtils;
import net.runelite.client.plugins.spoonezswaps.util.MinionData;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.base.Predicates.equalTo;

@Extension
@PluginDescriptor(
	name = "<html><font color=#25c550>[S] Ez Swaps",
	enabledByDefault = false,
	description = "A shit ton of menu entry swapper stuff.",
	tags = {"pickpocket", "equipped items", "inventory", "items", "equip", "construction", "spoon", "ez", "skilling", "pvm", "custom", "swapper"}
)
public class SpoonEzSwapsPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private SpoonEzSwapsConfig config;

	@Inject
	private MinionOverlay overlay;

	@Inject
	private SkillingOverlay skillingOverlay;

	@Inject
	private PluginManager pluginManager;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private MenuManager menuManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	public EventBus eventBus;

	@Inject
	private CustomSwaps customswaps;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private DelayUtils delayUtils;

	private static <T extends Comparable<? super T>> void sortedInsert(List<T> list, T value) // NOPMD: UnusedPrivateMethod: false positive
	{
		int idx = Collections.binarySearch(list, value);
		list.add(idx < 0 ? -idx - 1 : idx, value);
	}

	List<String> targetList;
	List<String> optionsList;

	private static final Set<MenuAction> NPC_MENU_TYPES = ImmutableSet.of(
			MenuAction.NPC_FIRST_OPTION,
			MenuAction.NPC_SECOND_OPTION,
			MenuAction.NPC_THIRD_OPTION,
			MenuAction.NPC_FOURTH_OPTION,
			MenuAction.NPC_FIFTH_OPTION,
			MenuAction.EXAMINE_NPC);

	private final Multimap<String, Swap> swaps = Multimaps.synchronizedSetMultimap(LinkedHashMultimap.create());
	private final ArrayListMultimap<String, Integer> optionIndexes = ArrayListMultimap.create();

	private List<String> bankItemNames = new ArrayList<>();
	private List<String> hideCastIgnoredSpells = new ArrayList<>();

	public boolean depositTab = false;
	public ArrayList<String> customDropList = new ArrayList<>();

	private static Clip clip;

	private WeaponStyle weaponStyle;
	private boolean skipTickCheck = false;

	public static final Set<Integer> SIRE_IDS = ImmutableSet.of(
			NpcID.ABYSSAL_SIRE, NpcID.ABYSSAL_SIRE_5887, NpcID.ABYSSAL_SIRE_5888, NpcID.ABYSSAL_SIRE_5889,
			NpcID.ABYSSAL_SIRE_5890, NpcID.ABYSSAL_SIRE_5891, NpcID.ABYSSAL_SIRE_5908
	);

	private static final String BANDOS_BOSS = "general graardor";
	private static final String SARA_BOSS = "commander zilyana";
	private static final String ZAMMY_BOSS = "k'ril tsutsaroth";
	private static final String ARMA_BOSS = "kree'arra";

	private static final Set<String> BANDOS_MINIONS = ImmutableSet.of(
			"sergeant grimspike",
			"sergeant steelwill",
			"sergeant strongstack");
	private static final Set<String> SARA_MINIONS = ImmutableSet.of(
			"bree",
			"growler",
			"starlight");
	private static final Set<String> ZAMMY_MINIONS = ImmutableSet.of(
			"balfrug kreeyath",
			"tstanon karlak",
			"zakl'n gritch");
	private static final Set<String> ARMA_MINIONS = ImmutableSet.of(
			"flight kilisa",
			"wingman skree",
			"flockleader geerin");

	//Bandos, Sara, Zammy, Arma
	public ArrayList<Integer> gwdMinonListMelee = new ArrayList<>(Arrays.asList(2216, 2206, 3130, 3165));
	public ArrayList<Integer> gwdMinonListMage = new ArrayList<>(Arrays.asList(2217, 2207, 3132, 3163));
	public ArrayList<Integer> gwdMinonListRange = new ArrayList<>(Arrays.asList(2218, 2208, 3131, 3164));

	//Minion respawn timers
	@Getter
	private List<MinionData> trackedMinions = new ArrayList<>();

	@Getter
	private Instant lastTickUpdate;
	@Getter
	NPC boss = null;

	protected int strungAmuletCount;
	protected int totalAmuletCount;
	protected int cookedPieCount;
	protected int totalPieCount;

	@Provides
	SpoonEzSwapsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SpoonEzSwapsConfig.class);
	}

	@Override
	public void startUp() {
		reset();
		//parseOldFormatConfig();
		parseZCustomSwapperConfig();
		eventBus.register(customswaps);
		customswaps.startup();
		overlayManager.add(overlay);
		overlayManager.add(skillingOverlay);

		if (!config.customDrop().equals("")){
			for (String str : config.customDrop().split(",")) {
				if (!str.trim().equalsIgnoreCase("")) {
					customDropList.add(str.trim());
				}
			}
		}

		loadSwaps();
	}

	@Override
	public void shutDown() {
		eventBus.unregister(customswaps);
		customswaps.shutdown();
		reset();
		swaps.clear();
		trackedMinions.clear();
		overlayManager.remove(overlay);
		overlayManager.remove(skillingOverlay);
	}

	private void reset() {
		clip = null;
		weaponStyle = null;
		customDropList.clear();
	}

	public Swap swap(String option, String swappedOption, Supplier<Boolean> enabled)
	{
		return swap(option, alwaysTrue(), swappedOption, enabled);
	}

	public Swap swap(String option, String target, String swappedOption, Supplier<Boolean> enabled)
	{
		return swap(option, equalTo(target), swappedOption, enabled);
	}

	public Swap swap(String option, Predicate<String> targetPredicate, String swappedOption, Supplier<Boolean> enabled)
	{
		Swap swap = new Swap(alwaysTrue(), targetPredicate, swappedOption, enabled, true);
		swaps.put(option, swap);
		return swap;
	}

	public Swap swapContains(String option, Predicate<String> targetPredicate, String swappedOption,
							 Supplier<Boolean> enabled)
	{
		Swap swap = new Swap(alwaysTrue(), targetPredicate, swappedOption, enabled, false);
		swaps.put(option, swap);
		return swap;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if(event.getGroup().equalsIgnoreCase("spoonezswaps")) {
			if (event.getKey().equals("customDrop")) {
				customDropList.clear();
				if (!config.customDrop().equals("")){
					for (String str : config.customDrop().split(",")) {
						if (!str.trim().equalsIgnoreCase("")) {
							customDropList.add(str.trim());
						}
					}
				}
			}
			loadSwaps();
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOGGED_IN) {
			loadSwaps();
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		if (config.getEasyConstruction()) {
			if ((client.getVarbitValue(2176) != 1) && event.getType() != MenuAction.GAME_OBJECT_FIFTH_OPTION.getId()) {
				return;
			}
			MenuEntry[] menuEntries = client.getMenuEntries();
			swapConstructionMenu(menuEntries);
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if (event.getMenuAction() == MenuAction.ITEM_SECOND_OPTION) {
			WeaponStyle newStyle = WeaponMap.StyleMap.get(event.getId());
			if (newStyle != null) {
				skipTickCheck = true;
				weaponStyle = newStyle;
			}
		}
	}

	private void swapMenuEntry(int index, MenuEntry menuEntry) {
		final int eventId = menuEntry.getIdentifier();
		final MenuAction menuAction = MenuAction.of(menuEntry.getType().getId());
		final String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
		final String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();
		final NPC hintArrowNpc = client.getHintArrowNpc();

		if (hintArrowNpc != null && hintArrowNpc.getIndex() == eventId && NPC_MENU_TYPES.contains(menuAction)) {
			return;
		}

		if (shiftModifier() && (menuAction == MenuAction.ITEM_FIRST_OPTION
				|| menuAction == MenuAction.ITEM_SECOND_OPTION
				|| menuAction == MenuAction.ITEM_THIRD_OPTION
				|| menuAction == MenuAction.ITEM_FOURTH_OPTION
				|| menuAction == MenuAction.ITEM_FIFTH_OPTION
				|| menuAction == MenuAction.ITEM_USE)) {
			return;
		}

		Collection<Swap> swaps = this.swaps.get(option);
		for (Swap swap : swaps) {
			if (swap.getTargetPredicate().test(target) && swap.getEnabled().get()) {
				if (shitSwap(swap.getSwappedOption(), target, index, swap.isStrict())) {
					break;
				}
			}
		}

		if (target.contains("max cape") && option.contains("remove") && config.karambwans()) {
			if (client.getLocalPlayer().getWorldLocation().getRegionID() != 11568) {
				swapStuff("tele to poh", option, target, false);
			} else if (client.getLocalPlayer().getWorldLocation().getRegionID() == 11568) {
				swapStuff("crafting guild", option, target, false);
			}
		}
	}

	private Predicate<String> targetSwap(String string) {
		return (in) -> in.toLowerCase().contains(string);
	}

	private void addSwaps() {
		for (String option : new String[]{"attack", "talk-to"}) {
			swapContains(option, (s) -> true, "pickpocket", config::swapPickpocket);
		}

		for (String customDrop : customDropList) {
			swap("wield", targetSwap(customDrop), "release", () ->
					config.customDrop().contains(customDrop));
			swap("use", targetSwap(customDrop), "drop", () ->
					config.customDrop().contains(customDrop));
		}

		swap("remove", targetSwap("burning amulet"), "chaos temple", () ->
				config.getBurningAmuletMode() == BurningAmuletMode.CHAOS_TEMPLE);
		swap("remove", targetSwap("burning amulet"), "bandit camp", () ->
				config.getBurningAmuletMode() == BurningAmuletMode.BANDIT_CAMP);
		swap("remove", targetSwap("burning amulet"), "lava maze", () ->
				config.getBurningAmuletMode() == BurningAmuletMode.LAVA_MAZE);

		swap("remove", targetSwap("combat bracelet"), "warriors' guild", () ->
				config.getCombatBraceletMode() == CombatBraceletMode.WARRIORS_GUILD);
		swap("remove", targetSwap("combat bracelet"), "champions' guild", () ->
				config.getCombatBraceletMode() == CombatBraceletMode.CHAMPIONS_GUILD);
		swap("remove", targetSwap("combat bracelet"), "edgeville monastery", () ->
				config.getCombatBraceletMode() == CombatBraceletMode.EDGEVILLE_MONASTERY);
		swap("remove", targetSwap("combat bracelet"), "ranging guild", () ->
				config.getCombatBraceletMode() == CombatBraceletMode.RANGING_GUILD);

		swap("remove", targetSwap("games necklace"), "burthorpe", () ->
				config.getGamesNecklaceMode() == GamesNecklaceMode.BURTHORPE);
		swap("remove", targetSwap("games necklace"), "barbarian outpost", () ->
				config.getGamesNecklaceMode() == GamesNecklaceMode.BARBARIAN_OUTPOST);
		swap("remove", targetSwap("games necklace"), "corporeal beast", () ->
				config.getGamesNecklaceMode() == GamesNecklaceMode.CORPOREAL_BEAST);
		swap("remove", targetSwap("games necklace"), "tears of guthix", () ->
				config.getGamesNecklaceMode() == GamesNecklaceMode.TEARS_OF_GUTHIX);
		swap("remove", targetSwap("games necklace"), "wintertodt camp", () ->
				config.getGamesNecklaceMode() == GamesNecklaceMode.WINTER);

		swap("remove", targetSwap("ring of dueling"), "duel arena", () ->
				config.getDuelingRingMode() == DuelingRingMode.DUEL_ARENA);
		swap("remove", targetSwap("ring of dueling"), "castle wars", () ->
				config.getDuelingRingMode() == DuelingRingMode.CASTLE_WARS);
		swap("remove", targetSwap("ring of dueling"), "ferox enclave", () ->
				config.getDuelingRingMode() == DuelingRingMode.FEROX_ENCLAVE);

		swap("remove", targetSwap("amulet of glory"), "edgeville", () ->
				config.getGloryMode() == GloryMode.EDGEVILLE);
		swap("remove", targetSwap("amulet of glory"), "karamja", () ->
				config.getGloryMode() == GloryMode.KARAMJA);
		swap("remove", targetSwap("amulet of glory"), "al kharid", () ->
				config.getGloryMode() == GloryMode.AL_KHARID);
		swap("remove", targetSwap("amulet of glory"), "draynor village", () ->
				config.getGloryMode() == GloryMode.DRAYNOR_VILLAGE);
		swap("remove", targetSwap("amulet of eternal glory"), "edgeville", () ->
				config.getGloryMode() == GloryMode.EDGEVILLE);
		swap("remove", targetSwap("amulet of eternal glory"), "karamja", () ->
				config.getGloryMode() == GloryMode.KARAMJA);
		swap("remove", targetSwap("amulet of eternal glory"), "al kharid", () ->
				config.getGloryMode() == GloryMode.AL_KHARID);
		swap("remove", targetSwap("amulet of eternal glory"), "draynor village", () ->
				config.getGloryMode() == GloryMode.DRAYNOR_VILLAGE);

		swap("remove", targetSwap("skills necklace"), "fishing guild", () ->
				config.getSkillsNecklaceMode() == SkillsNecklaceMode.FISHING_GUILD);
		swap("remove", targetSwap("skills necklace"), "mining guild", () ->
				config.getSkillsNecklaceMode() == SkillsNecklaceMode.MINING_GUILD);
		swap("remove", targetSwap("skills necklace"), "farming guild", () ->
				config.getSkillsNecklaceMode() == SkillsNecklaceMode.FARMING_GUILD);
		swap("remove", targetSwap("skills necklace"), "cooking guild", () ->
				config.getSkillsNecklaceMode() == SkillsNecklaceMode.COOKING_GUILD);
		swap("remove", targetSwap("skills necklace"), "woodcutting guild", () ->
				config.getSkillsNecklaceMode() == SkillsNecklaceMode.WOODCUTTING_GUILD);
		swap("remove", targetSwap("skills necklace"), "crafting guild", () ->
				config.getSkillsNecklaceMode() == SkillsNecklaceMode.CRAFTING_GUILD);

		swap("remove", targetSwap("necklace of passage"), "wizards' tower", () ->
				config.getNecklaceofPassageMode() == NecklaceOfPassageMode.WIZARDS_TOWER);
		swap("remove", targetSwap("necklace of passage"), "the outpost", () ->
				config.getNecklaceofPassageMode() == NecklaceOfPassageMode.THE_OUTPOST);
		swap("remove", targetSwap("necklace of passage"), "eagles' eyrie", () ->
				config.getNecklaceofPassageMode() == NecklaceOfPassageMode.EAGLES_EYRIE);

		swap("remove", targetSwap("digsite pendant"), "digsite", () ->
				config.getDigsitePendantMode() == DigsitePendantMode.DIGSITE);
		swap("remove", targetSwap("digsite pendant"), "fossil island", () ->
				config.getDigsitePendantMode() == DigsitePendantMode.FOSSIL_ISLAND);
		swap("remove", targetSwap("digsite pendant"), "lithkren dungeon", () ->
				config.getDigsitePendantMode() == DigsitePendantMode.LITHKREN);

		swap("remove", targetSwap("ring of wealth"), "miscellania", () ->
				config.getRingofWealthMode() == RingOfWealthMode.MISCELLANIA);
		swap("remove", targetSwap("ring of wealth"), "grand exchange", () ->
				config.getRingofWealthMode() == RingOfWealthMode.GRAND_EXCHANGE);
		swap("remove", targetSwap("ring of wealth"), "falador", () ->
				config.getRingofWealthMode() == RingOfWealthMode.FALADOR);
		swap("remove", targetSwap("ring of wealth"), "dondakan", () ->
				config.getRingofWealthMode() == RingOfWealthMode.DONDAKAN);

		swap("remove", targetSwap("talisman"), "xeric's glade", () ->
				config.getXericsTalismanMode() == XericsTalismanMode.XERICS_GLADE);
		swap("remove", targetSwap("talisman"), "xeric's lookout", () ->
				config.getXericsTalismanMode() == XericsTalismanMode.XERICS_LOOKOUT);
		swap("remove", targetSwap("talisman"), "xeric's inferno", () ->
				config.getXericsTalismanMode() == XericsTalismanMode.XERICS_INFERNO);
		swap("remove", targetSwap("talisman"), "xeric's heart", () ->
				config.getXericsTalismanMode() == XericsTalismanMode.XERICS_HEART);
		swap("remove", targetSwap("talisman"), "xeric's honour", () ->
				config.getXericsTalismanMode() == XericsTalismanMode.XERICS_HONOUR);

		swap("wear", targetSwap("drakan's medallion"), "ver sinhaza", () ->
				config.getDrakanMode() == DrakanMode.VER_SINHAZA);
		swap("wear", targetSwap("drakan's medallion"), "darkmeyer", () ->
				config.getDrakanMode() == DrakanMode.DARKMEYER);
		swap("wear", targetSwap("drakan's medallion"), "slepe", () ->
				config.getDrakanMode() == DrakanMode.SLEPE);
		swap("remove", targetSwap("drakan's medallion"), "ver sinhaza", () ->
				config.getDrakanMode() == DrakanMode.VER_SINHAZA);
		swap("remove", targetSwap("drakan's medallion"), "darkmeyer", () ->
				config.getDrakanMode() == DrakanMode.DARKMEYER);
		swap("remove", targetSwap("drakan's medallion"), "slepe", () ->
				config.getDrakanMode() == DrakanMode.SLEPE);

		swap("lletya", targetSwap("teleport crystal"), "prifddinas", () ->
				config.teleportCrystal());

		swap("wield", targetSwap("skull sceptre"), "invoke", () ->
				config.skullSceptre());
		swap("remove", targetSwap("skull sceptre"), "invoke", () ->
				config.skullSceptre());

		//Diary
		swap("wear", targetSwap("ardougne cloak"), "farm teleport", () ->
				config.swapArdyCloak() == ArdyCloakMode.ARDOUGNE_FARM);
		swap("wear", targetSwap("ardougne cloak"), "monastery teleport", () ->
				config.swapArdyCloak() == ArdyCloakMode.KANDARIN_MONASTERY);
		swap("wear", targetSwap("ardougne max cape"), "farm teleport", () ->
				config.swapArdyCloak() == ArdyCloakMode.ARDOUGNE_FARM);
		swap("wear", targetSwap("ardougne max cape"), "monastery teleport", () ->
				config.swapArdyCloak() == ArdyCloakMode.KANDARIN_MONASTERY);
		swap("remove", targetSwap("ardougne cloak"), "ardougne farm", () ->
				config.swapArdyCloak() == ArdyCloakMode.ARDOUGNE_FARM);
		swap("remove", targetSwap("ardougne cloak"), "kandarin monastery", () ->
				config.swapArdyCloak() == ArdyCloakMode.KANDARIN_MONASTERY);
		swap("remove", targetSwap("ardougne max cape"), "ardougne farm", () ->
				config.swapArdyCloak() == ArdyCloakMode.ARDOUGNE_FARM);
		swap("remove", targetSwap("ardougne max cape"), "kandarin monastery", () ->
				config.swapArdyCloak() == ArdyCloakMode.KANDARIN_MONASTERY);

		swap("wear", targetSwap("desert amulet"), "nardah", () ->
				config.swapDesertAmulet() == DesertAmuletMode.NARDAH);
		swap("wear", targetSwap("desert amulet"), "kalphite cave", () ->
				config.swapDesertAmulet() == DesertAmuletMode.KALPHITE_CAVE);
		swap("remove", targetSwap("desert amulet"), "nardah", () ->
				config.swapDesertAmulet() == DesertAmuletMode.NARDAH);
		swap("remove", targetSwap("desert amulet"), "kalphite cave", () ->
				config.swapDesertAmulet() == DesertAmuletMode.KALPHITE_CAVE);

		swap("wield", targetSwap("ghommal's hilt"), "trollheim", () ->
				config.getCaHiltMode() == CaHiltMode.GWD);
		swap("wield", targetSwap("ghommal's hilt"), "mor ul rek", () ->
				config.getCaHiltMode() == CaHiltMode.ZUK);
		swap("remove", targetSwap("ghommal's hilt"), "trollheim", () ->
				config.getCaHiltMode() == CaHiltMode.GWD);
		swap("remove", targetSwap("ghommal's hilt"), "mor ul rek", () ->
				config.getCaHiltMode() == CaHiltMode.ZUK);

		swap("wear", targetSwap("karamja gloves"), "duradel", () ->
				config.swapKaramjaGloves() == KaramjaGlovesMode.DURADEL);
		swap("wear", targetSwap("karamja gloves"), "gem mine", () ->
				config.swapKaramjaGloves() == KaramjaGlovesMode.GEM_MINE);
		swap("remove", targetSwap("karamja gloves"), "duradel", () ->
				config.swapKaramjaGloves() == KaramjaGlovesMode.DURADEL);
		swap("remove", targetSwap("karamja gloves"), "gem mine", () ->
				config.swapKaramjaGloves() == KaramjaGlovesMode.GEM_MINE);

		swap("wear", targetSwap("morytania legs"), "ecto teleport", () ->
				config.swapMory() == MoryMode.ECTO);
		swap("wear", targetSwap("morytania legs"), "burgh teleport", () ->
				config.swapMory() == MoryMode.BURGH);
		swap("remove", targetSwap("morytania legs"), "ecto teleport", () ->
				config.swapMory() == MoryMode.ECTO);
		swap("remove", targetSwap("morytania legs"), "burgh teleport", () ->
				config.swapMory() == MoryMode.BURGH);

		swap("wear", targetSwap("fremennik sea boots"), "teleport", () ->
				config.swapFremmyBoots());
		swap("remove", targetSwap("fremennik sea boots"), "teleport", () ->
				config.swapFremmyBoots());

		swap("wear", targetSwap("kandarin headgear"), "teleport", () ->
				config.swapKandarinHelm());
		swap("remove", targetSwap("kandarin headgear"), "teleport", () ->
				config.swapKandarinHelm());

		swap("equip", targetSwap("rada's blessing"), "mount karuulm", () ->
				config.swapRadasBlessing());
		swap("remove", targetSwap("rada's blessing"), "mount karuulm", () ->
				config.swapRadasBlessing());

		//Cape Swaps
		swap("wear", targetSwap("crafting cape"), "teleport", () ->
				config.getCraftingCapeMode() == CraftingCapeMode.INVENTORY || config.getCraftingCapeMode() == CraftingCapeMode.ALWAYS);
		swap("remove", targetSwap("crafting cape"), "teleport", () ->
				config.getCraftingCapeMode() == CraftingCapeMode.EQUIPPED || config.getCraftingCapeMode() == CraftingCapeMode.ALWAYS);

		swap("wear", targetSwap("construct."), "tele to poh", () ->
				config.getConstructionCapeMode() == ConstructionCapeMode.INVENTORY || config.getConstructionCapeMode() == ConstructionCapeMode.ALWAYS);
		swap("remove", targetSwap("construct."), "tele to poh", () ->
				config.getConstructionCapeMode() == ConstructionCapeMode.EQUIPPED || config.getConstructionCapeMode() == ConstructionCapeMode.ALWAYS);

		swap("wear", targetSwap("magic cape"), "spellbook", () ->
				config.getMagicCapeMode() == MagicCapeMode.INVENTORY || config.getMagicCapeMode() == MagicCapeMode.ALWAYS);
		swap("remove", targetSwap("magic cape"), "spellbook", () ->
				config.getMagicCapeMode() == MagicCapeMode.EQUIPPED || config.getMagicCapeMode() == MagicCapeMode.ALWAYS);

		swap("remove", targetSwap("max cape"), "tele to poh", () ->
				config.swapMaxCape() && config.getMaxCapeEquippedMode() == MaxCapeEquippedMode.TELE_TO_POH);
		swap("remove", targetSwap("max cape"), "crafting guild", () ->
				config.swapMaxCape() && config.getMaxCapeEquippedMode() == MaxCapeEquippedMode.CRAFTING_GUILD);
		swap("remove", targetSwap("max cape"), "warriors' guild", () ->
				config.swapMaxCape() && config.getMaxCapeEquippedMode() == MaxCapeEquippedMode.WARRIORS_GUILD);
		swap("remove", targetSwap("max cape"), "fishing teleports", () ->
				config.swapMaxCape() && config.getMaxCapeEquippedMode() == MaxCapeEquippedMode.FISHING_TELEPORTS);
		swap("remove", targetSwap("max cape"), "poh portals", () ->
				config.swapMaxCape() && config.getMaxCapeEquippedMode() == MaxCapeEquippedMode.POH_PORTRALS);
		swap("remove", targetSwap("max cape"), "other teleports", () ->
				config.swapMaxCape() && config.getMaxCapeEquippedMode() == MaxCapeEquippedMode.OTHER_TELEPORTS);
		swap("remove", targetSwap("max cape"), "spellbook", () ->
				config.swapMaxCape() && config.getMaxCapeEquippedMode() == MaxCapeEquippedMode.SPELLBOOK);
		swap("remove", targetSwap("max cape"), "features", () ->
				config.swapMaxCape() && config.getMaxCapeEquippedMode() == MaxCapeEquippedMode.FEATURES);

		swap("wear", targetSwap("music cape"), "teleport", () ->
				config.getMusicCapeMode() == MusicCapeMode.INVENTORY || config.getMusicCapeMode() == MusicCapeMode.ALWAYS);
		swap("remove", targetSwap("music cape"), "teleport", () ->
				config.getMusicCapeMode() == MusicCapeMode.EQUIPPED || config.getMusicCapeMode() == MusicCapeMode.ALWAYS);

		swap("wear", targetSwap("quest point cape"), "teleport", () ->
				config.getQuestCapeMode() == QuestCapeMode.INVENTORY || config.getQuestCapeMode() == QuestCapeMode.ALWAYS);
		swap("remove", targetSwap("quest point cape"), "teleport", () ->
				config.getQuestCapeMode() == QuestCapeMode.EQUIPPED || config.getQuestCapeMode() == QuestCapeMode.ALWAYS);

		swap("wear", targetSwap("mythical cape"), "teleport", () ->
				config.getMythCapeMode() == MythCapeMode.INVENTORY || config.getMythCapeMode() == MythCapeMode.ALWAYS);
		swap("remove", targetSwap("mythical cape"), "teleport", () ->
				config.getMythCapeMode() == MythCapeMode.EQUIPPED || config.getMythCapeMode() == MythCapeMode.ALWAYS);
		swap("wear", targetSwap("mythical max cape"), "teleport", () ->
				config.getMythCapeMode() == MythCapeMode.INVENTORY || config.getMythCapeMode() == MythCapeMode.ALWAYS);
		swap("remove", targetSwap("mythical max cape"), "teleport", () ->
				config.getMythCapeMode() == MythCapeMode.EQUIPPED || config.getMythCapeMode() == MythCapeMode.ALWAYS);

		swap("wear", targetSwap("farming cape"), "teleport", () ->
				config.getFarmingCapeMode() == FarmingCapeMode.INVENTORY || config.getFarmingCapeMode() == FarmingCapeMode.ALWAYS);
		swap("remove", targetSwap("farming cape"), "teleport", () ->
				config.getFarmingCapeMode() == FarmingCapeMode.EQUIPPED || config.getFarmingCapeMode() == FarmingCapeMode.ALWAYS);

		//Misc Swaps
		swap("wield", targetSwap("silver sickle (b)"), "bloom", () ->
				config.swapBloom());
		swap("wield", targetSwap("ivandis"), "bloom", () ->
				config.swapBloom());
		swap("wield", targetSwap("blisterwood flail"), "bloom", () ->
				config.swapBloom());
		swap("remove", targetSwap("silver sickle (b)"), "bloom", () ->
				config.swapBloom());
		swap("remove", targetSwap("ivandis"), "bloom", () ->
				config.swapBloom());
		swap("remove", targetSwap("blisterwood flail"), "bloom", () ->
				config.swapBloom());

		swap("value", "buy 1", () -> config.swapShapBuy() == BuyMode.BUY_1);
		swap("value", "buy 5", () -> config.swapShapBuy() == BuyMode.BUY_5);
		swap("value", "buy 10", () -> config.swapShapBuy() == BuyMode.BUY_10);
		swap("value", "buy 50", () -> config.swapShapBuy() == BuyMode.BUY_50);

		swap("value", "sell 1", () -> config.swapShapSell() == SellMode.SELL_1);
		swap("value", "sell 5", () -> config.swapShapSell() == SellMode.SELL_5);
		swap("value", "sell 10", () -> config.swapShapSell() == SellMode.SELL_10);
		swap("value", "sell 50", () -> config.swapShapSell() == SellMode.SELL_50);

		swap("talk-to", "metamorphosis", () -> config.swapMetamorphosis());
		//end of swaps
	}

	private void loadSwaps() {
		addSwaps();
		loadConstructionItems();
		hideCastIgnoredSpells = Text.fromCSV(config.hideCastIgnoredSpells());
	}

	private final Predicate<MenuEntry> filterMenuEntries = entry -> {
		String option = Text.standardize(entry.getOption(), true).toLowerCase();
		String target = Text.standardize(entry.getTarget(), true).toLowerCase();

		boolean potOptions = !target.contains("potion") && !target.contains("anti") && !target.contains("super")
				&& !target.contains("divine") && !target.contains("serum") && !target.contains("guthix")
				&& !target.contains("brew") && !target.contains("venom") && !target.contains("restore")
				&& !target.contains("extended");

		if (config.hideEmpty() && option.contains("empty")) {
			return potOptions;
		}

		if (config.swapHerblore() && option.contains("drink")) {
			return potOptions;
		}

		if (config.hideTradeWith() && option.contains("trade with")) {
			return false;
		}

		if (config.hideDestroy() && option.contains("destroy") && target.contains("rune pouch")) {
			return false;
		}

		if (config.hideExamine() && option.contains("examine")) {
			return false;
		}

		if (config.hideReport() && option.contains("report")) {
			return false;
		}

		/*if (config.hideCancel() && option.contains("cancel")) {
			return false;
		}*/

		if (config.hideRestoreMutagen() && (target.contains("tanzanite helm") || target.contains("magma helm")) && option.contains("restore")) {
			return false;
		}

		if (config.hideLootImpJars() && target.contains("impling") && option.contains("loot")) {
			if (client.getItemContainer(InventoryID.BANK) != null) {
				bankItemNames = new ArrayList<>();
				for (Item i : Objects.requireNonNull(client.getItemContainer(InventoryID.BANK)).getItems()) {
					bankItemNames.add(client.getItemDefinition((i.getId())).getName());
				}
			}
			List<String> invItemNames = new ArrayList<>();
			switch (target) {
				case "gourmet impling jar":
					if (client.getItemContainer(InventoryID.INVENTORY) != null) {
						for (Item i : Objects.requireNonNull(client.getItemContainer(InventoryID.INVENTORY)).getItems()) {
							invItemNames.add(client.getItemDefinition((i.getId())).getName());
						}
						if ((invItemNames.contains("Clue scroll (easy)") || bankItemNames.contains("Clue scroll (easy)"))) {
							return false;
						}
					}
					break;
				case "young impling jar":
					if (client.getItemContainer(InventoryID.INVENTORY) != null) {
						for (Item i : Objects.requireNonNull(client.getItemContainer(InventoryID.INVENTORY)).getItems()) {
							invItemNames.add(client.getItemDefinition((i.getId())).getName());
						}
						if (invItemNames.contains("Clue scroll (beginner)") || bankItemNames.contains("Clue scroll (beginner)")) {
							return false;
						}
					}
					break;
				case "eclectic impling jar":
					if (client.getItemContainer(InventoryID.INVENTORY) != null) {
						for (Item i : Objects.requireNonNull(client.getItemContainer(InventoryID.INVENTORY)).getItems()) {
							invItemNames.add(client.getItemDefinition((i.getId())).getName());
						}
						if ((invItemNames.contains("Clue scroll (medium)") || bankItemNames.contains("Clue scroll (medium)"))) {
							return false;
						}
					}
					break;
				case "magpie impling jar":
				case "nature impling jar":
				case "ninja impling jar":
					if (client.getItemContainer(InventoryID.INVENTORY) != null) {
						for (Item i : Objects.requireNonNull(client.getItemContainer(InventoryID.INVENTORY)).getItems()) {
							invItemNames.add(client.getItemDefinition((i.getId())).getName());
						}
						if ((invItemNames.contains("Clue scroll (hard)") || bankItemNames.contains("Clue scroll (hard)"))) {
							return false;
						}
					}
					break;
				case "crystal impling jar":
				case "dragon impling jar":
					if (client.getItemContainer(InventoryID.INVENTORY) != null) {
						for (Item i : Objects.requireNonNull(client.getItemContainer(InventoryID.INVENTORY)).getItems()) {
							invItemNames.add(client.getItemDefinition((i.getId())).getName());
						}
						if ((invItemNames.contains("Clue scroll (elite)") || bankItemNames.contains("Clue scroll (elite)"))) {
							return false;
						}
					}
					break;
			}
		}

		if (config.hideCastRaids() && (client.getVar(Varbits.IN_RAID) == 1 || client.getVar(Varbits.THEATRE_OF_BLOOD) == 2)) {
			if (client.getSpellSelected() && !hideCastIgnoredSpells.contains(Text.standardize(client.getSelectedSpellName())) && entry.getType().getId() == MenuAction.SPELL_CAST_ON_PLAYER.getId()) {
				return false;
			}
		}

		if (config.hideCastThralls() && target.contains("thrall") && entry.getType().getId() == MenuAction.SPELL_CAST_ON_NPC.getId()) {
			return false;
		}

		if (config.removeSireSpawns()) {
			String sireTarget = Text.removeTags(entry.getTarget()).toLowerCase();
			if (sireTarget.contains("spawn  (level-60)") || sireTarget.contains("scion  (level-100)")) {
				for (NPC n : client.getNpcs()) {
					if (SIRE_IDS.contains(n.getId()) && !n.isDead()) {
						entry.setDeprioritized(true);
					}
				}
			}
		}

		if (config.hideAttackBandos() || config.hideAttackBandosMinions() || config.hideAttackSara() || config.hideAttackKree()
				|| config.hideAttackSaraMinions() || config.hideAttackZammy() || config.hideAttackZammyMinions() || config.hideAttackArmaMinions())
		{
			if (isInGodWars() || client.isInInstancedRegion())
			{
				boolean bossAlive = false;
				boolean minionsAlive = false;
				for (NPC npc : client.getNpcs())
				{
					if (npc != null && npc.getName() != null && npc.getHealthRatio() != 0)
					{
						String npcName = Text.standardize(npc.getName());
						if ((npcName.contains(BANDOS_BOSS) || npcName.contains(SARA_BOSS) || npcName.contains(ZAMMY_BOSS) || npcName.contains(ARMA_BOSS))
								&& npc.getComposition().getSize() > 1)
						{
							bossAlive = true;
						}
						if (BANDOS_MINIONS.contains(npcName) || SARA_MINIONS.contains(npcName) || ZAMMY_MINIONS.contains(npcName) || ARMA_MINIONS.contains(npcName))
						{
							minionsAlive = true;
						}
					}
				}

				target = target.replace("*", "");

				if (config.hideAttackBandos() && minionsAlive && target.contains(BANDOS_BOSS))
				{
					return false;
				}
				if (config.hideAttackBandosMinions() && bossAlive && BANDOS_MINIONS.contains(target))
				{
					return false;
				}
				if (config.hideAttackSara() && minionsAlive && target.contains(SARA_BOSS))
				{
					return false;
				}
				if (config.hideAttackSaraMinions() && bossAlive && SARA_MINIONS.contains(target))
				{
					return false;
				}
				if (config.hideAttackZammy() && minionsAlive && target.contains(ZAMMY_BOSS))
				{
					return false;
				}
				if (config.hideAttackZammyMinions() && bossAlive && ZAMMY_MINIONS.contains(target))
				{
					return false;
				}
				if (config.hideAttackKree() && minionsAlive && target.contains(ARMA_BOSS) && entry.getType().getId() == MenuAction.NPC_SECOND_OPTION.getId())
				{
					entry.setDeprioritized(true);
				}
				if (config.hideAttackArmaMinions() && bossAlive && ARMA_MINIONS.contains(target) && entry.getType().getId() == MenuAction.NPC_SECOND_OPTION.getId())
				{
					entry.setDeprioritized(true);
				}
			}
		}

		if ((config.swapDustDevils() || config.swapNechs() || config.swapSmokeDevil()) && option.equals("attack")
				&& weaponStyle != null) {
			boolean thermoAlive = false;

			if ((config.swapDustDevils() && (target.contains("dust devil") || target.contains("choke devil")))
					|| (config.swapNechs() && (target.contains("nechrya") || target.contains("death spawn")))) {
				entry.setDeprioritized(weaponStyle == WeaponStyle.MAGIC);
			}

			if (config.swapSmokeDevil() && client.getLocalPlayer() != null) {
				boolean hideAttack = target.contains("smoke devil") && !target.contains("thermonuclear") && !target.contains("pet");

				if (client.getLocalPlayer().getWorldLocation().getRegionID() == 9619) {
					for (NPC smokes : client.getNpcs()) {
						String npcName = Text.standardize(smokes.getName());
						if (smokes.getName() != null) {
							if (npcName.contains("thermonuclear") && smokes.getHealthRatio() != 0) {
								thermoAlive = true;
							}

							if (hideAttack) {
								entry.setDeprioritized(thermoAlive || weaponStyle == WeaponStyle.MAGIC);
							}
						}
					}
				} else if (client.getLocalPlayer().getWorldLocation().getRegionID() == 9363 && hideAttack) {
					entry.setDeprioritized(true);
				}
			}
		}

		if (config.deprioVetion() && client.getVar(Varbits.IN_WILDERNESS) == 1) {
			boolean houndsAlive = false;
			for (NPC npc : client.getNpcs())
			{
				if (npc != null && npc.getName() != null && npc.getHealthRatio() != 0)
				{
					String npcName = Text.standardize(npc.getName());
					if (npcName.contains("skeleton hellhound"))
					{
						houndsAlive = true;
					}
				}
			}

			if (target.contains("vet'ion") && entry.getType().getId() == MenuAction.NPC_SECOND_OPTION.getId()) {
				entry.setDeprioritized(houndsAlive);
			}
		}

		return true;
	};

	private void loadConstructionItems()
	{
		targetList = config.getConstructionMode().getTargetList();
		optionsList = config.getConstructionMode().getOptionsList();
	}

	private void swapConstructionMenu(MenuEntry[] menuEntries)
	{
		for (MenuEntry menuEntry : menuEntries)
		{
			if (validConstructionSwap(menuEntry))
			{
				createConstructionMenu(menuEntry);
			}
		}
	}

	public boolean validConstructionSwap(MenuEntry menuEntry)
	{
		return (matchesConstructionOption(menuEntry) && matchesConstructionTarget(menuEntry));
	}

	public boolean matchesConstructionOption(MenuEntry menuEntry)
	{
		return config.getConstructionMode().getOptionsList().stream()
				.anyMatch(Text.standardize(menuEntry.getOption())::contains);
	}

	public boolean matchesConstructionTarget(MenuEntry menuEntry)
	{
		return config.getConstructionMode().getTargetList().stream()
				.anyMatch(Text.standardize(menuEntry.getTarget())::contains);
	}

	private void createConstructionMenu(MenuEntry menuEntry)
	{
		MenuEntry[] newEntries = new MenuEntry[1];

		newEntries[0] = menuEntry;

		client.setMenuEntries(newEntries);
	}

	private void updateitemCounts() {
		if (config.getStringAmulet() || config.getBakePie()) {
			totalAmuletCount = 0;
			strungAmuletCount = 0;
			cookedPieCount = 0;
			totalPieCount = 0;
			Item[] items = new Item[0];
			ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
			try {
				items = itemContainer.getItems();
			} catch (NullPointerException ignored) {}
			for (int i = 0; i < 28; i++) {
				if (i < items.length) {
					Item item = items[i];
					if (item.getQuantity() > 0)
						if (item.getId() == 1692) {
							totalAmuletCount++;
							strungAmuletCount++;
						} else if (item.getId() == 1673) {
							totalAmuletCount++;
						} else if (item.getId() == 7216) {
							totalPieCount++;
						} else if (item.getId() == 7218) {
							totalPieCount++;
							cookedPieCount++;
						}
				}
			}
		}
	}

	//------------------------------------------------------------//
	// Pvm shit
	//------------------------------------------------------------//
	@Subscribe
	private void onNpcSpawned(NpcSpawned event) {
		NPC npc = event.getNpc();
		boolean npcModified = false;
		if (MinionData.isMinion(npc)) {
			for (MinionData minion : trackedMinions) {
				if (minion.npcIdx == npc.getIndex()) {
					minion.tickDied = -1;
					npcModified = true;
					break;
				}
			}
			if (!npcModified) {
				trackedMinions.add(new MinionData(npc));
			}
		} else if (MinionData.isBoss(npc)) {
			boss = npc;
		}
	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned event) {
		NPC npc = event.getNpc();
		if (!npc.isDead())
			return;
		if (MinionData.isMinion(npc) && boss != null) {
			for (MinionData minion : trackedMinions) {
				if (minion.npcIdx == npc.getIndex()) {
					minion.tickDied = client.getTickCount();
				}
			}
		} else if (MinionData.isBoss(npc)) {
			boss = null;
			for (MinionData trackedMinion : trackedMinions) {
				trackedMinion.tickDied = -1;
			}
		}
	}

	@Subscribe
	public void onOverheadTextChanged(OverheadTextChanged event) {
		if (event.getActor() instanceof Player && config.vengDeezNuts()){
			if(event.getActor().getOverheadText().equals("Taste vengeance!")) {
				event.getActor().setOverheadText(config.vengMessage());
			}
		}
	}

	private final ArrayList<Integer> GWD_MAP_REGIONS = new ArrayList<>(Arrays.asList(11603, 11347, 11346, 11601));

	public boolean isInGodWars() {
		if (client.isInInstancedRegion() && client.getLocalPlayer() != null) {
			return GWD_MAP_REGIONS.contains(WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID());
		} else {
			for (int region : client.getMapRegions()) {
				for (int gwdMapRegion : GWD_MAP_REGIONS) {
					if (region == gwdMapRegion) {
						return true;
					}
				}
			}
		}
		return false;
	}

	//------------------------------------------------------------//
	// Audio shit
	//------------------------------------------------------------//

	@Subscribe
	private void onAreaSoundEffectPlayed(AreaSoundEffectPlayed event){
		if(config.muteThralls()) {
			if (event.getSoundId() == 918 || event.getSoundId() == 2700 || event.getSoundId() == 65535 || event.getSoundId() == 211 || event.getSoundId() == 212) {
				event.consume();
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event){
		String msg = net.runelite.client.util.Text.removeTags(event.getMessage());
		if (config.cannonPing() && msg.equalsIgnoreCase("Your cannon is out of ammo!")){
			try {
				AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(SpoonEzSwapsPlugin.class.getResourceAsStream("m1Ping.wav")));
				AudioFormat format = stream.getFormat();
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				clip = (Clip)AudioSystem.getLine(info);
				clip.open(stream);
				FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
				if (control != null) {
					control.setValue((float)(config.cannonPingVolume() / 2 - 45));
				}
				clip.setFramePosition(0);
				clip.start();
			} catch (Exception var6) {
				clip = null;
			}
		}
	}

	@Subscribe
	private void onActorDeath(ActorDeath event){
		String lowerCaseActor = event.getActor().getName().toLowerCase();
		if(config.deathSounds()) {
			boolean sendSound = false;
			if (lowerCaseActor.equals(client.getLocalPlayer().getName().toLowerCase())) {
				sendSound = true;
			} else {
				String[] nameList = config.deathSoundsNames().split(",");
				for (String name : nameList) {
					if(name.startsWith(" ")){
						name = name.substring(1) ;
					}
					if (lowerCaseActor.equals(name.toLowerCase())) {
						sendSound = true;
					}
				}
			}

			if(sendSound){
				File directoryPath = new File(config.soundFilePath());
				FilenameFilter textFilefilter = new FilenameFilter(){
					public boolean accept(File dir, String name) {
						String lowercaseName = name.toLowerCase();
						if (lowercaseName.endsWith(".wav")) {
							return true;
						} else {
							return false;
						}
					}
				};

				File[] filesList = directoryPath.listFiles(textFilefilter);
				Random rand = new Random();
				if(filesList != null) {
					int rng = rand.nextInt(filesList.length);
					play(filesList[rng]);
				}
			}
		}
	}

	public void play(File soundFile) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(soundFile));
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			int intVol = config.deathSoundsVolume() - 50;
			float floatVol = (float) intVol;

			if(soundFile.getName().equals("thomasEarRape.wav")){
				floatVol += -50.0f;
			}else if(soundFile.getName().equals("godfather.wav")){
				floatVol += 30.0f;
			}

			if(floatVol < -50.0f){
				floatVol = -50.0f;
			}else if(floatVol > 6.0f){
				floatVol = 6.0f;
			}
			gainControl.setValue(floatVol);
			clip.start();
		}catch (Exception e) {
			clip = null;
		}
	}

	//------------------------------------------------------------//
	// Swapping shit
	//------------------------------------------------------------//
	@Subscribe
	private void onGameTick(GameTick event) {
		lastTickUpdate = Instant.now();

		if (skipTickCheck) {
			skipTickCheck = false;
		} else {
			if (client.getLocalPlayer() == null || client.getLocalPlayer().getPlayerComposition() == null) {
				return;
			}
			int equippedWeapon = ObjectUtils.defaultIfNull(client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON), -1);
			weaponStyle = WeaponMap.StyleMap.get(equippedWeapon);
		}

		if (config.karambwans() && client.getGameState() == GameState.LOGGED_IN) {
			if (!depositTab) {
				client.runScript(915, 4);
				depositTab = true;
			}
		}

		updateitemCounts();

		if (config.afkConstruction()) {
			Widget createMenu = client.getWidget(458, 1);
			Widget demonSendBank = client.getWidget(219, 1);
			Widget demonPayment = client.getWidget(231, 5);
			int delay = delayUtils.nextInt(0, 578);

			if (demonSendBank != null && !demonSendBank.isHidden() && !demonSendBank.isSelfHidden()) {
				for (Widget child : demonSendBank.getDynamicChildren()) {
					if (child.getText().contains("Really remove it?") || child.getText().contains("Repeat last task?")) {
						delayUtils.delayKey(KeyEvent.VK_1, delay);
					} else if (child.getText().contains("Okay, here's 10,000 coins.")) {
						delayUtils.delayKey(KeyEvent.VK_1, delay);
					}
				}
			}

			if (demonPayment != null && !demonPayment.isHidden() && !demonPayment.isSelfHidden()) {
				if (demonPayment.getText().contains("Master, if thou desire")) {
					delayUtils.delayKey(KeyEvent.VK_SPACE, delay);
				}
			}

			if (createMenu != null && !createMenu.isHidden() && !createMenu.isSelfHidden()) {
				delayUtils.delayKey(KeyEvent.VK_6, delay);
			}
		}
	}

	@Subscribe
	private void onWidgetLoaded (WidgetLoaded event) {
		if (event.getGroupId() == WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER.getGroupId()) {
			depositTab = true;
		}
	}

	@Subscribe
	private void onWidgetClosed (WidgetClosed event) {
		if(event.getGroupId() == WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER.getGroupId()) {
			depositTab = false;
		}
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick) {
		if (client.getGameState() == GameState.LOGGED_IN && !client.isMenuOpen()) {
			MenuEntry[] menuEntries = client.getMenuEntries();
			int idx = 0;
			optionIndexes.clear();
			for (MenuEntry entry : menuEntries) {
				String option = Text.removeTags(entry.getOption()).toLowerCase();
				optionIndexes.put(option, idx++);
			}

			idx = 0;
			for (MenuEntry entry : menuEntries) {
				swapMenuEntry(idx++, entry);
			}
		}
		client.setMenuEntries(updateMenuEntries(client.getMenuEntries()));
	}

	private int findIndex(MenuEntry[] entries, int limit, String option, String target, boolean strict) {
		if (strict) {
			List<Integer> indexes = optionIndexes.get(option);

			// We want the last index which matches the target, as that is what is top-most
			// on the menu
			for (int i = indexes.size() - 1; i >= 0; --i) {
				int idx = indexes.get(i);
				MenuEntry entry = entries[idx];
				String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

				// Limit to the last index which is prior to the current entry
				if (idx < limit && entryTarget.equals(target)) {
					return idx;
				}
			}
		} else {
			// Without strict matching we have to iterate all entries up to the current limit...
			for (int i = limit - 1; i >= 0; i--)
			{
				MenuEntry entry = entries[i];
				String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
				String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

				if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target))
				{
					return i;
				}
			}

		}
		return -1;
	}

	private int searchIndex(MenuEntry[] entries, String option, String target, boolean strict) {
		for (int i = entries.length - 1; i >= 0; i--) {
			MenuEntry entry = entries[i];
			String entryOption = net.runelite.client.util.Text.removeTags(entry.getOption()).toLowerCase();
			String entryTarget = net.runelite.client.util.Text.removeTags(entry.getTarget()).toLowerCase();
			if (strict) {
				if (entryOption.equals(option) && entryTarget.equals(target))
					return i;
			} else if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target)) {
				return i;
			}
		}
		return -1;
	}

	private MenuEntry[] updateMenuEntries(MenuEntry[] menuEntries)
	{
		return Arrays.stream(menuEntries)
				.filter(filterMenuEntries).sorted((o1, o2) -> 0)
				.toArray(MenuEntry[]::new);
	}

	private void swap(String optionA, String optionB, String target, int index, boolean strict) {
		MenuEntry[] menuEntries = client.getMenuEntries();
		int thisIndex = findIndex(menuEntries, index, optionB, target, strict);
		int optionIdx = findIndex(menuEntries, thisIndex, optionA, target, strict);
		if (thisIndex >= 0 && optionIdx >= 0)
			swapSpoon(optionIndexes, menuEntries, optionIdx, thisIndex);
	}

	private void swap(String optionA, String optionB, String target, int index) {
		swap(optionA, optionB, target, index, true);
	}

	private void swapSpoon(ArrayListMultimap<String, Integer> optionIndexes, MenuEntry[] entries, int index1, int index2) {
		MenuEntry entry = entries[index1];
		entries[index1] = entries[index2];
		entries[index2] = entry;
		client.setMenuEntries(entries);
		optionIndexes.clear();
		int idx = 0;
		for (MenuEntry menuEntry : entries) {
			String option = net.runelite.client.util.Text.removeTags(menuEntry.getOption()).toLowerCase();
			optionIndexes.put(option, idx++);
		}
	}

	public void swapStuff(String optionA, String optionB, String target, boolean strict) {
		MenuEntry[] entries = client.getMenuEntries();
		int idxA = searchIndex(entries, optionA, target, strict);
		int idxB = searchIndex(entries, optionB, target, strict);
		if (idxA >= 0 && idxB >= 0) {
			MenuEntry entry = entries[idxA];
			entries[idxA] = entries[idxB];
			entries[idxB] = entry;
			client.setMenuEntries(entries);
		}
	}

	private boolean shitSwap(String option, String target, int index, boolean strict)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();

		// find option to swap with
		int optionIdx = findIndex(menuEntries, index, option, target, strict);

		if (optionIdx >= 0)
		{
			shitSwap(optionIndexes, menuEntries, optionIdx, index);
			return true;
		}

		return false;
	}


	private void shitSwap(ArrayListMultimap<String, Integer> optionIndexes, MenuEntry[] entries, int index1, int index2)
	{
		Widget eq = client.getWidget(WidgetInfo.EQUIPMENT);
		if (client.getVar(VarClientInt.INVENTORY_TAB) == 4 && eq != null)
		{
			MenuEntry[] clonedEntries = new MenuEntry[entries.length];
			System.arraycopy(entries, 0, clonedEntries, 0, entries.length);

			MenuEntry entry1 = entries[index1];
			MenuEntry entry2 = entries[index2];

			int temp = entry1.getType().getId();
			entry1.setType(entry2.getType());
			entry2.setType(MenuAction.of(temp));

			clonedEntries[index1] = entry2;
			clonedEntries[index2] = entry1;

			client.setMenuEntries(clonedEntries);

			String option1 = Text.removeTags(entry1.getOption()).toLowerCase(),
					option2 = Text.removeTags(entry2.getOption()).toLowerCase();

			List<Integer> list1 = optionIndexes.get(option1),
					list2 = optionIndexes.get(option2);

			list1.remove((Integer) index1);
			list2.remove((Integer) index2);

			sortedInsert(list1, index2);
			sortedInsert(list2, index1);
		}
		else
		{
			MenuEntry entry1 = entries[index1],
					entry2 = entries[index2];

			entries[index1] = entry2;
			entries[index2] = entry1;

			client.setMenuEntries(entries);

			// Update optionIndexes
			String option1 = Text.removeTags(entry1.getOption()).toLowerCase(),
					option2 = Text.removeTags(entry2.getOption()).toLowerCase();

			List<Integer> list1 = optionIndexes.get(option1),
					list2 = optionIndexes.get(option2);

			// call remove(Object) instead of remove(int)
			list1.remove((Integer) index1);
			list2.remove((Integer) index2);

			sortedInsert(list1, index2);
			sortedInsert(list2, index1);
		}
	}


	private boolean shiftModifier() {
		return client.isKeyPressed(KeyCode.KC_SHIFT);
	}

	/*private void parseOldFormatConfig()
	{
		if (config.customSwapsString().trim().isEmpty())
		{
			return;
		}

		// LOAD OLD FORMAT CUSTOM SWAPS
		StringBuilder newFormatString = new StringBuilder();
		for (String oldFormat : config.customSwapsString().trim().split("\n"))
		{
			oldFormat = oldFormat.split(":")[0];
			newFormatString.append(oldFormat).append("\n");
		}

		if (newFormatString.length() > 0)
		{
			configManager.setConfiguration("menuentryswapperextended", "customSwaps", newFormatString);
		}
	}*/

	private void parseZCustomSwapperConfig() {
		if (config.customSwapsString().trim().isEmpty() && configManager.getConfiguration("zmenuentryswapper", "customSwapsStr") != null)
		{
			configManager.setConfiguration("spoonezswaps", "customSwapsStr", configManager.getConfiguration("zmenuentryswapper", "customSwapsStr"));
		}
		if (config.bankCustomSwapsString().trim().isEmpty() && configManager.getConfiguration("zmenuentryswapper", "bankCustomSwapsStr") != null)
		{
			configManager.setConfiguration("spoonezswaps", "bankCustomSwapsStr", configManager.getConfiguration("zmenuentryswapper", "bankCustomSwapsStr"));
		}

		if (config.shiftCustomSwapsString().trim().isEmpty() && configManager.getConfiguration("zmenuentryswapper", "shiftCustomSwapsStr") != null)
		{
			configManager.setConfiguration("spoonezswaps", "shiftCustomSwapsStr", configManager.getConfiguration("zmenuentryswapper", "shiftCustomSwapsStr"));
		}
		if (config.bankShiftCustomSwapsString().trim().isEmpty() && configManager.getConfiguration("zmenuentryswapper", "bankShiftCustomSwapsStr") != null)
		{
			configManager.setConfiguration("spoonezswaps", "bankShiftCustomSwapsStr", configManager.getConfiguration("zmenuentryswapper", "bankShiftCustomSwapsStr"));
		}

		if (config.keyCustomSwapsString().trim().isEmpty() && configManager.getConfiguration("zmenuentryswapper", "keyCustomSwapsStr") != null)
		{
			configManager.setConfiguration("spoonezswaps", "keyCustomSwapsStr", configManager.getConfiguration("zmenuentryswapper", "keyCustomSwapsStr"));
		}
		if (config.bankKeyCustomSwapsString().trim().isEmpty() && configManager.getConfiguration("zmenuentryswapper", "bankKeyCustomSwapsStr") != null)
		{
			configManager.setConfiguration("spoonezswaps", "bankKeyCustomSwapsStr", configManager.getConfiguration("zmenuentryswapper", "bankKeyCustomSwapsStr"));
		}

		if (config.removeOptionsString().trim().isEmpty() && configManager.getConfiguration("zmenuentryswapper", "removeOptionsStr") != null)
		{
			configManager.setConfiguration("spoonezswaps", "removeOptionsStr", configManager.getConfiguration("zmenuentryswapper", "removeOptionsStr"));
		}
	}
}
