package net.runelite.client.plugins.spoonrunecraft;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.spoonrunecraft.utils.Swapper;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

@Extension
@PluginDescriptor(
		name = "<html><font color=#25c550>[S] Runecraft",
		description = "Show minimap icons and clickboxes for abyssal rifts",
		tags = {"abyssal", "minimap", "overlay", "rifts", "rc", "runecrafting"},
		conflicts = "Runecraft"
)
public class sRunecraftPlugin extends Plugin {
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private AbyssOverlay abyssOverlay;

	@Inject
	private AbyssMinimapOverlay abyssMinimapOverlay;

	@Inject
	private sRunecraftConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private NpcOverlayService npcOverlayService;

	@Inject
	private Client client;

	@Inject
	private ShiftWalkerInputListener inputListener;

	@Inject
	private KeyManager keyManager;

	@Inject
	private DenseRunestoneOverlay denseRunestoneOverlay;

	private static final String POUCH_DECAYED_NOTIFICATION_MESSAGE = "Your rune pouch has decayed.";
	private static final String POUCH_DECAYED_MESSAGE = "Your pouch has decayed through use.";
	private static final List<Integer> DEGRADED_POUCHES = ImmutableList.of(
			ItemID.MEDIUM_POUCH_5511,
			ItemID.LARGE_POUCH_5513,
			ItemID.GIANT_POUCH_5515,
			ItemID.COLOSSAL_POUCH_26786
	);

	@Getter(AccessLevel.PACKAGE)
	private final Set<DecorativeObject> abyssObjects = new HashSet<>();

	private boolean degradedPouchInInventory;

	@Setter
	private boolean hotKeyPressed;

	private static final int FIRE_ALTAR = 10315;
	private boolean wearingTiara;
	private boolean wearingCape;

	private boolean leftClickTrade;
	private boolean offerAll;

	private static final int DENSE_RUNESTONE_SOUTH_ID = NullObjectID.NULL_10796;
	private static final int DENSE_RUNESTONE_NORTH_ID = NullObjectID.NULL_8981;

	@Getter(AccessLevel.PACKAGE)
	private GameObject denseRunestoneSouth;

	@Getter(AccessLevel.PACKAGE)
	private GameObject denseRunestoneNorth;

	@Getter(AccessLevel.PACKAGE)
	private LocalPoint localPointRunestoneSouth;

	@Getter(AccessLevel.PACKAGE)
	private LocalPoint localPointRunestoneNorth;

	@Getter(AccessLevel.PACKAGE)
	private Color CLICKBOX_BORDER_COLOR_MINABLE;

	@Getter(AccessLevel.PACKAGE)
	private Color CLICKBOX_FILL_COLOR_MINABLE;

	@Getter(AccessLevel.PACKAGE)
	private Color CLICKBOX_BORDER_HOVER_COLOR_MINABLE;

	@Getter(AccessLevel.PACKAGE)
	private Color CLICKBOX_BORDER_COLOR_DEPLETED;

	@Getter(AccessLevel.PACKAGE)
	private Color CLICKBOX_FILL_COLOR_DEPLETED;

	@Getter(AccessLevel.PACKAGE)
	private Color CLICKBOX_BORDER_HOVER_COLOR_DEPLETED;

	private final Function<NPC, HighlightedNpc> highlightDarkMage = this::highlightDarkMage;

	private LocalPoint ASTRAL_ALTAR_LOC = null;

	private final Set<LocalPoint> altarLoc = new HashSet<>();

	private final Swapper swapper = new Swapper();

	private static final int TAB_SWITCH_SCRIPT = 915;
	public boolean bankTab = false;

	@Provides
	sRunecraftConfig getConfig(ConfigManager configManager) {
		return (sRunecraftConfig)configManager.getConfig(sRunecraftConfig.class);
	}

