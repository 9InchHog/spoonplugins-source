package net.runelite.client.plugins.discoonthego;

import com.google.common.collect.ArrayListMultimap;
import com.google.inject.Provides;

import java.awt.*;
import java.util.*;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Disco On The Go",
        description = "Just fuck me up right good",
        tags = {"spoon", "wtf", "help"},
        enabledByDefault = false
)
public class DiscoOnTheGoPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private DiscoOnTheGoConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private DiscoOnTheGoOverlay overlay;

    private final ArrayListMultimap<String, Integer> optionIndexes = ArrayListMultimap.create();

	public int pulseOpacity = 0;
	public String pulseOpacityUpOrDown = "";
	
	public ArrayList<Color> discoColors = new ArrayList<Color>();

    @Provides
    DiscoOnTheGoConfig provideConfig(ConfigManager configManager) {
        return (DiscoOnTheGoConfig) configManager.getConfig(DiscoOnTheGoConfig.class);
    }

    @Override
    protected void startUp(){
		reset();
        this.overlayManager.add(this.overlay);
        setFloor();
    }

    @Override
    protected void shutDown(){
		reset();
        this.overlayManager.remove(this.overlay);
    }

	private void reset(){
		pulseOpacity = 0;
		pulseOpacityUpOrDown = "";
		discoColors.clear();
		if(this.client.getLocalPlayer() != null) {
            this.client.getLocalPlayer().setIdlePoseAnimation(-1);
            this.client.getLocalPlayer().setPoseAnimation(-1);
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if(event.getKey().equals("groovin")){
            if(!config.groovin()){
                if(this.client.getLocalPlayer() != null) {
                    this.client.getLocalPlayer().setIdlePoseAnimation(-1);
                    this.client.getLocalPlayer().setPoseAnimation(-1);
                }
            }
        }else if(event.getKey().equals("disco")){
            if(config.disco()){
                setFloor();
            }
        }
    }

    @Subscribe
    private void onActorDeath(ActorDeath event){
        if(event.getActor().getName() != null && this.client.getLocalPlayer() != null) {
            if (event.getActor().getName().equals(client.getLocalPlayer().getName())) {
                client.playSoundEffect(3892, 20);
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        setFloor();
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        if (this.client.getGameState() != GameState.LOGGED_IN || this.client.isMenuOpen()) {
            return;
        }

        if(this.client.getLocalPlayer() != null && config.groovin()) {
            if (this.client.getLocalPlayer().getAnimation() == -1) {
                this.client.getLocalPlayer().setIdlePoseAnimation(5316);
                this.client.getLocalPlayer().setPoseAnimation(5316);
            } else if (this.client.getLocalPlayer().getIdlePoseAnimation() == 5316) {
                this.client.getLocalPlayer().setIdlePoseAnimation(-1);
                this.client.getLocalPlayer().setPoseAnimation(-1);
            }
        }

		if(config.playerHelper()){
			if(pulseOpacity <= 0){
				pulseOpacityUpOrDown = "up";
			}else if(pulseOpacity >= 255){
				pulseOpacityUpOrDown = "down";
			} 
			
			if(pulseOpacityUpOrDown.equals("up")){
				pulseOpacity += 4;
				if(pulseOpacity > 255){
				    pulseOpacity = 255;
                }
			}else if(pulseOpacityUpOrDown.equals("down")){
				pulseOpacity -= 4;
                if(pulseOpacity < 0){
                    pulseOpacity = 0;
                }
			}
		}
    }

    public void setFloor(){
        discoColors.clear();
        for(int i=0; i<((config.discoSize()*2)+1) * ((config.discoSize()*2)+1); i++){
            discoColors.add(Color.getHSBColor(new Random().nextFloat(), 0.9F, 1.0F));
        }
    }
}
