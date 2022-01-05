package net.runelite.client.plugins.spoonrunecraft;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
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
import java.util.stream.Stream;

@Extension
@PluginDescriptor(
		name = "<html><font color=#25c550>[S] Runecraft",
		description = "Show minimap icons and clickboxes for abyssal rifts",
		tags = {"abyssal", "minimap", "overlay", "rifts", "rc", "runecrafting"},
		conflicts = "Runecraft"
)
public class sRunecraftPlugin extends Plugin {
	private LocalPoint MIND_ALTAR_LOC = null;

	private LocalPoint ASTRAL_ALTAR_LOC = null;

	private Set<LocalPoint> altarLoc = new HashSet<>();

	private Swapper swapper = new Swapper();

	private final Set<DecorativeObject> abyssObjects = new HashSet<>();

	private static final int TAB_SWITCH_SCRIPT = 915;
	public boolean bankTab = false;

	@Provides
	sRunecraftConfig getConfig(ConfigManager configManager) {
		return (sRunecraftConfig)configManager.getConfig(sRunecraftConfig.class);
	}

	private void updateConfig() {
		this.offerAll = this.config.getLeftClickOfferAll();
		this.lavas = this.config.lavas();
		this.essPouch = this.config.essPouch();
		this.leftClickTrade = this.config.getLeftClickTrade();
	}

	private void reset() {
		bankTab = false;
	}

	protected void startUp() throws Exception {
		reset();
		updateConfig();
		this.keyManager.registerKeyListener(this.inputListener);
		this.overlayManager.add(this.abyssOverlay);
		this.abyssOverlay.updateConfig();
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
		reset();
		updateConfig();
		this.keyManager.unregisterKeyListener(this.inputListener);
		this.overlayManager.remove(this.abyssOverlay);
		this.abyssObjects.clear();
		this.darkMage = null;
		this.degradedPouchInInventory = false;
		overlayManager.remove(denseRunestoneOverlay);
		denseRunestoneNorth = null;
		denseRunestoneSouth = null;
		CLICKBOX_FILL_COLOR_MINABLE = new Color(
				CLICKBOX_BORDER_COLOR_MINABLE.getRed(), CLICKBOX_BORDER_COLOR_MINABLE.getGreen(),
				CLICKBOX_BORDER_COLOR_MINABLE.getBlue(), 50);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		updateConfig();
		this.abyssOverlay.updateConfig();
	}

	@Subscribe
	private void onGameTick(GameTick event) {
		if (this.config.defaultLavas() && this.config.lavas() && !bankTab) {
			System.out.println("bank closed");
			this.client.runScript(915, 4);
			this.bankTab = true;
		}
	}

	@Subscribe
	private void onWidgetLoaded (WidgetLoaded event) {
		if(event.getGroupId() == WidgetInfo.BANK_CONTAINER.getGroupId()) {
			this.bankTab = true;
		}
	}

