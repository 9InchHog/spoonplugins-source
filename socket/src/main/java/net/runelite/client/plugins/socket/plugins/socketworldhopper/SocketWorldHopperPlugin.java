package net.runelite.client.plugins.socket.plugins.socketworldhopper;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanChannelMember;
import net.runelite.api.events.*;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.WorldService;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.socket.SocketPlugin;
import net.runelite.client.plugins.socket.org.json.JSONArray;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
import net.runelite.client.plugins.socket.plugins.socketworldhopper.ping.Ping;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.*;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Extension
@PluginDescriptor(
        name = "Socket - World Hopper",
        description = "Allows you to quickly hop worlds",
        conflicts = "World Hopper"
)
@PluginDependency(SocketPlugin.class)
public class SocketWorldHopperPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(SocketWorldHopperPlugin.class);
    private static final int WORLD_FETCH_TIMER = 10;
    private static final int REFRESH_THROTTLE = 60_000; // ms
    private static final int MAX_PLAYER_COUNT = 1950;
    private static final int TICK_THROTTLE = (int)Duration.ofMinutes(10L).toMillis();
    private static final int DISPLAY_SWITCHER_MAX_ATTEMPTS = 3;
    private static final String HOP_TO = "Hop-to";
    private static final String KICK_OPTION = "Kick";
    private static final ImmutableList<String> BEFORE_OPTIONS = ImmutableList.of("Add friend", "Remove friend", "Kick");
    private static final ImmutableList<String> AFTER_OPTIONS = ImmutableList.of("Message");
    public static boolean allowHopping = true;

    @Inject
    private EventBus eventBus;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ConfigManager configManager;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private WorldService worldService;

    @Inject
    private ScheduledExecutorService executorService;

    @Inject
    private SocketWorldHopperConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SocketWorldHopperPingOverlay worldHopperOverlay;

    private ScheduledExecutorService hopperExecutorService;
    private ScheduledExecutorService hopBlocked;

    private NavigationButton navButton;
    private SocketWorldSwitcherPanel panel;

    private net.runelite.api.World quickHopTargetWorld;
    private int displaySwitcherAttempts = 0;

    @Getter
    private int lastWorld;

    private int favoriteWorld1, favoriteWorld2;

    private ScheduledFuture<?> worldResultFuture;
    private ScheduledFuture<?> pingFuture, currPingFuture;
    private WorldResult worldResult;

    private int currentWorld;
    private Instant lastFetch;

    private boolean firstRun;
    private String customWorlds;
    private int logOutNotifTick = -1;
    private long hopDelay = 0L;
    private long hopDelayMS = 0L;
    private boolean allowedToHop = true;

    @Getter(AccessLevel.PACKAGE)
    private int currentPing;

    private final Map<Integer, Integer> storedPings = new HashMap<>();

    private final HotkeyListener previousKeyListener = new HotkeyListener(() -> this.config.previousKey()) {
        public void hotkeyPressed() {
            clientThread.invoke(() -> hop(true));
        }
    };

    private final HotkeyListener nextKeyListener = new HotkeyListener(() -> this.config.nextKey()) {
        public void hotkeyPressed() {
            clientThread.invoke(() -> hop(false));
        }
    };

    @Provides
    SocketWorldHopperConfig getConfig(ConfigManager configManager) {
        return (SocketWorldHopperConfig)configManager.getConfig(SocketWorldHopperConfig.class);
    }

    protected void startUp() throws Exception {
        allowedToHop = true;
        hopDelay = 0L;
        firstRun = true;

        currentPing = -1;
        customWorlds = config.customWorldCycle();

        keyManager.registerKeyListener(previousKeyListener);
        keyManager.registerKeyListener(nextKeyListener);

        panel = new SocketWorldSwitcherPanel(this);

        BufferedImage icon = ImageUtil.loadImageResource(SocketWorldHopperPlugin.class, "icon.png");
        navButton = NavigationButton.builder()
                .tooltip("World Switcher")
                .icon(icon)
                .priority(3)
                .panel(panel)
                .build();

        if (config.showSidebar()) {
            clientToolbar.addNavigation(navButton);
        }

        overlayManager.add(worldHopperOverlay);

        panel.setSubscriptionFilterMode(config.subscriptionFilter());
        panel.setRegionFilterMode(config.regionFilter());

        hopperExecutorService = new ExecutorServiceExceptionLogger(Executors.newSingleThreadScheduledExecutor());
        hopBlocked = new ExecutorServiceExceptionLogger(Executors.newSingleThreadScheduledExecutor());
        worldResultFuture = executorService.scheduleAtFixedRate(this::tick, 0L, 10L, TimeUnit.MINUTES);

        // Give some initial delay - this won't run until after pingInitialWorlds finishes from tick() anyway
        pingFuture = hopperExecutorService.scheduleWithFixedDelay(this::pingNextWorld, 15, 3, TimeUnit.SECONDS);
        currPingFuture = hopperExecutorService.scheduleWithFixedDelay(this::pingCurrentWorld, 15, 1, TimeUnit.SECONDS);

        // populate initial world list
        updateList();
    }

    protected void shutDown() throws Exception {
        allowedToHop = true;
        hopDelay = 0L;

        pingFuture.cancel(true);
        pingFuture = null;

        currPingFuture.cancel(true);
        currPingFuture = null;

        overlayManager.remove(worldHopperOverlay);

        keyManager.unregisterKeyListener(previousKeyListener);
        keyManager.unregisterKeyListener(nextKeyListener);

        worldResultFuture.cancel(true);
        worldResultFuture = null;
        worldResult = null;
        lastFetch = null;

        clientToolbar.removeNavigation(navButton);

        hopperExecutorService.shutdown();
        hopperExecutorService = null;
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        long x = System.currentTimeMillis() - hopDelay;
        if (x > 11000L) {
            allowedToHop = true;
        } else {
            allowedToHop = false;
        }
        hopDelayMS = 11000L - x;
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        String name = event.getActor().getName();
        if (name != null && event.getActor().getAnimation() != 829 && name.equals(Objects.requireNonNull(client.getLocalPlayer()).getName())) {
            hopDelay = System.currentTimeMillis();
            hopDelayMS = 11000L;
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("socketworldhopper")) {
            switch (event.getKey())
            {
                case "showSidebar":
                    if (config.showSidebar())
                    {
                        clientToolbar.addNavigation(navButton);
                    }
                    else
                    {
                        clientToolbar.removeNavigation(navButton);
                    }
                    break;
                case "ping":
                    if (config.ping())
                    {
                        SwingUtilities.invokeLater(() -> panel.showPing());
                    }
                    else
                    {
                        SwingUtilities.invokeLater(() -> panel.hidePing());
                    }
                    break;
                case "subscriptionFilter":
                    panel.setSubscriptionFilterMode(config.subscriptionFilter());
                    updateList();
                    break;
                case "regionFilter":
                    panel.setRegionFilterMode(config.regionFilter());
                    updateList();
                    break;
                case "customWorldCycle":
                    customWorlds = config.customWorldCycle();
                    String s = config.customWorldCycle();
                    JSONArray data = new JSONArray();
                    JSONObject jsonwp = new JSONObject();
                    jsonwp.put("worlds", s);
                    data.put(jsonwp);
                    JSONObject payload = new JSONObject();
                    payload.put("worldhopper-extended", data);
                    eventBus.post(new SocketBroadcastPacket(payload));
                    break;
            }
        }

    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event) {
        try {
            JSONObject payload = event.getPayload();
            if (!payload.has("worldhopper-extended")) {
                return;
            }

            JSONArray data = payload.getJSONArray("worldhopper-extended");
            JSONObject jsonwp = data.getJSONObject(0);
            String worlds = jsonwp.getString("worlds");
            clientThread.invoke(() -> client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=b4281e>Custom world list: " + worlds + ".", null));
            customWorlds = worlds;
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    private void setFavoriteConfig(int world)
    {
        configManager.setConfiguration(SocketWorldHopperConfig.GROUP, "favorite_" + world, true);
    }

    private boolean isFavoriteConfig(int world)
    {
        Boolean favorite = configManager.getConfiguration(SocketWorldHopperConfig.GROUP, "favorite_" + world, Boolean.class);
        return favorite != null && favorite;
    }

    private void clearFavoriteConfig(int world)
    {
        configManager.unsetConfiguration(SocketWorldHopperConfig.GROUP, "favorite_" + world);
    }

    boolean isFavorite(World world) {
        int id = world.getId();
        return id == favoriteWorld1 || id == favoriteWorld2 || isFavoriteConfig(id);
    }

    int getCurrentWorld() {
        return client.getWorld();
    }

    void hopTo(World world) {
        clientThread.invoke(() -> hop(world.getId()));
    }

    void addToFavorites(World world) {
        log.debug("Adding world {} to favorites", world.getId());
        setFavoriteConfig(world.getId());
        panel.updateFavoriteMenu(world.getId(), true);
    }

    void removeFromFavorites(World world) {
        log.debug("Removing world {} from favorites", world.getId());
        clearFavoriteConfig(world.getId());
        panel.updateFavoriteMenu(world.getId(), false);
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged)
    {
        int old1 = favoriteWorld1;
        int old2 = favoriteWorld2;

        favoriteWorld1 = client.getVar(Varbits.WORLDHOPPER_FAVROITE_1);
        favoriteWorld2 = client.getVar(Varbits.WORLDHOPPER_FAVROITE_2);

        if (old1 != favoriteWorld1 || old2 != favoriteWorld2)
        {
            SwingUtilities.invokeLater(panel::updateList);
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        if (!config.menuOption())
        {
            return;
        }

        final int componentId = event.getActionParam1();
        int groupId = WidgetInfo.TO_GROUP(componentId);
        String option = event.getOption();

        if (groupId == WidgetInfo.FRIENDS_LIST.getGroupId() || groupId == WidgetInfo.FRIENDS_CHAT.getGroupId()
                || componentId == WidgetInfo.CLAN_MEMBER_LIST.getId() || componentId == WidgetInfo.CLAN_GUEST_MEMBER_LIST.getId())
        {
            boolean after;

            if (AFTER_OPTIONS.contains(option))
            {
                after = true;
            }
            else if (BEFORE_OPTIONS.contains(option))
            {
                after = false;
            }
            else
            {
                return;
            }

            // Don't add entry if user is offline
            ChatPlayer player = getChatPlayerFromName(event.getTarget());
            WorldResult worldResult = worldService.getWorlds();

            if (player == null || player.getWorld() == 0 || player.getWorld() == client.getWorld()
                    || worldResult == null)
            {
                return;
            }

            World currentWorld = worldResult.findWorld(client.getWorld());
            World targetWorld = worldResult.findWorld(player.getWorld());
            if (targetWorld == null || currentWorld == null
                    || (!currentWorld.getTypes().contains(WorldType.PVP) && targetWorld.getTypes().contains(WorldType.PVP)))
            {
                // Disable Hop-to a PVP world from a regular world
                return;
            }

            client.createMenuEntry(after ? -2 : -1)
                    .setOption(HOP_TO)
                    .setTarget(event.getTarget())
                    .setType(MenuAction.RUNELITE)
                    .onClick(e ->
                    {
                        ChatPlayer p = getChatPlayerFromName(e.getTarget());

                        if (p != null)
                        {
                            hop(p.getWorld());
                        }
                    });
        }
    }

    private void insertMenuEntry(MenuEntry newEntry, MenuEntry[] entries, boolean after) {
        MenuEntry[] newMenu = ObjectArrays.concat(entries, newEntry);
        if (after) {
            int menuEntryCount = newMenu.length;
            ArrayUtils.swap(newMenu, menuEntryCount - 1, menuEntryCount - 2);
        }

        client.setMenuEntries(newMenu);
    }

    @Subscribe
    public void onPlayerDespawned(PlayerDespawned event) {
        if (!event.getPlayer().equals(client.getLocalPlayer()) && event.getPlayer().getName() != null) {
            SetHopAbility(event.getPlayer().getName().toLowerCase(), true);
        }
    }

    @Subscribe
    public void onPlayerSpawned(PlayerSpawned event) {
        if (!event.getPlayer().equals(client.getLocalPlayer()) && event.getPlayer().getName() != null) {
            SetHopAbility(event.getPlayer().getName().toLowerCase(), false);
        }
    }

    void SetHopAbility(String name, boolean enabled) {
        if (!name.isEmpty() && (name.equalsIgnoreCase(config.getHopperName().trim()) || name.equalsIgnoreCase(config.getHopperName2().trim()))) {
            logOutNotifTick = enabled ? client.getTickCount() : -1;
            allowHopping = enabled;
        }

    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        // If the player has disabled the side bar plugin panel, do not update the UI
        if (config.showSidebar() && gameStateChanged.getGameState() == GameState.LOGGED_IN)
        {
            if (lastWorld != client.getWorld())
            {
                int newWorld = client.getWorld();
                panel.switchCurrentHighlight(newWorld, lastWorld);
                lastWorld = newWorld;
            }
        }
    }

    @Subscribe
    public void onWorldListLoad(WorldListLoad worldListLoad)
    {
        if (!config.showSidebar())
        {
            return;
        }

        Map<Integer, Integer> worldData = new HashMap<>();

        for (net.runelite.api.World w : worldListLoad.getWorlds())
        {
            worldData.put(w.getId(), w.getPlayerCount());
        }

        panel.updateListData(worldData);
        this.lastFetch = Instant.now(); // This counts as a fetch as it updates populations
    }

    private void tick() {
        Instant now = Instant.now();
        if (lastFetch != null && now.toEpochMilli() - lastFetch.toEpochMilli() < TICK_THROTTLE) {
            log.debug("Throttling world refresh tick");
        } else {
            fetchWorlds();
            if (firstRun) {
                firstRun = false;
                hopperExecutorService.execute(this::pingInitialWorlds);
            }

        }
    }

    void refresh()
    {
        Instant now = Instant.now();
        if (lastFetch != null && now.toEpochMilli() - lastFetch.toEpochMilli() < REFRESH_THROTTLE)
        {
            log.debug("Throttling world refresh");
            return;
        }

        lastFetch = now;
        worldService.refresh();
    }

    private void fetchWorlds() {
        log.debug("Fetching worlds");
        WorldResult worldResult = worldService.getWorlds();
        if (worldResult != null) {
            worldResult.getWorlds().sort(Comparator.comparingInt(net.runelite.http.api.worlds.World::getId));
            this.worldResult = worldResult;
            lastFetch = Instant.now();
            updateList();
        }

    }

    /**
     * This method ONLY updates the list's UI, not the actual world list and data it displays.
     */
    private void updateList()
    {
        WorldResult worldResult = worldService.getWorlds();
        if (worldResult != null)
        {
            SwingUtilities.invokeLater(() -> panel.populate(worldResult.getWorlds()));
        }
    }

    private void hop(boolean previous)
    {
        WorldResult worldResult = worldService.getWorlds();
        if (worldResult == null || client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        World currentWorld = worldResult.findWorld(client.getWorld());

        if (currentWorld == null)
        {
            return;
        }

        EnumSet<WorldType> currentWorldTypes = currentWorld.getTypes().clone();
        // Make it so you always hop out of PVP and high risk worlds
        if (config.quickhopOutOfDanger()) {
            currentWorldTypes.remove(WorldType.PVP);
            currentWorldTypes.remove(WorldType.HIGH_RISK);
        }
        // Don't regard these worlds as a type that must be hopped between
        currentWorldTypes.remove(WorldType.BOUNTY);
        currentWorldTypes.remove(WorldType.SKILL_TOTAL);
        currentWorldTypes.remove(WorldType.LAST_MAN_STANDING);

        List<World> worlds = worldResult.getWorlds();

        int worldIdx = worlds.indexOf(currentWorld);
        int totalLevel = client.getTotalLevel();

        final Set<RegionFilterMode> regionFilter = config.quickHopRegionFilter();

        boolean customCyclePresent = (customWorlds.length() > 0);
        if (customCyclePresent) {
            String[] customWorldCycleStr = customWorlds.split(",");
            List<Integer> customWorldCycleInt = new ArrayList<>();
            for (String world : customWorldCycleStr) {
                try {
                    int parsedWorld = Integer.parseInt(world);
                    customWorldCycleInt.add(parsedWorld);
                } catch (Exception ignored) {}
            }
            int currentIdx = -1;
            for (int i = 0; i < customWorldCycleInt.size(); i++) {
                if (customWorldCycleInt.get(i) == currentWorld.getId()) {
                    currentIdx = i;
                    break;
                }
            }
            if (currentIdx != -1)
                if (previous) {
                    if (--currentIdx <= -1)
                        currentIdx = customWorldCycleInt.size() - 1;
                } else if (++currentIdx >= customWorldCycleInt.size()) {
                    currentIdx = 0;
                }
            int temp = (currentIdx == -1) ? 0 : currentIdx;

            if (config.combatHop()) {
                if (allowedToHop) {
                    hop(customWorldCycleInt.get((currentIdx == -1) ? 0 : currentIdx));
                } else {
                    hopBlocked.submit(() -> {
                        try {
                            if (hopDelayMS > 0L)
                                Thread.sleep(hopDelayMS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        hop(customWorldCycleInt.get(temp));
                    });
                }
            } else {
                hop(customWorldCycleInt.get(temp));
            }
        } else {
            World world;
            do
            {
			/*
				Get the previous or next world in the list,
				starting over at the other end of the list
				if there are no more elements in the
				current direction of iteration.
			 */
                if (previous)
                {
                    worldIdx--;

                    if (worldIdx < 0)
                    {
                        worldIdx = worlds.size() - 1;
                    }
                }
                else
                {
                    worldIdx++;

                    if (worldIdx >= worlds.size())
                    {
                        worldIdx = 0;
                    }
                }

                world = worlds.get(worldIdx);

                // Check world region if filter is enabled
                if (!regionFilter.isEmpty() && !regionFilter.contains(RegionFilterMode.of(world.getRegion())))
                {
                    continue;
                }

                EnumSet<WorldType> types = world.getTypes().clone();

                types.remove(WorldType.BOUNTY);
                // Treat LMS world like casual world
                types.remove(WorldType.LAST_MAN_STANDING);

                if (types.contains(WorldType.SKILL_TOTAL))
                {
                    try
                    {
                        int totalRequirement = Integer.parseInt(world.getActivity().substring(0, world.getActivity().indexOf(" ")));

                        if (totalLevel >= totalRequirement)
                        {
                            types.remove(WorldType.SKILL_TOTAL);
                        }
                    }
                    catch (NumberFormatException ex)
                    {
                        log.warn("Failed to parse total level requirement for target world", ex);
                    }
                }

                // Avoid switching to near-max population worlds, as it will refuse to allow the hop if the world is full
                if (world.getPlayers() >= MAX_PLAYER_COUNT)
                {
                    continue;
                }

                // Break out if we've found a good world to hop to
                if (currentWorldTypes.equals(types))
                {
                    break;
                }
            }
            while (world != currentWorld);

            if (world == currentWorld)
            {
                String chatMessage = new ChatMessageBuilder()
                        .append(ChatColorType.NORMAL)
                        .append("Couldn't find a world to quick-hop to.")
                        .build();

                chatMessageManager.queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(chatMessage)
                        .build());
            }
            else if (config.combatHop()) {
                if (allowedToHop) {
                    hop(world.getId());
                } else {
                    World finalWorld = world;
                    hopBlocked.submit(() -> {
                        try {
                            if (hopDelayMS > 0L) {
                                Thread.sleep(hopDelayMS);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        hop(finalWorld.getId());
                    });
                }
            } else {
                hop(world.getId());
            }
        }
    }

    private void hop(int worldId)
    {
        assert client.isClientThread();

        WorldResult worldResult = worldService.getWorlds();
        // Don't try to hop if the world doesn't exist
        World world = worldResult.findWorld(worldId);
        if (world == null)
        {
            return;
        }

        final net.runelite.api.World rsWorld = client.createWorld();
        rsWorld.setActivity(world.getActivity());
        rsWorld.setAddress(world.getAddress());
        rsWorld.setId(world.getId());
        rsWorld.setPlayerCount(world.getPlayers());
        rsWorld.setLocation(world.getLocation());
        rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

        if (client.getGameState() == GameState.LOGIN_SCREEN)
        {
            // on the login screen we can just change the world by ourselves
            client.changeWorld(rsWorld);
            return;
        }

        if (config.showWorldHopMessage())
        {
            String chatMessage = new ChatMessageBuilder()
                    .append(ChatColorType.NORMAL)
                    .append("Quick-hopping to World ")
                    .append(ChatColorType.HIGHLIGHT)
                    .append(Integer.toString(world.getId()))
                    .append(ChatColorType.NORMAL)
                    .append("..")
                    .build();

            chatMessageManager
                    .queue(QueuedMessage.builder()
                            .type(ChatMessageType.CONSOLE)
                            .runeLiteFormattedMessage(chatMessage)
                            .build());
        }

        quickHopTargetWorld = rsWorld;
        displaySwitcherAttempts = 0;

        hopDelay = 0L;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        currentWorld = client.getWorld();
        if (client.getTickCount() == logOutNotifTick) {
            logOutNotifTick = -1;
            if (config.playSound())
                client.playSoundEffect(80);
        }

        if (quickHopTargetWorld == null)
        {
            return;
        }

        if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null)
        {
            client.openWorldHopper();

            if (++displaySwitcherAttempts >= DISPLAY_SWITCHER_MAX_ATTEMPTS)
            {
                String chatMessage = new ChatMessageBuilder()
                        .append(ChatColorType.NORMAL)
                        .append("Failed to quick-hop after ")
                        .append(ChatColorType.HIGHLIGHT)
                        .append(Integer.toString(displaySwitcherAttempts))
                        .append(ChatColorType.NORMAL)
                        .append(" attempts.")
                        .build();

                chatMessageManager
                        .queue(QueuedMessage.builder()
                                .type(ChatMessageType.CONSOLE)
                                .runeLiteFormattedMessage(chatMessage)
                                .build());

                resetQuickHopper();
            }
        }
        else {
            client.hopToWorld(quickHopTargetWorld);
            resetQuickHopper();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.GAMEMESSAGE)
        {
            return;
        }

        if (event.getMessage().equals("Please finish what you're doing before using the World Switcher."))
        {
            resetQuickHopper();
        }
    }

    private void resetQuickHopper() {
        displaySwitcherAttempts = 0;
        quickHopTargetWorld = null;
    }

    private ChatPlayer getChatPlayerFromName(String name)
    {
        String cleanName = Text.removeTags(name);

        // Search friends chat members first, because we can always get their world;
        // friends worlds may be hidden if they have private off. (#5679)
        FriendsChatManager friendsChatManager = client.getFriendsChatManager();
        if (friendsChatManager != null)
        {
            FriendsChatMember member = friendsChatManager.findByName(cleanName);
            if (member != null)
            {
                return member;
            }
        }

        ClanChannel clanChannel = client.getClanChannel();
        if (clanChannel != null)
        {
            ClanChannelMember member = clanChannel.findMember(cleanName);
            if (member != null)
            {
                return member;
            }
        }

        clanChannel = client.getGuestClanChannel();
        if (clanChannel != null)
        {
            ClanChannelMember member = clanChannel.findMember(cleanName);
            if (member != null)
            {
                return member;
            }
        }

        NameableContainer<Friend> friendContainer = client.getFriendContainer();
        if (friendContainer != null)
        {
            return friendContainer.findByName(cleanName);
        }

        return null;
    }

    /**
     * Ping all worlds. This takes a long time and is only run on first run.
     */
    private void pingInitialWorlds()
    {
        WorldResult worldResult = worldService.getWorlds();
        if (worldResult == null || !config.showSidebar() || !config.ping())
        {
            return;
        }

        Stopwatch stopwatch = Stopwatch.createStarted();

        for (World world : worldResult.getWorlds())
        {
            int ping = ping(world);
            SwingUtilities.invokeLater(() -> panel.updatePing(world.getId(), ping));
        }

        stopwatch.stop();

        log.debug("Done pinging worlds in {}", stopwatch.elapsed());
    }

    /**
     * Ping the next world
     */
    private void pingNextWorld()
    {
        WorldResult worldResult = worldService.getWorlds();
        if (worldResult == null || !config.showSidebar() || !config.ping())
        {
            return;
        }

        List<World> worlds = worldResult.getWorlds();
        if (worlds.isEmpty())
        {
            return;
        }

        if (currentWorld >= worlds.size())
        {
            // Wrap back around
            currentWorld = 0;
        }

        World world = worlds.get(currentWorld++);

        // If we are displaying the ping overlay, there is a separate scheduled task for the current world
        boolean displayPing = config.displayPing() && client.getGameState() == GameState.LOGGED_IN;
        if (displayPing && client.getWorld() == world.getId())
        {
            return;
        }

        int ping = ping(world);
        log.trace("Ping for world {} is: {}", world.getId(), ping);
        SwingUtilities.invokeLater(() -> panel.updatePing(world.getId(), ping));
    }

    /**
     * Ping the current world for the ping overlay
     */
    private void pingCurrentWorld()
    {
        WorldResult worldResult = worldService.getWorlds();
        // There is no reason to ping the current world if not logged in, as the overlay doesn't draw
        if (worldResult == null || !config.displayPing() || client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        final World currentWorld = worldResult.findWorld(client.getWorld());
        if (currentWorld == null)
        {
            log.debug("unable to find current world: {}", client.getWorld());
            return;
        }

        currentPing = ping(currentWorld);
        log.trace("Ping for current world is: {}", currentPing);

        SwingUtilities.invokeLater(() -> panel.updatePing(currentWorld.getId(), currentPing));
    }

    Integer getStoredPing(World world)
    {
        if (!config.ping())
        {
            return null;
        }

        return storedPings.get(world.getId());
    }

    private int ping(World world)
    {
        int ping = Ping.ping(world);
        storedPings.put(world.getId(), ping);
        return ping;
    }
}