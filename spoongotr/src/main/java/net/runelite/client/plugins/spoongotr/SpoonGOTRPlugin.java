package net.runelite.client.plugins.spoongotr;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.*;

@Extension
@PluginDescriptor(
		name = "<html><font color=#25c550>[S] GOTR",
		description = "All in one Guardians of the Rift plugin",
		tags = {"Spoon", "guardians", "rift", "rc", "gotr"},
		enabledByDefault = false
)

@Singleton
public class SpoonGOTRPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private SpoonGOTRConfig config;

	@Inject
	private SpoonGOTROverlay overlay;

	@Inject
	private SpoonGOTRTimerPanel timerPanel;

	@Inject
	private SpoonGOTRPointsPanel pointsPanel;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ClientThread clientThread;

	@Inject
    private InfoBoxManager infoBoxManager;

	public ArrayList<GameObject> guardians = new ArrayList<>();
	public GameObject hugePortal = null;
	public int portalTicks = 0;
	public NPC bigGuy = null;
	public boolean hasCell = false;
	public boolean hasGuardianStone = false;
	public boolean hasUnchargedCells = false;
	public GameObject unchargedCellTable = null;
	public boolean showPoints = false;
	public GameObject eleGuardian = null;
	public GameObject cataGuardian = null;
	public String elePoints = "";
	public String cataPoints = "";
	public String guardianWidgetText = "";

	public boolean gameStart = false;
	public int ticksSincePortal = 0;
	public int portalsSpawned = 0;
	public int timeTillNextGame = -1;

	private SpoonGOTRTimerBox timerBox;
	public int startTick = -1;

	public Map<Integer, Integer> runeIdMap = Map.ofEntries(
			Map.entry(43701, 556),//Air
			Map.entry(43702, 555),//Water
			Map.entry(43703, 557),//Earth
			Map.entry(43704, 554),//Fire
			Map.entry(43705, 558),//Mind
			Map.entry(43709, 559),//Body
			Map.entry(43710, 564),//Cosmic
			Map.entry(43706, 562),//Chaos
			Map.entry(43712, 563),//Law
			Map.entry(43711, 561),//Nature
			Map.entry(43707, 560),//Death
			Map.entry(43708, 565)//Blood
	);

	@Provides
	SpoonGOTRConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(SpoonGOTRConfig.class);
	}

	@Override
	protected void startUp() {
		reset();
		overlayManager.add(overlay);
		overlayManager.add(timerPanel);
		overlayManager.add(pointsPanel);
	}

	@Override
	protected void shutDown() {
		reset();
		overlayManager.remove(overlay);
		overlayManager.remove(timerPanel);
		overlayManager.remove(pointsPanel);
		infoBoxManager.removeInfoBox(timerBox);
	}

	private void reset() {
		guardians.clear();
		hugePortal = null;
		portalTicks = 0;
		gameStart = false;
		showPoints = false;
		timeTillNextGame = -1;
		ticksSincePortal = 0;
		bigGuy = null;
		hasCell = false;
		hasGuardianStone = false;
		hasUnchargedCells = false;
		portalsSpawned = 0;
		startTick = -1;
		unchargedCellTable = null;
		eleGuardian = null;
		cataGuardian = null;
		elePoints = "";
		cataPoints = "";
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals("SpoonGOTR")) {
			if (event.getKey().equals("instanceTimer")) {
				if (config.instanceTimer() && gameStart) {
                    BufferedImage image = itemManager.getImage(26348);
					timerBox = new SpoonGOTRTimerBox(image, config, this, client);
					infoBoxManager.addInfoBox(timerBox);
                } else {
					infoBoxManager.removeInfoBox(timerBox);
				}
			}
		}
	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event) {
		if (event.getGameObject().getId() >= 43701 && event.getGameObject().getId() <= 43712 && guardians.size() < 12) {
			guardians.add(event.getGameObject());
		} else if (event.getGameObject().getId() == 43729) {
			hugePortal = event.getGameObject();
			if (portalTicks == 0) {
				portalTicks = 45;
				ticksSincePortal = 0;
			}
		} else if ((event.getGameObject().getId() >= 34760 && event.getGameObject().getId() <= 34770) || event.getGameObject().getId() == 43479) {
			guardians.clear();
			hugePortal = null;
			unchargedCellTable = null;
			eleGuardian = null;
			cataGuardian = null;
		} else if (event.getGameObject().getId() == 43732) {
			unchargedCellTable = event.getGameObject();
		} else if (event.getGameObject().getId() == 43722) {
			eleGuardian = event.getGameObject();
		} else if (event.getGameObject().getId() == 43723) {
			cataGuardian = event.getGameObject();
		}
	}

	@Subscribe
	private void onGameTick(GameTick event) {
		if (portalTicks > 0) {
			portalTicks--;
			if(portalTicks <= 0) {
				hugePortal = null;
			}
		}

		if (gameStart) {
			ticksSincePortal++;
			if (client.getWidget(48889885) != null)
				guardianWidgetText = Objects.requireNonNull(client.getWidget(48889885)).getText();
		} else {
			if (timeTillNextGame >= 0)
				timeTillNextGame++;
		}

		int ticksTillPortal = portalsSpawned > 0 ? 225 - ticksSincePortal : 256 - ticksSincePortal;
		if (ticksTillPortal <= 0 && portalTicks == 0 && hugePortal == null
				&& client.getWidget(48889883) != null && !Objects.requireNonNull(client.getWidget(48889883)).getText().equals("")) {
			portalTicks = 45;
			ticksSincePortal = 0;
		}
	}

	@Subscribe
	private void onChatMessage(ChatMessage event) {
		if (event.getMessage().contains("A portal to the huge guardian fragment mine has opened to the")) {
			portalTicks = 45;
			ticksSincePortal = 0;
			portalsSpawned++;
		} else if (event.getMessage().contains("The Great Guardian successfully closed the rift!") || event.getMessage().contains("The Great Guardian was defeated!")) {
			gameStart = false;
			showPoints = true;
			cataPoints = "";
			elePoints = "";
			timeTillNextGame = 0;
			portalsSpawned = 0;
			infoBoxManager.removeInfoBox(timerBox);
			startTick = -1;
		} else if (event.getMessage().contains("Elemental attunement level:") && event.getMessage().contains("Catalytic attunement level:")) {
			String msg = Text.removeTags(event.getMessage());
			elePoints = msg.substring(msg.indexOf(": ") + 2, msg.indexOf(". Catalytic"));
			cataPoints = msg.substring(msg.indexOf("Catalytic attunement level: ") + 28, msg.lastIndexOf("."));
		} else if (event.getMessage().contains("The rift will become active in 30 seconds.")) {
			timeTillNextGame = 54;
		} else if (event.getMessage().contains("The rift will become active in 10 seconds.")) {
			timeTillNextGame = 87;
		} else if (event.getMessage().contains("The rift will become active in 5 seconds.")) {
			timeTillNextGame = 96;
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event) {
		if (client.getLocalPlayer() != null && ((event.getGameState() != GameState.LOGGED_IN && event.getGameState() != GameState.LOADING) || Arrays.stream(client.getMapRegions()).noneMatch(region -> region == 14484))) {
			guardians.clear();
			hugePortal = null;
			unchargedCellTable = null;
			eleGuardian = null;
			cataGuardian = null;
			showPoints = false;
			timeTillNextGame = -1;
		}
	}

	@Subscribe
	private void onWidgetLoaded(WidgetLoaded event) {
		if (event.getGroupId() == 746) {
			if(!gameStart) {
				ticksSincePortal = 0;
				gameStart = true;
				showPoints = false;
				timeTillNextGame = -1;
				if (startTick == -1) {
					startTick = client.getTickCount();
					if (config.instanceTimer()) {
						BufferedImage image = itemManager.getImage(26899);
						timerBox = new SpoonGOTRTimerBox(image, config, this, client);
						infoBoxManager.addInfoBox(timerBox);
					}
				}
			}
		} else if (event.getGroupId() == 745 && client.getWidget(745, 0) != null && config.hideFlashbang()) {
			Objects.requireNonNull(client.getWidget(745, 0)).setHidden(true);
		}
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned event) {
		if (event.getNpc().getId() == 11403) {
			bigGuy = event.getNpc();
		} 
	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned event) {
		if (event.getNpc().getId() == 11403) {
			bigGuy = null;
		} 
	}

	@Subscribe
    private void onItemContainerChanged(ItemContainerChanged event) {
		if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
			ItemContainer ic = client.getItemContainer(InventoryID.INVENTORY);
			if (ic != null) {
				hasCell = ic.count(26883) > 0 || ic.count(26884) > 0 || ic.count(26885) > 0 || ic.count(26886) > 0;
				hasGuardianStone = ic.count(26880) > 0 || ic.count(26881) > 0;
				hasUnchargedCells = ic.count(26882) > 0;
			}
		}
    }

	@Subscribe
    public void onMenuEntryAdded(MenuEntryAdded entry) {
        if (gameStart) {
            String target = Text.standardize(entry.getTarget());
			String option = Text.standardize(entry.getOption());
            if (config.hideNoCell() && !hasCell
					&& ((option.contains("assemble") && target.contains("essence pile (")) || (option.contains("place-cell") && target.contains(" cell tile")))) {
				client.setMenuOptionCount(client.getMenuOptionCount() - 1);
			} else if (config.hidePowerUp() && option.contains("power-up") && target.contains("the great guardian") && !hasGuardianStone) {
				client.setMenuOptionCount(client.getMenuOptionCount() - 1);
			}
		}
	}

	public String ticksToSeconds(int ticks) {
		int min = ticks / 100;
		int tmp = (ticks - min * 100) * 6;
		int sec = tmp / 10;
		return min + (sec < 10 ? ":0" : ":") + sec;
	}
}
