package net.runelite.client.plugins.hoptimer;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Hop Timer",
        description = "Shows how long before you are out of combat and can hop worlds",
        enabledByDefault = false
)
public class HopTimerPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private HopTimerOverlay overlay;

    public int hopTicks = 0;
    public boolean canHop = true;
    public int canHopTicks = 0;
    public String hopSecondsDisplay = "";
    public boolean otherDmg = false;

    private void reset() {
        hopTicks = 0;
        canHop = true;
        canHopTicks = 0;
        hopSecondsDisplay = "";
    }

    protected void startUp() throws Exception {
        reset();
        this.overlayManager.add(this.overlay);
    }

    protected void shutDown() throws Exception {
        reset();
        this.overlayManager.remove(this.overlay);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event){
        if (event.getGameState() != GameState.LOADING && event.getGameState() != GameState.LOGGED_IN && event.getGameState() != GameState.CONNECTION_LOST) {
            System.out.println(event.getGameState());
            reset();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event){
        if (event.getMessage().equals("Ow! You nearly broke a tooth!") || event.getMessage().equals("The rock cake resists all attempts to eat it.") ||
                event.getMessage().equals("You bite hard into the rock cake to guzzle it down.") || event.getMessage().equals("OW! A terrible shock jars through your skull.") ||
                event.getMessage().contains("You drink some of your divine") || event.getMessage().equals("You drink some of the foul liquid.") ||
                event.getMessage().equals("The locator orb is unstable and hurts you as you use it.")){
            canHop = true;
            otherDmg = true;
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event){
        if (event.getActor().getName() != null && event.getActor().getName().equals(this.client.getLocalPlayer().getName())) {
            if (event.getActor().getAnimation() == 4409 || event.getActor().getAnimation() == 4411) {
                canHop = true;
                otherDmg = true;
            }
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        if (event.getActor().getName() != null) {
            if (event.getActor().getName().equals(this.client.getLocalPlayer().getName())) {
                if (otherDmg) {
                    otherDmg = false;
                } else {
                    canHop = false;
                    hopTicks = 16;
                    canHopTicks = 8;
                }
            }
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if(canHop) {
            canHopTicks--;
            if (canHopTicks == 0) {
                canHop = false;
            }
        } else{
            hopTicks--;
            hopSecondsDisplay = to_mmss(hopTicks);
            if (hopTicks == 0) {
                canHop = true;
                canHopTicks = 8;
            }
        }
    }

    public static String to_mmss(int ticks) {
        int m = ticks / 100;
        int s = (ticks - m * 100) * 6 / 10;
        return String.valueOf(s);
    }
}
