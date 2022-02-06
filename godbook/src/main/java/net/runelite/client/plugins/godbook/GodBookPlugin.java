package net.runelite.client.plugins.godbook;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

@Extension
@PluginDescriptor(
        name = "Godbook",
        description = "Displays how long since someone preached.",
        tags = {"preach", "godbook"}
)
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

    @Inject
    private DelayUtils delayUtils;

    @Getter
    private ArrayList<String> names;

    @Getter
    private ArrayList<Integer> ticks;

    @Getter
    private boolean active;

    private boolean isShowing;

    private ArrayList<Integer> animationList;

    private boolean mirrorMode;

    @Provides
    GodBookConfig provideConfig(ConfigManager configManager) {
        return (GodBookConfig)configManager.getConfig(GodBookConfig.class);
    }

    protected void startUp() throws Exception {
        active = false;
        isShowing = false;
        names = new ArrayList<>();
        ticks = new ArrayList<>();
        animationList = new ArrayList<>();
        updateAnimationList();
    }

    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        isShowing = false;
    }

    @Subscribe
    protected void onConfigChanged(ConfigChanged event) {
        updateAnimationList();
    }

    private void updateAnimationList() {
        String[] parsedAnimations = config.animations().split(",");
        animationList.clear();
        for (String s : parsedAnimations) {
            try {
                if (!s.equals(""))
                    animationList.add(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                log.warn("Failed to parse: " + s);
            }
        }
    }

    private void addPlayer(String name) {
        active = true;
        ticks.add(0);
        names.add(name);
    }

    public void stop() {
        active = false;
        ticks.clear();
        names.clear();
    }

    private boolean containsAnimation(int id) {
        return animationList.contains(id);
    }

    public void increment() {
        ArrayList<Integer> toRemove = new ArrayList<>();
        int i;
        for (i = 0; i < ticks.size(); i++) {
            if (ticks.get(i) > config.maxTicks() - 2)
                toRemove.add(i);
        }
        for (i = 0; i < ticks.size(); i++) {
            if (toRemove.contains(i)) {
                ticks.remove(i);
                names.remove(i);
            }
        }
        if (ticks.size() == 0)
            stop();
        for (i = 0; i < ticks.size(); i++) {
            int temp = ticks.get(i) + 1;
            ticks.set(i, temp);
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if ((config.verzikOnly() && inRegion(12611, 12612)) || !config.verzikOnly())
            if (event.getActor().getAnimation() == 7155 || event.getActor().getAnimation() == 7154 || event.getActor().getAnimation() == 1336 || containsAnimation(event.getActor().getAnimation())) {
                if (!active) {
                    overlayManager.add(overlay);
                    isShowing = true;
                    active = true;
                }
                addPlayer(event.getActor().getName());
            }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (isShowing && ticks.size() == 0) {
            overlayManager.remove(overlay);
            isShowing = false;
        }
        if (active) {
            increment();
        }

        if (config.leftClick()) {
            Widget preach = client.getWidget(219, 1);
            if (preach != null && !preach.isHidden() && !preach.isSelfHidden()) {
                for (Widget child : preach.getDynamicChildren()) {
                    if (child.getText().contains("Select a relevant passage")) {
                        //(max - min) + min
                        int delay = delayUtils.nextInt(0, 241);
                        switch (config.keyToPress()) {
                            case ONE:
                                delayUtils.delayKey(KeyEvent.VK_1, delay);
                                break;
                            case TWO:
                                delayUtils.delayKey(KeyEvent.VK_2, delay);
                                break;
                            case THREE:
                                delayUtils.delayKey(KeyEvent.VK_3, delay);
                                break;
                            case FOUR:
                                delayUtils.delayKey(KeyEvent.VK_4, delay);
                                break;
                        }
                    }
                }
            }
        }
    }

    public boolean inRegion(int... regions) {
        if (client.getMapRegions() != null)
            for (int i : client.getMapRegions()) {
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
