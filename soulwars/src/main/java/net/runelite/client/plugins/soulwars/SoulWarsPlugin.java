package net.runelite.client.plugins.soulwars;

import com.google.common.collect.ArrayListMultimap;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Soul Wars",
        description = "Removes cast on teammates in Soul Wars",
        tags = {"soul wars", "soul", "wars", "avatar", "cast"},
        enabledByDefault = false
)
@Slf4j
public class SoulWarsPlugin extends Plugin {
    @Inject
    private SoulWarsConfig config;

    @Inject
    private BarricadeOverlay overlay;

    @Inject
    private AvatarOverlay avatarOverlay;

    @Inject
    public Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;

    private final ArrayListMultimap<String, Integer> optionIndexes = ArrayListMultimap.create();

    public ArrayList<String> playerEntry = new ArrayList<String>();
    public ArrayList<String> playerNames = new ArrayList<String>();
    public ArrayList<String> customTexts = new ArrayList<String>();

    public ArrayList<NPC> intNPC = new ArrayList<NPC>();
    public boolean namedTarget;

    public ArrayList<Integer> magicWeaponId = new ArrayList<Integer>(Arrays.asList(
            12899, 20736, 2417, 2416, 2415, 22323, 6562, 11998, 4675, 22292, 21006, 6914, 12422, 6912, 6910, 6908, 1393, 3053, 11787, 20730, 1401, 3054,
            24422, 24423, 24424, 24425, 11905, 11907, 22288));
    public ArrayList<Integer> rangeWeaponId = new ArrayList<Integer>(Arrays.asList(
            12926, 20997, 12788, 11235, 19478, 19481, 21012, 21902, 11785, 10156, 9185, 8880, 4934, 4935, 4936, 4937, 11959, 9977, 861,
            4212, 4214, 4215, 4216, 4217, 4218, 4219, 4220, 4221, 4222, 4223, 11748, 11749, 11750, 11751, 11752, 11753, 11754, 11755, 11756, 11757, 11758,
            806, 807, 808, 809, 810, 811, 11230));
    public ArrayList<Integer> meleeWeaponId = new ArrayList<Integer>(Arrays.asList(
            23360, 12006, 22324, 22325, 13576, 22978, 21003, 23987, 20370, 4587, 21009, 12809, 19675, 21219, 21015, 4886, 4910, 4982, 20727, 4153, 10887, 5698,
            3204, 11824, 11802, 18804, 11806, 11808, 20366, 20368, 20372, 20374, 21646, 21742, 1333, 20000, 11889, 22731, 22734, 22486, 11838, 13263, 4151,
            12773, 12774, 22840, 11037, 23995, 24219, 24551, 13652, 1215, 1231, 24417, 22542, 22545, 11804, -1));

    public ArrayList<Integer> soulWarsRegionIdList = new ArrayList<Integer>(Arrays.asList(8491, 8492, 8493, 8494, 8747, 8748, 8749, 8750, 9003, 9004, 9005, 9006, 8236, 8237, 8238, 9261, 9262));
    public ArrayList<Player> playersList = new ArrayList<>();

    public boolean barricadesActive = false;
    public ArrayList<NPC> barricades = new ArrayList<>();

    public boolean isInSW() {
        return soulWarsRegionIdList.contains(WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID());
    }

    public int avatarDamage = 0;

    @Provides
    SoulWarsConfig provideConfig(ConfigManager configManager) {
        return (SoulWarsConfig) configManager.getConfig(SoulWarsConfig.class);
    }

    private void reset() {
        this.barricades.clear();
        this.barricadesActive = false;
        this.avatarDamage = 0;
    }