	protected void startUp() throws Exception {
		bankTab = false;
		keyManager.registerKeyListener(inputListener);
		npcOverlayService.registerHighlighter(highlightDarkMage);
		overlayManager.add(abyssOverlay);
		overlayManager.add(abyssMinimapOverlay);

		overlayManager.add(denseRunestoneOverlay);
		CLICKBOX_BORDER_COLOR_MINABLE = config.showDenseRunestoneClickboxAvailable();
		CLICKBOX_FILL_COLOR_MINABLE = new Color(CLICKBOX_BORDER_COLOR_MINABLE.getRed(), CLICKBOX_BORDER_COLOR_MINABLE.getGreen(),
				CLICKBOX_BORDER_COLOR_MINABLE.getBlue(), 50);
		CLICKBOX_BORDER_HOVER_COLOR_MINABLE = CLICKBOX_BORDER_COLOR_MINABLE.darker();

		CLICKBOX_BORDER_COLOR_DEPLETED = config.showDenseRunestoneClickboxUnavailable();
		CLICKBOX_FILL_COLOR_DEPLETED = new Color(
				CLICKBOX_BORDER_COLOR_DEPLETED.getRed(),
				CLICKBOX_BORDER_COLOR_DEPLETED.getGreen(),
				CLICKBOX_BORDER_COLOR_DEPLETED.getBlue(), 50);
		CLICKBOX_BORDER_HOVER_COLOR_DEPLETED = CLICKBOX_BORDER_COLOR_DEPLETED.darker();
	}

	protected void shutDown() throws Exception {
		bankTab = false;
		keyManager.unregisterKeyListener(inputListener);
		npcOverlayService.unregisterHighlighter(highlightDarkMage);
		overlayManager.remove(abyssOverlay);
		overlayManager.remove(abyssMinimapOverlay);
		abyssObjects.clear();
		degradedPouchInInventory = false;

		overlayManager.remove(denseRunestoneOverlay);
		denseRunestoneNorth = null;
		denseRunestoneSouth = null;
		CLICKBOX_FILL_COLOR_MINABLE = new Color(
				CLICKBOX_BORDER_COLOR_MINABLE.getRed(), CLICKBOX_BORDER_COLOR_MINABLE.getGreen(),
				CLICKBOX_BORDER_COLOR_MINABLE.getBlue(), 50);
	}

	@Subscribe
	private void onGameTick(GameTick event) {
		if (config.defaultLavas() && config.lavas() && !bankTab) {
			client.runScript(TAB_SWITCH_SCRIPT, 4);
			bankTab = true;
		}
	}

	@Subscribe
	private void onWidgetLoaded (WidgetLoaded event) {
		if(event.getGroupId() == WidgetInfo.BANK_CONTAINER.getGroupId()) {
			bankTab = true;
		}
	}

