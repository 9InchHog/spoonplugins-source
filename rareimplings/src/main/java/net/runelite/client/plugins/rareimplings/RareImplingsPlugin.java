package net.runelite.client.plugins.rareimplings;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPCComposition;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.NpcChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Extension
@PluginDescriptor(
        name = "[D] Rare Implings",
        description = "Highlight rare implings in Puro-Puro"
)
public class RareImplingsPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private RareImplingsOverlay overlay;

    private Map<Long, Integer> changes = new HashMap<>();

    @Provides
    RareImplingsConfig provideConfig(ConfigManager configManager) {
        return (RareImplingsConfig)configManager.getConfig(RareImplingsConfig.class);
    }

    protected void startUp() throws Exception {
        overlayManager.add(overlay);
    }

    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onNpcChanged(NpcChanged e) {
        long hash = ((long) e.getNpc().getIndex() << 32 | (long) (e.getOld().getId() & 0xFFFF) << 16 | e.getNpc().getId() & 0xFFFF);
        Integer count = changes.get(hash);
        int num = (count == null) ? 0 : count;
        changes.put(hash, num + 1);
    }

    @Subscribe
    public void onCommandExecuted(CommandExecuted e) {
        if ("impstats".equals(e.getCommand())) {
            client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Num. changes: " + this.changes.size(), (String)null);
            for (Map.Entry<Long, Integer> en : this.changes.entrySet()) {
                long hash = en.getKey();
                int index = (int)(hash >> 32L);
                NPCComposition comp1 = client.getNpcDefinition((int)(hash >> 16L) & 0xFFFF);
                NPCComposition comp2 = client.getNpcDefinition((int)(hash & 0xFFFFL));
                String type1 = comp1.getName().replace(" impling", "");
                String type2 = comp2.getName().replace(" impling", "");
                String mes = "<col=00ff00>[" + index + "] " + type1 + "->" + type2 + ": " + en.getValue() + "</col>";
                client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", mes, null);
            }
        }
        System.out.println("Command: " + e.getCommand());
    }
}
