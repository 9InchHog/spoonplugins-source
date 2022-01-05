package net.runelite.client.plugins.spoonsmithing;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Smithing",
        description = "Game hard, me click button, me hammer metal",
        tags = {"smithing", "spoon"}
        )
public class SpoonSmithingPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private SpoonSmithingConfig config;

    private ArrayList<String> itemArr = new ArrayList<>();
	
    public SpoonSmithingPlugin() {
       
    }

    @Provides
    SpoonSmithingConfig getConfig(ConfigManager configManager) {
        return (SpoonSmithingConfig) configManager.getConfig(SpoonSmithingConfig.class);
    }

    protected void startUp() throws Exception {
        if(!config.smithingItems().equals("")){
            String[] strArr = config.smithingItems().split(",");
            for(String str : strArr){
                itemArr.add(str.toLowerCase().trim());
            }
        }
    }

    protected void shutDown() throws Exception {

    }

    protected void reset() {
        itemArr.clear();
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event){
        if (event.getKey().equals("smithingItems")){
            if(config.smithingItems().equals("")){
                itemArr.clear();
            }else {
                itemArr.clear();
                String[] strArr = config.smithingItems().split(",");
                for(String str : strArr){
                    itemArr.add(str.toLowerCase().trim());
                }
            }
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if(this.client.getWidget(20447232) != null) {
            if(itemArr.size() > 0 && (event.getOption().equalsIgnoreCase("smith") || event.getOption().equalsIgnoreCase("smith set"))
                && !Text.removeTags(event.getTarget()).equalsIgnoreCase("anvil")) {
                boolean match = false;
                for (String str : itemArr) {
                    if (Text.removeTags(event.getTarget().toLowerCase()).contains(str)) {
                        match = true;
                    }
                }

                if (!match) {
                    this.client.setMenuEntries(Arrays.copyOf(this.client.getMenuEntries(), this.client.getMenuEntries().length - 1));
                }
            }
        }
    }
}