    @Override
    protected void startUp() {
        reset();
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.avatarOverlay);
    }

    @Override
    protected void shutDown() {
        reset();
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.avatarOverlay);
    }

    private void swapMenuEntry(int index, MenuEntry menuEntry) {
        int eventId = menuEntry.getIdentifier();
        int type = menuEntry.getType().getId();
        String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
        String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();
        Player localPlayer = this.client.getLocalPlayer();

        if (config.removeCast() && localPlayer.getTeam() > 0) {
            String[] spells = {"ice barrage", "ice burst", "ice blitz", "ice rush", "entangle", "snare", "bind", "blood barrage", "blood burst", "blood rush",
                    "blood blitz"};
            MenuEntry[] entries = this.client.getMenuEntries();
            MenuEntry[] newEntries = this.client.getMenuEntries();

            playersList.clear();
            for (Player player : this.client.getPlayers()) {
                if (player.getTeam() == localPlayer.getTeam() && !player.getName().equals(localPlayer.getName())) {
                    playersList.add(player);
                }
            }

            for (int i = entries.length - 1; i >= 0; i--) {
                for (String spell : spells) {
                    if (Text.removeTags(entries[i].getTarget().toLowerCase()).startsWith(spell + " ->") && entries[i].getType().getId() != 8) {
                        for (Player pl : playersList) {
                            String name = pl.getName().replaceAll(" ", "");
                            String entry = entries[i].getTarget().replaceAll("[^A-Za-z0-9]", "");
                            if (entry.contains(name)) {
                                newEntries = (MenuEntry[]) ArrayUtils.remove((Object[]) entries, i);
                            }
                        }
                    }
                }
            }
            this.client.setMenuEntries(newEntries);
        }

        if (config.takePowerTable() != SoulWarsConfig.PowerTableMode.TAKE_FROM && option.contains("take-from") && isInSW() && target.contains("potion of power table")) {
            switch (config.takePowerTable()) {
                case TAKE_1:
                    swap("take-1", option, target, index);
                    break;
                case TAKE_5:
                    swap("take-5", option, target, index);
                    break;
                case TAKE_10:
                    swap("take-10", option, target, index);
                    break;
            }
        }

        if (config.takeBandageTable() != SoulWarsConfig.BandageTableMode.TAKE_FROM && option.contains("take-from") && isInSW() && target.contains("bandage table")) {
            switch (config.takeBandageTable()) {
                case TAKE_1:
                    swap("take-1", option, target, index);
                    break;
                case TAKE_5:
                    swap("take-5", option, target, index);
                    break;
                case TAKE_10:
                    swap("take-10", option, target, index);
                    break;
            }
        }
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick) {
        if (this.client.getGameState() != GameState.LOGGED_IN || this.client.isMenuOpen())
            return;
        MenuEntry[] menuEntries = this.client.getMenuEntries();
        int idx = 0;
        this.optionIndexes.clear();
        for (MenuEntry entry : menuEntries) {
            String option = Text.removeTags(entry.getOption()).toLowerCase();
            this.optionIndexes.put(option, Integer.valueOf(idx++));
        }
        idx = 0;
        for (MenuEntry entry : menuEntries)
            swapMenuEntry(idx++, entry);
    }

    private void swap(String optionA, String optionB, String target, int index) {
        swap(optionA, optionB, target, index, true);
    }

    private void swapContains(String optionA, String optionB, String target, int index) {
        swap(optionA, optionB, target, index, false);
    }

    private void swap(String optionA, String optionB, String target, int index, boolean strict) {
        MenuEntry[] menuEntries = this.client.getMenuEntries();
        int thisIndex = findIndex(menuEntries, index, optionB, target, strict);
        int optionIdx = findIndex(menuEntries, thisIndex, optionA, target, strict);
        if (thisIndex >= 0 && optionIdx >= 0)
            swap(this.optionIndexes, menuEntries, optionIdx, thisIndex);
    }

    private int findIndex(MenuEntry[] entries, int limit, String option, String target, boolean strict) {
        if (strict) {
            List<Integer> indexes = this.optionIndexes.get(option);
            for (int i = indexes.size() - 1; i >= 0; i--) {
                int idx = ((Integer) indexes.get(i)).intValue();
                MenuEntry entry = entries[idx];
                String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
                if (idx <= limit && entryTarget.equals(target))
                    return idx;
            }
        } else {
            for (int i = limit; i >= 0; i--) {
                MenuEntry entry = entries[i];
                String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
                String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
                if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target))
                    return i;
            }
        }
        return -1;
    }

    private void swap(ArrayListMultimap<String, Integer> optionIndexes, MenuEntry[] entries, int index1, int index2) {
        MenuEntry entry = entries[index1];
        entries[index1] = entries[index2];
        entries[index2] = entry;
        this.client.setMenuEntries(entries);
        optionIndexes.clear();
        int idx = 0;
        for (MenuEntry menuEntry : entries) {
            String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
            optionIndexes.put(option, Integer.valueOf(idx++));
        }
    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event) {
        if (event.getActor() != null && event.getActor().getName() != null && event.getNpc().getId() == 10539 || event.getNpc().getId() == 10540) {
            this.barricades.add(event.getNpc());
            this.barricadesActive = true;
        }
    }


    @Subscribe
    private void onNpcDespawned(NpcDespawned event) {
        if (event.getActor() != null && event.getActor().getName() != null && event.getNpc().getId() == 10539 || event.getNpc().getId() == 10540) {
            this.barricades.remove(event.getNpc());
            if (this.barricades.size() == 0)
                this.barricadesActive = false;
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        if(event.getActor() instanceof NPC) {
            if (event.getHitsplat().isMine() && event.getActor() != null && event.getActor().getName() != null) {
                if(event.getActor().getName().equalsIgnoreCase("Avatar of Creation") || event.getActor().getName().equalsIgnoreCase("Avatar of Destruction")) {
                    avatarDamage += event.getHitsplat().getAmount();
                }
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if(event.getGameState() == GameState.LOGGED_IN && this.client.getLocalPlayer() != null && this.client.getLocalPlayer().getTeam() == 0 && avatarDamage > 0 && isInSW()) {
            avatarDamage = 0;
        }
    }
}