	@Subscribe
	private void onWidgetClosed (WidgetClosed event) {
		if(event.getGroupId() == WidgetInfo.BANK_CONTAINER.getGroupId()) {
			this.bankTab = false;
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
			return;
		if (this.config.degradingNotification() && event.getMessage().contains("Your pouch has decayed through use."))
			this.notifier.notify("Your rune pouch has decayed.");
	}

	@Subscribe
	public void onDecorativeObjectSpawned(DecorativeObjectSpawned event) {
		DecorativeObject decorativeObject = event.getDecorativeObject();
		if (AbyssRifts.getRift(decorativeObject.getId()) != null)
			this.abyssObjects.add(decorativeObject);
	}

	@Subscribe
	public void onDecorativeObjectDespawned(DecorativeObjectDespawned event) {
		DecorativeObject decorativeObject = event.getDecorativeObject();
		this.abyssObjects.remove(decorativeObject);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		GameState gameState = event.getGameState();
		switch (gameState) {
			case LOADING:
				this.abyssObjects.clear();
				denseRunestoneNorth = null;
				denseRunestoneSouth = null;
				localPointRunestoneNorth = null;
				localPointRunestoneSouth = null;
				break;
			case CONNECTION_LOST:
			case HOPPING:
			case LOGIN_SCREEN:
				this.darkMage = null;
				break;
		}
	}

	@Subscribe
	public void onFocusChanged(FocusChanged event) {
		if (!event.isFocused())
			this.hotKeyPressed = false;
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event) {
		int ID = event.getGameObject().getId();
		if (ID == 34771) {
			this.ASTRAL_ALTAR_LOC = event.getTile().getLocalLocation();
			this.altarLoc.add(event.getTile().getLocalLocation());
		} else if (ID == 34761) {
			this.ASTRAL_ALTAR_LOC = null;
			this.MIND_ALTAR_LOC = event.getTile().getLocalLocation();
			this.altarLoc.add(event.getTile().getLocalLocation());
		} else if (ID == 34764) {
			this.ASTRAL_ALTAR_LOC = null;
			this.altarLoc.add(event.getTile().getLocalLocation());
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
		this.entries = this.client.getMenuEntries();
		String target = Text.removeTags(event.getTarget()).toLowerCase();
		String option = Text.removeTags(event.getOption()).toLowerCase();
		if (this.config.essPouch()) {
			ArrayList<String> pouchNames = new ArrayList<>();
			pouchNames.add("small pouch");
			pouchNames.add("medium pouch");
			pouchNames.add("large pouch");
			pouchNames.add("giant pouch");
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
		LocalPoint playerLoc = this.client.getLocalPlayer().getLocalLocation();
		for (LocalPoint lp : this.altarLoc) {
			if (playerLoc.distanceTo(lp) < 750)
				nearAltar = true;
		}
		if (this.leftClickTrade && nearAltar && !this.hotKeyPressed) {
			FriendsChatMember[] clanMembersArr = this.client.getFriendsChatManager().getMembers();
			ArrayList<String> clanMembersList = new ArrayList<>();
			List<String> playerNameList = new ArrayList<>();
			try {
				for (Player p : this.client.getPlayers())
					playerNameList.add(p.getName().toLowerCase());
			} catch (Exception exception) {}
			if (clanMembersArr != null)
				for (FriendsChatMember c : clanMembersArr)
					clanMembersList.add(c.getName().toLowerCase());
			if (clanMembersList.contains(splitTarget) || splitTarget.contains("senurerutan")) {
				if (option.equals("follow") && clanMembersList.contains(splitTarget)) {
					for (int i = this.entries.length - 1; i >= 0; i--) {
						if (this.entries[i].getOption().equals("Walk here")) {
							this.entries = (MenuEntry[])ArrayUtils.remove((Object[])this.entries, i);
							i--;
						}
					}
					this.client.setMenuEntries(this.entries);
				}
				swap("follow", "trade with", target, false);
			} else {
				Widget w = this.client.getWidget(WidgetInfo.LOGIN_CLICK_TO_PLAY_SCREEN);
				if (w == null)
					for (int j = this.entries.length - 1; j >= 0; j--) {
						if ((target.contains("tree") || target.contains("mysterious glow") || target.contains("pillar") || (!playerNameList.contains(splitTarget.toLowerCase()) && !target.contains("dueling") && !target.contains("binding") && !target.contains("magic") && !target.contains("contact") && !target.contains("crafting") && !target.contains("glory") && !target.contains("max") && !target.contains("pouch"))) && target != "" && !target.contains("altar") && !option.contains("offer") && !option.contains("remove") && !option.contains("use") && !option.contains("wield") && !option.contains("destroy")) {
							this.entries = (MenuEntry[])ArrayUtils.remove((Object[])this.entries, j);
							j--;
						}
						if (playerNameList.contains(splitTarget.toLowerCase())) {
							this.entries = (MenuEntry[])ArrayUtils.remove((Object[])this.entries, j);
							j--;
						}
						this.client.setMenuEntries(this.entries);
					}
			}
			if (this.offerAll && option.equals("offer")) {
				swap(option, "offer-all", target, false);
			} else if (target.contains("altar") && (option.contains("craft") || option.contains("pray"))) {
				if (this.ASTRAL_ALTAR_LOC == null)
					hide(option, target);
			} else if (target.contains("pure") && option.contains("use")) {
				hide("drop", target);
			}
		}
		if (target.contains("ring of dueling") && option.contains("remove") && this.config.lavas())
			if (this.client.getLocalPlayer().getWorldLocation().getRegionID() != 10315) {
				swap("duel arena", option, target, false);
			} else if (this.client.getLocalPlayer().getWorldLocation().getRegionID() == 10315) {
				swap("castle wars", option, target, false);
			}
	}

	public void swap(String optionA, String optionB, String target, boolean strict) {
		MenuEntry[] entries = this.client.getMenuEntries();
		int idxA = searchIndex(entries, optionA, target, strict);
		int idxB = searchIndex(entries, optionB, target, strict);
		if (idxA >= 0 && idxB >= 0) {
			MenuEntry entry = entries[idxA];
			entries[idxA] = entries[idxB];
			entries[idxB] = entry;
			this.client.setMenuEntries(entries);
		}
	}

	private void hide(String option, String target) {
		MenuEntry[] entries = this.client.getMenuEntries();
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
		this.client.setMenuEntries(newEntries);
	}

	private void removeAllButMultiple(ArrayList<String> leaveOption, String leaveTarget) {
		MenuEntry[] entries = this.client.getMenuEntries();
		ArrayList<MenuEntry> newEntries = new ArrayList<>();
		int index = -1;
		for (String option : leaveOption) {
			index = searchIndex(entries, option, leaveTarget, false);
			if (index != -1) {
				entries[index].setForceLeftClick(true);
				newEntries.add(entries[index]);
			}
		}
		this.client.setMenuEntries(newEntries.<MenuEntry>toArray(new MenuEntry[newEntries.size()]));
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
	public void onItemContainerChanged(ItemContainerChanged event) {
		if (event.getItemContainer() == this.client.getItemContainer(InventoryID.INVENTORY)) {
			Item[] items = event.getItemContainer().getItems();
			this.degradedPouchInInventory = Stream.<Item>of(items).anyMatch(i -> DEGRADED_POUCHES.contains(Integer.valueOf(i.getId())));
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event) {
		NPC npc = event.getNpc();
		if (npc.getId() == 2583)
			this.darkMage = npc;
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event) {
		NPC npc = event.getNpc();
		if (npc == this.darkMage)
			this.darkMage = null;
	}

	public void setHotKeyPressed(boolean hotKeyPressed) {
		this.hotKeyPressed = hotKeyPressed;
	}

	Set<DecorativeObject> getAbyssObjects() {
		return this.abyssObjects;
	}

	boolean isDegradedPouchInInventory() {
		return this.degradedPouchInInventory;
	}

	NPC getDarkMage() {
		return this.darkMage;
	}

	private static final List<Integer> DEGRADED_POUCHES = (List<Integer>)ImmutableList.of(Integer.valueOf(5511), Integer.valueOf(5513), Integer.valueOf(5515));

	private boolean hotKeyPressed;

	private MenuEntry[] entries;

	private static final int FIRE_ALTAR = 10315;

	private static final String POUCH_DECAYED_NOTIFICATION_MESSAGE = "Your rune pouch has decayed.";

	private static final String POUCH_DECAYED_MESSAGE = "Your pouch has decayed through use.";

	private boolean wearingTiara;

	private boolean wearingCape;

	private boolean lavas;

	private boolean leftClickTrade;

	private boolean offerAll;

	private boolean essPouch;

	private boolean degradedPouchInInventory;

	private NPC darkMage;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private AbyssOverlay abyssOverlay;

	@Inject
	private ShiftWalkerInputListener inputListener;

	@Inject
	private KeyManager keyManager;

	@Inject
	private sRunecraftConfig config;

	@Inject
	private DenseRunestoneOverlay denseRunestoneOverlay;

	@Inject
	private Notifier notifier;

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
}