	@Subscribe
	private void onWidgetClosed (WidgetClosed event) {
		if(event.getGroupId() == WidgetInfo.BANK_CONTAINER.getGroupId()) {
			bankTab = false;
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		if (config.degradingNotification())
		{
			if (event.getMessage().contains(POUCH_DECAYED_MESSAGE))
			{
				notifier.notify(POUCH_DECAYED_NOTIFICATION_MESSAGE);
			}
		}
	}

	@Subscribe
	public void onDecorativeObjectSpawned(DecorativeObjectSpawned event)
	{
		DecorativeObject decorativeObject = event.getDecorativeObject();
		if (AbyssRifts.getRift(decorativeObject.getId()) != null)
		{
			abyssObjects.add(decorativeObject);
		}
	}

	@Subscribe
	public void onDecorativeObjectDespawned(DecorativeObjectDespawned event)
	{
		DecorativeObject decorativeObject = event.getDecorativeObject();
		abyssObjects.remove(decorativeObject);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState gameState = event.getGameState();
		if (gameState == GameState.LOADING) {
			abyssObjects.clear();
			denseRunestoneNorth = null;
			denseRunestoneSouth = null;
			localPointRunestoneNorth = null;
			localPointRunestoneSouth = null;
		}
	}

	@Subscribe
	public void onFocusChanged(FocusChanged event) {
		if (!event.isFocused())
			hotKeyPressed = false;
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event) {
		int ID = event.getGameObject().getId();
		if (ID == 34771) {
			ASTRAL_ALTAR_LOC = event.getTile().getLocalLocation();
			altarLoc.add(event.getTile().getLocalLocation());
		} else if (ID == 34761) {
			ASTRAL_ALTAR_LOC = null;
			altarLoc.add(event.getTile().getLocalLocation());
		} else if (ID == 34764) {
			ASTRAL_ALTAR_LOC = null;
			altarLoc.add(event.getTile().getLocalLocation());
		}
		GameObject obj = event.getGameObject();
		int id = obj.getId();

		switch (id)
		{
			case DENSE_RUNESTONE_SOUTH_ID:
				denseRunestoneSouth = obj;
				localPointRunestoneSouth = event.getGameObject().getLocalLocation();
				break;
			case DENSE_RUNESTONE_NORTH_ID:
				denseRunestoneNorth = obj;
				localPointRunestoneNorth = event.getGameObject().getLocalLocation();
				break;
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		switch (event.getGameObject().getId())
		{
			case DENSE_RUNESTONE_SOUTH_ID:
				denseRunestoneSouth = null;
				break;
			case DENSE_RUNESTONE_NORTH_ID:
				denseRunestoneNorth = null;
				break;
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		MenuEntry[] entries = client.getMenuEntries();
		String target = Text.removeTags(event.getTarget()).toLowerCase();
		String option = Text.removeTags(event.getOption()).toLowerCase();
		if (config.essPouch()) {
			ArrayList<String> pouchNames = new ArrayList<>();
			pouchNames.add("small pouch");
			pouchNames.add("medium pouch");
			pouchNames.add("large pouch");
			pouchNames.add("giant pouch");
			pouchNames.add("colossal pouch");
			if (pouchNames.contains(target)) {
				ArrayList<String> options = new ArrayList<>();
				options.add("fill");
				options.add("empty");
				removeAllButMultiple(options, target);
			}
		}
		String splitTarget = target.split(" ")[0];
		if (target.toLowerCase().contains("senurerutan"))
			splitTarget = " senurerutan";
		boolean nearAltar = false;
		LocalPoint playerLoc = client.getLocalPlayer().getLocalLocation();
		for (LocalPoint lp : altarLoc) {
			if (playerLoc.distanceTo(lp) < 750)
				nearAltar = true;
		}
		if (config.getLeftClickTrade() && nearAltar && !hotKeyPressed) {
			FriendsChatMember[] clanMembersArr = client.getFriendsChatManager().getMembers();
			ArrayList<String> clanMembersList = new ArrayList<>();
			List<String> playerNameList = new ArrayList<>();
			try {
				for (Player p : client.getPlayers())
					playerNameList.add(p.getName().toLowerCase());
			} catch (Exception ignored) {}
			if (clanMembersArr != null)
				for (FriendsChatMember c : clanMembersArr)
					clanMembersList.add(c.getName().toLowerCase());
			if (clanMembersList.contains(splitTarget) || splitTarget.contains("senurerutan")) {
				if (option.equals("follow") && clanMembersList.contains(splitTarget)) {
					for (int i = entries.length - 1; i >= 0; i--) {
						if (entries[i].getOption().equals("Walk here")) {
							entries = (MenuEntry[])ArrayUtils.remove((Object[]) entries, i);
							i--;
						}
					}
					client.setMenuEntries(entries);
				}
				swap("follow", "trade with", target, false);
			} else {
				Widget w = client.getWidget(WidgetInfo.LOGIN_CLICK_TO_PLAY_SCREEN);
				if (w == null)
					for (int j = entries.length - 1; j >= 0; j--) {
						if ((target.contains("tree") || target.contains("mysterious glow") || target.contains("pillar") || (!playerNameList.contains(splitTarget.toLowerCase()) && !target.contains("dueling") && !target.contains("binding") && !target.contains("magic") && !target.contains("contact") && !target.contains("crafting") && !target.contains("glory") && !target.contains("max") && !target.contains("pouch"))) && target != "" && !target.contains("altar") && !option.contains("offer") && !option.contains("remove") && !option.contains("use") && !option.contains("wield") && !option.contains("destroy")) {
							entries = (MenuEntry[])ArrayUtils.remove((Object[]) entries, j);
							j--;
						}
						if (playerNameList.contains(splitTarget.toLowerCase())) {
							entries = (MenuEntry[])ArrayUtils.remove((Object[]) entries, j);
							j--;
						}
						client.setMenuEntries(entries);
					}
			}
			if (config.getLeftClickOfferAll() && option.equals("offer")) {
				swap(option, "offer-all", target, false);
			} else if (target.contains("altar") && (option.contains("craft") || option.contains("pray"))) {
				if (ASTRAL_ALTAR_LOC == null)
					hide(option, target);
			} else if (target.contains("pure") && option.contains("use")) {
				hide("drop", target);
			}
		}
		if (target.contains("ring of dueling") && option.contains("remove") && config.lavas())
			if (client.getLocalPlayer().getWorldLocation().getRegionID() != FIRE_ALTAR) {
				swap("duel arena", option, target, false);
			} else if (client.getLocalPlayer().getWorldLocation().getRegionID() == FIRE_ALTAR) {
				swap("castle wars", option, target, false);
			}
	}

	public void swap(String optionA, String optionB, String target, boolean strict) {
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

	private void hide(String option, String target) {
		MenuEntry[] entries = client.getMenuEntries();
		int index = searchIndex(entries, option, target, false);
		if (index < 0)
			return;
		MenuEntry[] newEntries = new MenuEntry[entries.length - 1];
		int i2 = 0;
		for (int j = 0; j < entries.length - 1; j++) {
			if (j != index) {
				newEntries[i2] = entries[j];
				i2++;
			}
		}
		client.setMenuEntries(newEntries);
	}

	private void removeAllButMultiple(ArrayList<String> leaveOption, String leaveTarget) {
		MenuEntry[] entries = client.getMenuEntries();
		ArrayList<MenuEntry> newEntries = new ArrayList<>();
		int index = -1;
		for (String option : leaveOption) {
			index = searchIndex(entries, option, leaveTarget, false);
			if (index != -1) {
				entries[index].setForceLeftClick(true);
				newEntries.add(entries[index]);
			}
		}
		client.setMenuEntries(newEntries.<MenuEntry>toArray(new MenuEntry[0]));
	}

	private int searchIndex(MenuEntry[] entries, String option, String target, boolean strict) {
		for (int i = entries.length - 1; i >= 0; i--) {
			MenuEntry entry = entries[i];
			String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
			String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
			if (strict) {
				if (entryOption.equals(option) && entryTarget.equals(target))
					return i;
			} else if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target)) {
				return i;
			}
		}
		return -1;
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() != InventoryID.INVENTORY.getId())
		{
			return;
		}

		final Item[] items = event.getItemContainer().getItems();
		degradedPouchInInventory = Stream.of(items).anyMatch(i -> DEGRADED_POUCHES.contains(i.getId()));
	}

	private HighlightedNpc highlightDarkMage(NPC npc)
	{
		if (npc.getId() == NpcID.DARK_MAGE)
		{
			return HighlightedNpc.builder()
					.npc(npc)
					.tile(true)
					.highlightColor(Color.GREEN)
					.render(n -> config.hightlightDarkMage() && degradedPouchInInventory)
					.build();
		}
		return null;
	}
}
