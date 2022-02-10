package net.runelite.client.plugins.decorhighlight;

import com.google.inject.Provides;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "[D] Decor Highlight",
        description = "Highlights ground decor \"graphics objects\" by ID in-game"
)
public class DecorHighlightPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private DecorSceneOverlay overlay;

    @Inject
    private DecorHighlightConfig config;

    Set<Integer> graphicsObjectWhitelist;

    Set<Integer> groundDecorWhitelist;

    @Provides
    DecorHighlightConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DecorHighlightConfig.class);
    }

    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        graphicsObjectWhitelist = new HashSet<>();
        groundDecorWhitelist = new HashSet<>();
        parse_list(graphicsObjectWhitelist, config.graphicsObjectsToHighlight());
        parse_list(groundDecorWhitelist, config.groundDecorToHighlight());
    }

    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged e) {
        if (!"decorhighlight".equals(e.getGroup()))
            return;
        graphicsObjectWhitelist.clear();
        groundDecorWhitelist.clear();
        parse_list(graphicsObjectWhitelist, config.graphicsObjectsToHighlight());
        parse_list(groundDecorWhitelist, config.groundDecorToHighlight());
    }

    private void parse_list(Set<Integer> list, String src) {
        String[] split = src.split(",");
        for (String value : split) {
            String s = value.trim();
            try {
                int n = Integer.parseInt(s);
                list.add(n);
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
