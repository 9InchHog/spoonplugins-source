package net.runelite.client.plugins.spoongroundmarkers;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.*;

import static net.runelite.api.Constants.CHUNK_SIZE;

@Extension
@PluginDescriptor(
	name = "<html><font color=#25c550>[S] Ground Markers",
	description = "Enable marking of tiles using the Shift key",
	tags = {"overlay", "tiles"},
	conflicts = "Ground Markers"
)
@Slf4j
public class sGroundMarkerPlugin extends Plugin {
	private static final String CONFIG_GROUP = "groundMarker";
	private static final String MARK = "Mark tile";
	private static final String UNMARK = "Unmark tile";
	private static final String LABEL = "Label tile";
	private static final String WALK_HERE = "Walk here";
	private static final String REGION_PREFIX = "region_";

	private static final Gson GSON = new Gson();

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private boolean hotKeyPressed;

	@Getter(AccessLevel.PACKAGE)
	private final List<sGroundMarkerWorldPoint> points = new ArrayList<>();

	@Inject
	private Client client;

	@Inject
	private GroundMarkerInputListener inputListener;

	@Inject
	private ConfigManager configManager;

	@Inject
	private sGroundMarkerConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private sGroundMarkerOverlay overlay;

	@Inject
	private sGroundMarkerMinimapOverlay minimapOverlay;

	@Inject
	private EventBus eventBus;

	@Inject
	private GroundMarkerSharingManager sharingManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	public void savePoints(int regionId, Collection<GroundMarkerPoint> points) {
		if (points == null || points.isEmpty()) {
			configManager.unsetConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId);
			return;
		}

