package net.runelite.client.plugins.vengcounter;


import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Extension
@PluginDescriptor(
        name = "Veng Counter",
        description = "Counts how many times each player venges",
        tags = {"veng", "counter"},
        enabledByDefault = false
)

public class VengCounterPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private VengCounterConfig config;
    @Inject
    private VengCounterOverlay overlay;
    @Inject
    private Client client;

    @Provides
    VengCounterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(VengCounterConfig.class);
    }

    Map<String, Integer> activity = new HashMap<>();

    boolean instanced = false;
    boolean prevInstance = false;

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        activity.clear();
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged e)
    {
        if (!(e.getActor() instanceof Player))
            return;
        Player p = (Player) e.getActor();
        if (p.getAnimation() == 8316)
        {
            String name = p.getName();
            activity.put(name, activity.getOrDefault(name, 0) + 1);
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        if (!config.instance())return;
        prevInstance = instanced;
        instanced = client.isInInstancedRegion();
        if (!prevInstance && instanced) activity.clear();

    }

    @Subscribe
    public void onOverlayMenuClicked(OverlayMenuClicked event)
    {
        if (event.getEntry().getMenuAction() == MenuAction.RUNELITE_OVERLAY &&
                event.getEntry().getTarget().equals("Veng counter") &&
                event.getEntry().getOption().equals("Reset"))
        {
            activity.clear();
        }
    }

}
