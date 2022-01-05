package net.runelite.client.plugins.godbook;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Extension
@PluginDescriptor(name = "Godbook", description = "Displays how long since someone preached.", tags = {"preach", "godbook"})
public class GodBookPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(GodBookPlugin.class);

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GodBookOverlay overlay;

    @Inject
    private GodBookConfig config;

    @Inject
    private Client client;

    private ArrayList<String> names;

    private ArrayList<Integer> ticks;

    private boolean active;

    private boolean isShowing;

    private ArrayList<Integer> animationList;

    public ArrayList<String> getNames() {
        return this.names;
    }

    public ArrayList<Integer> getTicks() {
        return this.ticks;
    }

    public boolean isActive() {
        return this.active;
    }

    private boolean mirrorMode;

    @Provides
    GodBookConfig provideConfig(ConfigManager configManager) {
        return (GodBookConfig)configManager.getConfig(GodBookConfig.class);
    }

    protected void startUp() throws Exception {
        this.active = false;
        this.isShowing = false;
        this.names = new ArrayList<>();
        this.ticks = new ArrayList<>();
        this.animationList = new ArrayList<>();
        updateAnimationList();
    }

    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.isShowing = false;
    }

    @Subscribe
    protected void onConfigChanged(ConfigChanged event) {
        updateAnimationList();
    }

    private void updateAnimationList() {
        List<String> parsedAnimations = Arrays.asList(this.config.animations().split(","));
        this.animationList.clear();
        for (String s : parsedAnimations) {
            try {
                if (s != "")
                    this.animationList.add(Integer.valueOf(Integer.parseInt(s)));
            } catch (NumberFormatException e) {
                log.warn("Failed to parse: " + s);
            }
        }
    }

    private void addPlayer(String name) {
        this.active = true;
        this.ticks.add(Integer.valueOf(0));
        this.names.add(name);
    }

    public void stop() {
        this.active = false;
        this.ticks.clear();
        this.names.clear();
    }

    private boolean containsAnimation(int id) {
        return this.animationList.contains(Integer.valueOf(id));
    }

    public void increment() {
        ArrayList<Integer> toRemove = new ArrayList<>();
        int i;
        for (i = 0; i < this.ticks.size(); i++) {
            if (((Integer)this.ticks.get(i)).intValue() > this.config.maxTicks() - 2)
                toRemove.add(Integer.valueOf(i));
        }
        for (i = 0; i < this.ticks.size(); i++) {
            if (toRemove.contains(Integer.valueOf(i))) {
                this.ticks.remove(i);
                this.names.remove(i);
            }
        }
        if (this.ticks.size() == 0)
            stop();
        for (i = 0; i < this.ticks.size(); i++) {
            int temp = ((Integer)this.ticks.get(i)).intValue() + 1;
            this.ticks.set(i, Integer.valueOf(temp));
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if ((this.config.verzikOnly() && inRegion(new int[] { 12611, 12612 })) || !this.config.verzikOnly())
            if (event.getActor().getAnimation() == 7155 || event.getActor().getAnimation() == 7154 || event.getActor().getAnimation() == 1336 || containsAnimation(event.getActor().getAnimation())) {
                if (!this.active) {
                    this.overlayManager.add(this.overlay);
                    this.isShowing = true;
                    this.active = true;
                }
                addPlayer(event.getActor().getName());
            }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (this.isShowing && this.ticks.size() == 0) {
            this.overlayManager.remove(this.overlay);
            this.isShowing = false;
        }
        if (this.active)
            increment();
    }

    public boolean inRegion(int... regions) {
        if (this.client.getMapRegions() != null)
            for (int i : this.client.getMapRegions()) {
                for (int j : regions) {
                    if (i == j)
                        return true;
                }
            }
        return false;
    }

    /*@Subscribe
    private void onClientTick(ClientTick event) {
        if (client.isMirrored() && !mirrorMode) {
            overlay.setLayer(OverlayLayer.AFTER_MIRROR);
            mirrorMode = true;
        }
    }*/

}
