package net.runelite.client.plugins.spoonbarrows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;
import net.runelite.client.ui.overlay.infobox.LoopTimer;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Extension
@PluginDescriptor(
		name = "<html><font color=#25c550>[S] Barrows Brothers",
		description = "Show helpful information for the Barrows minigame",
		tags = {"combat", "minigame", "bosses", "pve", "pvm"},
		conflicts = "Barrows Brothers"
)
public class sBarrowsPlugin extends Plugin {
	static Set<Integer> getBARROWS_WALLS() {
		return BARROWS_WALLS;
	}

	private static final Set<Integer> BARROWS_WALLS = Sets.newHashSet(20678, 20681, 20682, 20683, 20684, 20685, 20686, 20687,
			20688, 20689, 20690, 20691, 20692, 20693, 20694, 20695, 20696, 20697, 20700, 20701, 20702, 20703, 20704, 20705,
			20706, 20707, 20708, 20709, 20710, 20711, 20712, 20713, 20714, 20715, 20728, 20730);

	private static final Set<Integer> BARROWS_LADDERS = Sets.newHashSet(20675, 20676, 20677);

	private static final ImmutableList<WidgetInfo> POSSIBLE_SOLUTIONS = ImmutableList.of(WidgetInfo.BARROWS_PUZZLE_ANSWER1, WidgetInfo.BARROWS_PUZZLE_ANSWER2, WidgetInfo.BARROWS_PUZZLE_ANSWER3);

	private static final long PRAYER_DRAIN_INTERVAL_MS = 18200;
	private static final int CRYPT_REGION_ID = 14231;
	private static final int BARROWS_REGION_ID = 14131;

	private final Set<WallObject> walls = new HashSet<>();

	Set<WallObject> getWalls() {
		return this.walls;
	}

	private final Set<GameObject> ladders = new HashSet<>();

	private LoopTimer barrowsPrayerDrainTimer;

	Set<GameObject> getLadders() {
		return this.ladders;
	}

	private boolean wasInCrypt = false;

	private Widget puzzleAnswer;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private sBarrowsOverlay sBarrowsOverlay;

	@Inject
	private sBarrowsBrotherSlainOverlay brotherOverlay;

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private sBarrowsConfig config;

	public Widget getPuzzleAnswer() {
		return this.puzzleAnswer;
	}

	@Provides
	sBarrowsConfig provideConfig(ConfigManager configManager) {
		return (sBarrowsConfig)configManager.getConfig(sBarrowsConfig.class);
	}

	protected void startUp() throws Exception {
		this.overlayManager.add(this.sBarrowsOverlay);
		this.overlayManager.add((Overlay)this.brotherOverlay);
	}

