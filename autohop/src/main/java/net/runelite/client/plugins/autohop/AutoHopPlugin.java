package net.runelite.client.plugins.autohop;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.concurrent.Executors;

@Extension
@PluginDescriptor(
        name = "Auto Hopper",
        description = "Automatically hops away from people. Made by Owain",
        enabledByDefault = false
)
@Slf4j
public class AutoHopPlugin extends Plugin
{
    private static final int DISPLAY_SWITCHER_MAX_ATTEMPTS = 3;
    private static final int GRAND_EXCHANGE_REGION = 12598;
    private static final WorldArea FEROX_ENCLAVE_AREA = new WorldArea(new WorldPoint(3125, 3618, 0), new WorldPoint(3153, 3639, 0));
    public Set<String> blacklistName;

    @Inject
    private Client client;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private WorldService worldService;

    @Inject
    private AutoHopConfig config;

    private net.runelite.api.World quickHopTargetWorld;
    private int displaySwitcherAttempts = 0;
    private boolean openInventory;

    @Provides
    AutoHopConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(AutoHopConfig.class);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("autohop")) {
            if (event.getKey().equals("ignoredPlayers")) {
                blacklistName = new HashSet<>();
                for (String s : net.runelite.api.util.Text.COMMA_SPLITTER.split(config.ignoredPlayers().toLowerCase())) {
                    blacklistName.add(s);
                }
            }
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event)
    {
        final Player local = client.getLocalPlayer();

        if (event.getGameState() != GameState.LOGGED_IN || local == null ||
                (config.disableGrandExchange() && local.getWorldLocation().getRegionID() == GRAND_EXCHANGE_REGION) ||
                (config.disableFeroxEnclave() && local.getWorldArea().intersectsWith(FEROX_ENCLAVE_AREA))) {
            return;
        }
        if (config.returnInventory()) {
            openInventory = true;
        }
        for (Player player : client.getPlayers()) {
            if (player == null || player.equals(local) || (config.cmbBracket() && !PvPUtil.isAttackable(client, player))
                    || (config.hopRadius() && player.getWorldLocation().distanceTo(local.getWorldLocation()) > config.playerRadius()))
                continue;
            if (config.alwaysHop()) {
                shouldHop(player);
                continue;
            }
            if (config.underHop() && local.getWorldLocation() == player.getWorldLocation()) {
                shouldHop(player);
                continue;
            }
            if (config.skulledHop() && player.getSkullIcon() != null)
                shouldHop(player);
        }
    }

    @Subscribe
    private void onPlayerSpawned(PlayerSpawned event) {
        Player local = client.getLocalPlayer();
        Player player = event.getPlayer();
        if (local == null || player == null || player.equals(local) || (config.cmbBracket() && !PvPUtil.isAttackable(client, player)))
            return;
        if (config.alwaysHop()) {
            shouldHop(player);
        } else if (config.underHop() && local.getWorldLocation() == player.getWorldLocation()) {
            shouldHop(player);
        } else if (config.skulledHop() && player.getSkullIcon() != null) {
            shouldHop(player);
        }
    }

    private void shouldHop(Player player)
    {
        if ((config.friends() && client.isFriended(player.getName(), false)) ||
                (config.clanmember() && player.isFriendsChatMember() || config.clanmember() && player.isClanMember()) ||
                (config.hopRadius() && player.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation()) > config.playerRadius()) ||
                (config.disableGrandExchange() && player.getWorldLocation().getRegionID() == GRAND_EXCHANGE_REGION) ||
                (config.disableFeroxEnclave() && player.getWorldArea().intersectsWith(FEROX_ENCLAVE_AREA)) ||
                (blacklistName.contains(player.getName().toLowerCase())))
        {
            return;
        }

        hop();
    }

    private World findWorld(List<World> worlds, EnumSet<WorldType> currentWorldTypes, int totalLevel)
    {
        World world = worlds.get(new Random().nextInt(worlds.size()));

        EnumSet<WorldType> types = world.getTypes().clone();

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

        if (currentWorldTypes.equals(types))
        {
            int worldLocation = world.getLocation();

            if (config.american() && worldLocation == 0)
            {
                return world;
            }
            else if (config.unitedkingdom() && worldLocation == 1)
            {
                return world;
            }
            else if (config.australia() && worldLocation == 3)
            {
                return world;
            }
            else if (config.germany() && worldLocation == 7)
            {
                return world;
            }
        }

        return null;
    }

    private void hop()
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

        currentWorldTypes.remove(WorldType.PVP);
        currentWorldTypes.remove(WorldType.HIGH_RISK);
        currentWorldTypes.remove(WorldType.BOUNTY);
        currentWorldTypes.remove(WorldType.SKILL_TOTAL);
        currentWorldTypes.remove(WorldType.LAST_MAN_STANDING);

        List<World> worlds = worldResult.getWorlds();

        int totalLevel = client.getTotalLevel();

        World world;
        do
        {
            world = findWorld(worlds, currentWorldTypes, totalLevel);
        }
        while (world == null || world == currentWorld);

        hop(world.getId());
    }

    private void hop(int worldId)
    {
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
            client.changeWorld(rsWorld);
            return;
        }

        String chatMessage = new ChatMessageBuilder()
                .append(ChatColorType.NORMAL)
                .append("Hopping away from a player. New world: ")
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

        quickHopTargetWorld = rsWorld;
        displaySwitcherAttempts = 0;
    }

    private void pressKey() {
        KeyEvent keyPress = new KeyEvent(client.getCanvas(), 401, System.currentTimeMillis(), 0, 32);
        client.getCanvas().dispatchEvent(keyPress);
        KeyEvent keyRelease = new KeyEvent(client.getCanvas(), 402, System.currentTimeMillis(), 0, 32);
        client.getCanvas().dispatchEvent(keyRelease);
        KeyEvent keyTyped = new KeyEvent(client.getCanvas(), 400, System.currentTimeMillis(), 0, 32);
        client.getCanvas().dispatchEvent(keyTyped);
    }

    @Subscribe
    private void onGameTick(GameTick event)
    {
        if (config.autoCloseChatbox() && (
                client.getVarbitValue(Varbits.IN_WILDERNESS) == 1 || net.runelite.api.WorldType.isPvpWorld(client.getWorldType()))) {
            Widget x = client.getWidget(162, 56);
            Widget y = client.getWidget(162, 53);
            if (y != null && !y.isHidden() && !y.isSelfHidden() && (
                    x == null || x.isHidden() || x.isSelfHidden()))
                Executors.newSingleThreadExecutor().submit(this::pressKey);
        }

        if (config.returnInventory() && openInventory)
        {
            client.runScript(915, 3); //Open inventory
            openInventory = false;
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
        else
        {
            client.hopToWorld(quickHopTargetWorld);
            resetQuickHopper();
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage event)
    {
        final Player local = client.getLocalPlayer();
        String eventName = Text.sanitize(event.getName());

        if (local == null ||
                (config.disableGrandExchange() && local.getWorldLocation().getRegionID() == GRAND_EXCHANGE_REGION) ||
                (config.disableFeroxEnclave() && local.getWorldArea().intersectsWith(FEROX_ENCLAVE_AREA)) ||
                event.getType() != ChatMessageType.GAMEMESSAGE &&
                        !(config.chatHop() && event.getType() == ChatMessageType.PUBLICCHAT && eventName != local.getName()) &&
                        local.getName() != null)
        {
            return;
        }

        if (event.getMessage().equals("Please finish what you're doing before using the World Switcher."))
        {
            resetQuickHopper();
            return;
        }

        if (config.chatHop() && event.getType() == ChatMessageType.PUBLICCHAT &&
                !eventName.equals(client.getLocalPlayer().getName()) &&
                client.getLocalPlayer().getName() != null)
        {
            log.info("Chat message found -> Hopping");
            hop();
        }
    }

    private void resetQuickHopper()
    {
        displaySwitcherAttempts = 0;
        quickHopTargetWorld = null;
    }
}
