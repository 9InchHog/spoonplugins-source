package net.runelite.client.plugins.deasyscape;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Extension
@PluginDescriptor(
        name = "[D] EasyScape",
        enabledByDefault = false
)
public class DEasyScapePlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private DEasyScapeConfig config;

    private List<String> blocked_ground_items = new ArrayList<>();

    private List<String> blocked_inv_items = new ArrayList<>();

    private List<String> blocked_npcs = new ArrayList<>();

    private List<String> blocked_objects = new ArrayList<>();

    @Provides
    DEasyScapeConfig provideConfig(ConfigManager configManager) {
        return (DEasyScapeConfig)configManager.getConfig(DEasyScapeConfig.class);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged e) {
        blocked_ground_items.clear();
        blocked_inv_items.clear();
        blocked_npcs.clear();
        blocked_objects.clear();
        byte b;
        int i;
        String[] arrayOfString;
        for (i = (arrayOfString = config.removedGroundItems().split(",")).length, b = 0; b < i; ) {
            String str = arrayOfString[b];
            str = str.trim();
            if (!"".equals(str))
                blocked_ground_items.add(str.toLowerCase());
            b++;
        }
        for (i = (arrayOfString = config.removedInvItems().split(",")).length, b = 0; b < i; ) {
            String str = arrayOfString[b];
            str = str.trim();
            if (!"".equals(str))
                blocked_inv_items.add(str.toLowerCase());
            b++;
        }
        for (i = (arrayOfString = config.removedNpcs().split(",")).length, b = 0; b < i; ) {
            String str = arrayOfString[b];
            str = str.trim();
            if (!"".equals(str))
                blocked_npcs.add(str.toLowerCase());
            b++;
        }
        for (i = (arrayOfString = config.removedObjects().split(",")).length, b = 0; b < i; ) {
            String str = arrayOfString[b];
            str = str.trim();
            if (!"".equals(str))
                blocked_objects.add(str.toLowerCase());
            b++;
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded e) {
        int type = e.getType();
        int id = e.getIdentifier();
        if ((config.removeExamine() && type >= 1002 && type <= 1005) || (
                config.removeInvItems() && type >= 33 && type <= 38 && is_inv_item_blocked(id)) || (
                config.removeGroundItems() && type >= 18 && type <= 22 && is_ground_item_blocked(id)) || (
                config.removeNpcs() && type >= 7 && type <= 13 && type != 8 && is_npc_op_blocked(id, type)) || (
                config.removeObjects() && ((type >= 1 && type <= 6) || type == 1001) && is_object_blocked(id))) {
            MenuEntry[] entries = client.getMenuEntries();
            MenuEntry[] newEntries = new MenuEntry[entries.length - 1];
            System.arraycopy(entries, 0, newEntries, 0, newEntries.length);
            client.setMenuEntries(newEntries);
        }
    }

    private boolean is_ground_item_blocked(int id) {
        ItemComposition comp = client.getItemDefinition(id);
        if (comp.getName() == null)
            return false;
        return blocked_ground_items.contains(comp.getName().toLowerCase());
    }

    private boolean is_inv_item_blocked(int id) {
        ItemComposition comp = client.getItemDefinition(id);
        if (comp.getName() == null)
            return false;
        return blocked_inv_items.contains(comp.getName().toLowerCase());
    }

    private boolean is_object_blocked(int id) {
        ObjectComposition comp = client.getObjectDefinition(id);
        if (comp == null || comp.getName() == null)
            return false;
        return blocked_objects.contains(comp.getName().toLowerCase());
    }

    private boolean is_npc_op_blocked(int id, int type) {
        if (id < 0 || id >= 32768)
            return false;
        NPC npc = client.getCachedNPCs()[id];
        if (npc == null || npc.getName() == null)
            return false;
        if (type >= 9 && type <= 13) {
            int op = type - 9;
            /*if (npc.getTransformedComposition().getActions()[op].equalsIgnoreCase("attack"))
                return false;*/
        }
        return blocked_npcs.contains(npc.getName().toLowerCase());
    }
}