		String json = GSON.toJson(points);
		configManager.setConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId, json);
	}

	public Collection<GroundMarkerPoint> getPoints(int regionId) {
		String json = configManager.getConfiguration(CONFIG_GROUP, REGION_PREFIX + regionId);
		if (Strings.isNullOrEmpty(json)) {
			return Collections.emptyList();
		}
		return GSON.fromJson(json, new GroundMarkerListTypeToken().getType());
	}

	private static class GroundMarkerListTypeToken extends TypeToken<List<GroundMarkerPoint>> {
	}

	@Provides
	sGroundMarkerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(sGroundMarkerConfig.class);
	}

	public void loadPoints() {
		points.clear();
		int[] regions = client.getMapRegions();
		if (regions != null) {
			for (int regionId : regions) {
				Collection<GroundMarkerPoint> regionPoints = getPoints(regionId);
				Collection<sGroundMarkerWorldPoint> worldPoints = translateToWorld(regionPoints);
				points.addAll(worldPoints);
			}
		}
	}

	/**
	 * Translate a collection of ground marker points to world points, accounting for instances
	 *
	 * @param points
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Collection<sGroundMarkerWorldPoint> translateToWorld(Collection<GroundMarkerPoint> points)
	{
		if (points.isEmpty())
		{
			return Collections.emptyList();
		}

		List<sGroundMarkerWorldPoint> worldPoints = new ArrayList<>();
		for (GroundMarkerPoint point : points)
		{
			int regionId = point.getRegionId();
			int regionX = point.getRegionX();
			int regionY = point.getRegionY();
			int z = point.getZ();

			WorldPoint worldPoint = WorldPoint.fromRegion(regionId, regionX, regionY, z);

			if (!client.isInInstancedRegion())
			{
				worldPoints.add(new sGroundMarkerWorldPoint(point, worldPoint));
				continue;
			}

			// find instance chunks using the template point. there might be more than one.
			int[][][] instanceTemplateChunks = client.getInstanceTemplateChunks();
			for (int x = 0; x < instanceTemplateChunks[z].length; ++x)
			{
				for (int y = 0; y < instanceTemplateChunks[z][x].length; ++y)
				{
					int chunkData = instanceTemplateChunks[z][x][y];
					int rotation = chunkData >> 1 & 0x3;
					int templateChunkY = (chunkData >> 3 & 0x7FF) * CHUNK_SIZE;
					int templateChunkX = (chunkData >> 14 & 0x3FF) * CHUNK_SIZE;
					if (worldPoint.getX() >= templateChunkX && worldPoint.getX() < templateChunkX + CHUNK_SIZE
						&& worldPoint.getY() >= templateChunkY && worldPoint.getY() < templateChunkY + CHUNK_SIZE)
					{
						WorldPoint p = new WorldPoint(client.getBaseX() + x * CHUNK_SIZE + (worldPoint.getX() & (CHUNK_SIZE - 1)),
							client.getBaseY() + y * CHUNK_SIZE + (worldPoint.getY() & (CHUNK_SIZE - 1)),
							worldPoint.getPlane());
						p = rotate(p, rotation);
						worldPoints.add(new sGroundMarkerWorldPoint(point, p));
					}
				}
			}
		}
		return worldPoints;
	}

	/**
	 * Rotate the chunk containing the given point to rotation 0
	 *
	 * @param point    point
	 * @param rotation rotation
	 * @return world point
	 */
	private static WorldPoint rotateInverse(WorldPoint point, int rotation)
	{
		return rotate(point, 4 - rotation);
	}

	/**
	 * Rotate the coordinates in the chunk according to chunk rotation
	 *
	 * @param point    point
	 * @param rotation rotation
	 * @return world point
	 */
	private static WorldPoint rotate(WorldPoint point, int rotation)
	{
		int chunkX = point.getX() & -CHUNK_SIZE;
		int chunkY = point.getY() & -CHUNK_SIZE;
		int x = point.getX() & (CHUNK_SIZE - 1);
		int y = point.getY() & (CHUNK_SIZE - 1);
		switch (rotation)
		{
			case 1:
				return new WorldPoint(chunkX + y, chunkY + (CHUNK_SIZE - 1 - x), point.getPlane());
			case 2:
				return new WorldPoint(chunkX + (CHUNK_SIZE - 1 - x), chunkY + (CHUNK_SIZE - 1 - y), point.getPlane());
			case 3:
				return new WorldPoint(chunkX + (CHUNK_SIZE - 1 - y), chunkY + x, point.getPlane());
		}
		return point;
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		// map region has just been updated
		loadPoints();
	}

	@Subscribe
	private void onFocusChanged(FocusChanged focusChanged)
	{
		if (!focusChanged.isFocused())
		{
			hotKeyPressed = false;
		}
	}

	@Subscribe
	private void onMenuEntryAdded(MenuEntryAdded event) {
		if (hotKeyPressed && event.getOption().equals(WALK_HERE)) {
			final Tile selectedSceneTile = client.getSelectedSceneTile();
			if (selectedSceneTile != null) {
				MenuEntry[] menuEntries = client.getMenuEntries();
				int lastIndex = menuEntries.length;
				final Tile tile = client.getSelectedSceneTile();

				if (tile != null) {
					final WorldPoint loc = WorldPoint.fromLocalInstance(client, tile.getLocalLocation());
					if (loc != null) {
						final int regionId = loc.getRegionID();
						for (int i = config.getAmount().toInt(); i > 0; i--) {
							int finalI = i;
							final Optional<GroundMarkerPoint> stream = getPoints(regionId).stream().filter(x ->
									x.getRegionId() == regionId && x.getRegionX() == loc.getRegionX() && x.getRegionY() == loc.getRegionY() && x.getZ() == client.getPlane()
											&& x.getGroup() == finalI).findAny();
							String option = (stream.isPresent() && stream.get().getGroup() == i) ? UNMARK : MARK;
							option = Text.removeTags(option + (i == 1 ? "" : " (Group " + i + ")"));
							if (option.contains(UNMARK)) {
								menuEntries = Arrays.copyOf(menuEntries, lastIndex + config.getAmount().toInt() + 1);
								lastIndex = menuEntries.length - 1;
							} else {
								menuEntries = Arrays.copyOf(menuEntries, lastIndex + config.getAmount().toInt());
							}

							client.createMenuEntry(-1)
									.setOption(ColorUtil.prependColorTag(option, getColor(i)))
									.setTarget(event.getTarget())
									.setType(MenuAction.RUNELITE)
									.onClick(e ->
									{
										Tile target = client.getSelectedSceneTile();
										if (target != null)
										{
											markTile(target.getLocalLocation(), finalI, "");
										}
									});

							if (option.equals(UNMARK) || (option.contains(UNMARK) && option.contains(" (Group "))){
								client.createMenuEntry(-2)
										.setOption(LABEL + (i == 1 ? "" : " (Group " + i + ")"))
										.setTarget(event.getTarget())
										.setType(MenuAction.RUNELITE)
										.onClick(e ->
										{
											Tile target = client.getSelectedSceneTile();
											if (target != null)
											{
												labelTile(target, finalI);
											}
										});
							}

							lastIndex++;
						}
						//client.setMenuEntries(menuEntries);
					}
				}
			}
		}
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(minimapOverlay);
		if (config.showImportExport())
		{
			sharingManager.addImportExportMenuOptions();
		}
		if (config.showClear())
		{
			sharingManager.addClearMenuOption();
		}
		keyManager.registerKeyListener(inputListener);
		loadPoints();
		eventBus.register(sharingManager);
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(overlay);
		overlayManager.remove(minimapOverlay);
		keyManager.unregisterKeyListener(inputListener);
		points.clear();
	}

	private void markTile(LocalPoint localPoint, int group, String label) {
		if (localPoint != null) {
			WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, localPoint);
			if (worldPoint != null) {
				int regionId = worldPoint.getRegionID();
				List<GroundMarkerPoint> pointArea = new ArrayList<GroundMarkerPoint>();
				int size = config.tileSize().getSize();
				int offset = size / 2;

				for (int x = 0; x < size; x++) {
					for (int y = 0; y < size; y++) {
						pointArea.add(new GroundMarkerPoint(regionId, worldPoint.getRegionX() + x - offset, worldPoint.getRegionY() + y - offset, client.getPlane(), group, label));
					}
				}

				List<GroundMarkerPoint> points = new ArrayList<>(getPoints(regionId));
				for(GroundMarkerPoint pt : pointArea) {
					if (points.contains(pt)) {
						GroundMarkerPoint old = points.get(points.indexOf(pt));
						points.remove(pt);

						if (old.getGroup() != group) {
							points.add(pt);
						}
					}else {
						boolean found = false;
						GroundMarkerPoint old = null;
						for(GroundMarkerPoint gmp : getPoints(regionId)){
							if (gmp.getRegionId() == pt.getRegionId() && gmp.getRegionX() == pt.getRegionX() && gmp.getRegionY() == pt.getRegionY() && gmp.getZ() == pt.getZ()) {
								found = true;
								old = gmp;
								break;
							}
						}

						if(found){
							points.remove(old);
							if (old.getGroup() != group) {
								points.add(pt);
							}
						}else{
							points.add(pt);
						}
					}
				}
				savePoints(regionId, points);
				loadPoints();
			}
		}
	}

	public Color getColor(int group)
	{
		Color color = config.markerColor();
		switch (group)
		{
			case 2:
				color = config.markerColor2();
				break;
			case 3:
				color = config.markerColor3();
				break;
			case 4:
				color = config.markerColor4();
				break;
			case 5:
				color = config.markerColor5();
				break;
			case 6:
				color = config.markerColor6();
				break;
			case 7:
				color = config.markerColor7();
				break;
			case 8:
				color = config.markerColor8();
				break;
			case 9:
				color = config.markerColor9();
				break;
			case 10:
				color = config.markerColor10();
				break;
			case 11:
				color = config.markerColor11();
				break;
			case 12:
				color = config.markerColor12();
		}

		return color;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(sGroundMarkerConfig.GROUND_MARKER_CONFIG_GROUP)
				&& (event.getKey().equals(sGroundMarkerConfig.SHOW_IMPORT_EXPORT_KEY_NAME)
				|| event.getKey().equals(sGroundMarkerConfig.SHOW_CLEAR_KEY_NAME)))
		{
			// Maintain consistent menu option order by removing everything then adding according to config
			sharingManager.removeMenuOptions();

			if (config.showImportExport())
			{
				sharingManager.addImportExportMenuOptions();
			}
			if (config.showClear())
			{
				sharingManager.addClearMenuOption();
			}
		}
	}

	private void labelTile(Tile tile, int group) {
		LocalPoint localPoint = tile.getLocalLocation();
		WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, localPoint);
		final int regionId = worldPoint.getRegionID();

		GroundMarkerPoint searchPoint = new GroundMarkerPoint(regionId, worldPoint.getRegionX(), worldPoint.getRegionY(), client.getPlane(), group, "");
		Collection<GroundMarkerPoint> points = getPoints(regionId);
		points.stream()
				.filter(p -> p.getRegionId() == regionId && p.getRegionX() == worldPoint.getRegionX() && p.getRegionY() == worldPoint.getRegionY() && p.getZ() == client.getPlane()
						&& p.getGroup() == group)
				.findFirst().ifPresent(existing -> chatboxPanelManager.openTextInput("Tile label")
						.value(Optional.ofNullable(existing.getLabel()).orElse(""))
						.onDone((input) -> {
							input = Strings.emptyToNull(input);

							GroundMarkerPoint newPoint = new GroundMarkerPoint(regionId, worldPoint.getRegionX(), worldPoint.getRegionY(), client.getPlane(), group, input);
							points.remove(existing);
							points.add(newPoint);
							savePoints(regionId, points);

							loadPoints();
						})
						.build());
	}
}
