package net.runelite.client.plugins.ariatob;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Extension
@PluginDescriptor(
        name = "<html><font color=#ffb347>[A] Tob",
        description = "Aria's tob additions",
        tags = {"theatre", "tob", "aria"},
        enabledByDefault = false
)
@Slf4j
public class AriaTobPlugin extends Plugin
{
    private static final int NYLO_MAP_REGION = 13122;
    private static final List<Integer> NYLO_IDS = List.of(
            8342, // Regular
            8343, 8344, 8345, 8346, 8347, 8348, 8349, 8350, 8351, 8352, 8353,
            10774, // Story Mode
            10775, 10776, 10777, 10778, 10779, 10780, 10781, 10782, 10883, 10884, 10885,
            10791, // Hard Mode
            10792, 10793, 10794, 10795, 10796, 10797, 10798, 10799, 10800, 10801, 10802, 10803,
            10804, // Demi
            10805, 10806
    );

    private final HashMap<Integer, NPC> nyloNpcs = new HashMap<>();
    private final HashMap<NPC, Integer> nyloTickSpawned = new HashMap<>();

    // Injects our config
    @Inject
    private AriaTobConfig config;

    @Inject
    private Client client;

    public AriaTobPlugin()
    {
    }

    @Provides
    AriaTobConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(AriaTobConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick gameTick)
    {

    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned npcSpawned)
    {
        final var npc = npcSpawned.getNpc();
        final var id = npc.getId();
        if (NYLO_IDS.contains(id))
        {
            nyloNpcs.put(id, npc);
            nyloTickSpawned.put(npc, client.getTickCount());
        }
    }

    private static boolean isBig(NPC nylo)
    {
        return nylo.getCombatLevel() == 260; // 4 scale is 260. does this change between scales? Wiki thinks no.
    }

    private static boolean isNylos(MenuEntry menuEntry)
    {
        return menuEntry.getTarget().contains("Nylocas");
    }

    private NPC npcFromMenuEntry(MenuEntry menuEntry)
    {
        return client.getCachedNPCs()[menuEntry.getIdentifier()];
    }

    private int nyloTicksOld(NPC nylo)
    {
        return client.getTickCount() - nyloTickSpawned.get(nylo);
    }

    @Subscribe
    private void onClientTick(ClientTick event)
    {
        if (client.isMenuOpen() || !isInNylo() || !config.nyloPrio()) return;
        final var menuEntries = client.getMenuEntries();
        final var sortedNylos = Arrays.stream(menuEntries)
                .filter(AriaTobPlugin::isNylos)
                .filter(x -> nyloTickSpawned.containsKey(npcFromMenuEntry(x)))
                .sorted(Comparator.comparingInt(x -> nyloTickSpawned.get(npcFromMenuEntry(x))))
                .sorted((a, b) -> {
                    // this is an imperative fucking MESS. i feel so dirty. fuck.
                    if (!config.nyloPrioSmalls()) return 0;
                    final var aNpc = npcFromMenuEntry(a);
                    final var bNpc = npcFromMenuEntry(b);
                    final var aLifetime = nyloTicksOld(aNpc);
                    final var bLifetime = nyloTicksOld(bNpc);
                    final var prio35 = config.nyloPrio35s();
                    if (isBig(aNpc) == isBig(bNpc)) return 0;
                    if (prio35 && isBig(aNpc) && bLifetime > 35) return aLifetime > 35 ? -1 : 1; // 1
                    if (isBig(aNpc)) return -1;
                    if (prio35 && isBig(bNpc) && aLifetime > 35) return bLifetime > 35 ? 1 : -1; //return -1
                    return 1; //b is big
                })
                .collect(Collectors.toList());


        // put big nylos lower down in right click menu

        final var leftovers = Arrays.stream(menuEntries).filter(x -> !sortedNylos.contains(x)).collect(Collectors.toUnmodifiableList());
		/*sortedNylos.forEach(x -> {
			var target = x.getTarget();
			var ticks = nyloTickSpawned.get(client.getCachedNPCs()[x.getId()]);
			x.setTarget(target + " - " + (client.getTickCount() - ticks));
		});*/
        final var finalEntries = Stream.of(leftovers, sortedNylos).flatMap(List::stream).collect(Collectors.toUnmodifiableList());
        client.setMenuEntries(finalEntries.toArray(MenuEntry[]::new));

    }

    private boolean isInNylo()
    {
        if (!client.isInInstancedRegion()) return false;
        // //TODO: Spoontob does this differently. Does that matter?
        //return client.getLocalPlayer().getWorldLocation().getRegionID() == NYLO_MAP_REGION;
        // If spectating, above code will return a different id.
        return WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == NYLO_MAP_REGION;
    }
}