	protected void shutDown() {
		this.overlayManager.remove(this.sBarrowsOverlay);
		this.overlayManager.remove(this.brotherOverlay);
		this.puzzleAnswer = null;
		this.wasInCrypt = false;
		stopPrayerDrainTimer();

		// Restore widgets
		final Widget potential = client.getWidget(WidgetInfo.BARROWS_POTENTIAL);
		if (potential != null)
		{
			potential.setHidden(false);
		}

		final Widget barrowsBrothers = client.getWidget(WidgetInfo.BARROWS_BROTHERS);
		if (barrowsBrothers != null)
		{
			barrowsBrothers.setHidden(false);
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals("barrows") && !this.config.showPrayerDrainTimer())
			stopPrayerDrainTimer();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOADING) {
			this.wasInCrypt = isInCrypt();
			this.walls.clear();
			this.ladders.clear();
			this.puzzleAnswer = null;
		} else if (event.getGameState() == GameState.LOGGED_IN) {
			boolean isInCrypt = isInCrypt();
			if (this.wasInCrypt && !isInCrypt) {
				stopPrayerDrainTimer();
			} else if (!this.wasInCrypt && isInCrypt) {
				startPrayerDrainTimer();
			}
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == WidgetID.BARROWS_REWARD_GROUP_ID && config.showChestValue())
		{
			ItemContainer barrowsRewardContainer = client.getItemContainer(InventoryID.BARROWS_REWARD);
			Item[] items = barrowsRewardContainer.getItems();
			long chestPrice = 0;

			for (Item item : items)
			{
				long itemStack = (long) itemManager.getItemPrice(item.getId()) * (long) item.getQuantity();
				chestPrice += itemStack;
			}

			final ChatMessageBuilder message = new ChatMessageBuilder()
					.append(ChatColorType.HIGHLIGHT)
					.append("Your chest is worth around ")
					.append(QuantityFormatter.formatNumber(chestPrice))
					.append(" coins.")
					.append(ChatColorType.NORMAL);

			chatMessageManager.queue(QueuedMessage.builder()
					.type(ChatMessageType.ITEM_EXAMINE)
					.runeLiteFormattedMessage(message.build())
					.build());
		}
		else if (event.getGroupId() == WidgetID.BARROWS_PUZZLE_GROUP_ID)
		{
			final int answer = client.getWidget(WidgetInfo.BARROWS_FIRST_PUZZLE).getModelId() - 3;
			puzzleAnswer = null;

			for (WidgetInfo puzzleNode : POSSIBLE_SOLUTIONS)
			{
				final Widget widgetToCheck = client.getWidget(puzzleNode);

				if (widgetToCheck != null && widgetToCheck.getModelId() == answer)
				{
					puzzleAnswer = client.getWidget(puzzleNode);
					break;
				}
			}
		}
	}

	@Subscribe
	public void onBeforeRender(BeforeRender beforeRender)
	{
		// The barrows brothers and potential overlays have timers to unhide them each tick. Set them
		// hidden here instead of in the overlay, because if the overlay renders on the ABOVE_WIDGETS
		// layer due to being moved outside of the snap corner, it will be running after the overlays
		// had already been rendered.
		final Widget barrowsBrothers = client.getWidget(WidgetInfo.BARROWS_BROTHERS);
		if (barrowsBrothers != null)
		{
			barrowsBrothers.setHidden(true);
		}

		final Widget potential = client.getWidget(WidgetInfo.BARROWS_POTENTIAL);
		if (potential != null)
		{
			potential.setHidden(true);
		}
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed widgetClosed)
	{
		if (widgetClosed.getGroupId() == WidgetID.BARROWS_PUZZLE_GROUP_ID)
		{
			puzzleAnswer = null;
		}
	}

	private void startPrayerDrainTimer()
	{
		if (config.showPrayerDrainTimer())
		{
			assert barrowsPrayerDrainTimer == null;
			final LoopTimer loopTimer = new LoopTimer(
					PRAYER_DRAIN_INTERVAL_MS,
					ChronoUnit.MILLIS,
					null,
					this,
					true);

			spriteManager.getSpriteAsync(SpriteID.TAB_PRAYER, 0, loopTimer);

			loopTimer.setPriority(InfoBoxPriority.MED);
			loopTimer.setTooltip("Prayer Drain");

			infoBoxManager.addInfoBox(loopTimer);
			barrowsPrayerDrainTimer = loopTimer;
		}
	}

	private void stopPrayerDrainTimer()
	{
		infoBoxManager.removeInfoBox(barrowsPrayerDrainTimer);
		barrowsPrayerDrainTimer = null;
	}

	private boolean isInCrypt()
	{
		Player localPlayer = client.getLocalPlayer();
		return localPlayer != null && localPlayer.getWorldLocation().getRegionID() == CRYPT_REGION_ID;
	}

	@Subscribe
	public void onWallObjectSpawned(WallObjectSpawned event) {
		WallObject wallObject = event.getWallObject();
		if (BARROWS_WALLS.contains(wallObject.getId()))
			this.walls.add(wallObject);
	}

	@Subscribe
	public void onWallObjectChanged(WallObjectChanged event) {
		WallObject previous = event.getPrevious();
		WallObject wallObject = event.getWallObject();
		this.walls.remove(previous);
		if (BARROWS_WALLS.contains(wallObject.getId()))
			this.walls.add(wallObject);
	}

	@Subscribe
	public void onWallObjectDespawned(WallObjectDespawned event) {
		WallObject wallObject = event.getWallObject();
		this.walls.remove(wallObject);
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event) {
		GameObject gameObject = event.getGameObject();
		if (BARROWS_LADDERS.contains(gameObject.getId()))
			this.ladders.add(gameObject);
	}

	@Subscribe
	public void onGameObjectChanged(GameObjectChanged event) {
		GameObject previous = event.getPrevious();
		GameObject gameObject = event.getGameObject();
		this.ladders.remove(previous);
		if (BARROWS_LADDERS.contains(gameObject.getId()))
			this.ladders.add(gameObject);
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event) {
		GameObject gameObject = event.getGameObject();
		this.ladders.remove(gameObject);
	}

	boolean isBarrowsLoaded()
	{
		return ArrayUtils.contains(client.getMapRegions(), BARROWS_REGION_ID);
	}
}
